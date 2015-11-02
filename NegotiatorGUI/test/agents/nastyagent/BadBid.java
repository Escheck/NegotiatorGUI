package agents.nastyagent;

import java.util.List;

import negotiator.Bid;
import negotiator.actions.Action;
import negotiator.actions.Offer;
import negotiator.issue.Issue;
import negotiator.issue.IssueDiscrete;
import negotiator.issue.ValueDiscrete;
import negotiator.issue.ValueInteger;

/**
 * returns a deliberately miscrafted bid that contains an integer value where a
 * discrete is expected, or the other way round. The idea is that the opponent
 * may call getUtility on it and then cause a class cast exception.
 * 
 * @author W.Pasman 2nov15
 *
 */
public class BadBid extends NastyAgent {
	@Override
	public Action chooseAction(List<Class<? extends Action>> possibleActions) {
		Bid bid = bids.get(0);

		int randomissue = (int) (Math.random() * bid.getIssues().size());
		Issue issue = bid.getIssues().get(randomissue);
		if (issue instanceof IssueDiscrete) {
			bid.putValue(issue.getNumber(), new ValueInteger(2));
		} else {
			bid.putValue(issue.getNumber(), new ValueDiscrete("33"));
		}

		return new Offer(bid);
	}
}
