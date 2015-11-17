package negotiator.utility;

import negotiator.Bid;

public abstract class ZeroOutcomeContraint {

	public abstract void addContraint(Integer issueIndex,
			String conditionToBeCheck);

	public abstract boolean willGetZeroOutcomeUtility(Bid bid);
}
