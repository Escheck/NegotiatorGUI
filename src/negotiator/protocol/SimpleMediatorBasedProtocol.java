package negotiator.protocol;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import negotiator.Bid;
import negotiator.actions.InformVotingResult;
import negotiator.actions.OfferForVoting;
import negotiator.actions.VoteForOfferAcceptance;
import negotiator.parties.NegotiationParty;
import negotiator.parties.NegotiationPartyInternal;
import negotiator.parties.RandomFlippingMediator;
import negotiator.session.Round;
import negotiator.session.Session;
import negotiator.session.Turn;

/**
 * Basic implementation of a mediator based protocol. </p> Protocol:
 * 
 * <pre>
 * Mediator proposes an offer
 * Agents vote accept/reject
 * Mediator Informs parties of result
 * </pre>
 *
 * @author David Festen
 * @author Reyhan
 */
public class SimpleMediatorBasedProtocol extends MediatorProtocol {
	/**
	 * Get the structure of the current round. Each round, this method receives
	 * a list of all the {@link negotiator.parties.NegotiationParty} and the
	 * complete {@link negotiator.session .Session} which can be used to
	 * diversify the round structure at some point during the session.
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

		// initialize and split parties
		Round round = new Round();
		NegotiationPartyInternal mediator = getMediator(parties);
		List<NegotiationPartyInternal> otherParties = getNonMediators(parties);

		// mediator opening turn
		round.addTurn(new Turn(mediator, OfferForVoting.class));

		// other parties' turn
		for (NegotiationPartyInternal otherParty : otherParties) {
			round.addTurn(new Turn(otherParty, VoteForOfferAcceptance.class));
		}

		// mediator finishing turn
		round.addTurn(new Turn(mediator, InformVotingResult.class));

		// return new round structure
		return round;
	}

	/**
	 * Get a map of parties that are listening to each other's response
	 *
	 * @param parties
	 *            The parties to listen to
	 * @return A map where the key is a {@link NegotiationParty} that is
	 *         responding to a {@link NegotiationParty#chooseAction(List)}
	 *         event, and the value is a list of {@link NegotiationParty} that
	 *         are listening to that key party's response.
	 */
	@Override
	public Map<NegotiationPartyInternal, List<NegotiationPartyInternal>> getActionListeners(
			List<NegotiationPartyInternal> parties) {

		Map<NegotiationPartyInternal, List<NegotiationPartyInternal>> map = new HashMap<NegotiationPartyInternal, List<NegotiationPartyInternal>>();

		NegotiationPartyInternal mediator = getMediator(parties);

		// all other negotiating parties listen to the mediator
		for (NegotiationPartyInternal party : getNonMediators(parties)) {
			map.put(party, Arrays.asList(mediator));
		}

		// the mediator listens to all other negotiating parties.
		map.put(mediator, getNonMediators(parties));

		return map;
	}

	/**
	 * Returns the last offer for voting as the current agreement. If the
	 * mediator is a {@link negotiator.parties.RandomFlippingMediator}, then we
	 * return the last accepted bid of that mediator
	 *
	 * @param session
	 *            The complete session history up to this point
	 * @return The current agreement
	 */
	@Override
	public Bid getCurrentAgreement(Session session,
			List<NegotiationPartyInternal> parties) {
		NegotiationPartyInternal mediator = session.getRounds().get(0)
				.getTurns().get(0).getParty();

		if (mediator.getParty() instanceof RandomFlippingMediator) {
			return ((RandomFlippingMediator) mediator.getParty())
					.getLastAcceptedBid();
		}

		OfferForVoting offerForVoting = (OfferForVoting) session
				.getMostRecentRound().getActions().get(0);

		return offerForVoting.getBid();
	}
}
