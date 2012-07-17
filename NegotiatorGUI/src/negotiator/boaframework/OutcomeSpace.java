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
 * useful if someone wants to quickly implement an agent.
 * Note that while this outcomespace is faster upon initialization,
 * the sorted outcomespace class is faster during the negotiation.
 * 
 * @author Alex Dirkzwager, Mark Hendrikx
 */
public class OutcomeSpace {
	
	/** Reference to the utility space */
	protected UtilitySpace utilitySpace;
	/** List of all possible bids in the domain */
	protected List<BidDetails> allBids = new ArrayList<BidDetails>();
	
	/**
	 * Creates an unsorted outcome space.
	 * @param utilSpace
	 */
	public OutcomeSpace(UtilitySpace utilSpace) {
		this.utilitySpace = utilSpace;
		generateAllBids(utilSpace);
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
				BidDetails BidDetails = new BidDetails(bid, utilSpace.getUtility(bid), -1);
				allBids.add(BidDetails);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * @return list of all possible bids
	 */
	public List<BidDetails> getAllOutcomes(){
		return allBids;
	}
	
	
	/**
	 * gets a list of bids (from possibleBids) that have a utility within the
	 * given range.
	 * 
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
		return allBids.get(getIndexOfBidNearUtility(utility));
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
	 * @return best bid in the domain.
	 */
	public BidDetails getMaxBidPossible(){
		BidDetails maxBid = allBids.get(0);
		for(BidDetails bid : allBids){
			if(bid.getMyUndiscountedUtil() > maxBid.getMyUndiscountedUtil()) {
				maxBid = bid;
			}
		}
		return maxBid;
	}
	
	/**
	 * @return worst bid in the domain.
	 */
	public BidDetails getMinBidPossible(){
		BidDetails minBid = allBids.get(0);
		for(BidDetails bid : allBids){
			if(bid.getMyUndiscountedUtil() < minBid.getMyUndiscountedUtil()) {
				minBid = bid;
			}
		}
		return minBid;
	}

	/**
	 * @return index of the bid with the utility closest to the given utilty.
	 */
	public int getIndexOfBidNearUtility(double utility) {
		double closesDistance = 1;
		int best = 0;
		for(int i = 0; i < allBids.size(); i++){
			if(Math.abs(allBids.get(i).getMyUndiscountedUtil()-utility) < closesDistance) {
				closesDistance = Math.abs(allBids.get(i).getMyUndiscountedUtil()-utility);
				best = i;
			}
		}
		return best;
	}
}
