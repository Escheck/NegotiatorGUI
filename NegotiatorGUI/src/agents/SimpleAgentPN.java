package agents;

import negotiator.PocketNegotiatorAgent;
import negotiator.actions.Action;
import negotiator.utility.UtilitySpace;

/**
 * @author W.Pasman simple agent to test PN interface.
 */
public class SimpleAgentPN extends SimpleAgent implements PocketNegotiatorAgent {

	@Override
	public void initPN(UtilitySpace mySide, UtilitySpace otherSide) {
		utilitySpace = mySide;

	}

	@Override
	public void updateMyProfile(UtilitySpace us) {
		utilitySpace = us;
	}

	@Override
	public void updateOpponentProfile(UtilitySpace us) {
		// ignore. we don't have opponent utilityspace.
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
