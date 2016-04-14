package negotiator.events;

/**
 * Indicates that a session started. You get this message only in tournaments,
 * where multiple sessions can be running.
 * 
 * @author W.Pasman 15jul15
 *
 */
public class SessionStartedEvent extends TournamentEvent {

	private final int currentSession;
	private final int totalSessions;
	/**
	 * 
	 */
	private static final long serialVersionUID = -5141395796271569201L;

	/**
	 * 
	 * 
	 * @param source
	 * @param session
	 *            First session has number 1.
	 * @param total
	 */
	public SessionStartedEvent(Object source, int session, int total) {
		super(source);
		currentSession = session;
		totalSessions = total;
	}

	public int getCurrentSession() {
		return currentSession;
	}

	public int getTotalSessions() {
		return totalSessions;
	}

}
