package negotiator.tournament;

import java.util.ArrayList;
import java.util.HashMap;

import agents.BayesianAgent;
import agents.BayesianAgentForAuction;

import negotiator.AgentParam;
import negotiator.analysis.BidSpace;
import negotiator.repository.AgentRepItem;
import negotiator.repository.ProfileRepItem;
import negotiator.tournament.VariablesAndValues.AgentParamValue;
import negotiator.tournament.VariablesAndValues.AgentParameterVariable;
import negotiator.tournament.VariablesAndValues.AgentValue;
import negotiator.tournament.VariablesAndValues.AgentVariable;
import negotiator.tournament.VariablesAndValues.TournamentValue;
import negotiator.utility.UtilitySpace;

public class TournamentTwoPhaseAuction extends Tournament {

	@Override
	public ArrayList<NegotiationSession2> getSessions() throws Exception {
		bidSpaceCash = new HashMap<UtilitySpace, HashMap<UtilitySpace,BidSpace>>();
		ArrayList<AgentVariable> agents=getAgentVars();
		if (agents.size()!=2) throw new IllegalStateException("Tournament does not contain 2 agent variables");
		ArrayList<TournamentValue> agentAvalues=agents.get(0).getValues();
		if (agentAvalues.isEmpty()) 
			throw new IllegalStateException("Agent A does not contain any values!");
		ArrayList<TournamentValue> agentBvalues=agents.get(1).getValues();
		if (agentBvalues.isEmpty()) 
			throw new IllegalStateException("Agent B does not contain any values!");

		ArrayList<ProfileRepItem> profiles=getProfiles();

		// we need to exhaust the possible combinations of all variables.
		// we iterate explicitly over the profile and agents, because we need to permutate
		// only the parameters for the selected agents.
		ArrayList<NegotiationSession2>sessions =new ArrayList<NegotiationSession2>();
		ProfileRepItem profileA = profiles.get(0);
		
			for (int i=1;i<profiles.size();i++) {
				ProfileRepItem profileB =  profiles.get(i); 
				if (!(profileA.getDomain().equals(profileB.getDomain())) ) continue; // domains must match. Optimizable by selecting matching profiles first...
				if (profileA.equals(profileB)) continue;
					AgentRepItem agentA=((AgentValue)agentAvalues.get(0)).getValue();
					AgentRepItem agentB=((AgentValue)agentBvalues.get(0)).getValue();
					//prepare parameters
					HashMap<AgentParameterVariable,AgentParamValue>  paramsA=new HashMap<AgentParameterVariable,AgentParamValue> ();
					HashMap<AgentParameterVariable,AgentParamValue>  paramsB=new HashMap<AgentParameterVariable,AgentParamValue> ();
					paramsA.put(new AgentParameterVariable(new AgentParam(BayesianAgentForAuction.class.getName(),"role",-1.,1.)), new AgentParamValue(-0.9));
					paramsA.put(new AgentParameterVariable(new AgentParam(BayesianAgentForAuction.class.getName(),"reservation",0.,1.)), new AgentParamValue(0.6));
					paramsA.put(new AgentParameterVariable(new AgentParam(BayesianAgentForAuction.class.getName(),"phase",0.,1.)), new AgentParamValue(-0.9));
					paramsB.put(new AgentParameterVariable(new AgentParam(BayesianAgentForAuction.class.getName(),"role",-1.,1.)), new AgentParamValue(0.9));
					paramsB.put(new AgentParameterVariable(new AgentParam(BayesianAgentForAuction.class.getName(),"reservation",0.,1.)), new AgentParamValue(0.6));
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

}
