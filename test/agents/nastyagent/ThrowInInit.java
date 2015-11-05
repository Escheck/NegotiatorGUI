package agents.nastyagent;

import negotiator.AgentID;
import negotiator.Deadline;
import negotiator.session.TimeLineInfo;
import negotiator.utility.AbstractUtilitySpace;

public class ThrowInInit extends NastyAgent {

	@Override
	public void init(AbstractUtilitySpace utilitySpace, Deadline deadlines,
			TimeLineInfo timeline, long randomSeed, AgentID id) {
		super.init(utilitySpace, deadlines, timeline, randomSeed, id);
		throw new RuntimeException("just throwing in init for fun");
	}

}
