import negotiator.repository.*;
/**
 * negotiationSession contains all information regarding a single negotiation session 
 * that will be played during a tournament.
 * A session is a negotation between two negotiating agents, which means the 
 * exchange of bids until one of the agents accepts or stops.
 *  
 * @author wouter
 *
 */
public class NegotiationSession
{
	AgentRepItem agentA;
	ProfileRepItem profileA; // profile also includes the domain to be used.
	
	AgentRepItem agentB;
	ProfileRepItem profileB;
	
	public NegotiationSession(AgentRepItem agtA, ProfileRepItem profA, 
				AgentRepItem agtB, ProfileRepItem profB) {
		agentA=agtA; 
		profileA=profA;
		agentB=agtB;
		profileB=profB;
	}
}