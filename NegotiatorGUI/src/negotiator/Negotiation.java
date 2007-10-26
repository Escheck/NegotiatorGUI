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
import negotiator.agents.Agent;

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
    /** 
     * 
     * Creates a new instance of Negotiation 
     **/
    public Negotiation(Agent agentA, Agent agentB, NegotiationTemplate nt, 
    		int sessionNumber, int sessionTotalNumber) {
        this.agentA = agentA;
        this.agentB = agentB;
        this.sessionNumber=sessionNumber;
        this.sessionTotalNumber = sessionTotalNumber;
        this.nt = nt;
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
        ArrayList<Bid> lAgentABids = new ArrayList<Bid>();
        ArrayList<Bid> lAgentBBids = new ArrayList<Bid>();
        Date startTime=new Date();
        try {
        	
            agentA.init(sessionNumber, sessionTotalNumber,startTime,nt.getTotalTime(),nt.getAgentAUtilitySpace());
            //agentA.loadUtilitySpace(nt.getAgentAUtilitySpaceFileName());
            agentB.init(sessionNumber, sessionTotalNumber,startTime,nt.getTotalTime(),nt.getAgentBUtilitySpace());
            //agentB.loadUtilitySpace(nt.getAgentBUtilitySpaceFileName());
            stopNegotiation = false;
            Action action = null;
            double agentAUtility,agentBUtility;
            Agent agents[] = {agentA, agentB};
            currentAgent = getRandomAgent(agents) ;
            Main.logger.add("Agent " + currentAgent.getName() + " begins");
            while(!stopNegotiation) {
                try {
                   //inform agent about last action of his opponent
                   currentAgent.ReceiveMessage(action);
                   //get next action of the agent that has its turn now
                   action = currentAgent.chooseAction();
                   if(action==null) {
                       Main.logger.add("Agent " + currentAgent.getName() + " returned null: Protocol error");
                       if (lastBid==null) agentAUtility=agentBUtility=1.;
                       else {
                    	   agentAUtility = nt.getAgentAUtilitySpace().getUtility(lastBid);
                    	   agentBUtility = nt.getAgentBUtilitySpace().getUtility(lastBid);
                       }
                       if (currentAgent==agentA) agentAUtility=0.; else agentBUtility=0.;
                       no = new NegotiationOutcome(sessionNumber, 
                                  agentA.getClass().getCanonicalName(),
                                  agentB.getClass().getCanonicalName(),
                                  String.valueOf(agentAUtility),
                                  String.valueOf(agentBUtility),"Null action by "+currentAgent.getName());
                       stopNegotiation=true;
                   } else
                   
                   if (action instanceof Offer) {
                       Main.logger.add("Agent " + currentAgent.getName() + " sent the following offer:");                       
                       lastBid  = ((Offer)action).getBid();
                       Main.logger.add(action.toString());
                       Main.logger.add("Utility of " + agentA.getName() +": " + agentA.utilitySpace.getUtility(lastBid));
                       Main.logger.add("Utility of " + agentB.getName() +": " + agentB.utilitySpace.getUtility(lastBid));
                       checkAgentActivity(currentAgent) ;
                   } else                   
                   if ((action instanceof Accept)||
                       (action instanceof EndNegotiation)) {
                       stopNegotiation = true;
                       
                       if (action instanceof Accept) {
                           Accept accept = (Accept)action;
                           if(lastBid!=null) {
                                Main.logger.add("Agents accepted the following bid:");
                                Main.logger.add(((Accept)action).toString());
                                agentAUtility = nt.getAgentAUtilitySpace().getUtility(lastBid);
                                agentBUtility = nt.getAgentBUtilitySpace().getUtility(lastBid);
                                no = new NegotiationOutcome(sessionNumber, 
                                           agentA.getClass().getCanonicalName(),
                                           agentB.getClass().getCanonicalName(),
                                           String.valueOf(agentAUtility),
                                           String.valueOf(agentBUtility),null);
                           } else { // accept.getBid==null
                                no = new NegotiationOutcome(sessionNumber, 
                                           agentA.getClass().getCanonicalName(),
                                           agentB.getClass().getCanonicalName(),
                                                            String.valueOf(0),
                                                            String.valueOf(0),
                                   "Accept was done by "+currentAgent.getName()+" but opponent did not make a last bid.");
                                checkAgentActivity(currentAgent) ;
                           }
                       } else { // action instanceof endnegotiation
                            no = new NegotiationOutcome(sessionNumber, 
                                                       agentA.getClass().getCanonicalName(),
                                                       agentB.getClass().getCanonicalName(),
                                                       String.valueOf(0),
                                                       String.valueOf(0),
                                      "Agent "+currentAgent.getName()+" ended the negotiation without agreement");
                                
                            
                       }
                       
                       // Wouter: it seems that the following is not at the right place here.
                       // it may happen that timeout occurs right here, causing a succesful nego outcome
                       // but no update of lAgentXBids
                       if(currentAgent.equals(agentA)) {
                    	   if(action instanceof Offer) {
                    		   lAgentABids.add(((Offer)action).getBid());
                    	   }                    	   
                    	   currentAgent = agentB;
                       }
                       else{
                    	   if(action instanceof Offer) {
                    		   lAgentBBids.add(((Offer)action).getBid());
                    	   }
                    	   currentAgent = agentA;
                       }
                       currentAgent.ReceiveMessage(action);
                   }
                 
                   //save last results
                   if(currentAgent.equals(agentA)) {
                	   if(action instanceof Offer) {
                		   lAgentABids.add(((Offer)action).getBid());
                	   } 
                	   currentAgent = agentB; //Wouter: seems rather useless, we finished the nego???
                   }
                   else{
                	   if(action instanceof Offer) {
                		   lAgentBBids.add(((Offer)action).getBid());
                	   } 
                	   currentAgent = agentA;
                   }

                 
                } catch(Exception e) {
                    if(e instanceof InterruptedException) {
                        no = new NegotiationOutcome(sessionNumber, 
                                                   agentA.getClass().getCanonicalName(),
                                                   agentB.getClass().getCanonicalName(),
                                                    String.valueOf(0),
                                                    String.valueOf(0),
                                                    "Negotiation was interrupted!");
                    }
                	System.out.println("Nego was interrupted in deep level");
                	e.printStackTrace();                    
                    System.exit(-1);
                    
                }
            }
            synchronized (Main.nm) {  Main.nm.notify();  }
            
        } catch (Error e) {
            if(e instanceof ThreadDeath) {
            	System.out.println("Nego was timed out");
                // Main.logger.add("Negotiation was timed out. Both parties get util=0");
            }     
             
        }
        
        
        /*
         * Wouter: WE CAN NOT DO MORE PROCESSING HERE!!!!!
         * Maybe even catching the ThreadDeath error is wrong. 
         * If we do more processing, we risk getting a ThreadDeath exception
         * causing Eclipse to pop up a dialog bringing us into the debugger.
         * 
        //Wouter: old code to plot a graph. Currently disabled. 
        // Probably will not work either, remember that the Negotiator is killed as soon
        // as this run function exits.
        double[][] lAgentAUtilities = new double[lAgentABids.size()][2];
        double[][] lAgentBUtilities = new double[lAgentBBids.size()][2];
        
        try
        {
	        for(int i=0;i< lAgentABids.size();i++) {
	        	lAgentAUtilities [i][0] = nt.getAgentAUtilitySpace().getUtility(lAgentABids.get(i));
	        	lAgentAUtilities [i][1] = nt.getAgentBUtilitySpace().getUtility(lAgentABids.get(i));
	        }
	        for(int i=0;i< lAgentBBids.size();i++) {
	        	lAgentBUtilities [i][0] = nt.getAgentAUtilitySpace().getUtility(lAgentBBids.get(i));
	        	lAgentBUtilities [i][1] = nt.getAgentBUtilitySpace().getUtility(lAgentBBids.get(i));
	        }
	        
	        if (Main.fChart==null) throw new Exception("fChart=null, can not add curve.");
	        Main.fChart.addCurve("Negotiation path of Agent A ("+String.valueOf(sessionNumber)+")", lAgentAUtilities);
	        Main.fChart.addCurve("Negotiation path of Agent B ("+String.valueOf(sessionNumber)+")", lAgentBUtilities);
	        Main.fChart.show();
	        
	        // Wouter: logger is causing crashes. Removed.......
	        //synchronized(Main.logger) { Main.logger.add("Session is finished"); }
        } catch (Exception e) {  System.out.println("Exception in negotiation (interrupt?):"+e.getMessage());e.printStackTrace();}
        */
        return;
    }
    
}
