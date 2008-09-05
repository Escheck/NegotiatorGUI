package negotiator.events;

import negotiator.tournament.NegotiationSession2;

public class NegotiationSessionEvent extends NegotiationEvent {
	private NegotiationSession2 session;
	public NegotiationSessionEvent(Object source, NegotiationSession2 session) {
		super(source);
		this.session = session;
	}
	public NegotiationSession2 getSession() {
		return session;
	}

}
