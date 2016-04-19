package agents.anac.y2013.InoxAgent;

import negotiator.SupportedNegotiationSetting;
import negotiator.boaframework.BOAagent;
import negotiator.boaframework.acceptanceconditions.anac2013.AC_InoxAgent;
import negotiator.boaframework.offeringstrategy.anac2013.InoxAgent_Offering;
import negotiator.boaframework.omstrategy.BestBid;
import negotiator.boaframework.opponentmodel.InoxAgent_OM;

public class InoxAgent extends BOAagent {

	@Override
	public void agentSetup() {
		opponentModel = new InoxAgent_OM(negotiationSession);
		opponentModel.init(negotiationSession);
		omStrategy = new BestBid(negotiationSession, opponentModel);
		offeringStrategy = new InoxAgent_Offering(negotiationSession,
				opponentModel, omStrategy);
		acceptConditions = new AC_InoxAgent(negotiationSession,
				offeringStrategy, opponentModel);
	}

	@Override
	public String getName() {
		return "InoxAgent";
	}

	@Override
	public SupportedNegotiationSetting getSupportedNegotiationSetting() {
		return SupportedNegotiationSetting.getLinearUtilitySpaceInstance();
	}
}