package negotiator.boaframework.acceptanceconditions.anac2011;

import java.util.HashMap;
import negotiator.boaframework.AcceptanceStrategy;
import negotiator.boaframework.Actions;
import negotiator.boaframework.NegotiationSession;
import negotiator.boaframework.OfferingStrategy;
import negotiator.boaframework.sharedagentstate.anac2011.ValueModelAgentSAS;


public class AC_ValueModelAgent extends AcceptanceStrategy {
	
    public AC_ValueModelAgent() { }
    
	public AC_ValueModelAgent(NegotiationSession negoSession, OfferingStrategy strat) throws Exception {
		initializeAgent(negoSession, strat);
	}
	
	public void init(NegotiationSession negoSession, OfferingStrategy strat, HashMap<String, Double> parameters) throws Exception {
		initializeAgent(negoSession, strat);
	}
	
	public void initializeAgent(NegotiationSession negotiationSession, OfferingStrategy os) throws Exception {
		this.negotiationSession = negotiationSession;
		this.offeringStrategy = os;
		helper = os.getHelper();
	}
	
	@Override
	public Actions determineAcceptability() {
		boolean skip = ((ValueModelAgentSAS)helper).shouldSkipAcceptDueToCrash();
		if (negotiationSession.getOpponentBidHistory().size() > 0) {

			
			
			if (!skip && negotiationSession.getTime() > 0.98 && negotiationSession.getTime() <= 0.99) {
				if (((ValueModelAgentSAS)helper).getOpponentUtil() >= ((ValueModelAgentSAS)helper).getLowestApprovedInitial() - 0.01) {
					return Actions.Accept;
				}
			}
			if (!skip && negotiationSession.getTime() > 0.995 && ((ValueModelAgentSAS)helper).getOpponentMaxBidUtil() > 0.55) {
				if (((ValueModelAgentSAS)helper).getOpponentUtil() >= ((ValueModelAgentSAS)helper).getOpponentMaxBidUtil() * 0.99) {
					return Actions.Accept;
				}
			}
			

			// if our opponent settled enough for us we accept, and there is
			// a discount factor we accept
			// if(opponent.expectedDiscountRatioToConvergence()*opponentUtil
			// > lowestApproved){
			if (!skip && ((ValueModelAgentSAS)helper).getOpponentUtil() > ((ValueModelAgentSAS)helper).getLowestApproved()
					&& (negotiationSession.getDiscountFactor() > 0.02 || ((ValueModelAgentSAS)helper).getOpponentUtil() > 0.975)) {
					return Actions.Accept;
			}
			if (!skip && negotiationSession.getTime() > 0.9) {
				if (((ValueModelAgentSAS)helper).getOpponentUtil() >= ((ValueModelAgentSAS)helper).getPlannedThreshold() - 0.01) {
					return Actions.Accept;
				}
			}
		}
		return Actions.Reject;
	}
}