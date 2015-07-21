package agents.nastyagent;

import negotiator.Deadline;
import negotiator.session.Timeline;
import negotiator.utility.UtilitySpace;

public class SleepInInit extends NastyAgent {

	@Override
	public void init(UtilitySpace utilitySpace, Deadline deadlines,
			Timeline timeline, long randomSeed) {
		super.init(utilitySpace, deadlines, timeline, randomSeed);
		try {
			Thread.sleep(2000000);// sleep 2000 seconds.
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}
