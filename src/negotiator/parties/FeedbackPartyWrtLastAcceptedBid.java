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

public class FeedbackPartyWrtLastAcceptedBid extends Party{

	private double lastAcceptedUtility;
	private double lastReceivedBidUtility;
	private Feedback currentFeedback;
	private Vote currentVote;
	private boolean voteTime;
	
	public FeedbackPartyWrtLastAcceptedBid()
	{
		super();
		lastAcceptedUtility=0.0;
		lastReceivedBidUtility=0.0;
		currentFeedback=Feedback.SAME;
		voteTime=false;
	}
	
	@Override
	public void ReceiveMessage(Action opponentAction) {
		
		if (opponentAction instanceof InformVotingResult) {
			
			if (((InformVotingResult)opponentAction).getVotingResult()==Vote.ACCEPT) // update the utility of last accepted bid by all 
				lastAcceptedUtility=lastReceivedBidUtility;
			 return;
		}
			
        Bid receivedBid=Action.getBidFromAction(opponentAction);
		if (receivedBid== null)
			return;
		
		if (getDeadlineType()==DeadlineType.TIME) 
			lastReceivedBidUtility=getUtilityWithDiscount(receivedBid);
	    else 
	    	lastReceivedBidUtility=getUtility(receivedBid);
		
		if (opponentAction instanceof OfferForFeedback) {
			currentFeedback=Feedback.madeupFeedback(lastAcceptedUtility,lastReceivedBidUtility);
			voteTime=false;
		}
		if (opponentAction instanceof OfferForVoting) {
			voteTime=true;
			if (lastAcceptedUtility<=lastReceivedBidUtility) 
				currentVote=Vote.ACCEPT;			
			else 
				currentVote=Vote.REJECT;
		}
		
		
	}

	@Override
	public Action chooseAction(ArrayList<Class> validActions) {
		
		if (voteTime)
			return (new VoteForOfferAcceptance(partyID, currentVote));
		else
			return (new GiveFeedback(partyID,currentFeedback)); 
				
	}

}
