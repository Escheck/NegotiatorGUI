package negotiator.parties;

import java.util.List;

import negotiator.AgentID;
import negotiator.Deadline;
import negotiator.actions.Action;
import negotiator.actions.EndNegotiation;
import negotiator.actions.Offer;
import negotiator.session.TimeLineInfo;
import negotiator.utility.AdditiveUtilitySpace;

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
	@Override
	public void init(final AdditiveUtilitySpace utilitySpace, final Deadline deadlines,
			final TimeLineInfo timeline, final long randomSeed, AgentID id) {
		super.init(utilitySpace, deadlines, timeline, randomSeed, id);
	}

	/**
	 * If offer was proposed: Accept offer, otherwise: Propose random offer
	 *
	 * @param possibleActions
	 *            List of all actions possible.
	 * @return Accept or Offer action
	 */
	@Override
	public Action chooseAction(
			final List<Class<? extends Action>> possibleActions) {

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
	public void receiveMessage(final AgentID sender, final Action arguments) {
		super.receiveMessage(sender, arguments);
	}

}
