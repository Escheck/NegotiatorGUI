package negotiator;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;

import negotiator.actions.Action;
import negotiator.exceptions.Warning;
import negotiator.protocol.BilateralAtomicNegotiationSession;
import negotiator.tournament.TournamentRunner;
import negotiator.tournament.VariablesAndValues.AgentParamValue;
import negotiator.tournament.VariablesAndValues.AgentParameterVariable;
import negotiator.utility.DataObjects;
import negotiator.utility.UtilitySpace;

/**
 * A basic negotiation agent. You might want to consider using the BOA
 * framework.
 * 
 * @author Dmytro Tykhonov
 * @author W.Pasman
 */
public abstract class Agent {

	/** ID of the agent as assigned by the protocol. */
	private AgentID agentID;
	/** Name of the name as set by the method setName. */
	private String fName = null;
	/** Preference profile of the agent as assigned by the tournamentrunner. */
	public UtilitySpace utilitySpace;
	/**
	 * Date object specifying when the negotiation started. Use timeline
	 * instead.
	 */
	@Deprecated
	public Date startTime;
	/**
	 * Total time which an agent has to complete the negotiation. Use timeline
	 * instead.
	 */
	@Deprecated
	public Integer totalTime; // total time to complete entire nego, in seconds.
	/** Use timeline for everything time-related. */
	public Timeline timeline;
	/**
	 * A session can be repeated multiple times. This parameter specifies which
	 * match we are at in the range [0, totalMatches - 1].
	 */
	public int sessionNr;
	/**
	 * Amount of repetitions of this session, how many times this session is
	 * repeated in total.
	 */
	public int sessionsTotal;
	/** Reference to protocol which is set when experimental setup is enabled. */
	public BilateralAtomicNegotiationSession fNegotiation;// can be accessed
															// only in the
															// expermental setup
	/** Parameters given to the agent which may be specified in the agent. */
	@Deprecated
	protected HashMap<AgentParameterVariable, AgentParamValue> parametervalues;
	/**
	 * Parameters given to the agent which may be specified in the agent
	 * repository.
	 */
	protected StrategyParameters strategyParameters;
	/**
	 * A static instance (shared by all UtilitySpace instances) which handles
	 * saving and loading data for the agents. We set "DataObjects" to be the
	 * source folder that saves the data.
	 */
	private static DataObjects dataObjects = new DataObjects("DataObjects");

	/**
	 * Empty constructor used to initialize the agent. Later on internalInit is
	 * called to set all variables.
	 */
	public Agent() {
		this.strategyParameters = new StrategyParameters();
	}

	/**
	 * @return version of the agent.
	 */
	public String getVersion() {
		return "unknown";
	};

	/**
	 * This method is called by the protocol every time before starting a new
	 * session after the internalInit method is called. User can override this
	 * method.
	 */
	public void init() {
	}

	/**
	 * This method is called by the protocol to initialize the agent with a new
	 * session information.
	 * 
	 * @param startTimeP
	 * @param totalTimeP
	 * @param timeline
	 *            keeping track of the time in the negotiation.
	 * @param us
	 *            utility space of the agent for the session.
	 * @param params
	 *            parameters of the agent.
	 */
	public final void internalInit(int sessionNr, int sessionsTotal,
			Date startTimeP, Integer totalTimeP, Timeline timeline,
			UtilitySpace us,
			HashMap<AgentParameterVariable, AgentParamValue> params) {
		startTime = startTimeP;
		totalTime = totalTimeP;
		this.timeline = timeline;
		this.sessionNr = sessionNr;
		this.sessionsTotal = sessionsTotal;
		utilitySpace = us;
		parametervalues = params;
		if (parametervalues != null && !parametervalues.isEmpty())
			System.out.println("Agent " + getName()
					+ " initted with parameters " + parametervalues);
		return;
	}

	/**
	 * informs you which action the opponent did
	 * 
	 * @param opponentAction
	 */
	public void ReceiveMessage(Action opponentAction) {
		return;
	}

	/**
	 * this function is called after ReceiveMessage, with an Offer-action.
	 * 
	 * @return (should return) the bid-action the agent wants to make.
	 */
	public abstract Action chooseAction();

	/**
	 * @return name of the agent.
	 */
	public String getName() {
		return fName;
	}

	/**
	 * @return a type of parameters used solely by the BayesianAgent.
	 */
	@Deprecated
	public HashMap<AgentParameterVariable, AgentParamValue> getParameterValues() {
		return parametervalues;
	}

	/**
	 * Sets the name of the agent to the given name.
	 * 
	 * @param pName
	 *            to which the agent's name must be set.
	 */
	public final void setName(String pName) {
		if (this.fName == null)
			this.fName = pName;
		return;
	}

	/**
	 * A convenience method to get the discounted utility of a bid. This method
	 * will take discount factors into account (if any), using the status of the
	 * current {@link #timeline}.
	 * 
	 * @see UtilitySpace
	 * @param bid
	 *            of which we are interested in the utility.
	 * @return discounted utility of the given bid.
	 */
	public double getUtility(Bid bid) {
		return utilitySpace.getUtilityWithDiscount(bid, timeline);
	}

	/**
	 * Let the agent wait in case of a time-based protocol. Example:<br>
	 * sleep(0.1) will let the agent sleep for 10% of the negotiation time (as
	 * defined by the {@link Timeline}).
	 * 
	 * @param fraction
	 *            should be between 0 and 1.
	 */
	public void sleep(double fraction) {
		if (timeline.getType().equals(Timeline.Type.Time)) {
			long sleep = (long) ((timeline.getTotalTime() * 1000) * fraction);
			try {
				Thread.sleep(sleep);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Determine if this agent is communicating with the user about nego steps.
	 * 
	 * @return true if a human user is directly communicating with the agent in
	 *         order to steer the nego. This flag is used to determine the
	 *         timeout for the negotiation (larger with human users).
	 */
	public boolean isUIAgent() {
		return false;
	}

	/**
	 * Method which informs an agent about the utility it received.
	 * 
	 * @param dUtil
	 *            discounted utility of previous session round.
	 */
	public void endSession(NegotiationResult dUtil) {
	}

	/**
	 * @return ID of the agent as assigned by the protocol.
	 */
	public AgentID getAgentID() {
		return agentID;
	}

	/**
	 * @param value
	 *            to which the agent's ID must be set.
	 */
	public void setAgentID(AgentID value) {
		agentID = value;
	}

	public StrategyParameters getStrategyParameters() {
		return strategyParameters;
	}

	/**
	 * Indicates what negotiation settings are supported by an agent, such as
	 * linear or non-linear utility spaces. The default is non-restrictive, but
	 * can be overriden if the agent can only be used in certain settings.
	 */
	public SupportedNegotiationSetting getSupportedNegotiationSetting() {
		return SupportedNegotiationSetting.getDefault();
	}

	/**
	 * Used to parse parameters presented in the agent repository. The
	 * particular implementation below parses parameters such as time=0.9;e=1.0.
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
					strategyParameters
							.addVariable(expression[0], expression[1]);
				} else {
					throw new Exception(
							"Expected variablename and result but got "
									+ expression.length + " elements. "
									+ "Correct in XML or overload the method.");
				}
			}
		}
	}

	/**
	 * @return which session we are in. If a session is first started it is
	 *         zero.
	 */
	public int getSessionNumber() {
		return sessionNr;
	}

	/**
	 * @return total sessions. How many times the session is repeated.
	 */
	public int getSessionsTotal() {
		return sessionsTotal;
	}

	/**
	 * Saves information (dataToSave) about the current session for future
	 * loading by the agent, when negotiating again with the specific preference
	 * profile referred by "filename".
	 * 
	 * Important: 1) The dataToSave must implement {@link Serializable}
	 * interface. This is to make sure the data can be saved. 2) If the function
	 * is used more than once regarding the same preference profile, it will
	 * override the data saved from last session with the new Object
	 * "dataToSave".
	 * 
	 * @param dataToSave
	 *            the data regarding the last session that "agent" wants to
	 *            save.
	 * @return true if dataToSave is successfully saved, false otherwise.
	 */
	final protected boolean saveSessionData(Serializable dataToSave) {
		String agentClassName = getUniqueIdentifier();
		try { // utility may be null
			String prefProfName = utilitySpace.getFileName();
			return dataObjects.saveData(dataToSave, agentClassName,
					prefProfName);
		} catch (Exception e) {
			new Warning("Exception during saving data for agent "
					+ agentClassName + " : " + e.getMessage());
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * @return unique identifier of the Agent object.
	 */
	protected String getUniqueIdentifier() {
		return getClass().getName();
	}

	/**
	 * Loads the {@link Serializable} data for the agent. If the function
	 * "saveSessionData" wasn't used for this type of agent with the current
	 * preference profile of the agent - the data will be null. Otherwise, it
	 * will load the saved data of the agent for this specific preference
	 * profile.
	 * 
	 * @return the {@link Serializable} data if the load is successful, null
	 *         otherwise.
	 */
	final protected Serializable loadSessionData() {

		String agentClassName = getUniqueIdentifier();
		try { // utility may be null
			String prefProfName = utilitySpace.getFileName();
			return dataObjects.loadData(agentClassName, prefProfName);
		} catch (Exception e) {
			new Warning("Exception during loading data for agent "
					+ agentClassName + " : " + e.getMessage());
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Restarts the folder "DataObjects", meaning it deletes all files in it and
	 * then creates a new empty folder with the same name.
	 * 
	 * @param sender
	 *            the Object trying to reset DataObjectFolder NOTE: only
	 *            TournamentRunner is allowed to apply this procedure.
	 * @return true if succeeded
	 */
	public static boolean restartDataObjectsFolder(Object sender) {
		if (sender instanceof TournamentRunner) {
			return dataObjects.restartFolder();
		}
		return false;
	}

}