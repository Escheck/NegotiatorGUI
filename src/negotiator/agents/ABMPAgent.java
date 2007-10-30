/*
 * SimpleAgent.java
 *
 * Created on November 6, 2006, 9:55 AM
 *
 */

package negotiator.agents;

import java.util.Collections;
import java.util.Comparator;
import java.util.Vector;
import java.util.HashMap;

import negotiator.*;
import negotiator.actions.*;
import negotiator.issue.*;
import negotiator.utility.*;

/**
 * 
 * @author Dmytro Tykhonov & Koen Hindriks
 */

public class ABMPAgent extends Agent {
	private String myName;
	private Action messageOpponent;
	private int sessionNumber;
	private int sessionTotalNumber;
	private int nrOfIssues;
	private Bid myLastBid = null;
	private Action myLastAction = null;
	private static final double BREAK_OFF_POINT = 0.5;
	private double[] fIssueWeight;
	private enum ACTIONTYPE { START, OFFER, ACCEPT, BREAKOFF };

	// Paraters used in ABMP strategy
	// TODO: Include these parameters as agent parameters in agent's utility template.
	// QUESTION: How to do that nicely, since these parameters are strategy specific?
	private static final double NEGOTIATIONSPEED = 0.05; // TODO: Probably still somewhere a bug. When you set this too low (e.g. 0.05), no deal is reached and no concession is done!
	private static final double CONCESSIONFACTOR = 0.8;
	private static final double CONFTOLERANCE = 0;
	private static final double UTIlITYGAPSIZE = 0.02; // Accept when utility gap is <= UTILITYGAPSIZE.
	// CHECK: Utility gap size needed since concession steps get VERY small when opponent's last bid utility is
	// close to own last bid utility.

	// Code is independent from AMPO vs CITY case, but some specifics about
	// using this case as test are specified below.
	// ****************************************************************************************************
	// AMPO VS CITY: Outcome space has size of about 7 milion.
	// ****************************************************************************************************
	// ******************************************************** *******************************************
	// CHECK: ABMP gets stuck on the Car Example with a negotiation speed of less than 0.05!!
	// ABMP "gets stuck" on AMPO vs CITY. The search through the space is not effective in discrete outcome
	// spaces. Even with very high negotiation speed parameters (near 1) no bid can be found with the target utility
	// at a certain point. In a discrete space, the evaluation distance between two different values on an
	// issue need to be taken into account, which may differ from value to value... In such spaces one strategy
	// would be to consider which combination of concessions on a set of issues would provide 
	// ******************************************************** *******************************************

	/** Creates a new instance of MyAgent */

	public ABMPAgent() {
		super();
	}

	protected void init(int sessionNumber, int sessionTotalNumber,NegotiationTemplate nt) {		
		super.init(sessionNumber, sessionTotalNumber, nt);
		myName = super.getName();
		this.sessionNumber = sessionNumber;
		this.sessionTotalNumber = sessionTotalNumber;

		messageOpponent = null;
		myLastBid = null;
		myLastAction = null;
	}

	public void ReceiveMessage(Action opponentAction) {
		messageOpponent = opponentAction;
	}

	private Action proposeInitialBid() {
		Bid lBid;
		
		// Return (one of the) possible bid(s) with maximal utility.
		lBid = getMaxUtilityBid();
		myLastBid = lBid;
		return new Offer(this, getMaxUtilityBid());
	}

	private Action proposeNextBid(Bid lOppntBid) throws Exception{
		Bid lBid = null;
		double lMyUtility, lOppntUtility, lTargetUtility;
		// Both parties have made an initial bid. Compute associated utilities from my point of view.
		lMyUtility = utilitySpace.getUtility(myLastBid);
		lOppntUtility = utilitySpace.getUtility(lOppntBid);
		lTargetUtility = getTargetUtility(lMyUtility, lOppntUtility);
		lBid = getBidABMPsimple(lTargetUtility);
		myLastBid = lBid;
		return new Offer(this, lBid);
	}

	public Action chooseAction() {
		Action lAction = null;
		ACTIONTYPE lActionType;
		Bid lOppntBid = null;

		lActionType = getActionType(messageOpponent);
		switch (lActionType) {
		case OFFER: // Offer received from opponent
			lOppntBid = ((Offer) messageOpponent).getBid();
			if (myLastAction == null)
				// Other agent started, lets propose my initial bid.
				lAction = proposeInitialBid();
			else if (utilitySpace.getUtility(lOppntBid) >= (utilitySpace.getUtility(myLastBid))-UTIlITYGAPSIZE)
				// Opponent bids equally, or outbids my previous bid, so lets accept.
				lAction = new Accept(this, lOppntBid);
			else
				// Propose counteroffer. Get next bid.
				lAction = proposeNextBid(lOppntBid);
			break;
		case ACCEPT: // Presumably, opponent accepted last bid, but let's check...
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
		
		utilitySpace = new SimpleUtilitySpace(getNegotiationTemplate().getDomain(), fileName);
	
		nrOfIssues = getNegotiationTemplate().getDomain().getNumberOfIssues();
		fIssueWeight = new double[nrOfIssues];
		for (int i=0; i<nrOfIssues; i++) {
			fIssueWeight[i] = this.utilitySpace.getWeight(i);
		}
	}

	// ABMP Specific Code

	private Bid getBidABMPsimple(double targetUtility) {
		//Value[] lIssueIndex = new Value[nrOfIssues];
		HashMap<Integer, Value> lIssueIndex = new HashMap<Integer, Value>();
		double[] lIssueAlpha = new double[nrOfIssues];
		double[] lBE = new double[nrOfIssues];
		double[] lBTE = new double[nrOfIssues];
		double[] lTE = new double[nrOfIssues];
		double lUtility = 0, lNF = 0, lAlpha, lUtilityGap, lTotalConcession = 0;

		// ASSUMPTION: Method computes a second bid. Method proposeInitialBid is used to compute first bid.
		lUtilityGap = targetUtility - utilitySpace.getUtility(myLastBid);
		for (int i = 0; i < nrOfIssues; i++) {
			lBE[i] = (Double)(utilitySpace.getEvaluator(i).getEvaluation(utilitySpace, myLastBid, i));
		}

		// STEP 1: Retrieve issue value for last bid and compute concession on each issue.
		for (int i = 0; i < nrOfIssues; i++) {
			lAlpha = (1 - fIssueWeight[i]) * lBE[i]; // CHECK: (1 - lBE[i]); This factor is not right??
			lNF = lNF + fIssueWeight[i] * lAlpha;
			lIssueAlpha[i] = lAlpha;
		}

		// Compute basic target evaluations per issue
		for (int i = 0; i < nrOfIssues; i++) {
			lBTE[i] = lBE[i] + (lIssueAlpha[i] / lNF) * lUtilityGap;
		}

		// STEP 2: Add configuration tolerance for opponent's bid
		for (int i = 0; i < nrOfIssues; i++) {
			lUtility =(Double)(utilitySpace.getEvaluator(i).getEvaluation(utilitySpace,((Offer) messageOpponent).getBid(), i));
			lTE[i] = (1 - CONFTOLERANCE) * lBTE[i] + CONFTOLERANCE * lUtility;
		}

		// STEP 3: Find bid in outcome space with issue target utilities corresponding with those computed above.
		// ASSUMPTION: There is always a UNIQUE issue value with utility closest to the target evaluation.
		// First determine new values for discrete-valued issues.
		double lEvalValue;
		int lNrOfRealIssues = 0;
		for (int i = 0; i < nrOfIssues; i++) {
			lUtility = 1; // ASSUMPTION: Max utility = 1.
			Objective lIssue = getNegotiationTemplate().getDomain().getIssue(i);
			if(lIssue.getType() == discrete) {
				IssueDiscrete lIssueDiscrete =(IssueDiscrete)lIssue;
				for (int j = 0; j < lIssueDiscrete.getNumberOfValues(); j++) {
					lEvalValue = ((EvaluatorDiscrete) utilitySpace.getEvaluator(i)).getEvaluation(lIssueDiscrete.getValue(j));
					if (Math.abs(lTE[i] - lEvalValue) < lUtility) {
//						lIssueIndex[i] = lIssueDiscrete.getValue(j);
						lIssueIndex.put(new Integer(i), lIssueDiscrete.getValue(i));
						lUtility = Math.abs(lTE[i]- lEvalValue);
					}//if
				}//for
	//FIXME		lTotalConcession += fIssueWeight[i]*(lBE[i] - ((EvaluatorDiscrete) utilitySpace.getEvaluator(i)).getEvaluation((ValueDiscrete)lIssueIndex[i]));
			} else if (lIssue.getType() == real)
				lNrOfRealIssues += 1;
		}
		
		// TODO: Still need to integrate integer-valued issues somewhere here. Low priority.
		
		// STEP 4: RECOMPUTE size of remaining concession step
		// Reason: Issue value may not provide exact match with basic target evaluation value.
		// NOTE: This recomputation also includes any concession due to configuration tolerance parameter...
		// First compute difference between actual concession on issue and target evaluation.
		// TODO: Think about how to (re)distribute remaining concession over MULTIPLE real issues. In car example
		// not important. Low priority.
		double lRestUtitility = lUtilityGap + lTotalConcession;
		// Distribute remaining utility of real and/or price issues. Integers still to be done. See above.
		for (int i = 0; i < nrOfIssues; i++) {
			Objective lIssue = getNegotiationTemplate().getDomain().getIssue(i);
			if(lIssue.getType() == real) {
				lTE[i] += lRestUtitility/lNrOfRealIssues;
				switch(utilitySpace.getEvaluator(i).getType()) {
				case REAL:
					EvaluatorReal lRealEvaluator=(EvaluatorReal) (utilitySpace.getEvaluator(i));
					double r = lRealEvaluator.getValueByEvaluation(lTE[i]);
//					lIssueIndex[i] = new ValueReal(r);
					lIssueIndex.put(new Integer(i), new ValueReal(r));
					break;
				case PRICE:
					EvaluatorPrice lPriceEvaluator=(EvaluatorPrice)(utilitySpace.getEvaluator(i));
//					lIssueIndex [i] =  new ValueReal(lPriceEvaluator.getLowerBound());
					lIssueIndex.put(new Integer(i), new ValueReal(lPriceEvaluator.getLowerBound()));
					Bid lTempBid = new Bid(getNegotiationTemplate().getDomain(), lIssueIndex);
//					lIssueIndex[i] =  lPriceEvaluator.getValueByEvaluation(utilitySpace, lTempBid, lTE[i]);
					lIssueIndex.put(new Integer(i), lPriceEvaluator.getValueByEvaluation(utilitySpace, lTempBid, lTE[i]));
					break;
				}
			}
		}
		
		return new Bid(getNegotiationTemplate().getDomain(), lIssueIndex);
	}

	private double getTargetUtility(double myUtility, double oppntUtility) {
		return myUtility + getConcessionStep(myUtility, oppntUtility);
	}

	private double getNegotiationSpeed() {
		return NEGOTIATIONSPEED;
	}

	private double getConcessionFactor() {
		// The more the agent is willing to concess on its aspiration value, the
		// higher this factor.
		return CONCESSIONFACTOR;
	}

	private double getConcessionStep(double myUtility, double oppntUtility) {
		double lConcessionStep = 0, lMinUtility = 0, lUtilityGap = 0;

		// Compute concession step
		lMinUtility = 1 - getConcessionFactor();
		lUtilityGap = (oppntUtility - myUtility);
		lConcessionStep = getNegotiationSpeed() * (1 - lMinUtility / myUtility) * lUtilityGap;
		System.out.println(lConcessionStep);
		return lConcessionStep;
	}

	
	// Quicksort algorithm that returns a sorted list of issue indices based on weights
	
	private void quickSort(double[] lWeights, int[] lSortedIndex, int left, int right) {
		int pivotIndex;
		
	    // if (right > left)
    	pivotIndex = left;
	    int pivotNewIndex = partition(lWeights, lSortedIndex, left, right, pivotIndex);
	    if (pivotNewIndex > left+1)
	    	quickSort(lWeights, lSortedIndex, left, pivotNewIndex-1);
	    if (pivotNewIndex+1<right)
	    	quickSort(lWeights, lSortedIndex, pivotNewIndex+1, right);
	}
	
	private int partition(double[] lWeights, int[] lSortedIndex, int left, int right, int pivotIndex) {
	    double pivotValue = lWeights[pivotIndex];
	    swap(lWeights, pivotIndex, right); // Move pivot to end
	    swap(lSortedIndex, pivotIndex, right);
	    int storeIndex = left;
        for (int i=left; i<right; i++) {
            if (lWeights[i] <= pivotValue) {
            	swap(lWeights, storeIndex, i);
            	swap(lSortedIndex, storeIndex, i);
            	storeIndex = storeIndex + 1;
            }
        }
        swap(lWeights, right, storeIndex); // Move pivot to its final place
        swap(lSortedIndex, right, storeIndex);
        return storeIndex;
	}
	
	private void swap(double[] values, int fromIndex, int toIndex) {
		double x = values[fromIndex];
		values[fromIndex] = values[toIndex];
		values[toIndex] = x;
	}
	
	private void swap(int[] values, int fromIndex, int toIndex) {
		int x = values[fromIndex];
		values[fromIndex] = values[toIndex];
		values[toIndex] = x;
	}

	
}
