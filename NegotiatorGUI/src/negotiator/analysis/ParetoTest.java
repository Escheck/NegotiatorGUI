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
import negotiator.utility.UtilitySpace;
import negotiator.xml.OrderedSimpleElement;

/**
 * This class can be used to test if the implementation of the Pareto frontier
 * algorithm in BidSpace returns the correct results on each domain.
 * 
 * @author Mark Hendrikx
 * @contact m.j.c.hendrikx@student.tudelft.nl
 */
public class ParetoTest {

	
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
		
		for (ScenarioInfo domainSt : domains) {
			Domain domain = new Domain(dir + domainSt.getDomain());
			UtilitySpace utilitySpaceA, utilitySpaceB;
			utilitySpaceA =  new UtilitySpace(domain, dir + domainSt.getPrefProfA());
			utilitySpaceB =  new UtilitySpace(domain, dir + domainSt.getPrefProfB());
			
			ArrayList<BidPoint> realParetoBids = bruteforceParetoBids(domain, utilitySpaceA, utilitySpaceB);
			BidSpace space = new BidSpace(utilitySpaceA, utilitySpaceB);
			ArrayList<BidPoint> estimatedParetoBids = space.getParetoFrontier();
			
			if (checkValidity(estimatedParetoBids, realParetoBids)) {
				System.out.println("No problems in: " + domain.getName());
			} else {
				System.out.println("Found difference in: " + domain.getName());
				System.out.println("REAL " + realParetoBids.size());
				for (int i = 0; i < realParetoBids.size(); i++) {
					System.out.println(realParetoBids.get(i).bid + " " + realParetoBids.get(i).utilityA + " " + realParetoBids.get(i).utilityB);
				}
				System.out.println("ESTIMATE " + estimatedParetoBids.size());
				for (int i = 0; i < estimatedParetoBids.size(); i++) {
					System.out.println(estimatedParetoBids.get(i).utilityA + " " + estimatedParetoBids.get(i).utilityB);
				}
			}
		}
		System.out.println("Finished processing domains");
	}
	
	private static boolean checkValidity(ArrayList<BidPoint> estimatedParetoBids, ArrayList<BidPoint> realParetoBids) {
		if (realParetoBids.size() != estimatedParetoBids.size()) {
			return false;
		}
		for (BidPoint paretoBid : realParetoBids) {
			boolean found = false;
			for (int a = 0; a < estimatedParetoBids.size(); a++) {
				if (estimatedParetoBids.get(a).utilityA.equals(paretoBid.utilityA) &&
						estimatedParetoBids.get(a).utilityB.equals(paretoBid.utilityB)) {
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
	
	private static ArrayList<BidPoint> bruteforceParetoBids(Domain domain, UtilitySpace spaceA, UtilitySpace spaceB) {
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