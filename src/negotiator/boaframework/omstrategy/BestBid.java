package negotiator.boaframework.omstrategy;

import java.util.HashMap;
import java.util.List;
import java.util.Random;
import negotiator.bidding.BidDetails;
import negotiator.boaframework.NegotiationSession;
import negotiator.boaframework.OMStrategy;
import negotiator.boaframework.OpponentModel;

/**
 * This class uses an opponent model to determine the next bid for the opponent, while taking
 * the opponent's preferences into account. The opponent model is used to select the best bid.

 * @author Mark Hendrikx
 */
public class BestBid extends OMStrategy {

	/**  when to stop updating the opponentmodel */
	double updateThreshold = 1.1;
	
	/**
	 * Empty constructor used for reflexion. Note this constructor assumes that init
	 * is called next.
	 */
	public BestBid() { }

	public BestBid(NegotiationSession negotiationSession, OpponentModel model) {
		super.init(negotiationSession, model);
	}
	
	/**
	 * Initializes the opponent model strategy. If a value for the paramter t is given, then
	 * it is set to this value. Otherwise, the default value is used.
	 */
	public void init(NegotiationSession negotiationSession, OpponentModel model, HashMap<String, Double> parameters) throws Exception {
		super.init(negotiationSession, model);
		if (parameters.get("t") != null) {
			updateThreshold = parameters.get("t").doubleValue();
		} else {
			System.out.println("OMStrategy assumed t = 1.0");
		}
	}
	
	/**
	 * Returns the best bid for the opponent given a set of similarly
	 * preferred bids.
	 */
	@Override
	public BidDetails getBid(List<BidDetails> allBids) {
		
		// 1. If there is only a single bid, return this bid
		if (allBids.size() == 1) {
			return allBids.get(0);
		}
		double bestUtil = -1;
		BidDetails bestBid = allBids.get(0);
		
		// 2. Check that not all bids are assigned at utility of 0
		// to ensure that the opponent model works. If the opponent model
		// does not work, offer a random bid.
		boolean allWereZero = true;
		// 3. Determine the best bid
		for (BidDetails bid : allBids) {
			double evaluation = model.getBidEvaluation(bid.getBid());
			if (evaluation > 0.0001) {
				allWereZero = false;
			}
			if (evaluation > bestUtil) {
				bestBid = bid;
				bestUtil = evaluation;
			}
		}
		// 4. The opponent model did not work, therefore, offer a random bid.
		if (allWereZero) {
			Random r = new Random();
			return allBids.get(r.nextInt(allBids.size()));
		}
		return bestBid;
	}

	/**
	 * The opponent model may be updated, unless the time is higher
	 * than a given constant.
	 */
	@Override
	public boolean canUpdateOM() {
		return negotiationSession.getTime() < updateThreshold;
	}
}