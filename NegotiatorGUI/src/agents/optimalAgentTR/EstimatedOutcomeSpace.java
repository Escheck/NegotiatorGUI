package agents.optimalAgentTR;

import java.util.ArrayList;

import negotiator.Bid;
import negotiator.BidIterator;
import negotiator.utility.UtilitySpace;

public class EstimatedOutcomeSpace {

	private ArrayList<BidInfo>  estimatedOutcomeSpace;
	
	public EstimatedOutcomeSpace (UtilitySpace myUtilSpace) {
		this.estimatedOutcomeSpace=new ArrayList<BidInfo>();
		generateAllBids(myUtilSpace);
	}
	
	
	public EstimatedOutcomeSpace (UtilitySpace myUtilSpace, UtilitySpace oppUtilSpace) {
		this.estimatedOutcomeSpace=new ArrayList<BidInfo>();
		generateAllBids(myUtilSpace,oppUtilSpace);
	}
	
	
	public void generateAllBids(UtilitySpace utilSpace) {
		
		BidIterator iter = new BidIterator(utilSpace.getDomain());
		while (iter.hasNext()) {
			Bid bid = iter.next();
			try {
				BidInfo bidInformation = new BidInfo(bid, utilSpace.getUtility(bid), 1-utilSpace.getUtility(bid));
				estimatedOutcomeSpace.add(bidInformation);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public void generateAllBids(UtilitySpace utilSpace, UtilitySpace oppUtilSpace) {
		
		BidIterator iter = new BidIterator(utilSpace.getDomain());
		while (iter.hasNext()) {
			Bid bid = iter.next();
			try {
				BidInfo bidInformation = new BidInfo(bid, utilSpace.getUtility(bid), oppUtilSpace.getUtility(bid));
				estimatedOutcomeSpace.add(bidInformation);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public ArrayList<BidInfo> getEstimatedOutcomeSpace(){
		return this.estimatedOutcomeSpace;
	}

	public BidInfo getBidInfo(int index) {
		return this.estimatedOutcomeSpace.get(index);
	}
}

