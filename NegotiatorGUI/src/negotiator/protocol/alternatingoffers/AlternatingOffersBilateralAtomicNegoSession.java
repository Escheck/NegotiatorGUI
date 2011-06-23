package negotiator.protocol.alternatingoffers;

import java.util.Date;
import java.util.HashMap;

import negotiator.Agent;
import negotiator.NegotiationOutcome;
import negotiator.Timeline;
import negotiator.actions.Accept;
import negotiator.actions.EndNegotiation;
import negotiator.actions.Offer;
import negotiator.analysis.BidPoint;
import negotiator.exceptions.Warning;
import negotiator.protocol.BilateralAtomicNegotiationSession;
import negotiator.protocol.Protocol;
import negotiator.tournament.VariablesAndValues.AgentParamValue;
import negotiator.tournament.VariablesAndValues.AgentParameterVariable;
import negotiator.utility.UtilitySpace;
/**
 * This is an updated version which has shared deadlines for both agents, implemented with {@link Timeline}.
 */
public class AlternatingOffersBilateralAtomicNegoSession extends BilateralAtomicNegotiationSession {

	//AlternatingOffersNegotiationSession session;
	/**
	 * stopNegotiation indicates that the session has now ended.
	 * it is checked after every call to the agent,
	 * and if it happens to be true, session is immediately returned without any updates to the results list.
	 * This is because killing the thread in many cases will return Agent.getAction() but with
	 * a stale action. By setting stopNegotiation to true before killing, the agent will still immediately return.
	 */
	public boolean stopNegotiation=false;



	public NegotiationOutcome no;

	private boolean agentAtookAction = false;
	private boolean agentBtookAction = false;
	protected String startingAgent;
	protected boolean startingWithA=true;    
	/* time/deadline */
	protected Date startTime; 
	protected long startTimeMillies; //idem.
	/** In ms. */
	protected Integer totalTime = 1000 * AlternatingOffersProtocol.non_gui_nego_time;
	Integer totTime; // total time, seconds, of this negotiation session.
	protected int sessionTotalNumber = 1;
	protected Protocol protocol;

	public Agent currentAgent=null; // agent currently bidding.



	/** load the runtime objects to start negotiation */
	public AlternatingOffersBilateralAtomicNegoSession(Protocol protocol,
			Agent agentA,
			Agent agentB, 
			String agentAname, 
			String agentBname,
			UtilitySpace spaceA, 
			UtilitySpace spaceB, 
			HashMap<AgentParameterVariable,AgentParamValue> agentAparams,
			HashMap<AgentParameterVariable,AgentParamValue> agentBparams,
			String startingAgent,
			int totalTime) throws Exception {

		super(protocol, agentA, agentB, agentAname, agentBname, spaceA, spaceB, agentAparams, agentBparams);
		this.protocol = protocol;
		this.startingAgent = startingAgent;
		this.totTime = totalTime;
	}

	/**
	 * a parent thread will call this via the Thread.run() function.
	 * Then it will start a timer to handle the time-out of the negotiation.
	 * At the end of this run, we will notify the parent so that he does not keep waiting for the time-out.
	 */
	public void run() {
		startTime=new Date(); startTimeMillies=System.currentTimeMillis();
		try {
			double agentAUtility,agentBUtility;

			Timeline timeline = new Timeline((int) (totalTime));
			// note, we clone the utility spaces for security reasons, so that the agent
			// can not damage them.
			agentA.internalInit(sessionNumber, sessionTotalNumber,startTime,totalTime,timeline,
					new UtilitySpace(spaceA),agentAparams);
			agentA.init();
			agentB.internalInit(sessionNumber, sessionTotalNumber,startTime,totalTime,timeline,
					new UtilitySpace(spaceB),agentBparams);
			agentB.init();
			stopNegotiation = false;
			lastAction = null;

			if (startingAgent.equals(agentAname)) currentAgent=agentA;
			else currentAgent=agentB;

			System.out.println("starting with agent "+currentAgent);
			//Main.log("Agent " + currentAgent.getName() + " begins");
			fireLogMessage("Nego","Agent " + currentAgent.getName() + " begins");
			while(!stopNegotiation) {
				//            	timeline.printTime();
				try {
					//inform agent about last action of his opponent
					currentAgent.ReceiveMessage(lastAction);
					String deadlineReachedMsg = "Deadline reached while waiting for [" + currentAgent + "]";
					if(timeline.isDeadlineReached()) 
					{
						System.out.println(deadlineReachedMsg);
						badOutcome(timeline, deadlineReachedMsg);                	   
					}
					if (stopNegotiation) return;
					//get next action of the agent that has its turn now
					lastAction = currentAgent.chooseAction();
					if(timeline.isDeadlineReached()) 
					{
						System.out.println(deadlineReachedMsg);
						badOutcome(timeline, deadlineReachedMsg);              	   
					}

					if (stopNegotiation) return;

					if(lastAction instanceof EndNegotiation) 
					{
						badOutcome(timeline, "Agent [" + currentAgent.getName() + "] sent EndNegotiation, so the negotiation ended without agreement");
						checkAgentActivity(currentAgent);
					}
					else if (lastAction instanceof Offer) {
						//Main.log("Agent " + currentAgent.getName() + " sent the following offer:");
						fireLogMessage("Nego","Agent " + currentAgent.getName() + " sent the following offer:");
						lastBid  = ((Offer)lastAction).getBid();
						if (lastBid == null)
						{
							badOutcome(timeline, "Agent [" + currentAgent.getName() + "] sent an offer with null in it, so the negotiation ended without agreement");
							return;
						}
						//Main.log(lastAction.toString());
						fireLogMessage("Nego",lastAction.toString());
						double utilA=agentA.utilitySpace.getUtility(lastBid);
						double utilB=agentB.utilitySpace.getUtility(lastBid);
						//Main.log("Utility of " + agentA.getName() +": " + utilA);
						fireLogMessage("Nego","Utility of " + agentA.getName() +": " + utilA);
						//Main.log("Utility of " + agentB.getName() +": " + utilB);
						fireLogMessage("Nego","Utility of " + agentB.getName() +": " + utilB);
						//save last results 
						BidPoint p=null;
						p=new BidPoint(lastBid,
								spaceA.getUtility(lastBid),
								spaceB.getUtility(lastBid));
						if(currentAgent.equals(agentA))                    {
							fAgentABids.add(p);
						} else{
							fAgentBBids.add(p);
						}

						double time = timeline.getTime();
						double agentAUtilityDisc = spaceA.getUtilityWithDiscount(lastBid, time);
						double agentBUtilityDisc = spaceB.getUtilityWithDiscount(lastBid, time);

						fireNegotiationActionEvent(currentAgent,lastAction,sessionNumber,
								System.currentTimeMillis()-startTimeMillies, time, utilA,utilB,agentAUtilityDisc,agentBUtilityDisc,"bid by "+currentAgent.getName());

						checkAgentActivity(currentAgent) ;
					}                   
					else if (lastAction instanceof Accept) {
						stopNegotiation = true;
						Accept accept = (Accept)lastAction;
						if(lastBid==null)
							throw new Exception("Accept was done by "+
									currentAgent.getName()+" but no bid was done yet.");
						//Global.log("Agents accepted the following bid:");
						//Global.log(((Accept)lastAction).toString());
						double time = timeline.getTime();
						double agentAUtilityDisc = spaceA.getUtilityWithDiscount(lastBid, time);
						double agentBUtilityDisc = spaceB.getUtilityWithDiscount(lastBid, time);

						agentAUtility = spaceA.getUtility(lastBid);
						agentBUtility = spaceB.getUtility(lastBid);
						
						BidPoint lastbidPoint = new BidPoint(lastBid, agentAUtility, agentBUtility);
						BidPoint nash = bidSpace.getNash();
						double distanceToNash = lastbidPoint.distanceTo(nash);
//						System.out.println("Distance to Nash: " + distanceToNash);
						newOutcome(currentAgent, agentAUtility,agentBUtility,agentAUtilityDisc,agentBUtilityDisc, null, time, distanceToNash);
						checkAgentActivity(currentAgent) ;
						otherAgent(currentAgent).ReceiveMessage(lastAction);                      
					} else {  // lastAction instanceof unknown action, e.g. null.
						throw new Exception("unknown action by agent "+currentAgent.getName());
					}


				} catch(Exception e) {
					new Warning("Caught exception:",e,true,2);
					stopNegotiation=true;
					new Warning("Protocol error by Agent"+currentAgent.getName(),e,true,3);
					//Global.log("Protocol error by Agent " + currentAgent.getName() +":"+e.getMessage());
					if (lastBid==null) agentAUtility=agentBUtility=1.;
					else {
						agentAUtility=agentBUtility=0.;
						// handle both getUtility calls apart, if one crashes
						// the other should not be affected.
						try {
							agentAUtility = spaceA.getUtility(lastBid);
						}  catch (Exception e1) {}
						try {
							agentBUtility = spaceB.getUtility(lastBid);
						}  catch (Exception e1) {}
					}
					if (currentAgent==agentA) agentAUtility=0.; else agentBUtility=0.;
					try 
					{
						BidPoint lastbidPoint = new BidPoint(lastBid, agentAUtility, agentBUtility);
						BidPoint nash = bidSpace.getNash();
						double distanceToNash = lastbidPoint.distanceTo(nash);
						newOutcome(currentAgent, agentAUtility,agentBUtility,0,0, "Caught exception. Agent [" + currentAgent.getName() + "] sent " + lastAction + ". Details: "+e.toString(), timeline.getTime(), distanceToNash);
						System.err.println("Emergency outcome: " + agentAUtility + ", " + agentBUtility);
					}
					catch (Exception err) { err.printStackTrace(); new Warning("exception raised during exception handling: "+err); }
					// don't compute the max utility, we're in exception which is already bad enough.
				}
				// swap to other agent
				if(currentAgent.equals(agentA))     currentAgent = agentB; 
				else   currentAgent = agentA;
			}

			// nego finished by Accept or illegal lastAction.
			// notify parent that we're ready.
			synchronized (protocol) {  protocol.notify();  }

			/*
			 * Wouter: WE CAN NOT DO MORE PROCESSING HERE!!!!!
			 * Maybe even catching the ThreadDeath error is wrong. 
			 * If we do more processing, we risk getting a ThreadDeath exception
			 * causing Eclipse to pop up a dialog bringing us into the debugger.
			 */            

		} catch (Error e) {
			if(e instanceof ThreadDeath) {
				System.out.println("Nego was timed out");
				// Main.logger.add("Negotiation was timed out. Both parties get util=0");
				// if this happens, the caller will adjust utilities.
			}     

		}

	}

	protected void badOutcome(Timeline timeline, String logMsg) throws Exception
	{
		stopNegotiation=true;
		Double utilA = spaceA.getReservationValue();
		Double utilB = spaceB.getReservationValue();
		if (utilA == null) utilA = 0.0;
		if (utilB == null) utilB = 0.0;
		BidPoint lastbidPoint = new BidPoint(lastBid, utilA, utilB);
		BidPoint nash = bidSpace.getNash();
		double distanceToNash = lastbidPoint.distanceTo(nash);
		newOutcome(currentAgent,utilA,utilB, utilA,utilB, logMsg, timeline.getTime(), distanceToNash);
	}



	/** This is the running method of the negotiation thread.
	 * It contains the work flow of the negotiation. 
	 */
	protected void checkAgentActivity(Agent agent) {
		if(agent.equals(agentA)) agentAtookAction = true;
		else agentBtookAction = true;

	}


	public Agent otherAgent(Agent ag)
	{
		if (ag==agentA) return agentB;
		return agentA;    	
	}

	/**
	 * Make a new outcome and update table
	 * @param distanceToNash 
	 */
	public void newOutcome(Agent currentAgent, double utilA, double utilB, double utilADiscount, double utilBDiscount, String message, double time, double distanceToNash) throws Exception {

		no=new NegotiationOutcome(this, sessionNumber, lastAction,
				agentA.getName(),  agentB.getName(),
				agentA.getClass().getCanonicalName(), agentB.getClass().getCanonicalName(),
				utilA,utilB,
				utilADiscount,utilBDiscount,
				message,
				fAgentABids,fAgentBBids,
				1.0,
				1.0,
				// This is super slow
				//            spaceA.getUtility(spaceA.getMaxUtilityBid()),
				//            spaceB.getUtility(spaceB.getMaxUtilityBid()),
				startingWithA, 
				spaceA.getDomain().getName(),
				spaceA.getFileName(),
				spaceB.getFileName(),
				additionalLog,
				time,
				distanceToNash
		);
		
		fireNegotiationActionEvent(currentAgent,lastAction,sessionNumber,
				System.currentTimeMillis()-startTimeMillies,time,utilA,utilB,utilADiscount,utilBDiscount,message);
	}

	/**
	 * This is called whenever the protocol is timed-out. 
	 * What happens in case of a time-out is 
	 * (1) the sessionrunner is killed with a Thread.interrupt() call  from the NegotiationSession2.
	 * (2) judgeTimeout() is called.
	 * @author W.Pasman
	 */
	public void JudgeTimeout() 
	{
		System.out.println("Judging time-out.");
		try {
			double reservationValueA = 0;
			if(spaceA.getReservationValue()!=null) reservationValueA = spaceA.getReservationValue();
			double reservationValueB = 0;
			if(spaceB.getReservationValue()!=null) reservationValueB = spaceB.getReservationValue(); 

			BidPoint lastbidPoint = new BidPoint(lastBid, reservationValueA, reservationValueB);
			BidPoint nash = bidSpace.getNash();
			double distanceToNash = lastbidPoint.distanceTo(nash);
			
			newOutcome(currentAgent, reservationValueA, reservationValueB,reservationValueA,reservationValueB, "JudgeTimeout: negotiation was timed out", 1, distanceToNash);
		} catch (Exception err) { new Warning("error during creation of new outcome:",err,true,2); }
		// don't bother about max utility, both have zero anyway.

	}
	public NegotiationOutcome getNegotiationOutcome() {
		return no;
	}
	public String getStartingAgent() {
		return startingAgent;
	}
	public void setStartingWithA(boolean val) { 
		startingWithA = val;
	}
	public void setTotalTime(int val) {
		totalTime = val;
	}
	public void setSessionTotalNumber(int val) {
		sessionTotalNumber = val;
	}
}

