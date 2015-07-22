package negotiator.parties;

import java.util.List;

import negotiator.actions.Accept;
import negotiator.actions.Action;
import negotiator.actions.OfferForVoting;
import negotiator.actions.Reject;

/**
 * Basic voting implementation: this agent accepts and rejects offers with a 50%
 * chance.
 * <p/>
 * The class was created as part of a series of agents used to understand the
 * api better
 *
 * @author David Festen
 */
public class RandomFiftyFiftyNegotiationParty extends AbstractNegotiationParty {

	/**
	 * If placing offers: do random offer if voting: accept/reject with a 50%
	 * chance on both
	 *
	 * @param possibleActions
	 *            List of all actions possible.
	 * @return The chosen action
	 */
	@Override
	public Action chooseAction(List<Class> possibleActions) {

		// if we are the first party, place offer.
		if (possibleActions.contains(OfferForVoting.class)) {
			return new OfferForVoting(getPartyId(), generateRandomBid());
		}

		// else do 50/50 accept or reject
		return rand.nextBoolean() ? new Accept() : new Reject();
	}

	/**
	 * Processes action messages received by a given sender.
	 *
	 * @param sender
	 *            The initiator of the action
	 * @param arguments
	 *            The action performed
	 */
	@Override
	public void receiveMessage(Object sender, Action arguments) {
		// not used
	}

}
