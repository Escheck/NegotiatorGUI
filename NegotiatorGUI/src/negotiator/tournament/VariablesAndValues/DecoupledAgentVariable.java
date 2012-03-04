package negotiator.tournament.VariablesAndValues;

/**
 * {@link AgentVariable} indicates the agents used in a tournament.
 */
public class DecoupledAgentVariable extends TournamentVariable
{
	private String side = null;
	public void addValue(TournamentValue a) throws Exception {
		if (!(a instanceof DecoupledAgentValue))
			throw new IllegalArgumentException("Expected DecoupledAgentValue but received "+a);
		values.add(a);
	}
	
	public String varToString() {
		String res = "DecoupledAgent";
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