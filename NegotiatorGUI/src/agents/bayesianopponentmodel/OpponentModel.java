package agents.bayesianopponentmodel;

import java.util.ArrayList;

import negotiator.Bid;
import negotiator.BidIterator;
import negotiator.Domain;

public class OpponentModel {
	protected Domain fDomain;
	public ArrayList<Bid> fBiddingHistory;
	
	public Domain getDomain() { return fDomain; }
	Double minUtility=null,maxUtility=null;
	public double getExpectedUtility(Bid pBid) throws Exception {return -1;}
	public double getNormalizedUtility(Bid pBid) throws Exception
	{
		double u=getExpectedUtility(pBid);
		if (minUtility==null || maxUtility==null) findMinMaxUtility();
		return (u-minUtility)/(maxUtility-minUtility);
	}
	public void updateBeliefs(Bid pBid) throws Exception { };
	public double getExpectedWeight(int pIssueNumber) { return 0;}
	public boolean haveSeenBefore(Bid pBid) {
		for(Bid tmpBid : fBiddingHistory) {
			if(pBid.equals(tmpBid)) return true;
		}
		return false;
	}
	protected void findMinMaxUtility() throws Exception
	{
		BidIterator biditer=new BidIterator(fDomain);
		minUtility=1.;  maxUtility=0.; double u;
		int i=0;
		while (biditer.hasNext())
		{
			Bid b=biditer.next();
			u=getExpectedUtility(b);
			
			if (minUtility>u) minUtility=u;
			if (maxUtility<u) maxUtility=u;
			i++;
//			System.out.println(String.valueOf(i));
		}
	}
}
