package negotiator.actions;

import negotiator.AgentID;

/**
 * This class is used to create an action which symbolizes
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
    @Deprecated
    public Accept(AgentID agentID) { }
    
    /**
     * @return string representation of action: "(Accept)".
     */
    public String toString() {
        return "(Accept)";
    }    
}