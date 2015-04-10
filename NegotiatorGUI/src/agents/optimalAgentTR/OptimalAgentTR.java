package agents.optimalAgentTR;


import negotiator.Bid;
import negotiator.DiscreteTimeline;
import negotiator.tournament.TournamentConfiguration;
import negotiator.utility.UtilitySpace;
import agents.anac.y2011.Nice_Tit_for_Tat.BilateralAgent;
import agents.optimalAgentTR.collegePackage.College;
import agents.optimalAgentTR.collegePackage.Colleges;
import agents.optimalAgentTR.collegePackage.SimultaneousSearch;


public class OptimalAgentTR extends BilateralAgent{

	private EstimatedOutcomeSpace estimatedSpace;
	private Colleges candidateColleges;
	
	
	@Override
	public void init() {
		super.init();
		initSetup();		
	}
	
	private void initSetup(){
		
		if (TournamentConfiguration.getBooleanOption("accessPartnerPreferences", false)) {
			UtilitySpace opponentUtilitySpace = this.fNegotiation.getAgentAUtilitySpace();
			if (this.utilitySpace.getFileName().equals(opponentUtilitySpace.getFileName())) {
				opponentUtilitySpace = this.fNegotiation.getAgentBUtilitySpace();
			}
			
			estimatedSpace=new EstimatedOutcomeSpace(utilitySpace, opponentUtilitySpace);
		} else  //If we do not have access the oppponent's preferences
			estimatedSpace=new EstimatedOutcomeSpace(utilitySpace);	
	
	
		
	}
	

	private Bid makeCandidateBidSpace(){
		Colleges myCollegeList=new Colleges ();
		int index=0;
		
		//create college list
		for (BidInfo bid: estimatedSpace.getEstimatedOutcomeSpace())
		{
			myCollegeList.add(new College(bid.getMyUtil(), bid.getOpponentUtil(), index++));
		}
		
		SimultaneousSearch newSearch=new SimultaneousSearch(myCollegeList);
		System.out.println("Round :"+ (((DiscreteTimeline)this.timeline).getOwnRoundsLeft()+1));
		newSearch.setDeadlineCost(((DiscreteTimeline)this.timeline).getOwnRoundsLeft()+1);  
		candidateColleges=newSearch.computeSigmaStar();
		candidateColleges.sort();		
		
		System.out.println("SigmaStar returned " + candidateColleges);
		
		if (candidateColleges.get(0)!=null)	{	
			
			BidInfo bidInfo = this.estimatedSpace.getBidInfo(candidateColleges.get(0).getIndex());
			System.out.println("Bid:"+bidInfo.getBid()+" my util:"+bidInfo.getMyUtil());
			return bidInfo.getBid();	
		}else
			return null;
	}

	@Override
	public boolean isAcceptable(Bid plannedBid) {
		Bid opponentLastBid = getOpponentLastBid();
		// is acnext(1, 0);
		if (getUndiscountedUtility(opponentLastBid) >= getUndiscountedUtility(plannedBid))
			return true;
		return false;
	}
	
	@Override
	public Bid chooseCounterBid() {
		return makeCandidateBidSpace();
	}

	@Override
	public Bid chooseOpeningBid() {
		
		return makeCandidateBidSpace();
		
	}

	@Override
	public Bid chooseFirstCounterBid() {
		return makeCandidateBidSpace();
	}

}
