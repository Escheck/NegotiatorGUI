package agents;

import java.util.Set;

import negotiator.Agent;
import negotiator.Bid;
import negotiator.DiscreteTimeline;
import negotiator.PocketNegotiatorAgent;
import negotiator.Timeline;
import negotiator.actions.Accept;
import negotiator.actions.Action;
import negotiator.actions.Offer;
import negotiator.analysis.BidPoint;
import negotiator.utility.UtilitySpace;

/**
 * Agent proposed by Reynan #944. Notice, this agent only works with a
 * {@link DiscreteTimeline} just like the {@link OptimalBidder}.
 * 
 * @author W.Pasman 2sep2014
 * 
 */
public class DenizPN extends Agent implements PocketNegotiatorAgent {

	/**
	 * We have a hard coded reservation value now. To be fixed later to use
	 * utilitySpace.getReservationValue()
	 */
	private final double RESERVATION_VALUE = 0.5;

	/**
	 * The move that our agent determines to make.
	 */
	private enum MyMoves {
		/** best of concession or accept */
		CONCEDE,
		/** best of concession, accept, or opponent bid projected onto pareto */
		CONCEDE_OR_PARETO,
		/** repeat our last bid */
		SAME,
		/** a bid with same utils as our last bid, but different */
		SILENT
	};

	HistorySpace historySpace = new HistorySpace();

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
		utilitySpace = myUtilities; // keep Agent happy... For Genius
		historySpace.setUtilities(myUtilities, opponentUtilities);
	}

	/**************** extends Agent *******************/
	@Override
	public void init() {
	}

	@Override
	public String getName() {
		return "Deniz agent PN";
	}

	@Override
	public void ReceiveMessage(Action opponentAction) {
		try {
			if (opponentAction instanceof Offer) {
				receiveMessage1(((Offer) opponentAction).getBid());
			}
		} catch (Exception e) {
			// this should be fatal. But
			// we can't do much, ReceiveMessage does not allow throw.
			e.printStackTrace();
		}
	}

	private void receiveMessage1(Bid bid) throws Exception {
		historySpace.getOpponentBids().add(bid);

		if (historySpace.getOutcomeSpace() == null) {
			// if this is null, we are running in Genius.
			historySpace.setUtilities(utilitySpace,
					new SimpleFakeOpponentUtilitySpace(utilitySpace, bid));
		}
	}

	@Override
	public Action chooseAction() {
		/**
		 * default action. Also returned if exception occurs. Notice, that may
		 * be a protocol error but what else can we do?
		 */

		Action action = new Accept();

		try {
			Bid bid = chooseAction1();
			Bid effectiveBid;
			if (bid != null) {
				action = new Offer(bid);
				effectiveBid = bid;
			} else {
				effectiveBid = historySpace.getOpponentBids().last();
			}
			historySpace.getMyBids().add(effectiveBid);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return action;
	}

	/**
	 * get next bid. Returns null for accept. returns max util bid if
	 * historySpace null (which can happen if running from Genius and opponent
	 * did not yet place bid) or if we did not yet place initial bid.
	 * 
	 * @return chosen bid, or null to accept last opponent bid.
	 * @throws Exception
	 *             if fatal problem occurs somewhere in our code.
	 */
	private Bid chooseAction1() throws Exception {
		BidPoint myNextBid = null;

		if (historySpace.getOpponentBids().isEmpty()
				|| historySpace.getMyBids().isEmpty()) {
			// First round. place our best bid
			return utilitySpace.getMaxUtilityBid();
		}

		// below this point, both sides did initial bid
		switch (determineMyMove()) {
		case CONCEDE:
			myNextBid = getConcessionBid();
			break;
		case CONCEDE_OR_PARETO:
			myNextBid = getConcedeOrPareto();
			break;
		case SAME:
			myNextBid = bidPoint(historySpace.getMyBids().last());
			break;
		case SILENT:
			myNextBid = bidPoint(historySpace.getSilentBid());
			break;
		}

		if (acceptOpponentBid(myNextBid)) {
			return null;
		}

		return myNextBid.getBid();
	}

	/**
	 * Checks if we should accept opponent's last bid.
	 * 
	 * @param mynextbid
	 *            the next bid that WE will place, if we do not accept the
	 *            opponents bid.
	 * 
	 * @return true if bid=null, if the opponent's last bid is better than
	 *         mynextbid, or if the opponent's last bid has utility > current
	 *         target utility (see {@link #getTargetUtility()}.
	 * @throws Exception
	 */
	private boolean acceptOpponentBid(BidPoint mynextbid) throws Exception {
		if (mynextbid == null) {
			return true;
		}
		Bid lastopponentbid = historySpace.getOpponentBids().last();
		double lastutil = historySpace.getOutcomeSpace().getMyUtilitySpace()
				.getUtility(lastopponentbid);
		if (mynextbid.getUtilityA() < lastutil) {
			return true;
		}
		if (lastutil > getTargetUtility()) {
			return true;
		}

		return false;
	}

	/**
	 * Determine a move according to the table. The move that we do depends on
	 * the opponent's moves. Actually we are using a reversed version of the
	 * table (columns are the opponent moves: his current, one back and two
	 * back) and after the arrow it's the move that we decide to do: {self means
	 * selfish or silent; nonself means any other}
	 * <ol>
	 * <li>self , self , self -> SAME
	 * <li>self, self, nonself -> CONCEDE_OR_ACCEPT
	 * <li>self, nonself -> SILENT
	 * <li>nice/concession/fortunate -> CONCEDE_OR_ACCEPT
	 * <li>unfortunate -> PARETO_OR_CONCEDE_OR_ACCEPT
	 * </ol>
	 * Assumes that we did one offer and the other side did already 2 offers at
	 * least.
	 * 
	 * @return
	 */
	private MyMoves determineMyMove() {
		// check opponent previous moves. See the table above.
		MoveType move0 = historySpace.getMoveType(0);
		if (isSelfish(move0)) {
			if (isSelfish(historySpace.getMoveType(1))) {
				if (isSelfish(historySpace.getMoveType(2))) {
					return MyMoves.SAME;
				}
				// last 2 moves selfish, but before that not.
				return MyMoves.CONCEDE;
			}
			// current move selfish, previous move not
			return MyMoves.SILENT;
		}
		// current move not selfish
		if (move0 == MoveType.UNFORTUNATE) {
			return MyMoves.CONCEDE_OR_PARETO;
		}
		return MyMoves.CONCEDE;
	}

	/**
	 * Check if move is selfish
	 * 
	 * @param move
	 *            the {@link MoveType} that was done. Can be null
	 * @return true if move is selfish. Returns false if move=null.
	 */
	private boolean isSelfish(MoveType move) {
		return move == MoveType.SELFISH || move == MoveType.SILENT;
	}

	/**
	 * check the concession bid against the on-pareto-projected-other-bid and
	 * return the one with the highest our-utility
	 * 
	 * @return concession, accept or paretoprojected opponent bid.
	 */
	private BidPoint getConcedeOrPareto() {
		return bestBid(projectOtherBidToPareto(), getConcessionBid());
	}

	/**
	 * create a new bid by mapping the other's last bid to the pareto frontier.
	 * *
	 * 
	 * @return opponent's last bid mapped to pareto, or concession bid.
	 */
	private BidPoint projectOtherBidToPareto() {
		SimpleBidSpace space = historySpace.getOutcomeSpace();
		double otherUtil;
		try {
			otherUtil = space.getOpponentUtilitySpace().getUtility(
					historySpace.getOpponentBids().last());
		} catch (Exception e) {
			throw new IllegalStateException("getUtility failed", e);
		}

		return space.getPareto().getBidNearOpponentUtility(otherUtil);
	}

	/**
	 * Tries to concede. The starting point is a bid from the optimalbidding
	 * strategy. But instead of using that right away, we search around to find
	 * some other bidpoints close to that optimal bidpoint. We pick the bid that
	 * in the neighbourhood that has a minimal hamming distance with the
	 * opponent's last bid.
	 * 
	 * <h1>Assumes</h1> that opponent did place at least one bid.
	 * 
	 * @return concession bid according to {@link OptimalBidder}.
	 * @throws Exception
	 */
	private BidPoint getConcessionBid() {
		BidPoint optimalbid = getOptimalBid();
		Bid lastopponentbid = historySpace.getOpponentBids().last();
		Set<BidPoint> nearoptimalbids = historySpace.getOutcomeSpace()
				.getNearUtility(optimalbid, 0.05);
		return getSmallestHamming(nearoptimalbids, lastopponentbid);
	}

	/**
	 * Find in a set of BidPoints the one that has highest similarity (see
	 * {@link Bid#getSimilarity(Bid)}) to given target bid.
	 * 
	 * @param bids
	 *            set of bids to pick from
	 * @param targetbid
	 *            the
	 * @return a bid that has highest similarity with given target bid. May
	 *         return null if the given bids list is empty.
	 */
	private BidPoint getSmallestHamming(Set<BidPoint> bids, Bid targetbid) {
		BidPoint bestBid = null;
		double bestsimilarity = -1; // lower than worst similarity
		for (BidPoint bidpoint : bids) {
			double similarity = bidpoint.getBid().getSimilarity(targetbid);
			if (similarity > bestsimilarity) {
				bestBid = bidpoint;
				bestsimilarity = similarity;
			}
		}
		return bestBid;
	}

	/**
	 * Get the bid that has biggest myUtility.
	 * 
	 * @param point1
	 *            first bid
	 * @param point2
	 *            second bid
	 * @return best bid of the two points provided.
	 */
	private BidPoint bestBid(BidPoint point1, BidPoint point2) {
		if (isBetter(point1, point2)) {
			return point1;
		}
		return point2;
	}

	/**
	 * Would be nice to have this in BidPoint.
	 * 
	 * @param point1
	 * @param point2
	 * @return true if point1 is better for us than point2.
	 */
	private boolean isBetter(BidPoint point1, BidPoint point2) {
		return point1.getUtilityA() >= point2.getUtilityA();
	}

	/**
	 * Create a bid point for a given bid
	 * 
	 * @param bid
	 * @return {@link BidPoint}.
	 */
	private BidPoint bidPoint(Bid bid) {
		return historySpace.getOutcomeSpace().bidPoint(bid);
	}

	/****************** OptimalBidder code *************/

	/**
	 * The optimal bid is a bid on the pareto with a target utility
	 * {@link #targetUtil(remain)} where remain is the remaining turns for me.
	 *
	 */
	private BidPoint getOptimalBid() {
		return historySpace.getOutcomeSpace().getPareto()
				.getBidNearMyUtility(getTargetUtility());
	}

	/**
	 * Target utility that we want right now.
	 * 
	 * @return current targetutility.
	 */
	private double getTargetUtility() {
		int roundsleft = ((DiscreteTimeline) timeline).getOwnRoundsLeft();
		Double targetUtil = targetUtil(roundsleft + 1);
		return targetUtil;
	}

	/**
	 * computation of the bid for round j as in prop 4.3. Basically this
	 * increases with each round, starting at 0.5 + reservation value. This
	 * means that when many rounds are left, we aim at a high utility, and then
	 * lower our target utility when the number of rounds left decreases.
	 * 
	 * @param roundsLeft
	 *            minimum = 1.
	 * @return target utility for the given round.
	 **/

	private double targetUtil(int roundsLeft) {
		if (roundsLeft == 1)
			return 0.5 + 0.5 * RESERVATION_VALUE;
		else
			return 0.5 + 0.5 * Math.pow(targetUtil(roundsLeft - 1), 2);
	}

}
