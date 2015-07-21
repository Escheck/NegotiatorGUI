package negotiator.protocol;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import negotiator.Bid;
import negotiator.actions.Action;
import negotiator.actions.EndNegotiationWithAnOffer;
import negotiator.actions.GiveFeedback;
import negotiator.actions.Inform;
import negotiator.actions.InformVotingResult;
import negotiator.actions.NoAction;
import negotiator.actions.OfferForFeedback;
import negotiator.actions.OfferForVoting;
import negotiator.actions.VoteForOfferAcceptance;
import negotiator.parties.FeedbackMediator;
import negotiator.parties.NegotiationPartyInternal;
import negotiator.session.Round;
import negotiator.session.Session;
import negotiator.session.Turn;

/**
 * Implementation of a mediator based protocol based on feedback. </p> Protocol:
 * 
 * <pre>
 * Mediator proposes an offer
 * Agents give feedback
 * Mediator Informs parties of result
 * </pre>
 *
 * @author David Festen
 * @author Reyhan
 */
public class MediatorFeedbackProtocol extends MediatorProtocol {
	/**
	 * holds true if this protocol is finished
	 */
	private boolean isFinished;

	/**
	 * Get the structure of the current round. Each round, this method receives
	 * a list of all the {@link negotiator.parties.NegotiationParty} and the
	 * complete {@link negotiator.session.Session} which can be used to
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
		round.addTurn(new Turn(mediator, OfferForFeedback.class,
				OfferForVoting.class, EndNegotiationWithAnOffer.class));

		// other parties' turn
		for (NegotiationPartyInternal otherParty : otherParties)
			round.addTurn(new Turn(otherParty, GiveFeedback.class,
					VoteForOfferAcceptance.class));

		// mediator finishing turn
		round.addTurn(new Turn(mediator, InformVotingResult.class,
				NoAction.class));

		// return new round structure
		return round;
	}

	/**
	 * Apply the action according to the protocol. All actions done by all
	 * agents come through this method. If protocol needs to adapt anything
	 * according to actions, it can be handled here.
	 *
	 * @param action
	 *            action to apply
	 * @param session
	 *            the current state of this session
	 */
	@Override
	public void applyAction(Action action, Session session) {
		if (action instanceof EndNegotiationWithAnOffer)
			isFinished = true;
	}

	/**
	 * Before each session we'd like to inform the mediator of the number of
	 * parties
	 *
	 * @param session
	 *            The session instance that will be used for the session
	 * @param parties
	 *            The parties that will participate in the session
	 */
	@Override
	public void beforeSession(Session session,
			List<NegotiationPartyInternal> parties) {
		Inform inform = new Inform().setName("numParties").setValue(
				parties.size());
		getMediator(parties).getParty().receiveMessage(this, inform);
	}

	/**
	 * Returns the most recent agreement found by the mediator. assumption:
	 * mediator is a {@link FeedbackMediator}
	 *
	 * @param session
	 *            The complete session history up to this point
	 * @return The agreed upon bid or null if no agreement
	 */
	@Override
	public Bid getCurrentAgreement(Session session,
			List<NegotiationPartyInternal> parties) {
		NegotiationPartyInternal firstParty = session.getMostRecentRound()
				.getTurns().get(0).getParty();
		if (firstParty.getParty() instanceof FeedbackMediator)
			return ((FeedbackMediator) (firstParty.getParty()))
					.getLastAcceptedBid();
		else
			return null;
	}

	/**
	 * Check if the protocol is done or still busy. If this method returns true,
	 * the {@link negotiator.session.SessionManager} will not start a new
	 * {@link negotiator.session.Round} after the current one. It will however
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
		return isFinished;
	}

	/**
	 * Get a map of parties that are listening to each other's response
	 *
	 * @param parties
	 *            The parties currently participating
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
		Map<NegotiationPartyInternal, List<NegotiationPartyInternal>> map = new HashMap<NegotiationPartyInternal, List<NegotiationPartyInternal>>();
		NegotiationPartyInternal mediator = getMediator(parties);

		// all other negotiating parties listen to the mediator
		for (NegotiationPartyInternal party : getNonMediators(parties))
			map.put(party, Arrays.asList(mediator));

		// the mediator listens to all other negotiating parties.
		map.put(mediator, getNonMediators(parties));

		return map;
	}
}
