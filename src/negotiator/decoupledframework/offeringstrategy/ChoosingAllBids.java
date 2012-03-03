package negotiator.decoupledframework.offeringstrategy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import negotiator.bidding.BidDetails;
import negotiator.decoupledframework.NegotiationSession;
import negotiator.decoupledframework.OMStrategy;
import negotiator.decoupledframework.OfferingStrategy;
import negotiator.decoupledframework.OpponentModel;
import negotiator.decoupledframework.OutcomeSpace;

/**
 * This class implements an offering strategy which creates a list of possible bids and 
 * then offers them in descending order. If all bids were offered, then the last bid
 * is repeated.
 * 
 * @author Alex Dirkzwager, Mark Hendrikx
 * @version 15-12-11
 */
public class ChoosingAllBids extends OfferingStrategy {

	private int counter = 0;
	private ArrayList<BidDetails> allBids;
	
	public ChoosingAllBids() { }
	
	public ChoosingAllBids(NegotiationSession negoSession, OpponentModel model, OMStrategy omStrat) {
		initializeAgent(negoSession, model);
	}
	
	private void initializeAgent(NegotiationSession negoSession, OpponentModel model) {
		this.negotiationSession = negoSession;
		
		OutcomeSpace space = new OutcomeSpace();
		space.init(negotiationSession.getUtilitySpace());
		negotiationSession.setOutcomeSpace(space);
		
		allBids = (ArrayList<BidDetails>) negotiationSession.getOutcomeSpace().getAllOutcomes();
		Collections.sort(allBids);
		this.opponentModel = model;
	}
	
	@Override
	public void init(NegotiationSession domainKnow, OpponentModel model, OMStrategy omStrat, HashMap<String, Double> parameters) throws Exception {
		this.opponentModel = model;
		initializeAgent(domainKnow, model);
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
	
	public ChoosingAllBids clone() {
		ChoosingAllBids clone = (ChoosingAllBids) super.clone();
		clone.counter = this.counter;
		clone.allBids = this.allBids;
		return clone;
	}
	
	public ChoosingAllBids reset() {
		super.reset();

		return this;
	}

	@Override
	public void agentReset() {
		counter = 0;
		allBids = null;		
	}
}