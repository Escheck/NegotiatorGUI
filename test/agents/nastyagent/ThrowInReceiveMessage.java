package agents.nastyagent;

import negotiator.actions.Action;

public class ThrowInReceiveMessage extends NastyAgent {

	@Override
	public void receiveMessage(Object sender, Action arguments) {
		throw new RuntimeException("bla");
	}
}
