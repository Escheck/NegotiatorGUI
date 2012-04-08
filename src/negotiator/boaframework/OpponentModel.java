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
public abstract class OpponentModel implements Cloneable {
	
	protected NegotiationSession negotiationSession;
	protected UtilitySpace opponentUtilitySpace;
	
	public void init(NegotiationSession domainKnow, HashMap<String, Double> parameters) throws Exception {
		negotiationSession = domainKnow;
		opponentUtilitySpace = new UtilitySpace(domainKnow.getUtilitySpace());
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
	
	public OpponentModel clone() {
		try {
			OpponentModel clone = (OpponentModel) super.clone();
			clone.opponentUtilitySpace = this.opponentUtilitySpace;
			clone.negotiationSession = this.negotiationSession;
			return clone;
		} catch (CloneNotSupportedException e) {
			throw new RuntimeException("this could never happen", e);
		}
	}
	
	public OpponentModel reset() {
		opponentUtilitySpace = null;
		negotiationSession = null;
		return this;
	}

	public void setOpponentUtilitySpace(BilateralAtomicNegotiationSession fNegotiation) { }
}
