package negotiator.analysis;


import java.util.Arrays;
import negotiator.Bid;

/**
 * BidPoint is a bid with utilities of the agents in a negotiation.
 * This class is used in the outcome space analysis.
 * @author Tim Baarslag & Dmytro Tykhonov
 */
public class BidPoint 
{
	public Bid bid;
	private Double [] utility;
	private static final String[] alphabet = new String[] {"A","B","C","D","E","F","G","H","I","J","K","L","M","N","O","P","Q","R","S","T","U","V","W","X","Y","Z"};
	
	public BidPoint(Bid b, Double[] util)
	{
		bid=b; 
		utility = util.clone();
	}
	
	public BidPoint(Bid b, double utilA, double utilB)
	{
		bid=b;
		Double[] utilities = new Double[2];
		utilities[0] = utilA;
		utilities[1] = utilB;
		this.utility = utilities;
	}
	
	
	public String toString()
	{
		String result = "BidPoint ["+bid;
		for(int i=0;i<utility.length;i++) {
			result += "util"+alphabet[i]+"["+utility[i]+"],";
		}
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
		if (!Arrays.equals(utility, other.utility))
			return false;
		return true;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((bid == null) ? 0 : bid.hashCode());
		result = prime * result + Arrays.hashCode(utility);
		return result;
	}

	public Bid getBid() 
	{
		return bid;
	}
	
	public Double getUtility(int index) 
	{
		return utility[index];
	}
	
	public Double getUtilityA()
	{
		return utility[0];
	}
	
	public Double getUtilityB()
	{
		return utility[1];
	}
	
	public boolean isDominatedBy(BidPoint p)
	{

		if ((this != p && (p.getUtilityA() > this.getUtilityA() &&
				p.getUtilityB() >= this.getUtilityB()) ||
				(p.getUtilityA() >= this.getUtilityA() &&
				p.getUtilityB() > this.getUtilityB()))){
				return true;
		}
		return false;
	}
	

	public double getTwoDimensionalDistance(BidPoint b) {
		return Math.sqrt(Math.pow((this.getUtilityA() - b.getUtilityA()), 2) + Math.pow((this.getUtilityB() - b.getUtilityB()), 2));
	}
}
