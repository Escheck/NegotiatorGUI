package negotiator.events;

import negotiator.Bid;
import negotiator.session.Session;

/**
 * Records an end of a multi-party session end.
 *
 */
@SuppressWarnings("serial")
public class MultipartySessionEndedEvent extends NegotiationEvent {
	private Session session;
	private Bid agreement;

	public MultipartySessionEndedEvent(Object source, Session session,
			Bid agreement) {

		super(source);
		this.session = session;
		this.agreement = agreement;
	}

	public Session getSession() {
		return session;
	}

	/**
	 * 
	 * @return final agreement bid, or null if no agreement was reached
	 */
	public Bid getAgreement() {
		return agreement;
	}
}
