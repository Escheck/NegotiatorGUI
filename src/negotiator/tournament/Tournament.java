package negotiator.tournament;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Random;

import misc.Serializer;
import negotiator.Global;
import negotiator.boaframework.BOAagentInfo;
import negotiator.protocol.Protocol;
import negotiator.repository.AgentRepItem;
import negotiator.repository.ProfileRepItem;
import negotiator.repository.ProtocolRepItem;
import negotiator.tournament.VariablesAndValues.AgentParameterVariable;
import negotiator.tournament.VariablesAndValues.AgentValue;
import negotiator.tournament.VariablesAndValues.AgentVariable;
import negotiator.tournament.VariablesAndValues.AssignedParameterVariable;
import negotiator.tournament.VariablesAndValues.BOAagentValue;
import negotiator.tournament.VariablesAndValues.BOAagentVariable;
import negotiator.tournament.VariablesAndValues.ProfileValue;
import negotiator.tournament.VariablesAndValues.ProfileVariable;
import negotiator.tournament.VariablesAndValues.ProtocolValue;
import negotiator.tournament.VariablesAndValues.ProtocolVariable;
import negotiator.tournament.VariablesAndValues.TotalSessionNumberValue;
import negotiator.tournament.VariablesAndValues.TotalSessionNumberVariable;
import negotiator.tournament.VariablesAndValues.TournamentOptionsValue;
import negotiator.tournament.VariablesAndValues.TournamentOptionsVariable;
import negotiator.tournament.VariablesAndValues.TournamentValue;
import negotiator.tournament.VariablesAndValues.TournamentVariable;

/**
 * This class stores all tournament info (protocol, list of profiles, list of
 * agents, etc.) This is then converted into a list of {@link Protocol}s using
 * {@link #getSessions()}. These {@link Protocol}s (which are actually just
 * negotiation sessions) are then run by {@link TournamentRunner}, one by one,
 * in {@link TournamentRunner}.run().
 * 
 * (Tournament contains the information for only one negotiation if you choose
 * Negotiation Session!).
 * 
 * Only ONE ProfileValue is allowed in the variables. Only TWO AgentValues are
 * allowed.
 * 
 * @author wouter
 */
public class Tournament implements Serializable {
	private static final long serialVersionUID = -7088222038853719662L;
	/**
	 * TournamentNumber is used to give a unique reference to this tournament to
	 * the user. So the first tournament the user creates is tournament 1, the
	 * second 2, etc. The number is used in the tabs, eg "tour1" as tab name.
	 */
	public final int TournamentNumber;
	int nrOfRunsPerSession;
	ArrayList<BOAagentInfo> BOAagentListA = new ArrayList<BOAagentInfo>();
	ArrayList<BOAagentInfo> BOAagentListB = new ArrayList<BOAagentInfo>();
	private ArrayList<TournamentVariable> variables = new ArrayList<TournamentVariable>();
	// ASSSUMPTIONS: variable 0 is the ProfileVariable.
	// variable 1 is AgentVariable for agent A.
	// variable 2 is AgentVariable for agent B.
	// variable 3 is number of runs per session
	// rest is AgentParameterVariables.
	public static final int VARIABLE_PROTOCOL = 0;
	public static final int VARIABLE_PROFILE = 1;
	public static final int VARIABLE_AGENT_A = 2;
	public static final int VARIABLE_AGENT_B = 3;
	public static final int VARIABLE_NUMBER_OF_RUNS = 4;
	public static final int VARIABLE_TOURNAMENT_OPTIONS = 5;

	// parameters for the decoupled agents framework
	public static final int VARIABLE_DECOUPLED_A = 6;
	public static final int VARIABLE_DECOUPLED_B = 7;

	// Database parameters; used for distributed tournaments
	public static final int VARIABLE_DB_LOCATION = 8;
	public static final int VARIABLE_DB_USER = 9;
	public static final int VARIABLE_DB_PASSWORD = 10;
	public static final int VARIABLE_DB_SESSIONNAME = 11;

	ArrayList<Protocol> sessions = null;

	/** creates empty tournament with the next TournamenNumber */
	static int next_number = 1;

	public Tournament() {
		TournamentNumber = next_number;
		next_number++;
	}

	/**
	 * Get all combinations of agents, domains, etc. via reflection
	 */
	public ArrayList<Protocol> getSessions() throws Exception {
		ProtocolRepItem protRepItem = getProtocol();
		Class<Protocol> protocol = Global.getProtocolClass(protRepItem);
		Class[] paramTypes = { Tournament.class };
		Method mthdGetTournamentSessions = protocol.getMethod(
				"getTournamentSessions", paramTypes);
		sessions = (ArrayList<Protocol>) (mthdGetTournamentSessions.invoke(
				null, this));

		int mode = TournamentConfiguration
				.getIntegerOption("generationMode", 0);
		if (mode == 1) {
			int seed = TournamentConfiguration
					.getIntegerOption("randomSeed", 0);
			Collections.shuffle(sessions, new Random(seed));
		}

		return sessions;
	}

	/**
	 * Throw away all calculated sessions to allow serialization.
	 */
	public void resetTournament() {
		sessions = null;
	}

	/**
	 * @return the available AgentVariables in the tournament.
	 */
	public ArrayList<AgentVariable> getAgentVars() {
		ArrayList<AgentVariable> agents = new ArrayList<AgentVariable>();
		for (TournamentVariable v : variables) {
			if (v instanceof AgentVariable)
				agents.add((AgentVariable) v);
		}
		return agents;
	}

	/**
	 * @return the number of sessions the tournament. Default = 1.
	 */
	public int getNumberOfSessions() {
		for (TournamentVariable v : variables)
			if (v instanceof TotalSessionNumberVariable) {
				ArrayList<TournamentValue> values = ((TotalSessionNumberVariable) v)
						.getValues();
				for (TournamentValue val : values) {
					if (val instanceof TotalSessionNumberValue) {
						int nosessions = ((TotalSessionNumberValue) val)
								.getValue();
						return nosessions;
					}
				}
			}
		return 1;
	}

	/**
	 * Returns how many times each session is repeated in a tournament.
	 * 
	 * @return round count
	 */
	public int getRounds() {
		int count = 1;
		TournamentVariable runs = variables.get(VARIABLE_NUMBER_OF_RUNS);
		if (runs != null && runs.getValues().size() > 0) {
			count = Integer.parseInt(runs.getValues().get(0).toString());
		}
		return count;
	}

	public HashMap<String, Integer> getOptions() {
		TournamentVariable options = (TournamentOptionsVariable) variables
				.get(VARIABLE_TOURNAMENT_OPTIONS);
		if (options != null && options.getValues().size() > 0) {
			return ((TournamentOptionsValue) (options.getValues().get(0)))
					.getValue();
		}
		return null;
	}

	public ProtocolRepItem getProtocol() throws Exception {
		for (TournamentVariable v : variables) {
			if (v instanceof ProtocolVariable) {
				ArrayList<ProtocolRepItem> protocols = new ArrayList<ProtocolRepItem>();
				for (TournamentValue tv : ((ProtocolVariable) v).getValues()) {
					protocols.add(((ProtocolValue) tv).getValue());
				}
				return protocols.get(0);
			}
		}
		throw new RuntimeException(
				"tournament does not contain a profile variable");

	}

	/**
	 * Get the profiles that are available. The TournamentVarsUI will always
	 * place them in position 0 of the array but that is not mandatory.
	 */
	public ArrayList<ProfileRepItem> getProfiles() throws Exception {
		for (TournamentVariable v : variables) {
			if (v instanceof ProfileVariable) {
				ArrayList<ProfileRepItem> profiles = new ArrayList<ProfileRepItem>();
				for (TournamentValue tv : ((ProfileVariable) v).getValues()) {
					profiles.add(((ProfileValue) tv).getProfile());
				}
				return profiles;
			}
		}
		throw new RuntimeException(
				"tournament does not contain a profile variable");
	}

	/**
	 * @param agent
	 *            the agent you want the parameters of
	 * @param name
	 *            the name of this unique instantiation of the agent. Typically
	 *            AgentA and AgentB.
	 * @return ArrayList of AssignedParameterVariable of given agent that are
	 *         selected/set in this tournament.
	 */
	public ArrayList<AssignedParameterVariable> getParametersOfAgent(
			AgentRepItem agent, String name) {
		ArrayList<AssignedParameterVariable> allparameters = new ArrayList<AssignedParameterVariable>();
		for (TournamentVariable v : variables) {
			if (!(v instanceof AgentParameterVariable))
				continue;
			AgentParameterVariable agentparam = (AgentParameterVariable) v;
			if (!(agentparam.getAgentParam().agentclass
					.equals(agent.getClass())))
				continue;
			allparameters.add(new AssignedParameterVariable(agentparam, name));
		}
		return allparameters;
	}

	public ArrayList<TournamentVariable> getVariables() {
		return variables;
	}

	@Override
	public String toString() {
		return "Variables: " + variables + "\nSessions: " + sessions;
	}

	/**
	 * Converts the descriptions of the BOA framework agents to actual agents
	 * compatible with Genius.
	 * 
	 * @return list of Genius compatible agents
	 */
	public ArrayList<AgentVariable> getDecoupledAgentVars() {
		ArrayList<BOAagentVariable> decoupledAgentVars = new ArrayList<BOAagentVariable>();
		for (TournamentVariable v : variables) {
			if (v instanceof BOAagentVariable)
				decoupledAgentVars.add((BOAagentVariable) v);
		}
		// now we have two decoupledagentvarinfo's, which we need to convert to
		// agentvariables.
		// agentvariables are basically collections of agentrepitem.
		// An agentrepitem can be created by serializing the DecoupledAgentInfo,
		// and using it's name
		ArrayList<AgentVariable> agentVars = new ArrayList<AgentVariable>();
		for (BOAagentVariable decoupledVar : decoupledAgentVars) {
			ArrayList<TournamentValue> values = decoupledVar.getValues();
			AgentVariable agentVar = new AgentVariable();
			agentVar.setSide(decoupledVar.getSide());

			for (TournamentValue value : values) {
				BOAagentValue dav = (BOAagentValue) value;
				BOAagentInfo agent = dav.getValue();
				Serializer<BOAagentInfo> serializer = new Serializer<BOAagentInfo>(
						"");
				AgentRepItem agentRep = new AgentRepItem(agent.getName(),
						"negotiator.boaframework.agent.TheBOAagent", "",
						serializer.writeToString(agent));
				AgentValue av = new AgentValue(agentRep);
				try {
					agentVar.addValue(av);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			agentVars.add(agentVar);
		}
		return agentVars;
	}

	public ArrayList<BOAagentInfo> getBOAagentA() {
		return BOAagentListA;
	}

	public ArrayList<BOAagentInfo> getBOAagentB() {
		return BOAagentListB;
	}

	public void setBOAagentA(ArrayList<BOAagentInfo> BOAagentList) {
		BOAagentListA = BOAagentList;
	}

	public void setBOAagentB(ArrayList<BOAagentInfo> BOAagentList) {
		BOAagentListB = BOAagentList;
	}
}