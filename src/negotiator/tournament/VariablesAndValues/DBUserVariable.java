package negotiator.tournament.VariablesAndValues;

/**
 * @author Mark
 * Stores the location of the database.
 */
public class DBUserVariable extends TournamentVariable
{
	@Override
	public void addValue(TournamentValue a) throws Exception {
	}
	
	public String varToString() {
		return "Database user";
	}
}