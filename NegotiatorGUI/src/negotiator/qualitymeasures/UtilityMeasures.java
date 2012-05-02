package negotiator.qualitymeasures;

import java.util.ArrayList;
import negotiator.analysis.BidPoint;
import negotiator.analysis.BidSpace;
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
			double nashA = nash.utilityA;
			double nashB = nash.utilityB;
			nashDistance = distanceBetweenTwoPoints(nashA, nashB, utilA, utilB);
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
			double kalaiA = kalai.utilityA;
			double kalaiB = kalai.utilityB;
			kalaiDistance = distanceBetweenTwoPoints(kalaiA, kalaiB, utilA, utilB);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return kalaiDistance;
	}
	
	private double distanceBetweenTwoPoints(double ax, double ay, double bx, double by) {
		return Math.sqrt((Math.pow((ax - bx), 2) + Math.pow((ay - by), 2)));
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
			ArrayList<BidPoint> bids = bidSpace.getParetoFrontier();

			for (BidPoint bid : bids) {
				double dist = distanceBetweenTwoPoints(bid.utilityA, bid.utilityB, utilA, utilB);
				if (dist < paretoDistance) {
					paretoDistance = dist;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return paretoDistance;
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
		
		return omQualityMeasures;
	}
}