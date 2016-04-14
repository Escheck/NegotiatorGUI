package negotiator.events;

import negotiator.protocol.Protocol;

/**
 * Reports the start of a session.
 * 
 * @author W.Pasman
 */
public class NegotiationSessionStartedEvent extends NegotiationEvent {

	private static final long serialVersionUID = -1005804477869072693L;
	private Protocol session;

	public NegotiationSessionStartedEvent(Object source, Protocol session) {
		super(source);
		this.session = session;
	}

	public Protocol getSession() {
		return session;
	}

}
