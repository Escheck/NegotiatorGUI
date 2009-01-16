package negotiator.protocol.auction;

import java.util.HashMap;

import negotiator.protocol.NegotiationSession;
import negotiator.repository.AgentRepItem;
import negotiator.repository.ProfileRepItem;
import negotiator.tournament.VariablesAndValues.AgentParamValue;
import negotiator.tournament.VariablesAndValues.AgentParameterVariable;

public class AuctionNegotiationSession extends NegotiationSession{

	public AuctionNegotiationSession(AgentRepItem[] agentRepItems,
			ProfileRepItem[] profileRepItems,
			HashMap<AgentParameterVariable, AgentParamValue>[] agentParams)
			throws Exception {
		super(agentRepItems, profileRepItems, agentParams);
		// TODO Auto-generated constructor stub
	}

}
