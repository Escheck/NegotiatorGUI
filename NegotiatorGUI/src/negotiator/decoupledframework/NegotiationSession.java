package negotiator.decoupledframework;

import java.util.ArrayList;
import java.util.Collections;

import negotiator.Bid;
import negotiator.BidDetails;
import negotiator.BidDetailsSorter;
import negotiator.BidHistory;
import negotiator.Domain;
import negotiator.Timeline;
import negotiator.issue.Issue;
import negotiator.utility.UtilitySpace;

/**
 * This is an abstract class which manages all the negotiation Session pertinent information to a single agent
 * 
 * @author Alex Dirkzwager
 */
public class NegotiationSession {
	
	protected OutcomeSpace outcomeSpace;
	protected BidHistory opponentBidHistory;
	protected BidHistory ownBidHistory;
	protected Domain domain;
	protected double discountFactor;
	protected UtilitySpace utilSpace;
	protected Timeline timeline;
	private BidDetails maxBidDetails;
	

	public NegotiationSession(UtilitySpace utilitySpace, Timeline time){
		utilSpace = utilitySpace;
		timeline = time;
		domain = utilSpace.getDomain();
		discountFactor = utilSpace.getDiscountFactor();
		opponentBidHistory = new BidHistory();
		ownBidHistory = new BidHistory();
		try {
			Bid maxBid = utilitySpace.getMaxUtilityBid();
			double maxBidUtil = utilitySpace.getUtility(maxBid);
			maxBidDetails = new BidDetails(maxBid, maxBidUtil, 0);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void init(OutcomeSpace space) {
		this.outcomeSpace = space;
	}
	
	/**
	 * gives a list of bids which the opponent has offered
	 * @return a list of BidDetails
	 */
	public BidHistory getOpponentBidHistory(){
		return opponentBidHistory;
	}
	
	public BidHistory getOwnBidHistory(){
		return ownBidHistory;
	}
	
	public double getDiscountFactor() {
		return discountFactor;
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
		return utilSpace;
	}
	
	public OutcomeSpace getOutcomeSpace(){
		return outcomeSpace;
	}
	
	/**
	 * gets the maximum utility possible from the domain
	 * @return double 
	 */
	public BidDetails getMaxBidinDomain() {
		return maxBidDetails;
	}
	
	/**
	 * gets the minimum utility possible from the domain
	 * @return double 
	 */
	public BidDetails getMinBidinDomain() {
		ArrayList<BidDetails> outcomes = new ArrayList<BidDetails>(outcomeSpace.getAllOutcomes());
		BidDetails minBid = outcomes.get(0);
		for (int i = 0; i < outcomes.size(); i++) {
			if (outcomes.get(i).getMyUndiscountedUtil() < minBid.getMyUndiscountedUtil()) {
				minBid = outcomes.get(i);
			}
		}
		return minBid;
	}	
}
