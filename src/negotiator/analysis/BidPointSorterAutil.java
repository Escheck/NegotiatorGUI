package negotiator.analysis;

import java.util.Comparator;

/**
 * Sorts {@link BidPoint}s in utilityA.
 */
public class BidPointSorterAutil implements Comparator<BidPoint>
{
	public int compare(BidPoint b1, BidPoint b2)
	{
		if (b1 == null || b2 == null)
			throw new NullPointerException();
		if (b1.utilityA == b2.utilityA)
			return 0;
		if (b1.utilityA < b2.utilityA)
			return -1;
		else if (b1.utilityA > b2.utilityA)
	        return 1;
	    else
	        return ((Integer) b1.hashCode()).compareTo(b2.hashCode());
	}
}

