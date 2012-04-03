package negotiator.decoupledframework.omstrategy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import negotiator.Bid;
import negotiator.bidding.BidDetails;
import negotiator.bidding.BidDetailsSorterUtility;
import negotiator.decoupledframework.NegotiationSession;
import negotiator.decoupledframework.OMStrategy;
import negotiator.decoupledframework.OpponentModel;

/**
 * This class uses an opponent model to determine the next bid for the opponent, while taking
 * the opponent's preferences into account. The opponent model is used to select the N best
 * bids. Following, a random bid is selected from this subset. Setting N > 1 is rational,
 * as opponent models cannot be assumed to be perfect.
 * 
 * @author Mark Hendrikx
 */
public class OfferBestN extends OMStrategy {

	private Random rand;
	// parameter which determines which n best bids should be considered
	private int bestN;
	// used to sort the opponent's bid with regard to utility
	private static BidDetailsSorterUtility comp = new BidDetailsSorterUtility();
	
	public OfferBestN() { }

	public OfferBestN(NegotiationSession negotiationSession, OpponentModel model, int n) {
		initializeAgent(negotiationSession, model, n);
	}
	
	public void init(NegotiationSession negotiationSession, OpponentModel model, HashMap<String, Double> parameters) throws Exception {
		this.negotiationSession = negotiationSession;
		this.model = model;
		if (parameters.get("n") != null) {
			initializeAgent(negotiationSession, model, parameters.get("n").intValue());
		} else {
			throw new Exception("Constant \"n\" for amount of best bids was not set.");
		}
	}
	
	private void initializeAgent(NegotiationSession negotiationSession, OpponentModel model, int n) {
		super.init(negotiationSession, model);
		this.rand = new Random();
		this.bestN = n;
	}
	
	/**
	 * Returns the (likely) best bid for the opponent given a set of more-or-less
	 * equally prefered bids.
	 */
	@Override
	public BidDetails getBid(List<BidDetails> allBids) {
		// determine the utility for the opponent for each of the bids
		ArrayList<BidDetails> oppBids = new ArrayList<BidDetails>(allBids.size());
		for (BidDetails bidDetail : allBids) {
			Bid bid = bidDetail.getBid();
			BidDetails newBid = new BidDetails(bid, model.getBidEvaluation(bid));
			oppBids.add(newBid);
		}
		
		// sort the bids on the utility for the opponent
		Collections.sort(oppBids, comp);
		
		// select a random bid from the N best bids and offer this bid
		int entry = rand.nextInt(Math.min(bestN, oppBids.size()));
		Bid opponentBestBid = oppBids.get(entry).getBid();
		BidDetails nextBid = null;
		try {
			nextBid = new BidDetails(opponentBestBid, negotiationSession.getUtilitySpace().getUtility(opponentBestBid));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return nextBid;
	}
}