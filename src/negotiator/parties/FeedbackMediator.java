package negotiator.parties;

import java.util.ArrayList;
import java.util.List;

import negotiator.AgentID;
import negotiator.Bid;
import negotiator.Deadline;
import negotiator.Feedback;
import negotiator.Vote;
import negotiator.actions.Action;
import negotiator.actions.EndNegotiationWithAnOffer;
import negotiator.actions.GiveFeedback;
import negotiator.actions.Inform;
import negotiator.actions.NoAction;
import negotiator.actions.OfferForFeedback;
import negotiator.issue.Issue;
import negotiator.issue.Value;
import negotiator.parties.partialopponentmodel.PartialPreferenceModels;
import negotiator.protocol.MultilateralProtocol;
import negotiator.session.TimeLineInfo;
import negotiator.utility.UtilitySpace;

/**
 * Implementation of a mediator that uses feedback to make a (partial)
 * preference graph of the participating parties offers.
 * <p/>
 * This class was adapted from Reyhan's parties.SmartMediatorOnlyFeedback class
 * (see svn history for details about that class), and adapted to fit into the
 * new framework.
 *
 * @author David Festen
 * @author Reyhan (Orignal code)
 */
public class FeedbackMediator extends AbstractNegotiationParty implements
		Mediator {

	private ArrayList<Feedback> currentFeedbackList;
	/**
	 * The current bid
	 */
	private Bid currentBid;
	/**
	 * The bid that is offered in the previous round
	 */
	private Bid lastBid;
	/**
	 * Keeping the last accepted bid by all parties
	 */
	private Bid lastAcceptedBid;
	/**
	 * The model is learned by the mediator during the negotiation
	 */
	private int roundNumber;
	private int numListeners;
	private int currentIndex;
	private int lastAcceptedRoundNumber;
	private Vote isAcceptable;
	private PartialPreferenceModels preferenceList;

	/**
	 * Initializes a new instance of the {@link negotiator.Party} class.
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
		lastAcceptedBid = null;
		currentBid = null;
		lastBid = null;
		currentFeedbackList = new ArrayList<Feedback>();
		isAcceptable = Vote.ACCEPT;
	}

	public Bid getLastAcceptedBid() {
		return lastAcceptedBid;
	}

	/**
	 * When this class is called, it is expected that the Party chooses one of
	 * the actions from the possible action list and returns an instance of the
	 * chosen action. This class is only called if this {@link negotiator.Party}
	 * is in the
	 * {@link negotiator.protocol .Protocol#getRoundStructure(java.util.List, negotiator.session.Session)}
	 * .
	 *
	 * @param possibleActions
	 *            List of all actions possible.
	 * @return The chosen action
	 */
	@Override
	public Action chooseAction(List<Class<? extends Action>> possibleActions) {
		if (possibleActions.contains(OfferForFeedback.class))
			roundNumber++;

		// left out deadline related code

		try {

			if (possibleActions.contains(NoAction.class)) {

				isAcceptable = Feedback.isAcceptable(currentFeedbackList);

				if (isAcceptable == Vote.ACCEPT) {
					lastAcceptedBid = new Bid(currentBid);
					lastAcceptedRoundNumber = roundNumber;
				}

				return new NoAction();

			} else if (roundNumber == 1) { // initially generate a random bid
											// and create the
				// preference model
				currentBid = generateRandomBid();
				lastBid = new Bid(currentBid);
				preferenceList = new PartialPreferenceModels(
						new Bid(currentBid), numListeners);
				return (new OfferForFeedback(getPartyId(), currentBid));
				// when we stop searching part and start voting, inform the
				// agents about the last
				// accepted bid by all
			} else {

				if (roundNumber > 2) {

					preferenceList.updateIssuePreferenceList(currentIndex,
							lastBid.getValue(currentIndex),
							currentBid.getValue(currentIndex),
							currentFeedbackList);
					lastBid = new Bid(currentBid);
				}

				currentFeedbackList.clear();
				currentBid = modifyLastBid();

				if (currentBid == null) { // if we are not able to generate
											// bids;
					System.out.println("Last Accepted Round Number:"
							+ lastAcceptedRoundNumber);
					System.out.println(preferenceList.toString());
					return (new EndNegotiationWithAnOffer(getPartyId(),
							lastAcceptedBid));
				}

			}
		} catch (Exception e) {
			System.out.println("" + roundNumber
					+ " :Cannot generate random bid or update "
					+ "preference list problem:" + e.getMessage());

		}

		return (new OfferForFeedback(getPartyId(), currentBid));
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
		if (arguments instanceof GiveFeedback)
			currentFeedbackList.add(((GiveFeedback) arguments).getFeedback());

		else if (arguments instanceof Inform) {
			Inform inform = (Inform) arguments;
			if (inform.getName().equals("numParties"))
				numListeners = (Integer) inform.getValue();
		}

	}

	private Bid modifyLastBid() throws Exception {

		Bid modifiedBid = new Bid(lastBid);

		// double epsilon = (double) (getTotalRoundOrTime() - getRound() - 1) /
		// getTotalRoundOrTime();
		double epsilon = getNormalizedRound();
		Value selectedValue = null;

		// if ((getRound() <= ((double) getTotalRoundOrTime() / 2)) || (epsilon
		// > Math.random()))
		if (epsilon > 0.5 || epsilon > rand.nextDouble()) {
			selectedValue = searchForNewValue();
		}

		if (selectedValue == null) {
			selectedValue = getNashValue();
		}

		if (selectedValue == null)
			return null;

		modifiedBid.setValue(currentIndex, selectedValue);

		// if (modifiedBid==lastBid)
		// return null;

		return modifiedBid;

	}

	// round number between 0..1 or 1 if no round deadline set
	private double getNormalizedRound() {
		if (!deadlines.isRounds())
			return 0d;

		double totalRounds = deadlines.getTotalRounds();
		double currentRound = roundNumber;
		return (totalRounds - currentRound - 1d) / totalRounds;
	}

	private Value searchForNewValue() throws Exception {

		ArrayList<Issue> issues = utilitySpace.getDomain().getIssues();
		Issue currentIssue;
		Value newValue;

		// noinspection unchecked
		ArrayList<Issue> checkedIssues = (ArrayList<Issue>) issues.clone();
		int checkID;
		do {
			checkID = rand.nextInt(checkedIssues.size());
			currentIssue = checkedIssues.get(checkID);
			currentIndex = currentIssue.getNumber();
			newValue = preferenceList.getMissingValue(currentIndex);
			if (newValue != null)
				return newValue;
			checkedIssues.remove(checkID);
		} while (checkedIssues.size() > 0);

		// noinspection unchecked
		checkedIssues = (ArrayList<Issue>) issues.clone();
		do {
			checkID = rand.nextInt(checkedIssues.size());
			currentIssue = checkedIssues.get(checkID);
			currentIndex = currentIssue.getNumber();

			for (Value incomparable : preferenceList.getIncomparableValues(
					currentIndex, lastBid.getValue(currentIndex))) {
				if (incomparable != null)
					return incomparable;
			}
			checkedIssues.remove(checkID);

		} while (checkedIssues.size() > 0);

		// noinspection unchecked
		checkedIssues = (ArrayList<Issue>) issues.clone();
		do {
			checkID = rand.nextInt(checkedIssues.size());
			currentIssue = checkedIssues.get(checkID);
			currentIndex = currentIssue.getNumber();

			ArrayList<Value> allValues = preferenceList
					.getAllPossibleValues(currentIndex);
			do {
				newValue = allValues.get(rand.nextInt(allValues.size()));
				if ((!newValue.equals(lastBid.getValue(currentIndex)))
						&& (preferenceList.mayImproveAll(currentIndex,
								lastBid.getValue(currentIndex), newValue)))
					return newValue;
				allValues.remove(newValue);
			} while (allValues.size() > 0);

			checkedIssues.remove(checkID);

		} while (checkedIssues.size() > 0);

		return null;
	}

	private Value getNashValue() throws Exception {
		ArrayList<Issue> issues = utilitySpace.getDomain().getIssues();
		Issue currentIssue;
		Value newValue;

		// noinspection unchecked
		ArrayList<Issue> checkedIssues = (ArrayList<Issue>) issues.clone();
		int checkID;

		do {
			checkID = rand.nextInt(checkedIssues.size());
			currentIssue = checkedIssues.get(checkID);
			currentIndex = currentIssue.getNumber();

			ArrayList<Value> allNashValues = preferenceList
					.getNashValues(currentIndex);
			do {
				newValue = allNashValues
						.get(rand.nextInt(allNashValues.size()));

				// if ((newValue!=lastBid.getValue(currentIndex)))
				if ((!newValue.equals(lastBid.getValue(currentIndex)))
						&& (preferenceList.mayImproveAll(currentIndex,
								lastBid.getValue(currentIndex), newValue)))
					return newValue;
				allNashValues.remove(newValue);
			} while (allNashValues.size() > 0);

			checkedIssues.remove(checkID);

		} while (checkedIssues.size() > 0);

		return newValue;

	}
}
