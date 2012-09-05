package negotiator.analysis;

import negotiator.Bid;
import negotiator.xml.OrderedSimpleElement;
import negotiator.xml.SimpleElement;

public class BidPointTime extends BidPoint{
	
	public Double time;

	public BidPointTime(Bid b, Double uA, Double uB, double time) {
		super(b, uA, uB);
		this.time = time;
	}
	
	@Override
	public String toString(){
		return "BidPointTime ["+bid+" utilA["+utilityA+"],utilB["+utilityB+"], Time["+time+"]]";
	}
	
	@Override
	public SimpleElement toXML()
	{
		SimpleElement xml = new OrderedSimpleElement("BidPoint");
		xml.addChildElement(bid.toXML());
		xml.setAttribute("utilityA", String.valueOf(utilityA));
		xml.setAttribute("utilityB", String.valueOf(utilityB));
		xml.setAttribute("time", String.valueOf(time));
		return xml;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		BidPointTime other = (BidPointTime) obj;
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
		if (time == null) {
			if (other.time != null)
				return false;
		} else if (!time.equals(other.time))
			return false;
		return true;
	}

}
