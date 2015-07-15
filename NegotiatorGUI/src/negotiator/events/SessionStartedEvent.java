package negotiator.events;

/**
 * Indicates that a tournament terminated.
 * 
 * @author W.Pasman 15jul15
 *
 */
public class SessionStartedEvent extends NegotiationEvent {

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
