package negotiator.protocol.auction;

import java.util.ArrayList;
import java.util.HashMap;

import agents.BayesianAgentForAuction;

import negotiator.*;
import negotiator.exceptions.Warning;
import negotiator.repository.AgentRepItem;
import negotiator.repository.ProfileRepItem;
import negotiator.tournament.Tournament;
import negotiator.tournament.VariablesAndValues.AgentParamValue;
import negotiator.tournament.VariablesAndValues.AgentParameterVariable;
import negotiator.tournament.VariablesAndValues.AgentValue;
import negotiator.tournament.VariablesAndValues.AgentVariable;
import negotiator.tournament.VariablesAndValues.TournamentValue;

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
	@Override
	public void run() {
		try { 
			int numberOfSellers = getNumberOfAgents()-1;
			//run the sessions
			AuctionBilateralAtomicNegoSession[] sessions = new AuctionBilateralAtomicNegoSession[numberOfSellers];
			for (int i=0;i<numberOfSellers;i++) {
				HashMap<AgentParameterVariable,AgentParamValue> centerParams = getAgentParams(0);
				centerParams.put(new AgentParameterVariable(new AgentParam(BayesianAgentForAuction.class.getName(),"opponent",-1.,1.)), new AgentParamValue(Double.valueOf(i)));
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
			Bid lSecondBestBid = null;
			AuctionBilateralAtomicNegoSession winnerSession = null;
			//				NegotiationSession2 secondBestSession = null;
			int winnerSessionIndex=0, i=0;
			Bid lBestBid=null;
			for (AuctionBilateralAtomicNegoSession s: sessions) {
				if(s.getNegotiationOutcome().agentAutility>lMaxUtil) {
					lSecondPrice = lMaxUtil;
					lSecondBestBid = s.getNegotiationOutcome().AgentABids.get(s.getNegotiationOutcome().AgentABids.size()-1).bid;
					lBestBid = s.getNegotiationOutcome().AgentABids.get(s.getNegotiationOutcome().AgentABids.size()-1).bid;
					lMaxUtil = s.getNegotiationOutcome().agentAutility;
					//secondBestSession = winnerSession;
					winnerSession = s;
					winnerSessionIndex = i;
				} else if(s.getNegotiationOutcome().agentAutility>lSecondPrice) { 
					lSecondPrice = s.getNegotiationOutcome().agentAutility;
					lSecondBestBid = s.getNegotiationOutcome().AgentABids.get(s.getNegotiationOutcome().AgentABids.size()-1).bid;
				}
				i++;
			}
			boolean bContinue = true;
			int opponentIndex = winnerSessionIndex;
			while(bContinue) {
				//calculate the strarting utils

				if(opponentIndex==0) opponentIndex =1; else opponentIndex = 0;
				AuctionBilateralAtomicNegoSession nextSession = sessions[opponentIndex];
				BayesianAgentForAuction center = (BayesianAgentForAuction)(nextSession.getAgentA());
				BayesianAgentForAuction seller = (BayesianAgentForAuction)(nextSession.getAgentB());
				double centerStartingUtil = Double.NEGATIVE_INFINITY;
				try {
					BidIterator iter = new BidIterator(nextSession.getAgentAUtilitySpace().getDomain());
					double tmp = center.getOpponentUtility(lBestBid);
					while(iter.hasNext()) {
						Bid bid = iter.next();
						double lTmpExpecteUtility = nextSession.getAgentAUtilitySpace().getUtility(bid);
						if(lTmpExpecteUtility > centerStartingUtil)
							if(Math.abs(center.getOpponentUtility(bid)-tmp)<ALLOWED_UTILITY_DEVIATION) 
								centerStartingUtil = lTmpExpecteUtility;
					}
				}catch (Exception e) {
					e.printStackTrace();
				}
				double sellerStartingUtil = Double.NEGATIVE_INFINITY;
				try {
					BidIterator iter = new BidIterator(nextSession.getAgentBUtilitySpace().getDomain());
					double tmp = seller.getOpponentUtility(lBestBid);
					while(iter.hasNext()) {				
						Bid bid = iter.next();
						double lTmpExpecteUtility = nextSession.getAgentBUtilitySpace().getUtility(bid);				
						if(lTmpExpecteUtility > sellerStartingUtil ) {
							if(Math.abs(seller.getOpponentUtility(bid)- tmp)<ALLOWED_UTILITY_DEVIATION) {						
								sellerStartingUtil = lTmpExpecteUtility ;
							}
						}									
					}
				}catch (Exception e) {
					e.printStackTrace();
				}

				HashMap<AgentParameterVariable,AgentParamValue> paramsA = new HashMap<AgentParameterVariable,AgentParamValue> ();
				HashMap<AgentParameterVariable,AgentParamValue> paramsB = new HashMap<AgentParameterVariable,AgentParamValue> ();
				paramsA.put(new AgentParameterVariable(new AgentParam(BayesianAgentForAuction.class.getName(),"role",-1.,1.)), new AgentParamValue(0.9));
				paramsA.put(new AgentParameterVariable(new AgentParam(BayesianAgentForAuction.class.getName(),"reservation",0.,1.)), new AgentParamValue(lSecondPrice));
				paramsA.put(new AgentParameterVariable(new AgentParam(BayesianAgentForAuction.class.getName(),"starting_utility",0.,1.)), new AgentParamValue(centerStartingUtil));
				paramsA.put(new AgentParameterVariable(new AgentParam(BayesianAgentForAuction.class.getName(),"opponent",-1.,1.)), new AgentParamValue(Double.valueOf(opponentIndex)));			
				//paramsA.put(new AgentParameterVariable(new AgentParam(BayesianAgentForAuction.class.getName(),"reservation",0.,1.)), new AgentParamValue(0.6));
				paramsA.put(new AgentParameterVariable(new AgentParam(BayesianAgentForAuction.class.getName(),"phase",0.,1.)), new AgentParamValue(0.9));
				paramsB.put(new AgentParameterVariable(new AgentParam(BayesianAgentForAuction.class.getName(),"role",-1.,1.)), new AgentParamValue(-0.9));
				paramsB.put(new AgentParameterVariable(new AgentParam(BayesianAgentForAuction.class.getName(),"phase",0.,1.)), new AgentParamValue(0.9));
				paramsB.put(new AgentParameterVariable(new AgentParam(BayesianAgentForAuction.class.getName(),"starting_utility",0.,1.)), new AgentParamValue(sellerStartingUtil));

				AuctionBilateralAtomicNegoSession secondPhaseSession = 
					runNegotiationSession(
							nextSession.getAgentA(),
							nextSession.getAgentB(),
							getAgentRepItem(0), 
							getAgentRepItem(1+opponentIndex), 
							"Buyer", "Seller", 
							getProfileRepItems(0),
							getProfileRepItems(1+opponentIndex),
							getAgentUtilitySpaces(0),
							getAgentUtilitySpaces(1+opponentIndex), 
							paramsA, paramsB); 
				//TODO: secondPhaseSession.setAdditional(theoreticalOutcome);
				//secondPhaseSession.run(); // note, we can do this because TournamentRunner has no relation with AWT or Swing.
				//secondPhaseSession.getNegotiationOutcome().
				lBestBid = secondPhaseSession.getNegotiationOutcome().AgentABids.get(secondPhaseSession.getNegotiationOutcome().AgentABids.size()).bid;
				for (AuctionBilateralAtomicNegoSession s: sessions) 
					s.cleanUp();
				secondPhaseSession.cleanUp();
			}
		} catch (Exception e) { e.printStackTrace(); new Warning("Fatail error cancelled tournament run:"+e); }

	}
	@Override
	protected static AuctionProtocol createSession(Tournament tournament,ProfileRepItem profileCenter, ProfileRepItem profileSeller1, ProfileRepItem profileSeller2) throws Exception {

		ArrayList<AgentVariable> agentVars=tournament.getAgentVars();
		if (agentVars.size()!=2) throw new IllegalStateException("Tournament does not contain 2 agent variables");
		ArrayList<TournamentValue> agentAvalues=agentVars.get(0).getValues();
		if (agentAvalues.isEmpty()) 
			throw new IllegalStateException("Agent A does not contain any values!");
		ArrayList<TournamentValue> agentBvalues=agentVars.get(1).getValues();
		if (agentBvalues.isEmpty()) 
			throw new IllegalStateException("Agent B does not contain any values!");

		ProfileRepItem[] profiles= new ProfileRepItem[3];//getProfiles();
		profiles[0] = profileCenter;
		profiles[1] = profileSeller1;
		profiles[2] = profileSeller2;
		AgentRepItem[] agents= new AgentRepItem[3];//getProfiles();
		AgentRepItem agentA=((AgentValue)agentAvalues.get(0)).getValue();
		agents[0] = agentA;
		agents[1] = agentA;
		agents[2] = agentA;
		//prepare parameters
		HashMap<AgentParameterVariable,AgentParamValue> paramsA = new HashMap<AgentParameterVariable,AgentParamValue>();
		HashMap<AgentParameterVariable,AgentParamValue> paramsB = new HashMap<AgentParameterVariable,AgentParamValue>();
		paramsA.put(new AgentParameterVariable(new AgentParam(BayesianAgentForAuction.class.getName(),"role",-1.,1.)), new AgentParamValue(2.1));
		//paramsA.put(new AgentParameterVariable(new AgentParam(BayesianAgentForAuction.class.getName(),"reservation",0.,1.)), new AgentParamValue(reservationValue));
		paramsA.put(new AgentParameterVariable(new AgentParam(BayesianAgentForAuction.class.getName(),"phase",0.,1.)), new AgentParamValue(-0.9));
		paramsB.put(new AgentParameterVariable(new AgentParam(BayesianAgentForAuction.class.getName(),"role",-1.,1.)), new AgentParamValue(2.1));
		//paramsB.put(new AgentParameterVariable(new AgentParam(BayesianAgentForAuction.class.getName(),"reservation",0.,1.)), new AgentParamValue(reservationValue));
		paramsB.put(new AgentParameterVariable(new AgentParam(BayesianAgentForAuction.class.getName(),"phase",0.,1.)), new AgentParamValue(-0.9));
		HashMap<AgentParameterVariable, AgentParamValue>[] params = new HashMap[3]; 
		params[0] = paramsA;
		params[1] = paramsB;
		params[2] = paramsB;
		MultiPhaseAuctionProtocol session = new  MultiPhaseAuctionProtocol(agents,  profiles,	params) ;
		return session;

	}

}
