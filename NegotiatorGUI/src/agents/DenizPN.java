package agents;

import java.util.Set;

import negotiator.Agent;
import negotiator.Bid;
import negotiator.DiscreteTimeline;
import negotiator.PocketNegotiatorAgent;
import negotiator.Timeline;
import negotiator.actions.Accept;
import negotiator.actions.Action;
import negotiator.actions.EndNegotiation;
import negotiator.actions.Offer;
import negotiator.analysis.BidPoint;
import negotiator.utility.UtilitySpace;

/**
 * Agent proposed by Reyhan Aydogan #944 , conform the 'optimal bidder' agent by
 * Tim Baarslag but fitted to the needs of PocketNegotiator. Designed for use
 * both as opponent agent and as suggest-a-bid-agent. Notice, this agent only
 * works with a {@link DiscreteTimeline} just like the {@link OptimalBidder}.
 * 
 * @author W.Pasman 2sep2014.
 * @modified W.Pasman 8oct2014 new concession procedure see Deniz documentation
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
		SILENT,
		/** stop the negotiation. */
		STOP
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

		/*
		 * DEBUG: print out the bid space System.out.println("udpate profiles");
		 * BidIterator it = new BidIterator(myUtilities.getDomain()); try {
		 * while (it.hasNext()) { Bid bid = it.next(); System.out.println("" +
		 * bid + " " + myUtilities.getUtility(bid) + " " +
		 * opponentUtilities.getUtility(bid));
		 * 
		 * } } catch (Exception e) { e.printStackTrace(); }
		 */
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

	/**
	 * Save the bid in the history. If this is the first bid and we are running
	 * in Genius, we use this bid to estimate the opponent's utility space in a
	 * simplistic way.
	 * 
	 * @param bid
	 * @throws Exception
	 */
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
		 * be a protocol error but what else can we do? seems worse than Accept.
		 */
		Action action = new Accept();

		try {
			action = chooseAction1();

			Bid effectiveBid = getEffectiveBid(action);
			if (effectiveBid != null) {
				historySpace.getMyBids().add(effectiveBid);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return action;
	}

	/**
	 * get the 'effective bid' of an action. For an offer, the effective bid is
	 * the bid contained in the offer. For an accept, it is the opponent's last
	 * bid. For a endnegotiation, we return null.
	 * 
	 * Throws exception if action is accept and opponent made no bid yet.
	 * 
	 * @param action
	 */
	private Bid getEffectiveBid(Action action) {
		if (action instanceof Offer) {
			return ((Offer) action).getBid();
		} else if (action instanceof Accept) {
			return historySpace.getOpponentBids().last();
		}
		return null;
	}

	/**
	 * get next bid. returns offer with max util bid if historySpace null (which
	 * can happen if running from Genius and opponent did not yet place bid) or
	 * if we did not yet place initial bid.
	 * 
	 * @return chosen bid, Accept or Stop..
	 * @throws Exception
	 *             if fatal problem occurs somewhere in our code.
	 */
	private Action chooseAction1() throws Exception {

		if (historySpace.getOpponentBids().isEmpty()
				|| historySpace.getMyBids().isEmpty()) {
			// First round. place our best bid
			return new Offer(utilitySpace.getMaxUtilityBid());
		}

		// below this point, both sides did initial bid
		Action myNextAction;
		switch (checkDeadlineAndDetermineMyMove()) {
		case CONCEDE:
			myNextAction = new Offer(getConcessionBid().getBid());
			break;
		case CONCEDE_OR_PARETO:
			myNextAction = new Offer(getConcedeOrPareto().getBid());
			break;
		case SAME:
			myNextAction = new Offer(historySpace.getMyBids().last());
			break;
		case SILENT:
			myNextAction = new Offer(historySpace.getSilentBid());
			break;
		case STOP:
			myNextAction = new EndNegotiation();
			break;
		default:
			throw new IllegalStateException("internal error, unknown move");
		}

		if (isAcceptBetter(myNextAction)) {
			return new Accept();
		}

		return myNextAction;
	}

	/**
	 * Checks if accepting opponent's last bid has higher util than doing
	 * #mynextaction.
	 * 
	 * @param mynextaction
	 *            the next bid that WE will place, if we do not accept the
	 *            opponents bid.
	 * 
	 * @return true if the opponent's last bid is better than mynextaction
	 *         effectivebid, or if the opponent's last bid has utility > current
	 *         target utility (see {@link #getTargetUtility()}.
	 * @throws Exception
	 *             if opp did not make a bid.
	 */
	private boolean isAcceptBetter(Action mynextaction) throws Exception {
		// first check if opponent's last bid was good.
		Bid lastopponentbid = historySpace.getOpponentBids().last();
		double lastutil = historySpace.getOutcomeSpace().getMyUtilitySpace()
				.getUtility(lastopponentbid);

		if (lastutil < RESERVATION_VALUE) {
			return false;
		}

		if (lastutil >= getTargetUtility()) {
			return true;
		}

		// check if accept is better than our next action.
		Bid mynextbid = getEffectiveBid(mynextaction);
		if (mynextbid == null) { // we want to stop the nego
			return false;
		}

		double mynextbidutility = historySpace.getOutcomeSpace()
				.getMyUtilitySpace().getUtility(mynextbid);

		// suggest accept if it has higher util than our current action.
		return mynextbidutility < lastutil;
	}

	/**
	 * Check the deadline. If across, do {@link MyMoves#SILENT} or
	 * {@link MyMoves#STOP}. If not, return a move according to the table.
	 * 
	 * @return next planned move.
	 */
	private MyMoves checkDeadlineAndDetermineMyMove() {
		int roundsleft = ((DiscreteTimeline) timeline).getOwnRoundsLeft();
		if (roundsleft < 0) { // pass the deadline?
			if (roundsleft < -3) { // pass the extra-time?
				return MyMoves.STOP;
			}
			return MyMoves.SILENT;
		}
		return determineMyMove();
	}

	/**
	 * Determine a move according to the table. The move that we do depends on
	 * the opponent's moves. Actually we are using a reversed version of the
	 * table (columns are the opponent moves: his current, one back and two
	 * back) and after the arrow it's the move that we decide to do: {self means
	 * selfish or silent; nonself means any other}.
	 * 
	 * 
	 * <ol>
	 * <li>self , self , self -> SAME
	 * <li>self, self, nonself -> CONCEDE_OR_ACCEPT
	 * <li>self, nonself -> SILENT
	 * <li>nice/concession/fortunate -> CONCEDE_OR_ACCEPT
	 * <li>unfortunate -> PARETO_OR_CONCEDE_OR_ACCEPT
	 * </ol>
	 * 
	 * <br>
	 * This code does not check against the possibility to accept. This is
	 * because this code does not compute the effective offer, it only suggests
	 * to do eg a concession.
	 * 
	 * Assumptions:
	 * <ul>
	 * <li>we did one offer and the other side did already 2 offers at least.
	 * <li>we are still before the deadline.
	 * </ul>
	 * 
	 * @return
	 */
	private MyMoves determineMyMove() {
		// check opponent previous moves. See the table above.
		MoveType move0 = historySpace.getMoveType(0);
		if (isSelfish(move0)) {
			if (isSelfishOrUnfortunate(historySpace.getMoveType(1))) {
				if (isSelfishOrUnfortunate(historySpace.getMoveType(2))) {
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
	 * Check if move is selfish or unfortunate
	 * 
	 * @param move
	 *            the {@link MoveType} that was done. Can be null
	 * @return true if move is selfish. Returns false if move=null.
	 */
	private boolean isSelfishOrUnfortunate(MoveType move) {
		return move == MoveType.SELFISH || move == MoveType.SILENT
				|| move == MoveType.UNFORTUNATE;
	}

	/**
	 * check the concession bid against the on-pareto-projected-other-bid and
	 * return the one with the highest our-utility
	 * 
	 * @return concession, accept or paretoprojected opponent bid.
	 * @throws Exception
	 */
	private BidPoint getConcedeOrPareto() throws Exception {
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
	 * Tries to find a concession bid. A concession is a bid that has higher or
	 * equal opponent utility than our previous bid. The starting point is a bid
	 * from the optimalbidding strategy (see {@link OptimalBidder}). But instead
	 * of using that right away, we pick the bid that in the neighbourhood that
	 * has a minimal hamming distance with the opponent's last bid. <br>
	 * The 'neighbourhood' is all the concessions with a utility for ourself
	 * between our current optimal bid and our previous bid. <br>
	 * If there are no concessions at all in this neighbourhood, this function
	 * may return the previous bid.
	 * 
	 * <h1>Assumes</h1>
	 * <ul>
	 * <li>both sides placed at least one bid.
	 * </ul>
	 * 
	 * @return concession bid
	 * @throws Exception
	 */
	private BidPoint getConcessionBid() throws Exception {
		double targetUtility = getTargetUtility();
		Bid prevBid = historySpace.getMyBids().last();
		double prevBidUtility = historySpace.getOutcomeSpace()
				.getMyUtilitySpace().getUtility(prevBid);
		double prevBidOppUtility = historySpace.getOutcomeSpace()
				.getOpponentUtilitySpace().getUtility(prevBid);

		Set<BidPoint> concessions = historySpace.getOutcomeSpace()
				.getBetweenUtility(prevBidUtility, targetUtility,
						prevBidOppUtility);

		Bid lastopponentbid = historySpace.getOpponentBids().last();

		return getSmallestHamming(concessions, lastopponentbid);
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

	/****************** OptimalBidder code *************/
	/**
	 * Optimal bidder basically means that we try to reach a target utility
	 * depending on the time left.
	 */

	/**
	 * Target utility that we want right now. This is the normalized value,
	 * considering the current maximum utility. if we are past deadline, we
	 * stick to the minimum target util. This is conform the discussion with Tim
	 * in #957 (oct2014).
	 * 
	 * @return current targetutility.
	 * @throws Exception
	 */
	private double getTargetUtility() throws Exception {
		int turnsleft = ((DiscreteTimeline) timeline).getOwnRoundsLeft();
		if (turnsleft < 0) {
			turnsleft = 0;
		}
		UtilitySpace us = historySpace.getOutcomeSpace().getMyUtilitySpace();
		double maxutil = us.getUtility(us.getMaxUtilityBid());
		return RESERVATION_VALUE + targetUtilNormalized(turnsleft)
				* (maxutil - RESERVATION_VALUE);
	}

	/**
	 * Get a normalized target utility ranging from 0.5 to 1. Computation of the
	 * bid for round j as in prop 4.3. Basically this increases with increasing
	 * number of remaining rounds. When many rounds are left, target utility=1.
	 * When no rounds are left, this decreases to 0.5 + 0.5*reservation value. <br>
	 * <b>WARNING</b>this is the unscaled utility. If the utility range is not
	 * entirely [0,1], a scaling function applies #957, see also
	 * {@link #getTargetUtility()}.
	 * 
	 * <h1>Discussion</h1> Tim explained why the target utility is not
	 * {@link #RESERVATION_VALUE} when turnsLeft=0. This is because you never
	 * want to go all the way down to your reservation value when bidding and is
	 * intended this way. This targetUtil function is used to make concessions,
	 * not for acceptance strategy. For accepting, we may still accept at the
	 * {@link #RESERVATION_VALUE}. <br>
	 * After more discussion, Tim proposed this version. See #957.
	 * 
	 * @param turnsLeft
	 *            minimum=0 when we are in the last round
	 * @return normalized ( range <0.5-1] ) target utility.
	 */
	private double targetUtilNormalized(int turnsLeft) {
		if (turnsLeft == 0) {

			return 0.5;
		} else {
			return 0.5 + 0.5 * Math.pow(targetUtilNormalized(turnsLeft - 1), 2);
		}
	}

}
