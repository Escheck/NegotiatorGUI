package agents;

import negotiator.Bid;
import negotiator.analysis.BidHistory;

public interface BidHistoryKeeper
{
	public BidHistory getOpponentHistory();

	public Bid getMyLastBid();

	public Bid getMySecondLastBid();

	public Bid getOpponentLastBid();
}
