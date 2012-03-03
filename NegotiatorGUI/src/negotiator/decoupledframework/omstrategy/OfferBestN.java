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

public class OfferBestN extends OMStrategy {

	private Random rand;
	private int bestN;
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
	
	// THIS COULD BE IMPLEMENTED A LOT FASTER :!
	@Override
	public BidDetails getBid(List<BidDetails> allBids) {
		ArrayList<BidDetails> oppBids = new ArrayList<BidDetails>(allBids.size());
		for (BidDetails bidDetail : allBids) {
			Bid bid = bidDetail.getBid();
			BidDetails newBid = new BidDetails(bid, model.getBidEvaluation(bid));
			oppBids.add(newBid);
		}
		Collections.sort(oppBids, comp);
		int entry = rand.nextInt(Math.min(bestN, oppBids.size()));

		return oppBids.get(entry);
	}
}