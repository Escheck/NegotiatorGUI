package negotiator.boaframework.acceptanceconditions.other;

import java.util.HashMap;
import negotiator.boaframework.AcceptanceStrategy;
import negotiator.boaframework.Actions;
import negotiator.boaframework.NegotiationSession;
import negotiator.boaframework.OfferingStrategy;

/**
 * This acceptance condition uses two versions of AC_next.
 * The used AC_next depends on if the discount of the domain is
 * (non)-negligible. The parameter e determines when a domain is
 * marked as discounted or not.
 * 
 * Decoupling Negotiating Agents to Explore the Space of Negotiation Strategies
 * T. Baarslag, K. Hindriks, M. Hendrikx, A. Dirkzwager, C.M. Jonker
 * 
 * @author Alex Dirkzwager, Mark Hendrikx
 */
public class AC_CombiV4 extends AcceptanceStrategy{

	private double a;
	private double b;
	private double c;
	private double d;
	private double e;
	private boolean discountedDomain;
	
public AC_CombiV4() { }

	public AC_CombiV4(NegotiationSession negoSession, OfferingStrategy strat, double a, double b, double c, double d, double e){
		
		this.negotiationSession = negoSession;
		this.offeringStrategy = strat;
		this.a = a;
		this.b = b;
		this.c = c;
		this.d = d;
		this.e = e;
		
		if (negotiationSession.getDiscountFactor() < 0.00001 || negotiationSession.getDiscountFactor() > e) {
			discountedDomain = false;
		}else {
			discountedDomain = true;
		}
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
			if (negotiationSession.getDiscountFactor() < 0.00001 || negotiationSession.getDiscountFactor() > e) {
				discountedDomain = false;
			}else {
				discountedDomain = true;
			}
		} else {
			throw new Exception("Paramaters were not correctly set");
		}
	}
	
	@Override
	public String printParameters() {
		return "[a: " + a + " b: " + b + " c: " + c + " d: " + d + " e: " + e + "]";
	}
	
	@Override
	public Actions determineAcceptability() {
		double nextMyBidUtil = offeringStrategy.getNextBid().getMyUndiscountedUtil();
		double lastOpponentBidUtil = negotiationSession.getOpponentBidHistory().getLastBidDetails().getMyUndiscountedUtil();
		
		double target = 0;
		if (!discountedDomain) {
			// no discount mode
			target = a * lastOpponentBidUtil + b;
		} else {
			// discount mode
			target = c * lastOpponentBidUtil + d;
		}
		if (target > 1.0) {
			target = 1.0;
		}
		
		if (target >= nextMyBidUtil) {
			return Actions.Accept;
		}

		return Actions.Reject;	
	}
}