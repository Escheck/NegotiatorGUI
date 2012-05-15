package negotiator.qualitymeasures;

import java.util.ArrayList;

import negotiator.Bid;
import negotiator.analysis.BidPoint;
import negotiator.analysis.BidSpace;
import negotiator.boaframework.OpponentModel;
import negotiator.issue.Issue;
import negotiator.utility.UtilitySpace;

/**
 * This class specifies a set of opponent model measures used to measure the performance
 * of an opponent model during a negotiation. Note that the measures are computationally
 * heavy and computed during the negotiation. This entails that it is recommended to use
 * the time-independent rounds protocol or the normal time-based protocol with the
 * pause functionality.
 * 
 * This work implement the measures discussed in 
 * "Towards a quality assessment method for learning preference profiles in negotiation" 
 * by Hindriks et al.
 * 
 * Additional measures were added to get a better view of point estimation and distance between
 * sets of points.
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
	/** The real Nash value */
	private BidPoint realNash;
	/** The real issue weights */
	private double[] realIssueWeights;
	/** The real set of Pareto optimal bids */
	private ArrayList<Bid> realParetoBids;
	
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
			realBS = new BidSpace(ownUS, opponentUS, false);
			realKalai = realBS.getKalaiSmorodinsky();
			realNash = realBS.getNash();
			realIssueWeights = UtilspaceTools.getIssueWeights(opponentModelUS);
			realParetoBids = realBS.getParetoFrontierBids();
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
	 * Calculates the Pearson correlation coefficient by comparing the utility of each weight estimated
	 * by the real and estimated opponent's utility space. Higher is better.
	 * 
	 * @param opponentModel
	 * @return pearson correlation coefficient
	 */
	public double calculatePearsonCorrelationCoefficientWeights(OpponentModel opponentModel) {
		double[] estimatedIssueWeights = opponentModel.getIssueWeights();
		return UtilspaceTools.calculatePearsonCorrelationCoefficient(realIssueWeights, estimatedIssueWeights);
	}
	
	/**
	 * Calculates the ranking distance by comparing the utility of each bid estimated
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
	 * Calculates the ranking distance by comparing the utility of each weight estimated
	 * by the real and estimated opponent's utility space. Lower is better.
	 * 
	 * @param opponentModel
	 * @return ranking distance
	 */
	public double calculateRankingDistanceWeights(OpponentModel opponentModel) {
		double[] estimatedIssueWeights = opponentModel.getIssueWeights();
		return UtilspaceTools.calculateRankingDistance(realIssueWeights, estimatedIssueWeights);
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
	
	/**
	 * Calculates the absolute difference between the estimated Nash point and the
	 * real Nash point. Note that we are only interested in the value for the
	 * opponent.
	 * 
	 * @param opponentModel
	 * @return difference between real and estimated Nashpoint
	 */
	public double calculateNashDiff(OpponentModel opponentModel) {
		UtilitySpace estimatedSpace = opponentModel.getOpponentUtilitySpace();
		BidSpace estimatedBS;
		BidPoint estimatedNash = null;
		try {
			estimatedBS = new BidSpace(ownUS, estimatedSpace, true);
			estimatedNash = estimatedBS.getNash();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return Math.abs(realNash.utilityB - estimatedNash.utilityB);
	}
	
	public double calculateAvgDiffParetoBidToEstimate(OpponentModel opponentModel) {
		double sum = 0;
		
		// its a difference, not a distance, as we know how we evaluate our own bid
		for (Bid paretoBid : realParetoBids) {
			double realOpp;
			double estOpp;
			try {
				realOpp = opponentUS.getUtility(paretoBid);
				estOpp = opponentModel.getOpponentUtilitySpace().getUtility(paretoBid);
				sum += Math.abs(realOpp - estOpp);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return sum / realParetoBids.size();
	}
	
	public double calculatePercCorrectlyEstimatedParetoBids(OpponentModel opponentModel) {
		UtilitySpace estimatedSpace = opponentModel.getOpponentUtilitySpace();
		BidSpace estimatedBS;
		ArrayList<Bid> estimatedPFBids = null;
		try {
			estimatedBS = new BidSpace(ownUS, estimatedSpace, false);
			estimatedPFBids = estimatedBS.getParetoFrontierBids();
		} catch (Exception e) {
			e.printStackTrace();
		}
		int count = 0;
		
		if (estimatedPFBids != null && estimatedPFBids.size() > 0 && estimatedPFBids.get(0) != null) {
			for (Bid pBid : realParetoBids) {
				if (estimatedPFBids.contains(pBid)) {
					count++;
				}
			}
		}
		return ((double)count / (double)realParetoBids.size());
	}
	
	public double calculatePercIncorrectlyEstimatedParetoBids() {
		return 0;
		
	}
	
	public double calculateParetoFrontierDistance() {
		// 1. map bids of estimated frontier to real space
		// 2. calculate surface of Pareto frontier bids
		// 3. subtract surface of estimated frontier
		return 0;
	}
}