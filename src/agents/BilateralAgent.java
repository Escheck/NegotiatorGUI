package agents;

import java.util.ArrayList;
import java.util.List;

import negotiator.Agent;
import negotiator.Bid;
import negotiator.Domain;
import negotiator.actions.Accept;
import negotiator.actions.Action;
import negotiator.actions.Offer;

/**
 * @author Tim Baarslag
 * Agent skeleton for a bilateral agent. It contains service functions to have access to the bidding history. 
 */
public abstract class BilateralAgent extends Agent
{
	protected Domain domain;
	private Action opponentAction;
	protected List<Bid> opponentPreviousBids;
	protected List<Bid> myPreviousBids;

	public void init()
	{ 
		domain = utilitySpace.getDomain();
		opponentPreviousBids 	= new ArrayList<Bid>();
		myPreviousBids 			= new ArrayList<Bid>();
	}

	public static String getVersion() { return "1.0"; }

	@Override
	public void ReceiveMessage(Action opponentAction) 
	{
		this.opponentAction = opponentAction;
		if (opponentAction instanceof Offer)
			opponentPreviousBids.add(((Offer) opponentAction).getBid());
	}

	/**
	 * @param remember: remember the action or not.
	 */
	@Override
	public Action chooseAction()
	{
		Bid opponentLastBid = getOpponentLastBid();
		Action myAction = null;

		// We start
		if (opponentLastBid == null)
		{
			Bid openingBid = chooseOpeningBid();
			myAction = new Offer(getAgentID(), openingBid);
		}

		// We make a counter-offer
		else if (opponentAction instanceof Offer)
		{
			Bid counterBid = chooseCounterBid();
			// Check to see if we want to accept
			if (isAcceptable(counterBid))
				myAction = new Accept(getAgentID());
			else
				myAction = new Offer(getAgentID(), counterBid);
		}

		remember(myAction);
		return myAction;
	}

	/**
	 * Remember our action, if it was an offer
	 */
	private void remember(Action myAction) 
	{
		if (myAction instanceof Offer)
		{
			Bid myLastBid = ((Offer) myAction).getBid();
			myPreviousBids.add(myLastBid);
		}
	}

	/**
	 * At some point, one of the parties has to accept an offer to end the negotiation. 
	 * Use this method to decide whether to accept the last offer by the opponent.  
	 * @param plannedBid 
	 */
	public abstract boolean isAcceptable(Bid plannedBid);

	/**
	 * The opponent has already made a bid. Use this method to make an counter bid.
	 */
	public abstract Bid chooseCounterBid(); 

	/**
	 * Use this method to make an opening bid
	 */
	public abstract Bid chooseOpeningBid(); 

	public Bid getMyLastBid()
	{
		if (myPreviousBids.isEmpty())
			return null;

		return myPreviousBids.get(myPreviousBids.size() - 1);
	}

	public Bid getOpponentLastBid()
	{
		if (opponentPreviousBids.isEmpty())
			return null;

		return opponentPreviousBids.get(opponentPreviousBids.size() - 1);
	}

	public List<Bid> getOpponentPreviousBids()
	{
		return opponentPreviousBids;
	}

	public List<Bid> getMyPreviousBids()
	{
		return myPreviousBids;
	}

	/**
	 * Returns the current round number, starting at 0. This is equal to the total number of placed bids.
	 */
	public int getRound()
	{
		return myPreviousBids.size() + opponentPreviousBids.size();
	}
}
