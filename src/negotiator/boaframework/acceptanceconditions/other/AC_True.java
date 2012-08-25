package negotiator.boaframework.acceptanceconditions.other;

import negotiator.boaframework.AcceptanceStrategy;
import negotiator.boaframework.Actions;

/**
 * This Acceptance Condition will accept any opponent offer.
 * Very handy for debugging.
 * 
 * Decoupling Negotiating Agents to Explore the Space of Negotiation Strategies
 * T. Baarslag, K. Hindriks, M. Hendrikx, A. Dirkzwager, C.M. Jonker
 * 
 * @author Alex Dirkzwager, Mark Hendrikx
 */
public class AC_True extends AcceptanceStrategy {

	public AC_True() { }

	@Override
	public Actions determineAcceptability() {
		return Actions.Accept;
	}
}