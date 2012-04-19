package negotiator.boaframework;

import java.util.HashMap;

/**
 * This is an abstract class for the agents acceptance strategy.
 * 
 * @author Alex Dirkzwager, Mark Hendrikx
 */
public abstract class  AcceptanceStrategy {
	
	protected NegotiationSession negotiationSession;
	protected OfferingStrategy offeringStrategy;
	protected SharedAgentState helper;
	
	/**
	 * Standard initialize method to be called after using the empty constructor.
	 * Most of the time this method should be overridden for usage by the decoupled
	 * framework.
	 * 
	 * @param domain
	 * @param strat
	 * @param parameters
	 * @throws Exception
	 */
	public void init(NegotiationSession domain, OfferingStrategy strat, HashMap<String, Double> parameters) throws Exception {
		this.negotiationSession = domain;
		this.offeringStrategy = strat;
	}
	
	public String printParameters(){
		return"";
	}
	
	/**
	 * Determines the either to accept and offer or not.
	 * @return true if accept
	 */
	public abstract Actions determineAcceptability();
}