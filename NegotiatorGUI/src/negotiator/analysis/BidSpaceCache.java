package negotiator.analysis;

import java.util.HashMap;
import negotiator.utility.UtilitySpace;

/**
 * Cache used to store one or multiple bidspaces. The idea is that calculating a bidspace
 * is an expensive activity and therefore instead of recalculating a bidspace it should 
 * be stored in memory.
 */
public class BidSpaceCache {
	
	private static HashMap<UtilitySpace,HashMap<UtilitySpace, BidSpace>> bidSpaceCache = new HashMap<UtilitySpace, HashMap<UtilitySpace,BidSpace>>();
	
	/**
	 * Add a utilityspace to the cache. Do not forget to remove the
	 * space when it is no longer needed.
	 * 
	 * @param utilityspaceA the utilityspace of agent A.
	 * @param utilityspaceB the utilityspace of agent B.
	 * @param bidSpace to be stored in the hashmap.
	 */
	public static void addBidSpaceToCash(UtilitySpace utilityspaceA, UtilitySpace utilityspaceB, BidSpace bidSpace) {
		if (bidSpaceCache.get(utilityspaceA)!=null) {
			if (bidSpaceCache.get(utilityspaceA).get(utilityspaceB)==null) {
				bidSpaceCache.get(utilityspaceA).put(utilityspaceB, bidSpace);
			} else {
				System.out.println(	"This space is already cached. " +
									"This means that you are redundantly calculating a " +
									"utilityspace twice.");
			}
		} else {
			HashMap<UtilitySpace, BidSpace> cashA = new HashMap<UtilitySpace, BidSpace>();
			cashA.put(utilityspaceB, bidSpace);
			bidSpaceCache.put(utilityspaceA, cashA);		
		}		
	}
	
	/**
	 * Returns a cached BidSpace if it is available.
	 * 
	 * @param utilityspaceA of the agent A.
	 * @param utilityspaceB of the agent B.
	 * @return a cached BidSpace if it was available, else null.
	 */
	public static BidSpace getBidSpace(UtilitySpace utilityspaceA, UtilitySpace utilityspaceB) {	
		if (bidSpaceCache.get(utilityspaceA)!=null)			
			return bidSpaceCache.get(utilityspaceA).get(utilityspaceB);
		else {
			return null;
		}
	}
	
	/**
	 * A utilityspace which is added to the cache must ultimately be removed to free
	 * computational resources.
	 * 
	 * @param utilityspaceA of the agent A.
	 * @param utilityspaceB of the agent B.
	 */
	public static void removeBidSpace(UtilitySpace utilityspaceA, UtilitySpace utilityspaceB) {
		if (!bidSpaceCache.isEmpty()){
			bidSpaceCache.get(utilityspaceA).remove(utilityspaceB);
			if (bidSpaceCache.get(utilityspaceA).size() == 0) {
				bidSpaceCache.remove(utilityspaceA);
			}
		}
	}
}