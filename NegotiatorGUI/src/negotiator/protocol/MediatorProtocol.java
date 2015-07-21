package negotiator.protocol;

import java.util.ArrayList;
import java.util.List;

import negotiator.parties.Mediator;
import negotiator.parties.NegotiationPartyInternal;

/**
 * Base class for all mediator-based protocol
 * <p/>
 * Initial implementations of this {@link MediatorProtocol} are:
 * {@link SimpleMediatorBasedProtocol}
 * {@link negotiator.protocol.MediatorFeedbackProtocol}
 *
 * @author David Festen
 */
public abstract class MediatorProtocol extends MultilateralProtocolAdapter {

	/**
	 * Returns the first mediator from a list of parties
	 *
	 * @param parties
	 *            The list of parties to find the mediator in
	 * @return The first mediator in the given list, or null if no such entity
	 *         exists.
	 */
	public static NegotiationPartyInternal getMediator(
			List<NegotiationPartyInternal> parties) {

		// Mediator should be first item, but this should find it wherever it
		// is.
		for (NegotiationPartyInternal party : parties) {
			if (party instanceof Mediator) {
				return party;
			}
		}

		// default case, no mediator found, return null
		return null;
	}

	/**
	 * Get the non-mediator parties. assumption: there are only zero or one
	 * mediators in the list
	 *
	 * @param parties
	 *            The list of parties with mediator
	 * @return The given without the mediators
	 */
	public static List<NegotiationPartyInternal> getNonMediators(
			List<NegotiationPartyInternal> parties) {

		List<NegotiationPartyInternal> nonMediators = new ArrayList<NegotiationPartyInternal>(
				parties);
		nonMediators.remove(getMediator(parties));
		return nonMediators;
	}
}
