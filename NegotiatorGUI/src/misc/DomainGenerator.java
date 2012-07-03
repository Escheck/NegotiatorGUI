package misc;

import java.util.Map.Entry;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

import javax.swing.JOptionPane;

import negotiator.Bid;
import negotiator.BidIterator;
import negotiator.Domain;
import negotiator.analysis.BidPoint;
import negotiator.analysis.BidSpace;
import negotiator.analysis.BidSpaceCash;
import negotiator.exceptions.Warning;
import negotiator.issue.Issue;
import negotiator.issue.IssueDiscrete;
import negotiator.issue.Objective;
import negotiator.utility.Evaluator;
import negotiator.utility.EvaluatorDiscrete;
import negotiator.utility.UtilitySpace;
import negotiator.xml.SimpleElement;

public class DomainGenerator {
	
	private static Range LOW_OPP = new Range(0.05, 0.20);
	private static Range MED_OPP = new Range(0.20, 0.45);
	private static Range HIGH_OPP = new Range(0.45, 0.65);
	private static Range LOW_DIST = new Range(0.05, 0.20);
	private static Range MED_DIST = new Range(0.20, 0.40);
	private static Range HIGH_DIST = new Range(0.40, 0.70);
	
	public static void main(String[] args) throws Exception {
		String dir = "c:/Users/Mark/workspace/Genius/etc/AccuracyTestSet/Travel/";
		Domain domain = new Domain(dir + "travel_domain.xml");
		UtilitySpace utilitySpaceA =  new UtilitySpace(domain, dir + "travel_chox.xml");
		UtilitySpace utilitySpaceB =  new UtilitySpace(domain, dir + "travel_fanny.xml");
		String logToDirA = dir + "travel_fanny_hOlB.xml";
		String logToDirB = dir + "travel_fanny_hOlB.xml";
		
		
		findDomain(domain, utilitySpaceA, utilitySpaceB, logToDirA, logToDirB, HIGH_OPP, LOW_DIST, true, false);
	}

	/**
	 * Calculates all metrics and saves the results. This is NOT the place
	 * to add mew metrics.
	 * 
	 * @param dir path to Genius main dir
	 * @throws Exception
	 */
	public static void findDomain(Domain domain, UtilitySpace spaceA, UtilitySpace spaceB, String logToDirA, String logToDirB, Range opp, Range dist, boolean biasForHighOpp, boolean varyBoth) throws Exception {
		System.out.println("Starting random domain generator");

		double[] result = {Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY};
		boolean found = false;

		while (!found) {
			if (varyBoth) {
				randomizeUtilSpace(spaceA, biasForHighOpp);
			}
			randomizeUtilSpace(spaceB, biasForHighOpp);
			result = calculateDistances(spaceA, spaceB);

			if (result[0] >= opp.getLowerbound() && result[0] < opp.getUpperbound() && result[1] >= dist.getLowerbound() && result[1] < dist.getUpperbound()) {
				found = true;
			}
			result = null;
		}
		JOptionPane.showMessageDialog(null, "saved to: " + logToDirB);
		if (varyBoth) {
			writeXMLtoFile(spaceA.toXML(), logToDirA);
		}
		writeXMLtoFile(spaceB.toXML(), logToDirB);
	}
	
	/**
	 * Not normalizing first works better in finding extreme domains.
	 * @param utilitySpaceB
	 */
	private static void randomizeUtilSpace(UtilitySpace utilitySpaceB, boolean bias) {
		
		if (bias) {
			for(Issue i : utilitySpaceB.getDomain().getIssues()){
				utilitySpaceB.setWeight(i, Math.random());
			}
			utilitySpaceB.normalizeChildren(utilitySpaceB.getDomain().getIssues().get(0).getParent());
		} else {
			for(Issue i : utilitySpaceB.getDomain().getIssues()){
				utilitySpaceB.setWeightSimple(i, Math.random());
			}
			utilitySpaceB.normalizeChildren(utilitySpaceB.getDomain().getIssues().get(0).getParent());
		}
		
		// Then for each issue value that has been offered last time, a constant value is added to its corresponding ValueDiscrete.
		try{
			for(Entry<Objective, Evaluator> e: utilitySpaceB.getEvaluators()){
				( (EvaluatorDiscrete)e.getValue() ).setEvaluation(utilitySpaceB.getDomain().getRandomBid().getValue(((IssueDiscrete)e.getKey()).getNumber()), 
					(int)(Math.random() * 1000));
			}
		} catch(Exception ex){
			ex.printStackTrace();
		}
	}

	private static void writeXMLtoFile(SimpleElement simpleElement, String logPath) {
		try {
			File log = new File(logPath);
			if (log.exists()) {
				log.delete();
			}
			BufferedWriter out = new BufferedWriter(new FileWriter(log, true));
			out.write("" + simpleElement);
			out.close();
		} catch (Exception e) {
			new Warning("Exception during writing s:" + e);
			e.printStackTrace();
		}
	}
	
	/**
	 * Calculate all metrics. This method should be extended if you want
	 * to add your own measures. 
	 * 
	 * @param element
	 * @param utilitySpaceA
	 * @param utilitySpaceB
	 * @return
	 */
	public static double[] calculateDistances(UtilitySpace utilitySpaceA, UtilitySpace utilitySpaceB) {
		double opposition = calculateOpposition(utilitySpaceA, utilitySpaceB);
		double bidDistribution = calculateBidDistribution(utilitySpaceA, utilitySpaceB);
		
		double[] result = new double[2];
		result[0] = opposition;
		result[1] = bidDistribution;
		return result;
	}
	
	/**
	 * Calculate the opposition of the domain, the distance to 1.0.
	 * This is a measure of competitiveness.
	 * 
	 * @param utilitySpaceA
	 * @param utilitySpaceB
	 * @return
	 */
	private static double calculateOpposition(
			UtilitySpace utilitySpaceA, UtilitySpace utilitySpaceB) {
		double result = 0;
		try {
			BidSpace bidSpace = BidSpaceCash.getBidSpace(utilitySpaceA, utilitySpaceB);
			
			if (bidSpace == null) {
				try {   
					bidSpace=new BidSpace(utilitySpaceA, utilitySpaceB);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			BidPoint kalai = bidSpace.getKalaiSmorodinsky();
			return kalai.distanceTo(new BidPoint(null, 1.0, 1.0));
		} catch (Exception e) { e.printStackTrace(); }
		
		return result;
	}
	
	/**
	 * Calculate the average pareto distance of the scenario.
	 * 
	 * @param utilitySpaceA
	 * @param utilitySpaceB
	 * @return
	 */
	private static double calculateBidDistribution(
			UtilitySpace utilitySpaceA, UtilitySpace utilitySpaceB) {
		BidIterator iterator = new BidIterator(utilitySpaceA.getDomain());
		double total = 0;
		try {
			BidSpace bidSpace = BidSpaceCash.getBidSpace(utilitySpaceA, utilitySpaceB);
			
			if (bidSpace == null) {
				try {   
					bidSpace=new BidSpace(utilitySpaceA, utilitySpaceB);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			while (iterator.hasNext()) {
				Bid bid = iterator.next();
				BidPoint point = new BidPoint(bid, utilitySpaceA.getUtility(bid), utilitySpaceB.getUtility(bid));
				total += bidSpace.distanceToNearestParetoBid(point);
			}
			return total / utilitySpaceA.getDomain().getNumberOfPossibleBids();
		} catch (Exception e) { e.printStackTrace(); }
		return -1;
	}
}