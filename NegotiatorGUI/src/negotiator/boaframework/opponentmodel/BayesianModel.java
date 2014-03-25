package negotiator.boaframework.opponentmodel;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import negotiator.Bid;
import negotiator.boaframework.BOAparameter;
import negotiator.boaframework.NegotiationSession;
import negotiator.boaframework.OpponentModel;
import negotiator.issue.Issue;
import negotiator.issue.ValueDiscrete;
import negotiator.utility.UtilitySpace;
import agents.bayesianopponentmodel.BayesianOpponentModel;
import agents.bayesianopponentmodel.OpponentModelUtilSpace;

/**
 * Adapter for BayesianModel. Note that this model only works on small domains.
 * 
 * Adapted by Mark Hendrikx to be compatible with the BOA framework.
 * 
 * Tim Baarslag, Koen Hindriks, Mark Hendrikx, Alex Dirkzwager and Catholijn M.
 * Jonker. Decoupling Negotiating Agents to Explore the Space of Negotiation
 * Strategies
 * 
 * @author Mark Hendrikx
 */
public class BayesianModel extends OpponentModel {

	/** Reference to the normal Bayesian Opponent Model */
	private BayesianOpponentModel model;
	/** Index of the first issue weight */
	private int startingBidIssue = 0;

	/**
	 * Initializes the opponent model. If the parameter m is set to a value
	 * greater than zero, only the best hypothesis about the opponent's utility
	 * space is used.
	 */
	@Override
	public void init(NegotiationSession negotiationSession,
			HashMap<String, Double> parameters) throws Exception {
		this.negotiationSession = negotiationSession;
		model = new BayesianOpponentModel(negotiationSession.getUtilitySpace());
		if (parameters.get("m") != null) {
			model.setMostProbableUSHypsOnly(parameters.get("m") > 0);
		} else {
			model.setMostProbableUSHypsOnly(false);
			System.out
					.println("Constant \"m\" was not set. Assumed default value.");
		}
		while (!testIndexOfFirstIssue(negotiationSession.getUtilitySpace()
				.getDomain().getRandomBid(), startingBidIssue)) {
			startingBidIssue++;
		}
	}

	/**
	 * Just an auxiliar funtion to calculate the index where issues start on a
	 * bid because we found out that it depends on the domain.
	 * 
	 * @return true when the received index is the proper index
	 */
	private boolean testIndexOfFirstIssue(Bid bid, int i) {
		try {
			@SuppressWarnings("unused")
			ValueDiscrete valueOfIssue = (ValueDiscrete) bid.getValue(i);
		} catch (Exception e) {
			return false;
		}
		return true;
	}

	/**
	 * Update the opponent model by updating all hypotheses about the opponent's
	 * preference profile.
	 * 
	 * @param opponentBid
	 * @param time
	 *            of offering
	 */
	@Override
	public void updateModel(Bid opponentBid, double time) {
		try {
			model.updateBeliefs(opponentBid);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public double getBidEvaluation(Bid bid) {
		try {
			return model.getNormalizedUtility(bid);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

	/**
	 * @return estimated issue weight of the given issue.
	 */
	public double getWeight(Issue issue) {
		return model.getNormalizedWeight(issue, startingBidIssue);
	}

	/**
	 * @return utilityspace created by using the opponent model adapter.
	 */
	@Override
	public UtilitySpace getOpponentUtilitySpace() {
		return new OpponentModelUtilSpace(model);
	}

	public void cleanUp() {
		super.cleanUp();
	}

	@Override
	public String getName() {
		return "Bayesian Model";
	}

	@Override
	public Set<BOAparameter> getParameters() {
		Set<BOAparameter> set = new HashSet<BOAparameter>();
		set.add(new BOAparameter("m", new BigDecimal(0.0),
				"If higher than 0 the most probable hypothesis is only used"));
		return set;
	}
}