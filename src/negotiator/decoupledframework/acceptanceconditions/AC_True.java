package negotiator.decoupledframework.acceptanceconditions;

import negotiator.decoupledframework.AcceptanceStrategy;

/**
 * This Acceptance Condition will accept any opponent offer.
 * Very handy for debugging.
 * 
 * @author Alex Dirkzwager, Mark Hendrikx
 */
public class AC_True extends AcceptanceStrategy {

	public AC_True() { }

	@Override
	public boolean determineAcceptability() {
		return true;
	}
}