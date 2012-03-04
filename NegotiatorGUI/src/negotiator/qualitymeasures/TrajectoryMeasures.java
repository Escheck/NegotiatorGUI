package negotiator.qualitymeasures;

import java.util.ArrayList;
import negotiator.analysis.BidPoint;
import negotiator.xml.SimpleElement;

/**
 * This class is an implementation of the trajectory measures discussed by
 * Hindriks et al. Extend calculateMeasures to add your own measures.
 * 
 * NOTE: currently there is a bug in Genius which results in the last bid
 * not being saved correctly. Therefore it is advised to remove the last
 * before calculating the measures below.
 * 
 * @author Mark Hendrikx
 * @contact m.j.c.hendrikx@student.tudelft.nl
 */
public class TrajectoryMeasures {

	ArrayList<BidPoint> agentABids;
	ArrayList<BidPoint> agentBBids;
	boolean agentAFirst;

	public TrajectoryMeasures(ArrayList<BidPoint> agentABids,
			ArrayList<BidPoint> agentBBids) {
		this.agentABids = agentABids;
		this.agentBBids = agentBBids;
	}

	/**
	 * Calculates the percentage of unfortunate moves.
	 * @param isAgentA is true if this is agent A
	 * @return percentage of unfortunate moves
	 */
	public double calculatePercentageUnfortunateMoves(boolean isAgentA) {
		ArrayList<BidPoint> bids = agentABids;
		if (!isAgentA) {
			bids = agentBBids;
		}

		if (bids.size() > 1) {
			int unfortunateMoves = 0;
			BidPoint prev = bids.get(0);
			
			for (int i = 1; i < bids.size(); i++) {
				if (bids.get(i).utilityA < prev.utilityA &&
						bids.get(i).utilityB < prev.utilityB) {
					unfortunateMoves++;
				}
				prev = bids.get(i);
			}
			// -1 since we are looking in between bids.
			return (double)unfortunateMoves / ((double)bids.size() - 1);
		} else {
			return 0;
		}
	}
	
	/**
	 * Returns an XML representation of all trajectory based quality measures.
	 * Extend this method with your own metrics.
	 * 
	 * @param utilA utility of agreement for party A
	 * @param utilB utility of agreement for party B
	 * @return XML representation of the quality measures.
	 */
	public SimpleElement calculateMeasures() {
		SimpleElement tjQualityMeasures = new SimpleElement("trajactory_based_quality_measures");
		
		SimpleElement agentA = new SimpleElement("trajectory");
		tjQualityMeasures.addChildElement(agentA);
		agentA.setAttribute("agent", "A");
		agentA.setAttribute("unfortunate_moves", calculatePercentageUnfortunateMoves(true) + "");
		
		SimpleElement agentB = new SimpleElement("trajectory");
		tjQualityMeasures.addChildElement(agentB);
		agentB.setAttribute("agent", "B");
		agentB.setAttribute("unfortunate_moves", calculatePercentageUnfortunateMoves(false) + "");
		
		return tjQualityMeasures;
	}
}
