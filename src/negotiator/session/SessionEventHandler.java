package negotiator.session;

import java.util.ArrayList;
import java.util.List;

import negotiator.Bid;
import negotiator.MultipartyNegotiationEventListener;
import negotiator.events.LogMessageEvent;
import negotiator.events.MultipartyNegotiationOfferEvent;
import negotiator.events.MultipartySessionEndedEvent;
import negotiator.parties.NegotiationPartyInternal;
import negotiator.protocol.MediatorProtocol;

/**
 * Reports {@link SessionManager} events to listeners.
 */
public class SessionEventHandler {
	// private list of all events this logger listens to
	private List<MultipartyNegotiationEventListener> listeners;

	// 'owner' of this logger: creating class. Used for reference in logging
	// messages
	private Object owner;

	/**
	 * Command line interface
	 *
	 * @param owner
	 *            The object creating this class. Used for reference in log
	 *            messages
	 */
	public SessionEventHandler(Object owner) {
		this.listeners = new ArrayList<MultipartyNegotiationEventListener>();
		this.owner = owner;
	}

	/**
	 * Add a listener to this logger
	 *
	 * @param listener
	 *            The class that should receive messages from this logger
	 */
	public void addListener(MultipartyNegotiationEventListener listener) {
		listeners.add(listener);
	}

	/**
	 * Logs a string message
	 *
	 * Usage: logMessage("This is %d %s message", 1, "format string") -> logs
	 * message: "This is 1 format string message"
	 *
	 * @param message
	 *            The message to log, can be a format string
	 * @param params
	 *            format arguments in case message is a format string.
	 *
	 */
	@Deprecated
	public void logMessage(String message, Object... params) {
		LogMessageEvent event = new LogMessageEvent(owner, "Nego",
				String.format(message, params));
		for (MultipartyNegotiationEventListener listener : listeners)
			listener.handleEvent(event);
	}

	/**
	 * some offer was placed. Broadcast {@link MultipartyNegotiationOfferEvent}
	 * containing the details.
	 * 
	 * @param parties
	 *            The parties involved in the offer
	 * @param bid
	 *            the bid that was offered
	 * @param agreed
	 *            the agreement bid, or null if no agreement reached yet.
	 * @param session
	 *            the session
	 */
	public void offered(List<NegotiationPartyInternal> parties, Bid bid,
			Bid agreed, Session session) {
		MultipartyNegotiationOfferEvent event = new MultipartyNegotiationOfferEvent(
				owner, bid, session.getRoundNumber(), session.getTurnNumber(),
				session.getRuntimeInSeconds(), getUtils(bid, parties),
				getUtils(agreed, parties));

		for (MultipartyNegotiationEventListener listener : listeners)
			listener.handleEvent(event);
	}

	/**
	 * Make list of utilities for a bid, one for each party.
	 * 
	 * @param bid
	 *            the {@link Bid} to be evaluated
	 * @param parties
	 *            list of {@link NegotiationPartyInternal} involved.
	 * @return List of utilities, in the order of the parties.
	 */
	private List<Double> getUtils(Bid bid,
			List<NegotiationPartyInternal> parties) {
		List<Double> utils = new ArrayList<Double>();
		for (NegotiationPartyInternal party : MediatorProtocol
				.getNonMediators(parties))
			utils.add(party.getUtilityWithDiscount(bid));
		return utils;
	}

	/**
	 * General session log. Called at start and end of each negotiation session
	 *
	 * @param session
	 *            The session to log
	 * @param agreement
	 *            The agreement if any (NULL otherwise)
	 */
	public void logSessionEnded(Session session, Bid agreement,
			List<NegotiationPartyInternal> parties) {
		MultipartySessionEndedEvent evt = new MultipartySessionEndedEvent(
				owner, session, agreement, parties);
		for (MultipartyNegotiationEventListener listener : listeners)
			listener.handleEvent(evt);
	}

}
