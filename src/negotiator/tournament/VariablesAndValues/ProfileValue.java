package negotiator.tournament.VariablesAndValues;

import negotiator.repository.ProfileRepItem;

public class ProfileValue extends TournamentValue
{
	ProfileRepItem value;
	
	public String toString() { return value.getURL().getFile(); } // ASSUMPTION: real file in URL.
}