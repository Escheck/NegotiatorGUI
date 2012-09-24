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
		return "BidPointTime ["+bid+" utilA["+getUtilityA()+"],utilB["+getUtilityB()+"], Time["+time+"]]";
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
		if (getUtilityA() == null) {
			if (other.getUtilityA() != null)
				return false;
		} else if (!getUtilityA().equals(other.getUtilityA()))
			return false;
		if (getUtilityB() == null) {
			if (other.getUtilityB() != null)
				return false;
		} else if (!getUtilityB().equals(other.getUtilityB()))
			return false;
		if (time == null) {
			if (other.time != null)
				return false;
		} else if (!time.equals(other.time))
			return false;
		return true;
	}

}
