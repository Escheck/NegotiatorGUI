package negotiator.parties;

import negotiator.AgentID;
import negotiator.Deadline;
import negotiator.session.Timeline;
import negotiator.utility.UtilitySpace;

public class ConcederNegotiationParty extends
		AbstractTimeDependentNegotiationParty {

	@Override
	public void init(UtilitySpace utilitySpace, Deadline deadlines,
			Timeline timeline, long randomSeed) {
		super.init(utilitySpace, deadlines, timeline, randomSeed);
		partyId = new AgentID(String.format("Conceder#%4s", hashCode()));
	}

	@Override
	public double getE() {
		return 2;
	}
}
