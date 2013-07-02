package negotiator;


import negotiator.events.LogMessageEvent;
import negotiator.events.MultipartyNegotiationOfferEvent;
import negotiator.events.MultipartyNegotiationSessionEvent;


/** 
 * implement this class in order to subscribe with the NegotiationManager
 * to get callback on handleEvent().
 * 
 * @author wouter
 *
 */
public interface MultipartyNegotiationEventListener 
{
	 /** IMPORTANT:
	  * in handleEvent, do not more than just storing the event and
	  * notifying your interface that a new event has arrived.
	  * Doing more than this will snoop time from the negotiation,
	  * which will disturb the negotiation.
	  * @param evt
	  */
	public void handleOfferActionEvent(MultipartyNegotiationOfferEvent evt);
	
	public void handleLogMessageEvent(LogMessageEvent evt);
	
	public void handleMultipartyNegotiationEvent(MultipartyNegotiationSessionEvent evt);
}


