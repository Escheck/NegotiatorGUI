/*
 * Main.java
 *
 * Created on November 6, 2006, 9:52 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package negotiator;

import java.util.Calendar;
import java.util.HashMap;
import java.util.TimeZone;

import java.io.File;
import java.lang.reflect.Constructor;

import negotiator.gui.NegoGUIApp;
import negotiator.protocol.Protocol;
import negotiator.repository.AgentRepItem;
import negotiator.repository.ProfileRepItem;
import negotiator.repository.ProtocolRepItem;
import negotiator.tournament.VariablesAndValues.AgentParamValue;
import negotiator.tournament.VariablesAndValues.AgentParameterVariable;

/**
 * 
 * @author dmytro
 */
public class Global {

	private static AgentsLoader agentsLoader;
	
	public static Logger logger;
	public static String[] args;
	
	public static boolean batchMode = false;
	public static boolean fDebug = false;
	public static boolean analysisEnabled = true; // set to true to enable the
													// realtime analysis tool.
	public static boolean experimentalSetup = false;// set to true to allow agent
													// to access negotiation
													// environment
	public static String outcomesFile = "outcomes.xml";
	/** Set to true to write to {@link #outcomesFile} with a lot more information */
	public static final boolean SHOW_BID_HISTORY_IN_OUTCOMES = false;
	public Global() {
	}

	/**
	 * @param args
	 *            the command line arguments
	 */

	private static void checkArguments(String[] args) {
		for (int i = 0; i < args.length; i++) {
			if (args[i].equals("-d"))
				fDebug = true;
		}
	}

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

		java.lang.ClassLoader loader = ClassLoader.getSystemClassLoader()/*
																		 * new
																		 * java
																		 * .net.
																		 * URLClassLoader
																		 * (new
																		 * URL
																		 * []{
																		 * agentAclass
																		 * })
																		 */;

		Class klass = loader.loadClass(protRepItem.getClassPath());
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
		if(agentsLoader!=null) {
			return agentsLoader.loadAgent(agentClassName);
		} else {
			java.lang.ClassLoader loaderA = Global.class.getClassLoader();// .getSystemClassLoader()/*new java.net.URLClassLoader(new URL[]{agentAclass})*/;
			Agent agent = (Agent)(loaderA.loadClass(agentClassName).newInstance());
			return agent;
		}
		
	}
	

	public static String getAgentDescription(Agent agent)
	{
		String agentDescription = agent.getName();
		if (agentDescription == null)
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
	
	
}
