package negotiator.decoupledframework;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import misc.Pair;
import misc.Range;
import negotiator.Bid;
import negotiator.bidding.BidDetails;
import negotiator.issue.Issue;
import negotiator.issue.IssueDiscrete;
import negotiator.issue.Value;
import negotiator.issue.ValueDiscrete;
import negotiator.utility.UtilitySpace;

/**
 * This class generates the complete outcome space and is therefore
 * Useful if someone wants to quickly implement an agent.
 * 
 * @author Alex Dirkzwager, Mark Hendrikx
 */

public class OutcomeSpace implements Cloneable {
	
	protected List<BidDetails> allBids = new ArrayList<BidDetails>();
	
	public OutcomeSpace() { }
	
	public OutcomeSpace(UtilitySpace utilSpace){
		init(utilSpace);
	}
	
	public void init(UtilitySpace utilSpace) {
		generateAllBids(utilSpace);
	}
	
	/**
	 * Generates all the possible bids in the domain
	 * @param utilSpace
	 */
	public void generateAllBids(UtilitySpace utilSpace) {
		ArrayList<Issue> issues = utilSpace.getDomain().getIssues();
		ArrayList<IssueDiscrete> discreteIssues = new ArrayList<IssueDiscrete>();
		
		for (Issue issue:issues) {
			discreteIssues.add((IssueDiscrete)issue);
		}
		
		ArrayList<ArrayList<Pair<Integer, ValueDiscrete>>> result = generateAllBids(discreteIssues, 0);
		
		for (ArrayList<Pair<Integer, ValueDiscrete>> bidSet : result) {
			HashMap<Integer, Value> values = new HashMap<Integer, Value>();
			for (Pair<Integer, ValueDiscrete> pair : bidSet) {
				values.put(pair.getFirst(), pair.getSecond());
			}
			try {
				Bid bid = new Bid(utilSpace.getDomain(), values);
				double utility = utilSpace.getUtility(bid);
				addPossibleBid(bid, utility);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Is used to generate a list of all possible bids (not in BidDetails form)
	 * @param issueList
	 * @param i
	 * @return ArrayList
	 */
	private ArrayList<ArrayList<Pair<Integer, ValueDiscrete>>> generateAllBids(ArrayList<IssueDiscrete> issueList, int i) {
		
		// stop condition
		if(i == issueList.size()) {
			// return a list with an empty list
			ArrayList<ArrayList<Pair<Integer, ValueDiscrete>>> result = new ArrayList<ArrayList<Pair<Integer, ValueDiscrete>>>();
			result.add(new ArrayList<Pair<Integer, ValueDiscrete>>());
			return result;
		}
		
		ArrayList<ArrayList<Pair<Integer, ValueDiscrete>>> result = new ArrayList<ArrayList<Pair<Integer, ValueDiscrete>>>();
		ArrayList<ArrayList<Pair<Integer, ValueDiscrete>>> recursive = generateAllBids(issueList, i+1); // recursive call
		
		// for each element of the first list of input
		for(int j = 0; j < issueList.get(i).getValues().size(); j++) {
			// add the element to all combinations obtained for the rest of the lists
			for(int k = 0; k < recursive.size(); k++) {
	                        // copy a combination from recursive
				ArrayList<Pair<Integer, ValueDiscrete>> newList = new ArrayList<Pair<Integer, ValueDiscrete>>();
				for(Pair<Integer, ValueDiscrete> set : recursive.get(k)) {
					newList.add(set);
				}
				// add element of the first list
				ValueDiscrete value = issueList.get(i).getValues().get(j);
				int issueNr = issueList.get(i).getNumber();
				newList.add(new Pair<Integer, ValueDiscrete>(issueNr, value));
				
				// add new combination to result
				result.add(newList);
			}
		}
		return result;
	}
	
	/**
	 * 
	 * @return list of all possible bids
	 */
	public List<BidDetails> getAllOutcomes(){
		return allBids;
	}
	
	
	/**
	 * gets a list of bids (from possibleBids) that have a utility between the range
	 * @param range
	 * @return list of BidDetails
	 */
	public List<BidDetails> getBidsinRange (Range r){
		ArrayList<BidDetails> result = new ArrayList<BidDetails>();
		double upperbound = r.getUpperbound();
		double lowerbound = r.getLowerbound();
		
		for(BidDetails bid: allBids){
			if (bid.getMyUndiscountedUtil() > lowerbound && bid.getMyUndiscountedUtil() < upperbound){
				result.add(bid);
			}
		}
		return result;
	}
	
	/**
	 * gets a BidDetails which is closest to the give utility
	 * @param utility
	 * @return BidDetails
	 */
	public BidDetails getBidNearUtility(double utility) {
		BidDetails closesBid = null;
		double closesDistance = 1;
		for(BidDetails bid : allBids){
			if(Math.abs(bid.getMyUndiscountedUtil()-utility) < closesDistance) {
				closesBid = bid;
				closesDistance = Math.abs(bid.getMyUndiscountedUtil()-utility);
			}
		}
		return closesBid;
	}
	

		
	
	/**
	 * adds a possible bid to the allBids list
	 * @param bid
	 * @param utility 
	 */
	protected void addPossibleBid(Bid bid, double utility){
		BidDetails BidDetails = new BidDetails(bid, utility);
		allBids.add(BidDetails);
	}
	
	
	public BidDetails getMaxBidPossible(){
		BidDetails maxBid = allBids.get(0);
		for(BidDetails bid : allBids){
			if(bid.getMyUndiscountedUtil() > maxBid.getMyUndiscountedUtil()) {
				maxBid = bid;
			}
		}
		return maxBid;
	}

	public OutcomeSpace clone() {
		try {
			OutcomeSpace clone = (OutcomeSpace) super.clone();
			clone.allBids = this.allBids;
			return clone;
		} catch (CloneNotSupportedException e) {
			throw new RuntimeException("this could never happen", e);
		}
	}
	
	public OutcomeSpace reset() {
		allBids = new ArrayList<BidDetails>();
		return this;
	}
}
