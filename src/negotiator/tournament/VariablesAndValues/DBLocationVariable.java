package negotiator.tournament.VariablesAndValues;

/**
 * @author Mark
 * Stores the location of the database.
 */
public class DBLocationVariable extends TournamentVariable
{
	@Override
	public void addValue(TournamentValue a) throws Exception {
	}
	
	public String varToString() {
		return "Database address";
	}
}