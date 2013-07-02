package negotiator.parties;

import java.util.ArrayList;

import negotiator.Bid;
import negotiator.DeadlineType;
import negotiator.Party;
import negotiator.Vote;
import negotiator.actions.*;

public class HillClimberParty extends Party{
	
	private double lastAcceptedBidUtility;
	private double lastReceivedBidUtility;
	private Vote currentVote;
	
	public HillClimberParty()
	{
		super();
		lastAcceptedBidUtility=0.0;
		lastReceivedBidUtility=0.0;
	}

	@Override
	public void ReceiveMessage(Action opponentAction) {
		
	    if (opponentAction instanceof InformVotingResult) {
			if (((InformVotingResult)opponentAction).getVotingResult()==Vote.ACCEPT) // update the utility of last accepted bid by all 
				lastAcceptedBidUtility=lastReceivedBidUtility;
			 return;
		}
		
        Bid receivedBid=Action.getBidFromAction(opponentAction);
		if (receivedBid== null)
			return;
		
		if (getDeadlineType()==DeadlineType.TIME) 
			lastReceivedBidUtility=getUtilityWithDiscount(receivedBid);
	    else 
	    	lastReceivedBidUtility=getUtility(receivedBid);
		
		if (lastAcceptedBidUtility<=lastReceivedBidUtility) 
			currentVote=Vote.ACCEPT;			
		else 
			currentVote=Vote.REJECT;
		
	}

	@Override
	public Action chooseAction(ArrayList<Class> validActions) {
		
	    return (new VoteForOfferAcceptance(partyID, currentVote));
	}



}
