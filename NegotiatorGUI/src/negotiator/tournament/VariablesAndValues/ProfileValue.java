package negotiator.tournament.VariablesAndValues;

import negotiator.repository.ProfileRepItem;

public class ProfileValue extends TournamentValue
{
	ProfileRepItem value;
	
	public ProfileValue(ProfileRepItem p) { value=p; }
	
	public String toString() { return value.getURL().getFile(); } // ASSUMPTION: real file in URL.
	
	public ProfileRepItem getProfile() { return value; }
}