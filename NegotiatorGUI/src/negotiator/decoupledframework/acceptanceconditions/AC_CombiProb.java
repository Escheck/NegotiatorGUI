package negotiator.decoupledframework.acceptanceconditions;

import java.util.HashMap;
import negotiator.BidHistory;
import negotiator.decoupledframework.AcceptanceStrategy;
import negotiator.decoupledframework.NegotiationSession;
import negotiator.decoupledframework.OfferingStrategy;

/**
 * This is the decoupled Acceptance Conditions Based on Tim Baarslag's paper
 * on Acceptance Conditions:
 * "Acceptance Conditions in Automated Negotiation"
 * 
 * This Acceptance Condition accepts a bid if it is higher than any bid seen
 * so far
 * 
 * @author Alex Dirkzwager
 */
public class AC_CombiProb extends AcceptanceStrategy {
	
	private double time;

	public AC_CombiProb() {
	}

	public AC_CombiProb(NegotiationSession negoSession, OfferingStrategy strat,
			double t) {
		this.negotiationSession = negoSession;
		this.offeringStrategy = strat;
		this.time = t;
	}

	@Override
	public void init(NegotiationSession negoSession, OfferingStrategy strat,
			HashMap<String, Double> parameters) throws Exception {
		this.negotiationSession = negoSession;
		if (parameters.get("t") != null) {
			time = parameters.get("t");
		} else {
			throw new Exception("Paramaters were not correctly set");
		}
	}

	@Override
	public String printParameters() {
		return "[t: " + time + "]";
	}

	@Override
	public boolean determineAcceptability() {
		if (negotiationSession.getOpponentBidHistory().getLastBidDetails()
				.getMyUndiscountedUtil() >= offeringStrategy.getNextBid()
				.getMyUndiscountedUtil()) {
			return true;
		}

		if (negotiationSession.getTime() < time) {
			return false;
		}

		double offeredUndiscountedUtility = negotiationSession
				.getOpponentBidHistory().getLastBidDetails()
				.getMyUndiscountedUtil();
		double now = negotiationSession.getTime();
		double timeLeft = 1 - now;

		// if we will still see a lot of bids
		BidHistory recentBids = negotiationSession.getOpponentBidHistory()
				.filterBetweenTime(now - timeLeft, now);
		int remainingBids = recentBids.size();
		if (remainingBids > 10)
			return false;

		// v2.0
		double window = timeLeft;
		// double window = 5 * timeLeft;
		BidHistory recentBetterBids = negotiationSession.getOpponentBidHistory().
										filterBetween(offeredUndiscountedUtility, 1, now - window, now);
		int n = recentBetterBids.size();
		double p = timeLeft / window;
		if (p > 1)
			p = 1;

		double pAllMiss = Math.pow(1 - p, n);
		if (n == 0)
			pAllMiss = 1;
		double pAtLeastOneHit = 1 - pAllMiss;

		double avg = recentBetterBids.getAverageUtility();

		double expectedUtilOfWaitingForABetterBid = pAtLeastOneHit * avg;

		if (offeredUndiscountedUtility > expectedUtilOfWaitingForABetterBid)
			return true;

		return false;
	}

}
