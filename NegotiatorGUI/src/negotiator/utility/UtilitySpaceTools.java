package negotiator.utility;

import negotiator.Bid;
import negotiator.BidIterator;

/**
 * Some utility functions that work on a utility space. Works as an add-on on a
 * given {@link UtilitySpace}. Does not extend it, so that receivers of an
 * abstract {@link UtilitySpace} can connect it with these tools too.
 * <p>
 * This is a class, not a set of static functions, to allow caching of results
 * (not yet used).
 *
 */
public class UtilitySpaceTools {
	private UtilitySpace utilSpace;

	public UtilitySpaceTools(UtilitySpace space) {
		utilSpace = space;
	}

	/**
	 * Returns the maximum bid in the utility space. This is only supported for
	 * linear utility spaces. Totally revised, brute-force search now.
	 * 
	 * @return a bid with the maximum utility value attainable in this util
	 *         space
	 * @throws Exception
	 *             if there is no bid at all in this util space.
	 */
	public final Bid getMaxUtilityBid() throws Exception {
		Bid maxBid = null;
		double maxutil = 0.;
		BidIterator bidit = new BidIterator(utilSpace.getDomain());

		if (!bidit.hasNext())
			throw new Exception("The domain does not contain any bids!");
		while (bidit.hasNext()) {
			Bid thisBid = bidit.next();
			double thisutil = utilSpace.getUtility(thisBid);
			if (thisutil > maxutil) {
				maxutil = thisutil;
				maxBid = thisBid;
			}
		}
		return maxBid;
	}

	/**
	 * Returns the worst bid in the utility space. This is only supported for
	 * linear utility spaces.
	 * 
	 * @return a bid with the lowest possible utility
	 * @throws Exception
	 *             if there is no bid at all in the util space
	 */
	public Bid getMinUtilityBid() throws Exception {
		Bid minBid = null;
		double minUtil = 1.2;
		BidIterator bidit = new BidIterator(utilSpace.getDomain());

		if (!bidit.hasNext())
			throw new Exception("The domain does not contain any bids!");
		while (bidit.hasNext()) {
			Bid thisBid = bidit.next();
			double thisutil = utilSpace.getUtility(thisBid);
			if (thisutil < minUtil) {
				minUtil = thisutil;
				minBid = thisBid;
			}
		}
		return minBid;
	}

}
