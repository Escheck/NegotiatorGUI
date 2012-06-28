package negotiator.boaframework.opponentmodel;

import java.util.HashMap;
import agents.bayesianopponentmodel.OpponentModelUtilSpace;
import negotiator.Bid;
import negotiator.boaframework.NegotiationSession;
import negotiator.boaframework.OpponentModel;
import negotiator.boaframework.opponentmodel.fsegaagent.*;
import negotiator.issue.Issue;
import negotiator.issue.ValueDiscrete;
import negotiator.utility.UtilitySpace;

/**
 * Adapter to opponent model of FSEGA. Weights become invalid after ~30 rounds.
 * This bug is not visible in the behavior of FSEGA, as the agent does not appear
 * to ever use an OM.
 * 
 * @author Mark Hendrikx
 */
public class FSEGABayesianModel extends OpponentModel {

	FSEGAOpponentModel model;
	private int startingBidIssue = 0;

	@Override
	public void init(NegotiationSession negotiationSession, HashMap<String, Double> parameters) throws Exception {
		model = new FSEGAOpponentModel(negotiationSession.getUtilitySpace());
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
	
	@Override
	public UtilitySpace getOpponentUtilitySpace() {
		return new OpponentModelUtilSpace(model);
	}
	
	public double getWeight(Issue issue) {
		return model.getNormalizedWeight(issue, startingBidIssue);
	}
	
	public void cleanUp() {
		super.cleanUp();
		model = null;
	}
}