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
 * This work is based on "Towards a quality assessment method for learning preference profiles in negotiation"
 * by Hindriks et al.
 * 
 * @author Mark Hendrikx
 */
public class OpponentModelMeasures {

	// the utility 
	private UtilitySpace ownUS;
	private UtilitySpace opponentUS;
	private BidSpace realBS;
	private BidPoint realKalai;
	
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
	
	public double calculatePearsonCorrelationCoefficientBids(OpponentModel opponentModel) {
		UtilitySpace estimatedSpace = opponentModel.getOpponentUtilitySpace();
		double pearsonDistUtil = UtilspaceTools.getPearsonCorrelationCoefficientOfBids(estimatedSpace, opponentUS);
		return pearsonDistUtil;
	}

	public double calculateRankingDistanceBids(OpponentModel opponentModel) {
		UtilitySpace estimatedSpace = opponentModel.getOpponentUtilitySpace();
		double rankingDistBids = UtilspaceTools.getRankingDistanceOfBids(estimatedSpace, opponentUS);
		return rankingDistBids;
	}
	
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