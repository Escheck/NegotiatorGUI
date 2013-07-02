package negotiator.actions;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import negotiator.AgentID;
import negotiator.Feedback;
import negotiator.Party;

/**
*
* @author Reyhan
*/

@XmlRootElement

public class GiveFeedback extends Action {
	
	@XmlElement
	 protected Feedback feedback;
	
	public GiveFeedback(AgentID party, Feedback feedback){
		super(party);
		this.feedback=feedback;
	}
	
    public GiveFeedback(Party party, Feedback feedback) {
	    this(party.getPartyID(),feedback);
	}
    
    public Feedback getFeedback(){
    	return feedback;
    }
    
    public String toString() {
        return "Feedback: " + (feedback == null ? "null" : (feedback==Feedback.BETTER ? "Better" :  (feedback==Feedback.SAME ? "SAME" : "Worse")));
    }
    
}
