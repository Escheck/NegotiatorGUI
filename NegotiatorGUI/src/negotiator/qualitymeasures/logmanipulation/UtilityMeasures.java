package negotiator.qualitymeasures.logmanipulation;

import java.util.List;
import negotiator.analysis.BidPoint;
import negotiator.analysis.BidSpace;
import negotiator.qualitymeasures.UtilspaceTools;
import negotiator.xml.OrderedSimpleElement;

/**
 * Class used to calculate utility-based measures relating to
 * the quality of the outcome. Extend calculateMeasures to add
 * new measures.
 * 
 * @author Mark Hendrikx (M.J.C.Hendrikx@student.tudelft.nl)
 */
public class UtilityMeasures {

	private BidSpace bidSpace;
	
	public UtilityMeasures(BidSpace bidSpace) {
		this.bidSpace = bidSpace;
	}
	
	/**
	 * Calculates the Nash distance given the agreement.
	 * @param utilA utility of agreement for party A
	 * @param utilB utility of agreement for party B
	 * @return Nash distance
	 */
	private double calculateNashDistance(double utilA, double utilB) {
		double nashDistance = 0;
		
		try {
			BidPoint nash = bidSpace.getNash();
			double nashA = nash.getUtilityA();
			double nashB = nash.getUtilityB();
			nashDistance = UtilspaceTools.distanceBetweenTwoPoints(nashA, nashB, utilA, utilB);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return nashDistance;
	}
	
	/**
	 * Calculates the Kalai distance given the agreement.
	 * @param utilA utility of agreement for party A
	 * @param utilB utility of agreement for party B
	 * @return Nash distance
	 */
	private double calculateKalaiSmorodinskyDistance(double utilA, double utilB) {
		double kalaiDistance = 0;
		try {
			BidPoint kalai = bidSpace.getKalaiSmorodinsky();
			double kalaiA = kalai.getUtilityA();
			double kalaiB = kalai.getUtilityB();
			kalaiDistance = UtilspaceTools.distanceBetweenTwoPoints(kalaiA, kalaiB, utilA, utilB);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return kalaiDistance;
	}
	
	/**
	 * Calculates the Pareto distance given the agreement.
	 * 
	 * @param utilA utility of agreement for party A
	 * @param utilB utility of agreement for party B
	 * @return Pareto distance
	 */
	private double calculateParetoDistance(double utilA, double utilB) {
		double paretoDistance = 2.0;
		try {
			List<BidPoint> bids = bidSpace.getParetoFrontier();

			for (BidPoint bid : bids) {
				double dist = UtilspaceTools.distanceBetweenTwoPoints(bid.getUtilityA(), bid.getUtilityB(), utilA, utilB);
				if (dist < paretoDistance) {
					paretoDistance = dist;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return paretoDistance;
	}
	
	private double calculateSocialWelfare(double utilA, double utilB) {
		return utilA + utilB;
	}

	/**
	 * Returns an XML representation of all utility based quality measures.
	 * Extend this method to add new measures.
	 * 
	 * @param utilA utility of agreement for party A
	 * @param utilB utility of agreement for party B
	 * @return XML representation of the quality measures.
	 */
	public OrderedSimpleElement calculateMeasures(double utilA, double utilB) {
		OrderedSimpleElement omQualityMeasures = new OrderedSimpleElement("utility_based_quality_measures");
		
		omQualityMeasures.setAttribute("nash_distance", calculateNashDistance(utilA, utilB) + "");
		omQualityMeasures.setAttribute("pareto_distance", calculateParetoDistance(utilA, utilB) + "");
		omQualityMeasures.setAttribute("kalai_distance", calculateKalaiSmorodinskyDistance(utilA, utilB) + "");
		omQualityMeasures.setAttribute("social_welfare", calculateSocialWelfare(utilA, utilB) + "");
		
		return omQualityMeasures;
	}
}