package negotiator.bidding;

import java.util.Comparator;
import negotiator.Bid;
import negotiator.utility.UtilitySpace;

/**
 * The problem with the default BidDetailsSorterUtility, is that if two bids
 * have the same utility, then their order can be interchanged.
 * This guarantees a unique ordering. Note that this class should ONLY be used
 * when ordering is important (for example to test equivalence of agent strategies).
 * 
 * @author Mark Hendrikx
 */
public class BidStrictSorterUtility implements Comparator<Bid>{
	
	private UtilitySpace  utilitySpace;

	
	public BidStrictSorterUtility(UtilitySpace utilitySpace) {
		super();
		this.utilitySpace = utilitySpace;
	}

	public int compare(Bid b1, Bid b2)
	{
		try{
			if (b1 == null || b2 == null)
				throw new NullPointerException();
			if (utilitySpace.getUtility(b1) == utilitySpace.getUtility(b2)) {
				return String.CASE_INSENSITIVE_ORDER.compare(b1.toString(), b2.toString());
			}
			if (utilitySpace.getUtility(b1)> utilitySpace.getUtility(b2))
				return -1;
			else if (utilitySpace.getUtility(b1)< utilitySpace.getUtility(b2))
		        return 1;
		    else
		        return ((Integer) b1.hashCode()).compareTo(b2.hashCode());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return -1;
	}
}
