package agents;

import java.util.ArrayList;
import java.util.Date;

import negotiator.Agent;
import negotiator.Bid;
import negotiator.BidIterator;
import negotiator.NegotiationTemplate;
import negotiator.actions.Accept;
import negotiator.actions.Action;
import negotiator.actions.EndNegotiation;
import negotiator.actions.Offer;
import agents.similarity.Similarity;
import negotiator.issue.IssueDiscrete;
import negotiator.issue.Value;
import negotiator.issue.ValueDiscrete;
import negotiator.issue.ValueReal;
import negotiator.utility.EvaluatorDiscrete;
import negotiator.utility.EvaluatorReal;
import negotiator.utility.UtilitySpace;


public class SimilarityAgent extends Agent {
	private String myName;
	private Action messageOpponent;
	private int sessionNumber;
	private int sessionTotalNumber;
	private int nrOfIssues;
	private Bid myLastBid = null;
	private Action myLastAction = null;
	private Similarity fSimilarity;
	private static final double BREAK_OFF_POINT = 0.5;
	private double[] lIssueWeight;
	private enum ACTIONTYPE { START, OFFER, ACCEPT, BREAKOFF };
	private enum STRATEGY {SMART, SERIAL, RESPONSIVE, RANDOM};
	private STRATEGY fStrategy = STRATEGY.SMART;
	private int fSmartSteps;
	private static final double CONCESSIONFACTOR = 0.03;
	private static final double ALLOWED_UTILITY_DEVIATION = 0.01;
	private static final int NUMBER_OF_SMART_STEPS = 2; 
	
	// Class constructor
	public SimilarityAgent() {
		super();
	}

	public void init(int sessionNumber, int sessionTotalNumber, Date startTimeP, 
		Integer totalTimeP, UtilitySpace us) {
		super.init(sessionNumber, sessionTotalNumber, startTimeP, totalTimeP, us);
		messageOpponent = null;
		myLastBid = null;
		myLastAction = null;
		fSmartSteps = 0;
		//load similarity info from the utility space
		fSimilarity = new Similarity(utilitySpace.getDomain());
		fSimilarity.loadFromXML(utilitySpace.getXMLRoot());
		
	}


	// Class methods
	public void ReceiveMessage(Action opponentAction) {
		messageOpponent = opponentAction;
	}

	private Action proposeInitialBid() {
		Bid lBid = null;
/*		Value[] values = new Value[4];
		if(myName.equals("Buyer"))	{
			values[0] = new ValueReal(0.6);
			values[1] = new ValueReal(0.9);
			values[2] = new ValueReal(0.6);
			values[3] = new ValueReal(1);
		} else {
			values[0] = new ValueReal(0);
			values[1] = new ValueReal(0.2);
			values[2] = new ValueReal(0);
			values[3] = new ValueReal(0.5);
		}
		lBid = new Bid(utilitySpace.getDomain(), values);*/
		// Return (one of the) possible bid(s) with maximal utility.
		try {
			lBid = utilitySpace.getMaxUtilityBid();
			lBid = getBidRandomWalk(utilitySpace.getUtility(lBid)*0.96, utilitySpace.getUtility(lBid));
		} catch (Exception e) {
			e.printStackTrace();
		}
		fSmartSteps=NUMBER_OF_SMART_STEPS+1;
		myLastBid = lBid;
		
		return new Offer(this, lBid);
		
	}
	private Bid getNextBidSmart(Bid pOppntBid) {
		double lMyUtility=0, lOppntUtility=0, lTargetUtility;
		// Both parties have made an initial bid. Compute associated utilities from my point of view.
		try {
			lMyUtility = utilitySpace.getUtility(myLastBid);
			lOppntUtility = utilitySpace.getUtility(pOppntBid);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if(fSmartSteps>NUMBER_OF_SMART_STEPS) {
			lTargetUtility = getTargetUtility(lMyUtility, lOppntUtility);
			fSmartSteps=0;
		} else {
			lTargetUtility = lMyUtility; 
			fSmartSteps++;
		}
		Bid lMyLastBid = myLastBid;
		Bid lBid = getTradeOffExhaustive(lTargetUtility, pOppntBid);
		if(Math.abs(fSimilarity.getSimilarity(lMyLastBid, lBid))>0.993) {
			lTargetUtility = getTargetUtility(lMyUtility, lOppntUtility);
			fSmartSteps=0;
			lBid = getTradeOffExhaustive(lTargetUtility, pOppntBid);
		}
		return lBid;
	}
/*	private Bid getTradeOff(double pUtility, Bid pOppntBid, double pE) {
		ArrayList<Object> lE = new ArrayList<Object>();
		for(int i=0;i<utilitySpace.getNrOfEvaluators();i++) {
			switch(utilitySpace.getEvaluator(i).getType()) {
			case DISCRETE:
				ArrayList<Double> lDeltas = new ArrayList<Double>();
				IssueDiscrete lIssue = (IssueDiscrete)(utilitySpace.getIssue(i));
				EvaluatorDiscrete lEval = (EvaluatorDiscrete)(utilitySpace.getEvaluator(i));
				for(int j=0;j<lIssue.getNumberOfValues();j++) {
					double lDelta = lEval.getEvaluation(lIssue.getValue(j))-lEval.getEvaluation((ValueDiscrete)(pOppntBid.getValue(i))); 
					if(lDelta > 0)
						lDeltas.add(lDelta);
				}
				lE.add(lDeltas);
				break;				
			case REAL:
			case PRICE:
				EvaluatorReal lEvalReal = (EvaluatorReal)(utilitySpace.getEvaluator(i));
				lE.add(new Double(1-lEvalReal.getEvaluation(((ValueReal)(pOppntBid.getValue(i))).getValue())));
				break;
			}//switch
		}//for
		//calculate Emax
		double lEmax = 0;
		for(int i=0;i<utilitySpace.getNrOfEvaluators();i++) {
			switch(utilitySpace.getEvaluator(i).getType()) {
			case DISCRETE:
				ArrayList<Double> lDeltas = (ArrayList<Double>) (lE.get(i));
				lEmax += utilitySpace.getWeight(i)*getMaxE(lDeltas);
				break;				
			case REAL:
			case PRICE:
				lEmax += utilitySpace.getWeight(i)*(Double)(lE.get(i));
				break;
			}
		}
		//small delta (3)
		double lSmallDelta = 0.01 * lEmax;
		if(lEmax>pE+lSmallDelta) {
			int k =0;
			double lEn = 0;
			ArrayList<Double[]> r = new ArrayList<Double[]>();
			while(lEn<pE) {
				r.add(new Double[utilitySpace.getNrOfEvaluators()]);
				k++;
				for(int i=0;i<utilitySpace.getNrOfEvaluators();i++) {
					if(lEn<pE) {
						switch(utilitySpace.getEvaluator(i).getType()) {
						case DISCRETE:							
							ArrayList<Double> lDeltas = (ArrayList<Double>) (lE.get(i));
							ArrayList<Double> lNewDeltas = new ArrayList<Double>(); 
							for(int n=0;n<lDeltas.size();n++)
								if(lDeltas.get(n)<=(pE-lEn)/utilitySpace.getWeight(i));
							int lRandomIndex = (new Double(Math.random()*lDeltas.size())).intValue();
							r.get(k-1)[i] = lNewDeltas.get(lRandomIndex);							
							break;				
						case REAL:
						case PRICE:
							Double lEUpperBound = (Double)(lE.get(i));
							double lRandomValue = Math.random()*lEUpperBound;
							if(lRandomValue<(pE-lEn)/utilitySpace.getWeight(i))
								r.get(k-1)[i] = lRandomValue;
							else
								r.get(k-1)[i] = (pE-lEn)/utilitySpace.getWeight(i);
							break;
						}
						
					} else
						r.get(k-1)[i] = new Double(0);
					lEn += utilitySpace.getWeight(i)*r.get(k-1)[i];
					//recalculate Es
					lE.clear();
					switch(utilitySpace.getEvaluator(i).getType()) {
					case DISCRETE:
						ArrayList<Double> lDeltas = new ArrayList<Double>();
						IssueDiscrete lIssue = (IssueDiscrete)(utilitySpace.getIssue(i));
						EvaluatorDiscrete lEval = (EvaluatorDiscrete)(utilitySpace.getEvaluator(i));
						//calculate total r
						double r_total = 0;
						for(int j=i;j<=k;j++) r_total += r.get(j)[i];
						for(int j=0;j<lIssue.getNumberOfValues();j++) {
							double lDelta = lEval.getEvaluation(lIssue.getValue(j))-lEval.getEvaluation((ValueDiscrete)(pOppntBid.getValue(i)))-r_total; 
							if(lDelta > 0)
								lDeltas.add(lDelta);
						}
						lE.add(lDeltas);
						break;				
					case REAL:
					case PRICE:
						EvaluatorReal lEvalReal = (EvaluatorReal)(utilitySpace.getEvaluator(i));
						lE.add(new Double(1-lEvalReal.getEvaluation(((ValueReal)(pOppntBid.getValue(i))).getValue())));
						break;
					}
					
				}//for
			}//while
			Value[] lValues = new Value[utilitySpace.getNrOfEvaluators()];
			for(int i=0;i<utilitySpace.getNrOfEvaluators();i++) {
				double lEIncrease = 0;
				for(int j=1;j<=k;j++) lEIncrease +=r.get(j)[i];
				
			} //for
		} //if
	}*/
	private double getMaxE(ArrayList<Double> pE) {
		double lMax = pE.get(0);
		for(int i=1;i<pE.size();i++)
			if(pE.get(i)>lMax)
				lMax = pE.get(i);
		return lMax;
	}
	private Bid getTradeOffExhaustive(double pUtility, Bid pOppntBid) {
		Bid lBid=null;
		double lSim = -1;
		BidIterator lIter = new BidIterator(utilitySpace.getDomain());
		while(lIter.hasNext()) {
			Bid tmpBid = lIter.next();
			double lUtil = 0;
			try {
				lUtil = utilitySpace.getUtility(tmpBid);
			} catch (Exception e) {
				e.printStackTrace();
			}
			if(Math.abs(lUtil-pUtility)<ALLOWED_UTILITY_DEVIATION) {
				double lTmpSim = fSimilarity.getSimilarity(tmpBid, pOppntBid);
				if(lTmpSim>lSim) {
					lSim = lTmpSim;
					lBid = tmpBid;
				}
			}				
		} //while
		return lBid;
	}
	private Action proposeNextBid(Bid pOppntBid) {
		Bid lBid = null;
		switch(fStrategy) {
		case SMART:
			lBid = getNextBidSmart(pOppntBid);
			break;
		}
		myLastBid = lBid;
		return new Offer(this, lBid);
	}

	public Action chooseAction(){
		Action lAction = null;
		ACTIONTYPE lActionType;
		Bid lOppntBid = null;

		lActionType = getActionType(messageOpponent);
		try{
		switch (lActionType) {
		case OFFER: // Offer received from opponent
			lOppntBid = ((Offer) messageOpponent).getBid();
			if (myLastAction == null)
				// Other agent started, lets propose my initial bid.
				lAction = proposeInitialBid();
			else if (utilitySpace.getUtility(lOppntBid) >= utilitySpace
					.getUtility(myLastBid))
				// Opponent bids equally, or outbids my previous bid, so lets
				// accept
				lAction = new Accept(this);
			else
				// Propose counteroffer. Get next bid.
				lAction = proposeNextBid(lOppntBid);
				// Check if utility of the new bid is lower than utility of the opponent's last bid
				// if yes then accept last bid of the opponent.
				if (utilitySpace.getUtility(lOppntBid) >= utilitySpace
					.getUtility(myLastBid))
					// Opponent bids equally, or outbids my previous bid, so lets
					// accept
				lAction = new Accept(this);			
			break;
		case ACCEPT: // Presumably, opponent accepted last bid, but let's
			// check...
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
		} catch(Exception e) {
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


	private Bid getBidRandomWalk(double lowerBound, double upperBoud) throws Exception{
		Bid lBid = null, lBestBid = null;

		// Return bid that gets closest to target utility in a "random walk"
		// search.
		lBestBid = utilitySpace.getDomain().getRandomBid();
		while(true) {
			lBid = utilitySpace.getDomain().getRandomBid();
			if ((utilitySpace.getUtility(lBid) > lowerBound)&&
					(utilitySpace.getUtility(lBestBid) < upperBoud)) {
				lBestBid = lBid;
				break;
			}
				
		}
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
