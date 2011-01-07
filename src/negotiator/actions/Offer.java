/*
 * SendBid.java
 *
 * Created on November 6, 2006, 10:23 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package negotiator.actions;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import negotiator.Agent;
import negotiator.AgentID;
import negotiator.Bid;
/**
 *
 * @author Dmytro Tykhonov
 */

@XmlRootElement
public class Offer extends Action {
    @XmlElement
    protected Bid bid;
    
    public Offer() { }
    /** Creates a new instance of SendBid */
    public Offer(AgentID agent, Bid bid) {
        super(agent);
        this.bid = bid;
    }
    
    /** Creates a new instance of SendBid */
    public Offer(Agent agent, Bid bid) {
        this(agent.getAgentID(), bid);
    }
    
    public Bid getBid() {
        return bid;
    }
    public String toString() {
        return "(Offer: " + (bid == null ? "null" : bid.toString()) + ")";
    }
    
}
