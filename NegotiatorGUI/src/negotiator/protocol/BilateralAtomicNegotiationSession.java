package negotiator.protocol;

import java.util.ArrayList;
import java.util.HashMap;

import negotiator.Agent;
import negotiator.Bid;
import negotiator.Global;
import negotiator.NegotiationEventListener;
import negotiator.actions.Action;
import negotiator.analysis.BidPoint;
import negotiator.analysis.BidSpace;
import negotiator.events.ActionEvent;
import negotiator.events.LogMessageEvent;
import negotiator.tournament.VariablesAndValues.AgentParamValue;
import negotiator.tournament.VariablesAndValues.AgentParameterVariable;
import negotiator.utility.UtilitySpace;

public abstract class BilateralAtomicNegotiationSession implements Runnable {
    protected 	Agent   		agentA;
    protected 	Agent   		agentB;
    protected 	UtilitySpace 	spaceA;
    protected 	UtilitySpace 	spaceB;
    
    protected 	Bid 			lastBid = null;				// the last bid that has been done
    protected	Protocol 		protocol;
    protected 	int				sessionNumber;
    public ArrayList<BidPoint> 	fAgentABids;
    public ArrayList<BidPoint> 	fAgentBBids;    
    protected 	BidSpace		bidSpace;
	protected HashMap<AgentParameterVariable,AgentParamValue> agentAparams;
	protected HashMap<AgentParameterVariable,AgentParamValue> agentBparams;

    
    ArrayList<NegotiationEventListener> actionEventListener = new ArrayList<NegotiationEventListener>();
    
    public BilateralAtomicNegotiationSession(Protocol protocol, 
    		Agent agentA, 
    		Agent agentB, 
    		UtilitySpace spaceA, 
    		UtilitySpace spaceB,
    		HashMap<AgentParameterVariable, AgentParamValue> agentAparams, 
    		HashMap<AgentParameterVariable, AgentParamValue> agentBparams) throws Exception {
    	this.protocol = protocol;
    	this.agentA = agentA;
    	this.agentB = agentB;
    	this.spaceA = spaceA;
    	this.spaceB = spaceB;
        this.agentAparams = new HashMap<AgentParameterVariable, AgentParamValue>(agentAparams);
        this.agentBparams = new HashMap<AgentParameterVariable, AgentParamValue>(agentBparams);

        if(Global.isExperimentalSetup()) {
        	agentA.fNegotiation = this;
        	agentB.fNegotiation = this;
        }
        fAgentABids = new ArrayList<BidPoint>();
        fAgentBBids = new ArrayList<BidPoint>();
    }

    public void addNegotiationEventListener(NegotiationEventListener listener) {
    	if(!actionEventListener.contains(listener))
    		actionEventListener.add(listener);
    }
    public void removeNegotiationEventListener(NegotiationEventListener listener) {
    	if(!actionEventListener.contains(listener))
    		actionEventListener.remove(listener);
    }
	protected synchronized void fireNegotiationActionEvent(Agent actorP,Action actP,int roundP,long elapsed,
			double utilA,double utilB,String remarks) {
		for(NegotiationEventListener listener : actionEventListener) {
			listener.handleActionEvent(new ActionEvent(this,actorP, actP, roundP, elapsed, utilA, utilB, remarks ));
		}
	}
	protected synchronized void fireLogMessage(String source, String log) { 
    	for(NegotiationEventListener listener : actionEventListener) { 
        	listener.handleLogMessageEvent(new LogMessageEvent(this, source, log));
    	}
	}
    public void cleanupAgents() {
    	this.agentA = null;
    	this.agentB = null;
    }
    public BidSpace getBidSpace() { 
    	if(bidSpace==null) {
    		try {    	
    			bidSpace=new BidSpace(spaceA,spaceB);
    		} catch (Exception e) {
    			e.printStackTrace();
			}
    	}
    	return bidSpace;     	
    }

}
