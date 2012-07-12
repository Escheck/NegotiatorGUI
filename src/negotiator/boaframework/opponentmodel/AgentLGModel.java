package negotiator.boaframework.opponentmodel;

import java.util.ArrayList;
import java.util.HashMap;
import negotiator.Bid;
import negotiator.boaframework.NegotiationSession;
import negotiator.boaframework.OpponentModel;
import negotiator.boaframework.opponentmodel.agentlg.BidStatistic;
import negotiator.boaframework.opponentmodel.tools.UtilitySpaceAdapter;
import negotiator.issue.Issue;
import negotiator.issue.Value;
import negotiator.utility.UtilitySpace;

public class AgentLGModel extends OpponentModel {
	
	private HashMap<Issue,BidStatistic> statistic = new HashMap<Issue,BidStatistic>();
	private ArrayList<Issue> issues;
	
	public void init(NegotiationSession negotiationSession, HashMap<String, Double> parameters) throws Exception {
		this.negotiationSession = negotiationSession;
		issues = negotiationSession.getUtilitySpace().getDomain().getIssues();
		for (Issue issue : issues) {
			statistic.put(issue,new BidStatistic(issue));
		}
	}
	
	@Override
	public void updateModel(Bid opponentBid, double time) {	
		try
		{
			//updates statistics
			for (Issue issue : statistic.keySet()) {
				Value v = opponentBid.getValue(issue.getNumber());
				statistic.get(issue).add(v);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	/**
	 * returns opponent bid utility that calculated from the vote statistics.
	 *
	 */
	public double getBidEvaluation(Bid bid) {
		double ret=0;
		for (Issue issue : issues) {
			try {
				ret+= statistic.get(issue).getValueUtility(bid.getValue(issue.getNumber()));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return (ret / issues.size());
	}
	
	public double getWeight(Issue issue) {
		return (1.0 / issues.size());
	}
	
	@Override
	public UtilitySpace getOpponentUtilitySpace() {
		return new UtilitySpaceAdapter(this, negotiationSession.getUtilitySpace().getDomain());
	}
	
	public void cleanUp() {
		super.cleanUp();
	}
	
	@Override
	public String getName() {
		return "AgentLG Model";
	}
}