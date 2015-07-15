package negotiator;

import negotiator.events.MultipartyNegotiationOfferEvent;
import negotiator.events.MultipartyNegotiationSessionEvent;
import negotiator.events.NegotiationEvent;

/**
 * implement this class in order to subscribe with the NegotiationManager to get
 * callback on handleEvent().
 * 
 * @author wouter
 *
 */
public interface MultipartyNegotiationEventListener {
	public void handleOfferActionEvent(MultipartyNegotiationOfferEvent evt);

	// public void handleLogMessageEvent(LogMessageEvent evt);

	public void handleMultipartyNegotiationEvent(
			MultipartyNegotiationSessionEvent evt);

	/**
	 * IMPORTANT: in handleEvent, do not more than just storing the event and
	 * notifying your interface that a new event has arrived. Doing more than
	 * this will snoop time from the negotiation, which will disturb the
	 * negotiation.
	 * 
	 * @param e
	 */
	public void handleEvent(NegotiationEvent e);
}
