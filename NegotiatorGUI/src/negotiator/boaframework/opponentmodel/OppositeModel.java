package negotiator.boaframework.opponentmodel;

import java.util.HashMap;
import negotiator.Bid;
import negotiator.boaframework.NegotiationSession;
import negotiator.boaframework.OpponentModel;
import negotiator.boaframework.opponentmodel.tools.UtilitySpaceAdapter;
import negotiator.issue.Issue;

/**
 * Simple baseline opponent model which just mirror's the utility space of the agent.
 * Note that the model does not measure the issue weights, it makes no sense.
 * 
 * @author Mark Hendrikx
 */
public class OppositeModel extends OpponentModel {

	@Override
	public void init(NegotiationSession negotiationSession, HashMap<String, Double> parameters) throws Exception {
		this.negotiationSession = negotiationSession;
		this.opponentUtilitySpace = new UtilitySpaceAdapter(this, negotiationSession.getUtilitySpace().getDomain());
	}
	
	@Override
	public void updateModel(Bid bid, double time) { }

	@Override
	public double getBidEvaluation(Bid bid) {
		try {
			return (1.0 - negotiationSession.getUtilitySpace().getUtility(bid));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0.0;
	}
	
	//
	public double getWeight(Issue issue) {
		return negotiationSession.getUtilitySpace().getWeight(issue.getNumber());
	}
	
	@Override
	public String getName() {
		return "Opposite Model";
	}
}