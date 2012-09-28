package negotiator.actions;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import negotiator.AgentID;

/**
 * @author Tim Baarslag and Dmytro Tykhonov
 */
@XmlRootElement
public class Action {

	@XmlElement
    protected AgentID agentID;
    
	public Action() {}
	
    /** Creates a new instance of Action 
     * @param agentID is the agent performing the action. 
     * Note that by referring to the agent class object we effectively prevent the agent
     * from garbage collection. */
    public Action(AgentID agentID) {
        this.agentID = agentID;
    }
	
    public AgentID getAgent() {
        return agentID;
    }
	
    public String toString() {
        return "(Unknown action)";
    }    
}