package agents;

import negotiator.PocketNegotiatorAgent;
import negotiator.Timeline;
import negotiator.actions.Action;
import negotiator.utility.UtilitySpace;

/**
 * @author W.Pasman simple agent to test PN interface.
 */
public class SimpleAgentPN extends SimpleAgent implements PocketNegotiatorAgent {

	@Override
	public void initPN(UtilitySpace mySide, UtilitySpace otherSide, Timeline tl) {
		utilitySpace = mySide;
		timeline = tl;

	}

	@Override
	public void updateProfiles(UtilitySpace my, UtilitySpace other) {
		if (my != null) {
			utilitySpace = my;
		}
	}

	@Override
	public void handleAction(Action act) {
		ReceiveMessage(act);
	}

	@Override
	public Action getAction() {
		return chooseAction();
	}

}
