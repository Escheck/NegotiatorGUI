package negotiator.protocol.auction;

import negotiator.Agent;
import negotiator.protocol.alternatingoffers.AlternatingOffersBilateralAtomicNegoSession;
import negotiator.utility.UtilitySpace;

public class AuctionBilateralAtomicNegoSession extends AlternatingOffersBilateralAtomicNegoSession {

	public AuctionBilateralAtomicNegoSession(Agent agentA, Agent agentB,
			UtilitySpace spaceA, UtilitySpace spaceB) throws Exception {
		super(agentA, agentB, spaceA, spaceB,1000);
		// TODO Auto-generated constructor stub
	}

}
