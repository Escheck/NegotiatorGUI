package negotiator.events;


import negotiator.multipartyprotocol.MultiPartyNegotiationSession;


public class MultipartyNegotiationSessionEvent extends NegotiationEvent {

	private MultiPartyNegotiationSession session;
	 
	public MultipartyNegotiationSessionEvent(Object source, MultiPartyNegotiationSession session) {
	
		super(source);
		this.session=session;
	}

	public MultiPartyNegotiationSession getSession() {
		return session;
	}
	
	
	
}
