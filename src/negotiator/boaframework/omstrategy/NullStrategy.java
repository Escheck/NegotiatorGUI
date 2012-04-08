package negotiator.boaframework.omstrategy;

import java.util.HashMap;
import java.util.List;
import java.util.Random;
import negotiator.bidding.BidDetails;
import negotiator.boaframework.NegotiationSession;
import negotiator.boaframework.OMStrategy;
import negotiator.boaframework.OpponentModel;

public class NullStrategy extends OMStrategy {

	private Random rand;
	
	public NullStrategy() {}
	
	public void init(NegotiationSession negotiationSession, OpponentModel model, HashMap<String, Double> parameters) throws Exception {
		rand = new Random();
	}
	
	@Override
	public BidDetails getBid(List<BidDetails> allBids) {
		return allBids.get(rand.nextInt(allBids.size()));
	}

	@Override
	public boolean canUpdateOM() {
		return true;
	}
}