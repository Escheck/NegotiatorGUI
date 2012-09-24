package negotiator.analysis;

import java.util.ArrayList;

import negotiator.Agent;

/**
 * Sensitivity analysis (see chapter 2 of Dmytro's thesis) 
 */
public class BiddingHistoryAnalysis 
{
	public enum STEP_CLASS { UNKNOWN, FORTUNATE, UNFORTUNATE, CONCESSION, NICE, SILENT, SELFISH;}
	public static final double CONSTANT_K = 0.006;

	
	public static void calculateOppPrefSens(Agent[] pAgents, ArrayList<BidPoint> pBiddingHistory, ArrayList<BidPoint> pPareto,  double[] pSensAgent) {
		double sensAgentA = 0, 		sensAgentB = 0;
		int    nrOfBidsAgentA = 0,  nrOfBidsAgentB = 0;
		int turn = 0;
		for (BidPoint bid : pBiddingHistory) 
		{
			double s = getDistanceToPareto(pAgents, turn, bid, pPareto);
			if(turn % 2 == 0) { 
				sensAgentA = sensAgentA + s;
				nrOfBidsAgentA++;
			} else { 
				sensAgentB = sensAgentB + s;
				nrOfBidsAgentB++;
			}
			turn++;
		}
		pSensAgent[0] = sensAgentA / nrOfBidsAgentA;
		pSensAgent[1] =  sensAgentB / nrOfBidsAgentB;
		
	}
	
	/**
	 * Calculates distance to Pareto frontier of a single bid
	 * @param turn 
	 */
	private static double getDistanceToPareto(Agent[] pAgents, int turn, BidPoint bid , ArrayList<BidPoint> pPareto) {
		Agent opponent = null;
		int agent = turn % 2;
		int o = (turn + 1) % 2;
		if(agent == 0) 
			opponent = pAgents[1];
		else 
			opponent = pAgents[0];		
		for (int i=1;i<pPareto.size();i++) 
		{
			if(pPareto.get(i-1).getUtility(agent) >= bid.getUtility(agent) &&
			   (pPareto.get(i).getUtility(agent)) <= bid.getUtility(agent))
			   {
				double u = (bid.getUtility(agent) - pPareto.get(i-1).getUtility(agent)) /
				(pPareto.get(i).getUtility(agent) - pPareto.get(i-1).getUtility(agent))*
				(pPareto.get(i).getUtility(o)-pPareto.get(i-1).getUtility(o))+pPareto.get(i-1).getUtility(o);
				return u-bid.getUtility(o); 
			}			  
		}
		return 0;
	}
	public static AnalysisResult sensitivityAnalysis(/*Agent[] pAgents,*/ ArrayList<BidPoint> fBiddingHistoryA, ArrayList<BidPoint> fBiddingHistoryB){
		//calculate number of steps in each class
		double lMyDelta = 0, lMyDeltaPrev= 0;
		double lOpponentDelta = 0, lOpponentDeltaPrev = 0;
		AnalysisResult res = new AnalysisResult();
		//int lOpponentIndex;
		
		boolean agentA;
		for(int j=0;j<2;j++) {
			if(j==0) {
				agentA = true;
			} else {
				agentA = false;
			}
		ArrayList<BidPoint> fBiddingHistory =null;
		if(agentA) {
			fBiddingHistory = fBiddingHistoryA;
		} else {
			fBiddingHistory = fBiddingHistoryB;
		}
		
		for(int i=1;i<fBiddingHistory.size();i++) {
			
//			Agent lOpponent = fBiddingHistory.get(i-1).getAgent();
			
			BidPoint lMyBidPrev = fBiddingHistory.get(i-1);
			//BidPoint lOpponentBid = fBiddingHistory.get(i);			
			BidPoint lMyBid = fBiddingHistory.get(i);

			//my step			
			if(agentA) {
				lMyDelta = lMyBid.getUtilityA() - lMyBidPrev.getUtilityA();
			} else {
				lMyDelta = lMyBid.getUtilityB() - lMyBidPrev.getUtilityB();
			}
				
			if(agentA) {
				lOpponentDelta = lMyBid.getUtilityB() - lMyBidPrev.getUtilityB();
			} else {
				lOpponentDelta = lMyBid.getUtilityA() - lMyBidPrev.getUtilityA();				
			}
			
			STEP_CLASS lMyStepType = getMyStepClass(lMyDelta, lOpponentDelta);

			int lAgentIndex;
			//TODO: implement equals method for the agent			
			if(agentA) 
				lAgentIndex = 0;
			else 
				lAgentIndex = 1;
 
			switch(lMyStepType) {
			case CONCESSION: res.lConcessionCount[lAgentIndex]++; break;
			case FORTUNATE: res.lFortunateCount[lAgentIndex]++; break;
			case UNFORTUNATE: res.lUnfortunateCount[lAgentIndex]++; break;
			case SELFISH: res.lSelfishCount[lAgentIndex]++; break;
			case NICE: res.lNiceCount[lAgentIndex]++; break;
			case SILENT: res.lSilentCount[lAgentIndex]++;break;
			case UNKNOWN: res.lUnknownCount[lAgentIndex]++;
			}//switch
			//can start calculating the matching only after third bid
			if(i<3) continue;
			BidPoint lOpponentBidPrev = fBiddingHistory.get(i-3);

			//opponent's step
/*			lMyDeltaPrev = lMyBid.getAgent().getUtility(lOpponentBid.getBid())-
						   lMyBid.getAgent().getUtility(lOpponentBidPrev.getBid());

			lOpponentDeltaPrev = lOpponent.getUtility(lOpponentBid.getBid())-
								 lOpponent.getUtility(lOpponentBidPrev.getBid());
*/						
			STEP_CLASS lOpponentStepType = getOpponentStepClass(lMyDeltaPrev, lOpponentDeltaPrev);
			
//			//sensitivity of own preferences
//			switch(lOpponentStepType) {
//			case FORTUNATE:
//			case CONCESSION:
//			case NICE: 
//				if((lMyStepType == STEP_CLASS.CONCESSION)|| 
//				   (lMyStepType == STEP_CLASS.UNFORTUNATE)/*||
//				   (lStep == STEP_CLASS.SILENT)*/)
//					lOwnSensitivity[lAgentIndex]++;
//				break;
//			case SELFISH:
//			case UNFORTUNATE:					
//				if((lMyStepType == STEP_CLASS.FORTUNATE)||
//				   (lMyStepType == STEP_CLASS.SELFISH)/*||
//				   (lStep == STEP_CLASS.SILENT)*/)
//					lOwnSensitivity[lAgentIndex]++;
//				break;
///*			case SILENT:
//				if((lMyStepType == STEP_CLASS.NICE)||
//				   (lMyStepType == STEP_CLASS.SILENT))							
//					lOwnSensitivity[lAgentIndex ]++;*/
//			}
//			//sensitivity for opponent preferences
//			switch(lOpponentStepType) {		
//			case FORTUNATE:
//			case SELFISH:
//				if((lMyStepType == STEP_CLASS.SELFISH)||
//				   (lMyStepType == STEP_CLASS.UNFORTUNATE))
//					lOppSensitivity[lAgentIndex ]++;
//				break;
//			case CONCESSION:
//			case UNFORTUNATE:
//					if((lMyStepType == STEP_CLASS.FORTUNATE)||
//					   (lMyStepType == STEP_CLASS.CONCESSION)||
//					   (lMyStepType == STEP_CLASS.NICE)) {
//						lOppSensitivity[lAgentIndex]++;
//					}
//					break;
///*			case NICE:
//			case SILENT:
//				if(lMyStepType == STEP_CLASS.SILENT)
//					lOppSensitivity[lAgentIndex]++;						
//			}*/				
		}//for
		}
		return res;
	}
	private static STEP_CLASS getMyStepClass(double lMyDelta, double lOpponentDelta){
		STEP_CLASS lStep = STEP_CLASS.UNKNOWN;
		//check for a Silent step
		if((Math.abs(lMyDelta)<CONSTANT_K)&&(Math.abs(lOpponentDelta)<CONSTANT_K))
			lStep = STEP_CLASS.SILENT;
		else
			//check for a nice step
			if((Math.abs(lMyDelta)<CONSTANT_K)&&(lOpponentDelta>0))
				lStep = STEP_CLASS.NICE;
			else
				//check for a Fortunate step
				if((lMyDelta>0)&&(lOpponentDelta>0))
					lStep = STEP_CLASS.FORTUNATE;
				else
					//check for an unfortunate step
					if((lMyDelta<CONSTANT_K)&&(lOpponentDelta<0))
						lStep = STEP_CLASS.UNFORTUNATE;
					else
						//check for a selfish step
						if((lMyDelta>0)&&(lOpponentDelta<=0))
							lStep = STEP_CLASS.SELFISH;
						else
							//check for a concession
							if((lMyDelta<0)&&(lOpponentDelta>=0))
								lStep = STEP_CLASS.CONCESSION;
		return lStep;
	}
	private static STEP_CLASS getOpponentStepClass(double lMyDelta, double lOpponentDelta){
		STEP_CLASS lStep = STEP_CLASS.UNKNOWN;
		//check for a Silent step
		if((Math.abs(lMyDelta)<CONSTANT_K)&&(Math.abs(lOpponentDelta)<CONSTANT_K))
			lStep = STEP_CLASS.SILENT;
		else
			//check for a nice step
			if((lMyDelta>CONSTANT_K)&&(Math.abs(lOpponentDelta)<CONSTANT_K))
				lStep = STEP_CLASS.NICE;
			else
				//check for a Fortunate step
				if((lMyDelta>0)&&(lOpponentDelta>0))
					lStep = STEP_CLASS.FORTUNATE;
				else
					//check for an unfortunate step
					if((lMyDelta<CONSTANT_K)&&(lOpponentDelta<0))
						lStep = STEP_CLASS.UNFORTUNATE;
					else
						//check for a selfish step
						if((lMyDelta>0)&&(lOpponentDelta<=0))
							lStep = STEP_CLASS.CONCESSION;
						else
							//check for a concession
							if((lMyDelta<0)&&(lOpponentDelta>=0))
								lStep = STEP_CLASS.SELFISH;
		return lStep;
	}
	
}
