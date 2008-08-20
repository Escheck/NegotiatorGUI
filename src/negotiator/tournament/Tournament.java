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
	 * This builds the sessions array from given Tournament vars */
	public void buildSessions() { }
	
	public ArrayList<TournamentVariable> getVariables() { return variables; }
	
	public void addSession(NegotiationSession s) { 
		sessions.add(s);
	}
}