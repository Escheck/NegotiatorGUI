package negotiator.protocol;

import negotiator.Agent;
import negotiator.NegotiationOutcome;
import negotiator.tournament.VariablesAndValues.TournamentValue;
import negotiator.tournament.VariablesAndValues.TournamentVariable;

public interface MetaProtocol extends Runnable {

	public String getName();
	
	public NegotiationOutcome getNegotiationOutcome();
	
	public Agent getAgent(int index);
	
	public NegotiationSession getTournamentSessions(TournamentValue[] vars);
}
