package negotiator.boaframework.repository;

import java.util.HashMap;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Simple XML parser which parses the BOA repository and stores the information
 * for easy access.
 * 
 * @author Mark Hendrikx
 */
class BOArepositoryParser extends DefaultHandler {
	/** List of bidding strategies in the repository */
	HashMap<String, BOArepItem> biddingStrategies = new HashMap<String, BOArepItem>();
	/** List of acceptance strategies in the repository */
	HashMap<String, BOArepItem> acceptanceConditions = new HashMap<String, BOArepItem>();
	/** List of opponent models in the repository */
	HashMap<String, BOArepItem> opponentModels = new HashMap<String, BOArepItem>();
	/** List of opponent model strategies in the repository */
	HashMap<String, BOArepItem> omStrategies = new HashMap<String, BOArepItem>();

	/** Modes in which the parser can be in */
	private enum Modes { BS, AC, OM, OMS, NULL }
	/** Current mode the parser is in. */
	private Modes mode = Modes.NULL;
	
	/**
	 * Main method used to parse the repository.
	 * @param nsURI of the XML element.
	 * @param strippedName of the XML element.
	 * @param tagName of the XML element.
	 * @param attributes of the XML element.
	 */
	public void startElement(String nsURI, String strippedName,
			String tagName, Attributes attributes) throws SAXException {
		
		// 1. If the mode of the parser is currently unknown, determin it.
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
		// 2. ELSE if the parser is in a mode, read an element and store it.
		} else {
			String description = attributes.getValue(0);
			
			if (mode.equals(Modes.BS)) {
				BOArepItem item = new BOArepItem(attributes.getValue(1), attributes.getValue(2));
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
	
	/**
	 * Method which switches the state of the parser if a section has ended.
	 * @param nsURI of the XML element.
	 * @param strippedName of the XML element.
	 * @param tagName of the XML element.
	 */
	public void endElement(String nsURI, String strippedName,
			String tagName) throws SAXException {
		
		if (tagName.equals("biddingstrategies") || tagName.equals("acceptanceconditions") ||
				tagName.equals("opponentmodels") || tagName.equals("omstrategies")) {
			mode = Modes.NULL;
		}
	}

	/**
	 * @return bidding strategies in the BOA repository.
	 */
	public HashMap<String, BOArepItem> getBiddingStrategies() {
		return biddingStrategies;
	}

	/**
	 * @return acceptance strategies in the BOA repository.
	 */
	public HashMap<String, BOArepItem> getAcceptanceConditions() {
		return acceptanceConditions;
	}

	/**
	 * @return opponent models in the BOA repository.
	 */
	public HashMap<String, BOArepItem> getOpponentModels() {
		return opponentModels;
	}

	/**
	 * @return opponent model strategies in the BOA repository.
	 */
	public HashMap<String, BOArepItem> getOMStrategies() {
		return omStrategies;
	}
}