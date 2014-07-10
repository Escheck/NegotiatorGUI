package agents;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import negotiator.Agent;
import negotiator.Bid;
import negotiator.BidIterator;
import negotiator.Domain;
import negotiator.PocketNegotiatorAgent;
import negotiator.Timeline;
import negotiator.actions.Accept;
import negotiator.actions.Action;
import negotiator.actions.Offer;
import negotiator.utility.UtilitySpace;

/**
 * Very simplistic tit-for-tat. Only PN can provide us with a good approximation
 * of the opponent util space. If we don't have it, we use our own space to make
 * guesses at opponent util space. This agent is the basis for a
 * 
 * @author W.Pasman 9jul2014
 * 
 */
public class SimpleTitForTatPN extends Agent implements PocketNegotiatorAgent {

	// initialized when opponent makes first bid, or when we receive it from PN.
	protected UtilitySpace otherUtilitySpace = null;

	private Bid lastBid = null;
	private Bid lastOpponentBid = null;

	/**
	 * Here we cache potential bids that we can make. Sorted on utility for us.
	 */
	private Map<Double, Bid> goodBids = new HashMap<Double, Bid>();

	/**
	 * Here we store the bids that have been used. We must store the bids, not
	 * the utilities because the utilities can change any time
	 * {@link #updateProfiles(UtilitySpace, UtilitySpace)} is called, and that
	 * call does not clear usedBids.
	 */
	private Set<Bid> usedBids = new HashSet<Bid>();

	/**************** extends Agent *******************/
	@Override
	public void init() {
	}

	@Override
	public String getName() {
		return "Simple Tit for Tat PN";
	}

	@Override
	public void ReceiveMessage(Action opponentAction) {
		if (opponentAction instanceof Offer) {
			Bid bid = ((Offer) opponentAction).getBid();
			lastOpponentBid = bid;
		}
	}

	@Override
	public Action chooseAction() {
		try {
			Bid bid = chooseAction1();
			if (bid == null) {
				return new Accept();
			}
			lastBid = bid;
			usedBids.add(bid);
			return new Offer(bid);
		} catch (Exception e) {
			// if we get here we're totally screwed. We return accept but that
			// may be a protocol error...
			e.printStackTrace();
			return new Accept();
		}
	}

	/**
	 * get next bid. Returns null for accept.
	 * 
	 * @return chosen bid, or null to accept last opponent bid.
	 * @throws Exception
	 */
	private Bid chooseAction1() throws Exception {
		if (lastOpponentBid == null) {
			// Other side did not yet place bid. we can only place our best bid.
			return utilitySpace.getMaxUtilityBid();
		}

		// below this point, other side did make a bid
		if (otherUtilitySpace == null) {
			// if we get here , we are running inside Genius.
			// That means we have to fake otherUtilitySpace
			try {
				otherUtilitySpace = new OpponentUtilitySpace(utilitySpace,
						lastOpponentBid);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		// below this point, other side made bid and we have otherUtilitySpace.
		if (goodBids.isEmpty()) {
			findGoodBids();
		}

		// the heart of tit-for-tat: estimate opponent utility for him, and do
		// an offer that has same util for us.
		double targetUtil = otherUtilitySpace.getUtility(lastOpponentBid);

		// #924 limit concession target (min. 0.6) and rate (max 0.1 per cycle).
		targetUtil = Math.max(targetUtil, 0.6);
		targetUtil = Math.max(targetUtil,
				utilitySpace.getUtility(lastBid) - 0.1);

		Bid bid = getUnusedBidNearUtil(targetUtil);
		double util = utilitySpace.getUtility(bid);
		// is the bid we have worse than what we were offered? We also need to
		// keep an eye on the deadline
		double minimumutility = util - 0.2 * (1. - timeline.getTime());
		if (utilitySpace.getUtility(lastOpponentBid) >= minimumutility) {
			return null;// accept
		}
		return bid;
	}

	/**
	 * Find unused bid in {@link #goodBids} close to given targetUtil
	 * 
	 * @param targetUtil
	 * @return
	 */
	private Bid getUnusedBidNearUtil(double targetUtil) {

		Bid good = goodBids.get(roundUtil(targetUtil));
		if (good != null && !usedBids.contains(good)) {
			return good;
		}

		// no. Find something nearby.
		double nearestUtil = 10.; // impossibly far away.
		Bid nearestBid = null;
		for (double util : goodBids.keySet()) {
			double distance = Math.abs(targetUtil - util);
			if (distance < nearestUtil) {
				Bid bid = goodBids.get(util);
				if (usedBids.contains(bid))
					continue;
				nearestUtil = distance;
				nearestBid = bid;
			}
		}
		return nearestBid;
	}

	/**
	 * Rounding the bid utilities is used to limit/bin the search space. This
	 * saves memory and avoids having too many options to choose from when it
	 * comes to finding a counter bid. The rounding mechanism should be
	 * consisstent throughout the code.
	 * 
	 * @param util
	 * @return
	 */
	private double roundUtil(double util) {
		return Math.round(1000. * util) / 1000.;
	}

	/**
	 * Find good bids in our utility space. Assumes that
	 * {@link #otherUtilitySpace} has been set.
	 * 
	 * @throws Exception
	 */
	private void findGoodBids() throws Exception {
		if (utilitySpace.getDomain().getNumberOfPossibleBids() > 20000) {
			findApproximateGoodBids();
		} else {
			findAllGoodBids();
		}
		if (goodBids.isEmpty()) {
			throw new IllegalStateException(
					"failed to generate bids in this space");
		}
	}

	/**
	 * Exhaustively checks all bids in the domain, and puts all good bids in
	 * {@link #goodBids}.
	 * 
	 * @throws Exception
	 */
	private void findAllGoodBids() throws Exception {
		BidIterator iterator = new BidIterator(utilitySpace.getDomain());
		while (iterator.hasNext()) {
			checkBid(iterator.next());
		}
	}

	/**
	 * checks 20k random bids in the domain, and puts all good bids in
	 * 
	 * @throws Exception
	 */
	private void findApproximateGoodBids() throws Exception {
		Domain domain = utilitySpace.getDomain();
		for (int n = 0; n < 20000; n++) {
			checkBid(domain.getRandomBid());
		}
	}

	/**
	 * Checks if given bid is a good bid and if so, inserts in {@link #goodBids}
	 * . Assumes that {@link #otherUtilitySpace} has been set
	 * 
	 * @param bid
	 * @throws Exception
	 */
	private void checkBid(Bid bid) throws Exception {
		// round util to 3 decimals, to limit our space.
		double util = roundUtil(utilitySpace.getUtility(bid));
		Bid good = goodBids.get(util); // already have good bid with same util?
		if (good == null) {
			goodBids.put(util, bid);
			return;
		}
		// ok, we have already a good bid here. Check opponent utilities and
		// keep best for him
		if (otherUtilitySpace.getUtility(bid) > otherUtilitySpace
				.getUtility(good)) {
			// new bid is better.
			goodBids.put(util, bid);
		}
	}

	/************* implements PocketNegotiatorAgent ***************/
	@Override
	public void initPN(UtilitySpace mySide, UtilitySpace otherSide, Timeline tl) {
		updateProfiles(mySide, otherSide);
		timeline = tl;
	}

	@Override
	public void handleAction(Action act) {
		ReceiveMessage(act);

	}

	@Override
	public Action getAction() {
		return chooseAction();
	}

	@Override
	public void updateProfiles(UtilitySpace myUtilities,
			UtilitySpace opponentUtilities) {
		utilitySpace = myUtilities;
		otherUtilitySpace = opponentUtilities;
		goodBids.clear(); // we fill it lazily
	}

}

/**
 * This fakes an opponent utility space. We assume :
 * <ul>
 * <li>his first bid is his best bid
 * <li>relative distances in our own bidspace are indicative of consessions of
 * the opponent.
 * <li>our own util space is varied enough regarding utilities, so that the
 * opponent has a chance to make different utilities in our space by changing
 * his bid
 * </ul>
 * 
 * Tech Note: UtilitySpace should be an interface but it is a class. This makes
 * the code here messy. We are not shielding out any original UtilitySpace code,
 * but it may crash as we don't initialize it.
 * 
 * @author W.Pasman
 * 
 */
@SuppressWarnings("serial")
class OpponentUtilitySpace extends UtilitySpace {
	private final UtilitySpace ownSpace;
	private final double firstOpponentBidUtility;

	/**
	 * 
	 * @param us
	 *            our OWN utilityspace (not the opponent's)
	 * @param firstBid
	 *            first opponent bid.
	 * @throws Exception
	 *             if we can't determine utility of firstBid
	 */
	public OpponentUtilitySpace(UtilitySpace us, Bid firstBid) throws Exception {
		if (firstBid == null)
			throw new NullPointerException("bid=null");

		ownSpace = us;
		firstOpponentBidUtility = ownSpace.getUtility(firstBid);
	}

	/**
	 * Returns utility distance (absolute value) to the first bid.
	 * 
	 * @param b
	 * @return (absolute) distance between given bid and first bid. Number in
	 *         range [0,1].
	 * @throws Exception
	 */
	private double distanceToFirstBid(Bid b) throws Exception {
		return Math.abs(ownSpace.getUtility(b) - firstOpponentBidUtility);
	}

	/**
	 * estimates utility of bid for the opponent.
	 */

	public double getUtility(Bid bid) throws Exception {
		return 1.0 - distanceToFirstBid(bid);
	}
}