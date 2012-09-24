package negotiator.actions;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import negotiator.AgentID;

/**
 * This action represents that the agent did an illegal action
 * (not fitting the protocol), eg kill his agent.
 * @author W.Pasman 17sept08
 */
@XmlRootElement
public class IllegalAction extends Action {

    @XmlAttribute
	private String details;
	
    /** Creates a new instance of BreakNegotiation */
    public IllegalAction(AgentID agent,String dets) {
        super(agent);
        details=dets;
    }
	
    public String toString() {
        return "(IllegalAction- "+details+")";
    }    
}
