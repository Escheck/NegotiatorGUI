package agents;

import negotiator.PocketNegotiatorAgent;
import negotiator.SupportedNegotiationSetting;
import negotiator.actions.Action;
import negotiator.session.Timeline;
import negotiator.utility.UtilitySpace;

public class TimeDependentAgentConcederPN extends TimeDependentAgent implements
		PocketNegotiatorAgent {
	@Override
	public double getE() {
		return 2;
	}

	@Override
	public String getName() {
		return "Conceder";
	}

	@Override
	public SupportedNegotiationSetting getSupportedNegotiationSetting() {
		return SupportedNegotiationSetting.getLinearUtilitySpaceInstance();
	}

	/******* implements PocketNegotiatorAgent ***********/

	@Override
	public void initPN(UtilitySpace mySide, UtilitySpace otherSide, Timeline tl) {
		utilitySpace = mySide;
		timeline = tl;
		init();
	}

	@Override
	public void handleAction(Action act) {
		ReceiveMessage(act);
	}

	@Override
	public Action getAction() {
		return chooseAction();
	}

	@Override
	public void updateProfiles(UtilitySpace my, UtilitySpace other) {
		if (my != null) {
			utilitySpace = my;
			initFields();
		}
	}

}
