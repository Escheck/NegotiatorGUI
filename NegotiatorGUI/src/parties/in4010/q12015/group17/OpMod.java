package parties.in4010.q12015.group17;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import negotiator.Bid;
import negotiator.BidHistory;
import negotiator.Domain;
import negotiator.bidding.BidDetails;
import negotiator.boaframework.NegotiationSession;
import negotiator.boaframework.OpponentModel;
import negotiator.issue.Value;
import negotiator.issue.ValueDiscrete;
import negotiator.utility.Evaluator;
import negotiator.utility.EvaluatorDiscrete;
import negotiator.utility.UtilitySpace;

public class OpMod extends OpponentModel {

	/**
	 * This is the opponent model class. Class variables.
	 */
	HashMap<String, UtilitySpace> opponentSpace;
	HashMap<String, BidHistory> bidHistories;
	UtilitySpace startSpace;
	double growRate;
	Domain domain;

	/**
	 * Initialisation method for {@link NegotiationSession}, opponentSpace,
	 * {@link BidHistory} and {@link Domain}.
	 * 
	 * @param negotiationSession
	 */
	public void init(NegotiationSession negotiationSession) {
		super.init(negotiationSession);
		opponentSpace = new HashMap<String, UtilitySpace>();
		bidHistories = new HashMap<String, BidHistory>();
		domain = negotiationSession.getUtilitySpace().getDomain();
	}

	/**
	 * Creates a new {@link UtilitySpace} for the current domain, with equal
	 * weights and evaluation values. Does this by cloning a "clean"
	 * {@link UtilitySpace} of the domain. This clean {@link UtilitySpace} is
	 * created the first time this method is called.
	 * 
	 * @returns The new {@link UtilitySpace}
	 * @throws Exception
	 */
	private UtilitySpace cloneUtil() throws Exception {
		if (startSpace == null) {
			startSpace = new UtilitySpace(domain);

			int issueAmount = domain.getObjectives().size();
			for (int i = 1; i < issueAmount; i++) {

				EvaluatorDiscrete eval = ((EvaluatorDiscrete) negotiationSession
						.getUtilitySpace().getEvaluator(i)).clone();
				Set<ValueDiscrete> valset = eval.getValues();
				Iterator<ValueDiscrete> vals = valset.iterator();

				while (vals.hasNext()) {
					ValueDiscrete v = vals.next();
					eval.setEvaluationDouble(v, (double) 0.0);
				}
				eval.setWeight((double) 1 / (issueAmount - 1));
				startSpace.addEvaluator(domain.getObjective(i), eval);
			}
		}
		return new UtilitySpace(startSpace);
	}

	/**
	 * Updates the opponent {@link UtilitySpace} (predicted utility space).
	 * 
	 * @param bid
	 *            The {@link Bid} offered by the opponent
	 * @param sender
	 *            The name of the opponent
	 * @throws Exception
	 */
	public void updateModel(Bid bid, String sender) throws Exception {
		// If this is the first time we hear of the opponent, we create a new
		// opponentSpace and bidHistory.
		if (!opponentSpace.containsKey(sender)) {
			UtilitySpace us = cloneUtil();
			opponentSpace.put(sender, us);
			bidHistories.put(sender, new BidHistory());
		}

		// Retrieves the opponents UtilitySpace for updating
		UtilitySpace opUtil = opponentSpace.get(sender);
		BidHistory opHist = bidHistories.get(sender);

		for (int i = 0; i < bid.getIssues().size(); i++) {

			Evaluator eval = opUtil.getEvaluator(i + 1);
			Value c = bid.getValue(i + 1);

			if (opHist.size() > 0) {
				Bid prevbid = opHist.getLastBid();
				Value p = prevbid.getValue(i + 1);

				if (p.equals(c)) {
					eval.setWeight(opUtil.getEvaluator(i + 1).getWeight() + 0.1);
				}
			}

			if (eval instanceof EvaluatorDiscrete) {
				ValueDiscrete dVal = (ValueDiscrete) c;
				EvaluatorDiscrete dEval = (EvaluatorDiscrete) eval;
				dEval.setEvaluation(dVal, dEval.getValue(dVal) + 1);
			}
		}
		// Normalise values after updating weights
		normalize(opUtil);

		// Adds bid to opponent history
		opHist.add(new BidDetails(bid, opUtil.getUtility(bid)));
	}

	/**
	 * Normalises the weights of every evaluator within the given
	 * {@link UtilitySpace}.
	 * 
	 * @param util
	 *            The provided {@link UtilitySpace}
	 */
	private void normalize(UtilitySpace util) {
		int size = util.getNrOfEvaluators();
		double sum = 0;

		// Get the sum of all weights
		for (int i = 1; i < size + 1; i++) {
			sum += util.getEvaluator(i).getWeight();
		}
		// Normalise the weight relative to the sum of the weights
		for (int i = 1; i < size + 1; i++) {
			util.getEvaluator(i).setWeight(
					util.getEvaluator(i).getWeight() / sum);
		}
	}

	/**
	 * Computes the product of all utilities for the input {@link Bid}.
	 * 
	 * @param bid
	 *            which is the bid to compute the groupUtility for
	 * @return The groupUtility
	 * @throws Exception
	 */
	public double getGroupUtility(Bid bid, double selfWeight) throws Exception {
		double util = Math.pow(
				negotiationSession.getUtilitySpace().getUtility(bid),
				selfWeight);
		Iterator<UtilitySpace> opSpaces = opponentSpace.values().iterator();

		while (opSpaces.hasNext()) {
			util = util * opSpaces.next().getUtility(bid);
		}
		return util;
	}

	/**
	 * Returns the {@link UtilitySpace} belonging to the provided party.
	 * 
	 * @param name
	 *            which is the name of the provided party
	 * @return Opponent {@link UtilitySpace}
	 * */
	public UtilitySpace getOpSpace(String name) {
		return opponentSpace.get(name);
	}

	public String toString() {
		return "OpModel tracking " + opponentSpace.size() + "opponents.";
	}

	/**
	 * Unused silly method which is not needed.
	 */
	@Override
	public void updateModel(Bid arg0, double arg1) {

	}
}
