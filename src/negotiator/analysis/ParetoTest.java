package negotiator.analysis;

import java.util.ArrayList;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;
import misc.Range;
import negotiator.Domain;
import negotiator.analysis.BidPoint;
import negotiator.analysis.BidSpace;
import negotiator.bidding.BidDetails;
import negotiator.boaframework.SortedOutcomeSpace;
import negotiator.qualitymeasures.ScenarioInfo;
import negotiator.utility.AdditiveUtilitySpace;

/**
 * This class can be used to test if the implementation of the Pareto frontier
 * algorithm in BidSpace returns the correct results on each domain.
 * The efficient algorithm is compared against a simple bruteforce algorithm.
 * 
 * No effort was made to optimize the bruteforce algorithm as I wanted to be
 * sure that it is correct. Therefore, it is not advised to check domains with
 * more than 200.000 bids.
 * 
 * @author Mark Hendrikx
 */
public class ParetoTest {

	/**
	 * Create an XML parser to parse the domainrepository.
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
	 * @param args are ignored.
	 * @throws Exception may occur if there are problems reading files.
	 */
	public static void main(String[] args) throws Exception {
		// 1. Path to main directory in which Genius is installed
		String dir = "c:/Users/Mark/workspace/Genius/";
		// 2. Compare the two algorithms for all domains.
		process(dir);
	}

	/**
	 * Simple method to compare if the algorithm for calculating the Pareto-bids in the
	 * BidSpace class returns the right results.
	 * 
	 * @param dir in which Genius is installed
	 * @throws Exception when an error occurs on parsing the files.
	 */
	public static void process(String dir) throws Exception {
		ArrayList<ScenarioInfo> domains = parseDomainFile(dir);
		
		for (ScenarioInfo domainSt : domains) {
			// 1. Load the domain
			Domain domain = new Domain(dir + domainSt.getDomain());
			AdditiveUtilitySpace utilitySpaceA, utilitySpaceB;
			utilitySpaceA =  new AdditiveUtilitySpace(domain, dir + domainSt.getPrefProfA());
			utilitySpaceB =  new AdditiveUtilitySpace(domain, dir + domainSt.getPrefProfB());
			
			// 2. Determine all Pareto-bids using both a bruteforce algorithm and algorithm in the BidSpace class
			ArrayList<BidPoint> realParetoBids = bruteforceParetoBids(domain, utilitySpaceA, utilitySpaceB);
			BidSpace space = new BidSpace(utilitySpaceA, utilitySpaceB);
			ArrayList<BidPoint> estimatedParetoBids = new ArrayList<BidPoint>(space.getParetoFrontier());
			
			// 3. Check if there is a difference in the output
			if (checkValidity(estimatedParetoBids, realParetoBids)) {
				System.out.println("No problems in: " + domain.getName());
			} else {
				System.out.println("Found difference in: " + domain.getName());
				System.out.println("REAL " + realParetoBids.size());
				for (int i = 0; i < realParetoBids.size(); i++) {
					System.out.println(realParetoBids.get(i).getBid() + " " + realParetoBids.get(i).getUtilityA() + " " + realParetoBids.get(i).getUtilityB());
				}
				System.out.println("ESTIMATE " + estimatedParetoBids.size());
				for (int i = 0; i < estimatedParetoBids.size(); i++) {
					System.out.println(estimatedParetoBids.get(i).getUtilityA() + " " + estimatedParetoBids.get(i).getUtilityB());
				}
			}
		}
		System.out.println("Finished processing domains");
	}
	
	/**
	 * Check if the output of the efficient algorithm and the brutefore algorithm
	 * to calculate the Pareto-optimal bids are identical.
	 * 
	 * @param estimatedParetoBids Pareto-bids as estimated by an efficient algorithm in the BidSpace class.
	 * @param realParetoBids Pareto-bids as calculated by the bruteforce algorithm.
	 * @return true if both sets contain the same Pareto-optimal bids.
	 */
	private static boolean checkValidity(ArrayList<BidPoint> estimatedParetoBids, ArrayList<BidPoint> realParetoBids) {
		if (realParetoBids.size() != estimatedParetoBids.size()) {
			return false;
		}
		for (BidPoint paretoBid : realParetoBids) {
			boolean found = false;
			for (int a = 0; a < estimatedParetoBids.size(); a++) {
				if (estimatedParetoBids.get(a).getUtilityA().equals(paretoBid.getUtilityA()) &&
						estimatedParetoBids.get(a).getUtilityB().equals(paretoBid.getUtilityB())) {
					found = true;
					break;
				}
			}
			if (!found) {
				return false;
			}
		}
		
		return true;
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
	 * Bruteforce algorithm to calculate the Pareto-bids.
	 * 
	 * @param domain
	 * @param spaceA
	 * @param spaceB
	 * @return set of Pareto-bids
	 */
	private static ArrayList<BidPoint> bruteforceParetoBids(Domain domain, AdditiveUtilitySpace spaceA, AdditiveUtilitySpace spaceB) {
		SortedOutcomeSpace outcomeSpaceA = new SortedOutcomeSpace(spaceA);
		ArrayList<BidPoint> paretoBids = new ArrayList<BidPoint>();
		try {
			for (BidDetails bid : outcomeSpaceA.getAllOutcomes()) {
				double utilA = spaceA.getUtility(bid.getBid());
				double utilB = spaceB.getUtility(bid.getBid());
				boolean found = false;
				
				for (BidDetails otherBid : outcomeSpaceA.getBidsinRange(new Range(utilA - 0.01, 1.1))) { // -0.01 as we want to include duplicates
					if ((otherBid != bid && ((spaceA.getUtility(otherBid.getBid()) > utilA &&
						spaceB.getUtility(otherBid.getBid()) >= utilB)) ||
						(otherBid != bid && spaceA.getUtility(otherBid.getBid()) >= utilA &&
						spaceB.getUtility(otherBid.getBid()) > utilB))){
						found = true;
						break;
					}
				}
				if (!found) {
					paretoBids.add(new BidPoint(bid.getBid(), bid.getMyUndiscountedUtil(), spaceB.getUtility(bid.getBid())));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return paretoBids;
	}
}