package negotiator.decoupledframework.repository;

import java.util.HashMap;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * IDEAS
 * Add support for default values?
 * 
 * @author Mark Hendrikx
 */
class BOArepositoryParser extends DefaultHandler {

	HashMap<String, BiddingStrategyItem> biddingStrategies = new HashMap<String, BiddingStrategyItem>();
	HashMap<String, BOArepItem> acceptanceConditions = new HashMap<String, BOArepItem>();
	HashMap<String, BOArepItem> opponentModels = new HashMap<String, BOArepItem>();
	HashMap<String, BOArepItem> omStrategies = new HashMap<String, BOArepItem>();

	private enum Modes { BS, AC, OM, OMS, NULL }
	private Modes mode = Modes.NULL;
	
	public void startElement(String nsURI, String strippedName,
			String tagName, Attributes attributes) throws SAXException {
		
		if (mode.equals(Modes.NULL)) {
			if (tagName.equals("biddingstrategies")) {
				mode = Modes.BS;
			} else if (tagName.equals("acceptanceconditions")) {
				mode = Modes.AC;
			} else if (tagName.equals("opponentmodels")) {
				mode = Modes.OM;
			} else if (tagName.equals("omstrategies")) {
				mode = Modes.OMS;
			} else {
				if (!tagName.equals("repository")) {
					throw new SAXException("Unsupported tag: " + tagName + " in XML");
				}
			}
		} else {
			String description = attributes.getValue(0);
			
			
			if (mode.equals(Modes.BS)) {
				BiddingStrategyItem item = new BiddingStrategyItem(attributes.getValue(1), attributes.getValue(2), attributes.getValue(3));
				biddingStrategies.put(description, item);
			}
			if (mode.equals(Modes.AC)) {
				BOArepItem item = new BOArepItem(attributes.getValue(1), attributes.getValue(2));
				acceptanceConditions.put(description, item);
			}
			if (mode.equals(Modes.OM)) {
				BOArepItem item = new BOArepItem(attributes.getValue(1), attributes.getValue(2));
				opponentModels.put(description, item);
			}
			if (mode.equals(Modes.OMS)) {
				BOArepItem item = new BOArepItem(attributes.getValue(1), attributes.getValue(2));
				omStrategies.put(description, item);
			}
		}
	}
	
	public void endElement(String nsURI, String strippedName,
			String tagName) throws SAXException {
		
		if (tagName.equals("biddingstrategies") || tagName.equals("acceptanceconditions") ||
				tagName.equals("opponentmodels") || tagName.equals("omstrategies")) {
			mode = Modes.NULL;
		}
	}

	public HashMap<String, BiddingStrategyItem> getBiddingStrategies() {
		return biddingStrategies;
	}

	public HashMap<String, BOArepItem> getAcceptanceConditions() {
		return acceptanceConditions;
	}

	public HashMap<String, BOArepItem> getOpponentModels() {
		return opponentModels;
	}

	public HashMap<String, BOArepItem> getOMStrategies() {
		return omStrategies;
	}
}