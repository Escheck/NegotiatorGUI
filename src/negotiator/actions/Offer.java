package negotiator.actions;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import negotiator.Agent;
import negotiator.AgentID;
import negotiator.Bid;

/**
 * @author Tim Baarslag and Dmytro Tykhonov
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
    
    @Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((bid == null) ? 0 : bid.hashCode());
		return result;
	}
    
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Offer other = (Offer) obj;
		if (bid == null) {
			if (other.bid != null)
				return false;
		} else if (!bid.equals(other.bid))
			return false;
		return true;
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