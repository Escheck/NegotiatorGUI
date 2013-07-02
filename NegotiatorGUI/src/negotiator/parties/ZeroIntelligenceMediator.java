package negotiator.parties;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import negotiator.Bid;
import negotiator.DeadlineType;
import negotiator.Party;
import negotiator.Vote;
import negotiator.actions.*;
import negotiator.issue.*;


public class ZeroIntelligenceMediator extends Party{
	
	private ArrayList<Bid> bidHistory;
	private Bid lastAcceptedBid;
	private int lastAcceptedRoundNumber;
	private Bid lastBid;
	private Vote isAcceptable;
	
	private boolean notIncludeRealIssue=true;
	private int maxPossibleOffer=1;
	
	
	public ZeroIntelligenceMediator() {
		
		super();
		bidHistory=new ArrayList<Bid> ();
		lastAcceptedBid=null;
		lastBid=null;		
		isAcceptable=Vote.REJECT;		
	}
	
	
	@Override
	public void init() {
		
		randomnr= new Random(getSessionNo()); //Randomizer
		ArrayList<Issue> issues=utilitySpace.getDomain().getIssues();
		int numberOfValue=0;

		for(Issue currentIssue:issues) {				
		
			switch(currentIssue.getType()) {
				case DISCRETE: 
					numberOfValue= ((IssueDiscrete)currentIssue).getNumberOfValues(); break;
				case INTEGER: 
					numberOfValue=((IssueInteger)currentIssue).getUpperBound()-((IssueInteger)currentIssue).getLowerBound()+1; break;
				case REAL: 
					notIncludeRealIssue=true; break;
				default: break;
			}
					
			if (numberOfValue!=0)
				maxPossibleOffer*=numberOfValue;
		}
	 }
	 
	@Override
	public void ReceiveMessage(Action opponentAction) {
		
		if (((VoteForOfferAcceptance)opponentAction).getVote()==Vote.REJECT)
			isAcceptable=Vote.REJECT;		
	}

	@Override
	public Action chooseAction(ArrayList<Class> validActions) {
	
		if (validActions.get(0)==InformVotingResult.class) {
			return new InformVotingResult(partyID, isAcceptable);
		}
		
		if (isAcceptable==Vote.ACCEPT) {
			lastAcceptedBid=new Bid(lastBid);
			lastAcceptedRoundNumber=getRound()-1;
		}
		
		isAcceptable=Vote.ACCEPT; //initialize the isAcceptable
		
		if ( ((getDeadlineType()==DeadlineType.TIME) && (getTimeline().isDeadlineReached())) ||
			     ((getDeadlineType()==DeadlineType.ROUND) && (getRound()==getTotalRoundOrTime())) )
		{
			System.out.println("Last Accepted Round Number:"+lastAcceptedRoundNumber);
			return new EndNegotiationWithAnOffer(partyID, lastAcceptedBid);	
		}
		
		if  ((notIncludeRealIssue) && (getRound()>maxPossibleOffer)) {
			System.out.println("Last Accepted Round Number:"+lastAcceptedRoundNumber);
			return new EndNegotiationWithAnOffer(partyID, lastAcceptedBid);
		}
		
		lastBid=null;
		
		try {
			lastBid=generateRandomBid();
		} catch (Exception e) { 
		    System.out.println("Cannnot generate random bid; problem:" +e.getMessage());
		}						
		
		if (lastBid == null){
			System.out.println("Last Accepted Round Number:"+lastAcceptedRoundNumber);
			return (new EndNegotiationWithAnOffer(this.partyID, lastAcceptedBid));
		}
		
		this.bidHistory.add(lastBid);
				
		return (new OfferForVoting(this.partyID, lastBid));
	}
	
	@Override
	protected Bid generateRandomBid() throws Exception
	{
		Bid randomBid=null;	
		HashMap<Integer, Value> values = new HashMap<Integer, Value>(); // pairs <issuenumber,chosen value string>
		ArrayList<Issue> issues=utilitySpace.getDomain().getIssues();
		
		do {
		 	for(Issue currentIssue:issues) {				
				values.put(currentIssue.getNumber(),getRandomValue(currentIssue));
			}
			
		 	randomBid=new Bid(utilitySpace.getDomain(),values);		
			
		} while (bidHistory.contains(randomBid));
	
		return randomBid;
	}
	
	
	
}