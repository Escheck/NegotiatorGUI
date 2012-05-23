package negotiator.boaframework.opponentmodel;

import java.util.HashMap;
import agents.bayesianopponentmodel.BayesianOpponentModelScalable;
import agents.bayesianopponentmodel.OpponentModelUtilSpace;
import negotiator.Bid;
import negotiator.boaframework.NegotiationSession;
import negotiator.boaframework.OpponentModel;
import negotiator.issue.Issue;
import negotiator.issue.ValueDiscrete;
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

	private BayesianOpponentModelScalable model;
	private int startingBidIssue = 0;
	
	public BayesianModelScalable(NegotiationSession negoSession, int n){
		this.negotiationSession = negoSession;
		model = new BayesianOpponentModelScalable(negotiationSession.getUtilitySpace());
		
		if (n==1) {
			System.out.println("Entering NTFT mode");
			DOMAINSIZE = negotiationSession.getUtilitySpace().getDomain().getNumberOfPossibleBids();
			niceTitForTatMode = true;
		}
	}
	
	@Override
	public void init(NegotiationSession negotiationSession, HashMap<String, Double> parameters) throws Exception {
		while (!testIndexOfFirstIssue(negotiationSession.getUtilitySpace().getDomain().getRandomBid(), startingBidIssue)){
			startingBidIssue++;
		}
		
		model = new BayesianOpponentModelScalable(negotiationSession.getUtilitySpace());
		this.negotiationSession = negotiationSession;
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

	@Override
	public double getDiscountedBidEvaluation(Bid b, double time) {
		try {
			throw new Exception("Unimplemented yet");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}
	
	public double getWeight(Issue issue) {
		return model.getNormalizedWeight(issue, startingBidIssue);
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