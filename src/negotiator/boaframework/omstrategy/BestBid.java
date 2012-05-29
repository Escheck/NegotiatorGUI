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
 * 
 * @author Mark Hendrikx
 */
public class BestBid extends OMStrategy {

	/**  when to stop updating */
	double updateThreshold = 1.0;
	
	/**
	 * Empty constructor used for reflexion. Note this constructor assumes that init
	 * is called next.
	 */
	public BestBid() { }

	public BestBid(NegotiationSession negotiationSession, OpponentModel model) {
		initializeAgent(negotiationSession, model);
	}
	
	public void init(NegotiationSession negotiationSession, OpponentModel model, HashMap<String, Double> parameters) throws Exception {
		initializeAgent(negotiationSession, model);
		if (parameters.get("t") != null) {
			updateThreshold = parameters.get("t").doubleValue();
		} else {
			System.out.println("OMStrategy assumed t = 1.0");
		}
	}
	
	private void initializeAgent(NegotiationSession negotiationSession, OpponentModel model) {
		super.init(negotiationSession, model);
	}
	
	/**
	 * Returns the best bid for the opponent given a set of more-or-less
	 * equally prefered bids.
	 */
	@Override
	public BidDetails getBid(List<BidDetails> allBids) {
		
		// determine the utility for the opponent for each of the bids
		if (allBids.size() == 1) {
			return allBids.get(0);
		}
		double bestUtil = -1;
		BidDetails bestBid = allBids.get(0);
		
		boolean allWereZero = true;
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