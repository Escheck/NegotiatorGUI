package negotiator.qualitymeasures;

import negotiator.analysis.BidPoint;
import negotiator.analysis.BidSpace;
import negotiator.boaframework.OpponentModel;
import negotiator.utility.UtilitySpace;

/**
 * This class specifies a set of opponent model measures used to measure the performance
 * of an opponent model during a negotiation. Note that the measures are computationally
 * heavy and computed during the negotiation. This entails that it is recommended to use
 * the time-independent rounds protocol.
 * 
 * In practice, it would also be interesting to calculate the correlation of the issue
 * weights. Unfortunately, the Bayesian-type models do not remember the issue weight index
 * (which in Genius is not guaranteed to start at 0).
 * 
 * In addition, imagine using a neural network. In this case an estimate of the utility can
 * be calculated, but not a particular issue weight.
 * 
 * This work is based on "Towards a quality assessment method for learning preference profiles 
 * in negotiation" by Hindriks et al.
 * 
 * @author Mark Hendrikx
 */
public class OpponentModelMeasures {

	/** Utilityspace of the agent under consideration */
	private UtilitySpace ownUS;
	/** Utilityspace of the opponent */
	private UtilitySpace opponentUS;
	/** Bidding space created using both real utility spaces */
	private BidSpace realBS;
	/** The real kalai value */
	private BidPoint realKalai;
	
	/**
	 * Creates the measures object by storing a reference to both utility spaces
	 * and calculating the real Kalai bid.
	 * 
	 * @param ownSpace utilityspace of self
	 * @param opponentModelUS utilityspace of opponent
	 */
	public OpponentModelMeasures(UtilitySpace ownSpace, UtilitySpace opponentModelUS) {
		this.ownUS = ownSpace;
		this.opponentUS = opponentModelUS;
		try {
			realBS = new BidSpace(ownUS, opponentUS);
			realKalai = realBS.getKalaiSmorodinsky();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Calculates the Pearson correlation coefficient by comparing the utility of each bid estimated
	 * by the real and estimated opponent's utility space. Higher is better.
	 * 
	 * @param opponentModel
	 * @return pearson correlation coefficient
	 */
	public double calculatePearsonCorrelationCoefficientBids(OpponentModel opponentModel) {
		UtilitySpace estimatedSpace = opponentModel.getOpponentUtilitySpace(); 
		return UtilspaceTools.getPearsonCorrelationCoefficientOfBids(estimatedSpace, opponentUS);
	}

	/**
	 * Calculates the ranking distanceby comparing the utility of each bid estimated
	 * by the real and estimated opponent's utility space. Lower is better.
	 * 
	 * @param opponentModel
	 * @return ranking distance
	 */
	public double calculateRankingDistanceBids(OpponentModel opponentModel) {
		UtilitySpace estimatedSpace = opponentModel.getOpponentUtilitySpace();
		return UtilspaceTools.getRankingDistanceOfBids(estimatedSpace, opponentUS);
	}
	
	/**
	 * Calculates the absolute difference between the estimated Kalai point and the
	 * real Kalai point. Note that we are only interested in the value for the
	 * opponent.
	 * 
	 * @param opponentModel
	 * @return difference between real and estimated Kalaipoint
	 */
	public double calculateKalaiDiff(OpponentModel opponentModel) {
		UtilitySpace estimatedSpace = opponentModel.getOpponentUtilitySpace();
		BidSpace estimatedBS;
		BidPoint estimatedKalai = null;
		try {
			estimatedBS = new BidSpace(ownUS, estimatedSpace, true);
			estimatedKalai = estimatedBS.getKalaiSmorodinsky();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return Math.abs(realKalai.utilityB - estimatedKalai.utilityB);
	}
}