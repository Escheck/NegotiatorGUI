/*
 * Agent.java
 *
 * Created on November 6, 2006, 9:52 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package negotiator;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import negotiator.actions.Action;
import negotiator.protocol.BilateralAtomicNegotiationSession;
import negotiator.tournament.VariablesAndValues.AgentParamValue;
import negotiator.tournament.VariablesAndValues.AgentParameterVariable;
import negotiator.utility.UtilitySpace;
/**
 *
 * @author Dmytro Tykhonov
 * @author W.Pasman
 * 
 */


public abstract class Agent 
{
	private AgentID 		agentID;
    private String          fName=null;
    public  UtilitySpace    utilitySpace;
    @Deprecated
    public	Date			startTime;
    @Deprecated
    public Integer			totalTime; // total time to complete entire nego, in seconds.
    /** Use timeline for everything time-related */
    public Timeline timeline;
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
    		Integer totalTimeP, Timeline timeline, UtilitySpace us, HashMap<AgentParameterVariable,AgentParamValue> params) {
        startTime=startTimeP;
        totalTime=totalTimeP;
        this.timeline = timeline;
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
    
    public String getName() 
    {
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
     * A convenience method to get the utility of a bid. This method will take discount factors into account (if any), 
     * using the status of the current {@link #timeline}.
     * @see {@link UtilitySpace}.
     */
    public double getUtility(Bid bid)
    {
    	return utilitySpace.getUtilityWithDiscount(bid, timeline);
    }
    
    /**
     * Let the agent wait.
     * Example:<br>
     * sleep(0.1) will let the agent sleep for 10% of the negotiation time (as defined by the {@link Timeline}).
     * @param fraction should be between 0 and 1.
     */
    public void sleep(double fraction)
    {
    	long sleep = (long) (timeline.getTotalMiliseconds() * fraction);
    	try
		{
			Thread.sleep(sleep);
		} catch (InterruptedException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
