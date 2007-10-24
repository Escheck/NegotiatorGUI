/*
 * AcceptBid.java
 *
 * Created on November 6, 2006, 10:26 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package negotiator.actions;

import negotiator.Bid;
import negotiator.Agent;
/**
 *
 * @author Dmytro Tykhonov
 */
public class Accept extends Action {
    
    // protected Bid fBid; //Wouter: NO you accept PREVIOUS bid, not current bid!!
    /** Creates a new instance of AcceptBid */
    public Accept(Agent agent, Bid bid) {
        super(agent);
    }
    //public Bid getBid() {  return fBid; }

    public String toString() {
        return "(Accept)";
    }    
}
