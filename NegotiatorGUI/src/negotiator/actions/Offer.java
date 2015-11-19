package negotiator.actions;

import negotiator.Agent;
import negotiator.AgentID;
import negotiator.Bid;

/**
 * Class which symbolizes an offer of an agent for the opponent.
 * 
 * @author Tim Baarslag and Dmytro Tykhonov
 */
public class Offer extends Action {

	/** Bid to be offered to the opponent. */
	protected Bid bid;

	/**
	 * Creates an action symbolizing an offer for the opponent.
	 * 
	 * @param agentID
	 *            id of the agent which created the offer.
	 * @param bid
	 *            for the opponent.
	 */
	public Offer(AgentID agentID, Bid bid) {
		super(agentID);
		if (bid == null) {
			throw new NullPointerException("offer with null bid");
		}
		this.bid = bid;
	}

	/**
	 * Creates an action symbolizing an offer for the opponent.
	 * 
	 * @param bid
	 *            for the opponent.
	 */
	public Offer(Bid bid) {
		if (bid == null) {
			throw new NullPointerException("offer with null bid");
		}
		this.bid = bid;
	}

	/**
	 * @return hashcode of this object.
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((bid == null) ? 0 : bid.hashCode());
		return result;
	}

	/**
	 * @param obj
	 *            object to which this object is compared.
	 * @return true if this object is equal to the given object.
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Offer other = (Offer) obj;
		if (bid == null) {
			if (other.bid != null)
				return false;
		} else if (!bid.equals(other.bid))
			return false;
		return true;
	}

	/**
	 * Creates an action symbolizing an offer to the opponent.
	 * 
	 * @param agent
	 *            which created this offer.
	 * @param bid
	 *            to be offered to the opponent.
	 */
	public Offer(Agent agent, Bid bid) {
		this(agent.getAgentID(), bid);
	}

	/**
	 * Returns the bid offered by the agent which created this offer.
	 * 
	 * @return bid to offer.
	 */
	public Bid getBid() {
		return bid;
	}

	/**
	 * @return string representation of action: "(Offer: BID)".
	 */
	public String toString() {
		return "(Offer: " + (bid == null ? "null" : bid.toString()) + ")";
	}
}