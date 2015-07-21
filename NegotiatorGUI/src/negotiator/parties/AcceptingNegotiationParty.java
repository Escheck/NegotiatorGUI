package negotiator.parties;

import java.util.List;

import negotiator.AgentID;
import negotiator.actions.Accept;
import negotiator.actions.Action;
import negotiator.actions.Offer;

/**
 * Most basic voting agent implementation I could think of: this agent accepts
 * any offer.
 * <p/>
 * The class was created as part of a series of agents used to understand the
 * api better
 *
 * @author David Festen
 */
public class AcceptingNegotiationParty extends AbstractNegotiationParty {

	/**
	 * If offer was proposed: Accept offer, otherwise: Propose random offer
	 *
	 * @param possibleActions
	 *            List of all actions possible.
	 * @return Accept or Offer action
	 */
	@Override
	public Action chooseAction(final List<Class> possibleActions) {

		System.out.println("getNumberOfParties() = " + getNumberOfParties());

		if (possibleActions.contains(Accept.class)) {
			return new Accept();
		} else {
			return new Offer(generateRandomBid());
		}
	}

	/**
	 * We ignore any messages received.
	 *
	 * @param sender
	 *            The initiator of the action
	 * @param arguments
	 *            The action performed
	 */
	@Override
	public void receiveMessage(final Object sender, final Action arguments) {
		super.receiveMessage(sender, arguments);
	}

	/**
	 * Gets the agent's unique id
	 * <p/>
	 * Each agent should contain a (unique) id. This id is used in log files to
	 * trace the agent's behavior. For all default implementations, this has
	 * either the format "ClassName" if only one such an agent exists (in case
	 * of mediator for example) or it has the format "ClassName@HashCode" if
	 * multiple agents of the same type can exists. You could also use the
	 * random hash used in the agent to identify it (making it easier to
	 * reproduce results).
	 *
	 * @return A uniquely identifying agent id
	 */
	@Override
	public AgentID getPartyId() {
		return partyId == null ? new AgentID("AcceptingParty@" + hashCode())
				: partyId;
	}
}
