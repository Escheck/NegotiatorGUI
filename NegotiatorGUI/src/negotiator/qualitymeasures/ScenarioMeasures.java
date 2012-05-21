package negotiator.qualitymeasures;

import java.util.ArrayList;
import java.util.Collections;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

import negotiator.Bid;
import negotiator.Domain;
import negotiator.analysis.BidPoint;
import negotiator.analysis.BidSpace;
import negotiator.analysis.BidSpaceCash;
import negotiator.exceptions.Warning;
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
					System.out.println("WARNING: Violation of two preference profiles per domain assumption for " + strippedName);
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
	 * Calculate all metrics. This method should be extended if you want
	 * to add your own measures. 
	 * 
	 * @param element
	 * @param utilitySpaceA
	 * @param utilitySpaceB
	 * @return
	 */
	public static OrderedSimpleElement calculateDistances(OrderedSimpleElement element, UtilitySpace utilitySpaceA, UtilitySpace utilitySpaceB) {
		double rankingDistWeights = UtilspaceTools.getRankingDistanceOfIssueWeights(utilitySpaceA, utilitySpaceB);	
		double pearsonCorrWeights = UtilspaceTools.getPearsonCorrelationCoefficientOfIssueWeights(utilitySpaceA, utilitySpaceB);
		double rankingDistUtil = UtilspaceTools.getRankingDistanceOfBids(utilitySpaceA, utilitySpaceB);
		double pearsonCorrUtil	= UtilspaceTools.getPearsonCorrelationCoefficientOfBids(utilitySpaceA, utilitySpaceB);
		double opposition = calculateOpposition(utilitySpaceA, utilitySpaceB);
		String listOfParetoBids = createListOfParetoBids(utilitySpaceA, utilitySpaceB);
		
		element.setAttribute("bids_count", utilitySpaceA.getDomain().getNumberOfPossibleBids() + "");
		element.setAttribute("issue_count", utilitySpaceA.getDomain().getIssues().size() + "");
		element.setAttribute("ranking_distance_weights", String.valueOf(rankingDistWeights));
		element.setAttribute("pearson_correlation_coefficient_weights", String.valueOf(pearsonCorrWeights));
		element.setAttribute("ranking_distance_utility_space", String.valueOf(rankingDistUtil));
		element.setAttribute("pearson_correlation_coefficient_utility_space", String.valueOf(pearsonCorrUtil));
		element.setAttribute("relative_opposition", String.valueOf(opposition));
		element.setAttribute("list_of_pareto_bids", listOfParetoBids);

		return element;
	}
	
	private static String createListOfParetoBids(UtilitySpace utilitySpaceA,
			UtilitySpace utilitySpaceB) {
		BidSpace space = null;
		try {
			space = new BidSpace(utilitySpaceA, utilitySpaceB, false);
		} catch (Exception e) {
			e.printStackTrace();
		}
		ArrayList<Bid> bids = null;
		try {
			bids = space.getParetoFrontierBids();
		} catch (Exception e) {
			e.printStackTrace();
		}
		Collections.sort(bids, new BidSorter());
		
		String bidStr = "";
		for (int i = 0; i < bids.size(); i++) {
			bidStr += bids.get(i) + " ";
		}
		return bidStr;
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
}