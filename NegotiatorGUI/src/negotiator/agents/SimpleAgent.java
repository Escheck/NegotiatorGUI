/*
 * MyAgent.java
 *
 * Created on November 6, 2006, 9:55 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package negotiator.agents;

import java.util.Date;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Random;

import negotiator.*;
import negotiator.actions.*;
import negotiator.issue.*;
import negotiator.utility.UtilitySpace;


/**
 *
 * @author W.Pasman
 * 
 */
public class SimpleAgent extends Agent
{
    private Action actionOfPartner=null;
    private static final double MINIMUM_BID_UTILITY = 0.5;
    
    
    	// just here to suggest possibilities, not used in this agent.
    private int sessionNumber;			
    private int sessionTotalNumber;
 
    /**
     * init is called when a next session starts with the same opponent.
     */
    public void init(int sessionNumber, int sessionTotalNumber, Date startTimeP, Integer totalTimeP, UtilitySpace us) {
        super.init (sessionNumber, sessionTotalNumber, startTimeP,totalTimeP,us);
        sessionNumber = sessionNumber;
        sessionTotalNumber = sessionTotalNumber;
    }

	public void ReceiveMessage(Action opponentAction) {
        actionOfPartner = opponentAction;
    }
    
 
	public Action chooseAction()
    {
        Action action = null;
        try { 
            if(actionOfPartner==null) action = chooseRandomBidAction();
            if(actionOfPartner instanceof Offer)
            {
                Bid partnerBid = ((Offer)actionOfPartner).getBid();
                double offeredutil=utilitySpace.getUtility(partnerBid);
                double time=((new Date()).getTime()-startTime.getTime())/(1000.*totalTime);
                double P=Paccept(offeredutil,time);
                if (P>Math.random()) action = new Accept(this);
                else action = chooseRandomBidAction();               
            }
            Thread.sleep(1000); // just for fun
        } catch (Exception e) { 
        	System.out.println("Exception in ChooseAction:"+e.getMessage());
        	action=new Accept(this); // best guess if things go wrong. 
        }
        return action;
    }
    
	/**
	 * Wrapper for getRandomBid, for convenience.
	 * @return new Action(Bid(..)), with bid utility > MINIMUM_BID_UTIL.
	 * If a problem occurs, it returns an Accept() action.
	 */
	private Action chooseRandomBidAction() 
    {
        Bid nextBid=null ;
        try { nextBid = getRandomBid(); }
        catch (Exception e) { System.out.println("Problem with received bid:"+e.getMessage()+". cancelling bidding"); }
        if (nextBid == null) return (new Accept(this));                
        return (new Offer(this, nextBid));
    }
	   
	/**
	 * @return a random bid with high enough utility value.
	 * @throws Exception if we can't compute the utility (eg no evaluators have been set)
	 * or when other evaluators than a DiscreteEvaluator are present in the util space.
	 */
	private Bid getRandomBid() throws Exception
    {
    	HashMap<Integer, Value> values = new HashMap<Integer, Value>(); // pairs <issuenumber,chosen value string>
    	ArrayList<Issue> issues=utilitySpace.getDomain().getIssues();
    	Random randomnr= new Random();
    	
    	// create a random bid with utility>MINIMUM_BID_UTIL.
    	// note that this may never succeed if you set MINIMUM too high!!!
   	 	// in that case we will search for a bid till the time is up (2 minutes)
   	 	// but this is just a simple agent.
       Bid bid=null;
    	do {
	        for(Issue lIssue:issues) 
	        {
				switch(lIssue.getType()) {
				case DISCRETE:
					IssueDiscrete lIssueDiscrete = (IssueDiscrete)lIssue;
		            int optionIndex=randomnr.nextInt(lIssueDiscrete.getNumberOfValues());
		            values.put(lIssue.getNumber(), lIssueDiscrete.getValue(optionIndex));
					break;
				default: throw new Exception("issue type "+lIssue.getType()+" not supported by SimpleAgent2");
				}
			}
	        bid=new Bid(utilitySpace.getDomain(),values);
    	} while (utilitySpace.getUtility(bid)<MINIMUM_BID_UTILITY);
    	
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
	double Paccept(double u, double t) throws Exception
	{
		if (u<0 || u>1) throw new Exception("utility "+u+" outside [0,1]");
		if (t<0 || t>1) throw new Exception("time "+t+" outside [0,1]");
		
		if (t==0.5) return u;
		return (u - 2.*u*t + 2.*(-1. + t + Math.sqrt(sq(-1. + t) + u*(-1. + 2*t))))/(-1. + 2*t);
	}
	
	double sq(double x) { return x*x; }

}
