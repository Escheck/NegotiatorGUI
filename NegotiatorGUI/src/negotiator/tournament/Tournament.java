package negotiator.tournament;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;



import negotiator.NegotiationEventListener;
import negotiator.Agent;

import negotiator.tournament.VariablesAndValues.*;
import negotiator.utility.UtilitySpace;
import negotiator.analysis.BidSpace;
import negotiator.protocol.Protocol;
import negotiator.repository.*;


/**
 * This class stores all tournament info.
 * Only ONE ProfileValue is allowed in the variables.
 * Only TWO AgentValues are allowed.
 * 
 * @author wouter
 *
 */
public class Tournament
{
	/** TournamentNumber is used to give a unique reference to this tournament to the user.
	 * So the first tournament the user creates is tournament 1, the second 2, etc.
	 * The number is used in the tabs, eg "tour1" as tab name.
	 */
	public final int TournamentNumber;
	int nrOfRunsPerSession;
	static final String AGENT_A_NAME="Agent A";
	static final String AGENT_B_NAME="Agent B";
	
	/** the time (ms) that GUI and non-GUI agents will get for a nego session 
	 * TODO Wouter: this is quick hack, make sure they get set properly.*/
	int tournament_gui_time=30*60, tournament_non_gui_time=120; 
	
	ArrayList<TournamentVariable> variables=new ArrayList<TournamentVariable>();
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
	ArrayList<Protocol> sessions=null;
	
	HashMap<UtilitySpace,HashMap<UtilitySpace, BidSpace>> bidSpaceCash = null;
	
	
	/** creates empty�tournament with the next TournamenNumber */
		static int next_number=1;
	public Tournament()
	{
		TournamentNumber=next_number;
		next_number++;
	}
	
	
	 /** shared counter */
	int session_number;

	/** called when you press start button in Tournament window.
	 * This builds the sessions array from given Tournament vars 
	 * The procedure skips sessions where both sides use the same preference profiles.
	 * @throws exception if something wrong with the variables, eg not set. 
	 */
	public synchronized  ArrayList<Protocol> getSessions() throws Exception {	
		session_number=1;
		bidSpaceCash = new HashMap<UtilitySpace, HashMap<UtilitySpace,BidSpace>>();
		// get agent A and B value(s)
		ArrayList<AgentVariable> agents=getAgentVars();
		if (agents.size()!=2) throw new IllegalStateException("Tournament does not contain 2 agent variables");
		ArrayList<TournamentValue> agentAvalues=agents.get(0).getValues();
		if (agentAvalues.isEmpty()) 
			throw new IllegalStateException("Agent A does not contain any values!");
		ArrayList<TournamentValue> agentBvalues=agents.get(1).getValues();
		if (agentBvalues.isEmpty()) 
			throw new IllegalStateException("Agent B does not contain any values!");

		ArrayList<ProfileRepItem> profiles=getProfiles();
		
		// we need to exhaust the possible combinations of all variables.
		// we iterate explicitly over the profile and agents, because we need to permutate
		// only the parameters for the selected agents.
		ArrayList<Protocol>sessions =new ArrayList<Protocol>();
		for (ProfileRepItem profileA: profiles) {
			for (ProfileRepItem profileB: profiles) {
				if (!(profileA.getDomain().equals(profileB.getDomain())) ) continue; // domains must match. Optimizable by selecting matching profiles first...
				if (profileA.equals(profileB)) continue;
				for (TournamentValue agentAval: agentAvalues ) {
					AgentRepItem agentA=((AgentValue)agentAval).getValue();
					for (TournamentValue agentBval: agentBvalues) {
						AgentRepItem agentB=((AgentValue)agentBval).getValue();
						sessions.addAll(allParameterCombis(agentA,agentB,profileA,profileB));
					}
				}
				
			}
		}
		return sessions;
	}
	
	/** 
	 * This is a recursive function that iterates over all *parameters* and tries all values for each,
	 * recursively calling itself to iterate over the remaining parameters.
	 * This only runs over parameters, not the other variables (Agents and Profiles)
	 * because there may be many parameters and we need to filter 
	 * Not all permutations of the vars are acceptable, for instance domains have to be idnetical.
	 * One optimization: 
	 * @param sessions is the final result: all valid permutations of variables. 
	 * @param varnr is the index of the variable in the variables array.
	 * @throws exception if one of the variables contains no values (which would prevent any 
	 * running sessions to be created with that variable.
	 */
	ArrayList<Protocol> allParameterCombis(AgentRepItem agentA,AgentRepItem agentB,
			ProfileRepItem profileA, ProfileRepItem profileB) throws Exception {
		ArrayList<AssignedParameterVariable> allparameters;
		allparameters=getParametersOfAgent(agentA,AGENT_A_NAME);
		allparameters.addAll(getParametersOfAgent(agentB,AGENT_B_NAME)); // are the run-time names somewhere?
		ArrayList<Protocol> sessions=new ArrayList<Protocol>();
		allParameterCombis(allparameters,sessions,profileA,profileB,agentA,agentB,new ArrayList<AssignedParamValue>());
		return sessions;
	}
	
	/**
	 * adds all permutations of all NegotiationSessions to the given sessions array.
	 * Note, this is not threadsafe, if called from multiple threads the session number will screw up.
	 * @param allparameters the parameters of the agents that were selected for this nego session.
	 * @param sessions
	 * @throws Exception
	 */
	void allParameterCombis(ArrayList<AssignedParameterVariable> allparameters, ArrayList<AlternatingOffersNegotiationSession> sessions,
			ProfileRepItem profileA, ProfileRepItem profileB,
			AgentRepItem agentA, AgentRepItem agentB,ArrayList<AssignedParamValue> chosenvalues) throws Exception {
		if (allparameters.isEmpty()) {
			 // separate the parameters into those for agent A and B.
			HashMap<AgentParameterVariable,AgentParamValue> paramsA = new HashMap<AgentParameterVariable,AgentParamValue>();
			HashMap<AgentParameterVariable,AgentParamValue> paramsB = new HashMap<AgentParameterVariable,AgentParamValue>();
			int i=0;
			for (AssignedParamValue v: chosenvalues) {
				if (v.agentname==AGENT_A_NAME) paramsA.put(allparameters.get(i).parameter, v.value); 
				else paramsB.put(allparameters.get(i).parameter,v.value);
				i++;
			}
			 // TODO compute total #sessions. Now fixed to 9999
			int numberOfSessions = 1;
			if(variables.get(3).getValues().size()>0)
				numberOfSessions = ((TotalSessionNumberValue)( variables.get(3).getValues().get(0))).getValue();
			Protocol session =new Protocol(agentA, agentB, profileA,profileB,
		    		AGENT_A_NAME, AGENT_B_NAME,paramsA,paramsB,session_number++, numberOfSessions , false,
		    		tournament_gui_time, tournament_non_gui_time,TournamentNumber) ;
			sessions.add(session);
			//check if the analysis is already made for the prefs. profiles
			BidSpace bidSpace = getBidSpace(session.getAgentAUtilitySpace(), session.getAgentBUtilitySpace());
			if(bidSpace!=null) {
				session.setBidSpace(bidSpace);
			} else {
				bidSpace = new BidSpace(session.getAgentAUtilitySpace(),session.getAgentBUtilitySpace());
				addBidSpaceToCash(session.getAgentAUtilitySpace(), session.getAgentBUtilitySpace(), bidSpace);
				session.setBidSpace(bidSpace);
			}
		} else {
			// pick next variable, and compute all permutations.
			AssignedParameterVariable v=allparameters.get(0);
			 // remove that variable from the list... using clone to avoid damaging the original being used higher up
			ArrayList<AssignedParameterVariable> newparameters=(ArrayList<AssignedParameterVariable>)allparameters.clone();
			newparameters.remove(0);
			ArrayList<TournamentValue> tvalues=v.parameter.getValues();
			if (tvalues.isEmpty()) throw new IllegalArgumentException("tournament parameter "+v.parameter+" has no values!");
			 // recursively do all permutations for the remaining vars.
			for (TournamentValue tv: tvalues) {
				ArrayList<AssignedParamValue> newchosenvalues=(ArrayList<AssignedParamValue>) chosenvalues.clone();
				newchosenvalues.add(new AssignedParamValue((AgentParamValue)tv,v.agentname));
				allParameterCombis(newparameters, sessions, profileA,  profileB,agentA,  agentB,newchosenvalues);
			} 
		}	    	
	}

	
	/**
	 * @param agent the agent you want the parameters of
	 * @param name the name of this unique instantiation of the agent. Typically AgentA and AgentB.
	 * @return ArrayList of AssignedParameterVariable of given agent that are selected/set in this tournament.
	 */
	ArrayList<AssignedParameterVariable> getParametersOfAgent(AgentRepItem agent,String name) {
		ArrayList<AssignedParameterVariable> allparameters= new ArrayList<AssignedParameterVariable>();
		for (TournamentVariable v: variables) {
			if (!(v instanceof AgentParameterVariable)) continue;
			AgentParameterVariable agentparam = (AgentParameterVariable)v;
			if (!(agentparam.getAgentParam().agentclass.equals(agent.getClass()))) continue;	
			allparameters.add(new AssignedParameterVariable(agentparam,name));
		}
		return allparameters;
	}
	
	/**
	 * @return the available AgentVariables in the tournament.
	 */
	ArrayList<AgentVariable> getAgentVars() {
		ArrayList<AgentVariable> agents=new ArrayList<AgentVariable>();
		for (TournamentVariable v: variables) {
			if (v instanceof AgentVariable) agents.add((AgentVariable)v);
		}
		return agents;
	}


	 /**
	  * Get the profiles that are available.
	  * The TournamentVarsUI will always place them in position 0 of the array but that is not mandatory.
	  */
	ArrayList<ProfileRepItem> getProfiles() throws Exception { 
		for (TournamentVariable v: variables) {
			if (v instanceof ProfileVariable) { 
				ArrayList<ProfileRepItem> profiles=new ArrayList<ProfileRepItem>();
				 for (TournamentValue tv: ((ProfileVariable)v).getValues()) {
					 profiles.add( ((ProfileValue)tv).getProfile());
				 }
				 return profiles;
			}
		}
		throw new RuntimeException("tournament does not contain a profile variable");
	}

	
	public ArrayList<TournamentVariable> getVariables() { return variables; }
	
	protected void addBidSpaceToCash(UtilitySpace spaceA, UtilitySpace spaceB, BidSpace bidSpace) {
		HashMap<UtilitySpace, BidSpace> cashA = new HashMap<UtilitySpace, BidSpace>();
		HashMap<UtilitySpace, BidSpace> cashB = new HashMap<UtilitySpace, BidSpace>();
		cashA.put(spaceA, bidSpace);		
		cashB.put(spaceB, bidSpace);
		bidSpaceCash.put(spaceA, cashB);		 
		bidSpaceCash.put(spaceB, cashA);
	}
	protected BidSpace getBidSpace(UtilitySpace spaceA, UtilitySpace spaceB) {		
		if(bidSpaceCash.get(spaceA)!=null)			
			return bidSpaceCash.get(spaceA).get(spaceB);
		else return null;
	}
}


/** simple datastructure to couple a parameter to an specific agent.
 * We need to do this because the AgentParam in the tournament are bound to a CLASS, not a particular agent,
 * while in the nego session we need to bind params to particular agents. */

class AssignedParameterVariable {
	public AgentParameterVariable parameter;
	public String agentname;
	AssignedParameterVariable(AgentParameterVariable param,String name) {
		parameter = param;
		agentname = name;
	}
}

class AssignedParamValue {
	public AgentParamValue value;
	public String agentname;
	AssignedParamValue(AgentParamValue v,String name) {
		value = v;
		agentname = name;
	}
}