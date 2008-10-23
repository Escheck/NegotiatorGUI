package negotiator.tournament;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;


import agents.BayesianAgentForAuction;

import negotiator.AgentParam;
import negotiator.analysis.BidSpace;
import negotiator.repository.AgentRepItem;
import negotiator.repository.DomainRepItem;
import negotiator.repository.ProfileRepItem;
import negotiator.tournament.VariablesAndValues.AgentParamValue;
import negotiator.tournament.VariablesAndValues.AgentParameterVariable;
import negotiator.tournament.VariablesAndValues.AgentValue;
import negotiator.tournament.VariablesAndValues.AgentVariable;
import negotiator.tournament.VariablesAndValues.TournamentValue;
import negotiator.utility.UtilitySpace;

public class TournamentTwoPhaseAuction extends Tournament {
	private int sessionIndex;
	private ArrayList<ArrayList<NegotiationSession2>> allSessions;
	public TournamentTwoPhaseAuction() {
		super();
	}
	private void generateAllSessions() {
		try {
			allSessions = new ArrayList<ArrayList<NegotiationSession2>>();
			sessionIndex = 0;
			DomainRepItem domain = new DomainRepItem(new URL("file:etc/templates/SON/son_domain.xml"));
			//center profiles
			ProfileRepItem center1 = new ProfileRepItem(new URL("file:etc/templates/SON/son_center_1.xml"),domain);
			ProfileRepItem center2 = new ProfileRepItem(new URL("file:etc/templates/SON/son_center_2.xml"),domain);
			ProfileRepItem center3 = new ProfileRepItem(new URL("file:etc/templates/SON/son_center_3.xml"),domain);
			ProfileRepItem center4 = new ProfileRepItem(new URL("file:etc/templates/SON/son_center_4.xml"),domain);
			ProfileRepItem center5 = new ProfileRepItem(new URL("file:etc/templates/SON/son_center_5.xml"),domain);
			ProfileRepItem center6 = new ProfileRepItem(new URL("file:etc/templates/SON/son_center_6.xml"),domain);
			ProfileRepItem center7 = new ProfileRepItem(new URL("file:etc/templates/SON/son_center_7.xml"),domain);
			ProfileRepItem center8 = new ProfileRepItem(new URL("file:etc/templates/SON/son_center_8.xml"),domain);
			ProfileRepItem center9 = new ProfileRepItem(new URL("file:etc/templates/SON/son_center_9.xml"),domain);
			ProfileRepItem center10 = new ProfileRepItem(new URL("file:etc/templates/SON/son_center_10.xml"),domain);
			ProfileRepItem center11 = new ProfileRepItem(new URL("file:etc/templates/SON/son_center_11.xml"),domain);
			ProfileRepItem center12 = new ProfileRepItem(new URL("file:etc/templates/SON/son_center_12.xml"),domain);
			//seller profiles
			ProfileRepItem seller1 = new ProfileRepItem(new URL("file:etc/templates/SON/son_seller_1.xml"),domain);
			ProfileRepItem seller2 = new ProfileRepItem(new URL("file:etc/templates/SON/son_seller_2.xml"),domain);
			ProfileRepItem seller3 = new ProfileRepItem(new URL("file:etc/templates/SON/son_seller_3.xml"),domain);
			ProfileRepItem seller4 = new ProfileRepItem(new URL("file:etc/templates/SON/son_seller_4.xml"),domain);
			ProfileRepItem seller5 = new ProfileRepItem(new URL("file:etc/templates/SON/son_seller_5.xml"),domain);
			ProfileRepItem seller6 = new ProfileRepItem(new URL("file:etc/templates/SON/son_seller_6.xml"),domain);
			ProfileRepItem seller7 = new ProfileRepItem(new URL("file:etc/templates/SON/son_seller_7.xml"),domain);
			ProfileRepItem seller8 = new ProfileRepItem(new URL("file:etc/templates/SON/son_seller_8.xml"),domain);
			ProfileRepItem seller9 = new ProfileRepItem(new URL("file:etc/templates/SON/son_seller_9.xml"),domain);
			ProfileRepItem seller10 = new ProfileRepItem(new URL("file:etc/templates/SON/son_seller_10.xml"),domain);
			ProfileRepItem seller11 = new ProfileRepItem(new URL("file:etc/templates/SON/son_seller_11.xml"),domain);
			ProfileRepItem seller12 = new ProfileRepItem(new URL("file:etc/templates/SON/son_seller_12.xml"),domain);
			
			//allSessions.add(createSession(center5, seller4, seller1 ));
			//allSessions.add(createSession(center12, seller2, seller10));
			//allSessions.add(createSession(center1, seller2, seller8));
			//allSessions.add(createSession(center10, seller7, seller9));
			//allSessions.add(createSession(center7, seller11, seller9));
			allSessions.add(createSession(center8, seller11, seller10));
			//allSessions.add(createSession(center10, seller3, seller6));
			//allSessions.add(createSession(center7, seller4, seller7));
			//allSessions.add(createSession(center10, seller11, seller8));
			//allSessions.add(createSession(center11, seller5, seller11));
			//allSessions.add(createSession(center6, seller7, seller3));
			//allSessions.add(createSession(center6, seller10, seller5));
			//allSessions.add(createSession(center8, seller6, seller3));
			//allSessions.add(createSession(center2, seller5, seller1));
			//allSessions.add(createSession(center3, seller5, seller4));
			//allSessions.add(createSession(center10, seller5, seller2));
			//allSessions.add(createSession(center1, seller3, seller6));
			//allSessions.add(createSession(center3, seller5, seller4));
			//allSessions.add(createSession(center8, seller10, seller6));
			//allSessions.add(createSession(center4, seller12, seller3));
			//allSessions.add(createSession(center3, seller2, seller11));
			//allSessions.add(createSession(center6, seller2, seller5));
			//allSessions.add(createSession(center10, seller2, seller6));
			//allSessions.add(createSession(center12, seller8, seller12));
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}

	}
	
	private ArrayList<NegotiationSession2> createSession(ProfileRepItem profileCenter, ProfileRepItem profileSeller1, ProfileRepItem profileSeller2) throws Exception {
		bidSpaceCash = new HashMap<UtilitySpace, HashMap<UtilitySpace,BidSpace>>();
		ArrayList<AgentVariable> agents=getAgentVars();
		if (agents.size()!=2) throw new IllegalStateException("Tournament does not contain 2 agent variables");
		ArrayList<TournamentValue> agentAvalues=agents.get(0).getValues();
		if (agentAvalues.isEmpty()) 
			throw new IllegalStateException("Agent A does not contain any values!");
		ArrayList<TournamentValue> agentBvalues=agents.get(1).getValues();
		if (agentBvalues.isEmpty()) 
			throw new IllegalStateException("Agent B does not contain any values!");

		ArrayList<ProfileRepItem> profiles= new ArrayList<ProfileRepItem>();//getProfiles();
		//profiles.add(profileCenter);
		profiles.add(profileSeller1);
		profiles.add(profileSeller2);
		// we need to exhaust the possible combinations of all variables.
		// we iterate explicitly over the profile and agents, because we need to permutate
		// only the parameters for the selected agents.
		ArrayList<NegotiationSession2>sessions =new ArrayList<NegotiationSession2>();
		ProfileRepItem profileA = profileCenter;

		for (int i=0;i<profiles.size();i++) {
			ProfileRepItem profileB =  profiles.get(i); 
			if (!(profileA.getDomain().equals(profileB.getDomain())) ) continue; // domains must match. Optimizable by selecting matching profiles first...
			if (profileA.equals(profileB)) continue;
			AgentRepItem agentA=((AgentValue)agentAvalues.get(0)).getValue();
			AgentRepItem agentB=((AgentValue)agentBvalues.get(0)).getValue();
			//prepare parameters
			HashMap<AgentParameterVariable,AgentParamValue>  paramsA=new HashMap<AgentParameterVariable,AgentParamValue> ();
			HashMap<AgentParameterVariable,AgentParamValue>  paramsB=new HashMap<AgentParameterVariable,AgentParamValue> ();
			paramsA.put(new AgentParameterVariable(new AgentParam(BayesianAgentForAuction.class.getName(),"role",-1.,1.)), new AgentParamValue(0.9));
			//paramsA.put(new AgentParameterVariable(new AgentParam(BayesianAgentForAuction.class.getName(),"reservation",0.,1.)), new AgentParamValue(reservationValue));
			paramsA.put(new AgentParameterVariable(new AgentParam(BayesianAgentForAuction.class.getName(),"phase",0.,1.)), new AgentParamValue(-0.9));
			paramsB.put(new AgentParameterVariable(new AgentParam(BayesianAgentForAuction.class.getName(),"role",-1.,1.)), new AgentParamValue(-0.9));
			//paramsB.put(new AgentParameterVariable(new AgentParam(BayesianAgentForAuction.class.getName(),"reservation",0.,1.)), new AgentParamValue(reservationValue));
			paramsB.put(new AgentParameterVariable(new AgentParam(BayesianAgentForAuction.class.getName(),"phase",0.,1.)), new AgentParamValue(-0.9));
			NegotiationSession2 session =new  NegotiationSession2(agentA, agentB, profileA,profileB,
					AGENT_A_NAME, AGENT_B_NAME,paramsA,paramsB,sessionnr, 1, false) ;
			sessions.add(session);
			//check if the analysis is already made for the prefs. profiles
			BidSpace bidSpace = getBidSpace(session.getAgentAUtilitySpace(), session.getAgentBUtilitySpace());
			if(bidSpace!=null) {
				session.setBidSpace(bidSpace);
			} else {
				bidSpace = new BidSpace(session.getAgentAUtilitySpace(),session.getAgentBUtilitySpace());
				addBidSpaceToCash(session.getAgentAUtilitySpace(), session.getAgentBUtilitySpace(), bidSpace);
				session.setBidSpace(bidSpace);
			}
		}

		return sessions;

	}
	@Override
	public ArrayList<NegotiationSession2> getSessions() throws Exception {
		if(allSessions==null) generateAllSessions();
		if(sessionIndex<allSessions.size()) {
		ArrayList<NegotiationSession2> result = allSessions.get(sessionIndex);
		sessionIndex++;
		return result;
		}
		else return null;
	}

}
