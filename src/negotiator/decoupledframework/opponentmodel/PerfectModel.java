package negotiator.decoupledframework.opponentmodel;

import negotiator.Bid;
import negotiator.Global;
import negotiator.decoupledframework.OpponentModel;
import negotiator.protocol.BilateralAtomicNegotiationSession;

/**
 * The perfect opponent model. Note that for using this model
 * experimentalSetup should be enabled in global.
 * 
 * @author Mark Hendrikx (m.j.c.hendrikx@student.tudelft.nl)
 *
 */
public class PerfectModel extends OpponentModel {

	@Override
	public void setOpponentUtilitySpace(BilateralAtomicNegotiationSession session) {
		if (Global.experimentalSetup) {
			opponentUtilitySpace= session.getAgentAUtilitySpace();
			if (negotiationSession.getUtilitySpace().equals(opponentUtilitySpace)) {
				opponentUtilitySpace = session.getAgentBUtilitySpace();
			}
		} else {
			try {
				throw new Exception("Global.experimentalSetup should be enabled!");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}	
	}

	@Override
	public double getBidEvaluation(Bid bid) {
		try {
			return opponentUtilitySpace.getUtility(bid);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

	@Override
	public double getDiscountedBidEvaluation(Bid bid, double time) {
		try {
			return opponentUtilitySpace.getUtilityWithDiscount(bid, time);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

	public void updateModel(Bid opponentBid) { }
}
