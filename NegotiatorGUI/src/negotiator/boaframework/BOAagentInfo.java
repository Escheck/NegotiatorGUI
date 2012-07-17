package negotiator.boaframework;

import java.io.Serializable;

/**
 * This class is a container which describes a full boa agent.
 * This object is used to carry the information from the GUI to the
 * agent loader.
 * 
 * Please report bugs to author.
 * 
 * @author Mark Hendrikx (m.j.c.hendrikx@student.tudelft.nl)
 * @version 16-01-12
 */
public class BOAagentInfo implements Serializable {
	
	private static final long serialVersionUID = 2868410344415899340L;
	/** Offering strategy of the specified agent */
	private BOAcomponent offeringStrategy;
	/** Acceptance strategy of the specified agent */
	private BOAcomponent acceptanceStrategy;
	/** Opponent model of the specified agent */
	private BOAcomponent opponentModel;
	/** Opponent model strategy of the specified agent */
	private BOAcomponent omStrategy;

	/**
	 * Creates a container object describing a decoupled agent.
	 * @param bs the bidding strategy of the agent
	 * @param as the acceptance strategy of the agent
	 * @param om the opponent model of the agent
	 * @param oms the opponent model strategy of the agent
	 */
	public BOAagentInfo(BOAcomponent bs, BOAcomponent as, 
								BOAcomponent om, BOAcomponent oms) {
		this.offeringStrategy = bs;
		this.acceptanceStrategy = as;
		this.opponentModel = om;
		this.omStrategy = oms;
	}
	
	public BOAcomponent getOfferingStrategy() {
		return offeringStrategy;
	}

	public void setOfferingStrategy(BOAcomponent offeringStrategy) {
		this.offeringStrategy = offeringStrategy;
	}

	public BOAcomponent getAcceptanceStrategy() {
		return acceptanceStrategy;
	}

	public void setAcceptanceStrategy(BOAcomponent acceptanceStrategy) {
		this.acceptanceStrategy = acceptanceStrategy;
	}

	public BOAcomponent getOpponentModel() {
		return opponentModel;
	}

	public void setOpponentModel(BOAcomponent opponentModel) {
		this.opponentModel = opponentModel;
	}
	
	public BOAcomponent getOMStrategy() {
		return omStrategy;
	}

	public void setOMStrategy(BOAcomponent omStrategy) {
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