package negotiator.boaframework.opponentmodel;

import java.util.HashMap;
import agents.bayesianopponentmodel.BayesianOpponentModelScalable;
import agents.bayesianopponentmodel.OpponentModelUtilSpace;
import negotiator.Bid;
import negotiator.boaframework.NegotiationSession;
import negotiator.boaframework.OpponentModel;
import negotiator.utility.UtilitySpace;

/**
 * Adapter for BayesianOpponentModelScalable. A parameter was added which allows
 * to stop the updating of the opponent model after a given time has passed.
 * Note that this model is solely for testing purposes, in practice it is better
 * to use the implementation of this model by IAMHaggler.
 * 
 * @author Mark Hendrikx
 */
public class BayesianModelScalable extends OpponentModel {

	BayesianOpponentModelScalable model;
	double updateThreshold;
	long DOMAINSIZE;
	boolean niceTitForTatMode = false;
	
	@Override
	public void init(NegotiationSession negotiationSession, HashMap<String, Double> parameters) throws Exception {
		model = new BayesianOpponentModelScalable(negotiationSession.getUtilitySpace());
		this.negotiationSession = negotiationSession;
		if (parameters != null && parameters.containsKey("t")) {
			updateThreshold = parameters.get("t");
		} else {
			updateThreshold = 1.0;
		}
		if (parameters != null && parameters.containsKey("n")) {
			System.out.println("Entering NTFT mode");
			DOMAINSIZE = negotiationSession.getUtilitySpace().getDomain().getNumberOfPossibleBids();
			niceTitForTatMode = true;
		}
	}

	@Override
	public void updateModel(Bid opponentBid) {
		try {
			if (niceTitForTatMode) {
				if (canUpdateBeliefs(negotiationSession.getTime())) {
					model.updateBeliefs(opponentBid);
				}
			} else {
				if (negotiationSession.getTime() < updateThreshold) {
					model.updateBeliefs(opponentBid);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private boolean canUpdateBeliefs(double time)
	{
		// in the last seconds we don't want to lose any time
		if (time > 0.99)
			return false;
		
		// in a big domain, we stop updating half-way
		if (DOMAINSIZE > 10000)
			if (time > 0.5)
				return false;
		return true;
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

	@Override
	public double getDiscountedBidEvaluation(Bid b, double time) {
		try {
			throw new Exception("Unimplemented yet");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}
	
	@Override
	public UtilitySpace getOpponentUtilitySpace() {
		return new OpponentModelUtilSpace(model);
	}
}