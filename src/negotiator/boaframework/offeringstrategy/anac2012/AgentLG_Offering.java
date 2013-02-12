package negotiator.boaframework.offeringstrategy.anac2012;

import java.util.HashMap;

import agents.anac.y2012.AgentLG.OpponentBids;

import negotiator.Bid;
import negotiator.actions.Accept;
import negotiator.actions.Action;
import negotiator.actions.Offer;
import negotiator.bidding.BidDetails;
import negotiator.boaframework.NegotiationSession;
import negotiator.boaframework.OMStrategy;
import negotiator.boaframework.OfferingStrategy;
import negotiator.boaframework.OpponentModel;
import negotiator.boaframework.sharedagentstate.anac2012.AgentLGSAS;

public class AgentLG_Offering extends OfferingStrategy{
	
	private Bid myLastBid = null;
	private OpponentBids oppenentsBid;
	private boolean bidLast= false;

	public AgentLG_Offering() { }
	
	public AgentLG_Offering(NegotiationSession negoSession, OpponentModel model, OMStrategy oms) throws Exception {
		init(negoSession, model, oms, null);
	}
	
	/**
	 * Init required for the Decoupled Framework.
	 */
	@Override
	public void init(NegotiationSession negoSession, OpponentModel model, OMStrategy oms, HashMap<String, Double> parameters) throws Exception {
		oppenentsBid = new OpponentBids(negoSession.getUtilitySpace());
		negotiationSession = negoSession;
	    helper = new AgentLGSAS(negotiationSession, oppenentsBid);
		
	}

	@Override
	public BidDetails determineOpeningBid() {
		if(!negotiationSession.getOpponentBidHistory().isEmpty())
			oppenentsBid.addBid(negotiationSession.getOpponentBidHistory().getLastBid());
		myLastBid = negotiationSession.getMaxBidinDomain().getBid();
		return negotiationSession.getMaxBidinDomain();
	}

	@Override
	public BidDetails determineNextBid() {
		
		oppenentsBid.addBid(negotiationSession.getOpponentBidHistory().getLastBid());

		BidDetails currentAction = null;
		try
		{
			double time=negotiationSession.getTime();
			double myUtility = negotiationSession.getUtilitySpace().getUtilityWithDiscount(negotiationSession.getOwnBidHistory().getLastBid(),time);
			double opponentUtility = negotiationSession.getUtilitySpace().getUtilityWithDiscount(negotiationSession.getOpponentBidHistory().getLastBid(),time);

			if (bidLast)
			{
				//System.out.println("Decoupled Last Bid");
				currentAction = negotiationSession.getOwnBidHistory().getLastBidDetails();
			}
			//there is lot of time ->learn the opponent and bid the 1/4 most optimal bids
			else 
				if(time<0.6)
				{
					if(!negotiationSession.getOpponentBidHistory().isEmpty())
						currentAction = ((AgentLGSAS) helper).getNextOptimicalBid(time);
						myLastBid = negotiationSession.getOwnBidHistory().getLastBid();
				} 
				else 
				{	
					//the time is over -> bid the opponents max utility bid for me 
					if ( time>=0.9995)
					{
						myLastBid = negotiationSession.getOpponentBidHistory().getBestBidDetails().getBid();
						if (negotiationSession.getUtilitySpace().getUtilityWithDiscount(myLastBid, time) <negotiationSession.getUtilitySpace().getReservationValueWithDiscount(time))
								myLastBid = ((AgentLGSAS) helper).getMyminBidfromBids();
						currentAction = new BidDetails(myLastBid, negotiationSession.getUtilitySpace().getUtility(myLastBid));
					}
					else
					{	
						//Comprise and chose better bid for the opponents that still good for me 
						currentAction = ((AgentLGSAS) helper).getNextBid(time);
					}
				}				
		/*
			System.out.println("Decoupled opponentBids: " + oppenentsBid.getOpponentsBids().size());
			System.out.println("Decoupled contains: " + (oppenentsBid.getOpponentsBids().contains(myLastBid)));
			System.out.println("Decoupled currentAction: " + currentAction.getBid());

			System.out.println("Decoupled LastBid: " + myLastBid);
		 */
			if (oppenentsBid.getOpponentsBids().contains(currentAction.getBid())){
				bidLast= true;
			}
				
		}
		catch(Exception e)
		{
			System.out.println("Error and thus accept: " + e);
			//currentAction = new Accept(getAgentID());	
		}
		
		return currentAction;
	}


	
	
	
	

}
