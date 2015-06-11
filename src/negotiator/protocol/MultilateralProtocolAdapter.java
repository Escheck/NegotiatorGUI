package negotiator.protocol;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import negotiator.Bid;
import negotiator.actions.Action;
import negotiator.exceptions.NegotiationPartyTimeoutException;
import negotiator.parties.NegotiationParty;
import negotiator.session.ExecutorWithTimeout;
import negotiator.session.Round;
import negotiator.session.Session;

/**
 * An adapter for the protocol class. This will implement all the methods int
 * the Protocol interface and return default values for them.
 *
 * @author David Festen
 */
public abstract class MultilateralProtocolAdapter implements MultilateralProtocol {

	protected boolean isAborted = false;
	private ExecutorWithTimeout executor;

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
	public Round getRoundStructure(List<NegotiationParty> parties,
			Session session) {
		return new Round();
	}

	/**
	 * This will get called just before the session starts. If some
	 * initialization with needs to be done by the protocol, it can be done
	 * here.
	 *
	 * @param session
	 *            The session instance that will be used for the session
	 * @param parties
	 *            The parties that will participate in the session
	 */
	@Override
	public void beforeSession(Session session, List<NegotiationParty> parties) throws NegotiationPartyTimeoutException, ExecutionException, InterruptedException {

	}

	/**
	 * This will get called just after ending the session. If the protocol needs
	 * to do some post session steps, it can be done here.
	 *
	 * @param session
	 *            The session instance that was used for the session
	 * @param parties
	 *            The parties that participated in the session
	 */
	@Override
	public void afterSession(Session session, List<NegotiationParty> parties) {

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
	public boolean isFinished(Session session, List<NegotiationParty> parties) {
		return isAborted;
	}

	/**
	 * Get a map of parties that are listening to each other's response
	 *
	 * @param parties
	 *            The parties involved in the current negotiation
	 * @return A map where the key is a
	 *         {@link negotiator.parties.NegotiationParty} that is responding to
	 *         a {@link NegotiationParty#chooseAction(java.util.List)} event,
	 *         and the value is a list of {@link NegotiationParty} that are
	 *         listening to that key party's response.
	 */
	@Override
	public Map<NegotiationParty, List<NegotiationParty>> getActionListeners(
			final List<NegotiationParty> parties) {
		return new HashMap<NegotiationParty, List<NegotiationParty>>(0);
	}

	/**
	 * This method should return the current agreement.
	 * <p/>
	 * Some protocols only have an agreement at the negotiation session, make
	 * sure that this method returns null until the end of the session in that
	 * case, because this method might be queried at intermediary steps.
	 *
	 * @param session
	 *            The complete session history up to this point
	 * @return The agreed upon bid or null if no agreement
	 */
	@Override
	public Bid getCurrentAgreement(Session session,
			List<NegotiationParty> parties) {
		return null;
	}

	/**
	 * Gets the number of parties that currently agree to the offer.
	 * <p/>
	 * Default implementation returns 0 if no agreement or number of parties if
	 * agreement exists.
	 *
	 * @param session
	 *            the current state of this session
	 * @param parties
	 *            The parties currently participating
	 * @return the number of parties agreeing to the current agreement
	 */
	@Override
	public int getNumberOfAgreeingParties(Session session,
			List<NegotiationParty> parties) {
		return getCurrentAgreement(session, parties) == null ? 0 : parties
				.size();
	}

	/**
	 * Filters the list by including only the type of negotiation parties.
	 * Optionally, this behavior can be reversed (i.e. excluding only the given
	 * type of negotiation parties).
	 *
	 * @param negotiationParties
	 *            The original list of parties
	 * @param negotiationPartyClass
	 *            The type of parties to include (or exclude if inclusionFilter
	 *            is set to false)
	 * @param inclusionFilter
	 *            If set to true, we include the given type. Otherwise, exclude
	 *            the given type
	 * @return The filtered list of parties
	 */
	private Collection<NegotiationParty> filter(
			Collection<NegotiationParty> negotiationParties,
			Class negotiationPartyClass, boolean inclusionFilter) {
		Collection<NegotiationParty> filtered = new ArrayList<NegotiationParty>(
				negotiationParties.size());

		for (NegotiationParty party : negotiationParties) {
			// if including and class is of the type searching for,
			// or excluding and class is not of the type searching for.
			if ((inclusionFilter && party.getClass().equals(
					negotiationPartyClass))
					|| (!inclusionFilter && !party.getClass().equals(
							negotiationPartyClass))) {
				filtered.add(party);
			}
		}

		return filtered;
	}

	/**
	 * Filters the list by including only the type of negotiation parties.
	 * Optionally, this behavior can be reversed (i.e. excluding only the given
	 * type of negotiation parties).
	 *
	 * @param negotiationParties
	 *            The original list of parties
	 * @param negotiationPartyClass
	 *            The type of parties to include
	 *
	 * @return The filtered list of parties
	 */
	public Collection<NegotiationParty> includeOnly(
			Collection<NegotiationParty> negotiationParties,
			Class negotiationPartyClass) {
		return filter(negotiationParties, negotiationPartyClass, true);
	}

	/**
	 * Filters the list by including only the type of negotiation parties.
	 * Optionally, this behavior can be reversed (i.e. excluding only the given
	 * type of negotiation parties).
	 *
	 * @param negotiationParties
	 *            The original list of parties
	 * @param negotiationPartyClass
	 *            The type of parties to include
	 *
	 * @return The filtered list of parties
	 */
	public Collection<NegotiationParty> exclude(
			Collection<NegotiationParty> negotiationParties,
			Class negotiationPartyClass) {
		return filter(negotiationParties, negotiationPartyClass, false);
	}

	/**
	 * Overwrites the rest of the protocol and sets the protocol state to finish
	 */
	@Override
	public void endNegotiation() {
		System.out.println("Negotiation aborted");
		isAborted = true;
	}

	/**
	 * Overwrites the rest of the protocol and sets the protocol state to finish
	 *
	 * @param reason
	 *            Optionally give a reason why the protocol is finished.
	 */
	@Override
	public void endNegotiation(String reason) {
		System.out.println("Negotiation aborted: " + reason);
		isAborted = true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ExecutorWithTimeout getExecutor() {
		return this.executor;
	}

	/**
	 * {@inheritDoc}
	 * @param executor
	 */
	@Override
	public void setExecutor(ExecutorWithTimeout executor) {
		this.executor = executor;
	}
}
