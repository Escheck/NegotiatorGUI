package negotiator.boaframework.acceptanceconditions.other;

import java.util.HashMap;
import negotiator.boaframework.AcceptanceStrategy;
import negotiator.boaframework.Actions;
import negotiator.boaframework.NegotiationSession;
import negotiator.boaframework.OfferingStrategy;

/**
 * This is the decoupled Acceptance Conditions Based on Tim Baarslag's paper on Acceptance Conditions:
 * "Acceptance Conditions in Automated Negotiation"
 * 
 * This Acceptance Condition accepts a bid if it is higher than any bid seen so far
 * 
 * @author Alex Dirkzwager
 */
public class AC_CombiMax extends AcceptanceStrategy {

	public AC_CombiMax() { }
	
	public AC_CombiMax(NegotiationSession negoSession, OfferingStrategy strat) {
		this.negotiationSession = negoSession;
		this.offeringStrategy = strat;
	}
	
	public void init(NegotiationSession negoSession, OfferingStrategy strat, HashMap<String, Double> parameters) throws Exception {
		this.negotiationSession = negoSession;
	}
	
	@Override
	public Actions determineAcceptability() {
		if(negotiationSession.getOpponentBidHistory().getLastBidDetails().getMyUndiscountedUtil() >= offeringStrategy.getNextBid().getMyUndiscountedUtil()) {
			return Actions.Accept;
		}
		
		if(negotiationSession.getTime() < 0.99) {
			return Actions.Reject;
		}
		
		double offeredUndiscountedUtility = negotiationSession.getOpponentBidHistory().getLastBidDetails().getMyUndiscountedUtil();
		double bestUtil = negotiationSession.getOpponentBidHistory().getBestBidDetails().getMyUndiscountedUtil();
		
		if (offeredUndiscountedUtility >= bestUtil)
			return Actions.Accept;
		
		return Actions.Reject;
	}
}
