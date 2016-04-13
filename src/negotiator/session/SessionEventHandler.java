package negotiator.session;

import java.util.ArrayList;
import java.util.List;

import negotiator.Bid;
import negotiator.MultipartyNegotiationEventListener;
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
	public void offered(List<NegotiationPartyInternal> parties, Bid bid,
			boolean agreed, Session session) {
		ArrayList<Double> utils = new ArrayList<Double>();
		for (NegotiationPartyInternal party : MediatorProtocol
				.getNonMediators(parties))
			utils.add(party.getUtilityWithDiscount(bid));
		MultipartyNegotiationOfferEvent event = new MultipartyNegotiationOfferEvent(
				owner, bid, session.getRoundNumber(), session.getTurnNumber(),
				session.getRuntimeInSeconds(), utils, agreed);

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
