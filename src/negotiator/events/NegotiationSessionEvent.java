package negotiator.events;

import negotiator.protocol.MetaProtocol;
import negotiator.protocol.NegotiationSession2;

public class NegotiationSessionEvent extends NegotiationEvent {
	private MetaProtocol session;
	public NegotiationSessionEvent(Object source, MetaProtocol session) {
		super(source);
		this.session = session;
	}
	public MetaProtocol getSession() {
		return session;
	}

}
