package agents;

import negotiator.Bid;
import negotiator.SupportedNegotiationSetting;

/**
 * This agent does not concede, but will accept anything equal to or above the
 * reservation value. For undiscounted domain only.
 */
public class UtilityBasedAcceptor extends TimeDependentAgent {
	@Override
	public double getE() {
		return 0;
	}

	@Override
	public String getName() {
		return "Utility Based Acceptor";
	}

	@Override
	public boolean isAcceptable(Bid plannedBid) {
		Bid opponentLastBid = getOpponentLastBid();
		if (getUtility(opponentLastBid) <= utilitySpace.getReservationValue()){
		
			if (Math.random()>= getUtility(opponentLastBid))
				return true;
			else return false;
		}
		return false;
	}

	@Override
	public SupportedNegotiationSetting getSupportedNegotiationSetting() {
		return SupportedNegotiationSetting.getLinearUtilitySpaceInstance();
	}
}
