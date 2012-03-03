package negotiator.decoupledframework.acceptanceconditions;

import java.util.HashMap;

import negotiator.decoupledframework.AcceptanceStrategy;
import negotiator.decoupledframework.NegotiationSession;
import negotiator.decoupledframework.OfferingStrategy;

/**
 * Acceptance condition which accepts when a bid has a higher utility
 * than a target utility calculated using a linear function of the utility
 * to be offered next.
 * 
 * Two linear functions are used, a function for when the negotiation has
 * a strong discount, and a function for all other cases.
 *
 * @author Alex Dirkzwager, Mark Hendrikx
 */
public class AC_CombiV4 extends AcceptanceStrategy{

	private double a;
	private double b;
	private double c;
	private double d;
	private double e;
	
public AC_CombiV4() { }
	
	@Override
	public void init(NegotiationSession negoSession, OfferingStrategy strat, HashMap<String, Double> parameters) throws Exception {
		this.negotiationSession = negoSession;
		this.offeringStrategy = strat;
		if (parameters.get("a") != null  || parameters.get("b")!=null || parameters.get("c") != null 
				&& parameters.get("t") != null && parameters.get("d") != null && parameters.get("e") != null){
			a = parameters.get("a");
			b = parameters.get("b");
			c = parameters.get("c");
			d = parameters.get("d");
			e = parameters.get("e");
		} else {
			throw new Exception("Paramaters were not correctly set");
		}
	}
	
	@Override
	public String printParameters() {
		return "[a: " + a + " b: " + b + " c: " + c + " d: " + d + " e: " + e + "]";
	}
	
	@Override
	public boolean determineAcceptability() {
		double nextMyBidUtil = offeringStrategy.getNextBid().getMyUndiscountedUtil();
		double lastOpponentBidUtil = negotiationSession.getOpponentBidHistory().getLastBidDetails().getMyUndiscountedUtil();

		double target = 0;
		if (negotiationSession.getDiscountFactor() < 0.00001 || negotiationSession.getDiscountFactor() > e) {
			// no discount mode
			target = a * nextMyBidUtil + b;
		} else {
			// discount mode
			target = c * nextMyBidUtil + d;
		}
		if (target > 1.0) {
			target = 1.0;
		}
		
		if (lastOpponentBidUtil >= target) {
			return true;
		}

		return false;		
	}
}