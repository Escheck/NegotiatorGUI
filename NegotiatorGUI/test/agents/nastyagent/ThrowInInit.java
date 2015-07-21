package agents.nastyagent;

import negotiator.Deadline;
import negotiator.session.Timeline;
import negotiator.utility.UtilitySpace;

public class ThrowInInit extends NastyAgent {

	public ThrowInInit(UtilitySpace utilitySpace, Deadline deadlines,
			Timeline timeline, long randomSeed) throws Throwable {
		super(utilitySpace, deadlines, timeline, randomSeed);
		throw new Throwable("blabla");
	}

}
