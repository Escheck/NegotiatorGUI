package negotiator.qualitymeasures;

import java.util.ArrayList;
import java.util.Collections;
import negotiator.Bid;
import negotiator.BidIterator;
import negotiator.analysis.BidPoint;
import negotiator.analysis.BidPointSorterAutil;
import negotiator.analysis.BidSpace;
import negotiator.bidding.BidDetails;
import negotiator.boaframework.OpponentModel;
import negotiator.boaframework.SortedOutcomeSpace;
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
	/** The real kalai value */
	private BidPoint realKalai;
	/** The real Nash value */
	private BidPoint realNash;
	/** The real issue weights */
	private double[] realIssueWeights;
	/** The real set of Pareto optimal bids */
	private ArrayList<Bid> realParetoBids;
	/** The real set of Pareto optimal bids */
	private double paretoSurface;
	/** Outcomespace of the opponent used to look up the index of the bid **/
	private SortedOutcomeSpace opponentOutcomeSpace;
	
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
			BidSpace realBS = new BidSpace(ownUS, opponentUS, false);
			realKalai = realBS.getKalaiSmorodinsky();
			realNash = realBS.getNash();
			realIssueWeights = UtilspaceTools.getIssueWeights(opponentModelUS);
			realParetoBids = realBS.getParetoFrontierBids();
			paretoSurface = calculateParetoSurface((ArrayList<BidPoint>) realBS.getParetoFrontier().clone());
			opponentOutcomeSpace = new SortedOutcomeSpace(opponentUS);
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

	public void setRealIssueWeights(double[] realIssueWeights) {
		this.realIssueWeights = realIssueWeights;
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
	public double calculateKalaiDiff(BidSpace estimatedBS) {
		BidPoint estimatedKalai = null;
		try {
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
	public double calculateNashDiff(BidSpace estimatedBS) {
		BidPoint estimatedNash = null;
		try {
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
	
	public double calculatePercCorrectlyEstimatedParetoBids(BidSpace estimatedBS) {
		ArrayList<Bid> estimatedPFBids = null;
		try {
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
	
	public double calculatePercIncorrectlyEstimatedParetoBids(BidSpace estimatedBS) {
		ArrayList<Bid> estimatedPFBids = null;
		try {
			estimatedPFBids = estimatedBS.getParetoFrontierBids();
		} catch (Exception e) {
			e.printStackTrace();
		}
		int count = 0;
		
		if (estimatedPFBids != null && estimatedPFBids.size() > 0 && estimatedPFBids.get(0) != null) {
			for (Bid pBid : estimatedPFBids) {
				if (realParetoBids.contains(pBid)) {
					count++;
				}
			}
		}
		return ((double)count / (double)estimatedPFBids.size());
	}
	
	/**
	 * Note that the Pareto frontier difference can be positive and negative.
	 * In general, the mapped estimate of the Pareto frontier will have less surface;
	 * however, it can happen that less Pareto-points were estimated. In this case
	 * a Pareto-point is missed, and it can happen that the surface is therefore larger.
	 * 
	 * @param estimatedBS
	 * @return
	 */
	public double calculateParetoFrontierDistance(BidSpace estimatedBS) {
		// 1. map bids of estimated frontier to real space
		ArrayList<BidPoint> estimatedPFBP = new ArrayList<BidPoint>();
		try {
			ArrayList<Bid> estimatedPFBids = estimatedBS.getParetoFrontierBids();
			for (Bid bid : estimatedPFBids) {
				estimatedPFBP.add(new BidPoint(null, ownUS.getUtility(bid), opponentUS.getUtility(bid)));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		double estimatedParetoSurface = calculateParetoSurface(estimatedPFBP);
		return Math.abs(paretoSurface - estimatedParetoSurface);
	}
	
	private double calculateParetoSurface(ArrayList<BidPoint> paretoFrontier) {
		// Add 0.0; 1.0 and 1.0; 0.0 to set
		boolean foundZero = false;
		boolean foundOne = false;
		for (int i = 0; i < paretoFrontier.size(); i++) {
			if (paretoFrontier.get(i).utilityA.equals(0.0)) {
				foundZero = true;
			} else if (paretoFrontier.get(i).utilityA.equals(1.0)) {
				foundOne = true;
			}
		}
		if (!foundZero) { paretoFrontier.add(new BidPoint(null, 0.0, 1.0)); }
		if (!foundOne) { paretoFrontier.add(new BidPoint(null, 1.0, 0.0)); }
		
		// Order bids on utilityA
		Collections.sort(paretoFrontier, new BidPointSorterAutil());

		double surface = 0;
		for (int i = 0; i < paretoFrontier.size() - 1; i++) {
			surface += calculateSurfaceBelowTwoPoints(paretoFrontier.get(i), paretoFrontier.get(i + 1));
		}
		return surface;
	}

	private double calculateSurfaceBelowTwoPoints(BidPoint higher, BidPoint lower) {
		
		// since the bidpoints are discrete, the surface can be decomposed in a triangle and a rectangle
		double rectangleSurface = lower.utilityB * (lower.utilityA - higher.utilityA);
		double triangleSurface = ((higher.utilityB - lower.utilityB) * (lower.utilityA - higher.utilityA)) / 2;

		return (rectangleSurface + triangleSurface);
	}
	
	public int getOpponentBidIndex(Bid opponentBid) {
		BidDetails oBid = null;
		try {
			oBid = new BidDetails(opponentBid, opponentUS.getUtility(opponentBid), -1);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return opponentOutcomeSpace.getAllOutcomes().indexOf(oBid);
	}

	public double calculateAvgDiffBetweenBids(OpponentModel model) {
		double difference = 0;
		BidIterator iterator = new BidIterator(opponentUS.getDomain());
		while (iterator.hasNext()) {
			Bid bid = iterator.next();
			try {
				difference += Math.abs(opponentUS.getUtility(bid) - model.getBidEvaluation(bid));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return difference / (double) opponentUS.getDomain().getNumberOfPossibleBids();
	}

	public double calculateAvgDiffBetweenIssueWeights(
			OpponentModel opponentModel) {
		double difference = 0;
		double[] estimatedIssueWeights = opponentModel.getIssueWeights();
		for (int i = 0; i < realIssueWeights.length; i++) {
			difference += Math.abs(realIssueWeights[i]- estimatedIssueWeights[i]);
		}
		return difference / (double) realIssueWeights.length;
	}
}