package negotiator.events;

/**
 * Indicates that a tournament started.
 * 
 * @author W.Pasman 15jul15
 *
 */
public class TournamentStartedEvent extends TournamentEvent {

	private final int totalNumberOfSessions;
	private final int tournamentNumber;
	/**
	 * 
	 */
	private static final long serialVersionUID = -5141395796271569201L;

	public TournamentStartedEvent(Object source, int tournament,
			int totalSessions) {
		super(source);
		tournamentNumber = tournament;
		totalNumberOfSessions = totalSessions;
	}

	public int getTotalNumberOfSessions() {
		return totalNumberOfSessions;
	}

	public int getTournamentNumber() {
		return tournamentNumber;
	}

}
