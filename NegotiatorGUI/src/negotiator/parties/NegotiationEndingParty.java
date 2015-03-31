package negotiator.parties;

import java.util.List;

import negotiator.AgentID;
import negotiator.Deadline;
import negotiator.Timeline;
import negotiator.actions.Action;
import negotiator.actions.EndNegotiation;
import negotiator.actions.Offer;
import negotiator.utility.UtilitySpace;

/**
 * Most basic voting agent implementation I could think of: this agent accepts
 * any offer.
 * <p/>
 * The class was created as part of a series of agents used to understand the
 * api better
 *
 * @author David Festen
 */
public class NegotiationEndingParty extends AbstractNegotiationParty {

	/**
	 * Initializes a new instance of the {@link NegotiationEndingParty} class.
	 *
	 * @param utilitySpace
	 *            The utility space used by this class
	 * @param deadlines
	 *            The deadlines for this session
	 * @param timeline
	 *            The time line (if time deadline) for this session, can be null
	 * @param randomSeed
	 *            The seed that should be used for all randomization (to be
	 *            reproducible)
	 */
	public NegotiationEndingParty(final UtilitySpace utilitySpace,
			final Deadline deadlines, final Timeline timeline,
			final long randomSeed) {
		super(utilitySpace, deadlines, timeline, randomSeed);
	}

	/**
	 * If offer was proposed: Accept offer, otherwise: Propose random offer
	 *
	 * @param possibleActions
	 *            List of all actions possible.
	 * @return Accept or Offer action
	 */
	@Override
	public Action chooseAction(final List<Class> possibleActions) {

		if (possibleActions.contains(EndNegotiation.class)) {
			return new EndNegotiation();
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
