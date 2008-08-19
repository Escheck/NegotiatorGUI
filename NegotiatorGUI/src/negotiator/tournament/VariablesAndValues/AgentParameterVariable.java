package negotiator.tournament.VariablesAndValues;

import negotiator.AgentParam;

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
	AgentParam agentparam; // the name and other info about the parameter
	
	/** 
	 * @param para the parameter info
	 */
	public AgentParameterVariable(AgentParam para) {
		agentparam=para;
	}
	
	public void addValue(TournamentValue v) throws Exception
	{
		if (!(v instanceof AgentParamValue))
			throw new IllegalArgumentException("Expected AgentParamValue but received "+v);
		values.add(v);
	}

	public String varToString() {
		return "AgentParamValue:"+agentparam.name;
	}
	
}