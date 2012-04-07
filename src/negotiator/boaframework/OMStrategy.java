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
public abstract class OMStrategy implements Cloneable {
	
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
		return getBid(bids);
	}
	
	public OMStrategy clone() {
		try {
			OMStrategy clone = (OMStrategy) super.clone();
			clone.negotiationSession = this.negotiationSession;
			clone.model = this.model;
			return clone;
		} catch (CloneNotSupportedException e) {
			throw new RuntimeException("this could never happen", e);
		}
	}
	
	public OMStrategy reset() {
		model = null;
		negotiationSession = null;
		return this;
	}
}