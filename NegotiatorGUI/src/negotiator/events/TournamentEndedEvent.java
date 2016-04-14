package negotiator.events;

/**
 * Indicates that a tournament terminated.
 * 
 * @author W.Pasman 15jul15
 *
 */
public class TournamentEndedEvent extends TournamentEvent {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5141395796271569201L;

	public TournamentEndedEvent(Object source) {
		super(source);
	}

}
