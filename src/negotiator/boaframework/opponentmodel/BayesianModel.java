package negotiator.boaframework.opponentmodel;

import java.util.HashMap;

import agents.bayesianopponentmodel.BayesianOpponentModel;
import agents.bayesianopponentmodel.OpponentModelUtilSpace;
import negotiator.Bid;
import negotiator.boaframework.NegotiationSession;
import negotiator.boaframework.OpponentModel;
import negotiator.issue.Issue;
import negotiator.issue.ValueDiscrete;
import negotiator.utility.UtilitySpace;

/**
 * Adapter for BayesianModel. Note that this model only works on small domains.
 * 
 * @author Mark Hendrikx
 */
public class BayesianModel extends OpponentModel {

	BayesianOpponentModel model;
	private int startingBidIssue = 0;
	
	@Override
	public void init(NegotiationSession negotiationSession, HashMap<String, Double> parameters) throws Exception {
		model = new BayesianOpponentModel(negotiationSession.getUtilitySpace());
		if (parameters.get("m") != null) {
			model.setMostProbableUSHypsOnly(parameters.get("m") > 0);
		} else {
			model.setMostProbableUSHypsOnly(false);
			System.out.println("Constant \"m\" was not set. Assumed default value.");
		}
		while (!testIndexOfFirstIssue(negotiationSession.getUtilitySpace().getDomain().getRandomBid(), startingBidIssue)){
			startingBidIssue++;
		}
	}
	
	/**
	 * Just an auxiliar funtion to calculate the index where issues start on a bid
	 * because we found out that it depends on the domain.
	 * @return true when the received index is the proper index
	 */
	private boolean testIndexOfFirstIssue(Bid bid, int i){
		try{
			ValueDiscrete valueOfIssue = (ValueDiscrete) bid.getValue(i);
		}
		catch (Exception e) {
			return false;
		}
		return true;
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

	public double getWeight(Issue issue) {
		return model.getNormalizedWeight(issue, startingBidIssue);
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
	
	public void cleanUp() {
		super.cleanUp();
		model = null;
	}
}