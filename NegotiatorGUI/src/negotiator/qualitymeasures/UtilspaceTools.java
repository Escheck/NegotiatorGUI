package negotiator.qualitymeasures;

import java.util.Random;

import negotiator.Bid;
import negotiator.BidIterator;
import negotiator.analysis.BidPoint;
import negotiator.issue.Issue;
import negotiator.utility.AbstractUtilitySpace;
import negotiator.utility.AdditiveUtilitySpace;

/**
 * Collection of useful methods to analyze a pair of utility spaces.
 * 
 * @author Mark Hendrikx
 */
public class UtilspaceTools {

	// if the amount of bids is larger or equal to this value, exact calculation
	// takes to long and an estimation procedure is used.
	static final int MAX_SIZE_FOR_EXACT_CALCULATION = 80000;

	public static double getRankingDistanceOfBids(AbstractUtilitySpace spaceA,
			AbstractUtilitySpace spaceB, int simulations) {
		double bidsUtilA[] = getBidsUtil(spaceA);
		double bidsUtilB[] = getBidsUtil(spaceB);
		if (bidsUtilA.length <= MAX_SIZE_FOR_EXACT_CALCULATION) {
			return calculateRankingDistance(bidsUtilA, bidsUtilB);
		}
		return calculateRankingDistanceMonteCarlo(bidsUtilA, bidsUtilB,
				simulations);
	}

	public static double getPearsonCorrelationCoefficientOfBids(
			AbstractUtilitySpace spaceA, AbstractUtilitySpace spaceB) {
		double bidsUtilA[] = getBidsUtil(spaceA);
		double bidsUtilB[] = getBidsUtil(spaceB);
		return calculatePearsonCorrelationCoefficient(bidsUtilA, bidsUtilB);
	}

	public static double getRankingDistanceOfIssueWeights(
			AdditiveUtilitySpace spaceA, AdditiveUtilitySpace spaceB) {
		double issueWeightsA[] = UtilspaceTools.getIssueWeights(spaceA);
		double issueWeightsB[] = UtilspaceTools.getIssueWeights(spaceB);
		return calculateRankingDistance(issueWeightsA, issueWeightsB);
	}

	public static double getPearsonCorrelationCoefficientOfIssueWeights(
			AdditiveUtilitySpace spaceA, AdditiveUtilitySpace spaceB) {
		double issueWeightsA[] = UtilspaceTools.getIssueWeights(spaceA);
		double issueWeightsB[] = UtilspaceTools.getIssueWeights(spaceB);
		return calculatePearsonCorrelationCoefficient(issueWeightsA,
				issueWeightsB);
	}

	/**
	 * Helper-method used to get the issue weights in an array of doubles.
	 * 
	 * @param utilityspace
	 * @return array of issue weights
	 */
	public static double[] getIssueWeights(AdditiveUtilitySpace space) {
		double issueWeights[] = new double[space.getDomain().getIssues().size()];

		int i = 0;
		for (Issue issue : space.getDomain().getIssues()) {
			issueWeights[i] = space.getWeight(issue.getNumber());
			i++;
		}
		return issueWeights;
	}

	/**
	 * Helper-method used to get the the utilities of all possible bids in an
	 * array of doubles.
	 * 
	 * @param utilityspace
	 * @return array of utilities
	 */
	private static double[] getBidsUtil(AbstractUtilitySpace space) {
		double bidsUtil[] = new double[(int) (space.getDomain()
				.getNumberOfPossibleBids())];
		BidIterator lIter = new BidIterator(space.getDomain());

		int i = 0;
		while (lIter.hasNext()) {
			Bid lBid = lIter.next();
			try {
				bidsUtil[i] = space.getUtility(lBid);
			} catch (Exception e) {
				e.printStackTrace();
			}
			i++;
		}
		return bidsUtil;
	}

	/**
	 * Calculate the Pearson distance between two sets.
	 * 
	 * @param setA
	 * @param setB
	 * @return
	 */
	public static double calculatePearsonCorrelationCoefficient(double[] setA,
			double[] setB) {
		if (setA.length != setB.length) {
			System.err.println("Amount of variables should be equal.");
		}

		double averageSetA = 0, averageSetB = 0;
		double sumA = 0, sumB = 0;

		// calculate average values
		for (int i = 0; i < setA.length; i++) {
			sumA += setA[i];
			sumB += setB[i];
		}
		averageSetA = (double) sumA / (double) setA.length;
		averageSetB = (double) sumB / (double) setB.length;

		// calculate the distance itself
		double nominator = 0;
		double sumSquareNormA = 0;
		double sumSquareNormB = 0;
		for (int i = 0; i < setA.length; i++) {
			double normA = setA[i] - averageSetA;
			double normB = setB[i] - averageSetB;

			nominator += (normA * normB);

			sumSquareNormA += Math.pow(normA, 2);
			sumSquareNormB += Math.pow(normB, 2);
		}
		return nominator / (Math.sqrt(sumSquareNormA * sumSquareNormB));
	}

	public static double distanceBetweenTwoPoints(double ax, double ay,
			double bx, double by) {
		return Math.sqrt((Math.pow((ax - bx), 2) + Math.pow((ay - by), 2)));
	}

	public static double distanceBetweenTwoBidPoints(BidPoint a, BidPoint b) {
		return distanceBetweenTwoPoints(a.getUtilityA(), a.getUtilityB(),
				b.getUtilityA(), b.getUtilityB());
	}

	/**
	 * Calculate the ranking distance between two sets.
	 * 
	 * @param bidsUtilA
	 * @param bidsUtilB
	 * @return
	 */
	public static double calculateRankingDistance(double[] setA, double[] setB) {
		if (setA.length != setB.length) {
			System.out.println("Amount of variables should be equal.");
		}

		double totalDistance = 0;
		for (int i = 0; i < setA.length; i++) {
			for (int j = 0; j < setB.length; j++) {
				// if the ordering differs
				if (Math.signum(setA[i] - setA[j]) != Math.signum(setB[i]
						- setB[j]))
					totalDistance++;
			}
		}
		return totalDistance / (setA.length * setB.length);
	}

	/**
	 * Calculate the ranking distance by using a Monte Carlo simulation.
	 * 
	 * @param setA
	 * @param setB
	 * @return
	 */
	private static double calculateRankingDistanceMonteCarlo(double[] setA,
			double[] setB, int comparisons) {
		double totalDistance = 0;

		for (int k = 0; k < comparisons; k++) {
			int i = (new Random()).nextInt(setA.length - 1);
			int j = (new Random()).nextInt(setB.length - 1);
			if (Math.signum(setA[i] - setA[j]) != Math
					.signum(setB[i] - setB[j]))
				totalDistance++;

		}
		return ((double) totalDistance) / ((double) comparisons);
	}
}