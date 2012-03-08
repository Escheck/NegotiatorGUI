package negotiator.decoupledframework.acceptanceconditions;

import java.util.HashMap;

import negotiator.decoupledframework.AcceptanceStrategy;
import negotiator.decoupledframework.NegotiationSession;
import negotiator.decoupledframework.OfferingStrategy;

public class AC_CombiV4 extends AcceptanceStrategy{

	private double a;
	private double b;
	private double c;
	private double d;
	private double e;
	private double highestUtilReceived = 0;
	
public AC_CombiV4() { }

	public AC_CombiV4(NegotiationSession negoSession, OfferingStrategy strat, double a, double b, double c, double d, double e){
		this.negotiationSession = negoSession;
		this.offeringStrategy = strat;
		this.a = a;
		this.b = b;
		this.c = c;
		this.d = d;
		this.e = e;
	}
	
	@Override
	public void init(NegotiationSession negoSession, OfferingStrategy strat, HashMap<String, Double> parameters) throws Exception {
		this.negotiationSession = negoSession;
		this.offeringStrategy = strat;
		if (parameters.get("a") != null  || parameters.get("b")!=null || parameters.get("c") != null
				&& parameters.get("d") != null && parameters.get("e") != null){
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

		boolean bestYet = false;
		if (lastOpponentBidUtil >= highestUtilReceived) {
			highestUtilReceived = lastOpponentBidUtil;
			bestYet = true;
		}
		
		double target = 0;
		if (negotiationSession.getDiscountFactor() < 0.00001 || negotiationSession.getDiscountFactor() > e) {
			// no discount mode
			target = a * nextMyBidUtil + b;
		} else {
			// discount mode
			target = c * nextMyBidUtil + d;
			//System.out.println(negotiationSession.getTime() + " " + "Target: " + target);
		}
		if (target > 1.0) {
			target = 1.0;
		}
		
		if (bestYet && lastOpponentBidUtil >= target) {
			return true;
		}

		return false;		
	}
}