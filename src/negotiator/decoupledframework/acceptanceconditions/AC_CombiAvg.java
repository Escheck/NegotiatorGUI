package negotiator.decoupledframework.acceptanceconditions;

import java.util.HashMap;
import negotiator.BidHistory;
import negotiator.decoupledframework.AcceptanceStrategy;
import negotiator.decoupledframework.NegotiationSession;
import negotiator.decoupledframework.OfferingStrategy;

/**
 * This is the decoupled Acceptance Conditions Based on Tim Baarslag's paper on Acceptance Conditions:
 * "Acceptance Conditions in Automated Negotiation"
 * 
 * This Acceptance Condition averages the opponents bids made in the previous time window. 
 * If the bid is higher than the average it will accept
 * 
 * @author Alex Dirkzwager
 */
public class AC_CombiAvg extends AcceptanceStrategy {

	private double time;
	
	public AC_CombiAvg() { }
	
	public AC_CombiAvg(NegotiationSession negoSession, OfferingStrategy strat, double t){
		this.negotiationSession = negoSession;
		this.offeringStrategy = strat;
		this.time = t;
	}
	
	@Override
	public void init(NegotiationSession negoSession, OfferingStrategy strat, HashMap<String, Double> parameters) throws Exception {
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
		if(negotiationSession.getOpponentBidHistory().getLastBidDetails().getMyUndiscountedUtil() >= offeringStrategy.getNextBid().getMyUndiscountedUtil()) {
			return true;
		}
		
		if(negotiationSession.getTime() < time) {
			return false;
		}
		
		double offeredUndiscountedUtility = negotiationSession.getOpponentBidHistory().getLastBidDetails().getMyUndiscountedUtil();
		double now = negotiationSession.getTime();
		double timeLeft = 1 - now;
		
		double window = timeLeft;
		BidHistory recentBids = negotiationSession.getOpponentBidHistory().filterBetweenTime(now - window, now);

		double avgOfBetterBids = recentBids.getAverageUtility();
		double expectedUtilOfWaitingForABetterBid = avgOfBetterBids;

		if (offeredUndiscountedUtility >= expectedUtilOfWaitingForABetterBid)
			return true;
		return false;
	}
}