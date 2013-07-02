package negotiator.actions;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import negotiator.AgentID;
import negotiator.Party;
import negotiator.Vote;

/**
*
* @author Reyhan
*/

@XmlRootElement

public class VoteForOfferAcceptance extends Action {
	
	@XmlElement
	 protected Vote vote;
	
	public VoteForOfferAcceptance(){
		vote=Vote.ACCEPT;
	}
	
	public VoteForOfferAcceptance(AgentID party, Vote vote){
		super(party);
		this.vote=vote;
	}
	
    public VoteForOfferAcceptance(Party party, Vote vote) {
	    this(party.getPartyID(),vote);
	}
    
    public Vote getVote(){
    	return vote;
    }
    
    public String toString() {
        return "Vote: " + (vote == null ? "null" : (vote==Vote.ACCEPT ? "Accept" : "Reject"));
    }
    
}
