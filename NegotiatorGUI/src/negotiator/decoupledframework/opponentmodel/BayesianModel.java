package negotiator.decoupledframework.opponentmodel;

import java.util.HashMap;

import agents.bayesianopponentmodel.BayesianOpponentModel;
import agents.bayesianopponentmodel.OpponentModelUtilSpace;
import negotiator.Bid;
import negotiator.decoupledframework.NegotiationSession;
import negotiator.decoupledframework.OpponentModel;
import negotiator.utility.UtilitySpace;

/**
 * Adapter for BayesianModel. Note that this model only works on small domains.
 * 
 * @author Mark Hendrikx
 */
public class BayesianModel extends OpponentModel {

	BayesianOpponentModel model;
	
	@Override
	public void init(NegotiationSession negotiationSession, HashMap<String, Double> parameters) throws Exception {
		model = new BayesianOpponentModel(negotiationSession.getUtilitySpace());
		if (parameters.get("m") != null) {
			model.setMostProbableUSHypsOnly(parameters.get("m") > 0);
		} else {
			throw new Exception("Constant \"m\" was not set.");
		}
	}

	@Override
	public void updateModel(Bid opponentBid) {
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