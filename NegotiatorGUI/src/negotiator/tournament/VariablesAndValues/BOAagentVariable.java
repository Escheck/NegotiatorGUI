package negotiator.tournament.VariablesAndValues;

/**
 * {@link AgentVariable} indicates the agents used in a tournament.
 */
public class BOAagentVariable extends TournamentVariable
{
	private String side = null;
	public void addValue(TournamentValue a) throws Exception {
		if (!(a instanceof BOAagentValue))
			throw new IllegalArgumentException("Expected DecoupledAgentValue but received "+a);
		values.add(a);
	}
	
	public String varToString() {
		String res = "BOA Agent";
		if(side != null) res = res + " side " +side;
		return res;
	}
	public void setSide(String val) 
	{
		side = val;
	}
	
	public String getSide() {
		return side;
	}
}