package negotiator.protocol;

import java.util.ArrayList;
import java.util.HashMap;

import negotiator.Agent;
import negotiator.Bid;
import negotiator.Domain;
import negotiator.Global;
import negotiator.NegotiationEventListener;
import negotiator.actions.Action;
import negotiator.analysis.BidPoint;
import negotiator.analysis.BidPointTime;
import negotiator.analysis.BidSpace;
import negotiator.events.ActionEvent;
import negotiator.events.LogMessageEvent;
import negotiator.qualitymeasures.CSVlogger;
import negotiator.qualitymeasures.OpponentModelMeasuresResults;
import negotiator.tournament.TournamentConfiguration;
import negotiator.tournament.VariablesAndValues.AgentParamValue;
import negotiator.tournament.VariablesAndValues.AgentParameterVariable;
import negotiator.utility.AdditiveUtilitySpace;
import negotiator.xml.SimpleElement;

public abstract class BilateralAtomicNegotiationSession implements Runnable {
	
    protected 	Agent   		agentA;
    protected 	Agent   		agentB;
    protected 	AdditiveUtilitySpace 	spaceA;
    protected 	AdditiveUtilitySpace 	spaceB;
    protected	String 			agentAname;
    protected	String 			agentBname;
    protected 	Bid 			lastBid    = null;				// the last bid that has been done
    protected 	Action 			lastAction = null;				// the last action that has been done (also included Accept, etc.)
    protected Protocol protocol;
    protected 	int				finalRound; // 0 during whole negotiation accept at agreement, in which case it is equal to rounds
    protected 	ArrayList<BidPointTime> 	fAgentABids;
    protected 	ArrayList<BidPointTime> 	fAgentBBids;
    protected 	BidSpace		bidSpace;
	protected 	HashMap<AgentParameterVariable,AgentParamValue> agentAparams;
	protected 	HashMap<AgentParameterVariable,AgentParamValue> agentBparams;
	protected 	CSVlogger matchDataLogger;
	protected 	OpponentModelMeasuresResults omMeasuresResults = new OpponentModelMeasuresResults();

    ArrayList<NegotiationEventListener> actionEventListener = new ArrayList<NegotiationEventListener>();
	private String log;
	/** tournamentNumber is the tournament.TournamentNumber, or -1 if this session is not part of a tournament*/
    int tournamentNumber=-1; 

    public SimpleElement additionalLog = new SimpleElement("additional_log");

    
    public BilateralAtomicNegotiationSession(Protocol protocol,
    		Agent agentA, 
    		Agent agentB, 
    		String agentAname,
    		String agentBname,
    		AdditiveUtilitySpace spaceA, 
    		AdditiveUtilitySpace spaceB,
    		HashMap<AgentParameterVariable, AgentParamValue> agentAparams, 
    		HashMap<AgentParameterVariable, AgentParamValue> agentBparams) throws Exception {
    	this.protocol = protocol;
        this.agentA = agentA;
    	this.agentB = agentB;
    	this.agentAname = agentAname;
    	this.agentBname = agentBname;
    	this.spaceA = spaceA;
    	this.spaceB = spaceB;
    	if(agentAparams!=null)
    		this.agentAparams = new HashMap<AgentParameterVariable, AgentParamValue>(agentAparams);
    	else this.agentAparams = new HashMap<AgentParameterVariable, AgentParamValue>();
        if(agentBparams!=null)
        	this.agentBparams = new HashMap<AgentParameterVariable, AgentParamValue>(agentBparams);
        else this.agentBparams = new HashMap<AgentParameterVariable, AgentParamValue>();

        if(TournamentConfiguration.getBooleanOption("accessPartnerPreferences", false)) {
        	agentA.fNegotiation = this;
        	agentB.fNegotiation = this;
        }
        fAgentABids = new ArrayList<BidPointTime>();
        fAgentBBids = new ArrayList<BidPointTime>();

        Domain domain = spaceA.getDomain();
		String domainName = "";
		if (domain == null)
			System.err.println("Warning: domain null in " + spaceA.getFileName());
		else
			domainName = domain.getName();
		matchDataLogger = new CSVlogger(Global.getOQMOutcomesFileName(),
        								agentA.getName(), spaceA.getFileName(), 
        								agentB.getName(), spaceB.getFileName(), domainName);
        
        actionEventListener.addAll(protocol.getNegotiationEventListeners());
    }

    public void addNegotiationEventListener(NegotiationEventListener listener) {
    	if(!actionEventListener.contains(listener))
    		actionEventListener.add(listener);
    }
    public void removeNegotiationEventListener(NegotiationEventListener listener) {
    	if(!actionEventListener.contains(listener))
    		actionEventListener.remove(listener);
    }
    
	protected synchronized void fireNegotiationActionEvent(Agent actorP,Action actP,int roundP,long elapsed,double time,
			double utilA,double utilB,double utilADiscount,double utilBDiscount,String remarks, boolean finalActionEvent) {
		for(NegotiationEventListener listener : actionEventListener) {
			ActionEvent event = new ActionEvent(this,actorP, actP, roundP, elapsed, time, utilA, utilB, utilADiscount, utilBDiscount, remarks, finalActionEvent);
			listener.handleActionEvent(event);
		}
	}
	
	/**
	 * Used by {@link AlternatingOffersBilateralAtomicNegoSessionSeparateTimelines}. Does not use time.
	 */
	@Deprecated
	protected synchronized void fireNegotiationActionEvent(Agent actorP,Action actP,int roundP,long elapsed,
			double utilA,double utilB,double utilADiscount,double utilBDiscount,String remarks, boolean finalActionEvent) {
		for(NegotiationEventListener listener : actionEventListener) {
			listener.handleActionEvent(new ActionEvent(this,actorP, actP, roundP, elapsed, -1, utilA, utilB, utilADiscount, utilBDiscount, remarks, finalActionEvent));
		}
	}
	
	protected synchronized void fireLogMessage(String source, String log) { 
    	for(NegotiationEventListener listener : actionEventListener) { 
        	listener.handleLogMessageEvent(new LogMessageEvent(this, source, log));
    	}
	}
    
    public Bid getLastBid()
	{
		return lastBid;
	}
    
	public int getNrOfBids(){
		return fAgentABids.size() + fAgentBBids.size();
	}
	
	//alinas code
	public double[][] getNegotiationPathA(){
//		System.out.println("fAgentABids "+fAgentABids.size());
		double[][] lAgentAUtilities = new double[2][fAgentABids.size()];
		try
        {
			int i=0;
	    	for (BidPoint p:fAgentABids)
	    	{
	        	lAgentAUtilities [0][i] = p.getUtilityA();
	        	lAgentAUtilities [1][i] = p.getUtilityB();
	        	i++;
	    	}
        } catch (Exception e) {
			e.printStackTrace();
        	return null;
		}
    	
		return lAgentAUtilities; 
	}
	
	public ArrayList<BidPointTime> getAgentABids() {
		return fAgentABids;
	}
	
	public ArrayList<BidPointTime> getAgentBBids() {
		return fAgentBBids;
	}

	public double[][] getNegotiationPathB(){
		//System.out.println("fAgentBBids "+fAgentBBids.size());
		double[][] lAgentBUtilities = new double [2][fAgentBBids.size()];  
		try{
			int i=0;
	    	for (BidPoint p:fAgentBBids)
	    	{
	        	lAgentBUtilities [0][i] = p.getUtilityA();
	        	lAgentBUtilities [1][i] = p.getUtilityB();
	        	i++;
	    	}
	 	} catch (Exception e) {
		   	e.printStackTrace();
		   	return null;
		}
		return lAgentBUtilities;
	}
    public double getOpponentUtility(Agent pAgent, Bid pBid) throws Exception{
    	if(pAgent.equals(agentA)) 
    		return spaceB.getUtility(pBid);
    	else
    		return spaceA.getUtility(pBid);
    }
    public double getOpponentWeight(Agent pAgent, int pIssueID) throws Exception{
    	if(pAgent.equals(agentA)) 
    		return spaceB.getWeight(pIssueID);
    	else
    		return spaceA.getWeight(pIssueID);
    }
    
    public void addAdditionalLog(SimpleElement pElem) {
    	if(pElem!=null)
    		additionalLog.addChildElement(pElem);
    	
    }

	public void setLog(String str){
		log = str;
	}
	public String getLog(){
		return log;
	}
	public String getAgentAname() {
		return agentAname;
	}
	public String getAgentBname() {
		return agentBname;
	}

    public int getTournamentNumber() { 
    	return tournamentNumber; 
    }

    public int getTestNumber() { 
    	return 1;//TODO:protocol.getSessionTestNumber(); 
    }

	public abstract String getStartingAgent() ;

	public HashMap<AgentParameterVariable, AgentParamValue> getAgentAparams() {
		return agentAparams;
	}

	public HashMap<AgentParameterVariable, AgentParamValue> getAgentBparams() {
		return agentBparams;
	}
	public Agent getAgentA() {
		return agentA;
	}
	public Agent getAgentB() {
		return agentB;
	}
	public AdditiveUtilitySpace getAgentAUtilitySpace() {
		return spaceA;
	}
	public AdditiveUtilitySpace getAgentBUtilitySpace() {
		return spaceB;
	}
	
	
}
