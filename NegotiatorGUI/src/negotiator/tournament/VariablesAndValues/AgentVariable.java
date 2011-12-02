package negotiator.tournament.VariablesAndValues;

/**
 * {@link AgentVariable} indicates the agents used in a tournament.
 */
public class AgentVariable extends TournamentVariable
{
	private String side = null;
	public void addValue(TournamentValue a) throws Exception
	{
		if (!(a instanceof AgentValue))
			throw new IllegalArgumentException("Expected AgentValue but received "+a);
		values.add(a);
	}
	
	public String varToString() {
		String res = "Agent";
		if(side != null) res = res + " side " +side;
		return res;
	}
	public void setSide(String val) 
	{
		side = val;
	}
		
}