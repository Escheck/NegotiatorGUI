package agents.nastyagent;

import negotiator.Deadline;
import negotiator.session.Timeline;
import negotiator.utility.UtilitySpace;

public class BadSuperInit extends NastyAgent {

	public BadSuperInit(UtilitySpace utilitySpace, Deadline deadlines,
			Timeline timeline, long randomSeed) throws InterruptedException {
		super(null, null, null, 0);
	}

}
