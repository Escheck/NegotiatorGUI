package negotiator.parties;

import negotiator.AgentID;
import negotiator.Deadline;
import negotiator.session.TimeLineInfo;
import negotiator.utility.AbstractUtilitySpace;

public class TracerParty extends NonDeterministicConcederNegotiationParty {

	@Override
	public void init(AbstractUtilitySpace utilitySpace, Deadline deadlines,
			TimeLineInfo timeline, long randomSeed, AgentID id) {
		super.init(utilitySpace, deadlines, timeline, randomSeed, id);

	}

	@Override
	public double getTargetUtility() {
		double t = super.getTargetUtility();
		System.out.println(t);
		return t;
	}
}
