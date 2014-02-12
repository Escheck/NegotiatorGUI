package negotiator.tournament.VariablesAndValues;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

public class TotalSessionNumberVariable extends TournamentVariable {

	private static final long serialVersionUID = -2284816489963319256L;

	@Override
	public void addValue(TournamentValue value) throws Exception {
		throw new NotImplementedException();
	}

	@Override
	public String varToString() {
		return "Number of sessions";
	}
}
