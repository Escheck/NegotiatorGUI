package negotiator.decoupledframework.repository;

import java.util.ArrayList;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;
import negotiator.Global;
import negotiator.decoupledframework.AcceptanceStrategy;
import negotiator.decoupledframework.OMStrategy;
import negotiator.decoupledframework.OfferingStrategy;
import negotiator.decoupledframework.OpponentModel;

/**
 * Simple class used to load the repository of decoupled agent components.
 * The main difference with the agent and domain repositories, is that this approach
 * does not use reflection.
 */
public class DecoupledAgentRepository {
	
	private static DecoupledAgentRepository ref;
	private static BOArepositoryParser repositoryParser;
	private static String filename = "boarepository.xml";
	
	private DecoupledAgentRepository() {
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
	
	public static DecoupledAgentRepository getInstance() {
	    if (ref == null)
	        ref = new DecoupledAgentRepository();		
	    return ref;
	  }
	
	public Object clone() throws CloneNotSupportedException {
		throw new CloneNotSupportedException();
	}

	/*
	 * 		
	private void loadOpponentModels() {				
		//ANAC2010 defaults
		omDefaults.put("AgentSmith", "Smith Frequency");
		omDefaults.put("AgentFSEGA", "FSEGA Bayesian");
		omDefaults.put("Nozomi", "Null");
		omDefaults.put("Agent K", "Null");
		omDefaults.put("Yushu", "Null");
		omDefaults.put("IAMcrazyHaggler", "Null");

		//ANAC2011 defaults
		omDefaults.put("Nice-Tit-For-Tat", "Bayesian scalable");
		omDefaults.put("TheNegotiator", "Null"); 
		omDefaults.put("HardHeaded", "Frequency"); 
		omDefaults.put("BRAMAgent", "Null");
		omDefaults.put("Agent K2", "Null");
		omDefaults.put("Gahboninho", "Null");
		omDefaults.put("IAMhaggler2011", "Null");
	}
	*/

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