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
import negotiator.exceptions.Warning;
import negotiator.issue.Issue;
import negotiator.issue.IssueDiscrete;
import negotiator.issue.Objective;
import negotiator.utility.Evaluator;
import negotiator.utility.EvaluatorDiscrete;
import negotiator.utility.UtilitySpace;
import negotiator.xml.SimpleElement;

/**
 * The domain generator automatically generates domain given a requested
 * value for opposition and bid distribution. The lower the opposition,
 * the more win-win solutions are available. The lower the bid distribution,
 * the more close bids are on average to the Pareto-frontier.
 * 
 * Generating a domain can take a few seconds for small domains (< 5000 bids)
 * and up to an hour for large domains (> 200.000 bids) depending on the requested
 * configuration.
 * 
 * Instructions on how to class can be found in the main method.
 * 
 * Note that this class can be easily extended to include other metrics to
 * which the generated domain must adhere.
 * 
 * @author Mark Hendrikx
 */
public class DomainGenerator {
	
	/** Range in which the opposition of the generated scenario may lie. */
	private static Range OPPOSITION = new Range(0.05, 0.20);
	/** Range in which the bid distribution of the generated scenario may lie. */
	private static Range BID_DISTRIBUTION = new Range(0.35, 0.50);
	
	/**
	 * This method generates a utility space for a given domain.
	 * Please follow the numbered steps inside this method.
	 * 
	 * @param args is ignored.
	 * @throws Exception when something goes wrong when storing the new domain.
	 */
	public static void main(String[] args) throws Exception {
		// 1. Specify the path to the directory in which the domain files are located
		String dir = "c:/Users/Mark/workspace/Genius/etc/AccuracyTestSet/Thompson/";
		
		// 2. Specify the path to the domain XML file
		Domain domain = new Domain(dir + "thompson_employment.xml");
		
		// 3. Specify the baseline utility spaces
		UtilitySpace utilitySpaceA =  new UtilitySpace(domain, dir + "thompson_employee.xml");
		UtilitySpace utilitySpaceB =  new UtilitySpace(domain, dir + "thompson_employer.xml");
		
		// 4. Specify the name and path to which the new domain must be saved
		String logToDirA = dir + "thompson_employee_hOhD.xml";
		String logToDirB = dir + "thompson_employer_hOhD.xml";
		
		// 5. Start the search. The last parameter indicates if both preference profiles should be modified, or
		// solely the B side. Setting this parameter to true may lead to earlier results at the cost of having two
		// new preference profiles instead of solely the B side. The before last parameter is used to bias the
		// search by using a strictly incorrect method to normalize the weights. For domains with a high opposition
		// it was noted that setting this value to true may lead to earlier results.
		findDomain(domain, utilitySpaceA, utilitySpaceB, logToDirA, logToDirB, OPPOSITION, BID_DISTRIBUTION, false, false);
	}

	/**
	 * Method which keeps generating new domains until a domain satisfying the bounds
	 * on the opposition and bid distribution is found.
	 * 
	 * @param domain for which the profile should be generated.
	 * @param spaceA preference profile of side A.
	 * @param spaceB preference profile of side B.
	 * @param logToDirA directory to log the new side A profile.
	 * @param logToDirB directory to log the new side B profile.
	 * @param opp range for opposition.
	 * @param dist range for bid distribution.
	 * @param biasForHighOpp bias search method to find domains with a high opposition faster.
	 * @param varyBoth if false then solely a new preference profile for the B side is created.
	 * @throws Exception when something goes wrong when storing the new domain.
	 */
	public static void findDomain(Domain domain, UtilitySpace spaceA, UtilitySpace spaceB, String logToDirA, String logToDirB, Range opp, Range dist, boolean biasForHighOpp, boolean varyBoth) throws Exception {
		System.out.println("Starting random domain generator");

		double[] result = {Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY};
		boolean found = false;

		// while no satisfying scenario is found
		while (!found) {
			// vary utility space A if both should be varied
			if (varyBoth) {
				randomizeUtilSpace(spaceA, biasForHighOpp);
			}
			// OR only the second utility space
			randomizeUtilSpace(spaceB, biasForHighOpp);
			result = calculateDistances(spaceA, spaceB);

			// if the scenario satisfies the bounds, then a result is found
			if (result[0] >= opp.getLowerbound() && result[0] < opp.getUpperbound() &&
					result[1] >= dist.getLowerbound() && result[1] < dist.getUpperbound()) {
				found = true;
			}
			result = null;
		}
		if (!varyBoth) {
			JOptionPane.showMessageDialog(null, "saved to: " + logToDirB);
		} else {
			JOptionPane.showMessageDialog(null, "saved to: " + logToDirA + "\n and " + logToDirB);
		}
		if (varyBoth) {
			writeXMLtoFile(spaceA.toXML(), logToDirA);
		}
		writeXMLtoFile(spaceB.toXML(), logToDirB);
	}
	
	/**
	 * Method which randomizes a given utility space. If the bias parameter is true,
	 * then the result may be more likely to be a profile with a high opposition.
	 * 
	 * @param utilitySpace profile to be randomized.
	 * @param bias towards domains with a high opposition.
	 */
	private static void randomizeUtilSpace(UtilitySpace utilitySpace, boolean bias) 
	{
		if (bias) {
			for(Issue i : utilitySpace.getDomain().getIssues()){
				utilitySpace.setWeight(i, Math.random());
			}
			utilitySpace.normalizeChildren(utilitySpace.getDomain().getIssues().get(0).getParent());
		} else {
			for(Issue i : utilitySpace.getDomain().getIssues()){
				setWeightSimple(utilitySpace, i, Math.random());
			}
			utilitySpace.normalizeChildren(utilitySpace.getDomain().getIssues().get(0).getParent());
		}

		try{
			for(Entry<Objective, Evaluator> e: utilitySpace.getEvaluators()){
				( (EvaluatorDiscrete)e.getValue() ).setEvaluation(utilitySpace.getDomain().getRandomBid().getValue(((IssueDiscrete)e.getKey()).getNumber()), 
					(int)(Math.random() * 1000));
			}
		} catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	/**
	 * Method which sets the weight of an issue without checking
	 * normalization. This is faster than setWeightSimple if normalization
	 * is ensured.
	 * 
	 * @param objective of which the weights must be set.
	 * @param weight to which the weight of the objective must be set.
	 */
	public static void setWeightSimple(UtilitySpace uspace, Issue i, double weight)
	{
		try
		{
			Evaluator ev =uspace.getEvaluator(i.getNumber());
			ev.setWeight(weight); //set weight
		}catch(NullPointerException e){
			e.printStackTrace();
		}
	}

	/**
	 * Stores an XML-element to a file.
	 * 
	 * @param simpleElement to be stored.
	 * @param logPath path to log the XML file.
	 */
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
	 * @param utilitySpaceA utility space of side A.
	 * @param utilitySpaceB utility space of side B.
	 * @return the opposition (position 0) and bid distribution (position 1) of a scenario.
	 */
	private static double[] calculateDistances(UtilitySpace utilitySpaceA, UtilitySpace utilitySpaceB) {
		BidSpace bidSpace = null;
		try {
			bidSpace = new BidSpace(utilitySpaceA, utilitySpaceB);
		} catch (Exception e) {
			e.printStackTrace();
		}
		double opposition = calculateOpposition(bidSpace);
		double bidDistribution = calculateBidDistribution(bidSpace, utilitySpaceA, utilitySpaceB);
		
		double[] result = new double[2];
		result[0] = opposition;
		result[1] = bidDistribution;
		return result;
	}
	
	/**
	 * Calculate the opposition of the domain, the distance to 1.0.
	 * This is a measure of competitiveness.
	 * 
	 * @param utilitySpaceA utility space of side A.
	 * @param utilitySpaceB utility space of side B.
	 * @return opposition of the given scenario.
	 */
	private static double calculateOpposition(BidSpace bidSpace) {
		double result = 0;
		try {
			BidPoint kalai = bidSpace.getKalaiSmorodinsky();
			return kalai.getDistance(new BidPoint(null, 1.0, 1.0));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return result;
	}

	/**
	 * Calculate the bid distribution of the scenario.
	 * 
	 * @param utilitySpaceA utility space of side A.
	 * @param utilitySpaceB utility space of side B.
	 * @return bid distribution of the given scenario.
	 */
	private static double calculateBidDistribution(BidSpace bidSpace,
			UtilitySpace utilitySpaceA, UtilitySpace utilitySpaceB) {
		BidIterator iterator = new BidIterator(utilitySpaceA.getDomain());
		double total = 0;
		try {
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