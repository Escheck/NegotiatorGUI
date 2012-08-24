package negotiator.boaframework.acceptanceconditions.other;

import negotiator.boaframework.AcceptanceStrategy;
import negotiator.boaframework.Actions;

/**
 * This Acceptance Condition will accept any opponent offer.
 * Very handy for debugging.
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