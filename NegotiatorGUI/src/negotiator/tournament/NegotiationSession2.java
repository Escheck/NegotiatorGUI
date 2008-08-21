package negotiator.tournament;

import negotiator.ActionEvent;
import negotiator.Agent;
import negotiator.Main;
import negotiator.NegotiationOutcome;
import negotiator.NegotiationTemplate;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.ArrayList;

import negotiator.actions.*;

import java.util.Date;
import negotiator.utility.UtilitySpace;
import negotiator.analysis.BidPoint;
import negotiator.xml.*;
import java.util.Random;
import negotiator.*;
import negotiator.exceptions.Warning;
import negotiator.gui.SessionFrame;
import negotiator.repository.*;




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
    int sessionNumber;
    int sessionTotalNumber;
    boolean startingWithA=true;
    ActionEventListener actionEventListener;
	AgentRepItem startingAgent;
	Integer totalTime; // will be set only AFTER running the session, because it depends on whether agent isUIAgent() or not
	NegotiationOutcome outcome;

    public static int NON_GUI_NEGO_TIME = 120;
    public static int GUI_NEGO_TIME=60*30; 	// Nego time if a GUI is involved in the nego

    /** 
     * 
     * Creates a new instance of Negotiation 
     * @param agentAStartsP = true to force start with agent A. with false, start agent is chosen randomly.
     * @param actionEventListener is the callback point for bidding events. null means you won't be given call backs.
     * @param waiting_parent is the object that will be waiting on us after we got started , and will be called as waiting_parent.notify();
     **/
    public NegotiationSession2(AgentRepItem agtA, AgentRepItem agtB, ProfileRepItem profA, ProfileRepItem profB,
    		String nameA, String nameB,int sessionnr, int totalsessions,boolean forceStartA,
    		ActionEventListener ael) throws Exception {
    	agentArep=agtA;
    	agentBrep=agtB;
    	profileArep=profA;
    	profileBrep=profB;
    	agentAname=nameA;
    	agentBname=nameB;
    	sessionNumber=sessionnr;
    	sessionTotalNumber=totalsessions;
    	startingWithA=forceStartA;
    	actionEventListener=ael;
    	startingAgent=agentArep;
    	if ( (!startingWithA) && new Random().nextInt(2)==1) { 
    		startingAgent=agentBrep;
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
 
    public void run() {
    	try { 
    		startNegotiation();
    		// only sleep if batch mode????
    		Thread.sleep(5000); // 5 seconds waiting for what???
    		// huh??           System.exit(0);
        } catch (Exception e) { new Warning("Problem starting negotiation:"+e); }
    }

    public void startNegotiation() throws Exception {
        sf = new SessionFrame(agentArep.getName(), agentBrep.getName());
        sf.setVisible(true);
        Main.log("Starting negotiations...");
        for(int i=0;i<sessionTotalNumber;i++) {
            Main.log("Starting session " + String.valueOf(i+1));
            runNegotiationSession(i+1);
        }
    }
    
    
    protected void runNegotiationSession(int sessionNumber)  throws Exception
    {
        //NegotiationSession nego = new NegotiationSession(agentA, agentB, nt, sessionNumber, sessionTotalNumber,agentAStarts,actionEventListener,this);
    	SessionRunner sessionrunner=new SessionRunner(this);
    	totalTime=sessionrunner.totTime;
    	if(Main.fDebug) {
    		sessionrunner.run();	
        } else {
        	negoThread = new Thread(sessionrunner);
            //System.out.println("nego start. "+System.currentTimeMillis()/1000);
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
        // TODO Wouter: I removed this, not the job of a negotiationsession. We have no nt here anyways.
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
    
    
    /*
	public ArrayList<BidPoint> getAgentABids() {
		return fAgentABids;
	}
	
	public ArrayList<BidPoint> getAgentBBids() {
		return fAgentBBids;
	}
	
    public double getOpponentUtility(Agent pAgent, Bid pBid) throws Exception{
    	if(pAgent.equals(agentA)) 
    		return agentB.utilitySpace.getUtility(pBid);
    	else
    		return agentA.utilitySpace.getUtility(pBid);
    }
    public double getOpponentWeight(Agent pAgent, int pIssueID) throws Exception{
    	if(pAgent.equals(agentA)) 
    		return agentB.utilitySpace.getWeight(pIssueID);
    	else
    		return agentA.utilitySpace.getWeight(pIssueID);
    }
    
    public void addAdditionalLog(SimpleElement pElem) {
    	if(pElem!=null)
    		additionalLog.addChildElement(pElem);
    	
    }
    
    // This is the running method of the negotiation thread.
      It contains the work flow of the negotiation. 
     /
    void checkAgentActivity(Agent agent) {
        if(agent.equals(agentA)) agentAtookAction = true;
        else agentBtookAction = true;
        
    }

  
    public Agent otherAgent(Agent ag)
    {
    	if (ag==agentA) return agentB;
    	return agentA;    	
    }
  
    
    public void newOutcome(Agent currentAgent, double utilA, double utilB, Action action, String message) throws Exception {
        UtilitySpace spaceA=nt.getAgentAUtilitySpace();
        UtilitySpace spaceB=nt.getAgentBUtilitySpace();

        
    	no=new NegotiationOutcome(sessionNumber, 
			   agentA.getName(),  agentB.getName(),
            agentA.getClass().getCanonicalName(), agentB.getClass().getCanonicalName(),
            utilA,utilB,
            message,
            fAgentABids,fAgentBBids,
            spaceA.getUtility(spaceA.getMaxUtilityBid()),
            spaceB.getUtility(spaceB.getMaxUtilityBid()),
            startingWithA, 
            nt.getAgentAUtilitySpaceFileName(),
            nt.getAgentBUtilitySpaceFileName(),
            additionalLog
            );
    	
    	if (actionEventListener!=null) {
        	actionEventListener.handleEvent(new ActionEvent(currentAgent,action,sessionNumber,
        			System.currentTimeMillis()-startTimeMillies,utilA,utilB,message));
    		
    	}
    }
    */
}
