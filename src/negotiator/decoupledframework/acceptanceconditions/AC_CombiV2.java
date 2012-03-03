package negotiator.decoupledframework.acceptanceconditions;

import java.util.HashMap;

import negotiator.decoupledframework.AcceptanceStrategy;
import negotiator.decoupledframework.NegotiationSession;
import negotiator.decoupledframework.OfferingStrategy;

/**
 * This is the decoupled Acceptance Conditions Based on Tim Baarslag's paper on Acceptance Conditions:
 * "Acceptance Conditions in Automated Negotiation"
 * 
 * This Acceptance Conditions is a combination of AC_Time and AC_Next
 * 
 * @author Alex Dirkzwager
 */
public class AC_CombiV2 extends AcceptanceStrategy {

	private double a;
	private double b;
	private double c;
	private double d;
	private double time;
	
	public AC_CombiV2() { }
	
	public AC_CombiV2(NegotiationSession negoSession, OfferingStrategy strat, double a, double b, double t, double c, double d){
		this.negotiationSession = negoSession;
		this.offeringStrategy = strat;
		this.a = a;
		this.b = b;
		this.c = c;
		this.d = d;
		this.time = t;
	}
	
	@Override
	public void init(NegotiationSession negoSession, OfferingStrategy strat, HashMap<String, Double> parameters) throws Exception {
		this.negotiationSession = negoSession;
		this.offeringStrategy = strat;
		if (parameters.get("a") != null  || parameters.get("b")!=null || parameters.get("c") != null  && parameters.get("t")!=null){
			a = parameters.get("a");
			b = parameters.get("b");
			c = parameters.get("c");
			d = parameters.get("d");
			time = parameters.get("t");
		} else {
			throw new Exception("Paramaters were not correctly set");
		}
	}
	
	@Override
	public String printParameters() {
		return "[a: " + a + " b: " + b + " t: " + time + " c: " + c + " d: " + d + "]";
	}
	
	@Override
	public boolean determineAcceptability() {
		double nextMyBidUtil = offeringStrategy.getNextBid().getMyUndiscountedUtil();
		double lastOpponentBidUtil = negotiationSession.getOpponentBidHistory().getLastBidDetails().getMyUndiscountedUtil();

		if (lastOpponentBidUtil > d) {
			return true;
		}
		
		if(negotiationSession.getDiscountFactor() != 0.0 && c < negotiationSession.getDiscountFactor()) {
			return a * lastOpponentBidUtil + b >= nextMyBidUtil;
		} else {
			return a * lastOpponentBidUtil + b >= nextMyBidUtil && negotiationSession.getTime() >= time;
		}
	}
}
