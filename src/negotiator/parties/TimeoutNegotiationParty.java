package negotiator.parties;

import java.util.List;

import negotiator.actions.Accept;
import negotiator.actions.Action;

/**
 * Most basic voting agent implementation I could think of: this agent accepts
 * any offer.
 * <p/>
 * The class was created as part of a series of agents used to understand the
 * api better
 *
 * @author David Festen
 */
public class TimeoutNegotiationParty extends AbstractNegotiationParty {

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

		try {
			Thread.sleep(Long.MAX_VALUE);
		} catch (InterruptedException e) {
			// do nothing
		}

		return new Accept();
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

}
