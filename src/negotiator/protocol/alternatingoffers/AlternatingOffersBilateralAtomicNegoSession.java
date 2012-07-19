package negotiator.protocol.alternatingoffers;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import negotiator.Agent;
import negotiator.Bid;
import negotiator.ContinuousTimeline;
import negotiator.DiscreteTimeline;
import negotiator.Global;
import negotiator.NegotiationOutcome;
import negotiator.PausableContinuousTimeline;
import negotiator.Timeline;
import negotiator.actions.Accept;
import negotiator.actions.Action;
import negotiator.actions.EndNegotiation;
import negotiator.actions.Offer;
import negotiator.analysis.BidPoint;
import negotiator.analysis.BidSpace;
import negotiator.analysis.BidSpaceCash;
import negotiator.boaframework.OpponentModel;
import negotiator.boaframework.OutcomeTuple;
import negotiator.boaframework.agent.BOAagent;
import negotiator.boaframework.opponentmodel.NoModel;
import negotiator.exceptions.Warning;
import negotiator.protocol.BilateralAtomicNegotiationSession;
import negotiator.protocol.Protocol;
import negotiator.qualitymeasures.OpponentModelMeasures;
import negotiator.tournament.VariablesAndValues.AgentParamValue;
import negotiator.tournament.VariablesAndValues.AgentParameterVariable;
import negotiator.utility.UtilitySpace;
/**
 * This is an updated version which has shared deadlines for both agents, implemented with {@link Timeline}.
 * 
 * KNOWN BUGS:
 * 1. In some cases, when a bad outcome is created, there is a nullpointer. Likely to occur when a
 *    computationally heavy agent plays on Energy.
 * 
 * 2. MAC does not always store the outcomes for each separate acceptance condition. This
 *    occurs in less than 1% of the matches, and can be easily detected by checking if an agent
 *    with Multi_AC in its name exists.
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
	private Timeline timeline;
	
	public ArrayList<NegotiationOutcome> MACoutcomes = new ArrayList<NegotiationOutcome>();
	private boolean agentAWithMultiAC = false;
	private boolean agentBWithMultiAC = false;
	private ArrayList<ArrayList<OutcomeTuple>> completeList = new ArrayList<ArrayList<OutcomeTuple>>();
	private OpponentModelMeasures omMeasures;

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
		
		startTime=new Date();
		startTimeMillies=System.currentTimeMillis();
		
		try {
			double agentAUtility,agentBUtility;

			if (Global.DISCRETE_TIMELINE){
				timeline = new DiscreteTimeline(Global.TOTAL_ROUNDS);			
			} else {
				if (Global.PAUSABLE_TIMELINE) {
					timeline = new PausableContinuousTimeline(totalTime);
				} else {
					timeline = new ContinuousTimeline((int) (totalTime));
				}
			}

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
			omMeasures = new OpponentModelMeasures(spaceA, spaceB);
			checkForMAC();
			
			while(!stopNegotiation) {
				//            	timeline.printTime();
				try {
					//inform agent about last action of his opponent
					currentAgent.ReceiveMessage(lastAction);
					String deadlineReachedMsg = "Deadline reached while waiting for [" + currentAgent + "]";

					if(timeline.isDeadlineReached()) {
						//if there is a MAC being used
						if(hasMAC()){
							createMACOutcomes(timeline);
						} else {
 							badOutcome(timeline, deadlineReachedMsg);   
						}
					}
					if (stopNegotiation) return;
					//get next action of the agent that has its turn now
					lastAction = currentAgent.chooseAction();
					if(timeline.isDeadlineReached()) {
						if(hasMAC()){
							createMACOutcomes(timeline);
						} else {
							badOutcome(timeline, deadlineReachedMsg);   
						}
					}

					if (stopNegotiation) return;

					if(lastAction instanceof EndNegotiation) {
						System.out.println("EndNegotiation was called");
						stopNegotiation= true;
						if(hasMAC()){						
							createMACOutcomes(timeline);
						} else {
							badOutcome(timeline, "Agent [" + currentAgent.getName() + "] sent EndNegotiation, so the negotiation ended without agreement");
						}
						checkAgentActivity(currentAgent);
					} else if (lastAction instanceof Offer) {
						//Main.log("Agent " + currentAgent.getName() + " sent the following offer:");
						fireLogMessage("Nego","Agent " + currentAgent.getName() + " sent the following offer:");
						lastBid  = ((Offer)lastAction).getBid();
						if (lastBid == null) {
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
						
						if (!timeline.isDeadlineReached()) {
							// if agent A just made an offer
							if(currentAgent.equals(agentA)) {
								fAgentABids.add(p);
								processOnlineData();
							} else{
								fAgentBBids.add(p);
							}
						}
						
						double time = timeline.getTime();
						double agentAUtilityDisc = spaceA.getUtilityWithDiscount(lastBid, time);
						double agentBUtilityDisc = spaceB.getUtilityWithDiscount(lastBid, time);

						fireNegotiationActionEvent(currentAgent,lastAction,sessionNumber,
								System.currentTimeMillis()-startTimeMillies, time, utilA,utilB,agentAUtilityDisc,agentBUtilityDisc,"bid by "+currentAgent.getName(), false);

						checkAgentActivity(currentAgent) ;
					}                   
					else if (lastAction instanceof Accept) {
						if(hasMAC()){
							createMACOutcomes(timeline);
						} else {
							createOutcome(lastBid, timeline, false, null);
						}
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

						//checkForMAC();
						if(!hasMAC()){	
							newOutcome(currentAgent, agentAUtility,agentBUtility,0,0, "Caught exception. Agent [" + currentAgent.getName() + "] sent " + lastAction + ". Details: "+e.toString(), timeline.getTime(), distanceToNash);
							}else {
								System.out.println("Error thrown: with MAC agent");
								for(ArrayList<OutcomeTuple> outcomeTupleList : completeList )
								for(OutcomeTuple outcomeTuple : outcomeTupleList){
									newOutcome(currentAgent, agentAUtility,agentBUtility,0,0, "Caught exception. Agent [" + currentAgent.getName() + "] sent " + lastAction + ". Details: "+e.toString(), timeline.getTime(), distanceToNash);
									changeNameofAC(agentAWithMultiAC, agentBWithMultiAC, outcomeTuple);
									System.out.println("OutcomeTuple: " + outcomeTuple.toString());
									MACoutcomes.add(no);
								}
							}
						
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

	private void processOnlineData() throws Exception {
		if (Global.RECORD_OPPONENT_TRACE) {
			if (Global.PAUSABLE_TIMELINE) {
				timeline.pause();
			}
			if (fAgentBBids.size() > 0) {
				Bid lastOpponentBid = fAgentBBids.get(fAgentBBids.size() - 1).bid;
				if (lastOpponentBid != null) {
					omMeasuresResults.addTimePoint(timeline.getTime());
					omMeasuresResults.addBidIndex(omMeasures.getOpponentBidIndex(lastOpponentBid));
				}
			}
			if (Global.PAUSABLE_TIMELINE) {
				timeline.resume();
			}
		}
	}

	/**
	 * Checks if one of the agents MAC has
	 */
	private boolean checkForMAC() {
		if(hasMAC()){
			return true;
		} else {			
			if(agentA instanceof BOAagent && ((BOAagent) agentA).getAcceptanceStrategy().isMAC()) {
				agentAWithMultiAC = true;
				//System.out.println("agentAWithMultiAC: " + agentAWithMultiAC);
			}
			if(agentB instanceof BOAagent && ((BOAagent) agentB).getAcceptanceStrategy().isMAC()) {
				agentBWithMultiAC = true;
				//System.out.println("agentBWithMultiAC: " + agentBWithMultiAC);
			}
		}
		
		return hasMAC();
	}
	
	/**returns true if one of the agents are using a MAC**/
	private boolean hasMAC(){
		if(agentAWithMultiAC || agentBWithMultiAC){
			return true;
		}
		
		return false;	
	}
	
	/**gets the MAC outcomeTuples from the agent class that has a MAC**/
	private void getOutcomeTuples(){
		if(agentAWithMultiAC){
			ArrayList<OutcomeTuple> listFromAgentA = ((BOAagent) agentA).getSavedOutcomes();
			completeList.add(listFromAgentA);
		}
		
		if(agentBWithMultiAC){
			ArrayList<OutcomeTuple> listFromAgentB = ((BOAagent) agentB).getSavedOutcomes();
			completeList.add(listFromAgentB);
		}
	}
	
	
	
	/**Creates a bad Outcome, which is an outcome with an error**/
	protected void badOutcome(Timeline timeline, String logMsg) throws Exception
	{
		double time = timeline.getTime();
		stopNegotiation=true;
		double rvADiscounted = spaceA.getReservationValueWithDiscount(time);
		double rvBDiscounted = spaceB.getReservationValueWithDiscount(time);
		double rvA = spaceA.getReservationValueUndiscounted();
		double rvB = spaceB.getReservationValueUndiscounted();
		
		BidPoint lastbidPoint = new BidPoint(lastBid, rvA, rvB);
		BidPoint nash = getBidSpace().getNash();
		double distanceToNash = lastbidPoint.distanceTo(nash);
		newOutcome(currentAgent, rvA, rvB, rvADiscounted, rvBDiscounted, logMsg, time, distanceToNash);
	}

	/**Creates the different outcomes for an agent that is using a MAC
	 * Calls createOutcome which actually creates the outcome to be logged.
	 ***/
	protected void createMACOutcomes(Timeline timeline) throws Exception
	{
		System.out.println("createBadMACOutcomes");
		getOutcomeTuples();
		for(ArrayList<OutcomeTuple> agentList: completeList){
			for(OutcomeTuple outcomeTuple : agentList){
				
				String logMsg="";
				if(outcomeTuple.getLogMsgType() == "deadline"){
					logMsg = "Deadline reached while waiting for [" + currentAgent + "]";
				} else if(outcomeTuple.getLogMsgType() == "breakoff"){
					logMsg = "Agent [" + currentAgent.getName() + "] sent EndNegotiation, so the negotiation ended without agreement";
				} else if(outcomeTuple.getLogMsgType()=="accept"){
					createOutcome(outcomeTuple.getLastBid(), timeline, true, outcomeTuple);
					continue;
				}
					
				badOutcome(timeline, logMsg);
				changeNameofAC(agentAWithMultiAC, agentBWithMultiAC, outcomeTuple);
				MACoutcomes.add(no);
			}
		}
	}


	/** This is the running method of the negotiation thread.
	 * It contains the work flow of the negotiation. 
	 */
	protected void checkAgentActivity(Agent agent) {
		if(agent.equals(agentA))
			agentAtookAction = true;
		else
			agentBtookAction = true;
		
		if(timeline instanceof DiscreteTimeline)
			((DiscreteTimeline) timeline).increment();

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
	private void newOutcome(Agent currentAgent, double utilA, double utilB, double utilADiscount, double utilBDiscount, String message, double time, double distanceToNash) throws Exception 
	{
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
		calculateFinalAccuracy(no);
		boolean agreement = (lastAction instanceof Accept);
		processDataForLogging(time, agreement);
		fireNegotiationActionEvent(currentAgent,lastAction,sessionNumber,
				System.currentTimeMillis()-startTimeMillies,time,utilA,utilB,utilADiscount,utilBDiscount,message, true);
		if (Global.LOW_MEMORY_MODE) {
			BidSpaceCash.removeBidSpace(spaceA, spaceB);
		}
	}
	
	
	/**Creates a newOutcome for the MAC where the amount of Bids is different than in the final stage of the negotiation**/
	private void newOutcome(Agent currentAgent, Action lastAction, double utilA, double utilB, double utilADiscount, double utilBDiscount, String message, ArrayList<BidPoint> agentASize, ArrayList<BidPoint> agentBSize, double time, double distanceToNash) throws Exception 
	{
		no=new NegotiationOutcome(this, sessionNumber, lastAction,
				agentA.getName(),  agentB.getName(),
				agentA.getClass().getCanonicalName(), agentB.getClass().getCanonicalName(),
				utilA,utilB,
				utilADiscount,utilBDiscount,
				message,
				(ArrayList<BidPoint>)agentASize, (ArrayList<BidPoint>)agentBSize,
				1.0,
				1.0,
				startingWithA, 
				spaceA.getDomain().getName(),
				spaceA.getFileName(),
				spaceB.getFileName(),
				additionalLog,
				time,
				distanceToNash
		);
		calculateFinalAccuracy(no);
		boolean agreement = (lastAction instanceof Accept);
		processDataForLogging(time, agreement);
		fireNegotiationActionEvent(currentAgent,lastAction,sessionNumber,
				System.currentTimeMillis()-startTimeMillies,time,utilA,utilB,utilADiscount,utilBDiscount,message, true);
		if (Global.LOW_MEMORY_MODE) {
			BidSpaceCash.removeBidSpace(spaceA, spaceB);
		}
	}

	private void calculateFinalAccuracy(NegotiationOutcome negoOutcome) {
		if (Global.CALCULATE_FINAL_ACCURACY) {
			if (agentA instanceof BOAagent) {
				OpponentModel opponentModel = ((BOAagent) agentA).getOpponentModel();
				if (!(opponentModel instanceof NoModel)) {
					UtilitySpace estimatedOpponentUS = opponentModel.getOpponentUtilitySpace();
					BidSpace estimatedBS = null;
					try {
						estimatedBS = new BidSpace(spaceA, estimatedOpponentUS, false);
					} catch (Exception e) {
						e.printStackTrace();
					}
					omMeasuresResults.addPearsonCorrelationCoefficientOfBids(omMeasures.calculatePearsonCorrelationCoefficientBids(estimatedOpponentUS));
					omMeasuresResults.addRankingDistanceOfBids(omMeasures.calculateRankingDistanceBids(estimatedOpponentUS));
					omMeasuresResults.addRankingDistanceOfIssueWeights(omMeasures.calculateRankingDistanceWeights(opponentModel));
					omMeasuresResults.addAverageDifferenceBetweenBids(omMeasures.calculateAvgDiffBetweenBids(opponentModel));
					omMeasuresResults.addAverageDifferenceBetweenIssueWeights(omMeasures.calculateAvgDiffBetweenIssueWeights(opponentModel));
					omMeasuresResults.addKalaiDistance(omMeasures.calculateKalaiDiff(estimatedBS));
					omMeasuresResults.addNashDistance(omMeasures.calculateNashDiff(estimatedBS));
					omMeasuresResults.addAverageDifferenceOfParetoFrontier(omMeasures.calculateAvgDiffParetoBidToEstimate(estimatedOpponentUS));
					omMeasuresResults.addPercentageOfCorrectlyEstimatedParetoBids(omMeasures.calculatePercCorrectlyEstimatedParetoBids(estimatedBS));
					omMeasuresResults.addPercentageOfIncorrectlyEstimatedParetoBids(omMeasures.calculatePercIncorrectlyEstimatedParetoBids(estimatedBS));
					omMeasuresResults.addParetoFrontierDistance(omMeasures.calculateParetoFrontierDistance(estimatedBS));
					estimatedBS = null;
					estimatedOpponentUS = null;
					System.gc();
				}
				negoOutcome.setNegotiationOutcome(omMeasuresResults);
			}
			
		}
	}
	private void processDataForLogging(double time, boolean agreement) {
		if (Global.RECORD_OPPONENT_TRACE) {
			matchDataLogger.addMeasure("time", omMeasuresResults.getTimePointList());
			matchDataLogger.addMeasure("bidindices", omMeasuresResults.getBidIndices());
			matchDataLogger.writeToFileCompact(time, agreement, protocol.getRun());
		}
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
			//checkForMAC();
			double time = 1;
			double rvADiscounted = spaceA.getReservationValueWithDiscount(time);
			double rvBDiscounted = spaceB.getReservationValueWithDiscount(time);
			double rvA = spaceA.getReservationValueUndiscounted();
			double rvB = spaceB.getReservationValueUndiscounted();

			BidPoint lastbidPoint = new BidPoint(lastBid, rvA, rvB);
			BidPoint nash = getBidSpace().getNash();
			double distanceToNash = lastbidPoint.distanceTo(nash);
			if(!agentBWithMultiAC){	
				newOutcome(currentAgent, rvA, rvB, rvADiscounted, rvBDiscounted, "JudgeTimeout: negotiation was timed out", time, distanceToNash);
				}else {
			
				System.out.println("Judge Timeout: with MAC agent");
				
				ArrayList<OutcomeTuple> list = ((BOAagent) agentB).getSavedOutcomes();
				for(OutcomeTuple outcomeTuple: list){
					newOutcome(currentAgent, rvA, rvB, rvADiscounted, rvBDiscounted,  "JudgeTimeout: negotiation was timed out", outcomeTuple.getTime(), distanceToNash);
					
					changeNameofAC(agentAWithMultiAC, agentBWithMultiAC, outcomeTuple);
					//System.out.println("OutcomeTuple: " + outcomeTuple.toString());
					MACoutcomes.add(no);
				}
			}
			
			
		} catch (Exception err) { new Warning("error during creation of new outcome:",err,true,2); }
		// don't bother about max utility, both have zero anyway.

	}
	
	/**Creates an actual outcome object that can be logged**/
	public void createOutcome(Bid lastBid, Timeline time, boolean isMac, OutcomeTuple outcomeTuple) throws Exception{
		
		stopNegotiation = true;
		//Accept accept = (Accept)lastAction;
		double agentAUtility;
		double agentBUtility;
		double agentAUtilityDisc;
		double agentBUtilityDisc;
		double distanceToNash;
		
		agentAUtilityDisc = spaceA.getUtilityWithDiscount(lastBid, time.getTime());
		agentBUtilityDisc = spaceB.getUtilityWithDiscount(lastBid, time.getTime());

		agentAUtility = spaceA.getUtility(lastBid);
		agentBUtility = spaceB.getUtility(lastBid);
		
		BidPoint lastbidPoint = new BidPoint(lastBid, agentAUtility, agentBUtility);
		BidPoint nash = getBidSpace().getNash();
		distanceToNash = lastbidPoint.distanceTo(nash);	
		
		if(isMac){
			ArrayList<BidPoint> subAgentABids;
			ArrayList<BidPoint>  subAgentBBids;
			
			if(fAgentABids.size()==outcomeTuple.getAgentASize() || fAgentBBids.size()==outcomeTuple.getAgentBSize()){
				subAgentABids = fAgentABids;
				subAgentBBids = fAgentBBids;
			}else {
				subAgentABids =  new ArrayList<BidPoint>(fAgentABids.subList(0, outcomeTuple.getAgentASize() + 1));
				subAgentBBids = new ArrayList<BidPoint>(fAgentBBids.subList(0, outcomeTuple.getAgentBSize() + 1));
			}
			newOutcome(currentAgent, new Accept(agentB.getAgentID()), agentAUtility,agentBUtility,agentAUtilityDisc,agentBUtilityDisc, null,subAgentABids, subAgentBBids, outcomeTuple.getTime(), distanceToNash);
			changeNameofAC(agentAWithMultiAC, agentBWithMultiAC, outcomeTuple);
			MACoutcomes.add(no);

		} else{
			newOutcome(currentAgent, agentAUtility,agentBUtility,agentAUtilityDisc,agentBUtilityDisc, null, time.getTime(), distanceToNash);
		}		
	}
	
	/**This changes the name of the AC from "Multi Acceptance Criteria" to the correct AC**/
	private void changeNameofAC(boolean agentAWithMultiAC, boolean agentBWithMultiAC, OutcomeTuple tuple) {
		if(agentAWithMultiAC){
			String newName = no.agentAname.replaceAll("Multi Acceptance Criteria", tuple.getName());
			no.agentAname = newName;
		}
		if(agentBWithMultiAC) {
			String newName = no.agentBname.replaceAll("Multi Acceptance Criteria", tuple.getName());
			no.agentBname = newName;
		}
		
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