package negotiator.events;

/**
 * Abstract superclass for all multiparty tournament events.
 *
 */
public abstract class TournamentEvent extends NegotiationEvent {

	public TournamentEvent(Object source) {
		super(source);
	}

}
