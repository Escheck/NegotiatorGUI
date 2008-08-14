/*
 * NegotiationManager.java
 *
 * Created on 13 ������ 2006 �., 10:29
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package negotiator;

import java.io.BufferedWriter;
import negotiator.exceptions.*;
import java.io.FileWriter;

import negotiator.actions.EndNegotiation;
import negotiator.gui.SessionFrame;
import negotiator.xml.SimpleElement;
import negotiator.tournament.NegotiationSession;
/**
 *
 * @author Dmytro Tykhonov
 * @modified Wouter 14aug08. 
 *  Wouter: the agent objects stay instantiated over the negotiation sessions.
 *  This is to enable them to learn from previuos sessions.
 *  This means that the Agent objects may have to do some cleanup work when GUI windows are still open.
 *  */

// TODO: Make negotiation environment more robust.

public class NegotiationManager implements Runnable {
    private Thread negoThread = null;
    private Agent agentA; 
    private Agent agentB;
    private int numberOfSessions;
    private NegotiationTemplate nt;
    
    // following contains default for nego between two machine agents.
    // the timeout is changed if one of the two agents isUIAgent().
    private int NON_GUI_NEGO_TIME = Integer.MAX_VALUE;//120; //Default 120 (seconds) 
    private int GUI_NEGO_TIME=60*30; 	// Nego time if a GUI is involved in the nego
    private String agentAclassName;
    private String agentBclassName;
    private boolean agentAStarts; // true if agent A should start the nego.
    SessionFrame sf;
    
    ActionEventListener actionEventListener; //null if no one subscribed.
    
    /** Creates a new instance of NegotiationManager
     * throws if exception occurs, particularly with creation of nego template.
     */
    public NegotiationManager(/*URL agentAclass,*/ 
            String agentAclassName, 
            String agentAName,
            String agentAUtilitySpace, 
            /*URL agentBclass,*/
            String agentBclassName,  
            String agentBName,
            String agentBUtilitySpace,
            String negotiationTemplateFileName,
            int numberOfSession,
            boolean agentAStartsP) throws Exception
    {
        this.agentAclassName = agentAclassName;
        this.agentBclassName = agentBclassName;
        agentAStarts=agentAStartsP;
        // load the utility spaces
        numberOfSessions = numberOfSession;
        Main.logger.add("Loading agents...");
        agentA = null;
        agentB = null;
        
        try {
        // TODO: load reservation value somewhere if present in utility template file.
            java.lang.ClassLoader loaderA = ClassLoader.getSystemClassLoader()/*new java.net.URLClassLoader(new URL[]{agentAclass})*/;
            agentA = (Agent)(loaderA.loadClass(agentAclassName/*"agentexample.MyAgent"*/).newInstance());
            agentA.setName(agentAName);
        } catch (Exception e) { throw new Exception("error "+e.getClass()+" while loading '"+agentAName+"': "+e.getMessage()); }
        
        try {
            java.lang.ClassLoader loaderB =ClassLoader.getSystemClassLoader();
            agentB = (Agent)(loaderB.loadClass(agentBclassName/*"agentexample.MyAgent"*/).newInstance());
            agentB.setName(agentBName);
        }         catch (Exception e) { throw new Exception("error "+e.getClass()+" while loading '"+agentAName+"': "+e.getMessage()); }
        
         // we can determine total nego time only after agents were loaded:
        Integer totTime=NON_GUI_NEGO_TIME;
        if (agentA.isUIAgent() || agentB.isUIAgent()) totTime=GUI_NEGO_TIME;
        nt = new NegotiationTemplate(negotiationTemplateFileName,agentAUtilitySpace,agentBUtilitySpace,totTime); 
        nt.getAgentAUtilitySpace().checkReadyForNegotiation(agentAName, nt.getDomain());
        nt.getAgentBUtilitySpace().checkReadyForNegotiation(agentBName, nt.getDomain());
        
		   /*
		    * test the BidSpace, temporary hack!!!
		    */

//		   BidSpace bs=new BidSpace(nt.getAgentAUtilitySpace(),nt.getAgentBUtilitySpace());
//		   ArrayList<BidPoint> frontier=bs.getParetoFrontier();
//		   System.out.println("NegoManager check pareto frontier:");
//		   for (BidPoint p:frontier) System.out.println(","+p);
    }
    
    public void run() {
        startNegotiation();
        if(Main.batchMode) {
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();;
            }
            System.exit(0);
        }
    }
    
    
    protected void runNegotiationSession(int sessionNumber, int sessionTotalNumber) 
    {
    	
        NegotiationSession nego = new NegotiationSession(agentA, agentB, nt, sessionNumber, sessionTotalNumber,agentAStarts,actionEventListener);
        if(Main.fDebug) {
        	nego.run();	
        } else {
        	negoThread = new Thread(nego);
            //System.out.println("nego start. "+System.currentTimeMillis()/1000);
            negoThread.start();
        	try {
        		synchronized (this) {
        			System.out.println("waiting NEGO_TIMEOUT="+nt.getTotalTime());
        			 // wait will unblock early if negotiation is finished in time.
    				wait(nt.getTotalTime());
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
        if (nt.getBidSpace()!=null)
        	nt.addNegotiationPaths(sessionNumber, nego.getAgentABids(), nego.getAgentBBids());
    	NegotiationOutcome no = null;
    	if(nego.no!=null) no = nego.no;
    	else
    		try {
    		nego.newOutcome(null, 0, 0, new EndNegotiation(null), "nego result was null(aborted)");
    		} catch (Exception err) { new Warning("error during creation of new outcome:"+err); }
    		// don't bother about max utility, both have zero anyway.
    	sf.addNegotiationOutcome(no);        // add new result to the outcome list. 
        try {
            BufferedWriter out = new BufferedWriter(new FileWriter("outcomes.xml",true));
            out.write(""+no.toXML());
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
    
    
    public void startNegotiation() {
        sf = new SessionFrame(agentA.getName(), agentB.getName());
        sf.setVisible(true);
        Main.logger.add("Starting negotiations...");
        for(int i=0;i<numberOfSessions;i++) {
            Main.logger.add("Starting session " + String.valueOf(i+1));
            runNegotiationSession(i+1, numberOfSessions);
        }
    }
    
    public void printDomainXML(String filename){
    	SimpleElement thisTemplate = new SimpleElement("negotiation_template");
    	thisTemplate.setAttribute("number_of_sessions", ""+numberOfSessions); //FIXME for now.
	
    	SimpleElement thisAgentA = new SimpleElement("agent");
    	thisAgentA.setAttribute("class", agentAclassName);
    	thisAgentA.setAttribute("name", agentA.getName());
    	thisAgentA.setAttribute("utility_space", nt.getAgentAUtilitySpaceFileName());
    	
    	SimpleElement thisAgentB = new SimpleElement("agent");
    	thisAgentB.setAttribute("class", agentBclassName);
    	thisAgentB.setAttribute("name", agentB.getName());
    	thisAgentB.setAttribute("utility_space", nt.getAgentBUtilitySpaceFileName());

    	thisTemplate.addChildElement(thisAgentA);
    	thisTemplate.addChildElement(thisAgentB);
    	thisTemplate.addChildElement(nt.domainToXML());
    	
    	try{
    	thisTemplate.saveToFile(filename);
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    }
    
    public void printAgentAUtilitySpace(String filename){
    	SimpleElement Autil = nt.getAgentAUtilitySpace().toXML();
    	Autil.saveToFile(filename);
    }
    
    public void printAgentBUtilitySpace(String filename){
    	SimpleElement Butil = nt.getAgentBUtilitySpace().toXML();
    	Butil.saveToFile(filename);
    }
    
    /** subscribe here to receive ActionEvents from negotiations. 
     * only one subscription is possible.
     * unsubscribe not available.
     * Subscribe before starting a negotiation!
     * 
     * @author W.Pasman */
    public void subscribe(ActionEventListener ael) throws Exception
    {
    	if (actionEventListener!=null)
    		throw new RuntimeException("subscribe can be done only once");
    	if (ael==null) throw new NullPointerException("null event listener!?");
    	actionEventListener=ael;
    }
    
}
