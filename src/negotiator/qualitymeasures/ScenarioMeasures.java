package negotiator.qualitymeasures;

import java.util.ArrayList;
import java.util.Random;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;
import negotiator.Bid;
import negotiator.BidIterator;
import negotiator.Domain;
import negotiator.analysis.BidPoint;
import negotiator.analysis.BidSpace;
import negotiator.analysis.BidSpaceCash;
import negotiator.exceptions.Warning;
import negotiator.issue.Issue;
import negotiator.utility.UtilitySpace;
import negotiator.xml.OrderedSimpleElement;

/**
 * This class is an improved version of the SpaceDistance class by Tykhonov.
 * Edit and run the main to automatically create a XML-sheet with the domain characteristics
 * of all domains in the domain file.
 * 
 * Note that the current implementation assumes that all domains only have two
 * preference profiles. For future work this could be extended by creating an arraylist
 * of all preference profiles and returning the distances between all possible pairs.
 * 
 * @author Mark Hendrikx
 * @contact m.j.c.hendrikx@student.tudelft.nl
 */
public class ScenarioMeasures {
	
	// if the amount of bids is larger or equal to this value, exact calculation
	// takes to long and an estimation procedure is used.
	static final int MAX_SIZE_FOR_EXACT_CALCULATION = 100000;
	// how many times the estimation procedure should be repeated.
	// Higher amount of simulations results in better estimate.
	static final int AMOUNT_OF_SIMULATIONS = 10000000;
	
	/**
	 * Create an XML parser to parse the domainrepository.
	 * @author Mark Hendrikx
	 */
	static class DomainParser extends DefaultHandler {

		ScenarioInfo domain = null;
		ArrayList<ScenarioInfo> domains = new ArrayList<ScenarioInfo>();

		public void startElement(String nsURI, String strippedName,
				String tagName, Attributes attributes) throws SAXException {
			if (tagName.equals("domainRepItem") && attributes.getLength() > 0) {
				domain = new ScenarioInfo(attributes.getValue("url").substring(5));
			} else if (tagName.equals("profile")) {
				if (domain.getPrefProfA() == null) {
					domain.setPrefProfA(attributes.getValue("url").substring(5));
				} else if (domain.getPrefProfB() == null){
					domain.setPrefProfB(attributes.getValue("url").substring(5));
				} else {
					System.out.println("WARNING: Violation of two preference profiles per domain assumption.");
				}
			}

		}

		public void endElement(String nsURI, String strippedName,
				String tagName) throws SAXException {
			// domain is not null check is required, as the domainRepItem is used in multiple contexts
			if (tagName.equals("domainRepItem") && domain != null) {
				domains.add(domain);
				domain = null;
			}
		}
		
		public ArrayList<ScenarioInfo> getDomains() {
			return domains;
		}
	}

	/**
	 * 
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		String dir = "c:/Users/Mark/workspace/Genius/";
		process(dir);
	}

	/**
	 * Calculates all metrics and saves the results. This is NOT the place
	 * to add mew metrics.
	 * 
	 * @param dir path to Genius main dir
	 * @throws Exception
	 */
	public static void process(String dir) throws Exception {
		ArrayList<ScenarioInfo> domains = parseDomainFile(dir);
		
		OrderedSimpleElement prefResults = new OrderedSimpleElement("preference_profiles_statistics");
		for (ScenarioInfo domainSt : domains) {
			Domain domain = new Domain(dir + domainSt.getDomain());
			UtilitySpace utilitySpaceA, utilitySpaceB;
			utilitySpaceA =  new UtilitySpace(domain, dir + domainSt.getPrefProfA());
			utilitySpaceB =  new UtilitySpace(domain, dir + domainSt.getPrefProfB());
			OrderedSimpleElement results = new OrderedSimpleElement("domain_result");
			results.setAttribute("domain", domainSt.getDomain());
			results.setAttribute("profileA", domainSt.getPrefProfA());
			results.setAttribute("profileB", domainSt.getPrefProfB());
			
			calculateDistances(results, utilitySpaceA, utilitySpaceB);
			
			prefResults.addChildElement(results);
			System.out.println("Processed domain: " + domain.getName() + " \t [" + utilitySpaceA.getFileName() + " , " + utilitySpaceB.getFileName() + "]");
		}
		writeXMLtoFile(prefResults, dir + "domain_info.xml");
		System.out.println("Finished processing domains");
	}
	
	/**
	 * Parses the domainrepository and returns a set of domain-objects containing all
	 * information.
	 * 
	 * @param dir
	 * @return set of domain-objects
	 * @throws Exception
	 */
	private static ArrayList<ScenarioInfo> parseDomainFile(String dir) throws Exception {
		XMLReader xr = XMLReaderFactory.createXMLReader();
		DomainParser handler = new DomainParser();
		xr.setContentHandler(handler);
		xr.setErrorHandler(handler);
		xr.parse(dir + "domainrepository.xml");
		
		return handler.getDomains();
	}
	
	/**
	 * Write the results to an output file.
	 * 
	 * @param results to be written
	 * @param logPath
	 */
	private static void writeXMLtoFile(OrderedSimpleElement results, String logPath) {
		try {
			File log = new File(logPath);
			if (log.exists()) {
				log.delete();
			}
			BufferedWriter out = new BufferedWriter(new FileWriter(log, true));
			out.write("" + results);
			out.close();
		} catch (Exception e) {
			new Warning("Exception during writing s:" + e);
			e.printStackTrace();
		}
	}
	
	/**
	 * Helper-method used to get the issue weights in an array of doubles.
	 * @param utilityspace
	 * @return array of issue weights
	 */
	private static double[] getIssueWeights(UtilitySpace space) {
		double issueWeights[] = new double[space.getDomain().getIssues().size()];

		int i = 0;
		for(Issue issue : space.getDomain().getIssues()) {
			issueWeights[i] = space.getWeight(issue.getNumber());
			i++;
		}
		return issueWeights;
	}
	
	/**
	 * Helper-method used to get the the utilities of all possible bids in an array
	 * of doubles.
	 * 
	 * @param utilityspace
	 * @return array of utilities
	 */
	private static double[] getBidsUtil(UtilitySpace space) {
		double bidsUtil[] = new double[(int)(space.getDomain().getNumberOfPossibleBids())];
		BidIterator lIter = new BidIterator( space.getDomain());
		
		int i = 0;
		while(lIter.hasNext()) {
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
	 * Calculate all metrics. This method should be extended if you want
	 * to add your own measures. 
	 * 
	 * @param element
	 * @param utilitySpaceA
	 * @param utilitySpaceB
	 * @return
	 */
	public static OrderedSimpleElement calculateDistances(OrderedSimpleElement element, UtilitySpace utilitySpaceA, UtilitySpace utilitySpaceB) {
		double issueWeightsA[] = getIssueWeights(utilitySpaceA);
		double issueWeightsB[] = getIssueWeights(utilitySpaceB);
		double bidsUtilA[] = getBidsUtil(utilitySpaceA);
		double bidsUtilB[] = getBidsUtil(utilitySpaceB);
		
		double rankingDistUtil;
		if (bidsUtilA.length <= MAX_SIZE_FOR_EXACT_CALCULATION) {
			rankingDistUtil = calculateRankingDistance(bidsUtilA, bidsUtilB);
		} else {
			rankingDistUtil = calculateRankingDistanceMonteCarlo(bidsUtilA, bidsUtilB);
		}
		double rankingDistWeights = calculateRankingDistance(issueWeightsA, issueWeightsB);
		double pearsonDistUtil	= calculatePearsonDistance(bidsUtilA,bidsUtilB);
		double pearsonDistWeights = calculatePearsonDistance(issueWeightsA, issueWeightsB);
		double kalaiDistance = calculateRelativeKalaiDistance(utilitySpaceA, utilitySpaceB);
		
		element.setAttribute("bids_count", String.valueOf(bidsUtilA.length));
		element.setAttribute("issue_count", String.valueOf(issueWeightsA.length));
		element.setAttribute("ranking_distance_utility_space", String.valueOf(rankingDistUtil));
		element.setAttribute("ranking_distance_weights", String.valueOf(rankingDistWeights));
		element.setAttribute("pearson_distance_utility_space", String.valueOf(pearsonDistUtil));
		element.setAttribute("pearson_distance_weights", String.valueOf(pearsonDistWeights));
		element.setAttribute("relative_kalai_distance", String.valueOf(kalaiDistance));

		return element;
	}
	
	/**
	 * Calculate the relative Kalai distance. This is the positive
	 * or negative distance to the point [0.5; 0.5]. This is a measure
	 * of competitiveness.
	 * 
	 * @param utilitySpaceA
	 * @param utilitySpaceB
	 * @return
	 */
	private static double calculateRelativeKalaiDistance(
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
			result = kalai.distanceTo(new BidPoint(null, 0.5, 0.5));
			if ((kalai.utilityA + kalai.utilityB) < 1.0) {
				result = (-result);
			}
			return result;
		} catch (Exception e) { e.printStackTrace(); }
		
		return result;
	}

	/**
	 * Calculate the Pearson distance between two sets.
	 * 
	 * @param setA
	 * @param setB
	 * @return
	 */
	public static double calculatePearsonDistance(double[] setA, double[] setB) {
		if (setA.length != setB.length) {
			System.out.println("Amount of variables should be equal.");
		}
		
		double averageSetA = 0, averageSetB = 0;
		double sumA = 0, sumB = 0;
		
		//calculate average values
		for(int i = 0; i < setA.length; i++) {
			sumA += setA[i];
			sumB += setB[i];
		}
		averageSetA = (double)sumA / (double)setA.length;
		averageSetB = (double)sumB / (double)setB.length;
		
		//calculate the distance itself
		double nominator = 0;
		double sumSquareNormA = 0;
		double sumSquareNormB = 0;
		for(int i = 0; i < setA.length; i++) { 
			double normA = setA[i] - averageSetA;
			double normB = setB[i] - averageSetB;
			//System.out.println(normB);
			nominator += (normA * normB);
			
			sumSquareNormA += Math.pow(normA, 2);
			sumSquareNormB += Math.pow(normB, 2);
		}
		return nominator / (Math.sqrt(sumSquareNormA * sumSquareNormB));
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
				if (Math.signum(setA[i] - setA[j]) != 
					Math.signum(setB[i] - setB[j]))
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
	public static double calculateRankingDistanceMonteCarlo(double[] setA, double[] setB) {
		double totalDistance = 0;
		int comparisons = AMOUNT_OF_SIMULATIONS;
		
		for (int k = 0; k < comparisons; k++) {
			int i = (new Random()).nextInt(setA.length - 1);
			int j = (new Random()).nextInt(setB.length - 1);
			if (Math.signum(setA[i] - setA[j]) != 
					Math.signum(setB[i] - setB[j]))
				totalDistance++;

		}
		return ((double) totalDistance) / ((double) comparisons);
	}
}