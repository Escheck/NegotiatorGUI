package agents.nastyagent;

import java.util.List;

import negotiator.Deadline;
import negotiator.Timeline;
import negotiator.actions.Action;
import negotiator.utility.UtilitySpace;

public class SleepInChoose extends NastyAgent {

	public SleepInChoose(UtilitySpace utilitySpace, Deadline deadlines,
			Timeline timeline, long randomSeed) {
		super(utilitySpace, deadlines, timeline, randomSeed);
	}

	@Override
	public Action chooseAction(List<Class> possibleActions) {
		try {
			Thread.sleep(2000000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return null;
	}
}
