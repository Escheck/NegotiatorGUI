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
import negotiator.actions.NoAction;
import negotiator.actions.OfferForFeedback;
import negotiator.issue.Issue;
import negotiator.issue.Value;
import negotiator.parties.partialopponentmodel.PartialPreferenceModels;

public class SmartMediatorOnlyFeedback extends Party {

	private Bid currentBid; // the current bid
	private Bid lastBid; // the bid that is offered in the previous round

	private Vote isAcceptable;
	private int lastAcceptedRoundNumber;
	private Bid lastAcceptedBid; // keeping the last accepted bid by all parties

	private int currentIndex;
	private ArrayList<Feedback> currentFeedbackList;

	// public static PrintStream writePreferenceStream;

	private PartialPreferenceModels preferenceList; // the model is learned by
													// the mediator during the
													// negotiation

	public SmartMediatorOnlyFeedback() {

		super();
		lastAcceptedBid = null;
		currentBid = null;
		lastBid = null;
		currentFeedbackList = new ArrayList<Feedback>();
		isAcceptable = Vote.ACCEPT;

	}

	@Override
	public void init() {

		randomnr = new Random(getSessionNo()); // Randomizer
		// randomnr= new Random(50+getSessionNo()); //Randomizer
		/*
		 * if (getSessionNo()==1) try{ FileOutputStream fileResultStream = new
		 * FileOutputStream("NegoPreferenceModels.dat");
		 * writePreferenceStream=new PrintStream(fileResultStream); } catch
		 * (Exception e) { System.err.print(e); }
		 */
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

			// writePreferenceStream.println("Session:"+getSessionNo());
			// writePreferenceStream.println(preferenceList.toString());
			// writePreferenceStream.println("**********************************************");

			System.out.println("Last Accepted Bid:" + lastAcceptedBid + " in "
					+ lastAcceptedRoundNumber + "th round");

			return new EndNegotiationWithAnOffer(partyID, lastAcceptedBid);
		}

		try {

			if (validActions.contains(NoAction.class)) {

				isAcceptable = Feedback.isAcceptable(currentFeedbackList);

				if (isAcceptable == Vote.ACCEPT) {
					lastAcceptedBid = new Bid(currentBid);
					lastAcceptedRoundNumber = getRound();
				}

				return new NoAction();

			} else if (getRound() == 1) { // initially generate a random bid and
											// create the preference model
				currentBid = generateRandomBid();
				lastBid = new Bid(currentBid);
				preferenceList = new PartialPreferenceModels(
						new Bid(currentBid), getPartyListenerIndices().size());
				return (new OfferForFeedback(this.partyID, currentBid));
				// when we stop searching part and start voting, inform the
				// agents about the last accepted bid by all
			} else {

				if (getRound() > 2) {

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
					return (new EndNegotiationWithAnOffer(this.partyID,
							lastAcceptedBid));
				}

			}
		} catch (Exception e) {
			System.out
					.println(getRound()
							+ " :Cannnot generate random bid or update preference list problem:"
							+ e.getMessage());

		}

		return (new OfferForFeedback(this.partyID, currentBid));
	}

	private Bid modifyLastBid() throws Exception {

		Bid modifiedBid = new Bid(lastBid);

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
					currentIndex, lastBid.getValue(currentIndex))) {
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
			do {
				newValue = allValues.get(randomnr.nextInt(allValues.size()));
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
