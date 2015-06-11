package negotiator.parties;

import java.util.ArrayList;
import java.util.List;

import negotiator.AgentID;
import negotiator.Bid;
import negotiator.Deadline;
import negotiator.Timeline;
import negotiator.Vote;
import negotiator.actions.Action;
import negotiator.actions.InformVotingResult;
import negotiator.actions.OfferForVoting;
import negotiator.actions.VoteForOfferAcceptance;
import negotiator.issue.Issue;
import negotiator.issue.Value;
import negotiator.protocol.MultilateralProtocol;
import negotiator.utility.UtilitySpace;

/**
 * Mediator that randomly flips one issue of the current offer to generate a new
 * offer.
 * <p/>
 * This class was adapted from Reyhan's parties.PureRandomFlippingMediator class
 * (see svn history for details about that class), and adapted to fit into the
 * new framework.
 */
public class RandomFlippingMediator extends Mediator {

	/**
	 * Holds whether the current offer is acceptable by all parties.
	 */
	private boolean isAcceptable;

	/**
	 * The most recently accepted bid.
	 */
	private Bid lastAcceptedBid;

	/**
	 * The most recently proposed bid.
	 */
	private Bid lastProposedBid;

	/**
	 * Initializes a new instance of the {@link RandomFlippingMediator} class.
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
	public RandomFlippingMediator(UtilitySpace utilitySpace,
			Deadline deadlines, Timeline timeline, long randomSeed) {
		super(utilitySpace, deadlines, timeline, randomSeed);
		isAcceptable = true;
	}

	/**
	 * When this class is called, it is expected that the Party chooses one of
	 * the actions from the possible action list and returns an instance of the
	 * chosen action. This class is only called if this {@link NegotiationParty}
	 * is in the
	 * {@link MultilateralProtocol#getRoundStructure(java.util.List, negotiator.session.Session)}
	 * .
	 *
	 * @param possibleActions
	 *            List of all actions possible.
	 * @return The chosen action
	 */
	@Override
	public Action chooseAction(List<Class> possibleActions) {
		AgentID mediatorId = new AgentID("Mediator");

		if (possibleActions.contains(OfferForVoting.class)) {
			// check if last proposition was acceptable
			if (isAcceptable) {
				lastAcceptedBid = lastProposedBid;
			}

			// init acceptable to true for next round
			isAcceptable = true;

			if (lastAcceptedBid == null) {
				lastProposedBid = generateRandomBid();
				return new OfferForVoting(mediatorId, lastProposedBid);
			} else {
				lastProposedBid = modifyLastBidRandomly();
				return new OfferForVoting(mediatorId, lastProposedBid);
			}
		} else if (possibleActions.contains(InformVotingResult.class)) {
			return new InformVotingResult(mediatorId,
					isAcceptable ? Vote.ACCEPT : Vote.REJECT);
		} else {
			return null;
		}
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
		if (arguments instanceof VoteForOfferAcceptance) {
			isAcceptable &= ((VoteForOfferAcceptance) arguments).getVote() == Vote.ACCEPT;
		}
	}

	public Bid getLastAcceptedBid() {
		return lastAcceptedBid;
	}

	/**
	 * modifies the most recently accepted bid by changing a single issue value.
	 *
	 * @return the modified bid
	 */
	private Bid modifyLastBidRandomly() {
		try {
			ArrayList<Issue> issues = utilitySpace.getDomain().getIssues();
			Bid modifiedBid = new Bid(lastAcceptedBid);
			Value newValue;
			Issue currentIssue;

			int currentIndex;
			do {
				currentIssue = issues.get(rand.nextInt(issues.size()));
				currentIndex = currentIssue.getNumber();
				newValue = getRandomValue(currentIssue);
			} while (newValue.equals(lastAcceptedBid.getValue(currentIndex)));

			modifiedBid.setValue(currentIndex, newValue);

			return modifiedBid;
		} catch (Exception e) {
			System.out
					.println("Can not generate random bid or receiveMessage preference list "
							+ "problem:" + e.getMessage());
			return null;
		}
	}
}
