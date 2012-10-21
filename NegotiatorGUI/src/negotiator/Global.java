package negotiator;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.TimeZone;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.security.CodeSource;
import java.text.SimpleDateFormat;

import negotiator.gui.NegoGUIApp;
import negotiator.protocol.Protocol;
import negotiator.repository.AgentRepItem;
import negotiator.repository.ProfileRepItem;
import negotiator.repository.ProtocolRepItem;
import negotiator.tournament.VariablesAndValues.AgentParamValue;
import negotiator.tournament.VariablesAndValues.AgentParameterVariable;

/**
 * Overview of global variables used throughout the application.
 *
 * @author dmytro
 */
public class Global {

	private static AgentsLoader agentsLoader;
	
	public static Logger logger;
	public static String[] args;
	
	/** Path to domain repository */
	public static final String DOMAIN_REPOSITORY = "domainrepository.xml";
	/** Path to agent repository */
	public static final String AGENT_REPOSITORY = "agentrepository.xml";
	/** Path to protocol repository */
	public static final String PROTOCOL_REPOSITORY = "protocolrepository.xml";
	
	public static boolean batchMode = false;

	public static boolean experimentalSetup = false;// set to true to allow agent
													// to access negotiation
													// environment
	public static String outcomesFile = "outcomes.xml";

	public static String logPrefix = "";

	public static boolean fDebug = false;
	
	public static final Date loadDate = Calendar.getInstance().getTime();
	/** Use extensive data-named logging files */
	public static final boolean LOG_TO_DATED_FILES = !false;

	/** Log things like competitiveness and minDemandedUtil */
	public static final boolean LOG_COMPETITIVENESS = false;

	/** Enables experimental vars in a tournament */
	public static final boolean EXPERIMENTAL_VARS_ENABLED = false;
	
	/** Enables the default timeline to be paused. DISCRETE_TIMELINE overrides this Global when enabled.
	 * A problem with this approach, is that the AlternatingOffersProtocol class has no access to the timeline,
	 * and therefore will judge a timeout after more than 3 minutes passed. To avoid this problem, the waiting time
	 * is raised to 30 minutes if this functionality is enabled.
	 */
	public static final boolean PAUSABLE_TIMELINE = false;
	
	/**
	 * Using this option is recommended when your PC has less than 4 GB of RAM.
	 * The only side-effect is that it is not possible to retrieve the Pareto bids
	 * given the bid points. This could be a problem for quality measures, but in this
	 * case the alternate constructor of BidSpace can be used.
	 * 
	 * Also, this boolean ensures that all bidspaces are not calculated beforehand.
	 */
	public static final boolean LOW_MEMORY_MODE = true;

	public static final boolean CALCULATE_FINAL_ACCURACY = false;
	
	public Global() {}

	public static String getCurrentTime() {
		Calendar cal = Calendar.getInstance(TimeZone.getDefault());
		String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
		java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat(
				DATE_FORMAT);
		/*
		 * on some JDK, the default TimeZone is wrong we must set the TimeZone
		 * manually!!! sdf.setTimeZone(TimeZone.getTimeZone("EST"));
		 */
		sdf.setTimeZone(TimeZone.getDefault());

		return sdf.format(cal.getTime());
	}

	public static boolean isDebug() {
		return fDebug;
	}

	public static String getLocalDirName() {
		String localDirName;

		// Use that name to get a URL to the directory we are executing in
		java.net.URL myURL = NegoGUIApp.class.getResource(NegoGUIApp
				.getClassName());
		// Open a URL to the our .class file

		// Clean up the URL and make a String with absolute path name
		localDirName = myURL.getPath(); // Strip path to URL object out
		localDirName = myURL.getPath().replaceAll("%20", " "); // change %20
																// chars to
																// spaces

		// Get the current execution directory
		localDirName = localDirName.substring(0, localDirName.lastIndexOf("/")); // clean
																					// off
																					// the
																					// file
																					// name

		return localDirName;
	}

	public static String getFileNameWithoutExtension(String fileName) {

		File tmpFile = new File(fileName);
		tmpFile.getName();
		int whereDot = tmpFile.getName().lastIndexOf('.');
		if (0 < whereDot && whereDot <= tmpFile.getName().length() - 2) {
			return tmpFile.getName().substring(0, whereDot);
			// extension = filename.substring(whereDot+1);
		}
		return "";
	}

	public static Class<Protocol> getProtocolClass(ProtocolRepItem protRepItem)
			throws Exception {
		java.lang.ClassLoader loader = Global.class.getClassLoader();//ClassLoader.getSystemClassLoader();
		Class<Protocol> klass = (Class<Protocol>) loader.loadClass(protRepItem
				.getClassPath());
		return klass;
	}

	public static Class<Protocol> getProtocolClass(ProtocolRepItem protRepItem, ClassLoader loader)
	throws Exception {
		
		Class<Protocol> klass = (Class<Protocol>) loader.loadClass(protRepItem
				.getClassPath());
		return klass;
	}


	public static Protocol createProtocolInstance(ProtocolRepItem protRepItem,
			AgentRepItem[] agentRepItems, ProfileRepItem[] profileRepItems,
			HashMap<AgentParameterVariable, AgentParamValue>[] agentParams)
			throws Exception {
		Protocol ns;

		java.lang.ClassLoader loader = ClassLoader.getSystemClassLoader();

		Class klass = loader.loadClass(protRepItem.getClassPath());
		Class[] paramTypes = { AgentRepItem[].class, ProfileRepItem[].class,
				HashMap[].class};

		Constructor cons = klass.getConstructor(paramTypes);

		System.out.println("Found the constructor: " + cons);

		Object[] args = { agentRepItems, profileRepItems, agentParams};

		Object theObject = cons.newInstance(args);
		// System.out.println( "New object: " + theObject);
		ns = (Protocol) (theObject);
		return ns;
	}

	public static Protocol createProtocolInstance(ProtocolRepItem protRepItem,
			AgentRepItem[] agentRepItems, ProfileRepItem[] profileRepItems,
			HashMap<AgentParameterVariable, AgentParamValue>[] agentParams,
			ClassLoader classLoader) throws Exception {
		Protocol ns;

		// java.lang.ClassLoader loader =
		// ClassLoader.getSystemClassLoader()/*new java.net.URLClassLoader(new
		// URL[]{agentAclass})*/;

		Class klass = classLoader.loadClass(protRepItem.getClassPath());
		Class[] paramTypes = { AgentRepItem[].class, ProfileRepItem[].class,
				HashMap[].class };

		Constructor cons = klass.getConstructor(paramTypes);

		System.out.println("Found the constructor: " + cons);

		Object[] args = { agentRepItems, profileRepItems, agentParams };

		Object theObject = cons.newInstance(args);
		// System.out.println( "New object: " + theObject);
		ns = (Protocol) (theObject);
		return ns;

	}

	public static Agent loadAgent(String agentClassName) throws InstantiationException, IllegalAccessException, ClassNotFoundException {
		return loadAgent(agentClassName, null);
	}
	
	public static Agent loadAgent(String agentClassName, String variables) throws InstantiationException, IllegalAccessException, ClassNotFoundException {
		if (agentsLoader!=null) {
			return agentsLoader.loadAgent(agentClassName);
		} else {
			java.lang.ClassLoader loaderA = Global.class.getClassLoader();// .getSystemClassLoader()/*new java.net.URLClassLoader(new URL[]{agentAclass})*/;
			Agent agent = (Agent)(loaderA.loadClass(agentClassName).newInstance());
			try{
				agent.parseStrategyParameters(variables);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return agent;
		}
	}
	
	/**
	 * Gives a useful agent name.
	 */
	public static String getAgentDescription(Agent agent)
	{
		if (agent == null)
			return "";
		String agentDescription = agent.getName();
		if (agentDescription == null || "Agent A".equals(agentDescription) || "Agent B".equals(agentDescription))
			agentDescription = agent.getClass().getSimpleName();
		
		return agentDescription;
	}
	
	public static boolean isExperimentalSetup() {
		return experimentalSetup;
	}

	/**
	 * @return the agentsLoader
	 */
	public static AgentsLoader getAgentsLoader() {
		return agentsLoader;
	}

	/**
	 * @param agentsLoader the agentsLoader to set
	 */
	public static void setAgentsLoader(AgentsLoader agentsLoader) {
		Global.agentsLoader = agentsLoader;
	}
	
	/**
	 * @return the agentsLoader
	 */
	private static String getLoadDate() 
	{
	    // (2) create our "formatter" (our custom format)
	    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH.mm.ss");

	    // (3) create a new String in the format we want
	    String name = formatter.format(loadDate);

		return name;
	}
	
	public static String getOutcomesFileName()
	{
		if (!logPrefix.equals(""))
			return logPrefix + "log.xml";
		else if (LOG_TO_DATED_FILES)
			return "log/" + getLoadDate() + ".xml";
		else
			return outcomesFile;
	}
	
	public static String getDistributedOutcomesFileName()
	{
		if (LOG_TO_DATED_FILES)
			return "log/DT-" + getLoadDate() + ".xml";
		else
			return outcomesFile;
	}
	
	public static String getTournamentOutcomeFileName()
	{
		if (LOG_TO_DATED_FILES)
			return "log/TM-" + getLoadDate() + ".xml";
		else
			return outcomesFile;
	}
	
	public static String getExtensiveOutcomesFileName()
	{
		if (!logPrefix.equals(""))
			return logPrefix + "extensive_log.xml";
		else if (LOG_TO_DATED_FILES)
			return "log/extensive " + getLoadDate() + ".xml";
		else
			return "extensive " + outcomesFile;
	}
	
	public static String getOQMOutcomesFileName()
	{
		return "log/OQM " + getLoadDate() + ".csv";
	}

	public static String getBinaryRoot() {
		String root = null;
		try {
			root = new java.io.File(".").getCanonicalPath() + File.separator;
			if (!isJar()) { // it returns the root of the project, but we should be in .bin
				root += "bin" + File.separator;
			} // else if Jar it returns the root of the directory
		} catch (IOException e) {
			e.printStackTrace();
		}
		return root;
	}
	
	public static boolean isJar() {
		boolean inJar = false;
		try {
		  CodeSource cs = Global.class.getProtectionDomain().getCodeSource();
		  inJar = cs.getLocation().toURI().getPath().endsWith(".jar");
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return inJar;
	}
}