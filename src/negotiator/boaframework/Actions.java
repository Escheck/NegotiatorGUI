package negotiator.boaframework;

/**
 * Possible actions of an acceptance strategy.
 * 
 * @author Mark Hendrikx
 */
public enum Actions {
	/** Accept the opponent's offer. */
	Accept,
	/**
	 * Reject the opponent's offer. From BOAagent:
	 * "if agent does not accept, it offers the counter bid"
	 */
	Reject,
	/** Walk away from the negotiation. */
	Break;
}