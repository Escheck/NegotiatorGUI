package negotiator.parties;

import java.util.HashMap;
import java.util.Random;

import negotiator.AgentID;
import negotiator.Bid;
import negotiator.Deadline;
import negotiator.Timeline;
import negotiator.actions.Action;
import negotiator.actions.Inform;
import negotiator.issue.Issue;
import negotiator.issue.IssueDiscrete;
import negotiator.issue.IssueInteger;
import negotiator.issue.IssueReal;
import negotiator.issue.Value;
import negotiator.issue.ValueInteger;
import negotiator.issue.ValueReal;
import negotiator.utility.UtilitySpace;

/**
 * A basic implementation of the {@link NegotiationParty} interface. This basic
 * implementation sets up some common variables for you.
 *
 * @author David Festen
 * @author Reyhan (The random bid generator)
 */
public abstract class AbstractNegotiationParty implements NegotiationParty {

	/**
	 * Time line used by the party if time deadline is set.
	 */
	protected Timeline timeline;

	/**
	 * map of all deadlines set for this party. Deadlines are combined using
	 * "or". So for example if we have Time and Round deadline, it means that
	 * the deadline is reached when either time or round will have reached it's
	 * deadline
	 */
	protected final Deadline deadlines;

	/**
	 * Random seed used by this party.
	 */
	protected final Random rand;

	/**
	 * utility space used by this party (set in constructor).
	 */
	protected final UtilitySpace utilitySpace;

	/**
	 * The id used to identify this agent (if set to null, a default identifier
	 * will be used).
	 */
	protected AgentID partyId;

	/**
	 * Initializes a new instance of the {@link NegotiationParty} class.
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
	public AbstractNegotiationParty(UtilitySpace utilitySpace,
			Deadline deadlines, Timeline timeline, long randomSeed) {
		this.utilitySpace = utilitySpace;
		this.rand = new Random(randomSeed);
		this.timeline = timeline;
		this.deadlines = deadlines;
	}

	/**
	 * Generates a random bid which will be generated using this.utilitySpace.
	 *
	 * @return A random bid
	 */
	protected Bid generateRandomBid() {
		try {
			// Pairs <issue number, chosen value string>
			HashMap<Integer, Value> values = new HashMap<Integer, Value>();

			// For each issue, put a random value
			for (Issue currentIssue : utilitySpace.getDomain().getIssues()) {
				values.put(currentIssue.getNumber(),
						getRandomValue(currentIssue));
			}

			// return the generated bid
			return new Bid(utilitySpace.getDomain(), values);

		} catch (Exception e) {

			// return empty bid if an error occurred
			return new Bid();
		}
	}

	/**
	 * Gets a random value for the given issue.
	 *
	 * @param currentIssue
	 *            The issue to generate a random value for
	 * @return The random value generated for the issue
	 * @throws Exception
	 *             if the issues type is not Discrete, Real or Integer.
	 */
	protected Value getRandomValue(Issue currentIssue) throws Exception {

		Value currentValue;
		int index;

		switch (currentIssue.getType()) {
		case DISCRETE:
			IssueDiscrete discreteIssue = (IssueDiscrete) currentIssue;
			index = (rand.nextInt(discreteIssue.getNumberOfValues()));
			currentValue = discreteIssue.getValue(index);
			break;
		case REAL:
			IssueReal realIss = (IssueReal) currentIssue;
			index = rand.nextInt(realIss.getNumberOfDiscretizationSteps()); // check
																			// this!
			currentValue = new ValueReal(
					realIss.getLowerBound()
							+ (((realIss.getUpperBound() - realIss
									.getLowerBound())) / (realIss
									.getNumberOfDiscretizationSteps())) * index);
			break;
		case INTEGER:
			IssueInteger integerIssue = (IssueInteger) currentIssue;
			index = rand.nextInt(integerIssue.getUpperBound()
					- integerIssue.getLowerBound() + 1);
			currentValue = new ValueInteger(integerIssue.getLowerBound()
					+ index);
			break;
		default:
			throw new Exception("issue type " + currentIssue.getType()
					+ " not supported");
		}

		return currentValue;
	}

	/**
	 * Gets the utility for the given bid.
	 *
	 * @param bid
	 *            The bid to get the utility for
	 * @return A double value between [0, 1] (inclusive) that represents the
	 *         bids utility
	 */
	@Override
	public double getUtility(Bid bid) {
		try {
			// throws exception if bid incomplete or not in utility space
			return bid == null ? 0 : utilitySpace.getUtility(bid);
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}

	/**
	 * Gets the time discounted utility for the given bid.
	 *
	 * @param bid
	 *            The bid to get the utility for
	 * @return A double value between [0, 1] (inclusive) that represents the
	 *         bids utility
	 */
	@Override
	public double getUtilityWithDiscount(Bid bid) {
		if (bid == null) {
			// utility is null if no bid
			return 0;
		} else if (timeline == null) {
			// return undiscounted utility if no timeline given
			return getUtility(bid);
		} else {
			// otherwise, return discounted utility
			return utilitySpace.getUtilityWithDiscount(bid, timeline);
		}
	}

	/**
	 * Gets this agent's utility space.
	 *
	 * @return The utility space
	 */
	@Override
	public final UtilitySpace getUtilitySpace() {
		return utilitySpace;
	}

	/**
	 * Gets this agent's time line.
	 *
	 * @return The time line for this agent
	 */
	@Override
	public Timeline getTimeLine() {
		return timeline;
	}

	/**
	 * Sets this agent's time line
	 *
	 * @param timeline
	 *            The timeline to set
	 */
	@Override
	public void setTimeLine(Timeline timeline) {
		this.timeline = timeline;
	}

	/**
	 * Returns a human readable string representation of this party.
	 *
	 * @return the string representation of party id
	 */
	@Override
	public String toString() {
		return getPartyId().toString();
	}

	/**
	 * Gets the party id for this party
	 *
	 * @return The uniquely identifying party id.
	 */
	@Override
	public AgentID getPartyId() {
		return partyId == null ? new AgentID("" + getClass() + "@" + hashCode())
				: partyId;
	}

	/**
	 * Sets the party id
	 *
	 * @param partyId
	 *            The uniquely identifying party id.
	 */
	@Override
	public void setPartyId(AgentID partyId) {
		this.partyId = partyId;
	}

	@Override
	public void receiveMessage(Object sender, Action arguments) {
		if (arguments instanceof Inform) {
			numberOfParties = (Integer) ((Inform) arguments).getValue();
		}
	}

	public int getNumberOfParties() {
		if (numberOfParties == -1) {
			System.out
					.println("Make sure that you call the super class in receiveMessage() method.");
		}
		return numberOfParties;
	}

	private int numberOfParties = -1;

}
