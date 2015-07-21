package agents.nastyagent;

import negotiator.actions.Action;

public class SleepInReceiveMessage extends NastyAgent {

	@Override
	public void receiveMessage(Object sender, Action arguments) {
		try {
			Thread.sleep(2000000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
