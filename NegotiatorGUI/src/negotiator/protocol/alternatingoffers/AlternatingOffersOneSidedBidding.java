package negotiator.protocol.alternatingoffers;

import java.util.HashMap;

import negotiator.repository.AgentRepItem;
import negotiator.repository.ProfileRepItem;
import negotiator.tournament.VariablesAndValues.AgentParamValue;
import negotiator.tournament.VariablesAndValues.AgentParameterVariable;

/**
 * This is a modified version of the {@link AlternatingOffersProtocol}, where the starting agent is the only
 * agent doing the bidding, by using a modified session runner, called
 * {@link AlternatingOffersBilateralAtomicNegoSessionOneSidedBidding}.
 */
public class AlternatingOffersOneSidedBidding extends AlternatingOffersProtocol
{

	public AlternatingOffersOneSidedBidding(AgentRepItem[] agentRepItems, ProfileRepItem[] profileRepItems,
			HashMap<AgentParameterVariable, AgentParamValue>[] agentParams, int totalSessionRounds) throws Exception
	{
		super(agentRepItems, profileRepItems, agentParams, totalSessionRounds);
		System.out.println("Using AlternatingOffersOneSidedBidding");
	}
	
	@Override
	protected AlternatingOffersBilateralAtomicNegoSession newAlternatingOffersBilateralAtomicNegoSession() throws Exception
	{
		return new AlternatingOffersBilateralAtomicNegoSessionOneSidedBidding(this, 
				agentA, 
				agentB, 
				getAgentAname(),
				getAgentBname(),
				getAgentAUtilitySpace(), 
				getAgentBUtilitySpace(), 
				getAgentAparams(),
				getAgentBparams(),
				startingAgent);
	}

}
