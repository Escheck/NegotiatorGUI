package agents.nastyagent;

import negotiator.Deadline;
import negotiator.Timeline;
import negotiator.actions.Action;
import negotiator.utility.UtilitySpace;

public class ThrowInReceiveMessage extends NastyAgent {

	public ThrowInReceiveMessage(UtilitySpace utilitySpace, Deadline deadlines,
			Timeline timeline, long randomSeed) {
		super(utilitySpace, deadlines, timeline, randomSeed);
	}

	@Override
	public void receiveMessage(Object sender, Action arguments) {
		throw new RuntimeException("bla");
	}
}
