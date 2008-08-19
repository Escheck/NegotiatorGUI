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

/*
import negotiator.issue.Issue;
import negotiator.issue.IssueDiscrete;
import negotiator.issue.IssueInteger;
import negotiator.issue.IssueReal;
import negotiator.issue.Value;
import negotiator.issue.ValueDiscrete;
import negotiator.issue.ValueInteger;
import negotiator.issue.ValueReal;
import negotiator.utility.EvaluatorDiscrete;
*/
import negotiator.utility.UtilitySpace;

import java.util.Date;
import negotiator.tournament.NegotiationSession;
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
    protected NegotiationSession 	fNegotiation;// can be accessed only in the expermental setup 

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
    public ArrayList<String> getParameters() { 
    	return new ArrayList<String>(); 
    	}

    /** 
     * set the values for the parameters for this agent.
     * @throws IllegalArgumentException if number of parameters is incorrect.
     * @param paramValues
     * @throws Exception
     */
    public void setParameters(ArrayList<Double> paramValues) throws Exception 
    { }
    
    
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
    public final void setNegotiationEnviroment(NegotiationSession pNegotiation) {
    	fNegotiation = pNegotiation;   	
    }
}
