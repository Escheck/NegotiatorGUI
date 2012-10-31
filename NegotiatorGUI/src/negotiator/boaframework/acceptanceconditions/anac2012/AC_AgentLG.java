package negotiator.boaframework.acceptanceconditions.anac2012;

import java.util.HashMap;
import negotiator.Bid;
import negotiator.boaframework.AcceptanceStrategy;
import negotiator.boaframework.Actions;
import negotiator.boaframework.NegotiationSession;
import negotiator.boaframework.OfferingStrategy;
import negotiator.utility.UtilitySpace;

/**
 * This is the decoupled Acceptance Condition from AgentLG (ANAC2012).
 * The code was taken from the ANAC2012 AgentLG and adapted to work within the BOA framework.
 * 
 * Decoupling Negotiating Agents to Explore the Space of Negotiation Strategies
 * T. Baarslag, K. Hindriks, M. Hendrikx, A. Dirkzwager, C.M. Jonker
 *
 * @author Alex Dirkzwager
 * @version 31/10/12
 */
public class AC_AgentLG extends AcceptanceStrategy {
	
	private UtilitySpace utilitySpace;
	private final boolean TEST_EQUIVALENCE = true;

	
	public AC_AgentLG() { }
	
	public AC_AgentLG(NegotiationSession negoSession, OfferingStrategy strat) throws Exception {
		init(negoSession, strat, null);
	}

	public void init(NegotiationSession negoSession, OfferingStrategy strat, HashMap<String, Double> parameters) throws Exception {
		this.negotiationSession = negoSession;
		offeringStrategy = strat;
		utilitySpace = negotiationSession.getUtilitySpace();

	}


	@Override
	public Actions determineAcceptability() {
		double time = negotiationSession.getTime();
		Bid oponnetLastBid;

			oponnetLastBid = negotiationSession.getOpponentBidHistory().getLastBid();
		
		Bid myLastBid = negotiationSession.getOwnBidHistory().getLastBid();
		
		if(oponnetLastBid == null || myLastBid == null){
			return Actions.Reject;
		}
		//System.out.println("Decoupled myLastBid: " + myLastBid);
		double opponentUtility = utilitySpace.getUtilityWithDiscount(oponnetLastBid,time);
		double myUtility = utilitySpace.getUtilityWithDiscount(myLastBid,time);
		
		if(!TEST_EQUIVALENCE){
			if( getMyWorstBidDiscountedUtility(time)<= opponentUtility){
				return Actions.Accept;				
			}
		} else {
			//System.out.println("Decoupled opponentUtility: " + opponentUtility);
			if(0.8<= opponentUtility){
				//System.out.println("Decoupled Acceptcondition1");
				return Actions.Accept;				
			}
		}
		
		//System.out.println("Decoupled WorstBidAC: " + getMyWorstBidDiscountedUtility(time));
	//	System.out.println("Decoupled opponentUtil: " + opponentUtility);
		//System.out.println("Decoupled myUtility*0.99: " + (myUtility ));

		//System.out.println("Decoupled Condition1: " + (opponentUtility >= myUtility*0.99 ));
		//System.out.println("Decoupled Condition2: " + ( time>0.999 && opponentUtility >= myUtility) );


		//accept if opponent offer is good enough or there is no time and the offer is 'good'
		if(opponentUtility >= myUtility*0.99 ||opponentUtility >= myUtility*0.99 )
		{
			//System.out.println("Decoupled Acceptcondition2");

			return Actions.Accept;				
		}
		

		return Actions.Reject;
	}
	
	public double getMyWorstBidDiscountedUtility(double time){
		Bid myWorstBid = negotiationSession.getOwnBidHistory().getWorstBidDetails().getBid();
		return  utilitySpace.getUtilityWithDiscount(myWorstBid,time);

	}
}
