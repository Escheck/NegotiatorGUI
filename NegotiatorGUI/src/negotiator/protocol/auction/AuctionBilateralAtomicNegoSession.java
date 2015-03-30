package negotiator.protocol.auction;

import java.util.HashMap;

import negotiator.Agent;
import negotiator.protocol.OldProtocol;
import negotiator.protocol.alternatingoffers.AlternatingOffersBilateralAtomicNegoSession;
import negotiator.tournament.VariablesAndValues.AgentParamValue;
import negotiator.tournament.VariablesAndValues.AgentParameterVariable;
import negotiator.utility.UtilitySpace;

public class AuctionBilateralAtomicNegoSession extends AlternatingOffersBilateralAtomicNegoSession {

	public AuctionBilateralAtomicNegoSession(OldProtocol oldProtocol, Agent agentA,
			Agent agentB, String agentAname, String agentBname,
			UtilitySpace spaceA, UtilitySpace spaceB,
			HashMap<AgentParameterVariable, AgentParamValue> agentAparams,
			HashMap<AgentParameterVariable, AgentParamValue> agentBparams,
			String startingAgent, int totalTime) throws Exception {
		super(oldProtocol, agentA, agentB, agentAname, agentBname, spaceA, spaceB,
				agentAparams, agentBparams, startingAgent);
	}

}
