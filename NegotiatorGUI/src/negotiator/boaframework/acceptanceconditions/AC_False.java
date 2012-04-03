package negotiator.boaframework.acceptanceconditions;

import negotiator.boaframework.AcceptanceStrategy;
import negotiator.boaframework.Actions;

/**
 * This Acceptance Condition never accepts an opponent offer.
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