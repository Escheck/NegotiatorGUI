package negotiator;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.security.CodeSource;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.TimeZone;
import java.util.regex.Matcher;

import javax.swing.JOptionPane;

import negotiator.gui.NegoGUIApp;
import negotiator.gui.agentrepository.AgentRepositoryUI;
import negotiator.protocol.Protocol;
import negotiator.repository.AgentRepItem;
import negotiator.repository.ProfileRepItem;
import negotiator.repository.ProtocolRepItem;
import negotiator.tournament.TournamentConfiguration;
import negotiator.tournament.VariablesAndValues.AgentParamValue;
import negotiator.tournament.VariablesAndValues.AgentParameterVariable;

/**
 * Overview of global variables used throughout the application.
 * 
 * @author dmytro
 */
public class Global {
	/** Path to domain repository */
	public static final String DOMAIN_REPOSITORY = "domainrepository.xml";
	/** Path to agent repository */
	public static final String AGENT_REPOSITORY = "agentrepository.xml";
	/** Path to protocol repository */
	public static final String PROTOCOL_REPOSITORY = "protocolrepository.xml";
	/** Path to simulator repository */
	public static final String SIMULATOR_REPOSITORY = "simulatorrepository.xml";

	public static String logPrefix = "";

	public static String logPreset = "";

	private static final Date loadDate = Calendar.getInstance().getTime();

	/** Temporary flag for use in AAMAS 2014 experiments */
	public static final boolean AAMAS_2014_EXPERIMENTS = false;// true; //RA: we
																// should change
																// it as false;

	public Global() {
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
		java.lang.ClassLoader loader = Global.class.getClassLoader();// ClassLoader.getSystemClassLoader();
		Class<Protocol> klass = (Class<Protocol>) loader.loadClass(protRepItem
				.getClassPath());
		return klass;
	}

	public static Class<Protocol> getProtocolClass(ProtocolRepItem protRepItem,
			ClassLoader loader) throws Exception {

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
				HashMap[].class, int.class };

		Constructor cons = klass.getConstructor(paramTypes);

		System.out.println("Found the constructor: " + cons);

		Object[] args = { agentRepItems, profileRepItems, agentParams, 1 };

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

	// RA: load party
	public static Party loadParty(String partyClassName)
			throws InstantiationException, IllegalAccessException,
			ClassNotFoundException {

		java.lang.ClassLoader loaderA = Global.class.getClassLoader();
		Party party = (Party) (loaderA.loadClass(partyClassName).newInstance());
		return party;

	}

	/**
	 * Load an object from a given path. If it's a .class file, figure out the
	 * correct class path and use that. If it's not a .class file, we assume
	 * it's already in the existing classpath and load it with the standard
	 * class loader.
	 * 
	 * 
	 * <p>
	 * we can't properly typecheck here. Generics fail as we have type erasure,
	 * and casting to the given type does NOTHING. So we leave this a general
	 * object and leave it to the caller to do the type checking.
	 * 
	 * @param path
	 *            This can be either a class name or filename.<br>
	 *            <ul>
	 *            <li> class name like"agents.anac.y2010.AgentFSEGA.AgentFSEGA".
	 *            In this case the agent must be already on the JVM's classpath
	 *            otherwise the agent will not be found. <br>
	 *            <li>a full path, eg
	 *            "/Volumes/documents/NegoWorkspace3/NegotiatorGUI/src/agents/anac/y2010/AgentFSEGA/AgentFSEGA.java"
	 *            . In this case, we can figure out the class path ourselves,
	 *            but the ref is system dependent (backslashes on windows) and
	 *            might be absolute path.
	 *            </ul>
	 * 
	 * @return
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws ClassNotFoundException
	 * @throws MalformedURLException
	 */
	public static Object loadObject(String path) throws InstantiationException,
			IllegalAccessException, ClassNotFoundException,
			MalformedURLException {
		if (path.endsWith(".class")) {
			return loadClassFromFile(new File(path));
		} else {
			java.lang.ClassLoader loaderA = Global.class.getClassLoader();
			return (loaderA.loadClass(path).newInstance());
		}

	}

	/**
	 * Load a file as a class.
	 * 
	 * @param file
	 *            the object to be loaded. Filename should end with ".class".
	 * @return the object contained in the file.
	 * @throws ClassNotFoundException
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 * @throws MalformedURLException
	 */
	public static Object loadClassFromFile(File file)
			throws MalformedURLException, InstantiationException,
			IllegalAccessException, ClassNotFoundException {
		String className = file.getName();
		if (!className.endsWith(".class")) {
			throw new IllegalArgumentException("file " + file
					+ " is not a .class file");
		}
		// strip the trailing '.class' from the string.
		className = className.substring(0, className.length() - 6);
		File packageDir = file.getParentFile();
		if (packageDir == null) {
			packageDir = new File(".");
		}

		try {
			return loadClassfile(className, packageDir);
		} catch (NoClassDefFoundError e) {
			// System.out.println("trying to recover correct path from " +
			// e.getMessage());//debug
			/**
			 * Hack: we try to get the correct name from the error message. Err
			 * msg ~ "SimpleAgent (wrong name: agents/SimpleAgent)"
			 */
			String errormsg = e.getMessage();
			// this is what we expect.
			String wrongname = "wrong name: ";
			int i = errormsg.indexOf(wrongname);
			if (i == -1) {
				throw e; // unknown error. We can't handle...
			}
			// remove leading and trailing stuff. We now have
			// 'agents.SimpleAgent'
			String correctName = errormsg.substring(i + wrongname.length(),
					errormsg.length() - 1).replaceAll("/", ".");

			// Check that file is in correct directory path
			// we need quoteReplacement because on Windows "\" will be treated
			// in special way by replaceAll. #906
			String expectedPath = File.separator
					+ correctName.replaceAll("\\.",
							Matcher.quoteReplacement(File.separator))
					+ ".class";
			if (!(file.getAbsolutePath().endsWith(expectedPath))) {
				throw new NoClassDefFoundError("file " + file
						+ "\nis not in the correct directory structure, "
						+ "\nas its class is " + correctName + "."
						+ "\nEnsure the file is in ..." + expectedPath);
			}

			// number of dots is number of times we need to go to parent
			// directory. We are already in the directory of the agent, so -1.
			for (int up = 0; up < correctName.split("\\.").length - 1; up++) {
				// since we checked the path already, parents must exist.
				packageDir = packageDir.getParentFile();
			}
			return loadClassfile(correctName, packageDir);
		}
	}

	/**
	 * Try to load an object with given classnamem from a given packagedir
	 * 
	 * @param classname
	 *            the exact class name, eg "examplepackage.example"
	 * @param packagedir
	 *            the root directory of the classes to be loaded. If you add the
	 *            given classname to it, you should end up at the correct
	 *            location for the class file. Eg,
	 *            "/Volumes/Users/wouter/Desktop/genius/".
	 * @return the loaded class object.
	 * @throws MalformedURLException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws ClassNotFoundException
	 */
	private static Object loadClassfile(String classname, File packagedir)
			throws MalformedURLException, InstantiationException,
			IllegalAccessException, ClassNotFoundException {
		try {
			java.lang.ClassLoader loader = AgentRepositoryUI.class
					.getClassLoader();
			URLClassLoader urlLoader = new URLClassLoader(
					new URL[] { packagedir.toURI().toURL() }, loader);
			Class<?> theclass;
			theclass = urlLoader.loadClass(classname);
			return (Object) theclass.newInstance();
		} catch (ClassNotFoundException e) {
			// improve on the standard error message...
			throw new ClassNotFoundException("agent " + classname
					+ " is not available in directory '" + packagedir + "'", e);
		}

	}

	/**
	 * Load an agent using the given classname/filename. DOES NOT call
	 * {@link Agent#parseStrategyParameters(String)}
	 * 
	 * @param classname
	 *            This can be either a class name or filename.<br>
	 *            <ul>
	 *            <li>
	 *            class name like"agents.anac.y2010.AgentFSEGA.AgentFSEGA". In
	 *            this case the agent must be already on the JVM's classpath
	 *            otherwise the agent will not be found.
	 *            <li>a full path, eg
	 *            "/Volumes/documents/NegoWorkspace3/NegotiatorGUI/src/agents/anac/y2010/AgentFSEGA/AgentFSEGA.java"
	 *            . In this case, we can figure out the class path ourselves,
	 *            but the ref is system dependent (backslashes on windows) and
	 *            might be absolute path.
	 *            </ul>
	 * @param variables
	 *            the variables to pass to the agent.
	 * @return instantiated agent ready to use.
	 * @throws ClassNotFoundException
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 * @throws IllegalArgumentException
	 * @throws ClassCastException
	 * @throws MalformedURLException
	 */
	public static Agent loadAgent(String path) throws InstantiationException,
			IllegalAccessException, ClassNotFoundException,
			MalformedURLException, ClassCastException, IllegalArgumentException {
		return (Agent) loadObject(path);
	}

	/**
	 * load agent and then set the parameters. See {@link #loadAgent(String)}
	 * 
	 * @param agentClassName
	 * @param variables
	 * @return
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws ClassNotFoundException
	 * @throws MalformedURLException
	 * @throws ClassCastException
	 * @throws IllegalArgumentException
	 */
	public static Agent loadAgent(String agentClassName, String variables)
			throws InstantiationException, IllegalAccessException,
			ClassNotFoundException, MalformedURLException, ClassCastException,
			IllegalArgumentException {

		Agent agent = loadAgent(agentClassName);

		// CHECK why do we catch failures in parseStrategyParameters?
		try {
			agent.parseStrategyParameters(variables);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return agent;

	}

	/**
	 * Load an Agent from a given .class file.
	 * 
	 * @param classname
	 *            a .class file.
	 * @param packagedir
	 *            the directory of the package
	 * @return the {@link Agent} object loaded from the class file.
	 * @throws MalformedURLException
	 * @throws ClassNotFoundException
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 * @throws ClassCastException
	 *             if File does not contain an {@link Agent}
	 * @throws {@link IllegalArgumentException} if File is not a class file.
	 * @throws NoClassDefFoundError
	 *             if the agent fails to load (eg, package name is not empty).
	 */
	// private static Agent loadAgentClassfile(String classname, File
	// packagedir)
	// throws MalformedURLException, InstantiationException,
	// IllegalAccessException, ClassNotFoundException, ClassCastException,
	// IllegalArgumentException, ClassFormatError {
	// try {
	// java.lang.ClassLoader loader = AgentRepositoryUI.class
	// .getClassLoader();
	// URLClassLoader urlLoader = new URLClassLoader(
	// new URL[] { packagedir.toURI().toURL() }, loader);
	// Class<?> theclass;
	// theclass = urlLoader.loadClass(classname);
	// return (Agent) theclass.newInstance();
	// } catch (ClassNotFoundException e) {
	// // improve on the standard error message...
	// throw new ClassNotFoundException("agent " + classname
	// + " is not available in directory '" + packagedir + "'", e);
	// }
	// // don't catch NoClassDefFoundError here, because we need the message
	// // in loaodAgentWithPackage
	// }

	/**
	 * Try to find an agent that has a package name defined. We just get the
	 * pointer to the filename, we need to discover which package it actually
	 * is. The file specified must be in the proper directory hierarchy in order
	 * to be loaded. If returns without throwing, the file is ok.
	 * 
	 * @param file
	 *            the class file to be found and checked. Should be full path to
	 *            the file, so that we can check the directory structure.
	 * @throws ClassNotFoundException
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 * @throws IllegalArgumentException
	 * @throws ClassCastException
	 * @throws MalformedURLException
	 */
	// private static Agent loadAgentWithPackage(File file)
	// throws MalformedURLException, ClassCastException,
	// IllegalArgumentException, InstantiationException,
	// IllegalAccessException, ClassNotFoundException {
	// String className = file.getName();
	// if (!className.endsWith(".class")) {
	// throw new IllegalArgumentException("file " + file
	// + " is not a .class file");
	// }
	// // strip the trailing '.class' from the string.
	// className = className.substring(0, className.length() - 6);
	// File packageDir = file.getParentFile();
	// if (packageDir == null) {
	// packageDir = new File(".");
	// }
	//
	// try {
	// return loadAgentClassfile(className, packageDir);
	// } catch (NoClassDefFoundError e) {
	// /**
	// * Hack: we try to get the correct name from the error message. Err
	// * msg ~ "SimpleAgent (wrong name: agents/SimpleAgent)"
	// */
	// String errormsg = e.getMessage();
	// // this is what we expect.
	// String wrongname = "wrong name: ";
	// int i = errormsg.indexOf(wrongname);
	// if (i == -1) {
	// throw e; // unknown error. We can't handle...
	// }
	// // remove leading and trailing stuff. We now have
	// // 'agents.SimpleAgent'
	// String correctName = errormsg.substring(i + wrongname.length(),
	// errormsg.length() - 1).replaceAll("/", ".");
	//
	// // Check that file is in correct directory path
	// String expectedPath = File.separator
	// + correctName.replaceAll("\\.", File.separator) + ".class";
	// if (!(file.getAbsolutePath().endsWith(expectedPath))) {
	// throw new NoClassDefFoundError("file " + file
	// + "\nis not in the correct directory structure, "
	// + "\nas its class is " + correctName + "."
	// + "\nEnsure the file is in ..." + expectedPath);
	// }
	//
	// // number of dots is number of times we need to go to parent
	// // directory. We are already in the directory of the agent, so -1.
	// for (int up = 0; up < correctName.split("\\.").length - 1; up++) {
	// // since we checked the path already, parents must exist.
	// packageDir = packageDir.getParentFile();
	// }
	// return loadAgentClassfile(correctName, packageDir);
	// }
	// }

	/**
	 * Gives a useful agent name.
	 */
	public static String getAgentDescription(Agent agent) {
		if (agent == null)
			return "";
		String agentDescription = agent.getName();
		if (agentDescription == null || "Agent A".equals(agentDescription)
				|| "Agent B".equals(agentDescription))
			agentDescription = agent.getClass().getSimpleName();

		return agentDescription;
	}

	/**
	 * Show a dialog to the user, explaining the exception that was raised while
	 * loading file fc. Typically this is used in combination with
	 * {@link #loadObject(String)} and associates. Also dumps a copy of the full
	 * stacktrace to the console, to help us debugging #906
	 * 
	 * @param fc
	 *            file that was attempted to be loaded
	 * @param e
	 *            the exception that was raised
	 */
	public static void showLoadError(File fc, Throwable e) {
		e.printStackTrace();
		if (e instanceof ClassNotFoundException) {
			showLoadError("No class found at " + fc, e);
		} else if (e instanceof InstantiationException) {
			// happens when object instantiated is interface or abstract
			showLoadError(
					"Class cannot be instantiated. Reasons may be that there is no constructor without arguments, "
							+ "or the class is abstract or an interface.", e);
		} else if (e instanceof IllegalAccessException) {
			showLoadError("Missing constructor without arguments", e);
		} else if (e instanceof NoClassDefFoundError) {
			showLoadError("Errors in loaded class.", e);
		} else if (e instanceof ClassCastException) {
			showLoadError("The loaded class seems to be of the wrong type. ", e);
		} else if (e instanceof IllegalArgumentException) {
			showLoadError("The given file can not be used.", e);
		} else if (e instanceof IOException) {
			showLoadError("The file can not be read.", e);
		} else {
			showLoadError("Something went wrong loading the file", e);
		}
	}

	/*
	 * show error while loading agent file. Also show the detail message.
	 */
	private static void showLoadError(String text, Throwable e) {
		String message = e.getMessage();
		if (message == null) {
			message = "";
		}

		JOptionPane.showMessageDialog(null, text + "\n" + message,
				"Load error", 0);
	}

	/**
	 * @return the agentsLoader
	 */
	private static String getLoadDate() {
		// (2) createFrom our "formatter" (our custom format)
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH.mm.ss");

		// (3) createFrom a new String in the format we want
		String name = formatter.format(loadDate);

		return name;
	}

	public static String getOutcomesFileName() {
		if (!logPreset.equals("")) {
			return logPreset;
		}
		if (!logPrefix.equals(""))
			return logPrefix + "log.xml";

		return "log/" + getLoadDate() + getPostFix() + ".xml";
	}

	public static String getDistributedOutcomesFileName() {
		return "log/DT-" + getLoadDate() + getPostFix() + ".xml";
	}

	public static String getTournamentOutcomeFileName() {
		return "log/TM-" + getLoadDate() + getPostFix() + ".xml";
	}

	public static String getExtensiveOutcomesFileName() {
		if (!logPrefix.equals(""))
			return logPrefix + "extensive_log.xml";
		return "log/extensive " + getLoadDate() + getPostFix() + ".xml";
	}

	public static String getOQMOutcomesFileName() {
		return "log/OQM " + getLoadDate() + getPostFix() + ".csv";
	}

	public static String getBinaryRoot() {
		String root = null;
		try {
			root = new java.io.File(".").getCanonicalPath() + File.separator;
			if (!isJar()) { // it returns the root of the project, but we should
							// be in .bin
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
		} catch (Exception e) {
			e.printStackTrace();
		}
		return inJar;
	}

	private static String getPostFix() {
		String postFix = "";
		if (TournamentConfiguration.getBooleanOption("appendModeAndDeadline",
				false)) {
			String mode = "time";
			if (TournamentConfiguration.getBooleanOption("protocolMode", false)) {
				mode = "rounds";
			}
			postFix += "_" + mode + "_"
					+ TournamentConfiguration.getIntegerOption("deadline", 180);
		}
		return postFix;
	}
}