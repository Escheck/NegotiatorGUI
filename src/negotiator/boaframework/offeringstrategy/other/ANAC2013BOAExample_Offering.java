package negotiator.boaframework.offeringstrategy.other;

import java.io.Serializable;
import java.util.HashMap;
import negotiator.Bid;
import negotiator.NegotiationResult;
import negotiator.bidding.BidDetails;
import negotiator.boaframework.NegotiationSession;
import negotiator.boaframework.OMStrategy;
import negotiator.boaframework.OfferingStrategy;
import negotiator.boaframework.OpponentModel;

public class ANAC2013BOAExample_Offering extends OfferingStrategy {
	
	private double breakoff = 0.5;

	/**
	 * Empty constructor called by BOA framework.
	 */
	public ANAC2013BOAExample_Offering() { }

	public void init(NegotiationSession negotiationSession, OpponentModel opponentModel, OMStrategy omStrategy, HashMap<String, Double> parameters) throws Exception {
		this.negotiationSession = negotiationSession;
		this.opponentModel = opponentModel;
		this.omStrategy = omStrategy;
		Serializable dataFromOffering = loadData();
		if (dataFromOffering != null) {
			breakoff = (Double) dataFromOffering;
		}
	}

	@Override
	public BidDetails determineOpeningBid() {
		return determineNextBid();
	}

	@Override
	public BidDetails determineNextBid() {

		Bid bid = null;
		try {
			do {
				bid = negotiationSession.getUtilitySpace().getDomain().getRandomBid();
			} while (negotiationSession.getUtilitySpace().getUtility(bid) <= breakoff);
			nextBid = new BidDetails(bid, negotiationSession.getUtilitySpace().getUtility(bid));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return nextBid;
	}
	
	public void endSession(NegotiationResult result) {
		if (result.isAccept()) {
			if (result.getMyDiscountedUtility() > breakoff) {
				System.out.println("Accept, my new target is: " + result.getMyDiscountedUtility());
				storeData(new Double(result.getMyDiscountedUtility()));
			}
		} else {
			double newBreakoff = breakoff - 0.05;
			System.out.println("No accept, my new target is: " + newBreakoff);
			storeData(new Double(newBreakoff));
		}
	}
}