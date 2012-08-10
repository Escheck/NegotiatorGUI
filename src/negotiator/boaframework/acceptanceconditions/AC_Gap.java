package negotiator.boaframework.acceptanceconditions;

import java.util.HashMap;
import negotiator.boaframework.AcceptanceStrategy;
import negotiator.boaframework.Actions;
import negotiator.boaframework.NegotiationSession;
import negotiator.boaframework.OfferingStrategy;

/**
 * This is the decoupled Acceptance Conditions Based on Tim Baarslag's paper
 * on Acceptance Conditions:
 * "Acceptance Conditions in Automated Negotiation"
 * 
 * This Acceptance Condition accepts a bid if the utility of the opponent's
 * bid plus a constant (the gap) is higher than the utility of the next bid.
 * The acceptance condition is a restricted version of AC_next.
 * 
 * @author Alex Dirkzwager
 */
public class AC_Gap extends AcceptanceStrategy{

	private double constant;
	
	public AC_Gap() { }
	
	public AC_Gap(NegotiationSession negoSession, double c){
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
		if(!negotiationSession.getOwnBidHistory().getHistory().isEmpty()) {
			double opponentBidUtil = negotiationSession.getOpponentBidHistory().getLastBidDetails().getMyUndiscountedUtil();
			double prevMyBidUtil = negotiationSession.getOwnBidHistory().getLastBidDetails().getMyUndiscountedUtil();

			if (opponentBidUtil + constant >= prevMyBidUtil) {
				return Actions.Accept;
			}
		}
		return Actions.Reject;
	}
}