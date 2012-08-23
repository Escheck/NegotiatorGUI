package agents.anac.y2012.TheNegotiatorReloaded;

import java.util.Comparator;

public class BidDetailsSorterTime  implements Comparator<BidDetails> {
	public int compare(BidDetails b1, BidDetails b2)
	{
		if (b1 == null || b2 == null)
			throw new NullPointerException();
		if (b1.equals(b2))
			return 0;
		if (b1.getTime() > b2.getTime())
			return -1;
		else if (b1.getTime() < b2.getTime())
	        return 1;
	    else
	        return ((Integer) b1.hashCode()).compareTo(b2.hashCode());
	}
}
