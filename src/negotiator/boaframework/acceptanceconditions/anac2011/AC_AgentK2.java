package negotiator.boaframework.acceptanceconditions.anac2011;

import java.util.HashMap;
import java.util.Random;

import negotiator.bidding.BidDetails;
import negotiator.boaframework.AcceptanceStrategy;
import negotiator.boaframework.Actions;
import negotiator.boaframework.NegotiationSession;
import negotiator.boaframework.OfferingStrategy;
import negotiator.boaframework.sharedagentstate.anac2011.AgentK2SAS;

/**
 * This is the decoupled Acceptance Condition from Agent K (ANAC2010).
 * The code was taken from the ANAC2010 Agent K and adapted to work within the Decoupledframework.
 *
 * @author Mark Hendrikx
 * @version 25/12/11
 */
public class AC_AgentK2 extends AcceptanceStrategy {
    
	private Random random100;
	private boolean activeHelper = false;
	private final boolean TEST_EQUIVALENCE = false;
	
    public AC_AgentK2() { }
    
	public AC_AgentK2(NegotiationSession negoSession, OfferingStrategy strat) throws Exception {
		initializeAgent(negoSession, strat);
	}
	
	public void init(NegotiationSession negoSession, OfferingStrategy strat, HashMap<String, Double> parameters) throws Exception {
		initializeAgent(negoSession, strat);
	}
	
	public void initializeAgent(NegotiationSession negotiationSession, OfferingStrategy os) throws Exception {
		this.negotiationSession = negotiationSession;
		this.offeringStrategy = os;

		if (offeringStrategy.getHelper() == null || (!offeringStrategy.getHelper().getName().equals("AgentK"))) {
			helper = new AgentK2SAS(negotiationSession);
			activeHelper = true;
		} else {	
			helper = (AgentK2SAS) offeringStrategy.getHelper();
		}
		
		if (TEST_EQUIVALENCE) {
			random100 = new Random(100);
		} else {
			random100 = new Random();
		}
	}
	
	@Override
	public Actions determineAcceptability() {
		BidDetails opponentBid = negotiationSession.getOpponentBidHistory().getLastBidDetails();
		if (opponentBid != null) {
			double p;
			if(activeHelper){
				p = ((AgentK2SAS)helper).calculateAcceptProbability();
			} else {
				p = ((AgentK2SAS)helper).getAcceptProbability();
			}
			if (p > random100.nextDouble()) {
				return Actions.Accept;
			}
		}
		return Actions.Reject;
	}
}