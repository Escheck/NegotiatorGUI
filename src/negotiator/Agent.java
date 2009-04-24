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
import java.util.HashMap;
import java.util.Hashtable;
import negotiator.utility.UtilitySpace;

import java.util.Date;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import com.sun.xml.txw2.annotation.XmlElement;

import negotiator.protocol.Protocol;
import negotiator.protocol.BilateralAtomicNegotiationSession;
import negotiator.tournament.VariablesAndValues.AgentParamValue;
import negotiator.tournament.VariablesAndValues.AgentParameterVariable;
/**
 *
 * @author Dmytro Tykhonov
 * @author W.Pasman
 * 
 */


public abstract class Agent {
	private AgentID 		agentID;
    private String          fName=null;
    public  UtilitySpace    utilitySpace;
    public	Date			startTime;
    public Integer			totalTime; // total time to complete entire nego, in seconds.
    public Integer			sessionNumber;
    public Integer			sessionTotalNumber;
    public BilateralAtomicNegotiationSession 	fNegotiation;// can be accessed only in the expermental setup 
     // Wouter: disabled 21aug08, are not necessarily run from a negotiation session.
     // particularly we now have NegotiationSession2 replacing NegotiationSession.
    
    
    //protected Hashtable<String,Double> parametervalues = new Hashtable<String, Double>(); // values for the parameters for this agent. Key is param name

    protected HashMap<AgentParameterVariable,AgentParamValue> parametervalues;
    
    public Agent() {
    }

    public static String getVersion() {return "unknown";};
    /**
     * This method is called by the environment (SessionRunner) every time before starting a new 
     * session after the internalInit method is called. User can override this method. 
     */
    public void init() {
    
    }
    
    /**
     * This method is called by the SessionRunner to initialize the agent with a new session information.
     * @param sessionNumber number of the session
     * @param sessionTotalNumber total number of sessions
     * @param startTimeP
     * @param totalTimeP
     * @param us utility space of the agent for the session
     * @param params parameters of the agent
     */
    public final void internalInit(int sessionNumber, int sessionTotalNumber, Date startTimeP, 
    		Integer totalTimeP, UtilitySpace us, HashMap<AgentParameterVariable,AgentParamValue> params) {
        startTime=startTimeP;
        totalTime=totalTimeP;
        this.sessionNumber = sessionNumber;
        this.sessionTotalNumber = sessionTotalNumber;
    	utilitySpace=us;
    	parametervalues=params;
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
    
    public HashMap<AgentParameterVariable,AgentParamValue> getParameterValues() {
    	return parametervalues;
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
    
    public AgentID getAgentID() {
    	return agentID;
    }
    public void setAgentID(AgentID value) {
    	agentID = value;
    }

}
