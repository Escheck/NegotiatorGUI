package negotiator.analysis;

import java.util.Comparator;

/**
 * Sorts {@link BidPoint} based on their utility for agent A.
 */
public class BidPointSorterA implements Comparator<BidPoint>
{
	public int compare(BidPoint b1, BidPoint b2)
	{
		if (b1 == null || b2 == null)
			throw new NullPointerException();
		if (b1.equals(b2))
			return 0;
		if (b1.getUtilityA() < b2.getUtilityA())
			return -1;
		else if (b1.getUtilityA() > b2.getUtilityA())
	        return 1;
	    else
	        return ((Integer) b1.hashCode()).compareTo(b2.hashCode());
	}
}