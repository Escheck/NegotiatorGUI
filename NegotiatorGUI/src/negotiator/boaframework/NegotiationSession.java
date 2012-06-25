package negotiator.boaframework;

import java.util.ArrayList;
import negotiator.Bid;
import negotiator.BidHistory;
import negotiator.Domain;
import negotiator.Timeline;
import negotiator.bidding.BidDetails;
import negotiator.issue.Issue;
import negotiator.utility.UtilitySpace;

/**
 * This is an abstract class which manages all the negotiation Session pertinent information to a single agent
 * 
 @author Alex Dirkzwager
 */
public class NegotiationSession {
	
	/** Optional outcomespace which should be set manually */
	protected OutcomeSpace outcomeSpace;
	/** History of bids made by the opponent */
	protected BidHistory opponentBidHistory;
	/** History of bids made by the agent */
	protected BidHistory ownBidHistory;
	/** Reference to the negotiation domain */
	protected Domain domain;
	/** Reference to the agent's preference profile for the domain */
	protected UtilitySpace utilitySpace;
	/** Reference to the timeline */
	protected Timeline timeline;

	protected NegotiationSession() { }
	
	public NegotiationSession(UtilitySpace utilitySpace, Timeline timeline){
		this.utilitySpace = utilitySpace;
		this.timeline = timeline;
		this.domain = utilitySpace.getDomain();
		this.opponentBidHistory = new BidHistory();
		this.ownBidHistory = new BidHistory();
	}
	
	/**
	 * Returns a list of bids offered by the opponent.
	 * @return a list of of opponent bids
	 */
	public BidHistory getOpponentBidHistory(){
		return opponentBidHistory;
	}
	
	public BidHistory getOwnBidHistory(){
		return ownBidHistory;
	}
	
	public double getDiscountFactor() {
		return utilitySpace.getDiscountFactor();
	}
	
	public ArrayList<Issue> getIssues(){
		return domain.getIssues();
	}
	
	public Timeline getTimeline(){
		return timeline;
	}
	
	/**
	 * gets the normalized time (t = [0,1])
	 * @return time normalized
	 */
	public double getTime() {
		return timeline.getTime();
	}
	
	public UtilitySpace getUtilitySpace(){
		return utilitySpace;
	}
	
	public OutcomeSpace getOutcomeSpace(){
		return outcomeSpace;
	}
	
	public void setOutcomeSpace(OutcomeSpace space) {
		this.outcomeSpace = space;
	}
	
	/**
	 * Returns the best bid in the domain.
	 */
	public BidDetails getMaxBidinDomain() {
		BidDetails maxBid = null;
		if (outcomeSpace == null) {
			try {
				Bid maximumBid = utilitySpace.getMaxUtilityBid();
				maxBid = new BidDetails(maximumBid, utilitySpace.getUtility(maximumBid), -1);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			maxBid = outcomeSpace.getMaxBidPossible();
		}
		return maxBid;
	}
	
	/**
	 * Returns the worst bid in the domain.
	 */
	public BidDetails getMinBidinDomain() {
		BidDetails minBid = null;
		if (outcomeSpace == null) {
			try {
				Bid minimumBidBid = utilitySpace.getMinUtilityBid();
				minBid = new BidDetails(minimumBidBid, utilitySpace.getUtility(minimumBidBid), -1);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			minBid = outcomeSpace.getMinBidPossible();
		}
		return minBid;
	}	
	
	public double getDiscountedUtility(Bid bid, double time){
		return utilitySpace.getUtilityWithDiscount(bid, timeline.getTime());
	}
}