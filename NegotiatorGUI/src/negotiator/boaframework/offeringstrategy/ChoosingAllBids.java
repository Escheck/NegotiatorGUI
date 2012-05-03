package negotiator.boaframework.offeringstrategy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import negotiator.bidding.BidDetails;
import negotiator.boaframework.NegotiationSession;
import negotiator.boaframework.OMStrategy;
import negotiator.boaframework.OfferingStrategy;
import negotiator.boaframework.OpponentModel;
import negotiator.boaframework.OutcomeSpace;

/**
 * This class implements an offering strategy which creates a list of possible bids and 
 * then offers them in descending order. If all bids are offered, then the last bid
 * is repeated.
 * 
 * This strategy has no straight-forward extension of using opponent models.
 * 
 * @author Alex Dirkzwager, Mark Hendrikx
 * @version 15-12-11
 */
public class ChoosingAllBids extends OfferingStrategy {
	/** counter used to determine which bid to offer from the sorted list of possible bids */
	private int counter = 0;
	/** reference to all bids */
	private ArrayList<BidDetails> allBids;
	
	/**
	 * Empty constructor used for reflexion. Note this constructor assumes that init
	 * is called next.
	 */
	public ChoosingAllBids() { }
	
	/**
	 * Constructor which can be used to create the agent without the GUI.
	 * 
	 * @param negoSession reference to the negotiationsession object
	 * @param model reference to the opponent model
	 */
	public ChoosingAllBids(NegotiationSession negoSession, OpponentModel model) {
		initializeAgent(negoSession, model);
	}
	
	@Override
	public void init(NegotiationSession domainKnow, OpponentModel model, OMStrategy omStrat, HashMap<String, Double> parameters) throws Exception {
		initializeAgent(domainKnow, model);
	} 

	private void initializeAgent(NegotiationSession negoSession, OpponentModel model) {
		this.negotiationSession = negoSession;
		
		OutcomeSpace space = new OutcomeSpace(negotiationSession.getUtilitySpace());
		negotiationSession.setOutcomeSpace(space);
		
		allBids = (ArrayList<BidDetails>) negotiationSession.getOutcomeSpace().getAllOutcomes();
		Collections.sort(allBids);
		this.opponentModel = model;
	}
	
	/**
	 * Returns the next bid in the sorted array of bids. If there are no more bids
	 * in the list, then the last bid is returned.
	 */
	@Override
	public BidDetails determineNextBid() {
		//System.out.println("allbids size: " + allBids.size());
		nextBid = this.allBids.get(counter);
		if (counter < allBids.size() - 1) {
			counter++;
		}
		return nextBid;
	}

	@Override
	public BidDetails determineOpeningBid() {
		return determineNextBid();
	}
}