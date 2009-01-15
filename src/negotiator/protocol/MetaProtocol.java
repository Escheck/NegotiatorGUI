package negotiator.protocol;

import negotiator.Agent;
import negotiator.NegotiationOutcome;

public interface MetaProtocol extends Runnable {

	public String getName();
	
	public NegotiationOutcome getNegotiationOutcome();
	
	public Agent getAgent(int index);
}
