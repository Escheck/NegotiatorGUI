package negotiator.boaframework;

import java.util.HashMap;

import negotiator.Bid;
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

	public abstract void updateModel(Bid opponentBid);
	
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
	
	/**
	 * Determines the discounted utility of the opponent according to the OpponentModel
	 * @param Bid
	 * @param Time
	 * @return
	 */
	public double getDiscountedBidEvaluation(Bid b, double time){
		return opponentUtilitySpace.getUtilityWithDiscount(b, time);
	}
	
	public UtilitySpace getOpponentUtilitySpace(){
		return opponentUtilitySpace;
	}

	public void setOpponentUtilitySpace(BilateralAtomicNegotiationSession fNegotiation) { 
		
	}
	
	public void cleanUp() {
		negotiationSession = null;
		opponentUtilitySpace = null;
		cleared = true;
	}

	public boolean isCleared() {
		return cleared;
	}
}
