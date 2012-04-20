package negotiator.boaframework.acceptanceconditions;

import java.util.HashMap;
import negotiator.bidding.BidDetails;
import negotiator.boaframework.AcceptanceStrategy;
import negotiator.boaframework.Actions;
import negotiator.boaframework.NegotiationSession;
import negotiator.boaframework.OfferingStrategy;

/**
 * This Acceptance Condition accepts an opponent bid if the utility is above a constant.
 * 
 * @author Alex Dirkzwager, Mark Hendrikx
 * @version 15-12-11
 */
public class AC_Const extends AcceptanceStrategy {

	private double constant;
	
	public AC_Const() { }
	
	public AC_Const(NegotiationSession negoSession, double c){
		this.negotiationSession = negoSession;
		this.constant = c;
	}

	@Override
	public void init(NegotiationSession negoSession, OfferingStrategy strat, HashMap<String, Double> parameters) throws Exception {
		this.negotiationSession = negoSession;
		if (parameters.get("c") != null) {
			constant = parameters.get("c");
		} else {
			throw new Exception("Constant \"c\" for the threshold was not set.");
		}
	}
	
	@Override
	public String printParameters() {
		return "[c: " + constant + "]";
	}
	
	@Override
	public Actions determineAcceptability() {
		BidDetails opponentBid = negotiationSession.getOpponentBidHistory().getLastBidDetails();
		if (opponentBid.getMyUndiscountedUtil() > constant) {
			return Actions.Accept;
		}
		return Actions.Reject;
	}
}