package negotiator.protocol.alternatingoffers;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import negotiator.Agent;
import negotiator.Bid;
import negotiator.NegotiationEventListener;
import negotiator.NegotiationOutcome;
import negotiator.actions.*;
import negotiator.analysis.BidPoint;
import negotiator.analysis.BidSpace;
import negotiator.exceptions.Warning;
import negotiator.protocol.*;
import negotiator.tournament.VariablesAndValues.AgentParamValue;
import negotiator.tournament.VariablesAndValues.AgentParameterVariable;
import negotiator.utility.UtilitySpace;
import negotiator.xml.SimpleElement;

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
    private long totalTimePerAgent = 3 * 60 * 1000;
	boolean startingWithA=true;    
    /* time/deadline */
    Date startTime; 
    long startTimeMillies; //idem.
	private Integer totalTime = 180000;
    Integer totTime; // total time, seconds, of this negotiation session.
    private int sessionTotalNumber = 1;
    private Protocol protocol;
    
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
		long totalTimeAgentA =0, totalTimeAgentB = 0;
        try {
            double agentAUtility,agentBUtility;

            // note, we clone the utility spaces for security reasons, so that the agent
        	 // can not damage them.
            agentA.internalInit(sessionNumber, sessionTotalNumber,startTime,totalTime,
            		new UtilitySpace(spaceA),agentAparams);
            agentA.init();
            agentB.internalInit(sessionNumber, sessionTotalNumber,startTime,totalTime,
            		new UtilitySpace(spaceB),agentBparams);
            agentB.init();
            stopNegotiation = false;
            Action action = null;
            
            if (startingAgent.equals(agentAname)) currentAgent=agentA;
           	else currentAgent=agentB;
            
        	System.out.println("starting with agent "+currentAgent);
            //Main.log("Agent " + currentAgent.getName() + " begins");
        	fireLogMessage("Nego","Agent " + currentAgent.getName() + " begins");
            while(!stopNegotiation) {
                try {
                   //inform agent about last action of his opponent
                   long currentTime = System.currentTimeMillis();
                   currentAgent.ReceiveMessage(action);
                   long timeSpent = System.currentTimeMillis() - currentTime; 
                   if(currentAgent == agentA) {
                	   totalTimeAgentA += timeSpent; 
                   } else {
                	   totalTimeAgentB += timeSpent;
                   }
                   if(totalTimeAgentA>totalTimePerAgent||totalTimeAgentB>totalTimePerAgent) {
                       stopNegotiation=true;
                       double utilA=spaceA.getUtility(spaceA.getMaxUtilityBid()); // normalized utility
                       double utilB=spaceB.getUtility(spaceB.getMaxUtilityBid());
                       newOutcome(currentAgent,0.,0., 0.,0., action, "Agent "+currentAgent.getName()+" ended the negotiation without agreement");                	   
                   }
                   if (stopNegotiation) return;
                   //get next action of the agent that has its turn now
                   currentTime = System.currentTimeMillis();                   
                   action = currentAgent.chooseAction();
                   timeSpent = System.currentTimeMillis() - currentTime;
                   if(currentAgent == agentA) {
                	   totalTimeAgentA += timeSpent; 
                   } else {
                	   totalTimeAgentB += timeSpent;
                   }
                   if(totalTimeAgentA>totalTimePerAgent||totalTimeAgentB>totalTimePerAgent) {
                       stopNegotiation=true;
                       double utilA=spaceA.getUtility(spaceA.getMaxUtilityBid()); // normalized utility
                       double utilB=spaceB.getUtility(spaceB.getMaxUtilityBid());
                       newOutcome(currentAgent,0.,0., 0.,0.,action, "Agent "+currentAgent.getName()+" ended the negotiation without agreement");                	   
                   }
                   
                   if (stopNegotiation) return;
                   
                   if(action instanceof EndNegotiation) 
                   {
                       stopNegotiation=true;
                       double utilA=spaceA.getUtility(spaceA.getMaxUtilityBid()); // normalized utility
                       double utilB=spaceB.getUtility(spaceB.getMaxUtilityBid());
                       newOutcome(currentAgent,0.,0., 0.,0., action, "Agent "+currentAgent.getName()+" ended the negotiation without agreement");
                       checkAgentActivity(currentAgent) ;
                   }
                   else if (action instanceof Offer) {
                       //Main.log("Agent " + currentAgent.getName() + " sent the following offer:");
                       fireLogMessage("Nego","Agent " + currentAgent.getName() + " sent the following offer:");
                       lastBid  = ((Offer)action).getBid();                       
                       //Main.log(action.toString());
                       fireLogMessage("Nego",action.toString());
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
                       long timeAfterStart = System.currentTimeMillis() - startTimeMillies; 
                       double agentAUtilityDisc = spaceA.getUtilityWithDiscount(lastBid, timeAfterStart, totalTime * 1000);
                       double agentBUtilityDisc = spaceB.getUtilityWithDiscount(lastBid, timeAfterStart, totalTime * 1000);
                       
	                   fireNegotiationActionEvent(currentAgent,action,sessionNumber,
	                   		System.currentTimeMillis()-startTimeMillies,utilA,utilB,agentAUtilityDisc,agentBUtilityDisc,"bid by "+currentAgent.getName());
	                	
                       checkAgentActivity(currentAgent) ;
                   }                   
                   else if (action instanceof Accept) {
                       stopNegotiation = true;
                       Accept accept = (Accept)action;
                       if(lastBid==null)
                    	   throw new Exception("Accept was done by "+
                    			   currentAgent.getName()+" but no bid was done yet.");
                        //Global.log("Agents accepted the following bid:");
                        //Global.log(((Accept)action).toString());
                        long timeAfterStart = System.currentTimeMillis() -  startTimeMillies; 
                        double agentAUtilityDisc = spaceA.getUtilityWithDiscount(lastBid, timeAfterStart, totalTime * 1000);
                        double agentBUtilityDisc = spaceB.getUtilityWithDiscount(lastBid, timeAfterStart, totalTime * 1000);
                        agentAUtility = spaceA.getUtility(lastBid);
                        agentBUtility = spaceB.getUtility(lastBid);
                        newOutcome(currentAgent, agentAUtility,agentBUtility,agentAUtilityDisc,agentBUtilityDisc,action, null);
                        checkAgentActivity(currentAgent) ;
                        otherAgent(currentAgent).ReceiveMessage(action);                      
                   } else {  // action instanceof unknown action, e.g. null.
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
                   try {
                	   newOutcome(currentAgent, agentAUtility,agentBUtility,0,0,action, "Agent " + currentAgent.getName() +":"+e.getMessage());
                   }
                   catch (Exception err) { err.printStackTrace(); new Warning("exception raised during exception handling: "+err); }
                   // don't compute the max utility, we're in exception which is already bad enough.
                }
                // swap to other agent
                if(currentAgent.equals(agentA))     currentAgent = agentB; 
                else   currentAgent = agentA;
            }
            
            // nego finished by Accept or illegal action.
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

	
    
    /** This is the running method of the negotiation thread.
     * It contains the work flow of the negotiation. 
     */
    void checkAgentActivity(Agent agent) {
        if(agent.equals(agentA)) agentAtookAction = true;
        else agentBtookAction = true;
        
    }

  
    public Agent otherAgent(Agent ag)
    {
    	if (ag==agentA) return agentB;
    	return agentA;    	
    }
  
    
    public void newOutcome(Agent currentAgent, double utilA, double utilB, double utilADiscount, double utilBDiscount, Action action, String message) throws Exception {
        
    	no=new NegotiationOutcome(sessionNumber, 
			   agentA.getName(),  agentB.getName(),
            agentA.getClass().getCanonicalName(), agentB.getClass().getCanonicalName(),
            utilA,utilB,
            utilADiscount,utilBDiscount,
            message,
            fAgentABids,fAgentBBids,
            spaceA.getUtility(spaceA.getMaxUtilityBid()),
            spaceB.getUtility(spaceB.getMaxUtilityBid()),
            startingWithA, 
            spaceA.getFileName(),
            spaceB.getFileName(),
            additionalLog
            );
    	
    	fireNegotiationActionEvent(currentAgent,action,sessionNumber,
        	System.currentTimeMillis()-startTimeMillies,utilA,utilB,utilADiscount,utilBDiscount,message);
    		
    	
    }
    
    /**
     * This is called whenever the protocol is timed-out. 
     * What happens in case of a time-out is 
     * (1) the sessionrunner is killed with a Thread.interrupt() call  from the NegotiationSession2.
     * (2) judgeTimeout() is called.
     * @author W.Pasman
     */
    public void JudgeTimeout() {
		try {
    		newOutcome(currentAgent, 0, 0,0,0, new IllegalAction(currentAgent.getAgentID(),"negotiation was timed out"),"negotiation was timed out");
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

