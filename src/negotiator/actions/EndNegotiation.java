package negotiator.actions;

import negotiator.AgentID;

/**
 * Class which symbolizes the action to leave a negotiation.
 * 
 * @author Dmytro Tykhonov
 */
public class EndNegotiation extends Action {
    
    /**
     * Action to end the negotiation.
     */
	public EndNegotiation() { }
	
    /**
     * Action to end the negotiation.
     * @param agentID of the opponent
     */
    public EndNegotiation(AgentID agentID) {
    	this.agentID = agentID;
    }
	
    /**
     * @return string representation of action: "(EndNegotiation)".
     */
    public String toString() {
        return "(EndNegotiation)";
    }    
}