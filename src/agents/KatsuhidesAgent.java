package agents;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import negotiator.Agent;
import negotiator.Bid;
import negotiator.Timeline;
import negotiator.actions.Accept;
import negotiator.actions.Action;
import negotiator.actions.Offer;
import negotiator.issue.Issue;
import negotiator.issue.IssueInteger;
import negotiator.issue.Value;
import negotiator.issue.ValueInteger;
import negotiator.utility.*;


/**
 * @author K.Fujita
 * 
 * Some improvements over the standard SimpleAgent.
 *
 * Random Constraint Walker, Zero Intelligence Agent
 */

public class KatsuhidesAgent extends Agent 
	private Action actionOfPartner=null;
	/** Note: {@link SimpleAgent} does not account for the discount factor in its computations */
	private static double MINIMUM_UTILITY = 0.0;

	/**
	 * init is called when a next session starts with the same opponent.
	 */
	public void init()
	{
		MINIMUM_UTILITY = utilitySpace.getReservationValueUndiscounted();
		System.out.println("Minimum bid utility: " + MINIMUM_UTILITY);		
		
	}

	public static String getVersion() { return "5.0"; }

	@Override
	public String getName()
	{
		return "Simple ANAC2014 Agent";
	}

	public void ReceiveMessage(Action opponentAction)
	{
		actionOfPartner = opponentAction;
	}


	public Action chooseAction()
	{
		Action action = null;
		try
		{
			if(actionOfPartner==null) action = chooseRandomBidAction();
			if(actionOfPartner instanceof Offer)
			{
				Bid partnerBid = ((Offer)actionOfPartner).getBid();
				double offeredUtilFromOpponent = getUtility(partnerBid);
				// get current time
				double time = timeline.getTime();
				action = chooseRandomBidAction();

				Bid myBid = ((Offer) action).getBid();
				double myOfferedUtil = getUtility(myBid);

				// accept under certain circumstances
				if (isAcceptable(offeredUtilFromOpponent, myOfferedUtil, time))
					action = new Accept(getAgentID());

			}
			if (timeline.getType().equals(Timeline.Type.Time)) {
				sleep(0.005); // just for fun
			}
		} catch (Exception e) {
			System.out.println("Exception in ChooseAction:"+e.getMessage());
			action=new Accept(getAgentID()); // best guess if things go wrong.
		}
		return action;
	}

	private boolean isAcceptable(double offeredUtilFromOpponent, double myOfferedUtil, double time) throws Exception
	{
		double P = Paccept(offeredUtilFromOpponent,time);
		if (P > Math.random())
			return true;
		return false;
	}

	/**
	 * Wrapper for getRandomBid, for convenience.
	 * @return new Action(Bid(..)), with bid utility > MINIMUM_BID_UTIL.
	 * If a problem occurs, it returns an Accept() action.
	 */
	private Action chooseRandomBidAction()
	{
		Bid nextBid=null ;
		try {
			nextBid = getRandomBid();
			}catch (Exception e) {
			System.out.println("Problem with received bid:"+e.getMessage()+". cancelling bidding");
			}
		if (nextBid == null) return (new Accept(getAgentID()));
		return (new Offer(getAgentID(), nextBid));
	}

	/**
	 * @return a random bid including a random constraint with high enough utility value.
	 * @throws Exception if we can't compute the utility (eg no evaluators have been set)
	 * or when other evaluators than a DiscreteEvaluator are present in the util space.
	 */
	private Bid getRandomBid() throws Exception
	{
		Random randomnr= new Random();
		// select a random constraint with utility >= MINMUM_BID_UTIL
		// create a random bid from the area of the constraint.
		// note that this may never succeed if you set MINIMUM too high!!!
		// in that case we will search for a bid till the time is up (3 minutes)
		// but this is just a simple agent.d
			NonlinearUtilitySpace nonlinear = new NonlinearUtilitySpace(utilitySpace);
			
			//load all constraints from the nonlinear utility space
			ArrayList<InclusiveHyperRectangle> allConstraint = nonlinear.getAllInclusiveConstraints();  
			//get the number of constraints
			int constraint_num = allConstraint.size();
			InclusiveHyperRectangle aConstraint = new InclusiveHyperRectangle();
			Bid aBid = new Bid();
			do{
				int random_constraint = randomnr.nextInt(constraint_num);
				aConstraint = allConstraint.get(random_constraint);
				aBid =  getRandomBidFromConstraint(aConstraint);
			} while(getUtility(aBid)  < MINIMUM_UTILITY);
			return aBid;
	}

	// Find the bid randomly from the Constraint
	private Bid getRandomBidFromConstraint(InclusiveHyperRectangle constraint) throws Exception{
		
		ArrayList<Bound> boundlist=constraint.getBoundList();
		ArrayList<Issue> issues=utilitySpace.getDomain().getIssues();
		HashMap<Integer, Value> values = new HashMap<Integer, Value>(); // pairs <issuenumber,chosen value string>
		Random randomnr= new Random();
		int boundIndex = 0;
		for(int index=0; index<issues.size();index++){
			IssueInteger lIssueInteger = (IssueInteger) issues.get(index);
			int optionIndex = 0;
			if(boundIndex < boundlist.size()){//(Just to be safe)
				Bound lBound = boundlist.get(boundIndex);
				if(index == lBound.getIssueIndex()){ //Bound List don't have a bound when all values are acceptable.
					if(lBound.getMax() != lBound.getMin())
						optionIndex = lBound.getMin() + randomnr.nextInt(lBound.getMax()-lBound.getMin());
					else
						optionIndex = lBound.getMin(); // The acceptable value is only one when Minimum value and Maximum value are same.
					boundIndex++;
				}else{
					optionIndex = lIssueInteger.getLowerBound() + randomnr.nextInt(lIssueInteger.getUpperBound()-lIssueInteger.getLowerBound());
				}
			}else{
				optionIndex = lIssueInteger.getLowerBound() + randomnr.nextInt(lIssueInteger.getUpperBound()-lIssueInteger.getLowerBound());
			}
			values.put(lIssueInteger.getNumber(), new ValueInteger(optionIndex));
		}
		Bid bid=new Bid(utilitySpace.getDomain(),values);
		return bid;
	}


	/**
	 * This function determines the accept probability for an offer.
	 * At t=0 it will prefer high-utility offers.
	 * As t gets closer to 1, it will accept lower utility offers with increasing probability.
	 * it will never accept offers with utility 0.
	 * @param u is the utility
	 * @param t is the time as fraction of the total available time
	 * (t=0 at start, and t=1 at end time)
	 * @return the probability of an accept at time t
	 * @throws Exception if you use wrong values for u or t.
	 *
	 */
	double Paccept(double u, double t1) throws Exception
	{
		double t=t1*t1*t1; // steeper increase when deadline approaches.
		if (u<0 || u>1.05) throw new Exception("utility "+u+" outside [0,1]");
		// normalization may be slightly off, therefore we have a broad boundary up to 1.05
		if (t<0 || t>1) throw new Exception("time "+t+" outside [0,1]");
		if (u>1.) u=1;
		if (t==0.5) return u;
		return (u - 2.*u*t + 2.*(-1. + t + Math.sqrt(sq(-1. + t) + u*(-1. + 2*t))))/(-1. + 2*t);
	}

	double sq(double x) { return x*x; }
}
