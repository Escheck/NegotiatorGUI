package negotiator.session;

import negotiator.parties.NegotiationPartyInternal;

/**
 * Error that will be thrown when an action that is not valid for the given
 * round. At this moment (September 2014) The
 * {@link negotiator.session.SessionManager} detects these invalid actions and
 * throws this error.
 */
@SuppressWarnings("serial")
public class InvalidActionError extends Exception {
	/**
	 * Holds the party that did an invalid action
	 */
	private final NegotiationPartyInternal instigator;

	/**
	 * Initializes a new instance of the {@link InvalidActionError} class.
	 *
	 * @param instigator
	 *            The party that did an invalid action.
	 */
	public InvalidActionError(NegotiationPartyInternal instigator) {
		this.instigator = instigator;
	}

	/**
	 * Gets the party that did an invalid action
	 *
	 * @return The party that did an invalid action.
	 */
	@SuppressWarnings("UnusedDeclaration")
	// might be used in future
	public NegotiationPartyInternal getInstigator() {
		return instigator;
	}

	public String toString() {
		return "Invalid action by " + instigator;
	}
}