package negotiator.boaframework.opponentmodel.agentsmithv2;

import misc.ScoreKeeper;
import negotiator.Bid;
import negotiator.issue.Issue;
import negotiator.issue.Value;

/**
 * Model of one issue, takes all values of the opponent on this issue. Then the
 * utility and it's weight can be calculated on this issue. In the OpponentModel this
 * information is used to determine the utility of the other party. 
 */
public class IssueModel {
	private ScoreKeeper<Value> keeper;
	private int issueNr;
	
	/**
	 * Constructor
	 * @param lIssue
	 */
	public IssueModel(Issue lIssue) {
		keeper = new ScoreKeeper<Value>();
		this.issueNr = lIssue.getNumber();
	}
	
	public void addValue(Bid pBid) {
		keeper.score(getBidValueByIssue(pBid, issueNr));
	}
	
	/**
	 * The utility of a bid, which can be real, integer or discrete
	 */
	public double getUtility(Bid pBid) {
		double lUtility = keeper.getRelativeScore(getBidValueByIssue(pBid, issueNr));
		return lUtility;
	}
	
	/**
	 * Get's the importance of this issues utility
	 */
	public double getWeight() {
		return ((double) keeper.getMaxValue() / (double) keeper.getTotal());
	}
	
	/**
	 * returns the value of an issue in a bid
	 */
	public static Value getBidValueByIssue(Bid pBid, int issueNumber) {
		Value lValue = null;
		try {
			lValue = pBid.getValue(issueNumber); 
		} catch(Exception e) { }
		
		return lValue;
		
	}
}
