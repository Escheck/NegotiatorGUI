package negotiator.boaframework.offeringstrategy.anac2012;

import java.util.HashMap;
import negotiator.Bid;
import negotiator.bidding.BidDetails;
import negotiator.boaframework.NegotiationSession;
import negotiator.boaframework.OMStrategy;
import negotiator.boaframework.OfferingStrategy;
import negotiator.boaframework.OpponentModel;
import negotiator.boaframework.offeringstrategy.anac2012.AgentLR.BidChooser;
import negotiator.utility.UtilitySpace;
import agents.anac.y2012.AgentLG.OpponentBids;

/**
 * This is the decoupled Bidding Strategy of AgentLG
 * Note that the Opponent Model was not decoupled and thus
 * is integrated into this strategy
 * @author Alexander Dirkzwager
 */
public class AgentLG_Offering extends OfferingStrategy {
	
	
	
	private Bid myLastBid = null;
	private Bid oponnetLastBid = null;
	
	private BidChooser bidChooser = null;
	private OpponentBids oppenentsBid;
	private boolean bidLast= false;
	private UtilitySpace utilitySpace;
	

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
		bidChooser = new BidChooser(negoSession.getUtilitySpace(),oppenentsBid);
		utilitySpace = negoSession.getUtilitySpace();
		negotiationSession = negoSession;
		
	}

	@Override
	public BidDetails determineOpeningBid() {
		return determineNextBid();
	}

	@Override
	public BidDetails determineNextBid() {
		Bid bidToOffer = null;
		try
		{
			double time=negotiationSession.getTime();
			
			//first bid -> vote the optimal bid
			if(myLastBid == null || oponnetLastBid == null)
			{
				myLastBid =this.utilitySpace.getMaxUtilityBid();
				bidToOffer = myLastBid;	
				//System.out.println("Decoupled FirstBid: " + bidToOffer);
			} 
			else
			{	
				//Acceptance condition was here
				
				// Crossing of my bids with opponent's bids
				if (bidLast)
				{
					bidToOffer = myLastBid;	
				}
				//there is lot of time ->learn the opponent and bid the 1/4 most optimal bids
				else {
					if(time<0.6)
					{
						bidToOffer = bidChooser.getNextOptimicalBid(time);
					} 
					else 
					{	
						//the time is over -> bid the opponents max utility bid for me 
						if ( time>=0.9995)
						{
							myLastBid = oppenentsBid.getMaxUtilityBidForMe();
							if (utilitySpace.getUtilityWithDiscount(myLastBid, time) <utilitySpace.getReservationValueWithDiscount(time))
									myLastBid = bidChooser.getMyminBidfromBids();
							bidToOffer = myLastBid;	
							//System.out.println("Decoupled getMaxUtilityBidForMe(): " + bidToOffer);

						}
						else
						{	
							//Comprise and chose better bid for the opponents that still good for me 
							bidToOffer = bidChooser.getNextBid(time);
						}
					}	
				}
			}	
			if (oppenentsBid.getOpponentsBids().contains(myLastBid))
				bidLast= true;
			
				
		}
		catch(Exception e)
		{
			System.out.println("Error: " + e);
		}
		try {
			//System.out.println("Bid to offer: " + bidToOffer);
			nextBid = new BidDetails(bidToOffer, utilitySpace.getUtility(bidToOffer));
			//These variables are not actually used but a hack to allow agent to run normally
			myLastBid = bidToOffer;
			oponnetLastBid = negotiationSession.getOpponentBidHistory().getLastBid();
			oppenentsBid.addBid(negotiationSession.getOpponentBidHistory().getLastBid());

		} catch (Exception e) {
			e.printStackTrace();
		}
		return nextBid;
	}
}
