package negotiator.actions;

import negotiator.AgentID;

/**
 * Class which symbolizes a high level action.
 * 
 * @author Tim Baarslag and Dmytro Tykhonov
 */
public abstract class Action {

	/** ID of the agent which performed the action. */
    protected AgentID agentID;
    
	public Action() {}
	
    /** Creates a new instance of Action 
     * @param agentID is the agent performing the action. 
	 */
    public Action(AgentID agentID) {
        this.agentID = agentID;
    }
	
    /**
     * Returns the agent which performed the action.
     * @return action performing the action.
     */
    public AgentID getAgent() {
        return agentID;
    }
	
    public String toString() {
        return "(Unknown action)";
    }    
}