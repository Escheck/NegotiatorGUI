/*
 * Negotiation.java
 *
 * Created on November 6, 2006, 10:06 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package negotiator;

import java.util.ArrayList;

import negotiator.actions.*;
import java.util.Date;
import negotiator.utility.UtilitySpace;
import negotiator.analysis.BidPoint;

/**
 *
 * @author Dmytro Tykhonov
 */
public class Negotiation implements Runnable {
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
    
    
    public ArrayList<BidPoint> fAgentABids;
    public ArrayList<BidPoint> fAgentBBids;
    
    /** 
     * 
     * Creates a new instance of Negotiation 
     **/
    public Negotiation(Agent agentA, Agent agentB, NegotiationTemplate nt, 
    		int sessionNumber, int sessionTotalNumber, boolean agentAStartsP) {
        this.agentA = agentA;
        this.agentB = agentB;
        this.sessionNumber=sessionNumber;
        this.sessionTotalNumber = sessionTotalNumber;
        this.nt = nt;
        this.fAgentABids = new ArrayList<BidPoint>();
        this.fAgentBBids = new ArrayList<BidPoint>();
        agentAStarts=agentAStartsP;
    }
    
    public Agent otherAgent(Agent ag)
    {
    	if (ag==agentA) return agentB;
    	return agentA;    	
    }
    /**
     * 
     * his method returns random element of the agents array.
     */
    public Agent getRandomAgent(Agent[] agents) {
        int numberOfAgents = agents.length;
        double d = java.lang.Math.random()*numberOfAgents;
        int agentIndex = Double.valueOf(d).intValue();
        if (agentIndex >= numberOfAgents) agentIndex= numberOfAgents-1;
        return agents[agentIndex];
    }
    /** This is the running method of the negotiation thread.
     * It contains the work flow of the negotiation. 
     */
    void checkAgentActivity(Agent agent) {
        if(agent.equals(agentA)) agentAtookAction = true;
        else agentBtookAction = true;
        
    }
    
    public void run() {
        Agent currentAgent;
        Date startTime=new Date();
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
            stopNegotiation = false;
            Action action = null;

            if (agentAStarts) currentAgent=agentA;
            else {            
            	Agent agents[] = {agentA, agentB};
            	currentAgent = getRandomAgent(agents) ; 
            }
            Main.logger.add("Agent " + currentAgent.getName() + " begins");
            while(!stopNegotiation) {
                try {
                   //inform agent about last action of his opponent
                   currentAgent.ReceiveMessage(action);
                   //get next action of the agent that has its turn now
                   action = currentAgent.chooseAction();
                   if(action instanceof EndNegotiation) 
                   {
                       stopNegotiation=true;
                	   no = new NegotiationOutcome(sessionNumber, 
                               agentA.getClass().getCanonicalName(),
                               agentB.getClass().getCanonicalName(),
                               String.valueOf(0),
                               String.valueOf(0),
                               "Agent "+currentAgent.getName()+" ended the negotiation without agreement",
                               fAgentABids,fAgentBBids,
                               spaceA.getUtility(spaceA.getMaxUtilityBid()),
                               spaceB.getUtility(spaceB.getMaxUtilityBid()),
                               agentAStarts,
                               nt.getAgentAUtilitySpaceFileName(),
                               nt.getAgentBUtilitySpaceFileName()
                               );   
                       checkAgentActivity(currentAgent) ;
                   }
                   else if (action instanceof Offer) {
                       Main.logger.add("Agent " + currentAgent.getName() + " sent the following offer:");                       
                       lastBid  = ((Offer)action).getBid();
                       Main.logger.add(action.toString());
                       Main.logger.add("Utility of " + agentA.getName() +": " + agentA.utilitySpace.getUtility(lastBid));
                       Main.logger.add("Utility of " + agentB.getName() +": " + agentB.utilitySpace.getUtility(lastBid));
                       checkAgentActivity(currentAgent) ;
                   }                   
                   else if (action instanceof Accept) {
                       stopNegotiation = true;
                       Accept accept = (Accept)action;
                       if(lastBid==null)
                    	   throw new Exception("Accept was done by "+
                    			   currentAgent.getName()+" but no bid was done yet.");
                        Main.logger.add("Agents accepted the following bid:");
                        Main.logger.add(((Accept)action).toString());
                        agentAUtility = nt.getAgentAUtilitySpace().getUtility(lastBid);
                        agentBUtility = nt.getAgentBUtilitySpace().getUtility(lastBid);
                        no = new NegotiationOutcome(sessionNumber, 
                                   agentA.getClass().getCanonicalName(),
                                   agentB.getClass().getCanonicalName(),
                                   String.valueOf(agentAUtility),
                                   String.valueOf(agentBUtility),null,fAgentABids,fAgentBBids,
                                   spaceA.getUtility(spaceA.getMaxUtilityBid()),
                                   spaceB.getUtility(spaceB.getMaxUtilityBid()),
                                   agentAStarts,
                                   nt.getAgentAUtilitySpaceFileName(),
                                   nt.getAgentBUtilitySpaceFileName());
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
                   if(currentAgent.equals(agentA))
                   {
                	   if(action instanceof Offer) fAgentABids.add(p);
                	   currentAgent = agentB; 
                   }
                   else{
                	   if(action instanceof Offer) fAgentBBids.add(p);
                	   currentAgent = agentA;
                   }

                 
                } catch(Exception e) {
                   stopNegotiation=true;
             	   Main.logger.add("Protocol error by Agent " + currentAgent.getName() +":"+e.getMessage());
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
                   no = new NegotiationOutcome(sessionNumber, 
                      agentA.getClass().getCanonicalName(),
                      agentB.getClass().getCanonicalName(),
                      String.valueOf(agentAUtility),
                      String.valueOf(agentBUtility),
                      "Agent " + currentAgent.getName() +":"+e.getMessage(),fAgentABids,fAgentBBids,
                      1.,1.,
                      agentAStarts,
                      nt.getAgentAUtilitySpaceFileName(),
                      nt.getAgentBUtilitySpaceFileName());
                   // don't compute the max utility, we're in exception which is already bad enough.
                }
            }
            
            // nego finished by Accept or illegal action.
            synchronized (Main.nm) {  Main.nm.notify();  }
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
	
    
}
