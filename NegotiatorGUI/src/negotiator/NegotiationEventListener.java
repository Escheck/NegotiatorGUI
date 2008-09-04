package negotiator;

import negotiator.actions.Action;
import negotiator.events.ActionEvent;
import negotiator.events.LogMessageEvent;
import negotiator.tournament.NegotiationSession2;

/** 
 * implement this class in order to subscribe with teh NegotiationManager
 * to get callback on handleEvent().
 * 
 * @author wouter
 *
 */
public interface NegotiationEventListener 
{
	 /** IMPORTANT:
	  * in handleEvent, do not more than just storing the event and
	  * notifying your interface that a new event has arrived.
	  * Doing more than this will snoop time from the negotiation,
	  * which will disturb the negotiation.
	  * @param evt
	  */
	public void handleActionEvent(ActionEvent evt);
	
	public void handleLogMessageEvent(LogMessageEvent evt);
}


