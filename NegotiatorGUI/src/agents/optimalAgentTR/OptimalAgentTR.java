package agents.optimalAgentTR;


import negotiator.Bid;
import negotiator.DiscreteTimeline;
import negotiator.boaframework.NegotiationSession;
import negotiator.boaframework.SessionData;
import negotiator.boaframework.opponentmodel.CUHKFrequencyModelV2;
import negotiator.boaframework.opponentmodel.OppositeModel;
import negotiator.boaframework.opponentmodel.PerfectModel;
import negotiator.tournament.TournamentConfiguration;
import negotiator.utility.UtilitySpace;
import agents.anac.y2010.AgentFSEGA.OpponentModel;
import agents.anac.y2011.Nice_Tit_for_Tat.BilateralAgent;
import agents.anac.y2012.CUHKAgent.OpponentBidHistory;
import agents.anac.y2013.MetaAgent.portfolio.thenegotiatorreloaded.UtilitySpaceAdapter;
import agents.optimalAgentTR.collegePackage.College;
import agents.optimalAgentTR.collegePackage.Colleges;
import agents.optimalAgentTR.collegePackage.SimultaneousSearch;


public class OptimalAgentTR extends BilateralAgent{

	private EstimatedOutcomeSpace estimatedSpace;
	private negotiator.boaframework.OpponentModel opponentModel;
	private double mylastBidUtilityForOpponent,opponentLastBidUtility;
	
	
	@Override
	public void init() {
		super.init();
		initSetup();		
	}
	
	private void initSetup(){
		
		
		
		if (TournamentConfiguration.getBooleanOption("accessPartnerPreferences", false)) 
			opponentModel=new PerfectModel();
		else 
			opponentModel=new OppositeModel();
		
		try {
			opponentModel.init(new NegotiationSession(new SessionData(), utilitySpace, timeline), null);
			opponentModel.setOpponentUtilitySpace(fNegotiation);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		estimatedSpace=new EstimatedOutcomeSpace(utilitySpace,opponentModel.getOpponentUtilitySpace());
		
		Bid randomBid = utilitySpace.getDomain().getRandomBid();
		try {
			System.out.println(utilitySpace.getUtility(randomBid));
			System.out.println(opponentModel.getOpponentUtilitySpace().getUtility(randomBid));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
		mylastBidUtilityForOpponent=0.0;
		opponentLastBidUtility=1.0;
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
		//System.out.println("Round :"+ (((DiscreteTimeline)this.timeline).getOwnRoundsLeft()+1));
		newSearch.setDeadlineCost(((DiscreteTimeline)this.timeline).getOwnRoundsLeft()+1);  
		Colleges candidateColleges= candidateColleges=newSearch.computeSigmaStar();
		candidateColleges.sort();		
		
		//System.out.println("SigmaStar returned " + candidateColleges);
		
		if (candidateColleges.get(0)!=null)	{	
			
			BidInfo bidInfo = this.estimatedSpace.getBidInfo(candidateColleges.get(0).getIndex());
			System.out.println("Bid:"+bidInfo.getBid()+" my util:"+bidInfo.getMyUtil());
			try {
				mylastBidUtilityForOpponent=opponentModel.getOpponentUtilitySpace().getUtility(bidInfo.getBid());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return bidInfo.getBid();	
		}else
			return null;
	}

	@Override
	public boolean isAcceptable(Bid plannedBid)  {
		Bid opponentLastBid = getOpponentLastBid();
		opponentModel.updateModel(opponentLastBid, timeline.getTime()); // update the opponent model after receiving opponent's last bid
		try {
			opponentLastBidUtility=opponentModel.getOpponentUtilitySpace().getUtility(opponentLastBid);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} // update opponent's last bid utility 
		// is acnext(1, 0);
		if (getUndiscountedUtility(opponentLastBid) >= getUndiscountedUtility(plannedBid))
			return true;
		return false;
	}
	
	@Override
	public Bid chooseCounterBid() {
		//Update the estimated outcome space
		estimatedSpace=new EstimatedOutcomeSpace(utilitySpace, opponentModel.getOpponentUtilitySpace(),mylastBidUtilityForOpponent,opponentLastBidUtility);
		return makeCandidateBidSpace();
	}

	@Override
	public Bid chooseOpeningBid() {
		
		return makeCandidateBidSpace();
		
	}

	@Override
	public Bid chooseFirstCounterBid() {
		//Update the estimated outcome space
		estimatedSpace=new EstimatedOutcomeSpace(utilitySpace, opponentModel.getOpponentUtilitySpace(),mylastBidUtilityForOpponent,opponentLastBidUtility);
		return makeCandidateBidSpace();
	}

}
