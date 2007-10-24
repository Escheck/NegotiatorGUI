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

/**
 *
 * @author Dmytro Tykhonov
 */
public class Negotiation implements Runnable {
    protected Agent         agentA;
    protected Agent         agentB;
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
    public Negotiation(Agent agentA, Agent agentB, NegotiationTemplate nt, int sessionNumber, int sessionTotalNumber) {
        this.agentA = agentA;
        this.agentB = agentB;
        this.sessionNumber=sessionNumber;
        this.sessionTotalNumber = sessionTotalNumber;
        this.nt = nt;
    }
    /** T
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
        ArrayList<Bid> lAgentBBids = new ArrayList<Bid>();;
        try {
            agentA.init(sessionNumber, sessionTotalNumber,nt.getDomain());
            agentA.loadUtilitySpace(nt.getAgentAUtilitySpaceFileName());
            agentB.init(sessionNumber, sessionTotalNumber,nt.getDomain());
            agentB.loadUtilitySpace(nt.getAgentBUtilitySpaceFileName());
            stopNegotiation = false;
            Action action = null;
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
                       Main.logger.add("Agent " + currentAgent.getName() + " did not choose any action.");                       
                   } else
                   //
                   if (action instanceof Offer) {
                       Main.logger.add("Agent " + currentAgent.getName() + " sent the following offer:");                       
                       Bid bid = ((Offer)action).getBid();
                       Main.logger.add(action.toString());
                       Main.logger.add("Utility of " + agentA.getName() +": " + agentA.getUtility(bid));
                       Main.logger.add("Utility of " + agentB.getName() +": " + agentB.getUtility(bid));
                       checkAgentActivity(currentAgent) ;
                   } else                   
                   if ((action instanceof Accept)||
                       (action instanceof EndNegotiation)) {
                       stopNegotiation = true;
                       
                       if (action instanceof Accept) {
                           Accept accept = (Accept)action;
                           if(accept.getBid()!=null) {
                                Main.logger.add("Agents accepted the following bid:");
                                Main.logger.add(((Accept)action).toString());
                                double agentAUtility = agentA.getUtility(accept.getBid());
                                double agentBUtility = agentB.getUtility(accept.getBid());
                                String mess=null;
                                if (lAgentABids.isEmpty() && lAgentBBids.isEmpty())
                                	mess="Accept was done by "+currentAgent.getName()+" before any bid was made";
                                no = new NegotiationOutcome(sessionNumber, 
                                           agentA.getClass().getCanonicalName(),
                                           agentB.getClass().getCanonicalName(),
                                           String.valueOf(agentAUtility),
                                           String.valueOf(agentBUtility),mess);
                           } else { // accept.getBid==null
                                no = new NegotiationOutcome(sessionNumber, 
                                           agentA.getClass().getCanonicalName(),
                                           agentB.getClass().getCanonicalName(),
                                                            String.valueOf(0),
                                                            String.valueOf(0),
                                   "Accept was done by "+currentAgent.getName()+" but there is no bid.");
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
                       if(currentAgent.equals(agentA)) {
                    	   if(action instanceof Offer) {
                    		   lAgentABids.add(((Offer)action).getBid());
                    	   } if(action instanceof Accept) {
                    		   lAgentABids.add(((Accept)action).getBid());
                    	   }
                    	   currentAgent = agentB;
                       }
                       else{
                    	   if(action instanceof Offer) {
                    		   lAgentBBids.add(((Offer)action).getBid());
                    	   } if(action instanceof Accept) {
                    		   lAgentBBids.add(((Accept)action).getBid());
                    	   }
                    	   currentAgent = agentA;
                       }
                       currentAgent.ReceiveMessage(action);
                   }
                 
                   //change agents
                   if(currentAgent.equals(agentA)) {
                	   if(action instanceof Offer) {
                		   lAgentABids.add(((Offer)action).getBid());
                	   } if(action instanceof Accept) {
                		   lAgentABids.add(((Accept)action).getBid());
                	   }
                	   currentAgent = agentB;
                   }
                   else{
                	   if(action instanceof Offer) {
                		   lAgentBBids.add(((Offer)action).getBid());
                	   } else if(action instanceof Accept) {
                		   lAgentBBids.add(((Accept)action).getBid());
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
            System.out.println("NOTIFY NEGOMANAGER NOW!");
            synchronized (Main.nm) {  Main.nm.notify();  }
            
        } catch (Error e) {
            if(e instanceof ThreadDeath) {
            	System.out.println("Nego was timed out");
                // Main.logger.add("Negotiation was timed out. Both parties get util=0");
            }     
             
        }
        
        
        
        double[][] lAgentAUtilities = new double[lAgentABids.size()][2];
        double[][] lAgentBUtilities = new double[lAgentBBids.size()][2];
        
        try
        {
	        for(int i=0;i< lAgentABids.size();i++) {
	        	lAgentAUtilities [i][0] = agentA.getUtility(lAgentABids.get(i));
	        	lAgentAUtilities [i][1] = agentB.getUtility(lAgentABids.get(i));
	        }
	        for(int i=0;i< lAgentBBids.size();i++) {
	        	lAgentBUtilities [i][0] = agentA.getUtility(lAgentBBids.get(i));
	        	lAgentBUtilities [i][1] = agentB.getUtility(lAgentBBids.get(i));
	        }
	        
	        /*
	       	if (Main.fChart==null) throw new Exception("fChart=null, can not add curve.");
	        Main.fChart.addCurve("Negotiation path of Agent A ("+String.valueOf(sessionNumber)+")", lAgentAUtilities);
	        Main.fChart.addCurve("Negotiation path of Agent B ("+String.valueOf(sessionNumber)+")", lAgentBUtilities);
	        Main.fChart.show();
	        */
	        // Wouter: logger is causing crashes. Removed.......
	        //synchronized(Main.logger) { Main.logger.add("Session is finished"); }
        } catch (Exception e) {  System.out.println("Exception in negotiation (interrupt?):"+e.getMessage());e.printStackTrace();}
        return;
    }
    
}
