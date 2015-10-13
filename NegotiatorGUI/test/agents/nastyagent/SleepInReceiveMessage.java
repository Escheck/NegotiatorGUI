package agents.nastyagent;

import negotiator.AgentID;
import negotiator.actions.Action;

public class SleepInReceiveMessage extends NastyAgent {

	@Override
	public void receiveMessage(AgentID sender, Action arguments) {
		try {
			Thread.sleep(2000000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
