/*
 * NegotiationManager.java
 *
 * Created on 13 ������ 2006 �., 10:29
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package negotiator;

import java.net.URLClassLoader;
import java.net.URL;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import negotiator.gui.SessionFrame;
import negotiator.xml.SimpleElement;
/**
 *
 * @author Dmytro Tykhonov
 * 
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
    private int NEGOTIATION_TIMOUT = 120; //Default 120 (seconds) 
    private String agentAclassName;
    private String agentBclassName;
    SessionFrame sf;
    /** Creates a new instance of NegotiationManager */
    public NegotiationManager(/*URL agentAclass,*/ 
            String agentAclassName, 
            String agentAName,
            String agentAUtilitySpace, 
            /*URL agentBclass,*/
            String agentBclassName,  
            String agentBName,
            String agentBUtilitySpace,
            String negotiationTemplateFileName,
            int numberOfSession) {
        this.agentAclassName = agentAclassName;
        this.agentBclassName = agentBclassName;
        nt = new NegotiationTemplate(negotiationTemplateFileName,agentAUtilitySpace,agentBUtilitySpace );
        this.numberOfSessions = numberOfSession;
        Main.logger.add("Loading agents...");
        agentA = null;
        agentB = null;
        
        // TODO: load reservation value somewhere if present in utility template file.
        try {
            java.lang.ClassLoader loaderA = ClassLoader.getSystemClassLoader()/*new java.net.URLClassLoader(new URL[]{agentAclass})*/;
            agentA = (Agent)(loaderA.loadClass(agentAclassName/*"agentexample.MyAgent"*/).newInstance());
            agentA.setName(agentAName);
        } catch(ClassNotFoundException e1) {
            e1.printStackTrace();                        
            return;
        } catch(InstantiationException e2) {
            e2.printStackTrace();                        
            return;
        } catch(IllegalAccessException e2) {
            e2.printStackTrace();            
            return;
        }
        try {
            java.lang.ClassLoader loaderB =ClassLoader.getSystemClassLoader();
            agentB = (Agent)(loaderB.loadClass(agentBclassName/*"agentexample.MyAgent"*/).newInstance());
            agentB.setName(agentBName);
        } catch(ClassNotFoundException e1) {
            e1.printStackTrace();                        
        } catch(InstantiationException e2) {
            e2.printStackTrace();                        
        } catch(IllegalAccessException e2) {
            e2.printStackTrace();
            
        }
        
        if (agentA.isUIAgent() || agentB.isUIAgent()) NEGOTIATION_TIMOUT=5;//60*20;
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
        Negotiation nego = new Negotiation(agentA, agentB, nt, sessionNumber, sessionTotalNumber);
        if(Main.fDebug) {
        	nego.run();	
        } else {
			try { wait(10);  } catch (Exception e) {System.out.println("strange interrupts?!:");e.printStackTrace();}

        	negoThread = new Thread(nego);
        	negoThread.start();
			try { wait(10);  } catch (Exception e) {System.out.println("ignoring interrupts");e.printStackTrace();}
        	try {
        		synchronized (this) {
        				wait(NEGOTIATION_TIMOUT*1000);
        		}
        	} catch (InterruptedException ie) {
        		System.out.println("wait cancelled:"+ie.getMessage()); ie.printStackTrace();}
        	}
        
        	if (negoThread.isAlive()) {
        		try {
        			negoThread.stop(); // kill the stuff
        			 // Wouter: this will throw an Error into the nego thread
        			 // The nego thread will catch this and exit.
        		} catch (Exception e) {
        			System.out.println("problem stopping the nego:"+e.getMessage());
        			e.printStackTrace();
        		
        		}
        }
        NegotiationOutcome no = null;
        if(nego.no!=null) no = nego.no;
        else no = new NegotiationOutcome(sessionNumber,agentAclassName, agentBclassName, "0","0","nego result was null(aborted)");
        sf.addNegotiationOutcome(no);        // add new result to the outcome list. 
        try {
            BufferedWriter out = new BufferedWriter(new FileWriter("outcomes.csv",true));
            out.write(Main.getCurrentTime() + ";" + no.toString()+"\n");
            out.close();
        } catch (IOException e) {
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
    
    
}
