package negotiator.actions;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import negotiator.AgentID;
import negotiator.Bid;
import negotiator.Party;

/**
*
* @author Reyhan
*/

@XmlRootElement
public class EndNegotiationWithAnOffer extends Action{
	
	 @XmlElement
	    protected Bid bid;
	 
	 public EndNegotiationWithAnOffer() {}
	
	 public EndNegotiationWithAnOffer(AgentID party, Bid bid) {
	      super(party);
	      this.bid = bid;
	 }
	 
	 public EndNegotiationWithAnOffer(Party party, Bid bid) {
	      this(party.getPartyID(), bid);
	 }
	    
	 public Bid getBid() {
	      return bid;
	 }
	 
	 public String toString() {
	      return "End Negotiation with Offer: " + (bid == null ? "null" : bid.toString());
	 }

}
