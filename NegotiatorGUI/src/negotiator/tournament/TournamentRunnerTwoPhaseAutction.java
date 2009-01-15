package negotiator.tournament;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

import com.sun.org.apache.xpath.internal.operations.Lte;

import agents.BayesianAgentForAuction;

import negotiator.Agent;
import negotiator.AgentParam;
import negotiator.Bid;
import negotiator.BidIterator;
import negotiator.NegotiationEventListener;
import negotiator.analysis.BidSpace;
import negotiator.exceptions.Warning;
import negotiator.protocol.alternatingoffers.AlternatingOffersNegotiationSession;
import negotiator.tournament.VariablesAndValues.AgentParamValue;
import negotiator.tournament.VariablesAndValues.AgentParameterVariable;
import negotiator.utility.UtilitySpace;
import negotiator.xml.SimpleElement;

public class TournamentRunnerTwoPhaseAutction extends TournamentRunner {
	final private double ALLOWED_UTILITY_DEVIATION = 0.015; 

	public TournamentRunnerTwoPhaseAutction(Tournament t,
			NegotiationEventListener ael) throws Exception {
		super(t, ael);
		// TODO Auto-generated constructor stub
	}
	private void runFreeNegotiationSessions(LinkedList<AlternatingOffersNegotiationSession> sessions ) throws Exception {
		LinkedList<AlternatingOffersNegotiationSession> freeSessions = new LinkedList<AlternatingOffersNegotiationSession>();
		
		for (AlternatingOffersNegotiationSession s: sessions) {
			HashMap<AgentParameterVariable,AgentParamValue>  paramsA=new HashMap<AgentParameterVariable,AgentParamValue> ();
			HashMap<AgentParameterVariable,AgentParamValue>  paramsB=new HashMap<AgentParameterVariable,AgentParamValue> ();
			paramsA.put(new AgentParameterVariable(new AgentParam(BayesianAgentForAuction.class.getName(),"role",-1.,3.)), new AgentParamValue(2.1));
			paramsA.put(new AgentParameterVariable(new AgentParam(BayesianAgentForAuction.class.getName(),"phase",-1.,1.)), new AgentParamValue(-0.9));
			paramsB.put(new AgentParameterVariable(new AgentParam(BayesianAgentForAuction.class.getName(),"role",-1.,3.)), new AgentParamValue(2.1));
			paramsB.put(new AgentParameterVariable(new AgentParam(BayesianAgentForAuction.class.getName(),"phase",-1.,1.)), new AgentParamValue(-0.9));
			
			AlternatingOffersNegotiationSession freeSession = new AlternatingOffersNegotiationSession(s.agentArep,
					s.agentBrep,
					s.getProfileArep(),
					s.getProfileBrep(),
					s.getAgentAname(),
					s.getAgentBname(),
					paramsA,
					paramsB,
					100,
					1,
					true, 1200, 1200, 1);
			freeSessions.add(freeSession);
			BidSpace bidSpace = tournament.getBidSpace(freeSession.getAgentAUtilitySpace(), freeSession.getAgentBUtilitySpace());
			if(bidSpace!=null) {
				freeSession.setBidSpace(bidSpace);
			} else {
				bidSpace = new BidSpace(freeSession.getAgentAUtilitySpace(),freeSession.getAgentBUtilitySpace());
				tournament.addBidSpaceToCash(freeSession.getAgentAUtilitySpace(), freeSession.getAgentBUtilitySpace(), bidSpace);
				freeSession.setBidSpace(bidSpace);
			}
			//freeSession.setAdditional(theoreticalOutcome);
			for (NegotiationEventListener list: negotiationEventListeners) freeSession.addNegotiationEventListener(list);
			fireNegotiationSessionEvent(freeSession);
			freeSession.run(); // note, we can do this because TournamentRunner has no relation with AWT or Swing.			

		}
		//determine winner
		double lMaxUtil= Double.NEGATIVE_INFINITY;
		double lSecondPrice = Double.NEGATIVE_INFINITY;
		Bid lSecondBestBid = null;
		AlternatingOffersNegotiationSession winnerSession = null;

		for (AlternatingOffersNegotiationSession s: freeSessions) {
			if(s.getSessionRunner().getNegotiationOutcome().agentAutility>lMaxUtil) {
				lSecondPrice = lMaxUtil;
				lSecondBestBid = s.getSessionRunner().getNegotiationOutcome().AgentABids.get(s.getSessionRunner().getNegotiationOutcome().AgentABids.size()-1).bid;
				lMaxUtil = s.getSessionRunner().getNegotiationOutcome().agentAutility;
				//secondBestSession = winnerSession;
				winnerSession = s;
			} else if(s.getSessionRunner().getNegotiationOutcome().agentAutility>lSecondPrice) 
				lSecondPrice = s.getSessionRunner().getNegotiationOutcome().agentAutility;
				lSecondBestBid = s.getSessionRunner().getNegotiationOutcome().AgentABids.get(s.getSessionRunner().getNegotiationOutcome().AgentABids.size()-1).bid;
		}
		//calculate the strarting utils
		BayesianAgentForAuction center = (BayesianAgentForAuction)(winnerSession.getSessionRunner().agentA);
		BayesianAgentForAuction seller = (BayesianAgentForAuction)(winnerSession.getSessionRunner().agentB);
		double centerStartingUtil = Double.NEGATIVE_INFINITY;
		try {
			BidIterator iter = new BidIterator(winnerSession.getAgentAUtilitySpace().getDomain());
			double tmp = center.getOpponentUtility(lSecondBestBid);
			while(iter.hasNext()) {
				Bid bid = iter.next();
				double lTmpExpecteUtility = winnerSession.getAgentAUtilitySpace().getUtility(bid);
				if(lTmpExpecteUtility > centerStartingUtil)
					if(Math.abs(center.getOpponentUtility(bid)-tmp)<ALLOWED_UTILITY_DEVIATION) 
						centerStartingUtil = lTmpExpecteUtility ;
													
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		double sellerStartingUtil = Double.NEGATIVE_INFINITY;
		try {
			BidIterator iter = new BidIterator(winnerSession.getAgentBUtilitySpace().getDomain());
			double tmp = seller.getOpponentUtility(lSecondBestBid);
			while(iter.hasNext()) {				
				Bid bid = iter.next();
				double lTmpExpecteUtility = winnerSession.getAgentBUtilitySpace().getUtility(bid);				
				if(lTmpExpecteUtility > sellerStartingUtil ) {
					if(Math.abs(seller.getOpponentUtility(bid)- tmp)<ALLOWED_UTILITY_DEVIATION) {						
						sellerStartingUtil = lTmpExpecteUtility ;
					}
				}									
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("Center starting utility = " + String.valueOf(centerStartingUtil));
		System.out.println("Seller starting utility = " + String.valueOf(sellerStartingUtil));
		//NegotiationSession2 winnerSession = freeSessions.get(0);
		HashMap<AgentParameterVariable,AgentParamValue>  paramsA=new HashMap<AgentParameterVariable,AgentParamValue> ();
		HashMap<AgentParameterVariable,AgentParamValue>  paramsB=new HashMap<AgentParameterVariable,AgentParamValue> ();
		paramsA.put(new AgentParameterVariable(new AgentParam(BayesianAgentForAuction.class.getName(),"role",-1.,3.)), new AgentParamValue(2.1));
		paramsA.put(new AgentParameterVariable(new AgentParam(BayesianAgentForAuction.class.getName(),"phase",0.,1.)), new AgentParamValue(0.9));
		paramsA.put(new AgentParameterVariable(new AgentParam(BayesianAgentForAuction.class.getName(),"starting_utility",0.,1.)), new AgentParamValue(centerStartingUtil));
		paramsB.put(new AgentParameterVariable(new AgentParam(BayesianAgentForAuction.class.getName(),"role",-1.,3.)), new AgentParamValue(2.1));
		paramsB.put(new AgentParameterVariable(new AgentParam(BayesianAgentForAuction.class.getName(),"phase",0.,1.)), new AgentParamValue(0.9));
		paramsB.put(new AgentParameterVariable(new AgentParam(BayesianAgentForAuction.class.getName(),"starting_utility",0.,1.)), new AgentParamValue(sellerStartingUtil));

		AlternatingOffersNegotiationSession secondPhaseSession = new AlternatingOffersNegotiationSession(winnerSession.getAgentA(),
				winnerSession.getAgentB(),
				winnerSession.getProfileArep(),
				winnerSession.getProfileBrep(),
				winnerSession.getAgentAname(),
				winnerSession.getAgentBname(),
				paramsA,
				paramsB,
				100,
				1,
				true, 1200, 1200, 1
		);
		BidSpace bidSpace = tournament.getBidSpace(secondPhaseSession.getAgentAUtilitySpace(), secondPhaseSession.getAgentBUtilitySpace());
		if(bidSpace!=null) {
			secondPhaseSession.setBidSpace(bidSpace);
		} else {
			bidSpace = new BidSpace(secondPhaseSession.getAgentAUtilitySpace(),secondPhaseSession.getAgentBUtilitySpace());
			tournament.addBidSpaceToCash(secondPhaseSession.getAgentAUtilitySpace(), secondPhaseSession.getAgentBUtilitySpace(), bidSpace);
			secondPhaseSession.setBidSpace(bidSpace);
		}
		//secondPhaseSession.setAdditional(theoreticalOutcome);
		for (NegotiationEventListener list: negotiationEventListeners) secondPhaseSession.addNegotiationEventListener(list);
		fireNegotiationSessionEvent(secondPhaseSession);
		secondPhaseSession.run(); // note, we can do this because TournamentRunner has no relation with AWT or Swing.
		
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		LinkedList<AlternatingOffersNegotiationSession> sessions;
		try { 
			while(true) {
				sessions= new LinkedList<AlternatingOffersNegotiationSession>( tournament.getSessions());
				runFreeNegotiationSessions(sessions);
				if(true) continue;
				if(sessions==null) break;
				//calcualte theoretical outcome
				double outcome[] = new double[sessions.size()];
				//int i=0;
				for (int i=0;i<sessions.size();i++) {
					UtilitySpace centerUtilitySpace = sessions.get(i).getAgentAUtilitySpace();
					UtilitySpace sellerUtilitySpace = sessions.get(i).getAgentBUtilitySpace();
					outcome[i] = Double.NEGATIVE_INFINITY;
					BidIterator iter = new BidIterator(sessions.get(i).getAgentAUtilitySpace().getDomain());
					while(iter.hasNext()) {
						Bid bid = iter.next();
						if(Math.abs(sellerUtilitySpace.getUtility(bid)-sellerUtilitySpace.getReservationValue())<ALLOWED_UTILITY_DEVIATION) {
							double lTmpExpecteUtility = centerUtilitySpace.getUtility(bid);
							if(lTmpExpecteUtility > outcome[i]) {
								outcome[i]= lTmpExpecteUtility ;
							}
						}									
					}				
				}
				//find the winner
				int winner = 0;			
				double lSecondBest = Double.NEGATIVE_INFINITY;
				for(int i=1;i<outcome.length;i++) {
					if(outcome[i]>outcome[winner]) {
						lSecondBest = outcome[winner];
						winner = i;
					} else if (outcome[i]>lSecondBest) {
						lSecondBest =outcome[i]; 
					}
				}
				//find the final outcome
				UtilitySpace centerUtilitySpace = sessions.get(winner).getAgentAUtilitySpace();
				UtilitySpace sellerUtilitySpace = sessions.get(winner).getAgentBUtilitySpace();
				double finalOutcome[] = new double[2];
				BidIterator iter = new BidIterator(centerUtilitySpace.getDomain());
				while(iter.hasNext()) {
					Bid bid = iter.next();
					if(Math.abs(centerUtilitySpace.getUtility(bid)-lSecondBest)<ALLOWED_UTILITY_DEVIATION) {
						//double lTmpSim = fSimilarity.getSimilarity(tmpBid, pOppntBid);
						double lTmpExpecteUtility = sellerUtilitySpace.getUtility(bid);
						if(lTmpExpecteUtility > finalOutcome[0]) {
							finalOutcome[0] = lTmpExpecteUtility;
							finalOutcome[1] = centerUtilitySpace.getUtility(bid); 
						}
					}				

				}

				SimpleElement theoreticalOutcome = new SimpleElement("theoretical_outcome");
				theoreticalOutcome.setAttribute("winner", sessions.get(winner).getAgentBUtilitySpaceFileName());
				theoreticalOutcome.setAttribute("center_utility", String.valueOf(finalOutcome[1]));
				theoreticalOutcome.setAttribute("seller_utility", String.valueOf(finalOutcome[0]));
				SimpleElement optimalPoints = new SimpleElement("optimal_solution");
				theoreticalOutcome.addChildElement(optimalPoints); 
				for(int i=0;i<sessions.size();i++) {
					SimpleElement space = new SimpleElement("utility_space");
					optimalPoints.addChildElement(space);
					space.setAttribute("spaceA",sessions.get(i).getAgentAUtilitySpaceFileName() );
					space.setAttribute("spaceB",sessions.get(i).getAgentBUtilitySpaceFileName());
					SimpleElement solution = new SimpleElement("solution");
					optimalPoints.addChildElement(solution);
					solution.setAttribute("type", "Nash");
					solution.setAttribute("utilityA", String.valueOf(sessions.get(i).getBidSpace().getNash().utilityA));
					solution.setAttribute("utilityB", String.valueOf(sessions.get(i).getBidSpace().getNash().utilityB));
					solution = new SimpleElement("solution");
					optimalPoints.addChildElement(solution);
					solution.setAttribute("type", "Kalai");
					solution.setAttribute("utilityA", String.valueOf(sessions.get(i).getBidSpace().getKalaiSmorodinsky().utilityA));
					solution.setAttribute("utilityB", String.valueOf(sessions.get(i).getBidSpace().getKalaiSmorodinsky().utilityB));

				}
				//
				for (AlternatingOffersNegotiationSession s: sessions) {
					//if (the_event_listener!=null) s.actionEventListener=the_event_listener;
					for (NegotiationEventListener list: negotiationEventListeners) s.addNegotiationEventListener(list);
					fireNegotiationSessionEvent(s);
					s.run(); // note, we can do this because TournamentRunner has no relation with AWT or Swing.

				}

				//determine winner
				double lMaxUtil= Double.NEGATIVE_INFINITY;
				double lSecondPrice = Double.NEGATIVE_INFINITY;
				AlternatingOffersNegotiationSession winnerSession = null;
//				NegotiationSession2 secondBestSession = null;
				for (AlternatingOffersNegotiationSession s: sessions) {
					if(s.getSessionRunner().getNegotiationOutcome().agentAutility>lMaxUtil) {
						lSecondPrice = lMaxUtil;
						lMaxUtil = s.getSessionRunner().getNegotiationOutcome().agentAutility;
						//secondBestSession = winnerSession;
						winnerSession = s;
					} else if(s.getSessionRunner().getNegotiationOutcome().agentAutility>lSecondPrice) 
						lSecondPrice = s.getSessionRunner().getNegotiationOutcome().agentAutility;

				}

				HashMap<AgentParameterVariable,AgentParamValue>  paramsA=new HashMap<AgentParameterVariable,AgentParamValue> ();
				HashMap<AgentParameterVariable,AgentParamValue>  paramsB=new HashMap<AgentParameterVariable,AgentParamValue> ();
				paramsA.put(new AgentParameterVariable(new AgentParam(BayesianAgentForAuction.class.getName(),"role",-1.,1.)), new AgentParamValue(0.9));
				paramsA.put(new AgentParameterVariable(new AgentParam(BayesianAgentForAuction.class.getName(),"reservation",0.,1.)), new AgentParamValue(lSecondPrice));
				//paramsA.put(new AgentParameterVariable(new AgentParam(BayesianAgentForAuction.class.getName(),"reservation",0.,1.)), new AgentParamValue(0.6));
				paramsA.put(new AgentParameterVariable(new AgentParam(BayesianAgentForAuction.class.getName(),"phase",0.,1.)), new AgentParamValue(0.9));
				paramsB.put(new AgentParameterVariable(new AgentParam(BayesianAgentForAuction.class.getName(),"role",-1.,1.)), new AgentParamValue(-0.9));

				paramsB.put(new AgentParameterVariable(new AgentParam(BayesianAgentForAuction.class.getName(),"phase",0.,1.)), new AgentParamValue(0.9));

				AlternatingOffersNegotiationSession secondPhaseSession = new AlternatingOffersNegotiationSession(winnerSession.agentArep,
						winnerSession.agentBrep,
						winnerSession.getProfileArep(),
						winnerSession.getProfileBrep(),
						winnerSession.getAgentAname(),
						winnerSession.getAgentBname(),
						paramsA,
						paramsB,
						100,
						1,
						true, 1200, 1200, 1
				);
				BidSpace bidSpace = tournament.getBidSpace(secondPhaseSession.getAgentAUtilitySpace(), secondPhaseSession.getAgentBUtilitySpace());
				if(bidSpace!=null) {
					secondPhaseSession.setBidSpace(bidSpace);
				} else {
					bidSpace = new BidSpace(secondPhaseSession.getAgentAUtilitySpace(),secondPhaseSession.getAgentBUtilitySpace());
					tournament.addBidSpaceToCash(secondPhaseSession.getAgentAUtilitySpace(), secondPhaseSession.getAgentBUtilitySpace(), bidSpace);
					secondPhaseSession.setBidSpace(bidSpace);
				}
				secondPhaseSession.setAdditional(theoreticalOutcome);
				for (NegotiationEventListener list: negotiationEventListeners) secondPhaseSession.addNegotiationEventListener(list);
				fireNegotiationSessionEvent(secondPhaseSession);
				secondPhaseSession.run(); // note, we can do this because TournamentRunner has no relation with AWT or Swing.
			}	   		
		} catch (Exception e) { e.printStackTrace(); new Warning("Fatail error cancelled tournament run:"+e); }
		
	}

}
