package agents;

import negotiator.PocketNegotiatorAgent;
import negotiator.Timeline;
import negotiator.actions.Action;
import negotiator.utility.UtilitySpace;

/**
 * @author W.Pasman PocketNegotiator compatible version of BayesianAgent.
 */
public class BayesianAgentPN extends BayesianAgent implements
		PocketNegotiatorAgent {

	@Override
	public void initPN(UtilitySpace mySide, UtilitySpace otherSide, Timeline tl) {
		utilitySpace = mySide;
		// FIXME set other side for use
		timeline = tl;
		init();

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
