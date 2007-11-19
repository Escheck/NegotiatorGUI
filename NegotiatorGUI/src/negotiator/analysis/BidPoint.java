package negotiator.analysis;


import negotiator.Bid;

/**
 * 
 * 
 * @author W.Pasman
 * BidPoint is a point with two utilities for the two agents.
 */
public class BidPoint {
	public Bid bid;
	public Double utilityA;
	public Double utilityB;
	
	BidPoint(Bid b,Double uA, Double uB)
	{
		bid=b; utilityA=uA; utilityB=uB;
	}
	
	public String toString()
	{
		return "BidPoint ["+/*bid+*/",utilA["+utilityA+"],utilB["+utilityB+"]]";
	}
	
	boolean equals(BidPoint pt)
	{
		return bid.equals(pt.bid);
	}
}
