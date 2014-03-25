package negotiator.boaframework.acceptanceconditions.other;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import negotiator.boaframework.AcceptanceStrategy;
import negotiator.boaframework.Actions;
import negotiator.boaframework.BOAparameter;
import negotiator.boaframework.NegotiationSession;
import negotiator.boaframework.OfferingStrategy;
import negotiator.boaframework.OpponentModel;

/**
 * This Acceptance Condition accept an opponent bid after a certain time has
 * passed
 * 
 * Decoupling Negotiating Agents to Explore the Space of Negotiation Strategies
 * T. Baarslag, K. Hindriks, M. Hendrikx, A. Dirkzwager, C.M. Jonker
 * 
 * @author Alex Dirkzwager, Mark Hendrikx
 * @version 15-12-11
 */
public class AC_Time extends AcceptanceStrategy {

	private double constant;

	/**
	 * Empty constructor for the BOA framework.
	 */
	public AC_Time() {
	}

	public AC_Time(NegotiationSession negoSession, double c) {
		this.negotiationSession = negoSession;
		this.constant = c;
	}

	@Override
	public void init(NegotiationSession negoSession, OfferingStrategy strat,
			OpponentModel opponentModel, HashMap<String, Double> parameters)
			throws Exception {
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

	@Override
	public Set<BOAparameter> getParameters() {

		Set<BOAparameter> set = new HashSet<BOAparameter>();
		set.add(new BOAparameter("t", new BigDecimal(0.99),
				"If time greater than t, then accept"));

		return set;
	}
}