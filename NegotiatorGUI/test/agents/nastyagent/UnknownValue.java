package agents.nastyagent;

import java.util.List;

import negotiator.Bid;
import negotiator.actions.Action;
import negotiator.actions.Offer;
import negotiator.issue.Issue;
import negotiator.issue.IssueDiscrete;
import negotiator.issue.Value;
import negotiator.issue.ValueDiscrete;

/**
 * returns a deliberately miscrafted bid that contains an slightly altered value
 * that is not in the domain description. It only works if it finds an
 * issueDiscrete.
 * 
 * @author W.Pasman 2nov15
 *
 */
public class UnknownValue extends NastyAgent {
	@Override
	public Action chooseAction(List<Class<? extends Action>> possibleActions) {
		Bid bid = bids.get(0);

		int id = -1;
		for (Issue issue : bid.getIssues()) {
			if (issue instanceof IssueDiscrete) {
				id = issue.getNumber();
				break;
			}
		}
		if (id >= 0) {
			// found an issue Discrete, modify it.
			Value value;
			try {
				value = bid.getValue(id);
			} catch (Exception e) {
				throw new IllegalStateException(e);
			}

			bid = bid.putValue(id, new ValueDiscrete("new"
					+ ((ValueDiscrete) value).getValue()));
		} else {
			throw new IllegalArgumentException(
					"UnknownValue agent needs an IssueDiscrete");
		}

		return new Offer(bid);
	}
}
