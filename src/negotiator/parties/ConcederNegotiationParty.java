package negotiator.parties;

import negotiator.AgentID;
import negotiator.Deadline;
import negotiator.Timeline;
import negotiator.utility.UtilitySpace;

public class ConcederNegotiationParty extends
		AbstractTimeDependentNegotiationParty {

	public ConcederNegotiationParty(UtilitySpace utilitySpace,
			Deadline deadlines, Timeline timeline, long randomSeed) {
		super(utilitySpace, deadlines, timeline, randomSeed);
		partyId = new AgentID(String.format("Conceder#%4s", hashCode()));
	}

	@Override
	public double getE() {
		return 2;
	}
}
