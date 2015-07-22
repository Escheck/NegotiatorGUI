package negotiator.parties;

import negotiator.AgentID;
import negotiator.Deadline;
import negotiator.session.Timeline;
import negotiator.utility.UtilitySpace;

public class TracerParty extends NonDeterministicConcederNegotiationParty {

	@Override
	public void init(UtilitySpace utilitySpace, Deadline deadlines,
			Timeline timeline, long randomSeed, AgentID id) {
		super.init(utilitySpace, deadlines, timeline, randomSeed, id);

	}

	@Override
	public double getTargetUtility() {
		double t = super.getTargetUtility();
		System.out.println(t);
		return t;
	}
}
