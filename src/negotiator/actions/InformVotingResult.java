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

public class InformVotingResult extends Action{
	@XmlElement
	 protected Vote vote;
	
	public InformVotingResult(){
		vote=Vote.ACCEPT;
	}
	
	public  InformVotingResult(AgentID party, Vote vote){
		super(party);
		this.vote=vote;
	}
	
   public InformVotingResult(Party party, Vote vote) {
	    this(party.getPartyID(),vote);
	}
   
   public Vote getVotingResult(){
   	return vote;
   }
   
   public String toString() {
       return "Voting Result: " + (vote == null ? "null" : (vote==Vote.ACCEPT ? "Accept" : "Reject"));
   }
   
}
