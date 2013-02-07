package negotiator.boaframework.acceptanceconditions.anac2012;

import java.util.HashMap;
import negotiator.boaframework.AcceptanceStrategy;
import negotiator.boaframework.Actions;
import negotiator.boaframework.NegotiationSession;
import negotiator.boaframework.OfferingStrategy;
import negotiator.boaframework.sharedagentstate.anac2011.BRAMAgentSAS;
import negotiator.boaframework.sharedagentstate.anac2012.AgentLRSAS;

public class AC_AgentLG extends AcceptanceStrategy{
	
	private boolean activeHelper = false;

	
public AC_AgentLG() { }
	
	public AC_AgentLG(NegotiationSession negoSession,
			OfferingStrategy strat) throws Exception {
		init(negoSession, strat, null);
	}

	public void init(NegotiationSession negoSession, OfferingStrategy strat, HashMap<String, Double> parameters) throws Exception {
		this.negotiationSession = negoSession;
		this.offeringStrategy = strat;

		//checking if offeringStrategy SAS is a AgentLGSAS
				if (offeringStrategy.getHelper() == null || (!offeringStrategy.getHelper().getName().equals("AgentLR"))) {
					helper = new BRAMAgentSAS(negotiationSession);
					activeHelper = true;
				} else {	
					helper = (AgentLRSAS) offeringStrategy.getHelper();
				} 
	}


	@Override
	public Actions determineAcceptability() {

		double time = negotiationSession.getTime();
		if(negotiationSession.getOwnBidHistory().isEmpty()){
			return Actions.Reject;
		}
		double myUtility = negotiationSession.getUtilitySpace().getUtilityWithDiscount(negotiationSession.getOwnBidHistory().getLastBidDetails().getBid(),time);
		
		double opponentUtility = negotiationSession.getUtilitySpace().getUtilityWithDiscount(negotiationSession.getOpponentBidHistory().getLastBid(),time);
		
		/*
		if(activeHelper){
			if(!(time<0.6)) {
				if ( !(time>=0.9995)){
					//to set some parameters
					((AgentLRSAS) helper).getNextBid(time);
				}
				
			}
		}
		*/
		
		
		//System.out.println("decoupled Condition 1: " + (opponentUtility >= myUtility*0.99));
		//System.out.println("decoupled Condition 2: " + ( time>0.999 && opponentUtility >= myUtility*0.9));
		//System.out.println("decoupled Condition 3: " + (((AgentLRSAS) helper).getMyBidsMinUtility(time)<= opponentUtility));

		
		//accept if opponent offer is good enough or there is no time and the offer is 'good'
		if(opponentUtility >= myUtility*0.99 ||( time>0.999 && opponentUtility >= myUtility*0.9)
				|| ((AgentLRSAS) helper).getMyBidsMinUtility(time)<= opponentUtility)
		{
			return Actions.Accept;

		}
		return Actions.Reject;
	}

}
