package negotiator.boaframework.acceptanceconditions;

import java.util.HashMap;
import negotiator.boaframework.AcceptanceStrategy;
import negotiator.boaframework.Actions;
import negotiator.boaframework.NegotiationSession;
import negotiator.boaframework.OfferingStrategy;

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
		if (parameters.get("t") != null) {
			constant = parameters.get("t");
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
		if (negotiationSession.getTime() > constant) {
			return Actions.Accept;
		}
		return Actions.Reject;
	}
}