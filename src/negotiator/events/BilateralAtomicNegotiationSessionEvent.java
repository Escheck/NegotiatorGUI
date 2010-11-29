package negotiator.events;

import negotiator.protocol.BilateralAtomicNegotiationSession;
import negotiator.repository.AgentRepItem;
import negotiator.repository.ProfileRepItem;

public class BilateralAtomicNegotiationSessionEvent extends NegotiationEvent {
	private BilateralAtomicNegotiationSession session;
	private ProfileRepItem profileA;
	private ProfileRepItem profileB;
	private AgentRepItem agentA;
	private AgentRepItem agentB;
	private String agentAName;
	private String agentBName;
	
	public BilateralAtomicNegotiationSessionEvent(Object source, 
			BilateralAtomicNegotiationSession session, 	
			ProfileRepItem profileA,
			ProfileRepItem profileB,
			AgentRepItem agentA,
			AgentRepItem agentB, String agentAName, String agentBName) {
		super(source);
		this.session = session;
		this.agentA = agentA;
		this.agentB = agentB;
		this.profileA = profileA;
		this.profileB = profileB;
		this.agentAName = agentAName;
		this.agentBName = agentBName;
	}
	
	public BilateralAtomicNegotiationSession getSession() {
		return session;
	}
	public ProfileRepItem getProfileA() {
		return profileA;
	}
	public ProfileRepItem getProfileB() {
		return profileB;
	}
	public AgentRepItem getAgentA() {
		return agentA;
	}
	public AgentRepItem getAgentB() {
		return agentB;
	}

	public String getAgentAName()
	{
		return agentAName;
	}

	public String getAgentBName()
	{
		return agentBName;
	}
}
