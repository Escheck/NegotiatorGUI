package negotiator.actions;
import negotiator.Agent;
/**
 * This action represents that the agent did an illegal action (not fitting the protocol), eg kill his agent.
 * @author W.Pasman 17sept08
 */
public class IllegalAction extends Action {
    
	String details;
    /** Creates a new instance of BreakNegotiation */
    public IllegalAction(Agent agent,String dets) {
        super(agent);
        details=dets;
    }
    public String toString() {
        return "(IllegalAction- "+details+")";
    }    
}
