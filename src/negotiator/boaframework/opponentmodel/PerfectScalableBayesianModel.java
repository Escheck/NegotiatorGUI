package negotiator.boaframework.opponentmodel;

import java.util.HashMap;
import agents.bayesianopponentmodel.OpponentModelUtilSpace;
import agents.bayesianopponentmodel.PerfectBayesianOpponentModelScalable;
import negotiator.Bid;
import negotiator.Global;
import negotiator.boaframework.NegotiationSession;
import negotiator.boaframework.OpponentModel;
import negotiator.issue.Issue;
import negotiator.issue.ValueDiscrete;
import negotiator.protocol.BilateralAtomicNegotiationSession;
import negotiator.utility.UtilitySpace;

/**
 * Adapter for BayesianOpponentModelScalable for the BOA framework.
 * Modified such that it has perfect knowledge about the opponent's strategy.
 * 
 * Tim Baarslag, Koen Hindriks, Mark Hendrikx, Alex Dirkzwager and Catholijn M. Jonker.
 * Decoupling Negotiating Agents to Explore the Space of Negotiation Strategies
 *
 * @author Mark Hendrikx
 */
public class PerfectScalableBayesianModel extends OpponentModel {

	private PerfectBayesianOpponentModelScalable model;
	private int startingBidIssue = 0;

	@Override
	public void init(NegotiationSession negoSession, HashMap<String, Double> parameters) throws Exception {
		initializeModel(negoSession);
	}

	@Override
	public void setOpponentUtilitySpace(UtilitySpace opponentUtilitySpace) {
		System.out.println("called");
		model.setOpponentUtilitySpace(opponentUtilitySpace);
	}
	
	@Override
	public void setOpponentUtilitySpace(BilateralAtomicNegotiationSession session) {
		
		if (Global.experimentalSetup) {
			opponentUtilitySpace = session.getAgentAUtilitySpace();
			if (negotiationSession.getUtilitySpace().getFileName().equals(opponentUtilitySpace.getFileName())) {
				opponentUtilitySpace = session.getAgentBUtilitySpace();
			}
			model.setOpponentUtilitySpace(opponentUtilitySpace);
		} else {
			System.err.println("Global.experimentalSetup should be enabled!");
		}	
	}
	
	public void initializeModel(NegotiationSession negotiationSession) {
		this.negotiationSession = negotiationSession;
		while (!testIndexOfFirstIssue(negotiationSession.getUtilitySpace().getDomain().getRandomBid(), startingBidIssue)){
			startingBidIssue++;
		}
		model = new PerfectBayesianOpponentModelScalable(negotiationSession.getUtilitySpace());
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
			// time is not used by this opponent model
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
	public UtilitySpace getOpponentUtilitySpace() {
		return new OpponentModelUtilSpace(model);
	}
	
	public void cleanUp() {
		super.cleanUp();
	}
	
	@Override
	public String getName() {
		return "Scalable Bayesian Model";
	}
}