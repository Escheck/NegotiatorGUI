package negotiator.tournament;

import java.util.ArrayList;
import java.util.HashSet;



import negotiator.NegotiationEventListener;
import negotiator.Agent;

import negotiator.tournament.VariablesAndValues.*;
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
	int nrOfRunsPerSession;
	static final String AGENT_A_NAME="Agent A";
	static final String AGENT_B_NAME="Agent B";
	
	ArrayList<TournamentVariable> variables=new ArrayList<TournamentVariable>();
		// ASSSUMPTIONS: variable 0 is the ProfileVariable.
		// variable 1 is AgentVariable for agent A.
		// variable 2 is AgentVariable for agent B.
		// rest is AgentParameterVariables.
	ArrayList<NegotiationSession> sessions=null;
	
	/** called when you press start button in Tournament window.
	 * This builds the sessions array from given Tournament vars 
	 * @throws exception if something wrong with the variables, eg not set. */
	public ArrayList<NegotiationSession2> getSessions() throws Exception {
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
		ArrayList<NegotiationSession2>sessions =new ArrayList<NegotiationSession2>();
		for (ProfileRepItem profileA: profiles) {
			for (ProfileRepItem profileB: profiles) {
				if (!(profileA.getDomain().equals(profileB.getDomain())) ) continue; // domains must match. Optimizable by selecting matching profiles first...
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
	ArrayList<NegotiationSession2> allParameterCombis(AgentRepItem agentA,AgentRepItem agentB,
			ProfileRepItem profileA, ProfileRepItem profileB) throws Exception {
		ArrayList<AssignedParameterVariable> allparameters;
		allparameters=getParametersOfAgent(agentA,AGENT_A_NAME);
		allparameters.addAll(getParametersOfAgent(agentB,AGENT_B_NAME)); // are the run-time names somewhere?
		ArrayList<NegotiationSession2> sessions=new ArrayList<NegotiationSession2>();
		sessionnr=0;
		allParameterCombis(allparameters,sessions,profileA,profileB,agentA,agentB,new ArrayList<AssignedParamValue>());
		return sessions;
	}
	
	int sessionnr;
	/**
	 * adds all permutations of all NegotiationSessions to the given sessions array.
	 * Note, this is not threadsafe, if called from multiple threads the session number will screw up.
	 * @param allparameters the parameters of the agents that were selected for this nego session.
	 * @param sessions
	 * @throws Exception
	 */
	void allParameterCombis(ArrayList<AssignedParameterVariable> allparameters, ArrayList<NegotiationSession2> sessions,
			ProfileRepItem profileA, ProfileRepItem profileB,
			AgentRepItem agentA, AgentRepItem agentB,ArrayList<AssignedParamValue> chosenvalues) throws Exception {
		if (allparameters.isEmpty()) {
			 // separate the parameters into those for agent A and B.
			ArrayList<AgentParamValue> paramsA=new ArrayList<AgentParamValue>();
			ArrayList<AgentParamValue> paramsB=new ArrayList<AgentParamValue>();
			for (AssignedParamValue v: chosenvalues) {
				if (v.agentname==AGENT_A_NAME) paramsA.add(v.value); 
				else paramsB.add(v.value);
			}
			 // TODO compute total #sessions. Now fixed to 9999
			sessions.add(new  NegotiationSession2(agentA, agentB, profileA,profileB,
	    		AGENT_A_NAME, AGENT_B_NAME,paramsA,paramsB,sessionnr, 1, false, null));
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

}


/** simple datastructure to couple a parameter to an specific agent.
 * We need to do this because the AgentParam in the tournament are bound to a CLASS, not a particular agent,
 * while in the nego session we need to bind params to particular agents. */

class AssignedParameterVariable {
	public AgentParameterVariable parameter;
	public String agentname;
	AssignedParameterVariable(AgentParameterVariable param,String name) {
		parameter=param;
		agentname=name;
	}
}

class AssignedParamValue {
	public AgentParamValue value;
	public String agentname;
	AssignedParamValue(AgentParamValue v,String name) {
	value=v;
	agentname=name;
	}
}