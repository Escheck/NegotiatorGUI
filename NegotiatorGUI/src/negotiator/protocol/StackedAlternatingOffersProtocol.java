package negotiator.protocol;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import negotiator.Bid;
import negotiator.actions.Accept;
import negotiator.actions.Action;
import negotiator.actions.EndNegotiation;
import negotiator.actions.Inform;
import negotiator.actions.Offer;
import negotiator.exceptions.NegotiationPartyTimeoutException;
import negotiator.parties.NegotiationPartyInternal;
import negotiator.session.Round;
import negotiator.session.Session;
import negotiator.session.Turn;

/**
 * Implementation of an alternating offer protocol using offer/counter-offer.
 * <p/>
 * Protocol:
 * 
 * <pre>
 * The first agent makes an offer
 * Other agents can accept or make a counter-offer
 * 
 * If no agent makes a counter-offer, the negotiation end with this offer.
 * Otherwise, the process continues until reaching deadline or agreement.
 * </pre>
 *
 * @author David Festen
 * @author Reyhan
 */
public class StackedAlternatingOffersProtocol extends
		MultilateralProtocolAdapter {

	/**
	 * Get all possible actions for given party in a given session.
	 *
	 * @param parties
	 * @param session
	 *            the current state of this session
	 * @return A list of possible actions
	 */

	/**
	 * Defines the round structure.
	 * 
	 * <pre>
	 * The first agent makes an offer
	 * Other agents can accept or make a counter-offer
	 * </pre>
	 *
	 * @param parties
	 *            The parties currently participating
	 * @param session
	 *            The complete session history
	 * @return A list of possible actions
	 */
	@Override
	public Round getRoundStructure(List<NegotiationPartyInternal> parties,
			Session session) {
		Round round = new Round();
		boolean isFirstRound = session.getRoundNumber() == 0;

		for (NegotiationPartyInternal party : parties) {
			if (isFirstRound) {
				// If this is the first party in the first round, it can not
				// accept.
				round.addTurn(new Turn(party, Offer.class, EndNegotiation.class));
			} else {
				// Each party can either accept the outstanding offer, or
				// propose a counteroffer.
				round.addTurn(new Turn(party, Accept.class, Offer.class,
						EndNegotiation.class));
			}
		}

		// return round structure
		return round;
	}

	/**
	 * Will return the current agreement.
	 *
	 * @param session
	 *            The complete session history up to this point
	 * @return The agreed upon bid or null if no agreement
	 */
	@Override
	public Bid getCurrentAgreement(Session session,
			List<NegotiationPartyInternal> parties) {

		// if not all parties agree, we did not find an agreement
		if (getNumberOfAgreeingParties(session, parties) < parties.size())
			return null;

		// all parties agreed, return most recent offer
		return getMostRecentBid(session);
	}

	@Override
	public int getNumberOfAgreeingParties(Session session,
			List<NegotiationPartyInternal> parties) {
		int nAccepts = 0;
		ArrayList<Action> actions = getMostRecentTwoRounds(session);
		for (int i = actions.size() - 1; i >= 0; i--) {
			if (actions.get(i) instanceof Accept) {
				nAccepts++;
			} else {
				if (actions.get(i) instanceof Offer) {
					// voting party also counts towards agreeing parties
					nAccepts++;
				}
				// we found at least one not accepting party (offering/ending
				// negotiation) so stop counting
				break;
			}
		}
		return nAccepts;
	}

	/**
	 * Get a list of all actions of the most recent two rounds
	 * 
	 * @param session
	 *            Session to extract the most recent two rounds out of
	 * @return A list of actions done in the most recent two rounds.
	 */
	private ArrayList<Action> getMostRecentTwoRounds(Session session) {

		// holds actions
		ArrayList<Action> actions = new ArrayList<Action>();

		// add previous round if exists
		if (session.getRoundNumber() >= 2) {
			Round round = session.getRounds().get(session.getRoundNumber() - 2);
			for (Action action : round.getActions())
				actions.add(action);
		}

		// add current round if exists (does not exists before any offer is
		// made)
		if (session.getRoundNumber() >= 1) {
			Round round = session.getRounds().get(session.getRoundNumber() - 1);
			for (Action action : round.getActions())
				actions.add(action);
		}

		// return aggregated actions
		return actions;
	}

	private Bid getMostRecentBid(Session session) {

		// reverse rounds/actions until offer is found or return null
		for (int roundIndex = session.getRoundNumber() - 1; roundIndex >= 0; roundIndex--) {
			for (int actionIndex = session.getRounds().get(roundIndex)
					.getActions().size() - 1; actionIndex >= 0; actionIndex--) {
				Action action = session.getRounds().get(roundIndex)
						.getActions().get(actionIndex);
				if (action instanceof Offer)
					return ((Offer) action).getBid();
			}
		}

		// No offer found, so return null (no most recent bid exists)
		// since this is only possible when first party quits negotiation, it is
		// probably a bug when this happens
		System.err
				.println("Possible bug: No Offer was placed during negotiation");
		return null;
	}

	/**
	 * If all agents accept the most recent offer, than this negotiation ends.
	 * Also, when any agent ends the negotiation (EndNegotiationAction) the
	 * negotiation ends
	 *
	 * @param session
	 *            the current state of this session
	 * @return true if the protocol is finished
	 */
	@Override
	public boolean isFinished(Session session,
			List<NegotiationPartyInternal> parties) {
		return getCurrentAgreement(session, parties) != null
				|| session.getMostRecentAction() instanceof EndNegotiation;
	}

	/**
	 * Get a map of parties that are listening to each other.
	 *
	 * @return who is listening to who
	 */
	@Override
	public Map<NegotiationPartyInternal, List<NegotiationPartyInternal>> getActionListeners(
			List<NegotiationPartyInternal> parties) {

		// create a new map of parties
		Map<NegotiationPartyInternal, List<NegotiationPartyInternal>> map = new HashMap<NegotiationPartyInternal, List<NegotiationPartyInternal>>();

		// for each party add each other party
		for (NegotiationPartyInternal listener : parties) {
			ArrayList<NegotiationPartyInternal> talkers = new ArrayList<NegotiationPartyInternal>();
			for (NegotiationPartyInternal talker : parties) {
				if (talker != listener) {
					talkers.add(talker);
				}
			}
			map.put(listener, talkers);
		}

		return map;
	}

	@Override
	public void beforeSession(Session session,
			final List<NegotiationPartyInternal> parties)
			throws InterruptedException, ExecutionException,
			NegotiationPartyTimeoutException {
		super.beforeSession(session, parties);
		for (final NegotiationPartyInternal party : parties) {
			try {
				getExecutor().execute(party.getPartyId().toString(),
						new Callable<Object>() {
							@Override
							public Object call() throws Exception {
								party.getParty().receiveMessage(
										"Protocol",
										new Inform("NumberOfAgents", parties
												.size()));

								return null;
							}
						});
			} catch (TimeoutException e) {
				String msg = String
						.format("Negotiating party %s timed out in receiveMessage() method.",
								party.getPartyId());
				throw new NegotiationPartyTimeoutException(party, msg, e);
			}
		}
	}
}
