package negotiator;

import java.util.Date;
import java.util.HashMap;
import negotiator.actions.Action;
import negotiator.protocol.BilateralAtomicNegotiationSession;
import negotiator.tournament.VariablesAndValues.AgentParamValue;
import negotiator.tournament.VariablesAndValues.AgentParameterVariable;
import negotiator.utility.UtilitySpace;

/**
 * A basic negotiation agent. You might want to consider using the BOA framework.
 *
 * @author Dmytro Tykhonov
 * @author W.Pasman
 */
public abstract class Agent 
{
	/** ID of the agent as assigned by the protocol. */
	private AgentID 		agentID;
	/** Name of the name as set by the method setName. */
    private String          fName=null;
    /** Preference profile of the agent as assigned by the tournamentrunner. */
    public  UtilitySpace    utilitySpace;
    /** Date object specifying when the negotiation started. Use timeline instead.*/
    @Deprecated
    public	Date			startTime;
    /** Total time which an agent has to complete the negotiation. Use timeline instead.*/
    @Deprecated
    public Integer			totalTime; // total time to complete entire nego, in seconds.
    /** Use timeline for everything time-related. */
    public Timeline timeline;
    /** To be implemented correctly. */
    public Integer			sessionRound;
    /** To be implemented correctly. */
    public Integer			sessionRoundTotal;
    /** Reference to protocol which is set when experimental setup is enabled. */
    public BilateralAtomicNegotiationSession 	fNegotiation;// can be accessed only in the expermental setup 
    /** Parameters given to the agent which may be specified in the agent.*/
    @Deprecated
    protected HashMap<AgentParameterVariable,AgentParamValue> parametervalues;
    /** Parameters given to the agent which may be specified in the agent repository. */
	protected StrategyParameters strategyParameters;
    
	/**
	 * Empty constructor used to initialize the agent.
	 * Later on internalInit is called to set all variables.
	 */
    public Agent() {
    	this.strategyParameters = new StrategyParameters();
    }

    /**
     * @return version of the agent.
     */
    public static String getVersion() {return "unknown";};
    
    /**
     * This method is called by the protocol every time before starting a new 
     * session after the internalInit method is called. User can override this method. 
     */
    public void init() { }
    
    /**
     * This method is called by the protocol to initialize the agent with a new session information.
     * @param sessionNumber number of the session.
     * @param sessionTotalNumber total number of sessions.
     * @param startTimeP
     * @param totalTimeP
     * @param timeline keeping track of the time in the negotiation.
     * @param us utility space of the agent for the session.
     * @param params parameters of the agent.
     */
    public final void internalInit(int sessionRound, int sessionRoundTotal, Date startTimeP, 
    		Integer totalTimeP, Timeline timeline, UtilitySpace us, HashMap<AgentParameterVariable,AgentParamValue> params) {
        startTime=startTimeP;
        totalTime=totalTimeP;
        this.timeline = timeline;
        this.sessionRound = sessionRound;
        this.sessionRoundTotal = sessionRoundTotal;
    	utilitySpace=us;
    	parametervalues = params;
    	if (parametervalues != null && !parametervalues.isEmpty())
    		System.out.println("Agent " + getName() + " initted with parameters " + parametervalues);
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
    public abstract Action chooseAction();
    
    /**
     * @return name of the agent.
     */
    public String getName() 
    {
        return fName;
    }
    
    /**
     * @return a type of parameters used solely by the BayesianAgent.
     */
    @Deprecated
    public HashMap<AgentParameterVariable,AgentParamValue> getParameterValues() {
    	return parametervalues;
    }
    
    /**
     * Sets the name of the agent to the given name.
     * @param pName to which the agent's name must be set.
     */
    public final void setName(String pName) {
        if(this.fName==null) this.fName = pName;
        return;
    }
    
    /**
     * A convenience method to get the discounted utility of a bid. This method will take discount factors into account (if any), 
     * using the status of the current {@link #timeline}.
     * @see UtilitySpace
     * @param bid of which we are interested in the utility.
     * @return discounted utility of the given bid.
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
    	long sleep = (long) ((timeline.getTotalTime() * 1000) * fraction);
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
     * Determine if this agent is communicating with the user about nego steps.
     * @return true if a human user is directly communicating with the agent in order
     * to steer the nego. This flag is used to determine the timeout for the
     * negotiation (larger with human users).
     */
    public boolean isUIAgent() { return false; }
    
    /**
     * Determine if this agent can remember information if the same session is repeated.
     * A normal agents is simply reset each round. If you do not want this, create a MultiRoundAgent.
     */
    public boolean isMultiRoundsCompatible() { return false; }
    
    /**
     * Empty method never called for a normal Agent.
     * In a next version Genius the MultiRoundAgent should be made the superclass as it is more generic.
     */
    public void beginSessionRound() { }
    
    /**
     * Empty method never called for a normal Agent.
     * In a next version Genius the MultiRoundAgent should be made the superclass as it is more generic.
     */
    public void endSessionRound() { }
    
    /**
     * This function cleans up the remainders of the agent: open windows etc.
     * This function will be called when the agent is killed,
     * typically when it was timed out in a nego session.
     * The agent will not be able to do any negotiation actions here, just clean up.
     * To ensure that the agent can not sabotage the negotiation, 
     * this function will be called from a separate thread.
     */
    public void cleanUp() {  }
    
    /**
     * @return ID of the agent as assigned by the protocol.
     */
    public AgentID getAgentID() {
    	return agentID;
    }
    
    /**
     * @param value to which the agent's ID must be set.
     */
    public void setAgentID(AgentID value) {
    	agentID = value;
    }

    /**
     * @return strategy parameters
     */
	public StrategyParameters getStrategyParameters() {
		return strategyParameters;
	}
	
	/**
	 * Used to parse parameters presented in the agent repository. The particular
	 * implementation below parses parameters such as time=0.9;e=1.0.
	 * 
	 * @param variables
	 * @throws Exception
	 */
	public void parseStrategyParameters(String variables) throws Exception {
		if (variables != null) {
			String[] vars = variables.split(";");
			for (String var : vars) {
				String[] expression = var.split("=");
				if (expression.length == 2) {
					strategyParameters.addVariable(expression[0], expression[1]);
				} else {
					throw new Exception(	"Expected variablename and result but got " + expression.length + " elements. " +
											"Correct in XML or overload the method.");
				}
			}
		}
	}
}
