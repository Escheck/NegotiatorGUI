package negotiator.tournament;

import java.util.ArrayList;
import negotiator.tournament.VariablesAndValues.*;


/**
 * This class stores all tournament info.
 * 
 * @author wouter
 *
 */
public class Tournament
{
	int nrOfRunsPerSession;
	
	ArrayList<NegotiationSession> sessions=null; // filled by buildSessions()
	ArrayList<TournamentVariable> variables=new ArrayList<TournamentVariable>();
	
	/** called when you press start button in Tournament window.
	 * This builds the sessions array from given Tournament vars 
	 * @throws exception if something wrong with the variables, eg not set. */
	public void buildSessions() throws Exception {
		// get agent A and B value(s)
		ArrayList<AgentVariable> agents=getAgentVars();
		if (agents.size()!=2) throw new IllegalStateException("Tournament does not contain 2 agent variables");
		if (agents.get(0).getValues().isEmpty()) 
			throw new IllegalStateException("Agent A does not contain any values!");
		if (agents.get(1).getValues().isEmpty()) 
			throw new IllegalStateException("Agent B does not contain any values!");
		
		getProfiles(); // TODO get compatible profiles... need to sort them on their domains.... 
	}
	
	ArrayList<AgentVariable> getAgentVars() {
		ArrayList<AgentVariable> agents=new ArrayList<AgentVariable>();
		for (TournamentVariable v: variables) {
			if (v instanceof AgentVariable) agents.add((AgentVariable)v);
		}
		return agents;
	}

	void getProfiles() { }

	
	public ArrayList<TournamentVariable> getVariables() { return variables; }
	
	public void addSession(NegotiationSession s) { 
		sessions.add(s);
	}
}