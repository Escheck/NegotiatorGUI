package negotiator.tournament.VariablesAndValues;

import negotiator.repository.AgentRepItem;

public class AgentValue extends TournamentValue
{
	AgentRepItem value;	
	
	public String toString() { return value.getName(); }
}