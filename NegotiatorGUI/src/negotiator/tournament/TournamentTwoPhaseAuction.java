package negotiator.tournament;

import java.util.ArrayList;

import negotiator.analysis.BidSpace;
import negotiator.repository.AgentRepItem;
import negotiator.repository.ProfileRepItem;
import negotiator.tournament.VariablesAndValues.AgentValue;
import negotiator.tournament.VariablesAndValues.AgentVariable;
import negotiator.tournament.VariablesAndValues.TournamentValue;

public class TournamentTwoPhaseAuction extends Tournament {

	@Override
	public ArrayList<NegotiationSession2> getSessions() throws Exception {
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
		for (ProfileRepItem profileA: profiles) {
			for (ProfileRepItem profileB: profiles) {
				if (!(profileA.getDomain().equals(profileB.getDomain())) ) continue; // domains must match. Optimizable by selecting matching profiles first...
				if (profileA.equals(profileB)) continue;
				for (TournamentValue agentAval: agentAvalues ) {
					AgentRepItem agentA=((AgentValue)agentAval).getValue();
					for (TournamentValue agentBval: agentBvalues) {
						AgentRepItem agentB=((AgentValue)agentBval).getValue();
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
				}
				
			}
		}
		return sessions;
	}

}
