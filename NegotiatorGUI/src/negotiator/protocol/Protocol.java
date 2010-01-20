package negotiator.protocol;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;

import negotiator.Agent;
import negotiator.Domain;
import negotiator.Global;
import negotiator.NegotiationEventListener;
import negotiator.NegotiationOutcome;
import negotiator.actions.Action;
import negotiator.events.*;
import negotiator.exceptions.Warning;
import negotiator.repository.AgentRepItem;
import negotiator.repository.ProfileRepItem;
import negotiator.repository.Repository;
import negotiator.tournament.Tournament;
import negotiator.tournament.TournamentRunner;
import negotiator.tournament.VariablesAndValues.AgentParamValue;
import negotiator.tournament.VariablesAndValues.AgentParameterVariable;
import negotiator.utility.UtilitySpace;
import negotiator.xml.*;

public abstract class Protocol implements Runnable {
    protected Thread negoThread = null;
    protected TournamentRunner tournamentRunner;
    /**
     * stopNegotiation indicates that the session has now ended.
     * it is checked after every call to the agent,
     * and if it happens to be true, session is immediately returned without any updates to the results list.
     * This is because killing the thread in many cases will return Agent.getAction() but with
     * a stale action. By setting stopNegotiation to true before killing, the agent will still immediately return.
     */
    public boolean stopNegotiation=false;
    
	private AgentRepItem[] agentRepItems;	
    private ProfileRepItem[] profileRepItems;    
    private String[] agentNames;
    private HashMap<AgentParameterVariable,AgentParamValue>[]  agentParams;
    
    /** -- **/
    protected Domain domain;
    private UtilitySpace[] agentUtilitySpaces;
    
    ArrayList<NegotiationEventListener> actionEventListener = new ArrayList<NegotiationEventListener>();    

    private SimpleElement fRoot;
//	private String fFileName;    

	public abstract String getName();
	
	public abstract NegotiationOutcome getNegotiationOutcome();
	
	//public Agent getAgent(int index);
	
	public static ArrayList<Protocol> getTournamentSessions(Tournament tournament) throws Exception {
		throw new Exception("This protocol cannot be used in a tournament");
	}

    public final void startSession() {
    	Thread protocolThread = new Thread(this);
    	protocolThread.start();
    }
    
    
    public Protocol(AgentRepItem[] agentRepItems, ProfileRepItem[] profileRepItems, HashMap<AgentParameterVariable,AgentParamValue>[] agentParams) throws Exception{
    	this.agentRepItems = agentRepItems.clone();
    	this.profileRepItems = profileRepItems.clone();
    	if (agentParams!=null) 
    		this.agentParams = agentParams.clone();
    	else this.agentParams = new HashMap[agentRepItems.length];
    	loadAgentsUtilitySpaces();
    }
	protected void loadAgentsUtilitySpaces() throws Exception
	{
		if(domain==null)
			//domain = new Domain(profileRepItems[0].getDomain().getURL().getFile());
			domain = Repository.get_domain_repos().getDomain(profileRepItems[0].getDomain());
		//TODO: read the agent names
		agentNames = new String[profileRepItems.length];
		agentNames[0] = "Agent A";
		agentNames[1] = "Agent B";
		//load the utility space		
		agentUtilitySpaces = new UtilitySpace[profileRepItems.length]; 
		for(int i=0;i<profileRepItems.length;i++) {
			ProfileRepItem profile = profileRepItems[i];
			agentUtilitySpaces[i] =  Repository.get_domain_repos().getUtilitySpace(domain, profile); //new UtilitySpace(domain, profile.getURL().getFile());
			//System.out.println("utility space statistics for "+"Agent "+agentAUtilitySpaceFileName);
			//fAgentAUtilitySpace.showStatistics();
		}
		return;

	}
	
	/**
	 * @param fileName Wouter: I think this is the domain.xml file.
	 */
	private void loadFromFile(String fileName)  throws Exception
	{
		SimpleDOMParser parser = new SimpleDOMParser();
		BufferedReader file = new BufferedReader(new FileReader(new File(fileName)));                  
		fRoot = parser.parse(file);
		/*            if (root.getAttribute("negotiation_type").equals("FDP"))this.negotiationType = FAIR_DEVISION_PROBLEM;
        else thisnegotiationType = CONVENTIONAL_NEGOTIATION;*/
		SimpleElement xml_utility_space = (SimpleElement)(fRoot.getChildByTagName("utility_space")[0]);
		domain = new Domain(xml_utility_space);
		loadAgentsUtilitySpaces();
		if (Global.analysisEnabled && !Global.batchMode)
		{
			if(fRoot.getChildByTagName("analysis").length>0) {
				//fAnalysis = new Analysis(this, (SimpleElement)(fRoot.getChildByTagName("analysis")[0]));
			} else {
				//propose to build an analysis
/*				Object[] options = {"Yes","No"};                  
				int n = JOptionPane.showOptionDialog(null,
						"You have no analysis available for this template. Do you want build it?",
						"No Analysis",
						JOptionPane.YES_NO_OPTION,
						JOptionPane.WARNING_MESSAGE,
						null,
						options,
						options);
				if(n==0) {*/
					//bidSpace=new BidSpace(fAgentAUtilitySpace,fAgentBUtilitySpace);
					//fAnalysis = Analysis.getInstance(this);
					//  save the analysis to the cache
					//fAnalysis.saveToCache();
				//}
				
			}//if
		}
		//if(fAnalysis!=null) showAnalysis();	
		//if (bidSpace!=null) showAnalysis();
	}

    void check() throws Exception {
    	//if (!(getProfileArep().getDomain().equals(getProfileBrep().getDomain())))
    		//throw new IllegalArgumentException("profiles "+getProfileArep()+" and "+getProfileBrep()+" have a different domain.");
    }
    
    public void addNegotiationEventListener(NegotiationEventListener listener) {
    	if(!actionEventListener.contains(listener))
    		actionEventListener.add(listener);
    }
    public ArrayList<NegotiationEventListener> getNegotiationEventListeners() {
    	return (ArrayList<NegotiationEventListener>) (actionEventListener.clone());
    }

    public void removeNegotiationEventListener(NegotiationEventListener listener) {
    	if(!actionEventListener.contains(listener))
    		actionEventListener.remove(listener);
    }
	public synchronized void fireNegotiationActionEvent(Agent actorP,Action actP,int roundP,long elapsed,
			double utilA,double utilB, double utilADiscount, double utilBDiscount,String remarks) {
		for(NegotiationEventListener listener : actionEventListener) {
			listener.handleActionEvent(new ActionEvent(this,actorP, actP, roundP, elapsed, utilA, utilB, utilADiscount, utilBDiscount,remarks ));
		}
	}
	public synchronized void fireBilateralAtomicNegotiationSessionEvent(BilateralAtomicNegotiationSession session,ProfileRepItem profileA,
			ProfileRepItem profileB,
			AgentRepItem agentA,
			AgentRepItem agentB) {
		for(NegotiationEventListener listener : actionEventListener) {
			listener.handleBlateralAtomicNegotiationSessionEvent(new BilateralAtomicNegotiationSessionEvent (this, session,profileA,profileB,agentA,agentB));
		}
	}
	
    public synchronized void fireLogMessage(String source, String log) { 
    	for(NegotiationEventListener listener : actionEventListener) { 
        	listener.handleLogMessageEvent(new LogMessageEvent(this, source, log));
    	}
	}
    public void setTournamentRunner(TournamentRunner runner) {
    	tournamentRunner = runner; 
    }
    public Domain getDomain() {
    	return domain;
    }
	public AgentRepItem getAgentRepItem(int index) {
		return agentRepItems[index];
	}
    public ProfileRepItem getProfileRepItems(int index) {
    	return profileRepItems[index];
    }
    public String getAgentName(int index) {
    	return agentNames[index];
    }
    public HashMap<AgentParameterVariable,AgentParamValue> getAgentParams(int index) {
    	return agentParams[index];
    }
    
    public  UtilitySpace getAgentUtilitySpaces(int index) {
    	return agentUtilitySpaces[index];
    }
    public int getNumberOfAgents() {
    	return agentRepItems.length;
    }

    public int getSessionNumber() {
    	return 1;
    }
    public void stopNegotiation() {
    	if (negoThread!=null&&negoThread.isAlive()) {
    		try {
    			stopNegotiation=true; // see comments in sessionrunner..
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
    public abstract void cleanUP();

}
