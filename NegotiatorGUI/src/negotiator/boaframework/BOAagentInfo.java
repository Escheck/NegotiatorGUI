package negotiator.boaframework;

import java.io.Serializable;

/**
 * This class is a container which describes a full BOA agent. This object is
 * used to carry the information from the GUI to the agent loader.
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
	 * Creates a container object describing a BOA agent.
	 * 
	 * @param bs
	 *            the bidding strategy of the agent
	 * @param as
	 *            the acceptance strategy of the agent
	 * @param om
	 *            the opponent model of the agent
	 * @param oms
	 *            the opponent model strategy of the agent
	 */
	public BOAagentInfo(BOAcomponent bs, BOAcomponent as, BOAcomponent om,
			BOAcomponent oms) {
		this.offeringStrategy = bs;
		this.acceptanceStrategy = as;
		this.opponentModel = om;
		this.omStrategy = oms;
	}

	/**
	 * @return offering strategy of the BOA Agent.
	 */
	public BOAcomponent getOfferingStrategy() {
		return offeringStrategy;
	}

	/**
	 * @return acceptance strategy of the BOA Agent.
	 */
	public BOAcomponent getAcceptanceStrategy() {
		return acceptanceStrategy;
	}

	/**
	 * @return opponent model of the BOA Agent.
	 */
	public BOAcomponent getOpponentModel() {
		return opponentModel;
	}

	/**
	 * @return opponent model strategy of the BOA Agent.
	 */
	public BOAcomponent getOMStrategy() {
		return omStrategy;
	}

	/**
	 * FIXME this is NOT returning the name of the agent.
	 * 
	 * @return name of the BOA Agent.
	 * 
	 */
	public String getName() {
		return toString();
	}

	public String toString() {
		String result = offeringStrategy + " " + acceptanceStrategy + " "
				+ opponentModel + " " + omStrategy;
		return result;
	}
}