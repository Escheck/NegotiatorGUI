/*
 * Agent.java
 *
 * Created on November 6, 2006, 9:52 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package negotiator;

import negotiator.actions.Action;
import negotiator.issue.ISSUETYPE;
import java.util.ArrayList;
import java.util.Hashtable;
import negotiator.utility.UtilitySpace;

import java.util.Date;
import negotiator.tournament.NegotiationSession2;
/**
 *
 * @author Dmytro Tykhonov
 * @author W.Pasman
 * 
 */

public abstract class Agent {
    private String          fName=null;
    public  UtilitySpace    utilitySpace;
    public	Date			startTime;
    public Integer			totalTime; // total time to complete entire nego, in seconds.
     //protected NegotiationSession 	fNegotiation;// can be accessed only in the expermental setup 
     // Wouter: disabled 21aug08, are not necessarily run from a negotiation session.
     // particularly we now have NegotiationSession2 replacing NegotiationSession.
    
    
    Hashtable<String,Double> parametervalues; // values for the parameters for this agent. Key is param name

    public Agent() {
    }

    public static String getVersion() {return "unknown";};
    
    /** TODO: handle the parameters init */
    public void init(int sessionNumber, int sessionTotalNumber, Date startTimeP, 
    		Integer totalTimeP, UtilitySpace us) {
        startTime=startTimeP;
        totalTime=totalTimeP;
    	utilitySpace=us;
        return;
    }
    
    /**
     * informs you which action the opponent did
     * @param opponentAction
     */
    public void ReceiveMessage(Action opponentAction) {
        return;
    }
    
    /**
     * this function is called after ReceiveMessage,
     * with an Offer-action.
     * @return (should return) the bid-action the agent wants to make.
     */
    public Action chooseAction() {
        return null;
    }
    
    public String getName() {
        return fName;
    }
    
    /**
     * added W.Pasman 19aug08
     * @return arraylist with all parameter names that this agent can handle
     * defaults to an empty parameter list. Override when you use parameters.
     */
    public static ArrayList<AgentParam> getParameters() { 
    	return new ArrayList<AgentParam>();
    	}
    
    public Hashtable<String,Double> getParameterValues() {
    	return parametervalues;
    }

    /** 
     * set the values for the parameters for this agent.
     * @throws IllegalArgumentException if number of parameters is incorrect.
     * @param paramValues
     * @throws Exception
     */
    public void setParameter(String paramname, Double value) throws Exception { 
    	parametervalues.put(paramname, value);
    }
    
    
    public final void setName(String pName) {
        if(this.fName==null) this.fName = pName;
        return;
    }
    
    
    /**
     * @author W.Pasman
     * determine if this agent is communicating with the user about nego steps.
     * @return true if a human user is directly communicating with the agent in order
     * to steer the nego. This flag is used to determine the timeout for the
     * negotiation (larger with human users).
     */
    public boolean isUIAgent() { return false; }
    
    /**
     * This function cleans up the remainders of the agent: open windows etc.
     * This function will be called when the agent is killed,
     * typically when it was timed out in a nego session.
     * The agent will not be able to do any negotiation actions here, just clean up.
     * To ensure that the agent can not sabotage the negotiation, 
     * this function will be called from a separate thread.
     * 
     * @author W.Pasman
     */
    public void cleanUp() {  }
}
