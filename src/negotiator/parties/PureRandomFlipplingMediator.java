package negotiator.parties;

import java.util.ArrayList;
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
import negotiator.issue.Value;

public class PureRandomFlipplingMediator extends Party {

	private Bid lastAcceptedBid;
	private int lastAcceptedRoundNumber;

	private Bid lastBid;
	private Vote isAcceptable;

	private int currentIndex;

	public PureRandomFlipplingMediator() {

		super();
		lastAcceptedBid = null;
		lastBid = null;
		isAcceptable = Vote.ACCEPT;
	}

	@Override
	public void init() {

		randomnr = new Random(getSessionNo()); // Randomizer

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

	private Bid modifyLastBidRandomly() throws Exception {

		ArrayList<Issue> issues = utilitySpace.getDomain().getIssues();
		Bid modifiedBid = new Bid(lastAcceptedBid);
		Value newValue;
		Issue currentIssue;

		do {
			currentIssue = issues.get(randomnr.nextInt(issues.size()));
			currentIndex = currentIssue.getNumber();
			newValue = getRandomValue(currentIssue);
		} while (newValue.equals(lastAcceptedBid.getValue(currentIndex)));

		modifiedBid = modifiedBid.putValue(currentIndex, newValue);

		return modifiedBid;
	}

}
