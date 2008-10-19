package negotiator.tournament;

import java.util.ArrayList;
import java.util.HashMap;

import agents.BayesianAgentForAuction;

import negotiator.Agent;
import negotiator.AgentParam;
import negotiator.NegotiationEventListener;
import negotiator.analysis.BidSpace;
import negotiator.exceptions.Warning;
import negotiator.tournament.VariablesAndValues.AgentParamValue;
import negotiator.tournament.VariablesAndValues.AgentParameterVariable;

public class TournamentRunnerTwoPhaseAutction extends TournamentRunner {

	public TournamentRunnerTwoPhaseAutction(Tournament t,
			NegotiationEventListener ael) throws Exception {
		super(t, ael);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		ArrayList<NegotiationSession2> sessions;
	   	try { 
	   		while(true) {
    		sessions=tournament.getSessions();
    		if(sessions==null) break;
			for (NegotiationSession2 s: sessions) {
				//if (the_event_listener!=null) s.actionEventListener=the_event_listener;
				for (NegotiationEventListener list: negotiationEventListeners) s.addNegotiationEventListener(list);
				fireNegotiationSessionEvent(s);
				s.run(); // note, we can do this because TournamentRunner has no relation with AWT or Swing.
				
			}
			//determine winner
			double lMaxUtil= Double.NEGATIVE_INFINITY;
			double lSecondPrice = Double.NEGATIVE_INFINITY;
			NegotiationSession2 winnerSession = null;
//			NegotiationSession2 secondBestSession = null;
			for (NegotiationSession2 s: sessions) {
				if(s.getSessionRunner().getNegotiationOutcome().agentButility>lMaxUtil) {
					lSecondPrice = lMaxUtil;
					lMaxUtil = s.getSessionRunner().getNegotiationOutcome().agentButility;
					//secondBestSession = winnerSession;
					winnerSession = s;
				} else if(s.getSessionRunner().getNegotiationOutcome().agentButility>lSecondPrice) 
					lSecondPrice = s.getSessionRunner().getNegotiationOutcome().agentButility;
				
			}
			HashMap<AgentParameterVariable,AgentParamValue>  paramsA=new HashMap<AgentParameterVariable,AgentParamValue> ();
			HashMap<AgentParameterVariable,AgentParamValue>  paramsB=new HashMap<AgentParameterVariable,AgentParamValue> ();
			paramsA.put(new AgentParameterVariable(new AgentParam(BayesianAgentForAuction.class.getName(),"role",-1.,1.)), new AgentParamValue(-0.9));
			paramsA.put(new AgentParameterVariable(new AgentParam(BayesianAgentForAuction.class.getName(),"reservation",0.,1.)), new AgentParamValue(0.6));
			paramsA.put(new AgentParameterVariable(new AgentParam(BayesianAgentForAuction.class.getName(),"phase",0.,1.)), new AgentParamValue(0.9));
			paramsB.put(new AgentParameterVariable(new AgentParam(BayesianAgentForAuction.class.getName(),"role",-1.,1.)), new AgentParamValue(0.9));
			paramsB.put(new AgentParameterVariable(new AgentParam(BayesianAgentForAuction.class.getName(),"reservation",0.,1.)), new AgentParamValue(lSecondPrice));
			paramsB.put(new AgentParameterVariable(new AgentParam(BayesianAgentForAuction.class.getName(),"phase",0.,1.)), new AgentParamValue(0.9));
			
			NegotiationSession2 secondPhaseSession = new NegotiationSession2(winnerSession.agentArep,
					winnerSession.agentBrep,
					winnerSession.getProfileArep(),
					winnerSession.getProfileBrep(),
					winnerSession.getAgentAname(),
					winnerSession.getAgentBname(),
					paramsA,
					paramsB,
					100,
					1,
					true
					);
			BidSpace bidSpace = tournament.getBidSpace(secondPhaseSession.getAgentAUtilitySpace(), secondPhaseSession.getAgentBUtilitySpace());
			if(bidSpace!=null) {
				secondPhaseSession.setBidSpace(bidSpace);
			} else {
				bidSpace = new BidSpace(secondPhaseSession.getAgentAUtilitySpace(),secondPhaseSession.getAgentBUtilitySpace());
				tournament.addBidSpaceToCash(secondPhaseSession.getAgentAUtilitySpace(), secondPhaseSession.getAgentBUtilitySpace(), bidSpace);
				secondPhaseSession.setBidSpace(bidSpace);
			}
			
			for (NegotiationEventListener list: negotiationEventListeners) secondPhaseSession.addNegotiationEventListener(list);
			fireNegotiationSessionEvent(secondPhaseSession);
			secondPhaseSession.run(); // note, we can do this because TournamentRunner has no relation with AWT or Swing.
	   		}
    	} catch (Exception e) { e.printStackTrace(); new Warning("Fatail error cancelled tournament run:"+e); }
  
	}

}
