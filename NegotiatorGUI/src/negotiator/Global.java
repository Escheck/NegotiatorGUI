/*
 * Main.java
 *
 * Created on November 6, 2006, 9:52 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package negotiator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.lang.reflect.Constructor;
import java.text.SimpleDateFormat;

import negotiator.gui.NegoGUIApp;
import negotiator.gui.progress.ProgressUI2;
import negotiator.gui.progress.TournamentProgressUI2;
import negotiator.protocol.Protocol;
import negotiator.repository.AgentRepItem;
import negotiator.repository.DomainRepItem;
import negotiator.repository.ProfileRepItem;
import negotiator.repository.ProtocolRepItem;
import negotiator.repository.RepItem;
import negotiator.repository.Repository;
import negotiator.tournament.TournamentRunner;
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
	public static final boolean EXTENSIVE_OUTCOMES_LOG = !false;
	
	/** Every agent plays as each preference profile */
	public static final boolean PLAY_BOTH_SIDES = true;
	
	public static final Date loadDate = Calendar.getInstance().getTime();
	/** Use extensive data-named logging files */
	public static final boolean LOG_TO_DATED_FILES = !false;
	
	public static final boolean SHOW_TIME = false;

	/** Show all bid points in the GUI chart */
	public static final boolean SHOW_ALL_BIDS = false;

	/** Agents play themselves in a tournament */
	public static final boolean SELF_PLAY = false;

	/** Use experimental variables etc. */
	public static final boolean EXPERIMENTAL_SETTING = false;

	public static final boolean	HIGHLIGHT_LAST_BID	= false;
	
	/** Log things like competitiveness and minDemandedUtil */
	public static final boolean LOG_COMPETITIVENESS = false;
	
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
	
	/**
	 * Gives a useful agent name.
	 */
	public static String getAgentDescription(Agent agent)
	{
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
		if (LOG_TO_DATED_FILES)
			return "log/" + getLoadDate() + ".xml";
		else
			return outcomesFile;
	}
	
	public static String getExtensiveOutcomesFileName()
	{
		if (LOG_TO_DATED_FILES)
			return "log/extensive " + getLoadDate() + ".xml";
		else
			return "extensive " + outcomesFile;
	}

    //Liviu
    public static void runNegotiationfromCSV(String csvFilePath) throws Exception
    {
        List<Protocol> sessions = new ArrayList<Protocol>();
        
        try
        {
            FileReader fr = new FileReader(csvFilePath);
            BufferedReader br = new BufferedReader(fr);

            String line;

            //read each line from script file
            while((line = br.readLine()) != null)
            {
                line = line.trim();
                String[] tokens = line.split("\t");

                //ignore commented line
                if((tokens.length == 0)||(line.startsWith(";")))
                {
                    //in case the line br an comment
                    continue;
                }

                if(tokens.length < 4)
                {
                    System.out.println("Invalid line " + line + " parsed as " + Arrays.toString(tokens));
                    break;
                }

                String protocol = tokens[0].trim();
                String domain = tokens[1].trim();
                String[] agents = tokens[2].split(",");
                String[] profiles = tokens[3].split(",");
                ArrayList<HashMap<String, String>> agentParams = new ArrayList();
                HashMap<String, String> params = new HashMap<String, String>();
                
                Protocol newSession = loadSession(protocol, domain, agents, profiles, agentParams, params);
                sessions.add(newSession);
            }
            
            br.close();
            fr.close();
            
            ProgressUI2 progressUI = new ProgressUI2();
            TournamentProgressUI2 tournamentProgressUI = new TournamentProgressUI2(progressUI );
            NegoGUIApp.negoGUIView.addTab("Run from file: " + csvFilePath, tournamentProgressUI);
                        
            //new Thread(new TournamentRunnerTwoPhaseAutction (tournament,tournamentProgressUI)).start();
            new Thread(new TournamentRunner(sessions, tournamentProgressUI)).start();

        }
        catch(Exception e)
        {
            e.printStackTrace();
            throw new Exception("Batch file error!");
        }
    }

    public static Protocol loadSession(String protocol, String domain, String agents[], String profiles[], ArrayList<HashMap<String, String>> agentParams, HashMap<String, String> parameters) throws Exception
    {
        Protocol ns = null;
        if(profiles.length != agents.length)
        {
            throw new Exception("Invalid file - non-equal number of profiles and agents");
        }

        Repository repProtocol = Repository.getProtocolRepository();
        ProtocolRepItem protocolRI = (ProtocolRepItem) repProtocol.getItemByName(protocol);
        repProtocol = null;

        if(protocolRI == null)
        {
            throw new Exception("Unable to create protocol: " + protocol);
        }

        Repository repDomain = Repository.get_domain_repos();
        RepItem domainRI = repDomain.getItemByName(domain);
        repDomain = null;

        if(domainRI == null)
        {
            throw new Exception("Unable to find domain: " + domain);
        }

        Repository repAgent = Repository.get_agent_repository();
        AgentRepItem[] agentsRI = new AgentRepItem[agents.length];
        for(int i = 0; i < agents.length; i++)
        {
            agentsRI[i] = (AgentRepItem) repAgent.getItemByName(agents[i]);
            if(agentsRI[i] == null)
            {
                throw new Exception("Unable to create agent: " + agents[i]);
            }
        }
        repAgent = null;

        ArrayList<ProfileRepItem> profileArray = ((DomainRepItem)domainRI).getProfiles();

        ProfileRepItem[] profilesRI = new ProfileRepItem[profiles.length];
        for(int i = 0; i < profiles.length; i++)
        {
            for(ProfileRepItem prf: profileArray)
            {
                if(prf.getName().equals(profiles[i]))
                    profilesRI[i] = prf;
            }

            if(profilesRI[i] == null)
            {
                throw new Exception("Unable to create profile: " + profiles[i]);
            }
        }

        HashMap<AgentParameterVariable, AgentParamValue>[] agentParamsp = new HashMap[agentsRI.length];
        for(int i = 0; i < agentsRI.length; i++)
        {
        	agentParamsp[i] = new HashMap<AgentParameterVariable, AgentParamValue>();
        }

        // Try create the protocol instance
        try
        {
        	ns = Global.createProtocolInstance(protocolRI, agentsRI, profilesRI, agentParamsp);
        }
        catch(Exception e)
        {
        	throw new Exception("Cannot create protocol!");
        }

        
        return ns;
    }
}
