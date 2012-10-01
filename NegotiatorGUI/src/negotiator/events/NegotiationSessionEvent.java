package negotiator.events;

import negotiator.protocol.Protocol;

public class NegotiationSessionEvent extends NegotiationEvent {

	private static final long serialVersionUID = -1005804477869072693L;
	private Protocol session;
	
	public NegotiationSessionEvent(Object source, Protocol session) {
		super(source);
		this.session = session;
	}
	public Protocol getSession() {
		return session;
	}

}
