/*
 * AcceptBid.java
 *
 * Created on November 6, 2006, 10:26 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package negotiator.actions;

import javax.xml.bind.annotation.XmlRootElement;

import negotiator.Agent;
import negotiator.AgentID;
/**
 *
 * @author Dmytro Tykhonov
 */
@XmlRootElement
public class Accept extends Action {
    public Accept() {

	}
    // protected Bid fBid; //Wouter: NO you accept PREVIOUS bid, not current bid!!
    /** Creates a new instance of AcceptBid */
    public Accept(AgentID agent) {
        super(agent);
    }
    
    /** Creates a new instance of AcceptBid */
    public Accept(Agent agent) {
        this(agent.getAgentID());
    }
    
    //public Bid getBid() {  return fBid; }

    public String toString() {
        return "(Accept)";
    }    
}
