package negotiator.boaframework.omstrategy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import negotiator.Bid;
import negotiator.bidding.BidDetails;
import negotiator.bidding.BidDetailsSorterUtility;
import negotiator.boaframework.NegotiationSession;
import negotiator.boaframework.OMStrategy;
import negotiator.boaframework.OpponentModel;

/**
 * Implements the opponent model strategy used in the NTFT agent.
 * 
 * @author Mark Hendrikx
 */
public class NTFTstrategy extends OMStrategy {

	private boolean domainIsBig;
	private long possibleBids;	
	private Random random;
	private BidDetailsSorterUtility comp = new BidDetailsSorterUtility();
	
	public void init(NegotiationSession negotiationSession, OpponentModel model, HashMap<String, Double> parameters) throws Exception {
		initializeAgent(negotiationSession, model);
	}
	
	public void init(NegotiationSession negotiationSession, OpponentModel model) {
		initializeAgent(negotiationSession, model);
	}
	
	private void initializeAgent(NegotiationSession negoSession, OpponentModel model) {
		super.init(negotiationSession, model);
		this.possibleBids = negotiationSession.getUtilitySpace().getDomain().getNumberOfPossibleBids();
		domainIsBig = (possibleBids > 10000);
		random = new Random();
	}

	@Override
	public BidDetails getBid(List<BidDetails> bidsInRange) {
		ArrayList<BidDetails> bidsOM = new ArrayList<BidDetails>();
		for (BidDetails bid : bidsInRange)
		{
			double utility;
			try
			{
				utility = model.getBidEvaluation(bid.getBid());
				BidDetails bidDetails = new BidDetails(bid.getBid(), utility);
				bidsOM.add(bidDetails);
			} catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		
		// Pick the top 3 to 20 bids, depending on the domain size
		int n =  (int) Math.round(bidsOM.size() / 10.0);
		if (n < 3)
			n = 3;
		if (n > 20)
			n = 20;
		
		Collections.sort(bidsOM, comp);
		
		int entry = random.nextInt(Math.min(bidsOM.size(), n));
		Bid opponentBestBid = bidsOM.get(entry).getBid();
		BidDetails nextBid = null;
		try {
			nextBid = new BidDetails(opponentBestBid, negotiationSession.getUtilitySpace().getUtility(opponentBestBid), negotiationSession.getTime());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return nextBid;
	}
	
	@Override
	public boolean canUpdateOM() {
		// in the last seconds we don't want to lose any time
		if (negotiationSession.getTime() > 0.99)
			return false;
		
		// in a big domain, we stop updating half-way
		if (domainIsBig) {
			if (negotiationSession.getTime() > 0.5) {
				return false;
			}
		}
		return true;
	}
}