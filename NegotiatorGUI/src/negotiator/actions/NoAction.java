

package negotiator.actions;

import javax.xml.bind.annotation.XmlRootElement;

import negotiator.Agent;
import negotiator.AgentID;

@XmlRootElement
public class NoAction extends Action {
    
	public NoAction() {

	}
  
    public NoAction(AgentID agent) {
        super(agent);
    }
    
   
    public NoAction(Agent agent) {
        this(agent.getAgentID());
    }
    
  
    public String toString() {
        return "(No additional action)";
    }    
}
