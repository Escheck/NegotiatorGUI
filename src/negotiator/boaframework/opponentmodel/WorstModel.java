package negotiator.boaframework.opponentmodel;

import negotiator.Bid;
import negotiator.Global;
import negotiator.boaframework.OpponentModel;
import negotiator.boaframework.opponentmodel.tools.UtilitySpaceAdapter;
import negotiator.protocol.BilateralAtomicNegotiationSession;
import negotiator.utility.UtilitySpace;

/**
 * The theoretically worst opponent model. Note that for using this model
 * experimentalSetup should be enabled in global.
 * 
 * Tim Baarslag, Koen Hindriks, Mark Hendrikx, Alex Dirkzwager and Catholijn M. Jonker.
 * Decoupling Negotiating Agents to Explore the Space of Negotiation Strategies
 * 
 * @author Mark Hendrikx
 */
public class WorstModel extends OpponentModel {

	private UtilitySpaceAdapter worstUtilitySpace;

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
		this.worstUtilitySpace = new UtilitySpaceAdapter(this, opponentUtilitySpace.getDomain());
	}
	
	@Override
	public void setOpponentUtilitySpace(UtilitySpace opponentUtilitySpace) {
		this.opponentUtilitySpace = opponentUtilitySpace;
		this.worstUtilitySpace = new UtilitySpaceAdapter(this, opponentUtilitySpace.getDomain());
	}

	@Override
	public double getBidEvaluation(Bid bid) {
		try {
			return 1.0 - opponentUtilitySpace.getUtility(bid);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

	@Override
	public String getName() {
		return "Worst Model";
	}
	
	public UtilitySpace getOpponentUtilitySpace(){
		return worstUtilitySpace;
	}
	
	public void updateModel(Bid opponentBid, double time) { }
}