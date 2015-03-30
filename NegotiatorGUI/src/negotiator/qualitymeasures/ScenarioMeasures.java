package negotiator.qualitymeasures;

import java.util.ArrayList;
import java.util.List;

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
import negotiator.Global;
import negotiator.analysis.BidPoint;
import negotiator.analysis.BidSpace;
import negotiator.exceptions.Warning;
import negotiator.utility.UtilitySpace;
import negotiator.xml.OrderedSimpleElement;

/**
 * This class is an improved version of the SpaceDistance class by D. Tykhonov.
 * Edit and run the main to automatically createFrom a XML-sheet with the scenario characteristics
 * of all scenarios in the domain file.
 * 
 * Note that the current implementation assumes that all domains only have two
 * preference profiles. For future work this could be extended by creating an arraylist
 * of all preference profiles and returning the distances between all possible pairs.
 * 
 * @author Mark Hendrikx
 * @contact m.j.c.hendrikx@student.tudelft.nl
 */
public class ScenarioMeasures {

	private static final int MONTE_CARLO_SIMULATIONS = 5000000;
	
	/**
	 * Create an XML parser to parse the domainrepository.
	 * @author Mark Hendrikx
	 */
	static class DomainParser extends DefaultHandler {

		ScenarioInfo scenario = null;
		ArrayList<ScenarioInfo> scenarios = new ArrayList<ScenarioInfo>();

		public void startElement(String nsURI, String strippedName,
				String tagName, Attributes attributes) throws SAXException {
			if (tagName.equals("domainRepItem") && attributes.getLength() > 0) {
				scenario = new ScenarioInfo(attributes.getValue("url").substring(5));
			} else if (tagName.equals("profile")) {
				if (scenario.getPrefProfA() == null) {
					scenario.setPrefProfA(attributes.getValue("url").substring(5));
				} else if (scenario.getPrefProfB() == null){
					scenario.setPrefProfB(attributes.getValue("url").substring(5));
				} else {
					System.out.println("WARNING: Violation of two preference profiles per scenario assumption for " + scenario.getDomain());
				}
			}

		}

		public void endElement(String nsURI, String strippedName,
				String tagName) throws SAXException {
			// domain is not null check is required, as the domainRepItem is used in multiple contexts
			if (tagName.equals("domainRepItem") && scenario != null) {
				scenarios.add(scenario);
				scenario = null;
			}
		}
		
		public ArrayList<ScenarioInfo> getScenarios() {
			return scenarios;
		}
	}

	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		String dir = "./";
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
		String file = dir + "scenario_info.xml";
		writeXMLtoFile(prefResults, file);
		System.out.println("Finished processing domains. Written to " + file);
	}
	
	/**
	 * Parses the domainrepository and returns a set of domain-objects containing all
	 * information.
	 * 
	 * @param dir
	 * @return set of scenario-objects
	 * @throws Exception
	 */
	private static ArrayList<ScenarioInfo> parseDomainFile(String dir) throws Exception {
		XMLReader xr = XMLReaderFactory.createXMLReader();
		DomainParser handler = new DomainParser();
		xr.setContentHandler(handler);
		xr.setErrorHandler(handler);
		xr.parse(dir + Global.DOMAIN_REPOSITORY);
		
		return handler.getScenarios();
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
	 * Calculate all metrics. This method should be extended if you want
	 * to add your own measures. 
	 * 
	 * @param element
	 * @param utilitySpaceA
	 * @param utilitySpaceB
	 * @return XML representation of the domain characteristics
	 */
	public static OrderedSimpleElement calculateDistances(OrderedSimpleElement element, UtilitySpace utilitySpaceA, UtilitySpace utilitySpaceB) {
		double rankingDistWeights = UtilspaceTools.getRankingDistanceOfIssueWeights(utilitySpaceA, utilitySpaceB);	
		double pearsonCorrWeights = UtilspaceTools.getPearsonCorrelationCoefficientOfIssueWeights(utilitySpaceA, utilitySpaceB);
		double rankingDistUtil = UtilspaceTools.getRankingDistanceOfBids(utilitySpaceA, utilitySpaceB, MONTE_CARLO_SIMULATIONS);
		double pearsonCorrUtil	= UtilspaceTools.getPearsonCorrelationCoefficientOfBids(utilitySpaceA, utilitySpaceB);
		BidSpace bidSpace = null;
		try {
			bidSpace = new BidSpace(utilitySpaceA, utilitySpaceB);
		} catch (Exception e) {
			e.printStackTrace();
		}
		double opposition = calculateOpposition(bidSpace, utilitySpaceA, utilitySpaceB);
		double paretoDistance = calculateAverageParetoDistance(bidSpace, utilitySpaceA, utilitySpaceB);
		int paretoBids = calculateAmountOfParetoBids(bidSpace, utilitySpaceA, utilitySpaceB);
		
		element.setAttribute("bids_count", (int)utilitySpaceA.getDomain().getNumberOfPossibleBids() + "");
		element.setAttribute("issue_count", utilitySpaceA.getDomain().getIssues().size() + "");
		element.setAttribute("ranking_distance_weights", String.valueOf(rankingDistWeights));
		element.setAttribute("pearson_correlation_coefficient_weights", String.valueOf(pearsonCorrWeights));
		element.setAttribute("ranking_distance_utility_space", String.valueOf(rankingDistUtil));
		element.setAttribute("pearson_correlation_coefficient_utility_space", String.valueOf(pearsonCorrUtil));
		element.setAttribute("relative_opposition", String.valueOf(opposition));
		element.setAttribute("bid_distribution", String.valueOf(paretoDistance));
		element.setAttribute("amount_pareto_bids", String.valueOf(paretoBids));

		return element;
	}
	
	/**
	 * Calculates how many Pareto bids are available in the scenario.
	 * 
	 * @param utilitySpaceA
	 * @param utilitySpaceB
	 * @return amount of Pareto bids
	 */
	private static int calculateAmountOfParetoBids(BidSpace bidSpace, UtilitySpace utilitySpaceA,
			UtilitySpace utilitySpaceB) {
		List<Bid> bids = null;
		try {
			bids = bidSpace.getParetoFrontierBids();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return bids.size();
	}

	/**
	 * Calculate the opposition of the domain, the distance to 1.0.
	 * This is a measure of competitiveness.
	 * 
	 * @param utilitySpaceA
	 * @param utilitySpaceB
	 * @return opposition of scenario
	 */
	private static double calculateOpposition(
			BidSpace bidSpace, UtilitySpace utilitySpaceA, UtilitySpace utilitySpaceB) {
		double result = 0;
		try {
			BidPoint kalai = bidSpace.getKalaiSmorodinsky();
			return kalai.getDistance(new BidPoint(null, 1.0, 1.0));
		} catch (Exception e) { e.printStackTrace(); }
		
		return result;
	}
	
	/**
	 * Calculate the average pareto distance of the scenario.
	 * 
	 * @param utilitySpaceA
	 * @param utilitySpaceB
	 * @return average pareto distance of the scenario
	 */
	private static double calculateAverageParetoDistance(BidSpace bidSpace,
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