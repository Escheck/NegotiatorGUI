package negotiator.analysis;


import negotiator.Bid;
import negotiator.XMLable;
import negotiator.xml.OrderedSimpleElement;
import negotiator.xml.SimpleElement;

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
	
	public double distanceTo(BidPoint b)
	{
		return Math.sqrt(Math.pow(utilityA - b.utilityA, 2) + Math.pow(utilityB - b.utilityB, 2));
	}
	
	public SimpleElement toXML()
	{
		SimpleElement xml = new OrderedSimpleElement("BidPoint");
		xml.addChildElement(bid.toXML());
		xml.setAttribute("utilityA", String.valueOf(utilityA));
		xml.setAttribute("utilityB", String.valueOf(utilityB));
		return xml;
	}
	
	/**
	 * @param p
	 * @return whether <b>this</b> is (not-strictly) dominated by p.
	 */
	public boolean isDominatedBy(BidPoint p)
	{
		if (this.utilityA > p.utilityA)
			return false;
		
		if (this.utilityB > p.utilityB)
			return false;
		
		return true;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((bid == null) ? 0 : bid.hashCode());
		result = prime * result
				+ ((utilityA == null) ? 0 : utilityA.hashCode());
		result = prime * result
				+ ((utilityB == null) ? 0 : utilityB.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		BidPoint other = (BidPoint) obj;
		if (bid == null) {
			if (other.bid != null)
				return false;
		} else if (!bid.equals(other.bid))
			return false;
		if (utilityA == null) {
			if (other.utilityA != null)
				return false;
		} else if (!utilityA.equals(other.utilityA))
			return false;
		if (utilityB == null) {
			if (other.utilityB != null)
				return false;
		} else if (!utilityB.equals(other.utilityB))
			return false;
		return true;
	}
}
