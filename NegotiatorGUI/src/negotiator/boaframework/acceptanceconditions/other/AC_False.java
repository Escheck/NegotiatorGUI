package negotiator.boaframework.acceptanceconditions.other;

import negotiator.boaframework.AcceptanceStrategy;
import negotiator.boaframework.Actions;

/**
 * This Acceptance Condition never accepts an opponent offer.
 * 
 * Decoupling Negotiating Agents to Explore the Space of Negotiation Strategies
 * T. Baarslag, K. Hindriks, M. Hendrikx, A. Dirkzwager, C.M. Jonker
 * 
 * @author Alex Dirkzwager, Mark Hendrikx
 * @version 18/12/11
 */
public class AC_False extends AcceptanceStrategy {

	public AC_False() { }

	@Override
	public Actions determineAcceptability() {
		return Actions.Reject;
	}
}