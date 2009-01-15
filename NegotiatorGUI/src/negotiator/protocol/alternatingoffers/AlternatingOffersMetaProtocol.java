package negotiator.protocol.alternatingoffers;

import java.io.*;
import java.util.*;

import negotiator.*;
import negotiator.actions.Action;
import negotiator.analysis.*;
import negotiator.events.ActionEvent;
import negotiator.events.LogMessageEvent;
import negotiator.exceptions.Warning;
import negotiator.protocol.MetaProtocol;
import negotiator.repository.AgentRepItem;
import negotiator.repository.ProfileRepItem;
import negotiator.tournament.TournamentRunner;
import negotiator.tournament.VariablesAndValues.AgentParamValue;
import negotiator.tournament.VariablesAndValues.AgentParameterVariable;
import negotiator.utility.UtilitySpace;
import negotiator.xml.*;


public class AlternatingOffersMetaProtocol implements MetaProtocol {


     /** tournamentNumber is the tournament.TournamentNumber, or -1 if this session is not part of a tournament*/
    int tournamentNumber=-1; 
    int sessionTotalNumber;
    int sessionNumber; // the main session number: increases with different session setups
    public int sessionTestNumber; // the sub-session number: counts from 1 to sessionTotalNumber
    
    
    TournamentRunner tournamentRunner;
    boolean startingWithA=true;
    ArrayList<NegotiationEventListener> actionEventListener = new ArrayList<NegotiationEventListener>();
	String startingAgent; // agentAname or agnetBname

	NegotiationOutcome outcome;
	private String log;
	
	private Integer totalTime; // will be set only AFTER running the session, because it depends on whether agent isUIAgent() or not	
    public int non_gui_nego_time = 120;
    public int gui_nego_time=60*30; 	// Nego time if a GUI is involved in the nego


    private Agent agentA;
    private Agent agentB;
    
    /** fields copied from the NegotiationTemplate class */
    
	private String fFileName;    
    
	private SimpleElement fAdditional;
	
	AlternatingOffersProtocol sessionrunner;
    /** END OF fields copied from the NegotiationTemplate class */
    
    private AlternatingOffersNegotiationSession session;
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
    public AlternatingOffersMetaProtocol(AgentRepItem agtA, AgentRepItem agtB, ProfileRepItem profA, ProfileRepItem profB,
    		String nameA, String nameB,HashMap<AgentParameterVariable,AgentParamValue> agtApar,HashMap<AgentParameterVariable,AgentParamValue> agtBpar,
    		int sessionnr, int totalsessions,boolean forceStartA, int gui_time, int non_gui_time, int tournamentnr) throws Exception {
    	agentArep=agtA;
    	agentBrep=agtB;
    	
    	continueSetup( profA,  profB, nameA,nameB, agtApar, agtBpar, sessionnr, totalsessions, forceStartA,gui_time,non_gui_time, tournamentnr);
    }
    
    public AlternatingOffersMetaProtocol(Agent agtA, Agent agtB, ProfileRepItem profA, ProfileRepItem profB,
    		String nameA, String nameB,HashMap<AgentParameterVariable,AgentParamValue> agtApar,HashMap<AgentParameterVariable,AgentParamValue> agtBpar,
    		int sessionnr, int totalsessions,boolean forceStartA, int gui_time, int non_gui_time, int tournamentnr) throws Exception {
    	agentA=agtA;
    	agentB=agtB;
    	continueSetup( profA,  profB, nameA,nameB, agtApar, agtBpar, sessionnr, totalsessions, forceStartA,gui_time,non_gui_time,tournamentnr);
    }

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
		loadFromFile(fFileName);
    	check();
    }
    
    
    public void addNegotiationEventListener(NegotiationEventListener listener) {
    	if(!actionEventListener.contains(listener))
    		actionEventListener.add(listener);
    }
    

    /***************** RUN A NEGO SESSION. code below comes from NegotiationManager ****************************/
    private Thread negoThread = null;
    //SessionFrame sf; // this will show the outcomes. Not really a job for NegoSession, TODO remove this,
 
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
            //Main.log("Starting session " + String.valueOf(i+1));
            runNegotiationSession(i+1);
        }
    }
    
    
    /** do test run of negotiation session.
     * There may be multiple test runs of a single session, for isntance to take the average score.
     * returns the result in the global field "outcome"
     * @param nr is the sessionTestNumber
     * @throws Exception
     * 
     */
    protected void runNegotiationSession(int nr)  throws Exception
    {
    	java.lang.ClassLoader loaderA = ClassLoader.getSystemClassLoader()/*new java.net.URLClassLoader(new URL[]{agentAclass})*/;
    	agentA = (Agent)(loaderA.loadClass(session.agentArep.getClassPath()).newInstance());
   		agentA.setName(session.getAgentAname());
   		//session.setAgentA(agentA);

   		java.lang.ClassLoader loaderB =ClassLoader.getSystemClassLoader();
    	agentB = (Agent)(loaderB.loadClass(session.agentBrep.getClassPath()).newInstance());
    	agentB.setName(session.getAgentBname());
    	//session.setAgentB(agentB);
    	

    	sessionTestNumber=nr;
    	if(tournamentRunner!= null) tournamentRunner.fireNegotiationSessionEvent(this);
        //NegotiationSession nego = new NegotiationSession(agentA, agentB, nt, sessionNumber, sessionTotalNumber,agentAStarts,actionEventListener,this);
    	//SessionRunner sessionrunner=new SessionRunner(this);
    	sessionrunner=new AlternatingOffersProtocol(agentA, 
    							agentA, 
    							session.getAgentAUtilitySpace(), 
    							session.getAgentBUtilitySpace(), 
    							non_gui_nego_time);

    	if(Global.fDebug) {
    		sessionrunner.run();
        } else {
        	negoThread = new Thread(sessionrunner);
            System.out.println("nego start. "+System.currentTimeMillis()/1000);
            negoThread.start();
        	try {
        		synchronized (this) {
        			System.out.println("waiting NEGO_TIMEOUT="+totalTime*1000);
        			 // wait will unblock early if negotiation is finished in time.
    				wait(totalTime*1000);
        		}
        	} catch (InterruptedException ie) { new Warning("wait cancelled:",ie); }
        }
        	//System.out.println("nego finished. "+System.currentTimeMillis()/1000);
        	//synchronized (this) { try { wait(1000); } catch (Exception e) { System.out.println("2nd wait gets exception:"+e);} }
        
    	stopNegotiation();
    		
        // add path to the analysis chart
        // TODO Wouter: I removed this, not the job of a negotiationsession. We have no nt here anyway.
        //if (nt.getBidSpace()!=null)
        //	nt.addNegotiationPaths(sessionNumber, nego.getAgentABids(), nego.getAgentBBids());
        	
    	if(sessionrunner.no==null) {
    		sessionrunner.JudgeTimeout();
    	}
    		outcome=sessionrunner.no;
    		//sf.addNegotiationOutcome(outcome);        // add new result to the outcome list.
    		if(fAdditional!=null) { 
    			if(outcome.additional==null) {
    				outcome.additional = new SimpleElement("additional");
    			
    			}
    			outcome.additional.addChildElement(fAdditional);
    		}
        try {
            BufferedWriter out = new BufferedWriter(new FileWriter("outcomes.xml",true));
            out.write(""+outcome.toXML());
            out.close();
        } catch (Exception e) {
        	new Warning("Exception during writing s:"+e);
        	e.printStackTrace();
        }
        
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
 
    
    /* methods copied from the NegotiationTemplate class */
    
	
	public void setLog(String str){
		log = str;
	}
	public String getLog(){
		return log;
	}

     /**
      * @return total available time for entire nego, in seconds.
      */
    public Integer getTotalTime() { return totalTime; }
    public String getStartingAgent(){
    	return startingAgent;
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
    public void setAgentA(Agent agent) {
    	agentA=agent;
    }
    public void setAgentB(Agent agent) {
    	agentB=agent;
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

    public AlternatingOffersProtocol getSessionRunner() {
    	return sessionrunner;    
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

	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

	public NegotiationOutcome getNegotiationOutcome() {
		return outcome;
	}

}
