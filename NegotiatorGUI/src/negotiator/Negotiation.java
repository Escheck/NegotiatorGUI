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
        ArrayList<Bid> lAgentABids = new ArrayList<Bid>();
        ArrayList<Bid> lAgentBBids = new ArrayList<Bid>();;
    	
        try {
            agentA.init(sessionNumber, sessionTotalNumber, nt);
            agentA.loadUtilitySpace(nt.getAgentAUtilitySpaceFileName());
            agentB.init(sessionNumber, sessionTotalNumber, nt);
            agentB.loadUtilitySpace(nt.getAgentBUtilitySpaceFileName());
            stopNegotiation = false;
            Action action = null;
            Agent agents[] = {agentA, agentB};
            Agent agent = getRandomAgent(agents) ;
            Main.logger.add("Agent " + agent.getName() + " begins");
            while(!stopNegotiation) {
                try {
                   //inform agent about last action of his opponent
                   agent.ReceiveMessage(action);
                   //get next action of the agent that has its turn now
                   action = agent.chooseAction();
                   if(action==null) {
                       Main.logger.add("Agent " + agent.getName() + " did not choose any action.");                       
                   } else
                   //
                   if (action instanceof Offer) {
                       Main.logger.add("Agent " + agent.getName() + " sent the following offer:");                       
                       Bid bid = ((Offer)action).getBid();
                       Main.logger.add(action.toString());
                       Main.logger.add("Utility of " + agentA.getName() +": " + agentA.getUtility(bid));
                       Main.logger.add("Utility of " + agentB.getName() +": " + agentB.getUtility(bid));
                       checkAgentActivity(agent) ;
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
                                no = new NegotiationOutcome(sessionNumber, 
                                           agentA.getClass().getCanonicalName(),
                                           agentB.getClass().getCanonicalName(),
                                           String.valueOf(agentAUtility),
                                           String.valueOf(agentBUtility));
                           } else {
                                Main.logger.add("Agents ended the negotiation without agreement");
                                no = new NegotiationOutcome(sessionNumber, 
                                           agentA.getClass().getCanonicalName(),
                                           agentB.getClass().getCanonicalName(),
                                                            String.valueOf(0),
                                                            String.valueOf(0));
                                checkAgentActivity(agent) ;
                           }
                       } else {
                            Main.logger.add("Agents ended the negotiation without agreement");
                            no = new NegotiationOutcome(sessionNumber, 
                                                       agentA.getClass().getCanonicalName(),
                                                       agentB.getClass().getCanonicalName(),
                                                       String.valueOf(0),
                                                       String.valueOf(0));
                                
                            
                       }
                       if(agent.equals(agentA)) {
                    	   if(action instanceof Offer) {
                    		   lAgentABids.add(((Offer)action).getBid());
                    	   }
                    	   agent = agentB;
                       }
                       else{
                    	   if(action instanceof Offer) {
                    		   lAgentABids.add(((Offer)action).getBid());
                    	   }
                    	   agent = agentA;
                       }
                       agent.ReceiveMessage(action);
                   }
                 
                   //change agents
                   if(agent.equals(agentA)) agent = agentB;
                   else agent = agentA;
                 
                } catch(Exception e) {
                    if(e instanceof InterruptedException) {
                        Main.logger.add("Negotiation was interrupted!!!");
                        no = new NegotiationOutcome(sessionNumber, 
                                                   agentA.getClass().getCanonicalName(),
                                                   agentB.getClass().getCanonicalName(),
                                                    String.valueOf(0),
                                                    String.valueOf(0));
                                
                        
                    } else {
                    	
                    	e.printStackTrace();

                    }
                    System.exit(-1);
                }
            }
        } catch (Exception e) {
                    if(e instanceof InterruptedException) {
                        Main.logger.add("Negotiation was interrupted!!!");
                    } else e.printStackTrace();
                    
                
        }
        synchronized (Main.nm) {
            Main.nm.notify();
        }
/*        double[][] lAgentAUtilities = new double[lAgentABids.size()][2];
        double[][] lAgentBUtilities = new double[lAgentBBids.size()][2];
        for(int i=0;i< lAgentABids.size();i++) {
        	lAgentAUtilities [i][0] = agentA.getUtility(lAgentABids.get(i));
        	lAgentAUtilities [i][1] = agentB.getUtility(lAgentABids.get(i));
        }
        for(int i=0;i< lAgentBBids.size();i++) {
        	lAgentBUtilities [i][0] = agentA.getUtility(lAgentBBids.get(i));
        	lAgentBUtilities [i][1] = agentB.getUtility(lAgentBBids.get(i));
        }
        
        Main.fChart.addCurve("Negotiation path of Agent A ("+String.valueOf(sessionNumber)+")", lAgentAUtilities);
        Main.fChart.addCurve("Negotiation path of Agent B ("+String.valueOf(sessionNumber)+")", lAgentBUtilities);
        */
        Main.logger.add("Session is finished");
        return;
    }
}
