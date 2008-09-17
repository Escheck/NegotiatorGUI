package negotiator.tournament;

import java.util.ArrayList;
import java.util.Date;

import negotiator.Agent;
import negotiator.Bid;
import negotiator.Main;
import negotiator.NegotiationEventListener;
import negotiator.NegotiationOutcome;
import negotiator.actions.*;
import negotiator.analysis.BidPoint;
import negotiator.exceptions.Warning;
import negotiator.utility.UtilitySpace;
import negotiator.xml.SimpleElement;




/**
 * SessionRunner is the implementation of the Protocol - currently alternating offers.
 * It is a class that runs a session and stores the results.
 * TODO separate the protocol entirely
 * After a run is done, the NegotiationSession2 is notified.
 */
public class SessionRunner implements Runnable {
    NegotiationSession2 session;
	
    protected Agent         agentA;
    protected Agent         agentB;
    private Bid lastBid=null;				// the last bid that has been done

    /**
     * stopNegotiation indicates that the session has now ended.
     * it is checked after every call to the agent,
     * and if it happens to be true, session is immediately returned without any updates to the results list.
     * This is because killing the thread in many cases will return Agent.getAction() but with
     * a stale action. By setting stopNegotiation to true before killing, the agent will still immediately return.
     */
    public boolean stopNegotiation=false;
   // private NegotiationTemplate nt;
    public NegotiationOutcome no;
    boolean agentAtookAction = false;
    boolean agentBtookAction = false;
    boolean agentAStarts=false;
    public SimpleElement additionalLog = new SimpleElement("additional_log");
    Date startTime; 
    long startTimeMillies; //idem.
    Integer totTime; // total time, seconds, of this negotiation session.
	public Agent currentAgent=null; // agent currently bidding.

    public ArrayList<BidPoint> fAgentABids;
    public ArrayList<BidPoint> fAgentBBids;
    NegotiationEventListener the_event_listener;


     /** load the runtime objects to start negotiation */
    public SessionRunner(NegotiationSession2 s) throws Exception {
    	session=s;
		java.lang.ClassLoader loaderA = ClassLoader.getSystemClassLoader()/*new java.net.URLClassLoader(new URL[]{agentAclass})*/;
		agentA = (Agent)(loaderA.loadClass(session.agentArep.getClassPath()).newInstance());
		    agentA.setName(session.getAgentAname());
		
	    java.lang.ClassLoader loaderB =ClassLoader.getSystemClassLoader();
	    agentB = (Agent)(loaderB.loadClass(session.agentBrep.getClassPath()).newInstance());
	    agentB.setName(session.getAgentBname());

        totTime=session.NON_GUI_NEGO_TIME;
        if (agentA.isUIAgent() || agentB.isUIAgent()) totTime=session.GUI_NEGO_TIME;
//        nt = new NegotiationTemplate(session.profileArep.getDomain().getURL().getFile(),
//        		session.profileArep.getURL().getFile(),session.profileBrep.getURL().getFile(),totTime); 
 
    	
        
        fAgentABids = new ArrayList<BidPoint>();
        fAgentBBids = new ArrayList<BidPoint>();
    }
    
    /**
     * a parent thread will call this via the Thread.run() function.
     * Then it will start a timer to handle the time-out of the negotiation.
     * At the end of this run, we will notify the parent so that he does not keep waiting for the time-out.
     */
    public void run() {
		startTime=new Date(); startTimeMillies=System.currentTimeMillis();
        try {
            double agentAUtility,agentBUtility;
            UtilitySpace spaceA=session.getAgentAUtilitySpace();
            UtilitySpace spaceB=session.getAgentBUtilitySpace();

            // note, we clone the utility spaces for security reasons, so that the agent
        	 // can not damage them.
            agentA.init(session.sessionNumber, session.sessionTotalNumber,startTime,session.getTotalTime(),
            		new UtilitySpace(session.getAgentAUtilitySpace()));
            agentB.init(session.sessionNumber, session.sessionTotalNumber,startTime,session.getTotalTime(),
            		new UtilitySpace(session.getAgentBUtilitySpace()));
            
            stopNegotiation = false;
            Action action = null;
            
            if (session.startingAgent.equals(session.agentArep)) currentAgent=agentA;
           	else currentAgent=agentB;
            
        	System.out.println("starting with agent "+currentAgent);
            //Main.log("Agent " + currentAgent.getName() + " begins");
        	session.fireLogMessage("Nego","Agent " + currentAgent.getName() + " begins");
            while(!stopNegotiation) {
                try {
                   //inform agent about last action of his opponent
                   currentAgent.ReceiveMessage(action);
                   if (stopNegotiation) return;
                   //get next action of the agent that has its turn now
                   action = currentAgent.chooseAction();
                   if (stopNegotiation) return;
                   if(action instanceof EndNegotiation) 
                   {
                       stopNegotiation=true;
                       double utilA=spaceA.getUtility(spaceA.getMaxUtilityBid()); // normalized utility
                       double utilB=spaceB.getUtility(spaceB.getMaxUtilityBid());
                       newOutcome(currentAgent,0.,0., action, "Agent "+currentAgent.getName()+" ended the negotiation without agreement");
                       checkAgentActivity(currentAgent) ;
                   }
                   else if (action instanceof Offer) {
                       //Main.log("Agent " + currentAgent.getName() + " sent the following offer:");
                       session.fireLogMessage("Nego","Agent " + currentAgent.getName() + " sent the following offer:");
                       lastBid  = ((Offer)action).getBid();                       
                       //Main.log(action.toString());
                       session.fireLogMessage("Nego",action.toString());
                       double utilA=agentA.utilitySpace.getUtility(lastBid);
                       double utilB=agentB.utilitySpace.getUtility(lastBid);
                       //Main.log("Utility of " + agentA.getName() +": " + utilA);
                       session.fireLogMessage("Nego","Utility of " + agentA.getName() +": " + utilA);
                       //Main.log("Utility of " + agentB.getName() +": " + utilB);
                       session.fireLogMessage("Nego","Utility of " + agentB.getName() +": " + utilB);
                       //save last results 
                       BidPoint p=null;
               		   Bid b=((Offer)action).getBid();
               		   p=new BidPoint(b,
               				   session.getAgentAUtilitySpace().getUtility(b),
               				   session.getAgentBUtilitySpace().getUtility(b));
                       if(currentAgent.equals(agentA))                    {
                    	   fAgentABids.add(p);
                       } else{
                    	   fAgentBBids.add(p);
                       }
	                   session.fireNegotiationActionEvent(currentAgent,action,session.sessionNumber,
	                   		System.currentTimeMillis()-startTimeMillies,utilA,utilB,"bid by "+currentAgent.getName());
	                	
                       checkAgentActivity(currentAgent) ;
                   }                   
                   else if (action instanceof Accept) {
                       stopNegotiation = true;
                       Accept accept = (Accept)action;
                       if(lastBid==null)
                    	   throw new Exception("Accept was done by "+
                    			   currentAgent.getName()+" but no bid was done yet.");
                        Main.log("Agents accepted the following bid:");
                        Main.log(((Accept)action).toString());
                        agentAUtility = session.getAgentAUtilitySpace().getUtility(lastBid);
                        agentBUtility = session.getAgentBUtilitySpace().getUtility(lastBid);
                        newOutcome(currentAgent, agentAUtility,agentBUtility,action, null);
                        checkAgentActivity(currentAgent) ;
                        otherAgent(currentAgent).ReceiveMessage(action);                      
                   } else {  // action instanceof unknown action, e.g. null.
                	   throw new Exception("unknown action by agent "+currentAgent.getName());
                   }
                       
                   //save last results and swap to other agent
                   BidPoint p=null;
                   if (action instanceof Offer)
                   {
            		   Bid b=((Offer)action).getBid();
            		   p=new BidPoint(b,
            				   session.getAgentAUtilitySpace().getUtility(b),
            				   session.getAgentBUtilitySpace().getUtility(b));
                   }
                   if(currentAgent.equals(agentA))                    {
                	   if(action instanceof Offer) fAgentABids.add(p);
                   } else{
                	   if(action instanceof Offer) fAgentBBids.add(p);
                   }

                } catch(Exception e) {
                	new Warning("Caught exception:",e,true,2);
                   stopNegotiation=true;
                   new Warning("Protocol error by Agent"+currentAgent.getName(),e,true,3);
             	   Main.log("Protocol error by Agent " + currentAgent.getName() +":"+e.getMessage());
                   if (lastBid==null) agentAUtility=agentBUtility=1.;
                   else {
                	   agentAUtility=agentBUtility=0.;
                	   // handle both getUtility calls apart, if one crashes
                	   // the other should not be affected.
                	   try {
                		   agentAUtility = session.getAgentAUtilitySpace().getUtility(lastBid);
                	   }  catch (Exception e1) {}
                	   try {
                    	   agentBUtility = session.getAgentBUtilitySpace().getUtility(lastBid);
                	   }  catch (Exception e1) {}
                   }
                   if (currentAgent==agentA) agentAUtility=0.; else agentBUtility=0.;
                   try {
                	   newOutcome(currentAgent, agentAUtility,agentBUtility,action, "Agent " + currentAgent.getName() +":"+e.getMessage());
                   }
                   catch (Exception err) { err.printStackTrace(); new Warning("exception raised during exception handling: "+err); }
                   // don't compute the max utility, we're in exception which is already bad enough.
                }
                // swap to other agent
                if(currentAgent.equals(agentA))     currentAgent = agentB; 
                else   currentAgent = agentA;
            }
            
            // nego finished by Accept or illegal action.
            // notify parent that we're ready.
            synchronized (session) {  session.notify();  }
           
            /*
             * Wouter: WE CAN NOT DO MORE PROCESSING HERE!!!!!
             * Maybe even catching the ThreadDeath error is wrong. 
             * If we do more processing, we risk getting a ThreadDeath exception
             * causing Eclipse to pop up a dialog bringing us into the debugger.
             */            
            
        } catch (Error e) {
            if(e instanceof ThreadDeath) {
            	System.out.println("Nego was timed out");
                // Main.logger.add("Negotiation was timed out. Both parties get util=0");
            	// if this happens, the caller will adjust utilities.
           }     
             
        }

    }

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
    
    /** This is the running method of the negotiation thread.
     * It contains the work flow of the negotiation. 
     */
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
        UtilitySpace spaceA=session.getAgentAUtilitySpace();
        UtilitySpace spaceB=session.getAgentBUtilitySpace();

        
    	no=new NegotiationOutcome(session.sessionNumber, 
			   agentA.getName(),  agentB.getName(),
            agentA.getClass().getCanonicalName(), agentB.getClass().getCanonicalName(),
            utilA,utilB,
            message,
            fAgentABids,fAgentBBids,
            spaceA.getUtility(spaceA.getMaxUtilityBid()),
            spaceB.getUtility(spaceB.getMaxUtilityBid()),
            session.startingWithA, 
            session.getAgentAUtilitySpaceFileName(),
            session.getAgentBUtilitySpaceFileName(),
            additionalLog
            );
    	
    	session.fireNegotiationActionEvent(currentAgent,action,session.sessionNumber,
        	System.currentTimeMillis()-startTimeMillies,utilA,utilB,message);
    		
    	
    }
    
    /**
     * This is called whenever the protocol is timed-out. 
     * What happens in case of a time-out is 
     * (1) the sessionrunner is killed with a Thread.interrupt() call  from the NegotiationSession2.
     * (2) judgeTimeout() is called.
     * @author W.Pasman
     */
    public void JudgeTimeout() {
		try {
    		newOutcome(currentAgent, 0, 0, new IllegalAction(currentAgent,"negotiation was timed out"),"negotiation was timed out");
    		} catch (Exception err) { new Warning("error during creation of new outcome:",err,true,2); }
    		// don't bother about max utility, both have zero anyway.

    }
}
