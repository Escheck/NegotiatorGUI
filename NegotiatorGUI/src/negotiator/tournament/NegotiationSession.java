package negotiator.tournament;

import negotiator.ActionEvent;
import negotiator.Agent;

import java.util.ArrayList;

import negotiator.actions.*;

import java.util.Date;
import negotiator.utility.UtilitySpace;
import negotiator.analysis.BidPoint;
import negotiator.xml.*;
import java.util.Random;
import negotiator.*;
import negotiator.exceptions.Warning;


/*
 * NegotiationSession is 
 * (1) in control of guiding two agents through a negotiation
 * (2) responsible for routing progress messages to the logger
 *
 * Created on November 6, 2006, 10:06 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */



/**
 *
 * NegotiationSession is the run-time object / thread doing a session.
 * As such it contains also the protocol.
 * It also contains details regarding that run.
 * Wouter: I think these three (runtime code; protocol; nego detail info) should be separated..
 *
 * @author Dmytro Tykhonov
 * modified and partial cleanup 14aug08 W.Pasman
 * 
 * Wouter: TODO for better cleanup, separate PROTOCOL from the handling,
 * so that others (eg logger) can independently analyse the events and determine
 * what happened and proper utilities. 
 * As a workaround, the utilities were now included into the ActionEvents.
 * TODO datastructure contains many parts that belong to (1) Tournament (2) logging (3) time keeping.
 * I think these are not really part of a negotiationsession 
 */
public class NegotiationSession implements Runnable {
    protected Agent         agentA;
    protected Agent         agentB;
    private Bid lastBid=null;				// the last bid that has been done
    public boolean stopNegotiation;
    private NegotiationTemplate nt;
    private int sessionNumber;
    private int sessionTotalNumber;
    public NegotiationOutcome no;
    boolean agentAtookAction = false;
    boolean agentBtookAction = false;
    boolean agentAStarts=false;
    public SimpleElement additionalLog = new SimpleElement("additional_log");
    ActionEventListener actionEventListener=null;
    boolean startingWithA=true;
    Date startTime; // set when run() is called.
    long startTimeMillies; //idem.
    NegotiationManager nm;

    public ArrayList<BidPoint> fAgentABids;
    public ArrayList<BidPoint> fAgentBBids;
    
    /** 
     * 
     * Creates a new instance of Negotiation 
     * @param agentAStartsP = true to force start with agent A. with false, start agent is chosen randomly.
     * @param actionEventListener is the callback point for bidding events. null means you won't be given call backs.
     **/
    public NegotiationSession(Agent agentA, Agent agentB, NegotiationTemplate nt, 
    		int sessionNumber, int sessionTotalNumber, boolean agentAStartsP,
    		ActionEventListener ael, NegotiationManager themanager
    	) {
        this.agentA = agentA;
        this.agentB = agentB;
        this.sessionNumber=sessionNumber;
        this.sessionTotalNumber = sessionTotalNumber;
        this.nt = nt;
        this.fAgentABids = new ArrayList<BidPoint>();
        this.fAgentBBids = new ArrayList<BidPoint>();
        agentAStarts=agentAStartsP;
        actionEventListener=ael;
        nm=themanager;
    }
    
    public Agent otherAgent(Agent ag)
    {
    	if (ag==agentA) return agentB;
    	return agentA;    	
    }
    /**
     * 
     * his method returns random element of the agents array.
    public Agent getRandomAgent(Agent[] agents) {
        int numberOfAgents = agents.length;
        double d = java.lang.Math.random()*numberOfAgents;
        int agentIndex = Double.valueOf(d).intValue();
        if (agentIndex >= numberOfAgents) agentIndex= numberOfAgents-1;
        return agents[agentIndex];
    }
     */

    
    /** This is the running method of the negotiation thread.
     * It contains the work flow of the negotiation. 
     */
    void checkAgentActivity(Agent agent) {
        if(agent.equals(agentA)) agentAtookAction = true;
        else agentBtookAction = true;
        
    }

    public void run() {
        Agent currentAgent;
        startTime=new Date(); startTimeMillies=System.currentTimeMillis();
        try {
            double agentAUtility,agentBUtility;
            UtilitySpace spaceA=nt.getAgentAUtilitySpace();
            UtilitySpace spaceB=nt.getAgentBUtilitySpace();

            // note, we clone the utility spaces for security reasons, so that the agent
        	 // can not damage them.
            agentA.init(sessionNumber, sessionTotalNumber,startTime,nt.getTotalTime(),
            		new UtilitySpace(nt.getAgentAUtilitySpace()));
            agentB.init(sessionNumber, sessionTotalNumber,startTime,nt.getTotalTime(),
            		new UtilitySpace(nt.getAgentBUtilitySpace()));
            //allow agent to access the environment if this is a run in the experimental setup
            if(Main.experimentalSetup) {
            	agentA.setNegotiationEnviroment(this);            	
            	agentB.setNegotiationEnviroment(this);
            } else {
            	agentA.setNegotiationEnviroment(null);            	
            	agentB.setNegotiationEnviroment(null);            	
            }
            stopNegotiation = false;
            Action action = null;
            
            currentAgent=agentA;
            if (!agentAStarts && new Random().nextInt(2)==1) 
            {
            	currentAgent=agentB;
            	startingWithA=false;
            }
        	System.out.println("starting with agent "+currentAgent);
            Main.log("Agent " + currentAgent.getName() + " begins");
            while(!stopNegotiation) {
                try {
                   //inform agent about last action of his opponent
                   currentAgent.ReceiveMessage(action);
                   //get next action of the agent that has its turn now
                   action = currentAgent.chooseAction();
                   if(action instanceof EndNegotiation) 
                   {
                       stopNegotiation=true;
                       double utilA=spaceA.getUtility(spaceA.getMaxUtilityBid()); // normalized utility
                       double utilB=spaceB.getUtility(spaceB.getMaxUtilityBid());
                       newOutcome(currentAgent,0.,0., action, "Agent "+currentAgent.getName()+" ended the negotiation without agreement");
                       checkAgentActivity(currentAgent) ;
                   }
                   else if (action instanceof Offer) {
                       Main.log("Agent " + currentAgent.getName() + " sent the following offer:");                       
                       lastBid  = ((Offer)action).getBid();
                       Main.log(action.toString());
                       Main.log("Utility of " + agentA.getName() +": " + agentA.utilitySpace.getUtility(lastBid));
                       Main.log("Utility of " + agentB.getName() +": " + agentB.utilitySpace.getUtility(lastBid));
                       checkAgentActivity(currentAgent) ;
                   }                   
                   else if (action instanceof Accept) {
                       stopNegotiation = true;
                       Accept accept = (Accept)action;
                       if(lastBid==null)
                    	   throw new Exception("Accept was done by "+
                    			   currentAgent.getName()+" but no bid was done yet.");
                        Main.log("Agents accepted the following bid:");
                        Main.log(((Accept)action).toString());
                        agentAUtility = nt.getAgentAUtilitySpace().getUtility(lastBid);
                        agentBUtility = nt.getAgentBUtilitySpace().getUtility(lastBid);
                        newOutcome(currentAgent, agentAUtility,agentBUtility,action, null);
                        checkAgentActivity(currentAgent) ;
                        otherAgent(currentAgent).ReceiveMessage(action);                      
                   } else {  // action instanceof unknown action, e.g. null.
                	   throw new Exception("unknown action by agent "+currentAgent.getName());
                   }
                       
                 
                   //save last results and swap to other agent
                   BidPoint p=null;
                   if (action instanceof Offer)
                   {
            		   Bid b=((Offer)action).getBid();
            		   p=new BidPoint(b,
            				   nt.getAgentAUtilitySpace().getUtility(b),
            				   nt.getAgentBUtilitySpace().getUtility(b));
                   }
                   if(currentAgent.equals(agentA))                    {
                	   if(action instanceof Offer) fAgentABids.add(p);
                   } else{
                	   if(action instanceof Offer) fAgentBBids.add(p);
                   }

                 
                } catch(Exception e) {
                   stopNegotiation=true;
             	   Main.log("Protocol error by Agent " + currentAgent.getName() +":"+e.getMessage());
             	   e.printStackTrace();
                   if (lastBid==null) agentAUtility=agentBUtility=1.;
                   else {
                	   agentAUtility=agentBUtility=0.;
                	   // handle both getUtility calls apart, if one crashes
                	   // the other should not be affected.
                	   try {
                		   agentAUtility = nt.getAgentAUtilitySpace().getUtility(lastBid);
                	   }  catch (Exception e1) {}
                	   try {
                    	   agentBUtility = nt.getAgentBUtilitySpace().getUtility(lastBid);
                	   }  catch (Exception e1) {}
                   }
                   if (currentAgent==agentA) agentAUtility=0.; else agentBUtility=0.;
                   try {
                	   newOutcome(currentAgent, agentAUtility,agentBUtility,action, "Agent " + currentAgent.getName() +":"+e.getMessage());
                   }
                   catch (Exception err) { new Warning("exception raised during exception handling: "+err); }
                   // don't compute the max utility, we're in exception which is already bad enough.
                }
                
                if(currentAgent.equals(agentA))     currentAgent = agentB; 
                else   currentAgent = agentA;
            }
            
            // nego finished by Accept or illegal action.
            //Wouter: reverse engineered: notify main class that we're ready.
            synchronized (nm) {  nm.notify();  }
           
            /*
             * Wouter: WE CAN NOT DO MORE PROCESSING HERE!!!!!
             * Maybe even catching the ThreadDeath error is wrong. 
             * If we do more processing, we risk getting a ThreadDeath exception
             * causing Eclipse to pop up a dialog bringing us into the debugger.
             * 
            //Wouter: old code to plot a graph. Currently disabled. 
            // Probably will not work either, remember that the Negotiator is killed as soon
            // as this run function exits.
             */

    	        // Wouter: logger is causing crashes. Removed.......
    	        //synchronized(Main.logger) { Main.logger.add("Session is finished"); }
//            } catch (Exception e) {  System.out.println("Exception in negotiation (interrupt?):"+e.getMessage());e.printStackTrace();}
            
            
        } catch (Error e) {
            if(e instanceof ThreadDeath) {
            	System.out.println("Nego was timed out");
                // Main.logger.add("Negotiation was timed out. Both parties get util=0");
           }     
             
        }

    }

	public ArrayList<BidPoint> getAgentABids() {
		return fAgentABids;
	}
	
	public ArrayList<BidPoint> getAgentBBids() {
		return fAgentBBids;
	}
	
    public double getOpponentUtility(Agent pAgent, Bid pBid) throws Exception{
    	if(pAgent.equals(agentA)) 
    		return agentB.utilitySpace.getUtility(pBid);
    	else
    		return agentA.utilitySpace.getUtility(pBid);
    }
    public double getOpponentWeight(Agent pAgent, int pIssueID) throws Exception{
    	if(pAgent.equals(agentA)) 
    		return agentB.utilitySpace.getWeight(pIssueID);
    	else
    		return agentA.utilitySpace.getWeight(pIssueID);
    }
    
    public void addAdditionalLog(SimpleElement pElem) {
    	if(pElem!=null)
    		additionalLog.addChildElement(pElem);
    	
    }
    
    
    public void newOutcome(Agent currentAgent, double utilA, double utilB, Action action, String message) throws Exception {
        UtilitySpace spaceA=nt.getAgentAUtilitySpace();
        UtilitySpace spaceB=nt.getAgentBUtilitySpace();

        
    	no=new NegotiationOutcome(sessionNumber, 
			   agentA.getName(),  agentB.getName(),
            agentA.getClass().getCanonicalName(), agentB.getClass().getCanonicalName(),
            utilA,utilB,
            message,
            fAgentABids,fAgentBBids,
            spaceA.getUtility(spaceA.getMaxUtilityBid()),
            spaceB.getUtility(spaceB.getMaxUtilityBid()),
            startingWithA, 
            nt.getAgentAUtilitySpaceFileName(),
            nt.getAgentBUtilitySpaceFileName(),
            additionalLog
            );
    	
    	if (actionEventListener!=null) {
        	actionEventListener.handleEvent(new ActionEvent(currentAgent,action,sessionNumber,
        			System.currentTimeMillis()-startTimeMillies,this,utilA,utilB,message));
    		
    	}
    }
}
