package negotiator.boaframework.acceptanceconditions.other;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import negotiator.bidding.BidDetails;
import negotiator.boaframework.AcceptanceStrategy;
import negotiator.boaframework.Actions;
import negotiator.boaframework.BOAparameter;
import negotiator.boaframework.NegotiationSession;
import negotiator.boaframework.OfferingStrategy;
import negotiator.boaframework.OpponentModel;

/**
 * This Acceptance Condition accepts an opponent bid if the utility is above a
 * constant.
 * 
 * Decoupling Negotiating Agents to Explore the Space of Negotiation Strategies
 * T. Baarslag, K. Hindriks, M. Hendrikx, A. Dirkzwager, C.M. Jonker
 * 
 * @author Alex Dirkzwager, Mark Hendrikx
 * @version 15-12-11
 */
public class AC_Const extends AcceptanceStrategy {

	private double constant;

	/**
	 * Empty constructor for the BOA framework.
	 */
	public AC_Const() {
	}

	public AC_Const(NegotiationSession negoSession, double c) {
		this.negotiationSession = negoSession;
		this.constant = c;
	}

	@Override
	public void init(NegotiationSession negoSession, OfferingStrategy strat,
			OpponentModel opponentModel, HashMap<String, Double> parameters)
			throws Exception {
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
	public Actions determineAcceptability() {
		BidDetails opponentBid = negotiationSession.getOpponentBidHistory()
				.getLastBidDetails();
		if (opponentBid.getMyUndiscountedUtil() > constant) {
			return Actions.Accept;
		}
		return Actions.Reject;
	}

	@Override
	public Set<BOAparameter> getParameters() {

		Set<BOAparameter> set = new HashSet<BOAparameter>();
		set.add(new BOAparameter("c", new BigDecimal(0.9),
				"If utility of opponent's bid greater than c, then accept"));

		return set;
	}
}