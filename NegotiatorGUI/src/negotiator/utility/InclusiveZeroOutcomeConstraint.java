package negotiator.utility;

import java.util.HashMap;

import negotiator.Bid;

public class InclusiveZeroOutcomeConstraint extends ZeroOutcomeContraint {

	private HashMap<Integer, String> checkList;

	public InclusiveZeroOutcomeConstraint() {
		this.checkList = new HashMap<Integer, String>();
	}

	@Override
	public void addContraint(Integer issueIndex, String conditionToBeCheck) {
		this.checkList.put(issueIndex, conditionToBeCheck);
	}

	@Override
	public boolean willGetZeroOutcomeUtility(Bid bid) {

		for (Integer issueIndex : this.checkList.keySet()) {

			if (checkList.get(issueIndex).contains("numeric")) { // check if it
																	// is
																	// positive
																	// or nor

				if ((checkList.get(issueIndex).contains("positive"))
						&& (Integer
								.valueOf(bid.getValue(issueIndex).toString()) < 0)) {
					return false;
				} else if ((checkList.get(issueIndex).contains("negative"))
						&& (Integer
								.valueOf(bid.getValue(issueIndex).toString()) > 0)) {
					return false;
				}

			} else // check it involves "condition" keyword
			{
				if (!bid.getValue(issueIndex).toString()
						.contains(this.checkList.get(issueIndex))) {
					return false;
				}

			}

		}
		return true;
	}

}
