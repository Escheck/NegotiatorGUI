package negotiator.exceptions;

import negotiator.parties.NegotiationPartyInternal;

/**
 * Exception illustrating that calculating a feature of the bidspace (for
 * example the Nash point) went wrong.
 */
public class NegotiationPartyTimeoutException extends Exception {

	public NegotiationPartyInternal getInstigator() {
		return instigator;
	}

	protected NegotiationPartyInternal instigator;

	public NegotiationPartyTimeoutException(NegotiationPartyInternal instigator) {
		super();
		this.instigator = instigator;
	}

	public NegotiationPartyTimeoutException(
			NegotiationPartyInternal instigator, String message) {
		super(message);
		this.instigator = instigator;
	}

	public NegotiationPartyTimeoutException(
			NegotiationPartyInternal instigator, String message, Throwable cause) {
		super(message, cause);
		this.instigator = instigator;
	}

	public NegotiationPartyTimeoutException(
			NegotiationPartyInternal instigator, Throwable cause) {
		super(cause);
		this.instigator = instigator;
	}
}