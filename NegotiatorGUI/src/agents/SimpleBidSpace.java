package agents;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import negotiator.Bid;
import negotiator.BidIterator;
import negotiator.analysis.BidPoint;
import negotiator.utility.UtilitySpace;
import agents.anac.y2010.Southampton.analysis.BidSpace;

/**
 * A simple version of {@link BidSpace} that does not mess with files, and
 * allows changing and retrieving the utility spaces that are contained here.
 * Utility spaces should not be changed directly, because the ParetoFrontier can
 * not see such changes. {@link #changeSpaces} should be called when the space
 * changes.
 * 
 * @author W.Pasman 2sep14
 *
 */
public class SimpleBidSpace {

	private UtilitySpace mySpace, otherSpace;

	// lazy init to avoid duplicate computations.
	private ParetoFrontierPlus pareto = null;

	/**
	 * 
	 * @param my
	 *            {@link UtilitySpace} of mySide
	 * @param other
	 *            {@link UtilitySpace} of otherSide.
	 */
	public SimpleBidSpace(UtilitySpace my, UtilitySpace other) {
		changeSpaces(my, other);
	}

	/**
	 * select a new utility space. Might work incrementally but currently does
	 * not do that.
	 * 
	 * @param my
	 *            {@link UtilitySpace} of mySide
	 * @param other
	 *            {@link UtilitySpace} of otherSide
	 */
	public void changeSpaces(UtilitySpace my, UtilitySpace other) {
		if (my == null || other == null) {
			throw new NullPointerException("my or other UtilitySpace is null");
		}
		this.mySpace = my;
		this.otherSpace = other;
		pareto = null;

	}

	/**
	 * Utility spaces should not be changed directly, because the ParetoFrontier
	 * can not see such changes.
	 * 
	 * @return own's {@link UtilitySpace}.
	 */
	public UtilitySpace getMyUtilitySpace() {
		return mySpace;
	}

	/**
	 * Utility spaces should not be changed directly, because the ParetoFrontier
	 * can not see such changes.
	 * 
	 * @return the opponent's {@link UtilitySpace}.
	 */
	public UtilitySpace getOpponentUtilitySpace() {
		return otherSpace;
	}

	public ParetoFrontierPlus getPareto() {
		if (pareto == null) {
			pareto = new ParetoFrontierPlus(mySpace, otherSpace);
		}
		return pareto;
	}

	/**
	 * Find a bit that has utilities similar to given bid but is not equal to
	 * it. SortedOutcomeSpace does not help here because it sorts only in 1
	 * dimension and can not return a 2-D closest point.
	 * 
	 * @param last
	 * @return bid similar to given bid but not equal, unless there is no nearby
	 *         other point. This bid may not lie on the pareto.
	 */
	public Bid getSilentBid(Bid bid) {
		BidPoint bidpt = bidPoint(bid);
		Set<BidPoint> nearby = getNearUtility(bidpt, 0.05);
		nearby.remove(bidpt);
		if (nearby.isEmpty()) {
			return bid;
		}
		// pick random
		ArrayList<BidPoint> nearbylist = new ArrayList<BidPoint>(nearby);
		return nearbylist.get((int) (Math.random() * nearbylist.size()))
				.getBid();
	}

	/**
	 * Convert bid into bid point
	 * 
	 * @param bid
	 * @return {@link BidPoint} for given bid.
	 */
	public BidPoint bidPoint(Bid bid) {
		try {
			return new BidPoint(bid, mySpace.getUtility(bid),
					otherSpace.getUtility(bid));
		} catch (Exception e) {
			throw new IllegalStateException("failed to get utility of bid "
					+ bid, e);
		}
	}

	/**
	 * Get set of bids similar (or equal) to given {@link BidPoint}.
	 * 
	 * @param optimalbid
	 * @param distance
	 *            maximum euclidean distance to nearby points.
	 * @return set of {@link BidPoint}s near given point, including the bid
	 *         itself (assuming distance>0).
	 */
	public Set<BidPoint> getNearUtility(BidPoint optimalbid, double distance) {
		HashSet<BidPoint> similarbids = new BidPointSpace();
		BidIterator bids = new BidIterator(mySpace.getDomain());

		while (bids.hasNext()) {
			Bid bid = bids.next();
			BidPoint bidpoint = bidPoint(bid);
			if (bidpoint.getDistance(optimalbid) <= distance) {
				similarbids.add(bidpoint);
			}
		}

		return similarbids;
	}

	/**
	 * Get all bids with opponent utility >= minopputil, and with a utility for
	 * us in the range [minutil, maxutil].
	 * 
	 * @param minutil
	 *            the min utility for us
	 * @param maxutil
	 *            the max utility for us
	 * @param minopputil
	 *            the min opponent utility
	 * 
	 * @return set of {@link BidPoint}s that are inside the given boundaries. If
	 *         the limits are set using known bids, this set should never be
	 *         empty.
	 */
	public Set<BidPoint> getBetweenUtility(double minutil, double maxutil,
			double minopputil) {
		if (minutil > maxutil) {
			throw new IllegalArgumentException("min > max");
		}
		HashSet<BidPoint> filtered = new BidPointSpace();
		BidIterator bids = new BidIterator(mySpace.getDomain());

		while (bids.hasNext()) {
			BidPoint bidpoint = bidPoint(bids.next());

			if (bidpoint.getUtilityB() >= minopputil
					&& bidpoint.getUtilityA() >= minutil
					&& bidpoint.getUtilityA() <= maxutil) {
				filtered.add(bidpoint);
			}
		}

		return filtered;
	}
}
