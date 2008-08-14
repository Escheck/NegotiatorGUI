package negotiator;

import negotiator.actions.Action;
import negotiator.tournament.NegotiationSession;

/** This class records details about an action of an agent. 
 * It is passed as event to interested parties.
 * 
 * 
 * If there is a time-out or other protocol error,
 * an additional EndNegotiation action will be created
 * by the NegotiationManager and sent to listener.
 * 
 * @author wouter
 *
 */
class ActionEvent
{
	Agent actor;
	Action act;   				// Bid, Accept, etc.
	int round;					// integer 0,1,2,...: round in the overall bidding.
	long elapsedMilliseconds;	// milliseconds since start of nego. Using System.currentTimeMillis();
	NegotiationSession session; // the session details of this bid round.
	double normalizedUtilityA;
	double normalizedUtilityB;
	String errorRemarks;		// errors 
}
