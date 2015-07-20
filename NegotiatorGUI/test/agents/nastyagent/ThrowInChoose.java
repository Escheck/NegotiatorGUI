package agents.nastyagent;

import java.util.List;

import negotiator.Deadline;
import negotiator.Timeline;
import negotiator.actions.Action;
import negotiator.utility.UtilitySpace;

public class ThrowInChoose extends NastyAgent {

	public ThrowInChoose(UtilitySpace utilitySpace, Deadline deadlines,
			Timeline timeline, long randomSeed) {
		super(utilitySpace, deadlines, timeline, randomSeed);
	}

	@Override
	public Action chooseAction(List<Class> possibleActions) {
		throw new RuntimeException("bla");
	}
}
