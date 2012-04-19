package negotiator.boaframework;

import java.util.HashMap;
import java.util.List;

import misc.Range;
import negotiator.bidding.BidDetails;

/**
 * This is the abstract class for the agents utilization of the opponent model.
 * Given an array of bids, an OmUtilization-object should return a bid which
 * takes the preferences of the opponent into account.
 * 
 * @author Mark Hendrikx
 */
public abstract class OMStrategy {
	
	protected NegotiationSession negotiationSession;
	protected OpponentModel model;
	
	public void init(NegotiationSession negotiationSession, OpponentModel model, HashMap<String, Double> parameters) throws Exception {
		this.negotiationSession = negotiationSession;
		this.model = model;
	}
	
	public void init(NegotiationSession negotiationSession, OpponentModel model) {
		this.negotiationSession = negotiationSession;
		this.model = model;
	}
	
	public abstract BidDetails getBid(List<BidDetails> bidsInRange);
	
	public BidDetails getBid(OutcomeSpace space, Range range) {
		List<BidDetails> bids = space.getBidsinRange(range);
		if (bids.size() == 0) {
			range.increaseUpperbound(0.01);
			getBid(space, range);
		}
		return getBid(bids);
	}
	
	public abstract boolean canUpdateOM();
}