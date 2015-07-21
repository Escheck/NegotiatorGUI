package agents.nastyagent;

import negotiator.Deadline;
import negotiator.session.Timeline;
import negotiator.utility.UtilitySpace;

public class ThrowInInit extends NastyAgent {

	@Override
	public void init(UtilitySpace utilitySpace, Deadline deadlines,
			Timeline timeline, long randomSeed) {
		super.init(utilitySpace, deadlines, timeline, randomSeed);
		throw new RuntimeException("just throwing in init for fun");
	}

}
