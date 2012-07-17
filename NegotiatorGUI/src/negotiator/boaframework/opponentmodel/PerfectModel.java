package negotiator.boaframework.opponentmodel;

import negotiator.Bid;
import negotiator.Global;
import negotiator.boaframework.OpponentModel;
import negotiator.protocol.BilateralAtomicNegotiationSession;
import negotiator.utility.UtilitySpace;

/**
 * An opponent model symbolizing perfect knowledge about the opponent's preferences.
 * Note that for using this model experimentalSetup should be enabled in global.
 * 
 * @author Mark Hendrikx
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