package negotiator.boaframework.opponentmodel.agentsmithv2;

import java.util.ArrayList;
import java.util.HashMap;

import agents.bayesianopponentmodel.OpponentModel;

import negotiator.Bid;
import negotiator.issue.Issue;
import negotiator.utility.UtilitySpace;

/**
 * The OpponentModel. This model manages the opponents preferences, stores the bids and based
 * on the number of times the opponent proposes a specific option in a bid, that option becomes
 * more important and our agent uses this information to construct its own bids. 
 */
public class SmithModelV2 extends OpponentModel
{
	private HashMap<Issue, IssueModel> fIssueModels;
	private ArrayList<Issue> lIssues;
	private HashMap<Issue, Double> weights;
	
	/**
	 *  Constructor
	 */
	public SmithModelV2(UtilitySpace space)
	{
		fIssueModels = new HashMap<Issue, IssueModel>();
		fDomain = space.getDomain();
		lIssues = space.getDomain().getIssues();
		initIssueModels();	
	}
	
	/**
	 * For each of the issues it initializes a model which stores the opponents' preferences
	 */
	private void initIssueModels()
	{
		for(Issue lIssue:lIssues) {
			IssueModel lModel;
			lModel = new IssueModel(lIssue);	
			fIssueModels.put(lIssue, lModel);
		}
	}

	/**
	 * Adds the values of each issue of a bid to the preferenceprofilemanager
	 */
	public void addBid(Bid pBid) {
		for(Issue lIssue:lIssues) {
       		fIssueModels.get(lIssue).addValue(pBid);
       	}
		weights = getWeights();
	}
	
	
	/**
	 * Returns a hashmap with the weights for each of the issues
	 */
	private HashMap<Issue, Double> getWeights() {
		HashMap<Issue, Double> lWeights = new HashMap<Issue, Double>();

		for(Issue lIssue:lIssues) {
			double weight = fIssueModels.get(lIssue).getWeight();
			lWeights.put(lIssue, weight);
		}
		
		double lTotal = 0;
		for(Issue lIssue:lIssues)
			lTotal += lWeights.get(lIssue);
		
		for(Issue lIssue:lIssues) {
			lWeights.put(lIssue, lWeights.get(lIssue) / lTotal);
		}
		return lWeights;
	}
	
	public double getWeight(int issueNr) {
		Issue foundIssue = null;
		for (Issue issue : lIssues) {
			if (issue.getNumber() == issueNr) {
				foundIssue = issue;
				break;
			}
		}
		return weights.get(foundIssue);
	}
	
	/**
	 * Returns the utility of a bid, but instead of the normal utility it is
	 * based on the weights of each issues
	 */
	public double getNormalizedUtility(Bid pBid) {
		double lUtility = 0;
		
		for(Issue lIssue:lIssues) {
			double lWeight = weights.get(lIssue); 
			
			double lLocalUtility = fIssueModels.get(lIssue).getUtility(pBid); 	
			lUtility += lWeight * lLocalUtility;
		}
		return lUtility;
	}
}