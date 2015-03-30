package negotiator.utility;

import negotiator.Bid;

public abstract class RConstraint {

	public RConstraint() {}
	public abstract Integer getIssueIndex();
	public abstract boolean willZeroUtility(Bid bid) throws Exception;
	public abstract void addContraint(Integer issueIndex, String conditionToBeCheck);
}
