package negotiator.protocol.auction;

import java.util.HashMap;

import negotiator.Agent;
import negotiator.NegotiationOutcome;
import negotiator.protocol.Protocol;
import negotiator.repository.AgentRepItem;
import negotiator.repository.ProfileRepItem;
import negotiator.tournament.VariablesAndValues.AgentParamValue;
import negotiator.tournament.VariablesAndValues.AgentParameterVariable;
import negotiator.tournament.VariablesAndValues.TournamentValue;

public class AuctionProtocol extends Protocol {

	public AuctionProtocol(AgentRepItem[] agentRepItems,
			ProfileRepItem[] profileRepItems,
			HashMap<AgentParameterVariable, AgentParamValue>[] agentParams)
			throws Exception {
		super(agentRepItems, profileRepItems, agentParams);
		// TODO Auto-generated constructor stub
	}

	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

	public NegotiationOutcome getNegotiationOutcome() {
		// TODO Auto-generated method stub
		return null;
	}

	public void run() {
		// TODO Auto-generated method stub

	}

	public Agent getAgent(int index) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public void startSession() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Protocol getTournamentSessions(TournamentValue[] vars) {
		// TODO Auto-generated method stub
		return null;
	}

}
