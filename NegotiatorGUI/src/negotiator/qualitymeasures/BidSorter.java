package negotiator.qualitymeasures;

import java.util.Comparator;

import negotiator.Bid;


public class BidSorter  implements Comparator<Bid> {
	public int compare(Bid b1, Bid b2)
	{
		if (b1 == null || b2 == null)
			throw new NullPointerException();
		if (b1.equals(b2))
			return 0;
		return b1.toString().compareTo(b2.toString());
	}
}
