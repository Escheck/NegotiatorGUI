package negotiator.protocol.alternatingoffers;

import java.util.HashMap;

import negotiator.Agent;
import negotiator.Bid;
import negotiator.actions.Action;
import negotiator.actions.Offer;
import negotiator.protocol.Protocol;
import negotiator.tournament.VariablesAndValues.AgentParamValue;
import negotiator.tournament.VariablesAndValues.AgentParameterVariable;
import negotiator.utility.UtilitySpace;

/**
 * Used by {@link AlternatingOffersProtocol}.
 */
public class AlternatingOffersBilateralAtomicNegoSessionOneSidedBidding extends AlternatingOffersBilateralAtomicNegoSession
{

	public AlternatingOffersBilateralAtomicNegoSessionOneSidedBidding(Protocol protocol, Agent agentA, Agent agentB, String agentAname, String agentBname,
			UtilitySpace spaceA, UtilitySpace spaceB, HashMap<AgentParameterVariable, AgentParamValue> agentAparams,
			HashMap<AgentParameterVariable, AgentParamValue> agentBparams, String startingAgent) throws Exception
	{
		super(protocol, agentA, agentB, agentAname, agentBname, spaceA, spaceB, agentAparams, agentBparams, startingAgent);
	}
	
	@Override
	protected Action intercept(Action lastAction) throws Exception
	{
		if (currentAgent == agentA)
		{
			Bid maxUtilityBid = spaceA.getMaxUtilityBid();
			return new Offer(currentAgent, maxUtilityBid);
		}
		return lastAction;
	}

}
