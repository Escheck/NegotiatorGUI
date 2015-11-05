package agents.anac.y2014.Aster;

import java.util.ArrayList;

import negotiator.Bid;
import negotiator.issue.Issue;
import negotiator.issue.IssueInteger;
import negotiator.issue.ValueInteger;
import negotiator.utility.AdditiveUtilitySpace;

public class SearchBid {
	private AdditiveUtilitySpace utilitySpace;

	public SearchBid(AdditiveUtilitySpace utilitySpace) {
		this.utilitySpace = utilitySpace;
	}

	/**
	 * Search Bid for nonlinear ã� ã�„ã�¶é«˜é€ŸåŒ–ã�—ã�Ÿãƒ�ãƒ¼ã‚¸ãƒ§ãƒ³
	 */
	public ArrayList<Bid> searchOfferingBid(ArrayList<Bid> bidHistory, Bid bid,
			double bidTarget) throws Exception {
		ArrayList<Issue> issues = utilitySpace.getDomain().getIssues();
		Bid nextBid = new Bid(bid);
		IssueInteger lIssueInteger;
		double nextBidUtility;
		int issueNumber, issueIndexMin, issueIndexMax;

		for (Issue issue : issues) {
			lIssueInteger = (IssueInteger) issue;
			issueNumber = lIssueInteger.getNumber();
			issueIndexMin = lIssueInteger.getLowerBound();
			issueIndexMax = lIssueInteger.getUpperBound();

			for (int i = issueIndexMin; i <= issueIndexMax; i++) {
				nextBid = nextBid.putValue(issueNumber, new ValueInteger(i));
				nextBidUtility = utilitySpace.getUtility(nextBid);

				// æ–°ã�—ã�„Bidã�ŒTargetä»¥ä¸‹ã�®å ´å�ˆã�¯å�´ä¸‹
				if (nextBidUtility <= bidTarget) {
					continue;
				}

				// æ–°ã�—ã�„Bidã�Œç�¾åœ¨ã�®æŽ¢ç´¢ãƒ†ãƒ¼ãƒ–ãƒ«ã�«ã�ªã�‘ã‚Œã�°è¿½åŠ 
				if (!bidHistory.contains(nextBid)) {
					Bid newBid = new Bid(nextBid);
					bidHistory.add(newBid);
				}

			}
			nextBid = new Bid(bid); // BidReset
		}

		return bidHistory;
	}
}
