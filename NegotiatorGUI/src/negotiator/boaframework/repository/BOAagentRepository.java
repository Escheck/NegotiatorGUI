package negotiator.boaframework.repository;

import java.util.ArrayList;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;
import negotiator.Global;
import negotiator.boaframework.AcceptanceStrategy;
import negotiator.boaframework.OMStrategy;
import negotiator.boaframework.OfferingStrategy;
import negotiator.boaframework.OpponentModel;

/**
 * Simple class used to load the repository of decoupled agent components.
 * 
 * @author Mark Hendrikx
 */
public class BOAagentRepository {
	
	/** Reference to this class used to enforce a singleton pattern. */
	private static BOAagentRepository ref;
	/** Reference to the parser used to interpret the BOA repository. */
	private static BOArepositoryParser repositoryParser;
	/** Filename of the BOA repository. */
	private static String filename = "boarepository.xml";
	
	/**
	 * Initializes the parser and uses it to interpret the BOA repository.
	 */
	private BOAagentRepository() {
		XMLReader xr;
		try {
			xr = XMLReaderFactory.createXMLReader();
			repositoryParser = new BOArepositoryParser();
			xr.setContentHandler(repositoryParser);
			xr.setErrorHandler(repositoryParser);
			xr.parse(filename);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * @return Singleton instance of the repository loader.
	 */
	public static BOAagentRepository getInstance() {
	    if (ref == null)
	        ref = new BOAagentRepository();		
	    return ref;
	  }
	
	/**
	 * Override of clone method to enforce singleton pattern.
	 */
	public Object clone() throws CloneNotSupportedException {
		throw new CloneNotSupportedException();
	}

	/**
	 * Method which returns the list of offering strategies in the BOA repository.
	 * @return list of offering strategies.
	 */
	public ArrayList<String> getOfferingStrategies() {
		return new ArrayList<String>(repositoryParser.getBiddingStrategies().keySet());
	}
	
	/**
	 * Method which returns the list of acceptance strategies in the BOA repository.
	 * @return list of acceptance strategies.
	 */
	public ArrayList<String>getAcceptanceStrategies() {
		return new ArrayList<String>(repositoryParser.getAcceptanceConditions().keySet());
	}
	
	/**
	 * Method which returns the list of opponent models in the BOA repository.
	 * @return list of opponent models.
	 */
	public ArrayList<String> getOpponentModels() {
		return new ArrayList<String>(repositoryParser.getOpponentModels().keySet());
	}
	
	/**
	 * Method which returns the list of opponent model strategies in the BOA repository.
	 * @return list of opponent model strategies.
	 */
	public ArrayList<String> getOMStrategies() {
		return new ArrayList<String>(repositoryParser.getOMStrategies().keySet());
	}
	
	/**
	 * Method used to load the offering strategy associated with the given name.
	 * 
	 * @param name of the offering strategy to be loaded.
	 * @return offering strategy associated with the name.
	 */
	public OfferingStrategy getOfferingStrategy(String name) {
		BOArepItem item = repositoryParser.getBiddingStrategies().get(name);
		System.out.println(name);
		ClassLoader loader = Global.class.getClassLoader();
		OfferingStrategy os = null;
		try {
			os = (OfferingStrategy)(loader.loadClass(item.getClassPath()).newInstance());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return os;
	}
	
	/**
	 * Method used to get the tooltip of the offering strategy with the given name.
	 * 
	 * @param name of the offering strategy which tooltip should be loaded.
	 * @return tooltip associated with the name.
	 */
	public static String getOfferingStrategyTooltip(String name) {
		return repositoryParser.getBiddingStrategies().get(name).getTooltip();
	}
	
	/**
	 * Method used to load the acceptance strategy associated with the given name.
	 * 
	 * @param name of the acceptance strategy to be loaded.
	 * @return acceptance strategy associated with the name.
	 */
	public AcceptanceStrategy getAcceptanceStrategy(String name) {
		BOArepItem item = repositoryParser.getAcceptanceConditions().get(name);
		ClassLoader loader = Global.class.getClassLoader();
		AcceptanceStrategy as = null;
		try {
			as = (AcceptanceStrategy)(loader.loadClass(item.getClassPath()).newInstance());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return as;
	}
	
	/**
	 * Method used to get the tooltip of the acceptance strategy with the given name.
	 * 
	 * @param name of the acceptance strategy which tooltip should be loaded.
	 * @return tooltip associated with the name.
	 */
	public static String getAcceptanceStrategyTooltip(String name) {
		return repositoryParser.getAcceptanceConditions().get(name).getTooltip();
	}
	
	/**
	 * Method used to get the tooltip of the opponent model with the given name.
	 * 
	 * @param name of the opponent model which tooltip should be loaded.
	 * @return tooltip associated with the name.
	 */
	public static String getOpponentModelTooltip(String name) {
		return repositoryParser.getOpponentModels().get(name).getTooltip();
	}
	
	/**
	 * Method used to load the opponent model associated with the given name.
	 * 
	 * @param name of the opponent model to be loaded.
	 * @return opponent model associated with the name.
	 */
	public OpponentModel getOpponentModel(String name) {
		BOArepItem item = repositoryParser.getOpponentModels().get(name);
		ClassLoader loader = Global.class.getClassLoader();
		OpponentModel om = null;
		try {
			om = (OpponentModel)(loader.loadClass(item.getClassPath()).newInstance());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return om;
	}

	/**
	 * Method used to load the opponent model strategy associated with the given name.
	 * 
	 * @param name of the opponent model strategy.
	 * @return opponent model strategy associated with the name.
	 */
	public OMStrategy getOMStrategy(String name) {
		BOArepItem item = repositoryParser.getOMStrategies().get(name);
		ClassLoader loader = Global.class.getClassLoader();
		OMStrategy oms = null;
		try {
			oms = (OMStrategy)(loader.loadClass(item.getClassPath()).newInstance());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return oms;
	}
	
	/**
	 * Method used to get the tooltip of the opponent model strategy with the given name.
	 * 
	 * @param name of the opponent model strategy which tooltip should be loaded.
	 * @return tooltip associated with the name.
	 */
	public static String getOpponentModelStrategyTooltip(String name) {
		return repositoryParser.getOMStrategies().get(name).getTooltip();
	}
}