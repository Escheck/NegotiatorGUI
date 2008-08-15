package negotiator.tournament.VariablesAndValues;
import negotiator.Agent;

/**
 * ProfileVariable is a variable for a tournament,
 * indicating that the profile is to be manipulated.
 * It just is an indicator for the TournamentVariable that its
 * value array contains a ProfileValue.
 * 
 * @author wouter
 *
 */
public class AgentVariable extends TournamentVariable
{
	public void addValue(TournamentValue a) throws Exception
	{
		if (!(a instanceof AgentValue))
			throw new IllegalArgumentException("Expected AgentValue but received "+a);
		values.add(a);
	}
	
	public String varToString() {
		return "AgentVar";
	}
}