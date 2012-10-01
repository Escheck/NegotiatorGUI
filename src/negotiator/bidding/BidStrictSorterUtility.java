package negotiator.bidding;

import java.util.Comparator;
import negotiator.Bid;
import negotiator.utility.UtilitySpace;

/**
 * Comparator which sorts a set of Bids based on their utility.
 * The bid with the highest utility is on the front of the list.
 * In addition, the ordering is unique: bids with exactly the same utility
 * are always ordered the same. Use this class ONLY when comparing if two
 * strategies are equivalent.
 * 
 * @author Mark Hendrikx
 */
public class BidStrictSorterUtility implements Comparator<Bid>{
	
	private UtilitySpace utilitySpace;
	
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