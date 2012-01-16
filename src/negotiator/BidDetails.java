package negotiator;


/**
 * The BidDetails class is used to store a bid with it's corresponding utility and time it was offered.
 * In this way constant re-computation of the utility values is avoided.
 * 
 * @author Tim Baarslag, Alex Dirkzwager, Mark Hendrikx
 */
public class BidDetails implements Comparable<BidDetails>{

	// the bid of an agent
	private Bid bid;
	// the utility corresponding to the bid
	private double myUndiscountedUtil;
	//time the bid was offered (so the discounted utility can be calculated at that time)
	private double time;
	
	/**
	 * Creates a UTBid-object which stores a bid with it's corresponding
	 * utility.
	 * 
	 * @param bid of an agent
	 * @param myUndiscountedUtil of the bid
	 * @param if it has already been offered
	 */
	public BidDetails(Bid bid, double myUndiscountedUtil) {
		this.bid = bid;
		this.myUndiscountedUtil = myUndiscountedUtil;
	}
	
	public BidDetails(Bid bid, double myUndiscountedUtil, double time) {
		this.bid = bid;
		this.myUndiscountedUtil = myUndiscountedUtil;
		this.time = time;
	}
	
	/**
	 * Method which returns the bid.
	 * 
	 * @return bid
	 */
	public Bid getBid() {
		return bid;
	}
	
	/**
	 * Method which sets the bid.
	 * 
	 * @param bid
	 */
	public void setBid(Bid bid) {
		this.bid = bid;
	}
	
	/**
	 * Method which returns the utility.
	 * 
	 * @return utility
	 */
	public double getMyUndiscountedUtil() {
		return myUndiscountedUtil;
	}
	
	/**
	 * Method which sets the utility.
	 * 
	 * @param utility
	 */
	public void setMyUndiscountedUtil(double utility) {
		this.myUndiscountedUtil = utility;
	}
	
	
	public double getTime(){
		return time;
	}
	
	public void setTime(double t){
		time = t;
	}
	
	@Override
	public String toString()
	{
		return "(u=" + myUndiscountedUtil + ", t=" + time + ")";
	}
	
	/**
	 * compareTo is used to compare UTbids. The comparision is made 
	 * in such a way that the result is in reverse natural order.
	 * 
	 * @param another utbid
	 */
	public int compareTo(BidDetails utbid) {
		double otherUtil = utbid.getMyUndiscountedUtil();
		
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