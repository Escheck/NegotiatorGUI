package negotiator.bidding;

import negotiator.Bid;

/**
 * The BidDetails class is used to store a bid with it's corresponding utility and time it was offered.
 * In this way constant re-computation of the utility values is avoided.
 * 
 * @author Tim Baarslag, Alex Dirkzwager, Mark Hendrikx
 */
public class BidDetails implements Comparable<BidDetails>{

	/** the bid of an agent */
	private Bid bid;
	/** the utility corresponding to the bid */
	private double myUndiscountedUtil;
	/** time the bid was offered (so the discounted utility can be calculated at that time) */
	private double time;
	
	/**
	 * Creates a BidDetails-object which stores a bid with it's corresponding utility.
	 * 
	 * @param bid of an agent
	 * @param myUndiscountedUtil utility of the bid
	 */
	public BidDetails(Bid bid, double myUndiscountedUtil) {
		this.bid = bid;
		this.myUndiscountedUtil = myUndiscountedUtil;
	}
	
	/**
	 * Creates a BidDetails-object which stores a bid with it's corresponding
	 * utility and the time it was offered.
	 * 
	 * @param bid of an agent
	 * @param myUndiscountedUtil of the bid
	 * @param time of offering
	 */
	public BidDetails(Bid bid, double myUndiscountedUtil, double time) {
		this.bid = bid;
		this.myUndiscountedUtil = myUndiscountedUtil;
		this.time = time;
	}
	
	/**
	 * Returns the bid.
	 * @return bid.
	 */
	public Bid getBid() {
		return bid;
	}
	
	/**
	 * Set the bid.
	 * @param bid
	 */
	public void setBid(Bid bid) {
		this.bid = bid;
	}
	
	/**
	 * Returns the undiscounted utility of the bid.
	 * @return undiscounted utility of bid.
	 */
	public double getMyUndiscountedUtil() {
		return myUndiscountedUtil;
	}
	
	/**
	 * Set the undiscounted utility of the bid.
	 * @param utility of the bid.
	 */
	public void setMyUndiscountedUtil(double utility) {
		this.myUndiscountedUtil = utility;
	}
	
	/**
	 * Return the time at which this bid was offered.
	 * @return time of offering.
	 */
	public double getTime(){
		return time;
	}
	
	/**
	 * Set the time at which this bid was offered.
	 * @param time of offering.
	 */
	public void setTime(double time){
		this.time = time;
	}
	
	@Override
	public String toString()
	{
		return "(u=" + myUndiscountedUtil + ", t=" + time + ")";
	}
	
	/**
	 * A comparator for BidDetails which order the bids in
	 * reverse natural order of utility.
	 * 
	 * @param other bid to which this bid is compared.
	 * @return order of this bid relative to the given bid.
	 */
	public int compareTo(BidDetails other) {
		double otherUtil = other.getMyUndiscountedUtil();
		
		int value = 0;
		if (this.myUndiscountedUtil < otherUtil) {
			value = 1;
		} else if (this.myUndiscountedUtil > otherUtil) {
			value = -1;
		}
		return value;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((bid == null) ? 0 : bid.hashCode());
		long temp;
		temp = Double.doubleToLongBits(myUndiscountedUtil);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(time);
		result = prime * result + (int) (temp ^ (temp >>> 32));
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
		BidDetails other = (BidDetails) obj;
		if (bid == null) {
			if (other.bid != null)
				return false;
		} else if (!bid.equals(other.bid))
			return false;
		if (Double.doubleToLongBits(myUndiscountedUtil) != Double
				.doubleToLongBits(other.myUndiscountedUtil))
			return false;
		if (Double.doubleToLongBits(time) != Double
				.doubleToLongBits(other.time))
			return false;
		return true;
	}
}