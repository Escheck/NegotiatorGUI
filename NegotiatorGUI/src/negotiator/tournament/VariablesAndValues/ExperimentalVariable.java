package negotiator.tournament.VariablesAndValues;

/**
 * @author tim
 * Used for having values in experiments.
 */
public class ExperimentalVariable extends TournamentVariable
{
	@Override
	public void addValue(TournamentValue a) throws Exception
	{
		if (!(a instanceof ExperimentalValue))
			throw new IllegalArgumentException("Expected ExperimentalValue but received "+a);
		values.add(a);
	}
	
	public String varToString() {
		String res = "Experimental variable";
		return res;
	}
}