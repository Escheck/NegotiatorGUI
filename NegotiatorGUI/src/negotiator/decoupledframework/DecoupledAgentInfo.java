package negotiator.decoupledframework;

import java.io.Serializable;

/**
 * This class is a container which describes a full decoupled agent.
 * 
 * Please report bugs to author.
 * 
 * @author Mark Hendrikx (m.j.c.hendrikx@student.tudelft.nl)
 * @version 16-01-12
 */
public class DecoupledAgentInfo implements Serializable {
	
	private static final long serialVersionUID = 2868410344415899340L;
	private DecoupledComponent offeringStrategy;
	private DecoupledComponent acceptanceStrategy;
	private DecoupledComponent opponentModel;
	private DecoupledComponent omStrategy;

	/**
	 * Creates a container object describing a decoupled agent.
	 * @param bs the bidding strategy of the agent
	 * @param as the acceptance strategy of the agent
	 * @param om the opponent model of the agent
	 * @param oms the opponent model strategy of the agent
	 */
	public DecoupledAgentInfo(DecoupledComponent bs, DecoupledComponent as, 
								DecoupledComponent om, DecoupledComponent oms) {
		this.offeringStrategy = bs;
		this.acceptanceStrategy = as;
		this.opponentModel = om;
		this.omStrategy = oms;
	}
	
	public DecoupledComponent getOfferingStrategy() {
		return offeringStrategy;
	}

	public void setOfferingStrategy(DecoupledComponent offeringStrategy) {
		this.offeringStrategy = offeringStrategy;
	}

	public DecoupledComponent getAcceptanceStrategy() {
		return acceptanceStrategy;
	}

	public void setAcceptanceStrategy(DecoupledComponent acceptanceStrategy) {
		this.acceptanceStrategy = acceptanceStrategy;
	}

	public DecoupledComponent getOpponentModel() {
		return opponentModel;
	}

	public void setOpponentModel(DecoupledComponent opponentModel) {
		this.opponentModel = opponentModel;
	}
	
	public DecoupledComponent getOMStrategy() {
		return omStrategy;
	}

	public void setOMStrategy(DecoupledComponent omStrategy) {
		this.omStrategy = omStrategy;
	}
	
	public String getName() {
		return toString();
	}

	public String toString() {
		String result = offeringStrategy + " " + acceptanceStrategy + " " + opponentModel + " " + omStrategy;
		return result;
	}
}