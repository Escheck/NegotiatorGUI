package negotiator.protocol.alternatingoffers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import negotiator.Agent;
import negotiator.Domain;
import negotiator.Global;
import negotiator.NegotiationEventListener;
import negotiator.NegotiationOutcome;
import negotiator.analysis.BidPoint;
import negotiator.analysis.BidSpace;
import negotiator.analysis.BidSpaceCache;
import negotiator.exceptions.Warning;
import negotiator.protocol.Protocol;
import negotiator.qualitymeasures.TrajectoryMeasures;
import negotiator.qualitymeasures.logmanipulation.UtilityMeasures;
import negotiator.repository.AgentRepItem;
import negotiator.repository.ProfileRepItem;
import negotiator.tournament.Tournament;
import negotiator.tournament.TournamentRunner;
import negotiator.tournament.VariablesAndValues.AgentParamValue;
import negotiator.tournament.VariablesAndValues.AgentParameterVariable;
import negotiator.tournament.VariablesAndValues.AgentValue;
import negotiator.tournament.VariablesAndValues.AgentVariable;
import negotiator.tournament.VariablesAndValues.AssignedParamValue;
import negotiator.tournament.VariablesAndValues.AssignedParameterVariable;
import negotiator.tournament.VariablesAndValues.TotalSessionNumberValue;
import negotiator.tournament.VariablesAndValues.TournamentValue;
import negotiator.utility.UtilitySpace;
import negotiator.xml.SimpleElement;

/**
 * Manager of the Alternating Offers protocol: 
 * Loads and initializes agents, domain, etc. Signals the start/stop of negotiations by calling {@link BilateralAtomicNegotiationSession}.
 * Manages time-outs.
 */
public class AlternatingOffersProtocol extends Protocol {
	
	private static final long serialVersionUID = 7472004245336770247L;
	public static final int ALTERNATING_OFFERS_AGENT_A_INDEX = 0;
	public static final int ALTERNATING_OFFERS_AGENT_B_INDEX = 1;

	/** always 1? */
	int sessionTotalNumber;
	int sessionNumber; // the main session number: increases with different session setups
	public int sessionTestNumber; // the sub-session number: counts from 1 to sessionTotalNumber



	boolean startingWithA=true;
	ArrayList<NegotiationEventListener> actionEventListener = new ArrayList<NegotiationEventListener>();
	protected String startingAgent; // agentAname or agnetBname

	NegotiationOutcome outcome;


	private Integer totalTime; // will be set only AFTER running the session, because it depends on whether agent isUIAgent() or not
	/** Total negotiation time available in seconds. This also changes {@link AlternatingOffersBilateralAtomicNegoSession.totalTime} */
	public final static int gui_nego_time=60*30; 	// Nego time if a GUI is involved in the nego

	protected Agent agentA;
	protected Agent agentB;

	/** fields copied from the NegotiationTemplate class */

	AlternatingOffersBilateralAtomicNegoSession sessionrunner;
	/** END OF fields copied from the NegotiationTemplate class */


	/** non_tournament_next_session_nr is used to auto-number non-tournament sessions */
	static int non_tournament_next_session_nr=1;


	/** shared counter */
	static int session_number;

	/** fields copied from the NegotiationTemplate class */


	//private Analysis fAnalysis;
	private BidSpace bidSpace=null;
	
	/** END OF fields copied from the NegotiationTemplate class */

	/***************** RUN A NEGO SESSION. code below comes from NegotiationManager ****************************/

	public AlternatingOffersProtocol(AgentRepItem[] agentRepItems,
			ProfileRepItem[] profileRepItems,
			HashMap<AgentParameterVariable, AgentParamValue>[] agentParams,
			HashMap<String, Integer> configuration)
	throws Exception {
		super(agentRepItems, profileRepItems, agentParams, configuration);
		sessionTotalNumber = 1;
	}
	/**
	 * Warning. You can call run() directly (instead of using Thread.start() )
	 * but be aware that run() will not return until the nego session
	 * has completed. That means that your interface will lock up until the session is complete.
	 * And if the negosession uses modal interfaces, this will lock up swing, because modal
	 * interfaces will not launch until the other swing interfaces have handled their events.
	 * (at least this is my current understanding, Wouter, 22aug08).
	 * See "java dialog deadlock" on the web...
	 */
	public void run() {
		try { 
			startNegotiation();
			// only sleep if batch mode????
			Thread.sleep(1000); // 1 second delay before next nego starts. Used to be 5, is it needed anyway?
			// Wouter: huh?? removed this           System.exit(0);
		} catch (Exception e) { new Warning("Problem starting negotiation:"+e); e.printStackTrace();}
	}

	/** this runs sessionTotalNumber of sessions with the provided settings */
	public void startNegotiation() throws Exception {
		// Main.log("Starting negotiations...");
		for(int i=0;i<sessionTotalNumber;i++) {

//			System.out.println("Starting session " + String.valueOf(i+1));
			if(tournamentRunner!=null) {
				synchronized (tournamentRunner) {
					runNegotiationSession(i+1);					
					tournamentRunner.notify();
				}
			} else
				runNegotiationSession(i+1);
		}
	}



	/**
	 * Run a negotiation session.
	 * There may be multiple test runs of a single session, for instance to take the average score.
	 * returns the result in the global field "outcome"
	 * @param nr is the sessionTestNumber
	 * @throws Exception
	 */
	protected void runNegotiationSession(int nr)  throws Exception
	{


		//java.lang.ClassLoader loaderA = Global.class.getClassLoader();// .getSystemClassLoader()/*new java.net.URLClassLoader(new URL[]{agentAclass})*/;
		agentA = Global.loadAgent(getAgentARep().getClassPath(), getAgentARep().getParams());//(Agent)(loaderA.loadClass(getAgentARep().getClassPath()).newInstance());
		agentA.setName(getAgentAname());

		//java.lang.ClassLoader loaderB =Global.class.getClassLoader();//ClassLoader.getSystemClassLoader();
		agentB = Global.loadAgent(getAgentBRep().getClassPath(), getAgentBRep().getParams());//(Agent)(loaderB.loadClass(getAgentBRep().getClassPath()).newInstance());
		agentB.setName(getAgentBname());
		
		//Passes the Experimental Variables to agent A and B
		

		sessionTestNumber=nr;
		if(tournamentRunner!= null) tournamentRunner.fireNegotiationSessionEvent(this);
		//NegotiationSession nego = new NegotiationSession(agentA, agentB, nt, sessionNumber, sessionTotalNumber,agentAStarts,actionEventListener,this);
		//SessionRunner sessionrunner=new SessionRunner(this);
		startingAgent=getAgentAname();
		if ( (!startingWithA) && new Random().nextInt(2)==1) { 
			startingAgent=getAgentBname();
		}

		sessionrunner = newAlternatingOffersBilateralAtomicNegoSession();
		if(agentA.isUIAgent()||agentB.isUIAgent()) {
			totalTime = gui_nego_time;
		} else {
			if (configuration != null && configuration.containsKey("deadline")) {
				totalTime = configuration.get("deadline");
			} else {
				totalTime = 180;
			}
		}
		sessionrunner.setTotalTime(totalTime);
		sessionrunner.setSessionTotalNumber(sessionTotalNumber);
		sessionrunner.setStartingWithA(startingWithA);
		/* This eventually fills the GUI columns */
		fireBilateralAtomicNegotiationSessionEvent(sessionrunner,  getProfileArep(), getProfileBrep(), getAgentARep(), getAgentBRep(), Global.getAgentDescription(agentA), Global.getAgentDescription(agentB));
		if(Global.fDebug || (getConfiguration() != null && getConfiguration().size() > 0 && getConfiguration().get("protocolMode") == 1)) {
			sessionrunner.run();
		} else {
			negoThread = new Thread(sessionrunner);
			System.out.println("nego start. "+System.currentTimeMillis()/1000);
			negoThread.start();
			try {
				synchronized (this) {
					System.out.println("waiting NEGO_TIMEOUT="+totalTime*1000 + 10000);
					// wait will unblock early if negotiation is finished in time.
					if (Global.PAUSABLE_TIMELINE) {
						wait(totalTime*10000 + 10000);
					} else {
						wait(totalTime*1000 + 10000);
					}
				}
			} catch (InterruptedException ie) { new Warning("wait cancelled:",ie); }
		}
		stopNegotiation();

		
		if (sessionrunner.no == null)
		{
			// wait some more
			Thread.sleep(5000);
		}
		
		if(sessionrunner.no==null) {
			sessionrunner.JudgeTimeout();
		}
		if(sessionrunner.MACoutcomes.isEmpty()) {
			outcome=sessionrunner.no;
			createExtraLogData();
		} else{
			for(NegotiationOutcome savedOutcome: sessionrunner.MACoutcomes) {
				outcome = savedOutcome;
				outcome.additional = new SimpleElement("additional");

				createExtraLogData();
			}
		}
		sessionrunner.cleanUp();
	}
	
	/**
	 * Append quality measure information to the additional {@link #outcome} field.
	 */
	public void createExtraLogData() {
		outcome.setRunNr(getRun());

		// DEFAULT: no detailed analysis
		if (configuration != null && configuration.size() > 0 && configuration.get("logDetailedAnalysis") != 0) {
			// Calculate the opponent model quality measures and log them
			UtilityMeasures disCalc = new UtilityMeasures(getBidSpace());
			SimpleElement utQualityMeasures = disCalc.calculateMeasures(outcome.agentAutility, outcome.agentButility);
			
			TrajectoryMeasures trajCalc = new TrajectoryMeasures(outcome.getAgentABids(), outcome.getAgentBBids(), bidSpace);	
			SimpleElement tjQualityMeasures = trajCalc.calculateMeasures();
			
			if (outcome.additional==null) {
				outcome.additional = new SimpleElement("additional");
			}
			
			if (utQualityMeasures != null) { 
				outcome.additional.addChildElement(utQualityMeasures);				
			}
			if (tjQualityMeasures != null) {
				outcome.additional.addChildElement(tjQualityMeasures);	
			}
		}
			writeOutcomeToLog(false);
			// DEFAULT: extensive log disabled
			if (configuration != null && configuration.size() > 0 && configuration.get("logNegotiationTrace") != 0) {
				writeOutcomeToLog(true);
			}
	}

	protected AlternatingOffersBilateralAtomicNegoSession newAlternatingOffersBilateralAtomicNegoSession() throws Exception
	{
		return new AlternatingOffersBilateralAtomicNegoSession(this, 
				agentA, 
				agentB, 
				getAgentAname(),
				getAgentBname(),
				getAgentAUtilitySpace(), 
				getAgentBUtilitySpace(), 
				getAgentAparams(),
				getAgentBparams(),
				startingAgent);
	}


	private void writeOutcomeToLog(boolean extensive)
	{
		try {
			File outcomesFile;
			if (extensive)
				outcomesFile = getExtensiveLogFile();
			else
				outcomesFile = getLogFile();
			boolean exists = outcomesFile.exists();
			BufferedWriter out = new BufferedWriter(new FileWriter(outcomesFile, true));
			if (!exists)
			{
				System.out.println("Creating log file: " + Global.getOutcomesFileName());
				out.write("<a>\n");
			}
			
			if (extensive)
				out.write(outcome.toXMLWithBids().toString());
			else
				out.write(""+outcome.toXML());
			out.close();
		} catch (Exception e) {
			new Warning("Exception during writing s:"+e);
			e.printStackTrace();
		}
	}
	
	public static void closeLog(boolean extensive)
	{
		try {
			File outcomesFile;
			if (extensive) {
				outcomesFile = getExtensiveLogFile();
				System.out.println("Creating extensive log");
			}
			else {
				outcomesFile = getLogFile();
				System.out.println("Creating normal log");
			}

			boolean exists = outcomesFile.exists();
			if (exists)
			{
				BufferedWriter out = new BufferedWriter(new FileWriter(outcomesFile, true));
				System.out.println("Closing log file: " + outcomesFile.getName());
				out.write("</a>\n");
				out.close();
			}
		} catch (Exception e) {
			new Warning("Exception during closing log:"+e);
			e.printStackTrace();
		}
	}


	private static File getLogFile()
	{
		String outcomesFileName = Global.getOutcomesFileName();
		File outcomesFile = new File(outcomesFileName);
		return outcomesFile;
	}
	
	private static File getExtensiveLogFile()
	{
		String outcomesFileName = Global.getExtensiveOutcomesFileName();
		File outcomesFile = new File(outcomesFileName);
		return outcomesFile;
	}

	public void stopNegotiation() {
		if (negoThread!=null&&negoThread.isAlive()) {
			try {
				sessionrunner.stopNegotiation=true; // see comments in sessionrunner..
				negoThread.interrupt();
				// we call cleanup of agent from separate thread, preventing any sabotage on kill.
				//Thread cleanup=new Thread() {public void run() { sessionrunner.currentAgent.cleanUp();  } };
				//cleanup.start();
				//TODO call this from separate thread.
				//negoThread.stop(); // kill the stuff
				// Wouter: this will throw a ThreadDeath Error into the nego thread
				// The nego thread will catch this and exit immediately.
				// Maybe it should not even try to catch that.
			} catch (Exception e) {	new Warning("problem stopping the nego",e); }
		}
		return;
	}

	public String toString() {
		return "NegotiationSession["+getAgentAStrategyName()+" versus "+getAgentBStrategyName()+"]";
	}

	public String getOutcome() {
		System.out.println("outcomes count: " + sessionrunner.MACoutcomes.size());

		
		if(!sessionrunner.MACoutcomes.isEmpty()){
			System.out.println("get Multiple logs");
			StringBuilder sb = new StringBuilder();
			for(NegotiationOutcome outcome : sessionrunner.MACoutcomes) {
				sb.append(outcome.toXML());
			}
			return sb.toString();
		}else {
			System.out.println("get single logs");
			return "" + outcome.toXML();
		}
	}
	/* methods copied from the NegotiationTemplate class */

	/**
	 * 
	 * Call this method to draw the negotiation paths on the chart with analysis.
	 * Wouter: moved to here from Analysis. 
	 * @param pAgentABids
	 * @param pAgentBBids
	 */
	public void addNegotiationPaths(int sessionNumber, ArrayList<BidPoint> pAgentABids, ArrayList<BidPoint> pAgentBBids) 
	{
		double[][] lAgentAUtilities = new double[pAgentABids.size()][2];
		double[][] lAgentBUtilities = new double[pAgentBBids.size()][2];        
		try
		{
			int i=0;
			for (BidPoint p:pAgentABids)
			{
				lAgentAUtilities [i][0] = p.getUtilityA();
				lAgentAUtilities [i][1] = p.getUtilityB();
				i++;
			}
			i=0;
			for (BidPoint p:pAgentBBids)
			{
				lAgentBUtilities [i][0] = p.getUtilityA();
				lAgentBUtilities [i][1] = p.getUtilityB();
				i++;
			}

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}

	}

	public Domain getDomain() {
		return domain;
	}


	public UtilitySpace getAgentAUtilitySpace() {
		return getAgentUtilitySpaces(ALTERNATING_OFFERS_AGENT_A_INDEX);
	}

	public UtilitySpace getAgentBUtilitySpace() {
		return getAgentUtilitySpaces(ALTERNATING_OFFERS_AGENT_B_INDEX);
	}

	public SimpleElement domainToXML(){
		return domain.toXML(); 		
	}

	public BidSpace getBidSpace() { 
		bidSpace = BidSpaceCache.getBidSpace(getAgentAUtilitySpace(), getAgentBUtilitySpace());
		if(bidSpace==null) {
			try {
				bidSpace=new BidSpace(getAgentAUtilitySpace(),getAgentBUtilitySpace());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return bidSpace;     	
	}


	public String getAgentAname() {
		return getAgentName(ALTERNATING_OFFERS_AGENT_A_INDEX);
	}

	public String getAgentBname() {
		return getAgentName(ALTERNATING_OFFERS_AGENT_B_INDEX);
	}

	public HashMap<AgentParameterVariable,AgentParamValue>  getAgentAparams() {
		return getAgentParams(ALTERNATING_OFFERS_AGENT_A_INDEX);
	}

	public HashMap<AgentParameterVariable,AgentParamValue>  getAgentBparams() {
		return getAgentParams(ALTERNATING_OFFERS_AGENT_B_INDEX);
	}

	public ProfileRepItem getProfileArep() {
		return getProfileRepItems(ALTERNATING_OFFERS_AGENT_A_INDEX);
	}

	public ProfileRepItem getProfileBrep() {
		return getProfileRepItems(ALTERNATING_OFFERS_AGENT_B_INDEX);
	}

	public AgentRepItem getAgentARep() {
		return getAgentRepItem(ALTERNATING_OFFERS_AGENT_A_INDEX);
	}
	public AgentRepItem getAgentBRep() {
		return getAgentRepItem(ALTERNATING_OFFERS_AGENT_B_INDEX);
	}

	public String getAgentAStrategyName() {
		return getAgentARep().getName();

	}
	public String getAgentBStrategyName() {
		return getAgentBRep().getName();

	}
	public void setBidSpace(BidSpace pBidSpace) {
		bidSpace = pBidSpace;
	}


	/**
	 * @return total available time for entire nego, in seconds.
	 */
	public Integer getTotalTime() { return totalTime; }

	public String getStartingAgent(){
		return startingAgent;
	}

	public void setAgentA(Agent agent) {
		agentA=agent;
	}
	public void setAgentB(Agent agent) {
		agentB=agent;
	}

	public Agent getAgentA() {
		return agentA;
	}
	public Agent getAgentB() {
		return agentB;
	}

	public AlternatingOffersBilateralAtomicNegoSession getSessionRunner() {
		return sessionrunner;    
	}

	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

	public NegotiationOutcome getNegotiationOutcome() {
		return outcome;
	}

	public Agent getAgent(int index) {
		// TODO Auto-generated method stub
		return null;
	}

	/*-------------------------------------- TOURNAMENT BUILDING -------------------------*/

	static final String AGENT_A_NAME="Agent A";
	static final String AGENT_B_NAME="Agent B";

	/** 
	 * Called when you press start button in Tournament window via reflection in {@link TournamentRunner}.
	 * This builds the sessions array from given Tournament vars 
	 * The procedure skips sessions where both sides use the same preference profiles.
	 * @throws exception if something wrong with the variables, eg not set. 
	 */
    public static ArrayList<Protocol> getTournamentSessions(Tournament tournament) throws Exception
    {
        return getTournamentSessions(tournament, tournament.getOption("playAgainstSelf") != 0, tournament.getOption("playBothSides") != 0);
    }
    
	public static ArrayList<Protocol> getTournamentSessions(Tournament tournament, boolean selfplay, boolean both_sides) throws Exception {
		session_number=1;
		// get agent A and B value(s)
		ArrayList<AgentVariable> agents = tournament.getAgentVars();

		if (Global.DECOUPLED_AGENTS_ENABLED || Global.DISTRIBUTED_TOURNAMENTS_ENABLED) {
			ArrayList<AgentVariable> decoupledAgents = tournament.getDecoupledAgentVars();
			agents.get(0).getValues().addAll(decoupledAgents.get(0).getValues());
			agents.get(1).getValues().addAll(decoupledAgents.get(1).getValues());
		}
		if (agents.size()!=2) throw new IllegalStateException("Tournament does not contain 2 agent variables");
		ArrayList<TournamentValue> agentAvalues=agents.get(0).getValues();
		if (agentAvalues.isEmpty()) 
			throw new IllegalStateException("Agent A does not contain any values!");
		ArrayList<TournamentValue> agentBvalues=agents.get(1).getValues();
		if (agentBvalues.isEmpty()) 
			throw new IllegalStateException("Agent B does not contain any values!");

		ArrayList<ProfileRepItem> profiles=tournament.getProfiles();

		// we need to exhaust the possible combinations of all variables.
		// we iterate explicitly over the profile and agents, because we need to permutate
		// only the parameters for the selected agents.
		ArrayList<Protocol>sessions =new ArrayList<Protocol>();
		ArrayList<ProfileRepItem> skipProfiles =  new ArrayList<ProfileRepItem>();
		for (ProfileRepItem profileA: profiles) {
			for (ProfileRepItem profileB: profiles) {
				if(skipProfiles.contains(profileB)) continue;
				if (!(profileA.getDomain().equals(profileB.getDomain())) ) continue; // domains must match. Optimizable by selecting matching profiles first...
				if (profileA.equals(profileB)) continue;
				for (TournamentValue agentAval: agentAvalues ) {
					AgentRepItem agentA=((AgentValue)agentAval).getValue();
					for (TournamentValue agentBval: agentBvalues) {
						if(agentAval.equals(agentBval) && !selfplay) continue;
						AgentRepItem agentB=((AgentValue)agentBval).getValue();
						sessions.addAll(allParameterCombis(tournament, agentA,agentB,profileA,profileB));
//						System.out.println(agentA + ", " + profileA + " vs. ");
//						System.out.println(agentB + ", " + profileB);
					}
				}

			}
			if (!both_sides)
				skipProfiles.add(profileA);
		}
		return sessions;
	}

	/** 
	 * This is a recursive function that iterates over all *parameters* and tries all values for each,
	 * recursively calling itself to iterate over the remaining parameters.
	 * This only runs over parameters, not the other variables (Agents and Profiles)
	 * because there may be many parameters and we need to filter 
	 * Not all permutations of the vars are acceptable, for instance domains have to be idnetical.
	 * One optimization: 
	 * @param sessions is the final result: all valid permutations of variables. 
	 * @param varnr is the index of the variable in the variables array.
	 * @throws exception if one of the variables contains no values (which would prevent any 
	 * running sessions to be created with that variable.
	 */
	protected static ArrayList<AlternatingOffersProtocol> allParameterCombis(Tournament tournament, AgentRepItem agentA,AgentRepItem agentB,
			ProfileRepItem profileA, ProfileRepItem profileB) throws Exception {
		ArrayList<AssignedParameterVariable> allparameters;
		allparameters=tournament.getParametersOfAgent(agentA,AGENT_A_NAME);
		allparameters.addAll(tournament.getParametersOfAgent(agentB,AGENT_B_NAME)); // are the run-time names somewhere?
		ArrayList<AlternatingOffersProtocol> sessions=new ArrayList<AlternatingOffersProtocol>();
		allParameterCombis(tournament, allparameters,sessions,profileA,profileB,agentA,agentB,new ArrayList<AssignedParamValue>());
		return sessions;
	}

	/**
	 * adds all permutations of all NegotiationSessions to the given sessions array.
	 * Note, this is not threadsafe, if called from multiple threads the session number will screw up.
	 * @param allparameters the parameters of the agents that were selected for this nego session.
	 * @param sessions
	 * @throws Exception
	 */
	protected static void allParameterCombis(Tournament tournament, ArrayList<AssignedParameterVariable> allparameters, ArrayList<AlternatingOffersProtocol> sessions,
			ProfileRepItem profileA, ProfileRepItem profileB,
			AgentRepItem agentA, AgentRepItem agentB,ArrayList<AssignedParamValue> chosenvalues) throws Exception {
		
		if (allparameters.isEmpty()) {
			// separate the parameters into those for agent A and B.
			HashMap<AgentParameterVariable,AgentParamValue> paramsA = new HashMap<AgentParameterVariable,AgentParamValue>();
			HashMap<AgentParameterVariable,AgentParamValue> paramsB = new HashMap<AgentParameterVariable,AgentParamValue>();
			int i=0;
			for (AssignedParamValue v: chosenvalues) {
				if (v.agentname==AGENT_A_NAME) paramsA.put(allparameters.get(i).parameter, v.value); 
				else paramsB.put(allparameters.get(i).parameter,v.value);
				i++;
			}
			// TODO compute total #sessions. Now fixed to 9999
			int numberOfSessions = 1;
			if(tournament.getVariables().get(Tournament.VARIABLE_NUMBER_OF_RUNS ).getValues().size()>0)
				numberOfSessions = ((TotalSessionNumberValue)( tournament.getVariables().get(Tournament.VARIABLE_NUMBER_OF_RUNS).getValues().get(0))).getValue();
			
			// numberOfSessions is always 1
//			System.out.println("Number of sessions: " + numberOfSessions);
			/*			AlternatingOffersProtocol session =new AlternatingOffersProtocol(agentA, agentB, profileA,profileB,
		    		AGENT_A_NAME, AGENT_B_NAME,paramsA,paramsB,session_number++, numberOfSessions , false,
		    		tournament_gui_time, tournament_non_gui_time,1);//TODO::TournamentNumber) ;*/
			AgentRepItem[] agents = new AgentRepItem[2];
			agents[0] = agentA;
			agents[1] = agentB;
			ProfileRepItem[] profiles = new ProfileRepItem[2];
			profiles[0] = profileA;
			profiles[1] = profileB;
			HashMap<AgentParameterVariable,AgentParamValue>[] params = new HashMap[2];
			params[0] = paramsA;
			params[1] = paramsB;
			AlternatingOffersProtocol session = new AlternatingOffersProtocol(agents, profiles,params, tournament.getOptions()); 
			for (int k = 0; k < numberOfSessions; k++) {
				sessions.add(session);
			}
			if (!Global.LOW_MEMORY_MODE) {
				//check if the analysis is already made for the prefs. profiles
				BidSpace bidSpace = BidSpaceCache.getBidSpace(session.getAgentAUtilitySpace(), session.getAgentBUtilitySpace());
				if(bidSpace!=null) {
					session.setBidSpace(bidSpace);
				} else {
					bidSpace = new BidSpace(session.getAgentAUtilitySpace(),session.getAgentBUtilitySpace());
					BidSpaceCache.addBidSpaceToCash(session.getAgentAUtilitySpace(), session.getAgentBUtilitySpace(), bidSpace);
					session.setBidSpace(bidSpace);
				}
			}
		} else {
			// pick next variable, and compute all permutations.
			AssignedParameterVariable v=allparameters.get(0);
			// remove that variable from the list... using clone to avoid damaging the original being used higher up
			ArrayList<AssignedParameterVariable> newparameters=(ArrayList<AssignedParameterVariable>)allparameters.clone();
			newparameters.remove(0);
			ArrayList<TournamentValue> tvalues=v.parameter.getValues();
			if (tvalues.isEmpty()) throw new IllegalArgumentException("tournament parameter "+v.parameter+" has no values!");
			// recursively do all permutations for the remaining vars.
			for (TournamentValue tv: tvalues) {
				ArrayList<AssignedParamValue> newchosenvalues=(ArrayList<AssignedParamValue>) chosenvalues.clone();
				newchosenvalues.add(new AssignedParamValue((AgentParamValue)tv,v.agentname));
				allParameterCombis(tournament, newparameters, sessions, profileA,  profileB,agentA,  agentB,newchosenvalues);
			} 
		}	    	
	}

	@Override
	public void cleanUP() 
	{
		agentA = null;
		agentB = null;
	}
}