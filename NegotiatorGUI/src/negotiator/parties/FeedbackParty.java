package negotiator.parties;

import java.util.ArrayList;

import negotiator.Bid;
import negotiator.DeadlineType;
import negotiator.Feedback;
import negotiator.Party;
import negotiator.Vote;
import negotiator.actions.Action;
import negotiator.actions.GiveFeedback;
import negotiator.actions.InformVotingResult;
import negotiator.actions.OfferForFeedback;
import negotiator.actions.OfferForVoting;
import negotiator.actions.VoteForOfferAcceptance;

public class FeedbackParty extends Party{

	private double lastBidUtility;
	private double lastAcceptedUtility;
	private double currentBidUtility;
	private Feedback currentFeedback;
	private Vote currentVote;
	private boolean voteTime;
	
	public FeedbackParty()
	{
		super();
		lastBidUtility=0.0;
		lastAcceptedUtility=0.0;
		currentBidUtility=0.0;
		currentFeedback=Feedback.SAME;
		voteTime=false;
	}
	
	@Override
	public void ReceiveMessage(Action opponentAction) {
		
		if (opponentAction instanceof InformVotingResult) {
			
			if (((InformVotingResult)opponentAction).getVotingResult()==Vote.ACCEPT) // update the utility of last accepted bid by all 
				lastAcceptedUtility=currentBidUtility;
			 return;
		}
			
        Bid receivedBid=Action.getBidFromAction(opponentAction);
		if (receivedBid== null)
			return;
		
		if (getDeadlineType()==DeadlineType.TIME) 
			currentBidUtility=getUtilityWithDiscount(receivedBid);
	    else 
	    	currentBidUtility=getUtility(receivedBid);
		
		if (opponentAction instanceof OfferForFeedback) {
			currentFeedback=Feedback.madeupFeedback(lastBidUtility,currentBidUtility);
			voteTime=false;
		}
		if (opponentAction instanceof OfferForVoting) {
			voteTime=true;
			if (lastAcceptedUtility<=currentBidUtility) 
				currentVote=Vote.ACCEPT;			
			else 
				currentVote=Vote.REJECT;
		}
		
		lastBidUtility=currentBidUtility;
		
	}

	@Override
	public Action chooseAction(ArrayList<Class> validActions) {
		
		if (voteTime)
			return (new VoteForOfferAcceptance(partyID, currentVote));
		else
			return (new GiveFeedback(partyID,currentFeedback)); 
				
	}

}
