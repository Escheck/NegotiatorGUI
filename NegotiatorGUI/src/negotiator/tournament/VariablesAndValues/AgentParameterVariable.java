package negotiator.tournament.VariablesAndValues;


/**
 * ProfileVariable is a variable for a tournament,
 * indicating that the profile is to be manipulated.
 * It just is an indicator for the TournamentVariable that its
 * value array contains a ProfileValue.
 * 
 * @author wouter
 *
 */
public class AgentParameterVariable extends TournamentVariable
{
	String name;
	double min;
	double max;
	
	public AgentParameterVariable(String nm, double minimum, double maximum) {
		name=nm;
		min=minimum;
		max=maximum;
	}
	
	public void addValue(TournamentValue v) throws Exception
	{
		if (!(v instanceof ProfileValue))
			throw new IllegalArgumentException("Expected ProfileValue but received "+v);
		values.add(v);
	}
	
	public String getName() { return name; }
	
	public double getMinimum() { return min; }
	public double getMaximum() { return max; }
	
	public String varToString() {
		return "ProfileVar:"+name;
	}
	
}