package negotiator.boaframework;

import java.util.HashMap;
import java.util.List;
import misc.Range;
import negotiator.bidding.BidDetails;

/**
 * This is the abstract class which determines when the opponent model
 * may be updated, and how it used to select a bid for the opponent.
 * 
 * Tim Baarslag, Koen Hindriks, Mark Hendrikx, Alex Dirkzwager and Catholijn M. Jonker.
 * Decoupling Negotiating Agents to Explore the Space of Negotiation Strategies
 * 
 * @author Mark Hendrikx
 */
public abstract class OMStrategy {
	
	/** Reference to the object which holds all information about the negotiation */
	protected NegotiationSession negotiationSession;
	/** Reference to the opponent model */
	protected OpponentModel model;
	/** Increment used to increase the upperbound in case no bid is found in the range */
	private final double RANGE_INCREMENT = 0.01;
	/** Amount of bids expected in window */
	private final int EXPECTED_BIDS_IN_WINDOW = 100;
	/** Intitial range which is being searched for similarly preferable bids.
	 *  This range is increased with the RANGE_INCREMENT until EXPECTED_BIDS_IN_WINDOW
	 *  are found or the window is of maximum size */
	private final double INITIAL_WINDOW_RANGE = 0.01;
	
	public void init(NegotiationSession negotiationSession, OpponentModel model, HashMap<String, Double> parameters) throws Exception {
		initializeAgent(negotiationSession, model);
	}
	
	public void init(NegotiationSession negotiationSession, OpponentModel model) {
		initializeAgent(negotiationSession, model);
	}
	
	private void initializeAgent(NegotiationSession negotiationSession,
			OpponentModel model) {
		this.negotiationSession = negotiationSession;
		this.model = model;
	}

	/**
	 * Returns a bid selected using the opponent model from the given
	 * set of similarly preferred bids.
	 * 
	 * @param bidsInRange set of similarly preferred bids
	 * @return bid
	 */
	public abstract BidDetails getBid(List<BidDetails> bidsInRange);
	
	/**
	 * Returns a bid selected using the opponent model with a utility
	 * in the given range.
	 * 
	 * @param space of all possible outcomes
	 * @param range of utility
	 * @return bid
	 */
	public BidDetails getBid(OutcomeSpace space, Range range) {
		List<BidDetails> bids = space.getBidsinRange(range);
		if (bids.size() == 0) {
			if (range.getUpperbound() < 1.01) {
				range.increaseUpperbound(RANGE_INCREMENT);
				return getBid(space, range);
			} else {
				negotiationSession.setOutcomeSpace(space);
				return negotiationSession.getMaxBidinDomain();
			}
		}
		return getBid(bids);
	}

	/**
	 * Use this method in case no range is specified, but only a target utility.
	 * 
	 * @param space of all possible outcomes
	 * @param range of utility
	 * @return bid
	 */
	public BidDetails getBid(SortedOutcomeSpace space, double targetUtility) {
		Range range = new Range(targetUtility, targetUtility + INITIAL_WINDOW_RANGE);
		List<BidDetails> bids = space.getBidsinRange(range);
		if (bids.size() < EXPECTED_BIDS_IN_WINDOW) {
			if (range.getUpperbound() < 1.01) {
				range.increaseUpperbound(RANGE_INCREMENT);
				return getBid(space, range);
			} else {
				// futher increasing the window does not help
				return getBid(bids);
			}
		}
		return getBid(bids);
	}
	
	/**
	 * @return if given the negotiation state the opponent model may be updated
	 */
	public abstract boolean canUpdateOM();
}