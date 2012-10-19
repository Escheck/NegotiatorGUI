package negotiator.actions;

import negotiator.AgentID;

/**
 * This action represents that the agent did an illegal action
 * (not fitting the protocol), eg kill his agent.
 * 
 * @author W.Pasman 17sept08
 */
public class IllegalAction extends Action {

	private String details;
	
    /**
     * Specifies that an agent returned an action not
     * fitting the protocol.
     * 
     * @param agent to blame.
     * @param details of the error.
     */
    public IllegalAction(AgentID agent,String details) {
        this.details = details;
    }
	
    /**
     * Specifies that an agent returned an action not
     * fitting the protocol.
     * 
     * @param details of the error.
     */
    public IllegalAction(String details) {
        this.details = details;
    }
    
    /**
     * @return string representation of action: "(IllegalAction-DETAILS)".
     */
    public String toString() {
        return "(IllegalAction- "+details+")";
    }    
}