package negotiator.qualitymeasures;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Set;
import java.util.TreeSet;
import negotiator.ArrayListXML;
import negotiator.Domain;
import negotiator.analysis.BidPoint;
import negotiator.analysis.BidPointTime;
import negotiator.analysis.BidSpace;
import negotiator.xml.OrderedSimpleElement;

/**
 * This class is an implementation of the trajectory measures discussed by
 * Hindriks et al. in Negotiation Dynamics: Analysis, Concession Tactics, and Outcomes.
 * 
 * @author Mark Hendrikx and Alexander Dirkzwager
 * @contact m.j.c.hendrikx@student.tudelft.nl
 */
public class TrajectoryMeasures {

	ArrayList<BidPointTime> agentABids;
	ArrayList<BidPointTime> agentBBids;
	Domain domain;
	boolean agentAFirst;
	private final double SILENTTHRESHOLD = 0.0001;
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
	double explorationRateA;
	double explorationRateB;
	double percParetoBidsA;
	double percParetoBidsB;
	double jointExplorationRate;
	private BidSpace bidSpace;
	

	public TrajectoryMeasures(ArrayListXML<BidPointTime> arrayListXML,
			ArrayListXML<BidPointTime> arrayListXML2, BidSpace bidSpace) {
		this.agentABids = arrayListXML;
		this.agentBBids = arrayListXML2;
		this.bidSpace = bidSpace;
	}

	private boolean listContainsBidPoint(BidPoint bid, ArrayList<BidPoint> list) {
		for (int i = 0; i < list.size(); i++) {
			if (bid.getUtilityA().equals(list.get(i).getUtilityA()) &&
					bid.getUtilityB().equals(list.get(i).getUtilityB())) {
				return true;
			}
		}
		return false;
	}
	
	private void calculateExplorationRates() {
		// strictly, it can happen that multiple bids have the same utility for both parties
		ArrayList<BidPoint> setA = new ArrayList<BidPoint>();
		ArrayList<BidPoint> setB = new ArrayList<BidPoint>();
		ArrayList<BidPoint> setJoint = new ArrayList<BidPoint>();
		ArrayList<BidPoint> setAll = new ArrayList<BidPoint>();
		
		setAll.addAll(bidSpace.bidPoints);
		setAll = removeDuplicates(setAll);
		
		setA.addAll(agentABids);
		setA = removeDuplicates(setA);

		setB.addAll(agentBBids);
		setB = removeDuplicates(setB);
		
		setJoint.addAll(agentABids);
		setJoint.addAll(agentBBids);
		setJoint = removeDuplicates(setJoint);
		
		explorationRateA = (double)setA.size() / (double)setAll.size();
		explorationRateB = (double)setB.size() / (double)setAll.size();
		jointExplorationRate = (double)setJoint.size() / (double)setAll.size();
	}
	
	private void calculatePercentageParetoBids() {
		ArrayList<BidPoint> paretoBids = null;
		try {
			paretoBids = new ArrayList<BidPoint>(bidSpace.getParetoFrontier());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		int totalParetoBidsA = 0;
		for (int i = 0; i < agentABids.size(); i++) {
			BidPoint point = agentABids.get(i);
			if (listContainsBidPoint(point, paretoBids)) {
				totalParetoBidsA++;
			}
		}
		percParetoBidsA = (double) totalParetoBidsA / (double) agentABids.size();
		
		int totalParetoBidsB = 0;
		for (int i = 0; i < agentBBids.size(); i++) {
			BidPoint point = agentBBids.get(i);
			if (listContainsBidPoint(point, paretoBids)) {
				totalParetoBidsB++;
			}
		}
		percParetoBidsB = (double) totalParetoBidsB / (double) agentBBids.size();
	}
	
	/**
	 * Define the type of move for a single pair of bids. Note that the utility
	 * of agent B is swapped relative to agent A.
	 * @param prevBid
	 * @param bid
	 */
	private void processBid(BidPoint prevBid, BidPoint bid, boolean isAgentA) {
		
		double utilMine = bid.getUtilityA();
		double utilTheirs = bid.getUtilityB();
		double prevUtilMine = prevBid.getUtilityA();
		double prevUtilTheirs = prevBid.getUtilityB();
		if (!isAgentA) {
			utilMine = bid.getUtilityB();
			utilTheirs = bid.getUtilityA();
			prevUtilMine = prevBid.getUtilityB();
			prevUtilTheirs = prevBid.getUtilityA();
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
	private void processAllBids() {
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
	public OrderedSimpleElement calculateMeasures() {
		OrderedSimpleElement tjQualityMeasures = new OrderedSimpleElement("trajactory_based_quality_measures");
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
		
		OrderedSimpleElement agentA = new OrderedSimpleElement("trajectory");
		OrderedSimpleElement agentB = new OrderedSimpleElement("trajectory");
		if (sizeA > 0 && sizeB > 0) {
			processAllBids();
			
			tjQualityMeasures.addChildElement(agentA);
			agentA.setAttribute("agent", "A");
			agentA.setAttribute("unfortunate_moves", unfortunateA / sizeA + "");
			agentA.setAttribute("fortunate_moves", fortunateA / sizeA + "");
			agentA.setAttribute("nice_moves", niceA / sizeA + "");
			agentA.setAttribute("selfish_moves", selfishA / sizeA + "");
			agentA.setAttribute("silent_moves", silentA / sizeA + "");
			agentA.setAttribute("concession_moves", concessionA / sizeA + "");

			tjQualityMeasures.addChildElement(agentB);
			agentB.setAttribute("agent", "B");
			agentB.setAttribute("unfortunate_moves", unfortunateB / sizeB + "");
			agentB.setAttribute("fortunate_moves", fortunateB / sizeB + "");
			agentB.setAttribute("nice_moves",  niceB / sizeB + "");
			agentB.setAttribute("selfish_moves", selfishB / sizeB + "");
			agentB.setAttribute("silent_moves", silentB / sizeB + "");
			agentB.setAttribute("concession_moves", concessionB / sizeB + "");
		} else {
			tjQualityMeasures.addChildElement(agentA);
			agentA.setAttribute("agent", "A");
			agentA.setAttribute("unfortunate_moves", "0");
			agentA.setAttribute("fortunate_moves", "0");
			agentA.setAttribute("nice_moves", "0");
			agentA.setAttribute("selfish_moves", "0");
			agentA.setAttribute("silent_moves", "0");
			agentA.setAttribute("concession_moves", "0");

			tjQualityMeasures.addChildElement(agentB);
			agentB.setAttribute("agent", "B");
			agentB.setAttribute("unfortunate_moves", "0");
			agentB.setAttribute("fortunate_moves", "0");
			agentB.setAttribute("nice_moves", "0");
			agentB.setAttribute("selfish_moves", "0");
			agentB.setAttribute("silent_moves", "0");
			agentB.setAttribute("concession_moves", "0");
		}
		
		calculateExplorationRates();
		agentA.setAttribute("exploration_rate", explorationRateA + "");
		agentB.setAttribute("exploration_rate", explorationRateB + "");
		agentA.setAttribute("joint_exploration_rate", jointExplorationRate + "");
		agentB.setAttribute("joint_exploration_rate", jointExplorationRate + "");
		
		calculatePercentageParetoBids();
		agentA.setAttribute("perc_pareto_bids", percParetoBidsA + "");
		agentB.setAttribute("perc_pareto_bids", percParetoBidsB + "");
		
		return tjQualityMeasures;
	}
	
	public ArrayList<BidPoint> removeDuplicates(ArrayList<BidPoint> list) {
	    // ... the list is already populated
	    Set<BidPoint> s = new TreeSet<BidPoint>(new Comparator<BidPoint>() {

	        @Override
	        public int compare(BidPoint o1, BidPoint o2) {
	        	if (o1.getUtilityA().equals(o2.getUtilityA()) && o1.getUtilityB().equals(o2.getUtilityB())) {
	        		return 0;
	        	}
	        	return -1;
	        }
	    });
	    s.addAll(list);
	    return new ArrayList<BidPoint>(s);
	}
}
