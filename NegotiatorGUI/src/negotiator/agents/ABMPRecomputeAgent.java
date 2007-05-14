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

import negotiator.*;
import negotiator.actions.*;
import negotiator.issue.*;
import negotiator.utility.EvaluatorDiscrete;
import negotiator.utility.EvaluatorPrice;
import negotiator.utility.EvaluatorReal;

/**
 * 
 * @author Dmytro Tykhonov & Koen Hindriks
 * 
 */

// TODO: Rewrite ABMPRewrite code such that it is compatible with issue/evaluator additions.

public class ABMPRecomputeAgent extends Agent {
	private String myName;
	private Action messageOpponent;
	private int sessionNumber;
	private int sessionTotalNumber;
	private int nrOfIssues;
	private Bid myLastBid = null;
	private Action myLastAction = null;
	private static final double BREAK_OFF_POINT = 0.5;
	private double[] lIssueWeight;
	private int[] lSortedIssueIndex; // Issue indeces sorted in order of priority 
	private enum ACTIONTYPE { START, OFFER, ACCEPT, BREAKOFF };

	// Used in ABMP
	private static final double NEGOTIATIONSPEED = 0.3;
	private static final double CONCESSIONFACTOR = 0.8;
	private static final double CONFTOLERANCE = 0;

	// Code is independent from AMPO vs CITY case, but some specifics about
	// using this case as test are specified below.
	// ****************************************************************************************************
	// AMPO VS CITY: Outcome space has size of about 7 milion.
	// ****************************************************************************************************
	// ******************************************************** *******************************************
	// ABMP "gets stuck" on AMPO vs CITY. The search through the space is not effective in discrete outcome
	// spaces. Even with very high negotiation speed parameters (near 1) no bid can be found with the target utility
	// at a certain point. In a discrete space, the evaluation distance between two different values on an
	// issue need to be taken into account, which may differ from value to value... In such spaces one strategy
	// would be to consider which combination of concessions on a set of issues would provide 
	// ******************************************************** *******************************************

	// Class constructor
	public ABMPRecomputeAgent() {
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

	// Class methods
	public void ReceiveMessage(Action opponentAction) {
		messageOpponent = opponentAction;
	}

	private Action proposeInitialBid() {
		Bid lBid = null;
		// Return (one of the) outcome(s) with maximal utility.
		lBid = getMaxUtilityBid();
		myLastBid = lBid;
		return new Offer(this, lBid); // QUESTION: what is an offer with a null-bid??
	}

	private Action proposeNextBid(Bid lOppntBid) {
		Bid lBid = null;
		double lMyUtility, lOppntUtility, lTargetUtility;
		// Both parties have made an initial bid. Compute associated utilities from my point of view.
		lMyUtility = utilitySpace.getUtility(myLastBid);
		lOppntUtility = utilitySpace.getUtility(lOppntBid);
		lTargetUtility = getTargetUtility(lMyUtility, lOppntUtility);
		lBid = getBidABMPrecompute(lTargetUtility);
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
			else if (utilitySpace.getUtility(lOppntBid) >= utilitySpace
					.getUtility(myLastBid))
				// Opponent bids equally, or outbids my previous bid, so lets
				// accept
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
			// nothing left to do. DOC: What does Negotiator Manager do in this case?
			break;
		default:
			// I am starting, but not sure whether Negotiator checks this, so lets check also myLastAction...
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
		double[] lWeights;
		
		utilitySpace = new SimpleUtilitySpace(getNegotiationTemplate().getDomain(), fileName);
	
		nrOfIssues = getNegotiationTemplate().getDomain().getNumberOfIssues();
		lIssueWeight = new double[nrOfIssues];
		lWeights = new double[nrOfIssues];
		lSortedIssueIndex = new int[nrOfIssues];
		for (int i=0; i<nrOfIssues; i++) {
			lIssueWeight[i] = this.utilitySpace.getWeight(i);
			lWeights[i] = lIssueWeight[i];
			lSortedIssueIndex[i] = i;
		}
		
		quickSort(lWeights, lSortedIssueIndex, 0, nrOfIssues-1);
	}

	// ABMP Specific Code

	private Bid getBidABMPrecompute(double targetUtility) {
		Bid lBid = null;
		Value[] lIssueIndex = new Value[nrOfIssues];
		double[] lBE = new double[nrOfIssues];
		double[] lBTE = new double[nrOfIssues];
		double[] lTE = new double[nrOfIssues];
		double lUtility = 0, lNF = 0, lAlpha, lUtilityGap, lIssueConcessionLeftOver;

		// Method assumes this is second bid. Use method proposeInitialBid to do first bid.
		lUtilityGap = targetUtility - utilitySpace.getUtility(myLastBid);
		System.out.println(myName);
		for (int i = 0; i < nrOfIssues; i++) {
			//Change by DT: use Evaluator to get the evaluation values
			lBE[i] = (Double)(utilitySpace.getEvaluator(i).getEvaluation(utilitySpace, myLastBid, i));//, myLastBid.getValue(i)) / 100;
			System.out.print("Old value "); System.out.print(i+1); System.out.print(":");
			System.out.println(lBE[i]);
		}

		// STEP 1: Compute concession on each issue.
		// Retrieve issue weights and compute normalisation factor
		for (int i = 0; i < nrOfIssues; i++) {
			lAlpha = (1 - lIssueWeight[i]) * lBE[i]; // * (1 - lBE[i]); This factor is not right??
			lNF = lNF + lIssueWeight[i] * lAlpha;
			lIssueWeight[i] = lAlpha;
		}

		// Compute basic target evaluations per issue, starting with highest priority issue
		for (int i = 0; i < nrOfIssues; i++) {
			// Use normalized values!
			int lIndex = lSortedIssueIndex[i];
			lBTE[lIndex] = (Double)(utilitySpace.getEvaluator(i).getEvaluation(utilitySpace, myLastBid, i))+ (lIssueWeight[lIndex] / lNF) * lUtilityGap;//utilitySpace.getEvaluation(i, myLastBid.getValue(i))			
			// STEP 2: Add configuration tolerance for opponent's bid
			lUtility =(Double)(utilitySpace.getEvaluator(lIndex).getEvaluation(utilitySpace,((Offer) messageOpponent).getBid(), lIndex)); 
				
			lTE[lIndex] = (1 - CONFTOLERANCE) * lBTE[lIndex] + CONFTOLERANCE * lUtility;
			lTE[lIndex] = lTE[lIndex];
			System.out.println(lTE[lIndex]);

			// STEP 3: Find bid in outcome space with issue target utilities
			// corresponding with those computed above.
			// ASSUMPTION: There is always a UNIQUE issue value with utility closest
			// to the target evaluation.
			lUtility = 1;
			// Assumes: Discrete-valued issues only!
			
			Issue lIssue = getNegotiationTemplate().getDomain().getIssue(lIndex);
			if(lIssue.getType() == discrete) {
				IssueDiscrete lIssueDiscrite =(IssueDiscrete)lIssue;
				for (int j = 0; j < lIssueDiscrite.getNumberOfValues(); j++) {
					double lEvalValue = ((EvaluatorDiscrete) utilitySpace.getEvaluator(i)).getEvaluation(lIssueDiscrite.getValue(j)); 
					if (Math.abs(lTE[lIndex] - lEvalValue) < lUtility) {
						lIssueIndex[lIndex] = lIssueDiscrite.getValue(j);
						lUtility = Math.abs(lTE[lIndex]- lEvalValue);
					}//if
				}//for
			}
			if (Math.abs(lTE[lIndex] - utilitySpace.getEvaluation(lIndex, j)) < lUtility) {
				lIssueIndex[lIndex] = ((IssueDiscrete)getNegotiationTemplate().getDomain().getIssue(lIndex)).getValue(j);
				lUtility = Math.abs(lTE[lIndex] - utilitySpace.getEvaluation(lIndex, j));
			}
			
			// STEP 4: RECOMPUTE size of remaining concession step
			// Reason: Issue value may not provide exact match with basic target evaluation value.
			// NOTE: This recomputation also includes any concession due to configuration tolerance parameter...
			// First compute difference between actual concession on issue and target evaluation.
			lIssueConcessionLeftOver = utilitySpace.getEvaluation(lIndex, lIssueIndex[lIndex]) - lTE[lIndex];
			lUtilityGap = lUtilityGap + lIssueConcessionLeftOver;
			lNF = lNF-lIssueWeight[lIndex];
		}
		return new Bid(getNegotiationTemplate().getDomain(),lIssueIndex);
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
