package negotiator.analysis;

import java.util.HashMap;
import negotiator.utility.UtilitySpace;

/**
 * Cache of analysis (Pareto frontier, etc.) of a @link{UtilitySpace}A -> @link{UtilitySpace}B -> BidSpace
 */
public class BidSpaceCache {
	
	private static HashMap<UtilitySpace,HashMap<UtilitySpace, BidSpace>> bidSpaceCache = new HashMap<UtilitySpace, HashMap<UtilitySpace,BidSpace>>();
	
	public static void addBidSpaceToCash(UtilitySpace spaceA, UtilitySpace spaceB, BidSpace bidSpace) {
		if (bidSpaceCache.get(spaceA)!=null) {
			if (bidSpaceCache.get(spaceA).get(spaceB)==null) {
				bidSpaceCache.get(spaceA).put(spaceB, bidSpace);
			}
		} else {
			HashMap<UtilitySpace, BidSpace> cashA = new HashMap<UtilitySpace, BidSpace>();
			cashA.put(spaceB, bidSpace);
			bidSpaceCache.put(spaceA, cashA);		
		}		
	}
	public static BidSpace getBidSpace(UtilitySpace spaceA, UtilitySpace spaceB) {	
		if (bidSpaceCache.get(spaceA)!=null)			
			return bidSpaceCache.get(spaceA).get(spaceB);
		else {
			return null;
		}
	}
	
	/**
	 * A bidspace which is added to the cache must ultimately be removed to free
	 * computational resources.
	 * 
	 * @param spaceA
	 * @param spaceB
	 */
	public static void removeBidSpace(UtilitySpace spaceA, UtilitySpace spaceB) {
		if (!bidSpaceCache.isEmpty()){
			if (bidSpaceCache.get(spaceA).get(spaceB) != null) {
				bidSpaceCache.get(spaceA).remove(spaceB);
			}
			if (bidSpaceCache.get(spaceA).size() == 0) {
				bidSpaceCache.remove(spaceA);
			}
		}
	}
	
	public static int getDomainsCount() {
		return bidSpaceCache.size();
	}
}