package negotiator.actions;

import javax.xml.bind.annotation.XmlRootElement;

import negotiator.Agent;
import negotiator.AgentID;

/**
 * @author Dmytro Tykhonov
 */
@XmlRootElement
public class Accept extends Action {

    public Accept() { }

    /** Creates a new instance of AcceptBid */
    public Accept(AgentID agentID) {
        super(agentID);
    }
    
    /** Creates a new instance of AcceptBid */
    public Accept(Agent agent) {
        this(agent.getAgentID());
    }
    
    public String toString() {
        return "(Accept)";
    }    
}