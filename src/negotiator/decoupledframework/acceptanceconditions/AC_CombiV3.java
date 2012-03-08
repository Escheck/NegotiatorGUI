package negotiator.decoupledframework.acceptanceconditions;

import java.util.HashMap;

import negotiator.decoupledframework.AcceptanceStrategy;
import negotiator.decoupledframework.NegotiationSession;
import negotiator.decoupledframework.OfferingStrategy;

public class AC_CombiV3 extends AcceptanceStrategy{

	private double a;
	private double b;
	private double c;
	private double time;
	
public AC_CombiV3() { }
	
	public AC_CombiV3(NegotiationSession negoSession, OfferingStrategy strat, double a, double b, double t, double c){
		this.negotiationSession = negoSession;
		this.offeringStrategy = strat;
		this.a = a;
		this.b = b;
		this.c = c;
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
			time = parameters.get("t");
		} else {
			throw new Exception("Paramaters were not correctly set");
		}
	}
	
	@Override
	public String printParameters() {
		return "[a: " + a + " b: " + b + " t: " + time + " c: " + c + "]";
	}
	
	@Override
	public boolean determineAcceptability() {
		double nextMyBidUtil = offeringStrategy.getNextBid().getMyUndiscountedUtil();
		double lastOpponentBidUtil = negotiationSession.getOpponentBidHistory().getLastBidDetails().getMyUndiscountedUtil();

		double target = a * nextMyBidUtil + b;
		if (target > 1.0) {
			target = 1.0;
		}
		if (lastOpponentBidUtil >= target) {
			return true;
		}

		return negotiationSession.getTime() > time && lastOpponentBidUtil > c;			
	}

}
