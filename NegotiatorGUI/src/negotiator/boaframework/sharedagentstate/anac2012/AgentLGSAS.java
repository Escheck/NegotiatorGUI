package negotiator.boaframework.sharedagentstate.anac2012;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import agents.anac.y2012.AgentLG.BidsComparator;
import agents.anac.y2012.AgentLG.OpponentBids;
import negotiator.Bid;
import negotiator.Domain;
import negotiator.bidding.BidDetails;
import negotiator.boaframework.NegotiationSession;
import negotiator.boaframework.OMStrategy;
import negotiator.boaframework.OpponentModel;
import negotiator.boaframework.SharedAgentState;
import negotiator.boaframework.SortedOutcomeSpace;
import negotiator.boaframework.opponentmodel.NoModel;
import negotiator.issue.Issue;
import negotiator.issue.Value;
import negotiator.issue.ValueInteger;
import negotiator.issue.ValueReal;
import negotiator.utility.Evaluator;
import negotiator.utility.EvaluatorDiscrete;
import negotiator.utility.EvaluatorInteger;
import negotiator.utility.EvaluatorReal;
import negotiator.utility.UtilitySpace;



public class AgentLGSAS extends SharedAgentState{
	
	private UtilitySpace  utilitySpace;
	private OpponentBids  opponentBids;
	private ArrayList<Bid> allBids = null; 
	private Bid maxLastOpponentBid;
	private int numPossibleBids=0;
	private int index=0;
	private double lastTimeLeft=0;
	private int minSize = 160000;
	private Bid myBestBid=null;
	private OpponentModel opponentModel;
	private SortedOutcomeSpace outcomeSpace;
	private OMStrategy oms;

	public AgentLGSAS (NegotiationSession negoSession, OpponentBids opponentBids, OpponentModel opponentModel, OMStrategy oms) {
		NAME = "AgentLR";
		this.oms = oms;
		this.utilitySpace = negoSession.getUtilitySpace();
		this.opponentBids = opponentBids;
		this.opponentModel = opponentModel;
		if (!(opponentModel instanceof NoModel)) {
			outcomeSpace = new SortedOutcomeSpace(utilitySpace);
		}
	}
	
private void initBids() {
		
		//get all bids
		allBids = getAllBids();
		  	
		BidsComparator bidsComparator = new BidsComparator(utilitySpace);
		
		// sort the bids in order of highest utility
		//System.out.println("before size " + allBids.size() +" time " + System.currentTimeMillis());
		Collections.sort(allBids, bidsComparator);
		//System.out.println("after time " + System.currentTimeMillis());
		
	}

	/**
	 * Calculate the next bid for the agent (from 1/4 most optimal bids)
	 * 
	 */
	public BidDetails getNextBid(double time)
	{
		BidDetails currentAction = null ;
		try
		{
			Bid newBid= allBids.get(index);
			currentAction = new BidDetails(newBid, utilitySpace.getUtility(newBid));
			index++;
			if (index>numPossibleBids)
			{
				//the time is over compromising in a high rate
				if (time>=0.9)
				{
					if (time-lastTimeLeft>0.008 )
					{						
						double myBestUtility = utilitySpace.getUtility(myBestBid);
						double	oppBestUtility = utilitySpace.getUtility(opponentBids.getOpponentsBids().get(0));
						double avg =(myBestUtility+oppBestUtility)/2;
						
						if(index>=allBids.size())
							index= allBids.size()-1;
						else
							if (utilitySpace.getUtility(allBids.get(index))<avg)
							{
								index--;
								double maxUtilty = 0;
								int maxBidIndex = numPossibleBids;

								for (int i = numPossibleBids; i <= index; i++) 
								{
									//finds the next better bid for the opponent
									double utiliy = getUtilityOfOpponentsBid(utilitySpace.getDomain(), allBids.get(i));
									if (utiliy> maxUtilty)
									{
										maxUtilty = utiliy;
										maxBidIndex =i;
									}
								}
								numPossibleBids= maxBidIndex;
							}
							else
								index--;
					}
					else
						index=0;
				}
				else
				{
					index=0;
					double discount =utilitySpace.getDiscountFactor();
					//the time is over compromising in normal rate (0.05)
					if (time-lastTimeLeft>0.05)
					{
						//compromise only if the opponent is compromising 
						if (utilitySpace.getUtility(opponentBids.getMaxUtilityBidForMe())>utilitySpace.getUtility(maxLastOpponentBid)
								 || (discount < 1 && time-lastTimeLeft>0.1))
						{
							//finds the next better bid for the opponent
							double maxUtilty = 0;
							for (int i = 0; i <= numPossibleBids; i++) 
							{
								double utiliy = getUtilityOfOpponentsBid(utilitySpace.getDomain(), allBids.get(i));
								//System.out.println("UTILITY: " + utiliy);
								if (utiliy> maxUtilty)
									maxUtilty = utiliy;
							}
							
							for (int i =numPossibleBids+1; i < allBids.size(); i++) 
							{
								double utiliy = getUtilityOfOpponentsBid(utilitySpace.getDomain(), allBids.get(i));
								if (utiliy>= maxUtilty)
								{
									numPossibleBids = i;
									break;
								}
							}
							maxLastOpponentBid = opponentBids.getMaxUtilityBidForMe();
							lastTimeLeft= time;
						}
					}
	
				}		
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		if (!(opponentModel instanceof NoModel)) {
			try {
				currentAction = oms.getBid(outcomeSpace, utilitySpace.getUtility(currentAction.getBid()));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return currentAction;
	}
	
	/**
	 * Calculate the next optimal bid for the agent (from 1/4 most optimal bids)
	 *
	 */
	public BidDetails getNextOptimicalBid(double time)
	{
		BidDetails currentAction = null;
		Bid newBid=null;
		try
		{
			if (allBids == null)
				initBids();
			newBid= allBids.get(index);
		//	System.out.println("Decoupled getNextOptimal: " + newBid);

			currentAction = new BidDetails(newBid, utilitySpace.getUtility(newBid));
			index++;
			double myBestUtility = utilitySpace.getUtilityWithDiscount(myBestBid,time);
			double oppBestUtility = utilitySpace.getUtilityWithDiscount(opponentBids.getOpponentsBids().get(0),time);
			double downBond =myBestUtility- (myBestUtility-oppBestUtility)/4;
			//check if time passes and compromise a little bit
			if (time-lastTimeLeft>0.1 && numPossibleBids<allBids.size()-1 
					&&  downBond<=utilitySpace.getUtilityWithDiscount(allBids.get(numPossibleBids+1),time))
			{
				double futureUtility = utilitySpace.getUtilityWithDiscount(allBids.get(numPossibleBids), time+0.1);
				while (utilitySpace.getUtilityWithDiscount(allBids.get(numPossibleBids),time)>=futureUtility && numPossibleBids<allBids.size()-1)
					numPossibleBids++;
				lastTimeLeft =time;	
			}
			if (index>numPossibleBids)
				index=0;
		}
		catch (Exception e) {
				e.printStackTrace();
			}
		maxLastOpponentBid=opponentBids.getMaxUtilityBidForMe();

		if (!(opponentModel instanceof NoModel)) {
			try {
				currentAction = oms.getBid(outcomeSpace, utilitySpace.getUtility(currentAction.getBid()));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return currentAction;
	
	}
	
		
	
	/*
	 * returns the Evaluator of an issue
	 */
	public Evaluator getMyEvaluator(int issueID) {
		return utilitySpace.getEvaluator(issueID);
	}
	
	
	/*
	 * returns all bids 
	 */
	private ArrayList<Bid> getAllBids()
    {
		ArrayList<Bid> bids = new ArrayList<Bid>();
		ArrayList<Issue> issues =utilitySpace.getDomain().getIssues();
      	

       	HashMap<Integer, Value> issusesFirstValue = new HashMap<Integer, Value>();
       	for(Issue issue:issues) {
       		
       		Value v = getIsuueValues(issue).get(0);
       		issusesFirstValue.put(issue.getNumber(), v);	
       	}
       	try { 
       		bids.add(new Bid(utilitySpace.getDomain(), issusesFirstValue));
       		} 
       		catch(Exception e) 
       		{ 	e.printStackTrace();
       		}
       	
       	for(Issue issue:issues) {	
       		ArrayList<Bid> tempBids = new ArrayList<Bid>();
       		ArrayList<Value>  issueValues =  getIsuueValues(issue);
     
       		for(Bid bid : bids) {
       			
	       		for (Value value : issueValues) {
					
	       			HashMap<Integer, Value> lNewBidValues = getBidValues(bid);
	       			lNewBidValues.put(issue.getNumber(), value);
	       			
	       			try { 
	       				Bid newBid = new Bid(utilitySpace.getDomain(), lNewBidValues);
	       				tempBids.add(newBid); 
	       				
	       			} catch(Exception e) { 
	       				e.printStackTrace();
	       			}
	       		}
       		}
       		bids = tempBids;	
       	}
       	
       	//remove bids that are not good enough (the utility is less the 1/4 of the difference between the players) 
       
       	double myBestUtility=1;
       	double	oppBestUtility=0;
		try {
			myBestBid = utilitySpace.getMaxUtilityBid();
			myBestUtility = utilitySpace.getUtility(myBestBid);
			oppBestUtility = utilitySpace.getUtility(opponentBids.getOpponentsBids().get(0));	
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		
		return filterBids(bids, myBestUtility, oppBestUtility,0.75D);
    }


	private ArrayList<Bid> filterBids(ArrayList<Bid> bids,
			double myBestUtility, double oppBestUtility, double fraction) {
		double downBond =myBestUtility- (myBestUtility-oppBestUtility)*fraction;
		ArrayList<Bid> filteredBids= new ArrayList<Bid>();
       	for(Bid bid : bids) {
       		try {
       		    double reservation = utilitySpace.getReservationValue() != null ? utilitySpace.getReservationValue() :0;
				if(utilitySpace.getUtility(bid) < downBond || utilitySpace.getUtility(bid)<reservation) 
					continue;
				else
					filteredBids.add(bid);
				
			} catch (Exception e) {
				e.printStackTrace();
			}   
       	}
       	
       	//System.out.println("need to remove " + bids.size() +" removed" + bidstoRemove.size() + "time " );
       	if(filteredBids.size() < minSize){
       		return filteredBids;
       	} 
       	return filterBids(filteredBids,myBestUtility,oppBestUtility,fraction*0.85D);
	}


	/*
	 * returns bid values
	 */
	private HashMap<Integer, Value> getBidValues(Bid bid) {
		 HashMap<Integer, Value> bidValues = new HashMap<Integer,Value>();
			ArrayList<Issue> allIsuues = utilitySpace.getDomain().getIssues();
			for (Issue issue : allIsuues) {
				try {
					bidValues.put(issue.getNumber(),bid.getValue(issue.getNumber()));
				} catch (Exception e) {
					e.printStackTrace();
				}
					
			}
		return bidValues;
	}
	
	/*
	 * returns issue values
	 */
	public ArrayList<Value> getIsuueValues(Issue issue){

	  	Evaluator e = getMyEvaluator(issue.getNumber());  	
	  	ArrayList<Value> retValues= new ArrayList<Value>();
		switch(e.getType()) {
		case DISCRETE: EvaluatorDiscrete eD = ((EvaluatorDiscrete)e);
						retValues.addAll(eD.getValues());		
						break;
		case REAL:  	EvaluatorReal eR = ((EvaluatorReal)e);
						
						double intervalReal =(eR.getUpperBound()-eR.getLowerBound())/10;
						for (int i=0; i<= 10; i++ )
						{
							retValues.add(new ValueReal(eR.getLowerBound()+i*intervalReal));
						}
						break;
		case INTEGER:  
						EvaluatorInteger eI = ((EvaluatorInteger)e);
						
						int intervalInteger =(eI.getUpperBound()-eI.getLowerBound())/10;
						for (int i=0; i<= 10; i++ )
						{
							retValues.add(new ValueInteger(eI.getLowerBound()+i*intervalInteger));
						}
						break;
	}
		return retValues;
	}
	
	/*
	 * returns the minimum utility of the bid that the agent voted
	 */
	public double getMyBidsMinUtility(double time)
	{
		if (allBids == null)
			initBids();
		return  utilitySpace.getUtilityWithDiscount(allBids.get(numPossibleBids),time);
	}
	
	/*
	 * returns the bid with the minimum utility that the agent voted
	 */
	public Bid getMyminBidfromBids()
	{
		if (allBids == null)
			initBids();
		return allBids.get(numPossibleBids);
	}
	/*
	 * returns the bid utility
	 */
	public double getUtility(Bid bid)
	{
		try {
			return utilitySpace.getUtility(bid);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}
	
	public double getUtilityOfOpponentsBid(Domain domain, Bid bid) {
		double utility;
		if (opponentModel instanceof NoModel) {
			utility = opponentBids.getOpponentBidUtility(utilitySpace.getDomain(), bid);
		} else {
			utility = opponentModel.getBidEvaluation(bid);
		}
		return utility;
	}
}
