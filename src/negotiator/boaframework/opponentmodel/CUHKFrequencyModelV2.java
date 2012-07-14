package negotiator.boaframework.opponentmodel;

import java.util.ArrayList;
import java.util.HashMap;
import negotiator.Bid;
import negotiator.boaframework.NegotiationSession;
import negotiator.boaframework.OpponentModel;
import negotiator.boaframework.opponentmodel.tools.UtilitySpaceAdapter;
import negotiator.issue.Issue;
import negotiator.issue.IssueDiscrete;
import negotiator.issue.Value;
import negotiator.utility.UtilitySpace;

/**
 * Heavily modified version of the ANAC2012 CUKHAgent opponent model.
 * This version is adapted to work with the BOA framework.
 * 
 * @author Mark Hendrikx
 */
public class CUHKFrequencyModelV2 extends OpponentModel {

    private ArrayList<Bid> bidHistory;
    private ArrayList<HashMap<Value, Integer>> opponentBidsStatisticsDiscrete;
    private HashMap<Integer, Integer> maxPreferencePerIssue;
    private int maximumBidsStored = 100;
    private ArrayList<Issue> issues;
    private int maxPossibleTotal = 0;
    private UtilitySpace cache = null;
	private boolean cached = false;
    
    /**
     * initialization
     */
    public void init(NegotiationSession negotiationSession, HashMap<String, Double> parameters) throws Exception {
    	this.bidHistory = new ArrayList<Bid>();
        opponentBidsStatisticsDiscrete = new ArrayList<HashMap<Value, Integer>>();
        maxPreferencePerIssue = new HashMap<Integer, Integer>();
        this.negotiationSession = negotiationSession;
        try {
            issues = negotiationSession.getUtilitySpace().getDomain().getIssues();

            for (int i = 0; i < issues.size(); i++) {
                IssueDiscrete lIssueDiscrete = (IssueDiscrete) issues.get(i);
                HashMap<Value, Integer> discreteIssueValuesMap = new HashMap<Value, Integer>();
                for (int j = 0; j < lIssueDiscrete.getNumberOfValues(); j++) {
                    Value v = lIssueDiscrete.getValue(j);
                    discreteIssueValuesMap.put(v, 0);
                }
                maxPreferencePerIssue.put(i, 0);
                opponentBidsStatisticsDiscrete.add(discreteIssueValuesMap);
            }
        } catch (Exception e) {
        	e.printStackTrace();
        }
    }

    /**
     * This function updates the opponent's Model by calling the
     * updateStatistics method
     */
    public void updateModel(Bid bid, double time) {
    	if (this.bidHistory.size() > this.maximumBidsStored) {
    		return;
    	}
    	if (bidHistory.indexOf(bid) == -1) {
    		this.bidHistory.add(bid);
        }
        if (this.bidHistory.size() <= this.maximumBidsStored) {
        	this.updateStatistics(bid);
        }
    }

    /**
     * This function updates the statistics of the bids that were received from
     * the opponent.
     */
    private void updateStatistics(Bid bidToUpdate) {
        try {
            //counters for each type of the issues
            int discreteIndex = 0;
            for (Issue lIssue : issues) {
                int issueNum = lIssue.getNumber();
                Value v = bidToUpdate.getValue(issueNum);
                if (opponentBidsStatisticsDiscrete == null) {
                    System.out.println("opponentBidsStatisticsDiscrete is NULL");
                } else if (opponentBidsStatisticsDiscrete.get(discreteIndex) != null) {
                    int counterPerValue = opponentBidsStatisticsDiscrete.get(discreteIndex).get(v);
                    counterPerValue++;
                    if (counterPerValue > maxPreferencePerIssue.get(discreteIndex)) {
                    	maxPreferencePerIssue.put(discreteIndex, counterPerValue);
                    	maxPossibleTotal++; // must be an increase by 1 one the total
                    }
                    opponentBidsStatisticsDiscrete.get(discreteIndex).put(v, counterPerValue);
                }
                discreteIndex++;
            }
        } catch (Exception e) {
            System.out.println("Exception in updateStatistics: " + e.getMessage());
        }
    }
    
    public double getBidEvaluation(Bid bid) {
        int discreteIndex = 0;
        int totalBidValue = 0;
        try {
            for (int j = 0; j < issues.size(); j++) {
                Value v = bid.getValue(issues.get(j).getNumber());
                if (opponentBidsStatisticsDiscrete == null) {
                    System.err.println("opponentBidsStatisticsDiscrete is NULL");
                } else if (opponentBidsStatisticsDiscrete.get(discreteIndex) != null) {
                    int counterPerValue = opponentBidsStatisticsDiscrete.get(discreteIndex).get(v);
                    totalBidValue += counterPerValue;
                }
                discreteIndex++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (totalBidValue == 0) {
        	return 0.0;
        }
        return (double) totalBidValue / (double) maxPossibleTotal;
    }
    
    public UtilitySpace getOpponentUtilitySpace() {
    	if (!cached && this.bidHistory.size() >= this.maximumBidsStored) {
    		cached  = true;
    		
    		cache = new UtilitySpaceAdapter(this, negotiationSession.getUtilitySpace().getDomain());
    	} else if (this.bidHistory.size() < this.maximumBidsStored) {
			return new UtilitySpaceAdapter(this, negotiationSession.getUtilitySpace().getDomain());
		}
    	return cache;
	}
    
    public double getWeight(Issue issue) {
		return (1.0 / (double) issues.size());
	}
    
    public String getName() {
		return "CUHK Frequency Model V2";
	}
}