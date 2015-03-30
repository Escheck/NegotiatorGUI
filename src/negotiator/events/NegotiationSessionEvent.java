package negotiator.events;

import negotiator.protocol.OldProtocol;

public class NegotiationSessionEvent extends NegotiationEvent {

	private static final long serialVersionUID = -1005804477869072693L;
	private OldProtocol session;
	
	public NegotiationSessionEvent(Object source, OldProtocol session) {
		super(source);
		this.session = session;
	}
	public OldProtocol getSession() {
		return session;
	}

}
