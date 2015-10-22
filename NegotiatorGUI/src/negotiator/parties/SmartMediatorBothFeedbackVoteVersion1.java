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
import negotiator.actions.NoAction;
import negotiator.actions.OfferForFeedback;
import negotiator.actions.OfferForVoting;
import negotiator.actions.VoteForOfferAcceptance;
import negotiator.issue.Issue;
import negotiator.issue.Value;
import negotiator.parties.partialopponentmodel.PartialPreferenceModels;

public class SmartMediatorBothFeedbackVoteVersion1 extends Party {

	private Bid lastAcceptedBid; // keeping the last accepted bid by all parties
	private Bid currentBid; // the current bid
	private Bid lastBid; // the bid that is offered in the previous round
	private ArrayList<Bid> localOptimalBids;
	private ArrayList<Bid> bidsForVoting;

	private int currentIndex;
	private ArrayList<Feedback> currentFeedbackList;

	private PartialPreferenceModels preferenceList; // the model learnt by the
													// mediator during the
													// negotiation

	private Vote isAcceptable;
	private int lastAcceptedRoundNumber;
	private int bidIndex = 0;

	private boolean continueSearch;
	private boolean activateLearning;

	public SmartMediatorBothFeedbackVoteVersion1() {

		super();
		lastAcceptedBid = null;
		currentBid = null;
		lastBid = null;
		localOptimalBids = new ArrayList<Bid>();
		bidsForVoting = new ArrayList<Bid>();

		currentFeedbackList = new ArrayList<Feedback>();
		isAcceptable = Vote.ACCEPT;
		continueSearch = true;
		activateLearning = false;
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
		else if (opponentAction instanceof VoteForOfferAcceptance) {
			// if one of the parties votes as "reject", it will not be accepted
			// by the mediator
			if (((VoteForOfferAcceptance) opponentAction).getVote() == Vote.REJECT)
				isAcceptable = Vote.REJECT;
		}

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
				if (continueSearch == false) {
					if (isAcceptable == Vote.ACCEPT) {
						lastAcceptedBid = new Bid(lastBid);
						lastAcceptedRoundNumber = getRound();
					}
					return new InformVotingResult(partyID, isAcceptable);
				} else
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

				if (getRound() == 3)
					activateLearning = true;

				if (activateLearning) {
					preferenceList.updateIssuePreferenceList(currentIndex,
							lastBid.getValue(currentIndex),
							currentBid.getValue(currentIndex),
							currentFeedbackList);
					lastBid = new Bid(currentBid);

					// check the continue Search or use knowledge here
					if (getRound() > ((double) getTotalRoundOrTime() / 2)) {

						bidsForVoting = preferenceList
								.estimatePossibleNashBids(lastBid);
						preferenceList
								.sortBidsWrtProductUtility(localOptimalBids);
						for (Bid bid : localOptimalBids)
							if (!bidsForVoting.contains(bid))
								bidsForVoting.add(bid);

						if ((getTotalRoundOrTime() - getRound()) == (bidsForVoting
								.size())) { // time for voting !
							System.out.println("Time:" + getRound()
									+ " bid size:" + bidsForVoting.size());
							continueSearch = false;
							activateLearning = false;
							System.out
									.println("Turn off learning and searching in "
											+ getRound() + " round.");
						}

					}

				}

				if (continueSearch) { // evaluate feedbacks and generate bids
										// accordingly

					if ((Feedback.isAcceptable(currentFeedbackList) == Vote.REJECT)
							&& (isAcceptable == Vote.ACCEPT)) {
						localOptimalBids.add(currentBid);
					}

					isAcceptable = Feedback.isAcceptable(currentFeedbackList);
					currentBid = modifyLastBid();
					currentFeedbackList.clear(); // clear the content of the
													// list for the next round

					if (currentBid == null) { // if we are not able to generate
												// bids;
						continueSearch = false;
						activateLearning = false;
						System.out
								.println("Turn off learning and searching in "
										+ getRound() + " round.");
						bidsForVoting = preferenceList
								.estimatePossibleNashBids(lastBid);
						preferenceList
								.sortBidsWrtProductUtility(localOptimalBids);

						for (Bid bid : localOptimalBids)
							if (!bidsForVoting.contains(bid))
								bidsForVoting.add(bid);
					} else
						return (new OfferForFeedback(this.partyID, currentBid));
				}

				if (continueSearch == false) {

					isAcceptable = Vote.ACCEPT;// initialize the isAcceptable

					if (bidIndex < bidsForVoting.size()) {
						lastBid = bidsForVoting.get(bidIndex);
						bidIndex++;
						return (new OfferForVoting(this.partyID, lastBid));
					} else {
						System.out.println("Last Accepted Round Number:"
								+ lastAcceptedRoundNumber);
						System.out.println("Learnt preference model:"
								+ preferenceList.toString());
						return (new EndNegotiationWithAnOffer(this.partyID,
								lastAcceptedBid));
					}
				}

			}

		} catch (Exception e) {
			System.out
					.println("Cannnot generate random bid or update preference list problem:"
							+ e.getMessage());
		}

		return null;

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
			preferenceList.getNashValues(currentIndex);
			int index;
			do {
				index = randomnr.nextInt(allValues.size());
				newValue = allValues.get(index);

				// if ((!newValue.equals(lastBid.getValue(currentIndex)))&&
				// (preferenceList.mayImproveMajority(currentIndex,
				// lastBid.getValue(currentIndex), newValue)))

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

	private Bid modifyLastBid() throws Exception {

		Bid modifiedBid = new Bid(lastBid);
		Value selectedValue = searchForNewValue();
		if (selectedValue == null)
			return null;
		modifiedBid = modifiedBid.putValue(currentIndex, selectedValue);
		return modifiedBid;

	}

}
