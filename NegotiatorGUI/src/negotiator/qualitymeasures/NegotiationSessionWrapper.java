package negotiator.qualitymeasures;

import negotiator.Bid;
import negotiator.BidHistory;
import negotiator.Domain;
import negotiator.bidding.BidDetails;
import negotiator.boaframework.NegotiationSession;
import negotiator.session.Timeline;
import negotiator.utility.UtilitySpace;

/**
 * Converts a stored negotiation trace of the opponent to a negotiation session.
 * 
 * @author Mark Hendrikx
 */
public class NegotiationSessionWrapper extends NegotiationSession {
	
	/**
	 * Given a trace, and the path to the file where to load the utility profile,
	 * construct a negotiation session.
	 * 
	 * @param trace of the opponent
	 * @param mainPath to the utility space
	 */
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