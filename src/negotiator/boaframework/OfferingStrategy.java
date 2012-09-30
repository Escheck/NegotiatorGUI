package negotiator.boaframework;

import java.util.HashMap;

import negotiator.bidding.BidDetails;

/**
 * Describes a bidding strategy of an agent of the BOA framework.
 * 
 * Tim Baarslag, Koen Hindriks, Mark Hendrikx, Alex Dirkzwager and Catholijn M. Jonker.
 * Decoupling Negotiating Agents to Explore the Space of Negotiation Strategies
 * 
 * @author Alex Dirkzwager, Mark Hendrikx
 * @version 15-12-11
 */
public abstract class OfferingStrategy { 
	/** The next bid the agent plans to present */
	protected BidDetails nextBid;
	/** Reference to the negotiation session */
	protected NegotiationSession negotiationSession;
	/** Reference to the opponent model */
	protected OpponentModel opponentModel;
	/** Reference to the opponent model strategy */
	protected OMStrategy omStrategy;
	/** Reference to helper class used if there are dependencies between
	 * the acceptance condition an offering strategy  */
	protected SharedAgentState helper;
	/** Boolean to see if endNegotiation is called */
	protected boolean endNegotiation;
	
	/**
	 * Initializes the offering strategy. If parameters are used,
	 * this method should be overridden.
	 * 
	 * @param negotiationSession
	 * @param parameters
	 */
	public void init(NegotiationSession negotiationSession, OpponentModel opponentModel, 
						OMStrategy omStrategy, HashMap<String, Double> parameters) throws Exception {
		this.negotiationSession = negotiationSession;
		this.opponentModel = opponentModel;
		this.omStrategy = omStrategy;
		this.endNegotiation = false;
	}
	
	/**
	 * determines the first bid to be offered by the agent
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
	
	public SharedAgentState getHelper() {
		return helper;
	}

	public boolean isEndNegotiation() {
		return endNegotiation;
	}
	
	public NegotiationSession getNegotiationSession()
	{
		return negotiationSession;
	}
}