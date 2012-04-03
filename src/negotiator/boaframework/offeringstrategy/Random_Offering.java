package negotiator.decoupledframework.offeringstrategy;

import negotiator.Bid;
import negotiator.bidding.BidDetails;
import negotiator.decoupledframework.OfferingStrategy;

/**
 * This class implements the Simple Agent a.k.a. Zero Intelligence, Random Walker offering strategy.
 * This will choose a bid at random to offer the opponent.
 * 
 * @author Alex Dirkzwager, Mark Hendrikx
 */
public class Random_Offering extends OfferingStrategy {

	public Random_Offering() { }

	@Override
	public BidDetails determineNextBid() {
		Bid bid = negotiationSession.getUtilitySpace().getDomain().getRandomBid();
		try {
			nextBid = new BidDetails(bid, negotiationSession.getUtilitySpace().getUtility(bid));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return nextBid;
	}

	@Override
	public BidDetails determineOpeningBid() {
		return determineNextBid();
	}

	@Override
	public void agentReset() { }
}