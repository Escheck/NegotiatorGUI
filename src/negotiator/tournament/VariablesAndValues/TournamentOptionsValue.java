package negotiator.tournament.VariablesAndValues;

import java.util.HashMap;

public class TournamentOptionsValue extends TournamentValue {
	
	private static final long serialVersionUID = 1L;
	HashMap<String, Boolean> options = new HashMap<String, Boolean>();	
	
	public TournamentOptionsValue(HashMap<String, Boolean> options) { this.options = options; }
	
	public String toString() { return options.toString(); }
	
	public HashMap<String, Boolean> getValue(){ return options;	}
}