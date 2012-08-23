package agents.anac.y2012.AgentLG;
import java.util.ArrayList;
import java.util.HashMap;

import negotiator.Bid;
import negotiator.Domain;
import negotiator.issue.Issue;
import negotiator.issue.Value;
import negotiator.utility.UtilitySpace;

/**
 *  Class that is used to save opponents bid and learn opponent utility
 *
 */
public class OpponentBids {
	
	private ArrayList<Bid> oppBids = new ArrayList<Bid>();
	private HashMap<Issue,BidStatistic> statistic = new HashMap<Issue,BidStatistic>(); 
	private Bid maxUtilityBidForMe =null;
	private UtilitySpace  utilitySpace;
	
	/**
	 * add opponent bid and updates statistics
	 *
	 */
	public void addBid(Bid bid)
	{
		oppBids.add(bid);
		try
		{
			//updates statistics
			for (Issue issue : statistic.keySet()) {
				Value v= bid.getValue(issue.getNumber());
				statistic.get(issue).add(v);
			}
			
			//update the max bid for the agent from the opponent bids
 			if (oppBids.size()==1)
				maxUtilityBidForMe = bid;
			else  if (utilitySpace.getUtility(maxUtilityBidForMe)<utilitySpace.getUtility(bid))
				  maxUtilityBidForMe = bid;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * return opponents Bids
	 *
	 */
	public ArrayList<Bid> getOpponentsBids()
	{
		return oppBids;
	}

	public OpponentBids(UtilitySpace utilitySpace) {
		this.utilitySpace = utilitySpace;
		ArrayList<Issue> issues = utilitySpace.getDomain().getIssues();
		for (Issue issue : issues) {
			statistic.put(issue,new BidStatistic(issue));
		}
	}
	
	/**
	 * returns opponents Bids
	 *
	 */
	public Bid getMaxUtilityBidForMe()
	{
		return maxUtilityBidForMe;
	}
	
	/**
	 * returns the most voted value for an isuue
	 *
	 */
	public Value getMostVotedValueForIsuue(Issue issue)
	{
		return statistic.get(issue).getMostBided();
	}


	/**
	 * returns opponent bid utility that calculated from the vote statistics.
	 *
	 */
	public double getOpponentBidUtility (Domain domain,Bid bid)
	{
		double ret=0;
		ArrayList<Issue> issues = domain.getIssues();
		for (Issue issue : issues) {
			try {
				ret+= statistic.get(issue).getValueUtility(bid.getValue(issue.getNumber()));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		System.out.println(ret / domain.getIssues().size());
		return ret;
	}
}
