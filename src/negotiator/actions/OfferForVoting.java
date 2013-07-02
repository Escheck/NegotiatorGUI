package negotiator.actions;

import javax.xml.bind.annotation.XmlRootElement;

import negotiator.Agent;
import negotiator.AgentID;
import negotiator.Bid;
/**
 *
 * @author Reyhan Aydogan
 */

@XmlRootElement
public class OfferForVoting extends Offer {
            
    /** Creates a new instance of SendBid */
    public OfferForVoting(AgentID agent, Bid bid) {
        super(agent, bid);
    }
    
    /** Creates a new instance of SendBid */
    public OfferForVoting(Agent agent, Bid bid) {
        this(agent.getAgentID(), bid);
    }
    
    public Bid getBid() {
        return bid;
    }
    public String toString() {
        return "(Offer: " + (bid == null ? "null" : bid.toString()) + ")";
    }
    
}
