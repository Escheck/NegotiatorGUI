/*
 * BreakNegotiation.java
 *
 * Created on November 6, 2006, 10:28 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package negotiator.actions;
import negotiator.agents.Agent;
/**
 *
 * @author Dmytro Tykhonov
 */
public class EndNegotiation extends Action {
    
    /** Creates a new instance of BreakNegotiation */
    public EndNegotiation(Agent agent) {
        super(agent);
    }
    public String toString() {
        return "(EndNegotiation)";
    }    
}
