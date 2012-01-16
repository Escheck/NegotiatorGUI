package negotiator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;


/**
 * This class contains the history of a negotiation agent.
 * 
 * @author Alex Dirkzwager, Tim Baarslag
 */
public class BidHistory {
	
	private List<BidDetails> bidList;
	
	public BidHistory(ArrayList<BidDetails> bids) {
		bidList =  bids;
	}
	
	public BidHistory() {
		bidList = new ArrayList<BidDetails>();
	}
	
	public BidHistory filterBetweenTime(double minT, double maxT)
	{
		return filterBetween(0, 1, minT, maxT);		
	}

	public BidHistory filterBetween(double minU, double maxU, double minT, double maxT)
	{
		BidHistory bidHistory = new BidHistory();
		for (BidDetails b : bidList)
		{
			if (minU < b.getMyUndiscountedUtil() &&
					b.getMyUndiscountedUtil() <= maxU &&
					minT < b.getTime() &&
					b.getTime() <= maxT)
				bidHistory.add(b);
		}
		return bidHistory;			
	}
	
	public void add(BidDetails bid){
		bidList.add(bid);
	}

	public List<BidDetails> getHistory() {
		return bidList;
	}
	
	public BidDetails getLastBidDetails(){
		BidDetails bid = null;
		if (bidList.size() > 0) {
			bid = bidList.get(bidList.size() - 1);
		}
		return bid;
	}
	
	public BidDetails getFirstBidDetails() {
		return bidList.get(0);
	}
	
	
	public BidDetails getBestBidDetails(){
		List<BidDetails> sortedOpponentBids = new ArrayList<BidDetails>(bidList);
		Collections.sort(sortedOpponentBids, new BidDetailsSorter());
		return sortedOpponentBids.get(0);
	}
	
	public BidDetails getWorstBidDetails(){
		ArrayList<BidDetails> sortedOpponentBids = new ArrayList<BidDetails>(bidList);
		Collections.sort(sortedOpponentBids,new BidDetailsSorter());
		return sortedOpponentBids.get(bidList.size()-1);
	}
	
	/**
	 * gives a list of the top N bids which the opponent has offered
	 * @param count
	 * @return a list of UTBids
	 */
	public List<BidDetails> getNBestBids(int count){
		List<BidDetails> result = new ArrayList<BidDetails>();
		List<BidDetails> sortedOpponentBids = new ArrayList<BidDetails>(bidList);
		Collections.sort(sortedOpponentBids, new BidDetailsSorter());
		for(int i = 0; i < count && i < sortedOpponentBids.size(); i++){
			result.add(sortedOpponentBids.get(i));
		}
		
		return result;
	}
	
	public int size(){
		return bidList.size();
	}
	
	public double getAverageUtility(){
		int size = size();
		if (size == 0)
			return 0;
		double totalUtil = 0;
		for(BidDetails bid : bidList){
			totalUtil =+bid.getMyUndiscountedUtil();
		}
		return totalUtil / size;
	}
	
	/**
	 * Get the {@link BidDetails} of the {@link Bid} with utility closest to u.
	 */
	public BidDetails getBidDetailsOfUtility(double u)
	{
		double minDistance = -1;
		BidDetails closestBid = null;
		for (BidDetails b : bidList)
		{
			double utility = b.getMyUndiscountedUtil();
			if (Math.abs(utility - u) <= minDistance || minDistance == -1)
			{
				minDistance = Math.abs(utility - u);
				closestBid = b;
			}
		}
		return closestBid;
	}
	
	public BidHistory sortToUtility(){
		BidHistory sortedHistory = this;
		Collections.sort(sortedHistory.getHistory(), new BidDetailsSorter());
		return sortedHistory;
	}
	
	public BidHistory sortToTime(){
		BidHistory sortedHistory = this;
		Collections.sort(sortedHistory.getHistory(), new BidDetailSorterTime());
		return sortedHistory;
	}
	
	public BidDetails getRandom()
	{
		int size = size();
		if (size == 0)
			return null;
		int index = (new Random()).nextInt(size);
		return bidList.get(index);
	}
}