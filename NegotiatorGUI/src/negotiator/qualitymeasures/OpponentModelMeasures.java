package negotiator.qualitymeasures;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import negotiator.Bid;
import negotiator.BidIterator;
import negotiator.analysis.BidPoint;
import negotiator.analysis.BidPointSorterA;
import negotiator.analysis.BidSpace;
import negotiator.bidding.BidDetails;
import negotiator.boaframework.OpponentModel;
import negotiator.boaframework.SortedOutcomeSpace;
import negotiator.utility.AbstractUtilitySpace;
import negotiator.utility.AdditiveUtilitySpace;

/**
 * This class specifies a set of opponent model measures used to measure the
 * performance of an opponent model during a negotiation. Note that the measures
 * are computationally heavy and computed during the negotiation. This entails
 * that it is recommended to use the time-independent rounds protocol or the
 * normal time-based protocol with the pause functionality.
 * 
 * This work implement the measures discussed in
 * "Towards a quality assessment method for learning preference profiles in negotiation"
 * by Hindriks et al.
 * 
 * Additional measures were added to get a better view of point estimation and
 * distance between sets of points.
 * 
 * @author Mark Hendrikx
 */
public class OpponentModelMeasures {

	/** Utilityspace of the agent under consideration */
	private AbstractUtilitySpace ownUS;
	/** Utilityspace of the opponent */
	private AbstractUtilitySpace opponentUS;
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
	 * Amount of runs for Monte Carlo estimation. More is more accurate at the
	 * cost of computational speed
	 */
	private static final int AMOUNT_OF_SIMULATIONS = 100000;

	/**
	 * Creates the measures object by storing a reference to both utility spaces
	 * and calculating the real Kalai bid.
	 * 
	 * @param ownSpace
	 *            utilityspace of self
	 * @param opponentUS
	 *            utilityspace of opponent
	 */
	public OpponentModelMeasures(AbstractUtilitySpace ownSpace,
			AbstractUtilitySpace opponentUS) {
		this.ownUS = ownSpace;
		this.opponentUS = opponentUS;
		try {
			// we can't use the cache as we want to have the bids, not only the
			// bidpoints.
			BidSpace realBS = new BidSpace(ownUS, opponentUS, false, true);
			realKalai = realBS.getKalaiSmorodinsky();
			realNash = realBS.getNash();
			realIssueWeights = new double[] {};
			if (opponentUS instanceof AdditiveUtilitySpace) {
				UtilspaceTools
						.getIssueWeights((AdditiveUtilitySpace) opponentUS);
			}
			realParetoBids = new ArrayList<Bid>(realBS.getParetoFrontierBids());
			ArrayList<BidPoint> realParetoBidPoints = new ArrayList<BidPoint>(
					realBS.getParetoFrontier());
			paretoSurface = calculateParetoSurface(realParetoBidPoints);
			opponentOutcomeSpace = new SortedOutcomeSpace(opponentUS);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Calculates the Pearson correlation coefficient by comparing the utility
	 * of each bid estimated by the real and estimated opponent's utility space.
	 * Higher is better.
	 * 
	 * @param opponentModel
	 * @return pearson correlation coefficient
	 */
	public double calculatePearsonCorrelationCoefficientBids(
			AbstractUtilitySpace estimatedSpace) {
		return UtilspaceTools.getPearsonCorrelationCoefficientOfBids(
				estimatedSpace, opponentUS);
	}

	/**
	 * Calculates the ranking distance by comparing the utility of each bid
	 * estimated by the real and estimated opponent's utility space. Lower is
	 * better.
	 * 
	 * @param opponentModel
	 * @return ranking distance
	 */
	public double calculateRankingDistanceBids(
			AbstractUtilitySpace estimatedSpace) {
		return UtilspaceTools.getRankingDistanceOfBids(estimatedSpace,
				opponentUS, AMOUNT_OF_SIMULATIONS);
	}

	/**
	 * Calculates the ranking distance by comparing the utility of each weight
	 * estimated by the real and estimated opponent's utility space. Lower is
	 * better.
	 * 
	 * @param opponentModel
	 * @return ranking distance
	 */
	public double calculateRankingDistanceWeights(OpponentModel opponentModel) {
		double[] estimatedIssueWeights = opponentModel.getIssueWeights();
		return UtilspaceTools.calculateRankingDistance(realIssueWeights,
				estimatedIssueWeights);
	}

	/**
	 * Calculates the absolute difference between the estimated Kalai point and
	 * the real Kalai point. Note that we are only interested in the value for
	 * the opponent.
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
		return Math.abs(realKalai.getUtilityB() - estimatedKalai.getUtilityB());
	}

	/**
	 * Calculates the absolute difference between the estimated Nash point and
	 * the real Nash point. Note that we are only interested in the value for
	 * the opponent.
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
		return Math.abs(realNash.getUtilityB() - estimatedNash.getUtilityB());
	}

	/**
	 * Calculates the average difference between the real estimated pareto bids
	 * and their estimated utility for the opponent.
	 * 
	 * @param estimatedSpace
	 * @return average difference in utility for the Pareto optimal bids
	 */
	public double calculateAvgDiffParetoBidToEstimate(
			AbstractUtilitySpace estimatedSpace) {
		double sum = 0;

		// its a difference, not a distance, as we know how we evaluate our own
		// bid

		for (Bid paretoBid : realParetoBids) {
			double realOpp;
			double estOpp;
			try {
				realOpp = opponentUS.getUtility(paretoBid);
				estOpp = estimatedSpace.getUtility(paretoBid);
				sum += Math.abs(realOpp - estOpp);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return sum / realParetoBids.size();
	}

	/**
	 * Calculate the amount of real Pareto bids which have been found by the
	 * opponent model. Note that the estimated utility space may have more or
	 * less Pareto bids than there really are.
	 * 
	 * @param estimatedBS
	 * @return percentage of found real Pareto bids
	 */
	public double calculatePercCorrectlyEstimatedParetoBids(BidSpace estimatedBS) {
		List<Bid> estimatedPFBids = null;
		try {
			estimatedPFBids = estimatedBS.getParetoFrontierBids();
		} catch (Exception e) {
			e.printStackTrace();
		}
		int count = 0;

		if (estimatedPFBids != null && estimatedPFBids.size() > 0
				&& estimatedPFBids.get(0) != null) {
			for (Bid pBid : realParetoBids) {
				if (estimatedPFBids.contains(pBid)) {
					count++;
				}
			}
		}
		return ((double) count / (double) realParetoBids.size());
	}

	/**
	 * Calculate the percentage of bids in the estimated Pareto bids which is
	 * really Pareto optimal.
	 * 
	 * @param estimatedBS
	 * @return percentage of real Pareto optimal bids given the set of estimated
	 *         Pareto optimal bids
	 */
	public double calculatePercIncorrectlyEstimatedParetoBids(
			BidSpace estimatedBS) {
		List<Bid> estimatedPFBids = null;
		try {
			estimatedPFBids = estimatedBS.getParetoFrontierBids();
		} catch (Exception e) {
			e.printStackTrace();
		}
		int count = 0;

		if (estimatedPFBids != null && estimatedPFBids.size() > 0
				&& estimatedPFBids.get(0) != null) {
			for (Bid pBid : estimatedPFBids) {
				if (realParetoBids.contains(pBid)) {
					count++;
				}
			}
		}
		return ((double) count / (double) estimatedPFBids.size());
	}

	/**
	 * This methods calculates the Pareto frontier distance using the following
	 * steps: 1. Map the estimated Pareto-bids to the real space. 2. Calculate
	 * the surface beneath the real Pareto bids and estimated Pareto bids. 3.
	 * Subtract the surfaces and return the absolute difference.
	 * 
	 * Note that the Pareto frontier difference can be positive and negative. In
	 * general, the mapped estimate of the Pareto frontier will have less
	 * surface; however, it can happen that less Pareto-points were estimated.
	 * In this case a Pareto-point is missed, and it can happen that the surface
	 * is therefore larger.
	 * 
	 * @param estimatedBS
	 * @return
	 */
	public double calculateParetoFrontierDistance(BidSpace estimatedBS) {
		// 1. map bids of estimated frontier to real space
		List<BidPoint> estimatedPFBP = new ArrayList<BidPoint>();
		try {
			List<Bid> estimatedPFBids = estimatedBS.getParetoFrontierBids();
			for (Bid bid : estimatedPFBids) {
				estimatedPFBP.add(new BidPoint(null, ownUS.getUtility(bid),
						opponentUS.getUtility(bid)));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		double estimatedParetoSurface = calculateParetoSurface(estimatedPFBP);
		return Math.abs(paretoSurface - estimatedParetoSurface);
	}

	private double calculateParetoSurface(List<BidPoint> paretoFrontier) {
		// Add 0.0; 1.0 and 1.0; 0.0 to set
		boolean foundZero = false;
		boolean foundOne = false;
		for (int i = 0; i < paretoFrontier.size(); i++) {
			if (paretoFrontier.get(i).getUtilityA().equals(0.0)) {
				foundZero = true;
			} else if (paretoFrontier.get(i).getUtilityA().equals(1.0)) {
				foundOne = true;
			}
		}
		if (!foundZero) {
			paretoFrontier.add(new BidPoint(null, 0.0, 1.0));
		}
		if (!foundOne) {
			paretoFrontier.add(new BidPoint(null, 1.0, 0.0));
		}

		// Order bids on utilityA
		Collections.sort(paretoFrontier, new BidPointSorterA());

		double surface = 0;
		for (int i = 0; i < paretoFrontier.size() - 1; i++) {
			surface += calculateSurfaceBelowTwoPoints(paretoFrontier.get(i),
					paretoFrontier.get(i + 1));
		}
		return surface;
	}

	private double calculateSurfaceBelowTwoPoints(BidPoint higher,
			BidPoint lower) {

		// since the bidpoints are discrete, the surface can be decomposed in a
		// triangle and a rectangle
		double rectangleSurface = lower.getUtilityB()
				* (lower.getUtilityA() - higher.getUtilityA());
		double triangleSurface = ((higher.getUtilityB() - lower.getUtilityB()) * (lower
				.getUtilityA() - higher.getUtilityA())) / 2;

		return (rectangleSurface + triangleSurface);
	}

	/**
	 * @param opponentBid
	 * @return index of the opponent's bid in the sorted outcome space
	 */
	public int getOpponentBidIndex(Bid opponentBid) {
		BidDetails oBid = null;
		try {
			oBid = new BidDetails(opponentBid,
					opponentUS.getUtility(opponentBid), -1);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return opponentOutcomeSpace.getAllOutcomes().indexOf(oBid);
	}

	/**
	 * @param opponentModel
	 * @return average difference between the real and estimated utility
	 */
	public double calculateAvgDiffBetweenBids(OpponentModel opponentModel) {
		double difference = 0;
		BidIterator iterator = new BidIterator(opponentUS.getDomain());
		while (iterator.hasNext()) {
			Bid bid = iterator.next();
			try {
				difference += Math.abs(opponentUS.getUtility(bid)
						- opponentModel.getBidEvaluation(bid));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return difference
				/ (double) opponentUS.getDomain().getNumberOfPossibleBids();
	}

	/**
	 * @param opponentModel
	 * @return average difference between the real and estimated issue weights
	 */
	public double calculateAvgDiffBetweenIssueWeights(
			OpponentModel opponentModel) {
		double difference = 0;
		double[] estimatedIssueWeights = opponentModel.getIssueWeights();
		for (int i = 0; i < realIssueWeights.length; i++) {
			difference += Math.abs(realIssueWeights[i]
					- estimatedIssueWeights[i]);
		}
		return difference / (double) realIssueWeights.length;
	}
}