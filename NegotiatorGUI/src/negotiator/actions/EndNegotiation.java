/*
 * BreakNegotiation.java
 *
 * Created on November 6, 2006, 10:28 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package negotiator.actions;
import javax.xml.bind.annotation.XmlRootElement;


import negotiator.AgentID;
/**
 *
 * @author Dmytro Tykhonov
 */
@XmlRootElement
public class EndNegotiation extends Action {
    
	public EndNegotiation() { }
	
    /** Creates a new instance of BreakNegotiation */
    public EndNegotiation(AgentID agent) {
        super(agent);
    }
    public String toString() {
        return "(EndNegotiation)";
    }    
}
