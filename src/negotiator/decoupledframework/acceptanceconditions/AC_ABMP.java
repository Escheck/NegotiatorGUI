package negotiator.decoupledframework.acceptanceconditions;

import negotiator.decoupledframework.AcceptanceStrategy;
import negotiator.decoupledframework.Actions;
import negotiator.decoupledframework.NegotiationSession;
import negotiator.decoupledframework.OfferingStrategy;

/**
 * Acceptance condition of the ABMP agent.
 * 
 * http://www.verwaart.nl/culture/posterBNAIC2009ABMP.pdf
 * http://www.iids.org/publications/IJCAI01.ABMP.pdf
 * 
 * @author Alex Dirkzwager
 */
public class AC_ABMP extends AcceptanceStrategy {

	public AC_ABMP() { }

	public AC_ABMP(NegotiationSession negoSession, OfferingStrategy strat) throws Exception {
		init(negoSession, strat, null);
	}
	
	@Override
	public Actions determineAcceptability() {

		Actions decision = Actions.Reject;
		
		double UTIlITYGAPSIZE = 0.05;

		if (negotiationSession.getOwnBidHistory().getLastBidDetails() != null && 
				negotiationSession.getOpponentBidHistory().getLastBidDetails().getMyUndiscountedUtil() >= 
				negotiationSession.getOwnBidHistory().getLastBidDetails().getMyUndiscountedUtil() - UTIlITYGAPSIZE) {
			decision = Actions.Accept;
		}
		return decision;
	}
}