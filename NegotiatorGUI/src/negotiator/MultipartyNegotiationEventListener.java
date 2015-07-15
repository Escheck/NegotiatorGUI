package negotiator;

import negotiator.events.NegotiationEvent;

/**
 * implement this class in order to subscribe with the NegotiationManager to get
 * callback on handleEvent().
 * 
 * @author W.Pasman
 *
 */
public interface MultipartyNegotiationEventListener {

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
