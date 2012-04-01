package negotiator.bidding;

import java.util.Comparator;

import negotiator.bidding.BidDetails;

public class BidDetailsStrictSorterUtility implements Comparator<BidDetails>
{
	public int compare(BidDetails b1, BidDetails b2)
	{
		if (b1 == null || b2 == null)
			throw new NullPointerException();
		if (b1.getMyUndiscountedUtil() == b2.getMyUndiscountedUtil()) {
			return String.CASE_INSENSITIVE_ORDER.compare(b1.getBid().toString(), b2.getBid().toString());
		}
		if (b1.getMyUndiscountedUtil() > b2.getMyUndiscountedUtil())
			return -1;
		else if (b1.getMyUndiscountedUtil() < b2.getMyUndiscountedUtil())
	        return 1;
	    else
	        return ((Integer) b1.hashCode()).compareTo(b2.hashCode());
	}
}

