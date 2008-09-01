package negotiator.tournament;

import negotiator.Main;
import negotiator.NegotiationOutcome;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.ArrayList;

import negotiator.actions.*;


import java.util.Random;
import negotiator.*;
import negotiator.exceptions.Warning;
import negotiator.gui.SessionFrame;
import negotiator.repository.*;
import negotiator.tournament.VariablesAndValues.*;



/**
 *
 * NegotiationSession is both the storage place for a negotiation session 
 * and the run-time object / thread doing a session.
 * Actually the SessionRunner does most of handling the nego session, here we have only code
 * for enforcing the time deadline.
 * 
 * @author W.Pasman. Lots of old code from NegotiationSession and NegotmationManager
 * 
 */
public class NegotiationSession2 implements Runnable {
	AgentRepItem agentArep;
	AgentRepItem agentBrep;
    ProfileRepItem profileArep;
    ProfileRepItem profileBrep;
    String agentAname;
    String agentBname;
    ArrayList<AgentParamValue> agentAparams=new ArrayList<AgentParamValue> ();
    ArrayList<AgentParamValue> agentBparams=new ArrayList<AgentParamValue> ();
    int sessionNumber;
    int sessionTotalNumber;
    boolean startingWithA=true;
    NegotiationEventListener actionEventListener;
	String startingAgent; // agentAname or agnetBname
	Integer totalTime; // will be set only AFTER running the session, because it depends on whether agent isUIAgent() or not
	NegotiationOutcome outcome;

    public static int NON_GUI_NEGO_TIME = 120;
    public static int GUI_NEGO_TIME=60*30; 	// Nego time if a GUI is involved in the nego


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
     * @throws Exception
     */
    public NegotiationSession2(AgentRepItem agtA, AgentRepItem agtB, ProfileRepItem profA, ProfileRepItem profB,
    		String nameA, String nameB,ArrayList<AgentParamValue> agtApar,ArrayList<AgentParamValue> agtBpar,
    		int sessionnr, int totalsessions,boolean forceStartA,
    		NegotiationEventListener ael) throws Exception {
    	agentArep=agtA;
    	agentBrep=agtB;
    	profileArep=profA;
    	profileBrep=profB;
    	agentAname=nameA;
    	agentBname=nameB;
    	if (agtApar!=null) agentAparams=agtApar;
    	if (agtBpar!=null) agentBparams=agtBpar;
    	sessionNumber=sessionnr;
    	sessionTotalNumber=totalsessions;
    	startingWithA=forceStartA;
    	actionEventListener=ael;
    	startingAgent=agentAname;
    	if ( (!startingWithA) && new Random().nextInt(2)==1) { 
    		startingAgent=agentBname;
    	}
    	check();
    }
   
    
    void check() throws Exception {
    	if (!(profileArep.getDomain().equals(profileBrep.getDomain())))
    		throw new IllegalArgumentException("profiles "+profileArep+" and "+profileBrep+" have a different domain.");
    }

    /***************** RUN A NEGO SESSION. code below comes from NegotiationManager ****************************/
    private Thread negoThread = null;
    SessionFrame sf; // this will show the outcomes. Not really a job for NegoSession, TODO remove this,
 
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
        } catch (Exception e) { new Warning("Problem starting negotiation:"+e); }
    }

    public void startNegotiation() throws Exception {
        sf = new SessionFrame(agentAname, agentBname);
        sf.setVisible(true);
        Main.log("Starting negotiations...");
        for(int i=0;i<sessionTotalNumber;i++) {
            Main.log("Starting session " + String.valueOf(i+1));
            runNegotiationSession(i+1);
        }
    }
    
    
    protected void runNegotiationSession(int nr)  throws Exception
    {
    	sessionNumber=nr;
        //NegotiationSession nego = new NegotiationSession(agentA, agentB, nt, sessionNumber, sessionTotalNumber,agentAStarts,actionEventListener,this);
    	SessionRunner sessionrunner=new SessionRunner(this);
    	totalTime=sessionrunner.totTime;
    	if(Main.fDebug) {
    		sessionrunner.run();
        } else {
        	negoThread = new Thread(sessionrunner);
            System.out.println("nego start. "+System.currentTimeMillis()/1000);
            negoThread.start();
        	try {
        		synchronized (this) {
        			System.out.println("waiting NEGO_TIMEOUT="+totalTime);
        			 // wait will unblock early if negotiation is finished in time.
    				wait(totalTime*1000);
        		}
        	} catch (InterruptedException ie) {
        		System.out.println("wait cancelled:"+ie.getMessage()); ie.printStackTrace();}
        	}
        	//System.out.println("nego finished. "+System.currentTimeMillis()/1000);
        	//synchronized (this) { try { wait(1000); } catch (Exception e) { System.out.println("2nd wait gets exception:"+e);} }
        
        	if (negoThread!=null&&negoThread.isAlive()) {
        		try {
        			negoThread.stop(); // kill the stuff
        			 // Wouter: this will throw a ThreadDeath Error into the nego thread
        			 // The nego thread will catch this and exit immediately.
        			 // Maybe it should not even try to catch that.
        		} catch (Exception e) {
        			System.out.println("problem stopping the nego:"+e.getMessage());
        			e.printStackTrace();
        		
        		}
        }
        // add path to the analysis chart
        // TODO Wouter: I removed this, not the job of a negotiationsession. We have no nt here anyway.
        //if (nt.getBidSpace()!=null)
        //	nt.addNegotiationPaths(sessionNumber, nego.getAgentABids(), nego.getAgentBBids());
        	
    	if(sessionrunner.no==null) {
    		try {
    		sessionrunner.newOutcome(null, 0, 0, new EndNegotiation(null), "nego result was null(aborted)");
    		} catch (Exception err) { new Warning("error during creation of new outcome:"+err); }
    		// don't bother about max utility, both have zero anyway.
    	}
    		outcome=sessionrunner.no;
    		sf.addNegotiationOutcome(outcome);        // add new result to the outcome list. 
        try {
            BufferedWriter out = new BufferedWriter(new FileWriter("outcomes.xml",true));
            out.write(""+outcome.toXML());
            out.close();
        } catch (Exception e) {
        	new Warning("Exception during writing outcomes:"+e);
        }
        
    }
    public void stopNegotiation() {
        if (negoThread.isAlive()) {
            try {
//                negoThread.interrupt();
                negoThread.stop();
            } catch (Exception e) {
            }
        }
        return;
    }
    
    public String toString() {
    	return "NegotiationSession["+agentArep.getName()+" versus "+agentBrep.getName()+"]";
    }
 
}
