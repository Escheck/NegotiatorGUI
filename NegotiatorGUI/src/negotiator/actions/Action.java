package negotiator.actions;

import negotiator.AgentID;
import negotiator.Bid;

/**
 * Class which symbolizes a high level action.
 * 
 * @author Tim Baarslag and Dmytro Tykhonov
 */
public abstract class Action {

	private AgentID agentID;
	
    /**
     * Empty constructor used for inheritance.
     */
	public Action() {}
	
	/**
	 * Constructor which sets the agentID of an agent.
	 * @param agentID of the agent which created the action.
	 */
	public Action(AgentID agentID) {
        this.agentID = agentID;
    }
	
	/**
	 * Returns the ID of the agent which created the action.
	 * @return ID of the agent.
	 */
	public AgentID getAgent() {
        return agentID;
    }
	
    /**
     * Enforces that actions implements a string-representation.
     */
    public abstract String toString();
    
    /** Method which returns the bid of the current action if it
     * is of the type Offer or else Null.
     * @param currentAction of which we want the offer.
     * @return bid specifies by this action or null if there is none.
     */
    public static Bid getBidFromAction(Action currentAction) {
     		
 		Bid currentBid=null; 
 		if (currentAction instanceof EndNegotiationWithAnOffer) //RA 
 			currentBid= ((EndNegotiationWithAnOffer) currentAction).getBid();
 		else if (currentAction instanceof Offer)
 			currentBid= ((Offer) currentAction).getBid();
 		
 		return currentBid;
    }
}