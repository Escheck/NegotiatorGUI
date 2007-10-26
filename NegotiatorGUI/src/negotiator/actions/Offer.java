/*
 * SendBid.java
 *
 * Created on November 6, 2006, 10:23 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package negotiator.actions;

import negotiator.Bid;
import negotiator.agents.Agent;
/**
 *
 * @author Dmytro Tykhonov
 */

public class Offer extends Action {
    
    protected Bid bid;
    /** Creates a new instance of SendBid */
    public Offer(Agent agent, Bid bid) {
        super(agent);
        this.bid = bid;
    }
    public Bid getBid() {
        return bid;
    }
    public String toString() {
        return "(Offer: " + bid.toString() + ")";
    }
    
}
