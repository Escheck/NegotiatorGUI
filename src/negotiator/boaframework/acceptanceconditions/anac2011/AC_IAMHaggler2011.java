package negotiator.boaframework.acceptanceconditions.anac2011;

import java.util.HashMap;

import negotiator.boaframework.AcceptanceStrategy;
import negotiator.boaframework.Actions;
import negotiator.boaframework.NegotiationSession;
import negotiator.boaframework.OfferingStrategy;

/**
 * This is the decoupled Acceptance Conditions for IAMHaggler (ANAC2011).
 * The code was taken from the ANAC2011 IAMHaggler and adapted to work within the Decoupledframework.
 * 
 * @author Alex Dirkzwager, Mark Hendrikx
 * @version 18/12/11
 */

public class AC_IAMHaggler2011 extends AcceptanceStrategy {
	
	private double maximum_aspiration = 0.9;
	private final double acceptMultiplier = 1.02;
	
	public AC_IAMHaggler2011() { }
	
	public AC_IAMHaggler2011(NegotiationSession negoSession,
			OfferingStrategy strat) throws Exception {
		init(negoSession, strat, null);
	}

	public void init(NegotiationSession negoSession, OfferingStrategy strat, HashMap<String, Double> parameters) throws Exception {
		this.negotiationSession = negoSession;
		offeringStrategy = strat;
	}
	
	@Override
	public Actions determineAcceptability() {
		if (negotiationSession.getOpponentBidHistory() != null && negotiationSession.getOwnBidHistory().getLastBidDetails() != null) {
			double lastOpponentBidUtil = negotiationSession.getOpponentBidHistory().getLastBidDetails().getMyUndiscountedUtil();
			double lastMyBidUtil = negotiationSession.getOwnBidHistory().getLastBidDetails().getMyUndiscountedUtil();

			if (	lastOpponentBidUtil * acceptMultiplier > lastMyBidUtil ||
					lastOpponentBidUtil * acceptMultiplier > offeringStrategy.getNextBid().getMyUndiscountedUtil() ||
					lastOpponentBidUtil * acceptMultiplier >  maximum_aspiration) {
				return Actions.Accept;
			}
		}
		return Actions.Reject;
	}
}
