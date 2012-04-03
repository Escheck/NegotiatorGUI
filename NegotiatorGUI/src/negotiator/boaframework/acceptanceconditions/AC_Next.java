package negotiator.decoupledframework.acceptanceconditions;

import java.util.HashMap;
import negotiator.decoupledframework.AcceptanceStrategy;
import negotiator.decoupledframework.Actions;
import negotiator.decoupledframework.NegotiationSession;
import negotiator.decoupledframework.OfferingStrategy;

/**
 * This Acceptance Condition will accept an opponent bid if the utility is higher than the 
 * bid the agent is ready to present
 * 
 * @author Alex Dirkzwager, Mark Hendrikx
 * @version 18/12/11
 */
public class AC_Next extends AcceptanceStrategy {
	
	private double a;
	private double b;

	public AC_Next() { }
	
	public AC_Next(NegotiationSession negoSession, OfferingStrategy strat, double alpha, double beta){
		this.negotiationSession = negoSession;
		this.offeringStrategy = strat;
		this.a =  alpha;
		this.b = beta;
	}

	@Override
	public void init(NegotiationSession negoSession, OfferingStrategy strat, HashMap<String, Double> parameters) throws Exception {
		this.negotiationSession = negoSession;
		this.offeringStrategy = strat;

		if (parameters.get("a") != null || parameters.get("b") !=null) {
			a = parameters.get("a");
			b = parameters.get("b");
		} else {
			throw new Exception("Parameters were not set.");
		}
	}
	
	@Override
	public String printParameters() {
		String str = "[a: " + a + " b: " + b + "]";
		return str;
	}

	@Override
	public Actions determineAcceptability() {
		double nextMyBidUtil = offeringStrategy.getNextBid().getMyUndiscountedUtil();
		double lastOpponentBidUtil = negotiationSession.getOpponentBidHistory().getLastBidDetails().getMyUndiscountedUtil();
		if (a * lastOpponentBidUtil + b >= nextMyBidUtil) {
			return Actions.Accept;
		}
		return Actions.Reject;
	}
}