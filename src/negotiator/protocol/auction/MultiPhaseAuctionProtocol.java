package negotiator.protocol.auction;

import java.util.HashMap;

import negotiator.NegotiationOutcome;
import negotiator.protocol.Protocol;
import negotiator.repository.AgentRepItem;
import negotiator.repository.ProfileRepItem;
import negotiator.tournament.VariablesAndValues.AgentParamValue;
import negotiator.tournament.VariablesAndValues.AgentParameterVariable;

public class MultiPhaseAuctionProtocol extends Protocol {

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
		// TODO Auto-generated method stub

	}

}
