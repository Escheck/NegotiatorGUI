package negotiator.decoupledframework.acceptanceconditions;

import java.util.HashMap;
import negotiator.decoupledframework.AcceptanceStrategy;
import negotiator.decoupledframework.NegotiationSession;
import negotiator.decoupledframework.OfferingStrategy;

/**
 * This Acceptance Condition accept an opponent bid after a certain time has passed
 * 
 * @author Alex Dirkzwager, Mark Hendrikx
 * @version 15-12-11
 */

public class AC_Time extends AcceptanceStrategy {

	private double constant;
	
	public AC_Time() { }
	
	public AC_Time(NegotiationSession negoSession, double c){
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
		return negotiationSession.getTime() > constant;
	}
	
	@Override
	public AC_Time clone() {
		AC_Time clone = (AC_Time) super.clone();
		clone.constant = constant;
		return clone;
	}
}