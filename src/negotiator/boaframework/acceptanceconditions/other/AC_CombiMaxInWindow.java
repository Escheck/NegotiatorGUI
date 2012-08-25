package negotiator.boaframework.acceptanceconditions.other;

import java.util.HashMap;

import negotiator.BidHistory;
import negotiator.boaframework.AcceptanceStrategy;
import negotiator.boaframework.Actions;
import negotiator.boaframework.NegotiationSession;
import negotiator.boaframework.OfferingStrategy;

/**
 * This is the decoupled Acceptance Conditions Based on Tim Baarslag's paper on Acceptance Conditions:
 * "Acceptance Conditions in Automated Negotiation"
 * 
 * This Acceptance Condition accepts a bid if it is higher than any bid seen so far within the previous time window
 * 
 * Decoupling Negotiating Agents to Explore the Space of Negotiation Strategies
 * T. Baarslag, K. Hindriks, M. Hendrikx, A. Dirkzwager, C.M. Jonker
 * 
 * @author Alex Dirkzwager
 */
public class AC_CombiMaxInWindow extends AcceptanceStrategy {
	
	private double time;

	public AC_CombiMaxInWindow() { }

	public AC_CombiMaxInWindow(NegotiationSession negoSession, OfferingStrategy strat, double t){
		this.negotiationSession = negoSession;
		this.offeringStrategy = strat;
		this.time = t;
	}

	@Override
	public void init(NegotiationSession negoSession, OfferingStrategy strat, HashMap<String, Double> parameters) throws Exception {
		this.negotiationSession = negoSession;
		this.offeringStrategy = strat;
		if (parameters.get("t") != null) {
			time = parameters.get("t");
		} else {
			throw new Exception("Parameters were not correctly set");
		}
	}
	
	@Override
	public String printParameters() {
		return "[t: " + time + "]";
	}
	
	
	@Override
	public Actions determineAcceptability() {
		if(negotiationSession.getOpponentBidHistory().getLastBidDetails().getMyUndiscountedUtil() >= offeringStrategy.getNextBid().getMyUndiscountedUtil()) 
			return Actions.Accept;
		
		
		if(negotiationSession.getTime() < time) 
			return Actions.Reject;


		double offeredUndiscountedUtility = negotiationSession.getOpponentBidHistory().getLastBidDetails().getMyUndiscountedUtil();
		double now = negotiationSession.getTime();
		double timeLeft = 1 - now;
		
		// v2.0
		double window = timeLeft;
		BidHistory recentBids = negotiationSession.getOpponentBidHistory().filterBetweenTime(now - window, now);

		double max;
		if (recentBids.size() > 0)
			max = recentBids.getBestBidDetails().getMyUndiscountedUtil();
		else
			max = 0;

		// max = 0 als n = 0
		double expectedUtilOfWaitingForABetterBid = max;

		if (offeredUndiscountedUtility >= expectedUtilOfWaitingForABetterBid) 
			return Actions.Accept;
		
		return Actions.Reject;
	}
	

}
