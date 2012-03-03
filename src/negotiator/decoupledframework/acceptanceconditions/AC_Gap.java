package negotiator.decoupledframework.acceptanceconditions;

import java.util.HashMap;
import negotiator.decoupledframework.AcceptanceStrategy;
import negotiator.decoupledframework.NegotiationSession;
import negotiator.decoupledframework.OfferingStrategy;

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
	public boolean determineAcceptability() {
		if(!negotiationSession.getOwnBidHistory().getHistory().isEmpty()) {
			double opponentBidUtil = negotiationSession.getOpponentBidHistory().getLastBidDetails().getMyUndiscountedUtil();
			double ownLastBidUtil = negotiationSession.getOwnBidHistory().getLastBidDetails().getMyUndiscountedUtil();
			return opponentBidUtil + constant >= ownLastBidUtil;
		}else {
			return false;
		}
	}
}