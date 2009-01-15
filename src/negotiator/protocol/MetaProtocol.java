package negotiator.protocol;

import negotiator.NegotiationOutcome;

public interface MetaProtocol extends Runnable {

	public String getName();
	
	public NegotiationOutcome getNegotiationOutcome();
	
}
