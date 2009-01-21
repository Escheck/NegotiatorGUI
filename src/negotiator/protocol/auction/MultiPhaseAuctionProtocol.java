package negotiator.protocol.auction;

import java.util.HashMap;

import agents.BayesianAgentForAuction;

import negotiator.*;
import negotiator.exceptions.Warning;
import negotiator.repository.AgentRepItem;
import negotiator.repository.ProfileRepItem;
import negotiator.tournament.VariablesAndValues.AgentParamValue;
import negotiator.tournament.VariablesAndValues.AgentParameterVariable;

public class MultiPhaseAuctionProtocol extends AuctionProtocol {

	public MultiPhaseAuctionProtocol(AgentRepItem[] agentRepItems,
			ProfileRepItem[] profileRepItems,
			HashMap<AgentParameterVariable, AgentParamValue>[] agentParams)
			throws Exception {
		super(agentRepItems, profileRepItems, agentParams);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void cleanUP() {
		// TODO Auto-generated method stub

	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public NegotiationOutcome getNegotiationOutcome() {
		// TODO Auto-generated method stub
		return null;
	}

	public void run() {
		try { 
			int numberOfSellers = getNumberOfAgents()-1;
			//run the sessions
			AuctionBilateralAtomicNegoSession[] sessions = new AuctionBilateralAtomicNegoSession[numberOfSellers];
			for (int i=0;i<numberOfSellers;i++) {
				sessions[i] = 
					runNegotiationSession(
						getAgentRepItem(0),
						getAgentRepItem(i+1),
						"Buyer", "Seller", 
						getProfileRepItems(0),
						getProfileRepItems(i+1),
						getAgentUtilitySpaces(0),
						getAgentUtilitySpaces(i+1),
						getAgentParams(0),
						getAgentParams(i+1));
			}
			//determine winner
			double lMaxUtil= Double.NEGATIVE_INFINITY;
			double lSecondPrice = Double.NEGATIVE_INFINITY;
			AuctionBilateralAtomicNegoSession winnerSession = null;
//				NegotiationSession2 secondBestSession = null;
			int winnerSessionIndex=0, i=0;
			for (AuctionBilateralAtomicNegoSession s: sessions) {
				if(s.getNegotiationOutcome().agentAutility>lMaxUtil) {
					lSecondPrice = lMaxUtil;
					lMaxUtil = s.getNegotiationOutcome().agentAutility;
					//secondBestSession = winnerSession;
					winnerSession = s;
					winnerSessionIndex = i;
				} else if(s.getNegotiationOutcome().agentAutility>lSecondPrice) 
					lSecondPrice = s.getNegotiationOutcome().agentAutility;
				i++;
			}
			
			HashMap<AgentParameterVariable,AgentParamValue> paramsA = new HashMap<AgentParameterVariable,AgentParamValue> ();
			HashMap<AgentParameterVariable,AgentParamValue> paramsB = new HashMap<AgentParameterVariable,AgentParamValue> ();
			paramsA.put(new AgentParameterVariable(new AgentParam(BayesianAgentForAuction.class.getName(),"role",-1.,1.)), new AgentParamValue(0.9));
			paramsA.put(new AgentParameterVariable(new AgentParam(BayesianAgentForAuction.class.getName(),"reservation",0.,1.)), new AgentParamValue(lSecondPrice));
			//paramsA.put(new AgentParameterVariable(new AgentParam(BayesianAgentForAuction.class.getName(),"reservation",0.,1.)), new AgentParamValue(0.6));
			paramsA.put(new AgentParameterVariable(new AgentParam(BayesianAgentForAuction.class.getName(),"phase",0.,1.)), new AgentParamValue(0.9));
			paramsB.put(new AgentParameterVariable(new AgentParam(BayesianAgentForAuction.class.getName(),"role",-1.,1.)), new AgentParamValue(-0.9));
			paramsB.put(new AgentParameterVariable(new AgentParam(BayesianAgentForAuction.class.getName(),"phase",0.,1.)), new AgentParamValue(0.9));
			
			AuctionBilateralAtomicNegoSession secondPhaseSession = 
				runNegotiationSession(
						getAgentRepItem(0), 
						getAgentRepItem(1+winnerSessionIndex), 
						"Buyer", "Seller", 
						getProfileRepItems(0),
						getProfileRepItems(1+winnerSessionIndex),
						getAgentUtilitySpaces(0),
						getAgentUtilitySpaces(1+winnerSessionIndex), 
						paramsA, paramsB); 
				

				//TODO: secondPhaseSession.setAdditional(theoreticalOutcome);
			//secondPhaseSession.run(); // note, we can do this because TournamentRunner has no relation with AWT or Swing.
			for (AuctionBilateralAtomicNegoSession s: sessions) 
				s.cleanUp();
			secondPhaseSession.cleanUp();
		} catch (Exception e) { e.printStackTrace(); new Warning("Fatail error cancelled tournament run:"+e); }

	}

}
