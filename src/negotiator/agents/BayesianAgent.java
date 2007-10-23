package negotiator.agents;

import negotiator.Agent;
import negotiator.Bid;
import negotiator.BidIterator;
import negotiator.NegotiationTemplate;
import negotiator.actions.Accept;
import negotiator.actions.Action;
import negotiator.actions.EndNegotiation;
import negotiator.actions.Offer;
import negotiator.agents.BayesianOpponentModel.*;
import negotiator.issue.Value;
import negotiator.issue.ValueReal;
import negotiator.Domain;


public class BayesianAgent extends Agent {
	private String myName;
	private Action messageOpponent;
	private int sessionNumber;
	private int sessionTotalNumber;
	//private int nrOfIssues;
	private Bid myLastBid = null;
	private Action myLastAction = null;
//	private Similarity fSimilarity;
	private static final double BREAK_OFF_POINT = 0.5;
	private double[] lIssueWeight;
	private enum ACTIONTYPE { START, OFFER, ACCEPT, BREAKOFF };
	private enum STRATEGY {SMART, SERIAL, RESPONSIVE, RANDOM};
	private STRATEGY fStrategy = STRATEGY.SMART;
	private int fSmartSteps;
	private BayesianOpponentModel fOpponentModel;	
	private static final double CONCESSIONFACTOR = 0.03;
	private static final double ALLOWED_UTILITY_DEVIATION = 0.008;
	private static final int NUMBER_OF_SMART_STEPS = 2; 
	
	// Class constructor
	public BayesianAgent() {
		super();
	}

	protected void init(int sessionNumber, int sessionTotalNumber,Domain d) {		
		super.init(sessionNumber, sessionTotalNumber, d);
		myName = super.getName();
		this.sessionNumber = sessionNumber;
		this.sessionTotalNumber = sessionTotalNumber;
		messageOpponent = null;
		myLastBid = null;
		myLastAction = null;
		fSmartSteps = 0;
	}

	// Class methods
	public void ReceiveMessage(Action opponentAction) {
		messageOpponent = opponentAction;
	}

	private Action proposeInitialBid() throws Exception
	{
		Bid lBid=null;
				/*Value[] values = new Value[4];
			if(myName.equals("Buyer"))	{
				values[0] = new ValueReal(0.7);
				values[1] = new ValueReal(0.9);
				values[2] = new ValueReal(0.7);
				values[3] = new ValueReal(1);
			} else {
				values[0] = new ValueReal(0);
				values[1] = new ValueReal(0.2);
				values[2] = new ValueReal(0);
				values[3] = new ValueReal(0.3);
			}
			lBid = new Bid(utilitySpace.getDomain(), values);*/
		// Return (one of the) possible bid(s) with maximal utility.
		lBid = utilitySpace.getMaxUtilityBid();
		fSmartSteps=NUMBER_OF_SMART_STEPS;
		myLastBid = lBid;
		return new Offer(this, lBid);
	}

	private Bid getNextBidSmart(Bid pOppntBid) throws Exception 
	{
		double lMyUtility, lOppntUtility, lTargetUtility;
		// Both parties have made an initial bid. Compute associated utilities from my point of view.
		lMyUtility = utilitySpace.getUtility(myLastBid);
		lOppntUtility = utilitySpace.getUtility(pOppntBid);
		if(fSmartSteps>=NUMBER_OF_SMART_STEPS) {
			lTargetUtility = getTargetUtility(lMyUtility, lOppntUtility);
			fSmartSteps=0;
		} else {
			lTargetUtility = lMyUtility; 
			fSmartSteps++;
		}
		return getTradeOff(lTargetUtility, pOppntBid);
	}
	private Bid getTradeOff(double pUtility, Bid pOppntBid) throws Exception
	{
		Bid lBid=null;
		double lExpectedUtility = -1;
		BidIterator lIter = new BidIterator(utilitySpace.getDomain());
		int i=1;
		while(lIter.hasNext()) {
			Bid tmpBid = lIter.next();
//			System.out.println(tmpBid);
//			System.out.println(String.valueOf(i++));
			if(Math.abs(utilitySpace.getUtility(tmpBid)-pUtility)<ALLOWED_UTILITY_DEVIATION) {
				//double lTmpSim = fSimilarity.getSimilarity(tmpBid, pOppntBid);
				double lTmpExpecteUtility = fOpponentModel.getExpectedUtility(tmpBid);
				if(lTmpExpecteUtility > lExpectedUtility) {
					lExpectedUtility= lTmpExpecteUtility ;
					lBid = tmpBid;
				}
			}				
		} //while
		return lBid;
	}
	private Action proposeNextBid(Bid pOppntBid) throws Exception
	{
		Bid lBid = null;
		switch(fStrategy) {
		case SMART:
			lBid = getNextBidSmart(pOppntBid);
			break;
		}
		myLastBid = lBid;
		return new Offer(this, lBid);
	}

	public Action chooseAction()
	{
		Action lAction = null;
		ACTIONTYPE lActionType;
		Bid lOppntBid = null;

		try
		{
			lActionType = getActionType(messageOpponent);
			switch (lActionType) {
			case OFFER: // Offer received from opponent
				lOppntBid = ((Offer) messageOpponent).getBid();
				fOpponentModel.updateBeliefs(lOppntBid);
				if (myLastAction == null)
					// Other agent started, lets propose my initial bid.
					lAction = proposeInitialBid();
				else if (utilitySpace.getUtility(lOppntBid) >= utilitySpace
						.getUtility(myLastBid))
					// Opponent bids equally, or outbids my previous bid, so lets
					// accept
					lAction = new Accept(this, lOppntBid);
				else
					// Propose counteroffer. Get next bid.
					lAction = proposeNextBid(lOppntBid);
				// Check if utility of the new bid is lower than utility of the opponent's last bid
				// if yes then accept last bid of the opponent.
				if (utilitySpace.getUtility(lOppntBid) >= utilitySpace
						.getUtility(myLastBid))
					// Opponent bids equally, or outbids my previous bid, so lets
					// accept
					lAction = new Accept(this, lOppntBid);			
				break;
			case ACCEPT: // Presumably, opponent accepted last bid, but let's
				// check...
				lOppntBid = ((Accept) messageOpponent).getBid();
				if (lOppntBid.equals(myLastBid))
					lAction = new Accept(this, myLastBid);
				else
					lAction = new Offer(this, myLastBid);
				break;
			case BREAKOFF:
				// nothing left to do. Negotiation ended, which should be checked by
				// Negotiator...
				break;
			default:
				// I am starting, but not sure whether Negotiator checks this, so
				// lets check also myLastAction...
				if (myLastAction == null)
					lAction = proposeInitialBid();
				else
					// simply repeat last action
					lAction = myLastAction;
			break;
			}
		}
		catch (Exception e)
		{ 
			System.out.println("Exception in chooseAction:"+e.getMessage());
			e.printStackTrace();
		}
		myLastAction = lAction;
		return lAction;
	}

	private ACTIONTYPE getActionType(Action lAction) {
		ACTIONTYPE lActionType = ACTIONTYPE.START;
		if (lAction instanceof Offer)
			lActionType = ACTIONTYPE.OFFER;
		else if (lAction instanceof Accept)
			lActionType = ACTIONTYPE.ACCEPT;
		else if (lAction instanceof EndNegotiation)
			lActionType = ACTIONTYPE.BREAKOFF;
		return lActionType;
	}

	public void loadUtilitySpace(String fileName) {

		utilitySpace = new SimpleUtilitySpace(domain, fileName);

		//load similarity info from the utility space
//		fSimilarity = new Similarity(utilitySpace.getDomain());
//		fSimilarity.loadFromXML(utilitySpace.getXMLRoot());
		fOpponentModel = new BayesianOpponentModel(utilitySpace);
	}

	private Bid getBidRandomWalk(double targetUtility) {
		Bid lBid = null, lBestBid = null;

		// Return bid that gets closest to target utility in a "random walk"
		// search.
		lBestBid = domain.getRandomBid();
		return lBestBid;
	}

	private double getTargetUtility(double myUtility, double oppntUtility) {
		return myUtility -getConcessionFactor();
	}


	private double getConcessionFactor() {
		// The more the agent is willing to concess on its aspiration value, the
		// higher this factor.
		return CONCESSIONFACTOR;
	}

}
