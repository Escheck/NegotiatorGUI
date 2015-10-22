package negotiator.parties;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import negotiator.Bid;
import negotiator.DeadlineType;
import negotiator.Party;
import negotiator.Vote;
import negotiator.actions.Action;
import negotiator.actions.EndNegotiationWithAnOffer;
import negotiator.actions.InformVotingResult;
import negotiator.actions.OfferForVoting;
import negotiator.actions.VoteForOfferAcceptance;
import negotiator.issue.Issue;
import negotiator.issue.IssueDiscrete;
import negotiator.issue.IssueInteger;
import negotiator.issue.IssueReal;
import negotiator.issue.Value;
import negotiator.issue.ValueInteger;
import negotiator.issue.ValueReal;

// It is slightly different from pure random in that this mediator first chooses an issue whose value has not been used before, randomly while flipping.

public class RandomFlipplingMediator extends Party {

	private Bid lastAcceptedBid;
	private int lastAcceptedRoundNumber;

	private Bid lastBid;
	private Vote isAcceptable;

	private int currentIndex;

	private HashMap<Integer, ArrayList<Value>> usedValues;

	public RandomFlipplingMediator() {

		super();
		lastAcceptedBid = null;
		lastBid = null;
		isAcceptable = Vote.ACCEPT;
		usedValues = new HashMap<Integer, ArrayList<Value>>();
	}

	@Override
	public void init() {

		randomnr = new Random(getSessionNo()); // Randomizer
		// randomnr= new Random(50+getSessionNo()); //Randomizer

	}

	@Override
	public void ReceiveMessage(Action opponentAction) {

		if (((VoteForOfferAcceptance) opponentAction).getVote() == Vote.REJECT)
			isAcceptable = Vote.REJECT;
	}

	@Override
	public Action chooseAction(ArrayList<Class> validActions) {

		if (((getDeadlineType() == DeadlineType.TIME) && (getTimeline()
				.isDeadlineReached()))
				|| ((getDeadlineType() == DeadlineType.ROUND) && (getRound() == getTotalRoundOrTime()))) {
			System.out.println("Last Accepted Round Number:"
					+ lastAcceptedRoundNumber);
			return new EndNegotiationWithAnOffer(partyID, lastAcceptedBid);
		}

		try {
			if (validActions.get(0) == InformVotingResult.class) {
				if (isAcceptable == Vote.ACCEPT) {
					lastAcceptedBid = new Bid(lastBid);
					lastAcceptedRoundNumber = getRound();
				}

				return new InformVotingResult(partyID, isAcceptable);

			} else if (getRound() == 1)
				lastBid = generateRandomBid();
			else {
				isAcceptable = Vote.ACCEPT; // initialize the isAcceptable
				lastBid = modifyLastBidRandomly();
			}

			if (lastBid == null) {
				System.out.println("Last Accepted Round Number:"
						+ lastAcceptedRoundNumber);
				return (new EndNegotiationWithAnOffer(this.partyID,
						lastAcceptedBid));
			}
		} catch (Exception e) {
			System.out
					.println("Cannnot generate random bid or update preference list problem:"
							+ e.getMessage());
		}

		return (new OfferForVoting(this.partyID, lastBid));
	}

	@Override
	protected Bid generateRandomBid() throws Exception {
		Bid randomBid = null;
		HashMap<Integer, Value> values = new HashMap<Integer, Value>(); // pairs
																		// <issuenumber,chosen
																		// value
																		// string>
		ArrayList<Issue> issues = utilitySpace.getDomain().getIssues();

		for (Issue currentIssue : issues) {
			values.put(currentIssue.getNumber(), getRandomValue(currentIssue));
			ArrayList<Value> valueList = new ArrayList<Value>();
			valueList.add(values.get(currentIssue.getNumber()));
			usedValues.put(currentIssue.getNumber(), valueList);
		}

		randomBid = new Bid(utilitySpace.getDomain(), values);

		return randomBid;
	}

	public Value getMissingValue(Issue issue) {

		switch (issue.getType()) {

		case DISCRETE:
			IssueDiscrete issueDiscrete = (IssueDiscrete) issue;
			for (Value value : issueDiscrete.getValues()) {
				if (!usedValues.get(issue.getNumber()).contains(value))
					return value;
			}
			break;
		case INTEGER:
			IssueInteger issueInteger = (IssueInteger) issue;
			Value value;
			for (int i = 0; i <= (issueInteger.getUpperBound() - issueInteger
					.getLowerBound()); i++) {
				value = new ValueInteger(issueInteger.getLowerBound() + i);
				if (!usedValues.get(issue.getNumber()).contains(value))
					return value;
			}
			break;

		case REAL:
			IssueReal issueReal = (IssueReal) issue;
			for (int i = 0; i < issueReal.getNumberOfDiscretizationSteps(); i++) {
				value = new ValueReal(
						issueReal.getLowerBound()
								+ (((double) ((issueReal.getUpperBound() - issueReal
										.getLowerBound())) / (issueReal
										.getNumberOfDiscretizationSteps())) * i));
				if (!usedValues.get(issue.getNumber()).contains(value))
					return value;
			}
			break;
		}

		return null;
	}

	private Bid modifyLastBidRandomly() throws Exception {

		ArrayList<Issue> issues = utilitySpace.getDomain().getIssues();
		Bid modifiedBid = new Bid(lastAcceptedBid);

		ArrayList<Issue> checkedIssues = (ArrayList<Issue>) issues.clone();
		Issue currentIssue;
		int checkID;
		Value newValue = null;

		do {
			checkID = randomnr.nextInt(checkedIssues.size());
			currentIssue = checkedIssues.get(checkID);
			currentIndex = currentIssue.getNumber();
			newValue = getMissingValue(currentIssue);
			if (newValue != null) {
				if (!usedValues.get(currentIndex).contains(newValue))
					usedValues.get(currentIndex).add(newValue);

				modifiedBid = modifiedBid.putValue(currentIndex, newValue);
				return modifiedBid;

			}
			checkedIssues.remove(checkID);
		} while (checkedIssues.size() > 0);

		do {
			currentIssue = issues.get(randomnr.nextInt(issues.size()));
			currentIndex = currentIssue.getNumber();
			newValue = getRandomValue(currentIssue);
		} while (newValue.equals(lastAcceptedBid.getValue(currentIndex)));

		if (!usedValues.get(currentIndex).contains(newValue))
			usedValues.get(currentIndex).add(newValue);

		modifiedBid = modifiedBid.putValue(currentIndex, newValue);

		return modifiedBid;
	}

}
