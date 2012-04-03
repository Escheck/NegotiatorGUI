package negotiator.boaframework;

import java.util.HashMap;

/**
 * This is an abstract class for the agents acceptance strategy.
 * 
 * @author Alex Dirkzwager, Mark Hendrikx
 */
public abstract class  AcceptanceStrategy implements Cloneable {
	
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
	 * Clone method used to create a new object. This is a trick,
	 * such that we don't have to use reflection to create an object.
	 */
	public AcceptanceStrategy clone() {
		try {
			AcceptanceStrategy clone = (AcceptanceStrategy) super.clone();
			clone.negotiationSession = this.negotiationSession;
			clone.offeringStrategy = this.offeringStrategy;
			return clone;
		} catch (CloneNotSupportedException e) {
			throw new RuntimeException("this could never happen", e);
		}
    }
	
	/**
	 * Reset an object to ensure that no references are left in the memory.
	 * It's identical to cleanup() in agent.
	 * @return clean object
	 */
	public AcceptanceStrategy reset() {
		negotiationSession = null;
		offeringStrategy = null;
		return this;
	}
	
	/**
	 * Determines the either to accept and offer or not.
	 * @return true if accept
	 */
	public abstract Actions determineAcceptability();
}