package negotiator.boaframework;

import java.util.HashMap;

import negotiator.Bid;
import negotiator.issue.Issue;
import negotiator.protocol.BilateralAtomicNegotiationSession;
import negotiator.utility.UtilitySpace;

/**
 * This is the abstract class for the agents Opponent Model.
 * 
 * @author Alex Dirkzwager, Mark Hendrikx
 */
public abstract class OpponentModel {
	
	protected NegotiationSession negotiationSession;
	protected UtilitySpace opponentUtilitySpace;
	private boolean cleared;
	
	public void init(NegotiationSession domainKnow, HashMap<String, Double> parameters) throws Exception {
		negotiationSession = domainKnow;
		opponentUtilitySpace = new UtilitySpace(domainKnow.getUtilitySpace());
		cleared = false;
	}
	
	public void init(NegotiationSession domainKnow) {
		negotiationSession = domainKnow;
		opponentUtilitySpace = new UtilitySpace(domainKnow.getUtilitySpace());
	}

	public void updateModel(Bid opponentBid) {
		updateModel(opponentBid, negotiationSession.getTime());
	}
	
	public abstract void updateModel(Bid bid, double time);
	
	/**
	 * Determines the utility of the opponent according to the OpponentModel
	 * @param Bid
	 * @return Utility of the bid
	 */
	public double getBidEvaluation(Bid b){
		try {
			return opponentUtilitySpace.getUtility(b);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return -1;
	}
	
	public UtilitySpace getOpponentUtilitySpace(){
		return opponentUtilitySpace;
	}

	public void setOpponentUtilitySpace(BilateralAtomicNegotiationSession fNegotiation) { 
		
	}
	
	/**
	 * Returns the weight of a particular issue in the domain.
	 * @param issueID
	 * @return
	 */
	public double getWeight(Issue issue) {
		return opponentUtilitySpace.getWeight(issue.getNumber());
	}
	
	public double[] getIssueWeights() {
		double estimatedIssueWeights[] = new double[negotiationSession.getUtilitySpace().getDomain().getIssues().size()];
		int i = 0;
		for(Issue issue : negotiationSession.getUtilitySpace().getDomain().getIssues()) {
			estimatedIssueWeights[i] = getWeight(issue);
			i++;
		}
		return estimatedIssueWeights;
	}
	
	public void cleanUp() {
		negotiationSession = null;
		cleared = true;
	}

	public boolean isCleared() {
		return cleared;
	}

	public String getName() {
		return "Default";
	}

	public void setOpponentUtilitySpace(UtilitySpace opponentUtilitySpace) {
		
	}
}
