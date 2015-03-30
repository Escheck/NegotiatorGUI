package negotiator.actions;

import negotiator.AgentID;

/**
 * This class is used to createFrom an action which symbolizes
 * that an agent accepts an offer.
 *  
 * @author Dmytro Tykhonov
 */
public class Accept extends Action {

	/**
	 * Action to accept an opponent's bid.
	 */
    public Accept() { }
    
    /**
     * Action to accept an opponent's bid.
     * @param agentID of the opponent.
     */
    public Accept(AgentID agentID) {
		super(agentID);
	}
    
    /**
     * @return string representation of action: "(Accept)".
     */
    public String toString() {
        return "(Accept)";
    }    
}