package negotiator.boaframework.opponentmodel;

import negotiator.Bid;
import negotiator.Global;
import negotiator.boaframework.OpponentModel;
import negotiator.protocol.BilateralAtomicNegotiationSession;
import negotiator.utility.UtilitySpace;

/**
 * The perfect opponent model. Note that for using this model
 * experimentalSetup should be enabled in global.
 * 
 * @author Mark Hendrikx (m.j.c.hendrikx@student.tudelft.nl)
 */
public class PerfectModel extends OpponentModel {

	@Override
	public void setOpponentUtilitySpace(BilateralAtomicNegotiationSession session) {
		
		if (Global.experimentalSetup) {
			opponentUtilitySpace = session.getAgentAUtilitySpace();
			if (negotiationSession.getUtilitySpace().getFileName().equals(opponentUtilitySpace.getFileName())) {
				opponentUtilitySpace = session.getAgentBUtilitySpace();
			}
		} else {
			System.err.println("Global.experimentalSetup should be enabled!");
		}	
	}
	
	@Override
	public void setOpponentUtilitySpace(UtilitySpace opponentUtilitySpace) {
		this.opponentUtilitySpace = opponentUtilitySpace;
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
	public String getName() {
		return "Perfect Model";
	}
	
	public void updateModel(Bid opponentBid, double time) { }
}