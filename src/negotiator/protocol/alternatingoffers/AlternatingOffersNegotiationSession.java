package negotiator.protocol.alternatingoffers;

import negotiator.Domain;

import negotiator.Agent;
import negotiator.Global;
import negotiator.NegotiationOutcome; 

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import negotiator.actions.*;
import negotiator.analysis.BidPoint;
import negotiator.analysis.BidSpace;


import java.util.Random;

import javax.swing.JOptionPane;

import negotiator.*;
import negotiator.events.ActionEvent;
import negotiator.events.LogMessageEvent;
import negotiator.exceptions.Warning;

import negotiator.gui.SessionFrame;

import negotiator.protocol.NegotiationSession;
import negotiator.protocol.SessionRunner;
import negotiator.repository.*;
import negotiator.tournament.TournamentRunner;
import negotiator.tournament.VariablesAndValues.*;
import negotiator.utility.UtilitySpace;
import negotiator.xml.SimpleDOMParser;
import negotiator.xml.SimpleElement;



/**
 *
 * NegotiationSession is both the storage place for a negotiation session 
 * and the run-time object / thread doing a session.
 * Actually the SessionRunner does most of handling the nego session, here we have only code
 * for enforcing the time deadline.
 * It is done this way because it is much easier to keep a single timer running than to create a new timer 
 * every time a user agent is called, and because the SessionRunner protocol looks much cleaner without
 * such deadline hassles all the time.
 * BUT it restricts the deadline to a single global deadline, and prevents something like
 * an additional time after each bid.
 * 
 * @author W.Pasman. Lots of old code from NegotiationSession and NegotmationManager
 * 
 */
public class AlternatingOffersNegotiationSession extends NegotiationSession {
	public static final int ALTERNATING_OFFERS_AGENT_A_INDEX = 0;
	public static final int ALTERNATING_OFFERS_AGENT_B_INDEX = 1;

	/** tournamentNumber is the tournament.TournamentNumber, or -1 if this session is not part of a tournament*/
    int tournamentNumber=-1; 
    int sessionTotalNumber;
    int sessionNumber; // the main session number: increases with different session setups
    public int sessionTestNumber; // the sub-session number: counts from 1 to sessionTotalNumber
    
    
    TournamentRunner tournamentRunner;
    boolean startingWithA=true;
    ArrayList<NegotiationEventListener> actionEventListener = new ArrayList<NegotiationEventListener>();
	String startingAgent; // agentAname or agnetBname
	private Integer totalTime; // will be set only AFTER running the session, because it depends on whether agent isUIAgent() or not
	NegotiationOutcome outcome;
	private String log;
	
	
    public int non_gui_nego_time = 120;
    public int gui_nego_time=60*30; 	// Nego time if a GUI is involved in the nego


    /** fields copied from the NegotiationTemplate class */
    

    //private Analysis fAnalysis;
	private BidSpace bidSpace=null;
    //private int totalTime; // total available time for nego, in seconds.
    
	private SimpleElement fAdditional;	
    /** END OF fields copied from the NegotiationTemplate class */
    
    
    /** 
     * Creates a new instance of Negotiation 
     * @param agtA AgentRepItem (agent descriptor) for agent A.
     * @param agtB idem agent B.
     * @param profA ProfileRep Item (the profile descriptor) for agent A.
     * @param profB idem agent B.
     * @param nameA the run-name for agent A. This is not the class name!
     * @param nameB idem agent B.
     * @param agtApar parameters for Agent A. null is equivalent to empty parameters list.
     * @param agtBpar idem for agent B.
     * @param sessionnr
     * @param totalsessions
     * @param forceStartA true to force start with agent A. with false, start agent is chosen randomly.
     * @param ael is the callback point for bidding events. null means you won't be given call backs.
     * @param gui_time is the time (ms) available for normal GUI agents
     * @param non_gui_time is the time(ms) available for agents that are agents involving user interaction 
     * 		which is indicated by Agent.isUIAgent().
     * @param tournamentnr is the number of the tournament of which this session is a part, or -1 if this session is no part of a tournament.
     * @throws Exception
     */
    public AlternatingOffersNegotiationSession(AgentRepItem agtA, AgentRepItem agtB, ProfileRepItem profA, ProfileRepItem profB,
    		String nameA, String nameB,HashMap<AgentParameterVariable,AgentParamValue> agtApar,HashMap<AgentParameterVariable,AgentParamValue> agtBpar,
    		int sessionnr, int totalsessions,boolean forceStartA, int gui_time, int non_gui_time, int tournamentnr) throws Exception {
    	agentArep=agtA;
    	agentBrep=agtB;    	
    	continueSetup( profA,  profB, nameA,nameB, agtApar, agtBpar, sessionnr, totalsessions, forceStartA,gui_time,non_gui_time, tournamentnr);
    }
    
/*    public AlternatingOffersNegotiationSession(Agent agtA, Agent agtB, ProfileRepItem profA, ProfileRepItem profB,
    		String nameA, String nameB,HashMap<AgentParameterVariable,AgentParamValue> agtApar,HashMap<AgentParameterVariable,AgentParamValue> agtBpar,
    		int sessionnr, int totalsessions,boolean forceStartA, int gui_time, int non_gui_time, int tournamentnr) throws Exception {
    	agentA=agtA;
    	agentB=agtB;
    	continueSetup( profA,  profB, nameA,nameB, agtApar, agtBpar, sessionnr, totalsessions, forceStartA,gui_time,non_gui_time,tournamentnr);
    }
*/
    /** non_tournament_next_session_nr is used to auto-number non-tournament sessions */
    static int non_tournament_next_session_nr=1;
    
    
    private void continueSetup(ProfileRepItem profA, ProfileRepItem profB,
	String nameA, String nameB,HashMap<AgentParameterVariable,AgentParamValue> agtApar,HashMap<AgentParameterVariable,AgentParamValue> agtBpar,
	int sessionnr, int totalsessions,boolean forceStartA, int gui_time, int non_gui_time,int tournamentnr) throws Exception {

        non_gui_nego_time=non_gui_time;
    	gui_nego_time=gui_time;
    	tournamentNumber=tournamentnr;
    	setProfileArep(profA);
    	setProfileBrep(profB);
    	setAgentAname(nameA);
    	setAgentBname(nameB);
    	if (agtApar!=null) setAgentAparams(agtApar);
    	if (agtBpar!=null) setAgentBparams(agtBpar);
    	sessionNumber=sessionnr;
    	if (tournamentNumber==-1) sessionNumber=non_tournament_next_session_nr++;
    	sessionTotalNumber=totalsessions;
    	startingWithA=forceStartA;
    	//actionEventListener.add(ael);
    	startingAgent=getAgentAname();
    	if ( (!startingWithA) && new Random().nextInt(2)==1) { 
    		startingAgent=getAgentBname();
    	}
   		fFileName = getProfileArep().getDomain().getURL().getFile();
		this.agentAUtilitySpaceFileName = getProfileArep().getURL().getFile();
		this.agentBUtilitySpaceFileName = getProfileBrep().getURL().getFile();      	
		loadFromFile(fFileName);
    	
    	check();
    }
    
    
    public void addNegotiationEventListener(NegotiationEventListener listener) {
    	if(!actionEventListener.contains(listener))
    		actionEventListener.add(listener);
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
	        	lAgentAUtilities [i][0] = p.utilityA;
	        	lAgentAUtilities [i][1] = p.utilityB;
	        	i++;
        	}
        	i=0;
        	for (BidPoint p:pAgentBBids)
        	{
	        	lAgentBUtilities [i][0] = p.utilityA;
	        	lAgentBUtilities [i][1] = p.utilityB;
	        	i++;
        	}
	        
        } catch (Exception e) {
			// TODO: handle exception
        	e.printStackTrace();
		}
		
	}
	public int getNrOfBids(){
		return sessionrunner.fAgentABids.size()+sessionrunner.fAgentBBids.size();
	}
	
	//alinas code
	public double[][] getNegotiationPathA(){
		System.out.println("fAgentABids "+sessionrunner.fAgentABids.size());
		double[][] lAgentAUtilities = new double[2][sessionrunner.fAgentABids.size()];
		try
        {
			int i=0;
	    	for (BidPoint p:sessionrunner.fAgentABids)
	    	{
	        	lAgentAUtilities [0][i] = p.utilityA;
	        	lAgentAUtilities [1][i] = p.utilityB;
	        	i++;
	    	}
        } catch (Exception e) {
			e.printStackTrace();
        	return null;
		}
    	
		return lAgentAUtilities; 
	}
	public double[][] getNegotiationPathB(){
		System.out.println("fAgentBBids "+sessionrunner.fAgentBBids.size());
		double[][] lAgentBUtilities = new double [2][sessionrunner.fAgentBBids.size()];  
		try{
			int i=0;
	    	for (BidPoint p:sessionrunner.fAgentBBids)
	    	{
	        	lAgentBUtilities [0][i] = p.utilityA;
	        	lAgentBUtilities [1][i] = p.utilityB;
	        	i++;
	    	}
	 	} catch (Exception e) {
		   	e.printStackTrace();
		   	return null;
		}
		return lAgentBUtilities;
	}
	
	/**
	 * 
	 * @param fileName
	 * @param mf points to the MainFrame GUI that currently also holds the application data (...)
	 * @throws Exception if there are problems reading the file.
	 */
	/*public static void loadParamsFromFile (String fileName, MainFrame mf) throws Exception
	{
		SimpleDOMParser parser = new SimpleDOMParser();
		try {
			BufferedReader file = new BufferedReader(new FileReader(new File(fileName)));		
			SimpleElement root = parser.parse(file);

            mf.setNemberOfSessions(root.getAttribute("number_of_sessions"));
            SimpleElement xml_agentA = (SimpleElement)(root.getChildByTagName("agent")[0]);
            mf.setAgentAName(xml_agentA.getAttribute("name"));
            mf.setAgentAClassName(xml_agentA.getAttribute("class"));
            mf.setAgentAUtilitySpace((new File(fileName)).getParentFile().toString()+"/"+  xml_agentA.getAttribute("utility_space"));
            SimpleElement xml_agentB = (SimpleElement)(root.getChildByTagName("agent")[1]);
            mf.setAgentBName(xml_agentB.getAttribute("name"));
            mf.setAgentBClassName(xml_agentB.getAttribute("class"));
            mf.setAgentBUtilitySpace((new File(fileName)).getParentFile().toString()+"/"+  xml_agentB.getAttribute("utility_space"));
        } catch (Exception e) {
            throw new IOException("Problem loading parameters from "+fileName+": "+e.getMessage());
        }
    }
    */
    
    public Domain getDomain() {
        return domain;
    }


	public UtilitySpace getAgentAUtilitySpace() {
		return getAgentUtilitySpaces(ALTERNATING_OFFERS_AGENT_A_INDEX);
	}

	public UtilitySpace getAgentBUtilitySpace() {
		return getAgentUtilitySpaces(ALTERNATING_OFFERS_AGENT_B_INDEX);
	}
	
	public void setLog(String str){
		log = str;
	}
	public String getLog(){
		return log;
	}
    public SimpleElement domainToXML(){
    	return domain.toXML(); 		
    }

     /**
      * @return total available time for entire nego, in seconds.
      */
    public Integer getTotalTime() { return totalTime; }

    public BidSpace getBidSpace() { 
    	if(bidSpace==null) {
    		try {    	
    			bidSpace=new BidSpace(getAgentAUtilitySpace(),getAgentBUtilitySpace());
    		} catch (Exception e) {
    			e.printStackTrace();
			}
    	}
    	return bidSpace;     	
    }
    public String getStartingAgent(){
    	return startingAgent;
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
		return profileArep;
	}

	public ProfileRepItem getProfileBrep() {
		return profileBrep;
	}

	public synchronized void fireNegotiationActionEvent(Agent actorP,Action actP,int roundP,long elapsed,
			double utilA,double utilB,String remarks) {
		for(NegotiationEventListener listener : actionEventListener) {
			listener.handleActionEvent(new ActionEvent(this,actorP, actP, roundP, elapsed, utilA, utilB, remarks ));
		}
	}
    public synchronized void fireLogMessage(String source, String log) { 
    	for(NegotiationEventListener listener : actionEventListener) { 
        	listener.handleLogMessageEvent(new LogMessageEvent(this, source, log));
    	}
		
	}

    public Agent getAgentA() {
    	if(agentA==null)
    		if(sessionrunner!=null)
    			return sessionrunner.agentA;
    		else
    			return null;
    	else
    	return agentA;
    }
    public Agent getAgentB() {
    	if(agentB==null)
    		if(sessionrunner!=null)
    			return sessionrunner.agentB;
    		else
    			return null;
    	else
    	return agentB;
    }

    public String getAgentAStrategyName() {
    	if(agentArep!=null)
    		return agentArep.getName();
    	else
    		return "";//agentA.getClass().toString();
    	
    }
    public String getAgentBStrategyName() {
    	if(agentBrep!=null)
    		return agentBrep.getName();
    	else
    		return "";//agentB.getClass().toString();
    }
    public void setBidSpace(BidSpace pBidSpace) {
    	bidSpace = pBidSpace;
    }
    public void setAdditional(SimpleElement e) {
    	fAdditional = e;
    }
    public void setTournamentRunner(TournamentRunner runner) {
    	tournamentRunner = runner; 
    }
    public int getTournamentNumber() { 
    	return tournamentNumber; 
    }
    public int getSessionNumber() { 
    	return sessionNumber; 
    }
    public int getTestNumber() { 
    	return sessionTestNumber; 
    }
}
