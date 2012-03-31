package negotiator.qualitymeasures;

import java.util.ArrayList;
import negotiator.analysis.BidPoint;
import negotiator.xml.SimpleElement;

/**
 * This class is an implementation of the trajectory measures discussed by
 * Hindriks et al. in Negotiation Dynamics: Analysis, Concession Tactics, and Outcomes.
 * 
 * NOTE: currently there is a bug in Genius which results in the last bid
 * not being saved correctly. Therefore it is advised to remove the last
 * before calculating the measures below.
 * 
 * @author Mark Hendrikx and Alexander Dirkzwager
 * @contact m.j.c.hendrikx@student.tudelft.nl
 */
public class TrajectoryMeasures {

	ArrayList<BidPoint> agentABids;
	ArrayList<BidPoint> agentBBids;
	boolean agentAFirst;
	private final double SILENTTHRESHOLD = 0;
	double unfortunateA;
	double unfortunateB;
	double silentA;
	double silentB;
	double niceA;
	double niceB;
	double fortunateA;
	double fortunateB;
	double selfishA;
	double selfishB;
	double concessionA;
	double concessionB;

	public TrajectoryMeasures(ArrayList<BidPoint> agentABids,
			ArrayList<BidPoint> agentBBids) {
		this.agentABids = agentABids;
		this.agentBBids = agentBBids;
	}

	/**
	 * Define the type of move for a single pair of bids. Note that the utility
	 * of agent B is swapped relative to agent A.
	 * @param prevBid
	 * @param bid
	 */
	public void processBid(BidPoint prevBid, BidPoint bid, boolean isAgentA) {
		
		double utilMine = bid.utilityA;
		double utilTheirs = bid.utilityB;
		double prevUtilMine = prevBid.utilityA;
		double prevUtilTheirs = prevBid.utilityB;
		if (!isAgentA) {
			utilMine = bid.utilityB;
			utilTheirs = bid.utilityA;
			prevUtilMine = prevBid.utilityB;
			prevUtilTheirs = prevBid.utilityA;
		}

		if (Math.abs(utilMine - prevUtilMine) <= SILENTTHRESHOLD && Math.abs(utilTheirs - prevUtilTheirs) <= SILENTTHRESHOLD) {
			if (isAgentA)
				silentA++;
			else
				silentB++;
		} else if (utilTheirs > prevUtilTheirs && Math.abs(utilMine - prevUtilMine) <= SILENTTHRESHOLD) {
			if (isAgentA)
				niceA++;
			else
				niceB++;
		} else if (utilMine <= prevUtilMine && utilTheirs < prevUtilTheirs) {
			if (isAgentA)
				unfortunateA++;
			else
				unfortunateB++;
		} else if (utilMine > prevUtilMine && utilTheirs <= prevUtilTheirs) {
			if (isAgentA)
				selfishA++;
			else
				selfishB++;
		} else if (utilMine < prevUtilMine && utilTheirs >= prevUtilTheirs) {
			if (isAgentA)
				concessionA++;
			else
				concessionB++;
		} else {
			if (isAgentA)
				fortunateA++;
			else
				fortunateB++;
		}
	}
	
	/**
	 * Determine the move type of each bid.
	 */
	public void processAllBids() {
		BidPoint prevBidA = agentABids.get(0);
		for (int i = 1; i < agentABids.size(); i++) {
			BidPoint bidA = agentABids.get(i);
			processBid(prevBidA, bidA, true);
			prevBidA = bidA;
		}
		
		BidPoint prevBidB = agentBBids.get(0);
		for (int i = 1; i < agentBBids.size(); i++) {
			BidPoint bidB = agentBBids.get(i);
			processBid(prevBidB, bidB, false);
			prevBidB = bidB;
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
		
		unfortunateA = 0;
		unfortunateB = 0;
		silentA = 0;
		silentB = 0;
		niceA = 0;
		niceB = 0;
		fortunateA = 0;
		fortunateB = 0;
		selfishA = 0;
		selfishB = 0;
		concessionA = 0;
		concessionB = 0;
		
		// -1 because we are looking inbetween bids
		int sizeA = agentABids.size() - 1;
		int sizeB = agentBBids.size() - 1;
		
		if (sizeA > 0 && sizeB > 0) {
			processAllBids();
		}
	
		SimpleElement agentA = new SimpleElement("trajectory");
		tjQualityMeasures.addChildElement(agentA);
		agentA.setAttribute("agent", "A");
		agentA.setAttribute("unfortunate_moves", unfortunateA / sizeA + "");
		agentA.setAttribute("fortunate_moves", fortunateA / sizeA + "");
		agentA.setAttribute("nice_moves", niceA / sizeA + "");
		agentA.setAttribute("selfish_moves", selfishA / sizeA + "");
		agentA.setAttribute("silent_moves", silentA / sizeA + "");
		agentA.setAttribute("concession_moves", concessionA / sizeA + "");

		
		SimpleElement agentB = new SimpleElement("trajectory");
		tjQualityMeasures.addChildElement(agentB);
		agentB.setAttribute("agent", "B");
		agentB.setAttribute("unfortunate_moves", unfortunateB / sizeB + "");
		agentB.setAttribute("fortunate_moves", fortunateB / sizeB + "");
		agentB.setAttribute("nice_moves",  niceB / sizeB + "");
		agentB.setAttribute("selfish_moves", selfishB / sizeB + "");
		agentB.setAttribute("silent_moves", silentB / sizeB + "");
		agentB.setAttribute("concession_moves", concessionB / sizeB + "");
		
		return tjQualityMeasures;
	}
}
