package negotiator.analysis;


import negotiator.Bid;
import negotiator.xml.SimpleElement;
import negotiator.XMLable;

/**
 * 
 * 
 * @author W.Pasman
 * BidPoint is a point with two utilities for the two agents.
 */
public class BidPoint implements XMLable {
	public Bid bid;
	public Double utilityA;
	public Double utilityB;
	
	public BidPoint(Bid b,Double uA, Double uB)
	{
		bid=b; utilityA=uA; utilityB=uB;
	}
	
	public String toString()
	{
		return "BidPoint ["+bid+" utilA["+utilityA+"],utilB["+utilityB+"]]";
	}
	
	boolean equals(BidPoint pt)
	{
		return bid.equals(pt.bid);
	}
	
	public SimpleElement toXML()
	{
		SimpleElement xml = new SimpleElement("BidPoint");
		xml.addChildElement(bid.toXML());
		xml.setAttribute("utilityA", ""+utilityA);
		xml.setAttribute("utilityB", ""+utilityB);
		return xml;
	}
}
