package negotiator.parties;

import java.util.ArrayList;

import negotiator.Bid;
import negotiator.DeadlineType;
import negotiator.Party;
import negotiator.Vote;
import negotiator.actions.Action;
import negotiator.actions.InformVotingResult;
import negotiator.actions.VoteForOfferAcceptance;

public class AnnealerParty extends Party{
	
	private double lastAcceptedBidUtility;
	private double lastReceivedBidUtility;
	private Vote currentVote;
	

	public AnnealerParty() {
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
			else {
				double T= (double)(getTotalRoundOrTime()-getRound()-1)/getTotalRoundOrTime();
		
				
				double probability=	Math.pow(Math.E, ((double)(lastReceivedBidUtility-lastAcceptedBidUtility)/T));
				
			  /*
				System.out.println("Round:"+getRound() + "  T:"+  T);
				System.out.println("Negative utility difference:" +(lastReceivedBidUtility-lastAcceptedBidUtility));
				System.out.println("probability:"+probability);
			*/
				if (probability>Math.random()) 
					currentVote=Vote.ACCEPT;
				else
					currentVote=Vote.REJECT;
			}	
		
	}

	@Override
	public Action chooseAction(ArrayList<Class> validActions) {
		
	    return (new VoteForOfferAcceptance(partyID, currentVote));
	}

	
	/* testing formula
	 public static void main(String[] args) 
	 {
		
		double difference=-0.11783780661737131;
		double T= (double)47/50;
		System.out.println("Difference:"+Math.pow(Math.E, ((double)(difference)/T)));
	

	}
	*/
	
}
