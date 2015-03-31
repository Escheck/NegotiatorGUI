package negotiator.parties;

import negotiator.AgentID;
import negotiator.Deadline;
import negotiator.Timeline;
import negotiator.utility.UtilitySpace;

public class BoulwareNegotiationParty extends
		AbstractTimeDependentNegotiationParty {

	public BoulwareNegotiationParty(UtilitySpace utilitySpace,
			Deadline deadlines, Timeline timeline, long randomSeed) {
		super(utilitySpace, deadlines, timeline, randomSeed);
		partyId = new AgentID(String.format("Boulware#%4s", hashCode()));
	}

	@Override
	public double getE() {
		return 0.2;
	}
}
