package agents.nastyagent;

import negotiator.Deadline;
import negotiator.session.Timeline;
import negotiator.utility.UtilitySpace;

public class SleepInInit extends NastyAgent {

	public SleepInInit(UtilitySpace utilitySpace, Deadline deadlines,
			Timeline timeline, long randomSeed) throws InterruptedException {
		super(utilitySpace, deadlines, timeline, randomSeed);
		Thread.sleep(2000000);// sleep 2000 seconds. Should screw up every nego.
	}

}
