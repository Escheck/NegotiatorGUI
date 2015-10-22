package negotiator.parties;

import java.util.ArrayList;
import java.util.Random;

import negotiator.Bid;
import negotiator.DeadlineType;
import negotiator.Feedback;
import negotiator.Party;
import negotiator.Vote;
import negotiator.actions.Action;
import negotiator.actions.EndNegotiationWithAnOffer;
import negotiator.actions.GiveFeedback;
import negotiator.actions.InformVotingResult;
import negotiator.actions.OfferForFeedback;
import negotiator.issue.Issue;
import negotiator.issue.Value;
import negotiator.parties.partialopponentmodel.PartialPreferenceModels;

public class SmartMediatorOnlyFeedbackWrtLastAcceptedBid extends Party {

	private Bid currentBid; // the current bid

	private Vote isAcceptable;
	private int lastAcceptedRoundNumber;
	private Bid lastAcceptedBid; // keeping the last accepted bid by all parties

	private int currentIndex;
	private ArrayList<Feedback> currentFeedbackList;

	private PartialPreferenceModels preferenceList; // the model is learned by
													// the mediator during the
													// negotiation

	public SmartMediatorOnlyFeedbackWrtLastAcceptedBid() {

		super();
		lastAcceptedBid = null;
		currentBid = null;
		currentFeedbackList = new ArrayList<Feedback>();
		isAcceptable = Vote.ACCEPT;
	}

	@Override
	public void init() {

		randomnr = new Random(getSessionNo()); // Randomizer

	}

	@Override
	public void ReceiveMessage(Action opponentAction) {

		if (opponentAction instanceof GiveFeedback)
			currentFeedbackList.add(((GiveFeedback) opponentAction)
					.getFeedback());

	}

	@Override
	public Action chooseAction(ArrayList<Class> validActions) {

		// First check the deadline: If the deadline is reached, end negotiation
		// with last accepted bid by all parties..
		if (((getDeadlineType() == DeadlineType.TIME) && (getTimeline()
				.isDeadlineReached()))
				|| ((getDeadlineType() == DeadlineType.ROUND) && (getRound() == getTotalRoundOrTime()))) {

			System.out.println("Last Accepted Bid:" + lastAcceptedBid + " in "
					+ lastAcceptedRoundNumber + "th round");
			System.out.println("Learnt preferences:"
					+ preferenceList.toString());
			return new EndNegotiationWithAnOffer(partyID, lastAcceptedBid);
		}

		try {

			if (validActions.contains(InformVotingResult.class)) {

				if (getRound() > 1)
					preferenceList.updateIssuePreferenceList(currentIndex,
							lastAcceptedBid.getValue(currentIndex),
							currentBid.getValue(currentIndex),
							currentFeedbackList);

				isAcceptable = Feedback.isAcceptable(currentFeedbackList);

				if (isAcceptable == Vote.ACCEPT) {
					lastAcceptedBid = new Bid(currentBid);
					lastAcceptedRoundNumber = getRound();
				}

				return new InformVotingResult(partyID, isAcceptable);

			} else if (getRound() == 1) { // initially generate a random bid and
											// create the preference model
				currentBid = generateRandomBid();
				preferenceList = new PartialPreferenceModels(
						new Bid(currentBid), getPartyListenerIndices().size());
				return (new OfferForFeedback(this.partyID, currentBid));
				// when we stop searching part and start voting, inform the
				// agents about the last accepted bid by all
			} else {

				currentFeedbackList.clear();
				currentBid = modifyLastAcceptedBid();

				if (currentBid == null) { // if we are not able to generate
											// bids;
					System.out.println("Last Accepted Round Number:"
							+ lastAcceptedRoundNumber);
					System.out.println(preferenceList.toString());
					return (new EndNegotiationWithAnOffer(this.partyID,
							lastAcceptedBid));
				}

			}
		} catch (Exception e) {
			System.out
					.println("Cannnot generate random bid or update preference list problem:"
							+ e.getMessage());
		}

		return (new OfferForFeedback(this.partyID, currentBid));
	}

	private Bid modifyLastAcceptedBid() throws Exception {

		Bid modifiedBid = new Bid(lastAcceptedBid);

		double epsilon = (double) (getTotalRoundOrTime() - getRound() - 1)
				/ getTotalRoundOrTime();
		Value selectedValue = null;

		if ((getRound() <= ((double) getTotalRoundOrTime() / 2))
				|| (epsilon > Math.random())) {
			selectedValue = searchForNewValue();
		}

		if (selectedValue == null) {
			selectedValue = getNashValue();
		}

		if (selectedValue == null)
			return null;

		modifiedBid = modifiedBid.putValue(currentIndex, selectedValue);

		// if (modifiedBid==lastBid)
		// return null;

		return modifiedBid;

	}

	private Value searchForNewValue() throws Exception {

		ArrayList<Issue> issues = utilitySpace.getDomain().getIssues();
		Issue currentIssue;
		Value newValue = null;

		ArrayList<Issue> checkedIssues = (ArrayList<Issue>) issues.clone();
		int checkID;
		do {
			checkID = randomnr.nextInt(checkedIssues.size());
			currentIssue = checkedIssues.get(checkID);
			currentIndex = currentIssue.getNumber();
			newValue = preferenceList.getMissingValue(currentIndex);
			if (newValue != null)
				return newValue;
			checkedIssues.remove(checkID);
		} while (checkedIssues.size() > 0);

		checkedIssues = (ArrayList<Issue>) issues.clone();
		do {
			checkID = randomnr.nextInt(checkedIssues.size());
			currentIssue = checkedIssues.get(checkID);
			currentIndex = currentIssue.getNumber();

			for (Value incomparable : preferenceList.getIncomparableValues(
					currentIndex, lastAcceptedBid.getValue(currentIndex))) {
				if (incomparable != null)
					return incomparable;
			}
			checkedIssues.remove(checkID);

		} while (checkedIssues.size() > 0);

		checkedIssues = (ArrayList<Issue>) issues.clone();
		do {
			checkID = randomnr.nextInt(checkedIssues.size());
			currentIssue = checkedIssues.get(checkID);
			currentIndex = currentIssue.getNumber();

			ArrayList<Value> allValues = preferenceList
					.getAllPossibleValues(currentIndex);
			int index;
			do {
				index = randomnr.nextInt(allValues.size());
				newValue = allValues.get(index);
				if ((!newValue.equals(lastAcceptedBid.getValue(currentIndex)))
						&& (preferenceList.mayImproveAll(currentIndex,
								lastAcceptedBid.getValue(currentIndex),
								newValue)))
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
		Value newValue = null;

		ArrayList<Issue> checkedIssues = (ArrayList<Issue>) issues.clone();
		int checkID;

		do {
			checkID = randomnr.nextInt(checkedIssues.size());
			currentIssue = checkedIssues.get(checkID);
			currentIndex = currentIssue.getNumber();

			ArrayList<Value> allNashValues = preferenceList
					.getNashValues(currentIndex);
			do {
				newValue = allNashValues.get(randomnr.nextInt(allNashValues
						.size()));

				if (!newValue.equals(lastAcceptedBid.getValue(currentIndex)))
					return newValue;
				allNashValues.remove(newValue);
			} while (allNashValues.size() > 0);

			checkedIssues.remove(checkID);

		} while (checkedIssues.size() > 0);

		return newValue;

	}

}
