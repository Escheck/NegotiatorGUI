package negotiator.parties;

import java.util.List;

import negotiator.AgentID;
import negotiator.Deadline;
import negotiator.Vote;
import negotiator.actions.Action;
import negotiator.actions.InformVotingResult;
import negotiator.actions.OfferForVoting;
import negotiator.actions.VoteForOfferAcceptance;
import negotiator.protocol.MultilateralProtocol;
import negotiator.session.TimeLineInfo;
import negotiator.utility.UtilitySpace;

/**
 * Implementation of a party that uses hill climbing strategy to get to an
 * agreement.
 * <p/>
 * This party should be run with {@link negotiator.protocol.MediatorProtocol}
 *
 * @author David Festen
 * @author Reyhan
 */
public class HillClimber extends AbstractNegotiationParty {

	private double lastAcceptedBidUtility;

	private double lastReceivedBidUtility;

	private Vote currentVote;

	/**
	 * Initializes a new instance of the {@link HillClimber} class.
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
	public void init(UtilitySpace utilitySpace, Deadline deadlines,
			TimeLineInfo timeline, long randomSeed, AgentID id) {
		super.init(utilitySpace, deadlines, timeline, randomSeed, id);
		lastAcceptedBidUtility = 0;
		lastReceivedBidUtility = 0;
		currentVote = Vote.REJECT;
	}

	/**
	 * When this class is called, it is expected that the Party chooses one of
	 * the actions from the possible action list and returns an instance of the
	 * chosen action. This class is only called if this {@link NegotiationParty}
	 * is in the
	 * {@link negotiator.protocol .Protocol#getRoundStructure(java.util.List, negotiator.session.Session)}
	 * .
	 *
	 * @param possibleActions
	 *            List of all actions possible.
	 * @return The chosen action
	 */
	@Override
	public Action chooseAction(List<Class> possibleActions) {
		return new VoteForOfferAcceptance(getPartyId(), currentVote);
	}

	/**
	 * This method is called when an observable action is performed. Observable
	 * actions are defined in
	 * {@link MultilateralProtocol#getActionListeners(java.util.List)}
	 *
	 * @param sender
	 *            The initiator of the action
	 * @param arguments
	 *            The action performed
	 */
	@Override
	public void receiveMessage(Object sender, Action arguments) {
		if (arguments instanceof OfferForVoting) {
			lastReceivedBidUtility = getUtility(((OfferForVoting) arguments)
					.getBid());
			double reservationValue = (timeline == null) ? utilitySpace
					.getReservationValue() : utilitySpace
					.getReservationValueWithDiscount(timeline);

			if (lastReceivedBidUtility < reservationValue) {
				currentVote = Vote.REJECT;
			} else {
				currentVote = lastReceivedBidUtility >= lastAcceptedBidUtility ? Vote.ACCEPT
						: Vote.REJECT;
			}
		} else if (arguments instanceof InformVotingResult) {
			if (((InformVotingResult) arguments).getVotingResult() == Vote.ACCEPT) {
				lastAcceptedBidUtility = lastReceivedBidUtility;
			}
		}
	}

}
