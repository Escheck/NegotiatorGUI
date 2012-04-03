package negotiator.decoupledframework.acceptanceconditions;

import negotiator.decoupledframework.AcceptanceStrategy;
import negotiator.decoupledframework.Actions;

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