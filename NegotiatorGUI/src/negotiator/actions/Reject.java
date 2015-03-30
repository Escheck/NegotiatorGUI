package negotiator.actions;

import negotiator.AgentID;

/**
 * This class is used to createFrom an action which symbolizes
 * that an agent accepts an offer.
 *  
 * @author Dmytro Tykhonov
 */
public class Reject extends Action {

	/**
	 * Action to accept an opponent's bid.
	 */
    public Reject() { }

    /**
     * Action to accept an opponent's bid.
     * @param agentID of the opponent.
     */
    public Reject(AgentID agentID) {
		super(agentID);
	}
    
    /**
     * @return string representation of action: "(Reject)".
     */
    public String toString() {
        return "(Reject)";
    }    
}