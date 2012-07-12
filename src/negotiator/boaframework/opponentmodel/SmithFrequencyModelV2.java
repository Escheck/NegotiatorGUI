package negotiator.boaframework.opponentmodel;

import java.util.HashMap;

import agents.bayesianopponentmodel.OpponentModelUtilSpace;
import negotiator.Bid;
import negotiator.boaframework.NegotiationSession;
import negotiator.boaframework.OpponentModel;
import negotiator.boaframework.opponentmodel.agentsmithv2.SmithModelV2;
import negotiator.issue.Issue;
import negotiator.utility.UtilitySpace;

/**
 * Adapter for the Frequency Model of Agent Smith.
 * 
 * @author Mark Hendrikx
 */
public class SmithFrequencyModelV2 extends OpponentModel {

	private SmithModelV2 model;
	private int round = 0;

	@Override
	public void init(NegotiationSession negotiationSession, HashMap<String, Double> parameters) throws Exception {
		model = new SmithModelV2(negotiationSession.getUtilitySpace());
		this.negotiationSession = negotiationSession;
	}

	@Override
	public void updateModel(Bid opponentBid, double time) {
		round++;
		model.addBid(opponentBid);
	}

	@Override
	public double getBidEvaluation(Bid bid) {
		if (round > 0) {
			return model.getNormalizedUtility(bid);
		} else {
			return 0;
		}
	}
	
	@Override
	public UtilitySpace getOpponentUtilitySpace() {
		if (round > 0) {
			return new OpponentModelUtilSpace(model);
		} else {
			return negotiationSession.getUtilitySpace();
		}
	}
	
	public double getWeight(Issue issue) {
		return model.getWeight(issue.getNumber());
	}
	
	@Override
	public String getName() {
		return "Smith Frequency Model V2";
	}
	
	public void cleanUp() {
		super.cleanUp();
		model = null;
	}
}