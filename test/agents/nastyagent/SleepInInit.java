package agents.nastyagent;

import negotiator.AgentID;
import negotiator.Deadline;
import negotiator.session.TimeLineInfo;
import negotiator.utility.AbstractUtilitySpace;

public class SleepInInit extends NastyAgent {

	@Override
	public void init(AbstractUtilitySpace utilitySpace, Deadline deadlines,
			TimeLineInfo timeline, long randomSeed, AgentID id) {
		super.init(utilitySpace, deadlines, timeline, randomSeed, id);
		try {
			Thread.sleep(2000000);// sleep 2000 seconds.
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}
