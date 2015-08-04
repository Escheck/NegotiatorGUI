package negotiator.protocol;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import negotiator.Bid;
import negotiator.actions.Accept;
import negotiator.actions.Action;
import negotiator.actions.Offer;
import negotiator.actions.OfferForVoting;
import negotiator.actions.Reject;
import negotiator.parties.NegotiationPartyInternal;
import negotiator.session.Round;
import negotiator.session.Session;
import negotiator.session.Turn;

/**
 * Implementation of an alternating offer protocol using voting consensus.
 * <p/>
 * Protocol:
 * 
 * <pre>
 * Round 1: Each agent makes their own offer.
 * Round 2: Each agent votes (accept/reject) for each offer on the table.
 * 
 * If there is one offer that everyone accepts, the negotiation end with this offer.
 * Otherwise, the process continues until reaching deadline or agreement.
 * </pre>
 *
 * @author David Festen
 * @author Reyhan Aydogan
 * @author Catholijn Jonker
 */
public class AlternatingOfferConsensusProtocol extends
		MultilateralProtocolAdapter {
	private int maxNumberOfVotes = 0;

	/**
	 * factory function. To support testing.
	 * 
	 * @param votingParty
	 * @param acceptOrReject
	 * @return
	 */
	public Turn createTurn(NegotiationPartyInternal votingParty,
			Collection<Class<? extends Action>> acceptOrReject) {
		return new Turn(votingParty, acceptOrReject);
	}

	/**
	 * factory function. To support testing.
	 * 
	 * @param party
	 * @param class1
	 * @return
	 */
	public Turn createTurn(NegotiationPartyInternal party,
			Class<? extends Action> class1) {
		return new Turn(party, OfferForVoting.class);
	}

	/**
	 * factory function. To support testing.
	 * 
	 * @return
	 */
	public Round createRound() {
		return new Round();
	}

	/******************************************/
	/**
	 * Get the round structure used by this algorithm.
	 * <p/>
	 * Structure:
	 * 
	 * <pre>
	 * Round 1: Each agent makes their own offer.
	 * Round 2: Each agent votes (accept/reject) for each offer on the table.
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
		Round round = createRound();

		// NOTE: while roundnumber is normally one-based, in this function it's
		// zero based as you are initializing the
		// new round right in this function
		if (session.getRoundNumber() % 2 == 0) {
			// request an offer from each party
			for (NegotiationPartyInternal party : parties) {
				round.addTurn(createTurn(party, OfferForVoting.class));
			}
		} else {
			ArrayList<Class<? extends Action>> acceptOrReject = new ArrayList<Class<? extends Action>>(
					2);
			acceptOrReject.add(Accept.class);
			acceptOrReject.add(Reject.class);

			// request a reaction on each offer from party
			for (NegotiationPartyInternal ignored : parties) {
				for (NegotiationPartyInternal votingParty : parties) {
					round.addTurn(createTurn(votingParty, acceptOrReject));
				}
			}
		}
		return round;
	}

	/**
	 * Check if the protocol is done or still busy. If this method returns true,
	 * the {@link negotiator.session.SessionManager} will not start a new
	 * {@link negotiator.session .Round} after the current one. It will however
	 * finish all the turns described in the
	 * {@link #getRoundStructure(java.util.List, negotiator.session.Session)}
	 * method.
	 *
	 * @param session
	 *            the current state of this session
	 * @return true if the protocol is finished
	 */
	@Override
	public boolean isFinished(Session session,
			List<NegotiationPartyInternal> parties) {
		// if we are making new offers, we are never finished
		if (session.getRoundNumber() < 2 || !isVotingRound(session)) {
			return false;
		}

		// find an acceptable offer
		Round thisRound = session.getMostRecentRound();
		Round prevRound = session.getRounds().get(session.getRoundNumber() - 2);
		Offer acceptedOffer = acceptedOffer(thisRound, prevRound);

		// if null, we are not finished, otherwise we are
		return acceptedOffer != null;
	}

	/**
	 * Gets the current agreement if any.
	 *
	 * @param session
	 *            The complete session history up to this point
	 * @return The agreement bid or null if none
	 */
	@Override
	public Bid getCurrentAgreement(Session session,
			List<NegotiationPartyInternal> parties) {
		int round = session.getRoundNumber();
		if (round % 2 == 1 || round < 2) {
			return null;
		}
		Round thisRound = session.getMostRecentRound();
		Round prevRound = session.getRounds().get(session.getRoundNumber() - 2);
		Offer acceptedOffer = acceptedOffer(thisRound, prevRound);
		return acceptedOffer == null ? null : acceptedOffer.getBid();
	}

	/**
	 * returns the first offer everyone accepted, or null if no such offer.
	 *
	 * @param votingRound
	 *            the round with the voting (expected number of turns is agent#
	 *            * agent#)
	 * @param offerRound
	 *            the round with the offers (expected number of turns is agent#)
	 * @return The accepted offer if such an offer exists, null otherwise
	 */
	private Offer acceptedOffer(Round votingRound, Round offerRound) {
		int numOffers = offerRound.getActions().size();
		List<Turn> turns = votingRound.getTurns();
		List<Action> voteActions = offerRound.getActions();

		// for each block of offers
		for (int i = 0; i < numOffers * numOffers; i += numOffers) {
			int offerNumber = (i / numOffers);
			int votes = 0;
			// for each vote on this offer
			for (int j = i; j < i + numOffers; j++) {
				// count the vote
				if (turns.get(j).getAction() instanceof Accept) {
					votes++;
				}
			}
			maxNumberOfVotes = Math.max(maxNumberOfVotes, votes);

			// if enough votes, accept bid
			if (votes == numOffers) {
				return (Offer) voteActions.get(offerNumber);
			}
		}

		return null;
	}

	/**
	 * Returns whether this is a voting round.
	 *
	 * @param session
	 *            the current state of this session
	 * @return true is this is an even round
	 */
	private boolean isVotingRound(Session session) {
		return session.getRoundNumber() % 2 == 0;
	}

	/**
	 * Gets the maximum number of vote this protocol found.
	 *
	 * @param session
	 *            the current state of this session
	 * @param parties
	 *            The parties currently participating
	 * @return the number of parties agreeing to the current agreement
	 */
	@Override
	public int getNumberOfAgreeingParties(Session session,
			List<NegotiationPartyInternal> parties) {
		return maxNumberOfVotes;
	}

	/**
	 * Get a map of parties that are listening to each other's response
	 *
	 * @param parties
	 *            The parties involved in the current negotiation
	 * @return A map where the key is a
	 *         {@link negotiator.parties.NegotiationParty} that is responding to
	 *         a
	 *         {@link negotiator.parties.NegotiationParty#chooseAction(java.util.List)}
	 *         event, and the value is a list of
	 *         {@link negotiator.parties.NegotiationParty} that are listening to
	 *         that key party's response.
	 */
	@Override
	public Map<NegotiationPartyInternal, List<NegotiationPartyInternal>> getActionListeners(
			List<NegotiationPartyInternal> parties) {
		Map<NegotiationPartyInternal, List<NegotiationPartyInternal>> listenersMap = new HashMap<NegotiationPartyInternal, List<NegotiationPartyInternal>>();
		for (NegotiationPartyInternal party : parties) {
			listenersMap.put(party, parties);
		}
		return listenersMap;
	}
}
