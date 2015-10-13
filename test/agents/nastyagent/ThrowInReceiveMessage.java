package agents.nastyagent;

import negotiator.AgentID;
import negotiator.actions.Action;

public class ThrowInReceiveMessage extends NastyAgent {

	@Override
	public void receiveMessage(AgentID sender, Action arguments) {
		throw new RuntimeException("bla");
	}
}
