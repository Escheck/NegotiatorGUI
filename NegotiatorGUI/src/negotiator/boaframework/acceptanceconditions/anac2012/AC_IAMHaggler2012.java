package negotiator.boaframework.acceptanceconditions.anac2012;

import java.util.HashMap;

import negotiator.Bid;
import negotiator.boaframework.AcceptanceStrategy;
import negotiator.boaframework.Actions;
import negotiator.boaframework.NegotiationSession;
import negotiator.boaframework.OfferingStrategy;
import negotiator.utility.UtilitySpace;

/**
 * This is the decoupled Acceptance Condition from IAMhaggler2012 (ANAC2012).
 * The code was taken from the ANAC2012 IAMhaggler2012 and adapted to work within the BOA framework.
 * 
 * Decoupling Negotiating Agents to Explore the Space of Negotiation Strategies
 * T. Baarslag, K. Hindriks, M. Hendrikx, A. Dirkzwager, C.M. Jonker
 *
 * @author Alex Dirkzwager
 * @version 31/10/12
 */
public class AC_IAMHaggler2012 extends AcceptanceStrategy{

	private UtilitySpace utilitySpace;
	private double acceptMultiplier = 1.02;
	private double MAXIMUM_ASPIRATION = 0.9;

	
	 	public AC_IAMHaggler2012() { }
	    
		public AC_IAMHaggler2012(NegotiationSession negoSession, OfferingStrategy strat) throws Exception {
			initializeAgent(negoSession, strat);
		}
		
		@Override
		public void init(NegotiationSession negoSession, OfferingStrategy strat, HashMap<String, Double> parameters) throws Exception {
			super.init(negoSession, strat, parameters);
			initializeAgent(negoSession, strat);
		}
		
		public void initializeAgent(NegotiationSession negotiationSession, OfferingStrategy os) throws Exception {
			utilitySpace = negotiationSession.getUtilitySpace();

		}	
	@Override
	public Actions determineAcceptability() {
		Bid opponentBid = negotiationSession.getOpponentBidHistory().getLastBid();
		Bid myLastBid = negotiationSession.getOwnBidHistory().getLastBid();
		if(opponentBid == null || myLastBid == null){
			return Actions.Reject;
		}

		try{
			if (utilitySpace.getUtility(opponentBid) * acceptMultiplier >= utilitySpace.getUtility(myLastBid)) {
				// Accept opponent's bid based on my previous bid.
				return Actions.Accept;
			} else if (utilitySpace.getUtility(opponentBid) * acceptMultiplier >= MAXIMUM_ASPIRATION) {
				// Accept opponent's bid based on my previous bid.
				return Actions.Accept;
	
			}
			
			if (utilitySpace.getUtility(opponentBid) * acceptMultiplier >= offeringStrategy.getNextBid().getMyUndiscountedUtil()){
				// Accept opponent's bid based on my planned bid.
				return Actions.Accept;
			}
				
		} catch(Exception e){
			e.printStackTrace();
		}
		return Actions.Reject;
	}

}
