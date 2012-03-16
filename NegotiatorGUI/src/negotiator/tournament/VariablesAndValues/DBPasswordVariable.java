package negotiator.tournament.VariablesAndValues;

/**
 * @author Mark
 * Stores the sessionname of the database.
 */
public class DBPasswordVariable extends TournamentVariable
{
	@Override
	public void addValue(TournamentValue a) throws Exception {
	}
	
	public String varToString() {
		return "Database password";
	}
}