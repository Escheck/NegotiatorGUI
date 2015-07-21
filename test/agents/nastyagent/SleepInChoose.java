package agents.nastyagent;

import java.util.List;

import negotiator.actions.Action;

public class SleepInChoose extends NastyAgent {

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
