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
	
	private static BOAagentRepository ref;
	private static BOArepositoryParser repositoryParser;
	private static String filename = "boarepository.xml";
	
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
	
	public static BOAagentRepository getInstance() {
	    if (ref == null)
	        ref = new BOAagentRepository();		
	    return ref;
	  }
	
	public Object clone() throws CloneNotSupportedException {
		throw new CloneNotSupportedException();
	}

	public ArrayList<String> getOfferingStrategies() {
		return new ArrayList<String>(repositoryParser.getBiddingStrategies().keySet());
	}
	
	public ArrayList<String>getAcceptanceStrategies() {
		return new ArrayList<String>(repositoryParser.getAcceptanceConditions().keySet());
	}
	
	public ArrayList<String> getOpponentModels() {
		return new ArrayList<String>(repositoryParser.getOpponentModels().keySet());
	}
	
	public ArrayList<String> getOMStrategies() {
		return new ArrayList<String>(repositoryParser.getOMStrategies().keySet());
	}
	
	public OfferingStrategy getOfferingStrategy(String key) {
		BOArepItem item = repositoryParser.getBiddingStrategies().get(key);
		ClassLoader loader = Global.class.getClassLoader();
		OfferingStrategy os = null;
		try {
			os = (OfferingStrategy)(loader.loadClass(item.getClassPath()).newInstance());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return os;
	}
	
	public static String getOfferingStrategyTooltip(String key) {
		return repositoryParser.getBiddingStrategies().get(key).getTooltip();
	}
	
	public AcceptanceStrategy getAcceptanceStrategy(String key) {
		BOArepItem item = repositoryParser.getAcceptanceConditions().get(key);
		ClassLoader loader = Global.class.getClassLoader();
		AcceptanceStrategy as = null;
		try {
			as = (AcceptanceStrategy)(loader.loadClass(item.getClassPath()).newInstance());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return as;
	}
	
	public static String getAcceptanceStrategyTooltip(String key) {
		return repositoryParser.getAcceptanceConditions().get(key).getTooltip();
	}
	
	public static String getOpponentModelTooltip(String key) {
		return repositoryParser.getOpponentModels().get(key).getTooltip();
	}
	
	public OpponentModel getOpponentModel(String key) {
		BOArepItem item = repositoryParser.getOpponentModels().get(key);
		ClassLoader loader = Global.class.getClassLoader();
		OpponentModel om = null;
		try {
			om = (OpponentModel)(loader.loadClass(item.getClassPath()).newInstance());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return om;
	}

	public OMStrategy getOMStrategy(String key) {
		BOArepItem item = repositoryParser.getOMStrategies().get(key);
		ClassLoader loader = Global.class.getClassLoader();
		OMStrategy oms = null;
		try {
			oms = (OMStrategy)(loader.loadClass(item.getClassPath()).newInstance());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return oms;
	}
	
	public static String getOpponentModelStrategyTooltip(String key) {
		return repositoryParser.getOMStrategies().get(key).getTooltip();
	}
}