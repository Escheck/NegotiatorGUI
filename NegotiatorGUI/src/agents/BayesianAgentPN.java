package agents;

import negotiator.PocketNegotiatorAgent;
import negotiator.actions.Action;
import negotiator.session.Timeline;
import negotiator.utility.AdditiveUtilitySpace;

/**
 * @author W.Pasman PocketNegotiator compatible version of BayesianAgent.
 */
public class BayesianAgentPN extends BayesianAgent implements
		PocketNegotiatorAgent {

	@Override
	public void initPN(AdditiveUtilitySpace mySide, AdditiveUtilitySpace otherSide, Timeline tl) {
		utilitySpace = mySide;
		// FIXME set other side for use
		timeline = tl;
		init();

	}

	@Override
	public void updateProfiles(AdditiveUtilitySpace my, AdditiveUtilitySpace other) {
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
