package agents.anac.y2013.TheFawkes;

import negotiator.SupportedNegotiationSetting;
import negotiator.boaframework.BOAagent;
import negotiator.boaframework.acceptanceconditions.anac2013.AC_TheFawkes;
import negotiator.boaframework.offeringstrategy.anac2013.Fawkes_Offering;
import negotiator.boaframework.omstrategy.TheFawkes_OMS;
import negotiator.boaframework.opponentmodel.TheFawkes_OM;

public class TheFawkes extends BOAagent {

	@Override
	public void agentSetup() {
		opponentModel = new TheFawkes_OM();
		opponentModel.init(negotiationSession);
		omStrategy = new TheFawkes_OMS(negotiationSession, opponentModel);
		offeringStrategy = new Fawkes_Offering();
		acceptConditions = new AC_TheFawkes();
		try {
			offeringStrategy.init(negotiationSession, opponentModel,
					omStrategy, null);
			acceptConditions.init(negotiationSession, offeringStrategy,
					opponentModel, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public String getName() {
		return "TheFawkes";
	}

	@Override
	public SupportedNegotiationSetting getSupportedNegotiationSetting() {
		return SupportedNegotiationSetting.getLinearUtilitySpaceInstance();
	}
}