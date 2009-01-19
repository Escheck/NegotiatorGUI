package negotiator.analysis;

import java.util.HashMap;

import negotiator.utility.UtilitySpace;

public class BidSpaceCash {
	private static HashMap<UtilitySpace,HashMap<UtilitySpace, BidSpace>> bidSpaceCash = new HashMap<UtilitySpace, HashMap<UtilitySpace,BidSpace>>();
	
	public static void addBidSpaceToCash(UtilitySpace spaceA, UtilitySpace spaceB, BidSpace bidSpace) {
		HashMap<UtilitySpace, BidSpace> cashA = new HashMap<UtilitySpace, BidSpace>();
		HashMap<UtilitySpace, BidSpace> cashB = new HashMap<UtilitySpace, BidSpace>();
		cashA.put(spaceA, bidSpace);		
		cashB.put(spaceB, bidSpace);
		bidSpaceCash.put(spaceA, cashB);		 
		bidSpaceCash.put(spaceB, cashA);
	}
	public static BidSpace getBidSpace(UtilitySpace spaceA, UtilitySpace spaceB) {		
		if(bidSpaceCash.get(spaceA)!=null)			
			return bidSpaceCash.get(spaceA).get(spaceB);
		else return null;
	}
	

}
