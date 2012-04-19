package negotiator.boaframework;
import java.util.HashMap;
import misc.Range;
import negotiator.bidding.BidDetails;

/**
 * This is an abstract class for the agents offering strategy
 * 
 * @author Alex Dirkzwager, Mark Hendrikx
 * @version 15-12-11
 */
public abstract class OfferingStrategy { 
	//is the next bid the agent is willing to present
	protected BidDetails nextBid;	
	protected Range bidTargetRange;
	protected NegotiationSession negotiationSession;
	protected OpponentModel opponentModel;
	protected OMStrategy omStrategy;
	
	//A helper class for ANAC2010 and ANAC2011 agents
	protected SharedAgentState helper;
	
	/**
	 * Initializes the offering strategy. If parameters are used,
	 * this method should be overridden.
	 * 
	 * @param domainKnow
	 * @param parameters
	 */
	public void init(NegotiationSession domainKnow, OpponentModel model, OMStrategy omStrategy, HashMap<String, Double> parameters) throws Exception {
		negotiationSession = domainKnow;
		this.opponentModel = model;
		this.omStrategy = omStrategy;
	}
	
	/**
	 * determines the first bid
	 * @return UTBid the beginBid
	 */
	public abstract BidDetails determineOpeningBid();

	
	/**
	 * determines the next bid the agent will offer to the opponent
	 * @return UTBid the nextBid
	 */
	public abstract BidDetails determineNextBid();
		
	
	public BidDetails getNextBid(){
		return nextBid;
	}
	
	public void setNextBid(BidDetails counterBid) {
		nextBid = counterBid;
	}
	
	public Range getTargetRange(){
		return bidTargetRange;
	}
	
	public SharedAgentState getHelper() {
		return helper;
	}
}