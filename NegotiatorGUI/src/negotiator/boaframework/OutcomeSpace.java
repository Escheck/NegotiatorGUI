package negotiator.boaframework;

import java.util.ArrayList;
import java.util.List;
import misc.Range;
import negotiator.Bid;
import negotiator.BidIterator;
import negotiator.bidding.BidDetails;
import negotiator.utility.UtilitySpace;

/**
 * This class generates the complete outcome space and is therefore
 * Useful if someone wants to quickly implement an agent.
 * 
 * @author Alex Dirkzwager, Mark Hendrikx
 */

public class OutcomeSpace {
	
	protected UtilitySpace utilitySpace;
	protected List<BidDetails> allBids = new ArrayList<BidDetails>();
	
	public OutcomeSpace() { }
	
	public OutcomeSpace(UtilitySpace utilSpace){
		init(utilSpace);
	}
	
	public void init(UtilitySpace utilSpace) {
		generateAllBids(utilSpace);
		this.utilitySpace = utilSpace;
	}
	
	/**
	 * Generates all the possible bids in the domain
	 * @param utilSpace
	 */
	public void generateAllBids(UtilitySpace utilSpace) {
		
		BidIterator iter = new BidIterator(utilSpace.getDomain());
		while (iter.hasNext()) {
			Bid bid = iter.next();
			try {
				addPossibleBid(bid, utilSpace.getUtility(bid));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
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
	 * gets a list of bids (from possibleBids) that have a utility between the range
	 * @param range
	 * @return list of BidDetails
	 */
	public List<BidDetails> getBidsinDiscountedRange (Range r, double time){
		ArrayList<BidDetails> result = new ArrayList<BidDetails>();
		double upperbound = r.getUpperbound();
		double lowerbound = r.getLowerbound();
		

		for(BidDetails bid: allBids){
			if (utilitySpace.getUtilityWithDiscount(bid.getBid(), time) > lowerbound && utilitySpace.getUtilityWithDiscount(bid.getBid(), time) < upperbound){
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
	 * gets a BidDetails which is closest to the give utility
	 * @param utility
	 * @return BidDetails
	 */
	public BidDetails getBidNearDiscountedUtility(double utility, double time) {
		BidDetails closestBid = null;
		double closestDistance = 1;
		for(BidDetails bid : allBids){
			if(Math.abs(utilitySpace.getUtilityWithDiscount(bid.getBid(), time)-utility) < closestDistance) {
				closestBid = bid;
				closestDistance = Math.abs(bid.getMyUndiscountedUtil()-utility);
			}
		}
		return closestBid;
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
}
