package agents.nastyagent;

import negotiator.Deadline;
import negotiator.actions.Action;
import negotiator.session.Timeline;
import negotiator.utility.UtilitySpace;

public class SleepInReceiveMessage extends NastyAgent {

	public SleepInReceiveMessage(UtilitySpace utilitySpace, Deadline deadlines,
			Timeline timeline, long randomSeed) {
		super(utilitySpace, deadlines, timeline, randomSeed);
	}

	@Override
	public void receiveMessage(Object sender, Action arguments) {
		try {
			Thread.sleep(2000000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
