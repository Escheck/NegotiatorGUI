package negotiator.qualitymeasures;

import negotiator.Bid;
import negotiator.BidHistory;
import negotiator.Domain;
import negotiator.Timeline;
import negotiator.bidding.BidDetails;
import negotiator.boaframework.NegotiationSession;
import negotiator.utility.UtilitySpace;

public class NegotiationSessionWrapper extends NegotiationSession {
	
	public NegotiationSessionWrapper(Trace trace, String mainPath) {
		try {
			this.domain = new Domain(mainPath + "/" + trace.getDomain());
			this.utilitySpace = new UtilitySpace(domain, mainPath + "/" + trace.getAgentProfile());
			this.opponentBidHistory = new BidHistory();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	// not required by opponent models
	public BidHistory getOwnBidHistory() {
		return null;
	}
	
	// not required by opponent models
	public double getDiscountFactor() {
		return Double.POSITIVE_INFINITY;
	}
	
	// not required by opponent models
	public Timeline getTimeline() {
		return null;
	}
	
	// not required by opponent models
	public double getTime() {
		return Double.POSITIVE_INFINITY;
	}
	
	// not required by opponent models
	public BidDetails getMaxBidinDomain() {
		return null;
	}
	
	// not required by opponent models
	public BidDetails getMinBidinDomain() {
		return null;
	}	
	
	// not required by opponent models
	public double getDiscountedUtility(Bid bid, double time) {
		return Double.POSITIVE_INFINITY;
	}
}