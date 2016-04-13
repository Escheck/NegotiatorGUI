package negotiator.session;

import java.util.ArrayList;
import java.util.List;

import negotiator.Bid;
import negotiator.MultipartyNegotiationEventListener;
import negotiator.actions.Action;
import negotiator.actions.Offer;
import negotiator.events.LogMessageEvent;
import negotiator.events.MultipartyNegotiationOfferEvent;
import negotiator.events.MultipartyNegotiationSessionEvent;
import negotiator.parties.NegotiationPartyInternal;
import negotiator.protocol.MediatorProtocol;

/**
 * Logger class for the command line interface displayed in the gui during
 * multilateral negotiation sessions.
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
	public void logMessage(String message, Object... params) {
		LogMessageEvent event = new LogMessageEvent(owner, "Nego",
				String.format(message, params));
		for (MultipartyNegotiationEventListener listener : listeners)
			listener.handleEvent(event);
	}

	/**
	 * Log the sessions most recent action as an offer. Called after every
	 * offer.
	 *
	 * @param session
	 *            The session to get the most recent action out of
	 * @param parties
	 *            The parties involved in the offer
	 * @param agree
	 *            flag indicating whether the offer is an agreement or not
	 */
	public void logBid(Session session, List<NegotiationPartyInternal> parties,
			boolean agree) {
		Action action = session.getMostRecentAction();

		if (action instanceof Offer) {
			Bid bid = ((Offer) action).getBid();
			int round = session.getRoundNumber();
			int turn = session.getTurnNumber();
			double time = session.getRuntimeInSeconds();
			offered(parties, agree, bid, round, turn, time);
		}
	}

	/**
	 * some offer was placed. Make event containing the details.
	 * 
	 * @param parties
	 *            The parties involved in the offer
	 * @param agree
	 *            flag indicating whether the offer is an agreement or not
	 * @param bid
	 * @param round
	 * @param turn
	 * @param time
	 */
	public void offered(List<NegotiationPartyInternal> parties, boolean agree,
			Bid bid, int round, int turn, double time) {
		ArrayList<Double> utils = new ArrayList<Double>();
		for (NegotiationPartyInternal party : MediatorProtocol
				.getNonMediators(parties))
			utils.add(party.getUtilityWithDiscount(bid));
		MultipartyNegotiationOfferEvent event = new MultipartyNegotiationOfferEvent(
				owner, bid, round, turn, time, utils, agree);

		for (MultipartyNegotiationEventListener listener : listeners)
			listener.handleEvent(event);
	}

	/**
	 * General session log. Called at start and end of each negotiation session
	 *
	 * @param session
	 *            The session to log
	 * @param agreement
	 *            The agreement if any (NULL otherwise)
	 */
	public void logSession(Session session, Bid agreement) {
		for (MultipartyNegotiationEventListener listener : listeners)
			listener.handleEvent(new MultipartyNegotiationSessionEvent(owner,
					session, agreement));
	}

}
