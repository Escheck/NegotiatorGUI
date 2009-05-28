package agents;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;

import negotiator.Agent;
import negotiator.Bid;
import negotiator.analysis.BidPoint;
import negotiator.BidIterator;
import negotiator.actions.*;
import agents.bayesianopponentmodel.*;
import negotiator.issue.Issue;
import negotiator.issue.Value;
import negotiator.issue.ValueReal;
import negotiator.tournament.VariablesAndValues.AgentParamValue;
import negotiator.tournament.VariablesAndValues.AgentParameterVariable;
import negotiator.utility.UtilitySpace;
import negotiator.xml.SimpleElement;
import negotiator.Domain;
import negotiator.analysis.BidSpace;
import negotiator.AgentParam;


/**
 * Wrapper for opponentmodelspace, so that it is a neat utilityspace that we can give to the bidspace. 
 * @author wouter
 *
 */

public class BayesianAgent extends Agent {

	private Action messageOpponent;
	private Bid myLastBid = null;
	private Action myLastAction = null;
	private Bid fOpponentPreviousBid = null;
	
	private static final double BREAK_OFF_POINT = 0.5;
	

	private enum ACTIONTYPE { START, OFFER, ACCEPT, BREAKOFF };
	private enum STRATEGY {SMART, SERIAL, RESPONSIVE, RANDOM, TIT_FOR_TAT};
	private STRATEGY fStrategy = STRATEGY.SMART;
	private int fSmartSteps;
	protected OpponentModel fOpponentModel;	
	private static final double CONCESSIONFACTOR = 0.04;
	private static final double ALLOWED_UTILITY_DEVIATION = 0.01;
	private static final int NUMBER_OF_SMART_STEPS = 0; 
	private ArrayList<Bid> myPreviousBids;
	private boolean fSkipDistanceCalc = false;
	private boolean fDebug = false;
	private int fRound;
	// Class constructor
	public BayesianAgent() {
		super();
	}
	

	public static String getVersion() { return "2.0"; }

	 /** Dummy variables, for testing only. W.Pasman 19aug08 */
	public static ArrayList<AgentParam> getParameters() { 
		ArrayList<AgentParam> parameters=new ArrayList<AgentParam>();
		parameters.add(new AgentParam<>(BayesianAgent.class.getName(),"risetime",0.,2.));
		parameters.add(new AgentParamInteger(BayesianAgent.class.getName(),"risetime",0.,2.));
		parameters.add(new AgentParam(BayesianAgent.class.getName(),"tau",1.,4.));
		parameters.add(new AgentParam(BayesianAgent.class.getName(),"epsilon",-2.,2.));
		parameters.add(new AgentParam(BayesianAgent.class.getName(),"beta",18.3,22.17));
		return parameters;
	}
	
	public void init() {
		messageOpponent = null;
		myLastBid = null;
		myLastAction = null;
		fSmartSteps = 0;
		myPreviousBids = new ArrayList<Bid>();
		prepareOpponentModel();			
		fRound =0;
	}
	protected void prepareOpponentModel() {
		fOpponentModel = new BayesianOpponentModel(utilitySpace);	
	}

	// Class methods
	public void ReceiveMessage(Action opponentAction) {
		messageOpponent = opponentAction;
	}

	private Action proposeInitialBid() throws Exception
	{
		Bid lBid=null;
		// Return (one of the) possible bid(s) with maximal utility.
		lBid = utilitySpace.getMaxUtilityBid();
		fSmartSteps=NUMBER_OF_SMART_STEPS;
		myLastBid = lBid;
		return new Offer(getAgentID(), lBid);
	}
	

	
	/**
	 * 
	 * @param pOppntBid
	 * @return a counterbid that has max util for us and an opponent utility that is equal
	 * to 1-estimated utility of opponent's last bid.
	 * Or, if that bid was done already before, another bid that has same utility in our space
	 * as that counterbid.
	 * @throws Exception
	 */
	private Bid getNextBid(Bid pOppntBid) throws Exception 
	{
		if (pOppntBid==null) throw new NullPointerException("pOpptBid=null");
		if (myLastBid==null) throw new Exception("myLastBid==null");
		log("Get next bid ...");
		
		
		BidSpace bs=new BidSpace(utilitySpace,new OpponentModelUtilSpace(fOpponentModel),true);
		//System.out.println("Bidspace:\n"+bs);
		
		 // compute opponent's concession
		double opponentConcession=0.;
		if(fOpponentPreviousBid==null) opponentConcession=0;
		else
		{		
			double opponentUtil = fOpponentModel.getNormalizedUtility(pOppntBid);
			double opponentFirstBidUtil=fOpponentModel.getNormalizedUtility(fOpponentModel.fBiddingHistory.get(0));
			opponentConcession=opponentUtil-opponentFirstBidUtil;
		}
		log("opponent Concession:"+opponentConcession);
		
		 // determine our bid point
		double OurFirstBidOppUtil=fOpponentModel.getNormalizedUtility(myPreviousBids.get(0));
		double OurTargetBidOppUtil=OurFirstBidOppUtil-opponentConcession;
		if (OurTargetBidOppUtil>1) OurTargetBidOppUtil=1.;
		if (OurTargetBidOppUtil<OurFirstBidOppUtil) OurTargetBidOppUtil=OurFirstBidOppUtil;
		log("our target opponent utility="+OurTargetBidOppUtil);
		
		// find the target on the pareto curve
		double targetUtil=bs.OurUtilityOnPareto(OurTargetBidOppUtil);
		//exclude only the last bid
		ArrayList<Bid> excludeBids = new ArrayList<Bid>();
		excludeBids.add(myPreviousBids.get(myPreviousBids.size()-1));
		BidPoint bp= bs.NearestBidPoint(targetUtil,OurTargetBidOppUtil,.5,1,myPreviousBids);
		log("found bid "+bp);
		return bp.bid;
	}
	
	/**
	 * get a new bid (not done before) that has ourUtility for us.
	 * 
	 * @param ourUtility
	 * @return  the bid with max opponent utility that is close to ourUtility.
	 * or null if there is no such bid.
	 */
	Bid getNewBidWithUtil(double ourUtility,BidSpace bs)
	{
		BidPoint bestbid=null;
		double bestbidutil=0;
		for (BidPoint p:bs.bidPoints)
		{
			if (Math.abs(ourUtility-p.utilityA)<ALLOWED_UTILITY_DEVIATION
					&& p.utilityB>bestbidutil && !myPreviousBids.contains(p.bid))
			{
				bestbid=p;
				bestbidutil=p.utilityB;
			}
		}
		if (bestbid==null) return null;
		return bestbid.bid;
	}
	
	
		/**
		 * Wouter: Try to find a bid that has same utility for ourself
		 * but max utility for opponent.
		 * @author Dmytro
		 * @param pBid
		 * @return
		 * @throws Exception
		 */
	private Bid getSmartBid(Bid pBid) throws Exception {
		Bid lBid=null;
		double lExpectedUtility = -1;
		double lUtility = utilitySpace.getUtility(pBid);
		BidIterator lIter = new BidIterator(utilitySpace.getDomain());
//		int i=1;
		while(lIter.hasNext()) {
			Bid tmpBid = lIter.next();
//			System.out.println(tmpBid);
//			System.out.println(String.valueOf(i++));
			if(Math.abs(utilitySpace.getUtility(tmpBid)-lUtility)<ALLOWED_UTILITY_DEVIATION) {
				//double lTmpSim = fSimilarity.getSimilarity(tmpBid, pOppntBid);
				double lTmpExpecteUtility = fOpponentModel.getNormalizedUtility(tmpBid);
				if(lTmpExpecteUtility > lExpectedUtility) {
					lExpectedUtility= lTmpExpecteUtility ;
					lBid = tmpBid;
				}
			}				
		} 		
		//check if really found a better bid. if not return null
		if(fOpponentModel.getNormalizedUtility(lBid)>(fOpponentModel.getNormalizedUtility(pBid)+0.04))
			return lBid;
		else
			return null;
	}
	
	
	/**
	 * Finds a Pareto efficient bid on the circle of radius pRadius with a center in pBid bid. 
	 * @author Dmytro Tykhonov, W.Pasman
	 * @param pBid - bid that defines the circle 
	 * @param pRadius - radius of the circle
	 * @return a Pareto efficient bid on the circle 
	 * @throws Exception
	 */
	private Bid getBidOfRadius(Bid pBid, double pRadius) throws Exception {
		Bid lBid=null;		
		BidIterator lIter = new BidIterator(utilitySpace.getDomain());
//		ArrayList<Bid> lCircle = new ArrayList<Bid>();
		double pBidOppU = fOpponentModel.getNormalizedUtility(pBid);
		double pBidMyU  = utilitySpace.getUtility(pBid);		
		while(lIter.hasNext()) {
			Bid tmpBid = lIter.next();
			double tmpBidOppU = fOpponentModel.getNormalizedUtility(tmpBid);
			double tmpBidMyU  = utilitySpace.getUtility(tmpBid);
			double lRSquare   = (tmpBidOppU-pBidOppU)*(tmpBidOppU-pBidOppU)+
					   		    (tmpBidMyU - pBidMyU)*(tmpBidMyU - pBidMyU);
			if(Math.abs(lRSquare-pRadius*pRadius)<sq(ALLOWED_UTILITY_DEVIATION)) {
//				lCircle.add(tmpBid);
				if(lBid==null) {
					lBid = tmpBid;
				} else {
					if((tmpBidMyU>utilitySpace.getUtility(lBid))&&
					   (tmpBidOppU>fOpponentModel.getNormalizedUtility(lBid))) 
					lBid = tmpBid;
				}
			}				
		} //while
		return lBid;
	}
	
	/** check the points in given bid list, and find point closest to given radius.
	 * You can use this function to check any list of bidpoints, either the (guessed) pareto frontier
	 * or just the entire bidspace. 
	 * @author W.Pasman
	 * @param bidpoints is the list of points to be checked.
	 * @param pBid
	 * @param targetRadius
	 * @return bid that is at distance pRadius from pBid as close as possible, but NOT equal to pBid 
	 * @throws Exception
	 */
	private Bid getBidAtDistance(ArrayList<BidPoint> bidpoints, Bid pBid, double targetRadius) throws Exception 
	{
		if (targetRadius<0) throw new Exception ("targetRadius<0");
		double targetRadius2=sq(targetRadius); // target radius squared.
		BidPoint nearestbid=null;			
		double smallestDeltaSoFar = 999.; // square of closest bid so far. any bid will be closer than this.
		
		double pBidOppU = fOpponentModel.getNormalizedUtility(pBid);
		double pBidMyU  = utilitySpace.getUtility(pBid);	
		
		double tmpBidOppU, tmpBidMyU, r2, delta;
		for(BidPoint b : bidpoints) {
			if (b.equals(pBid)) continue; // skip the pBid itself
			//tmpBidOppU = fOpponentModel.getNormalizedUtility(b);
			tmpBidOppU = b.utilityB;
			tmpBidMyU  = b.utilityA;
			r2 = sq(tmpBidOppU-pBidOppU)+sq(tmpBidMyU - pBidMyU);
			delta=Math.abs(r2-targetRadius2);
			if (delta<smallestDeltaSoFar)
				nearestbid = b;
				smallestDeltaSoFar = delta; 
			}				
		if (nearestbid==null) throw new Exception("bid space seems empty??");
		return nearestbid.bid;
	}
	
	private Bid getNextBidSmart(Bid pOppntBid) throws Exception 
	{
		double lMyUtility, lOppntUtility, lTargetUtility;
		// Both parties have made an initial bid. Compute associated utilities from my point of view.
		lMyUtility = utilitySpace.getUtility(myLastBid);
		lOppntUtility = utilitySpace.getUtility(pOppntBid);
		if(fSmartSteps>=NUMBER_OF_SMART_STEPS) {
			lTargetUtility = getTargetUtility(lMyUtility, lOppntUtility);
			fSmartSteps=0;
		} else {
			lTargetUtility = lMyUtility; 
			fSmartSteps++;
		}
		return getTradeOff(lTargetUtility, pOppntBid);
	}
	
	/**
	 * Search a bid in given space close to given utility.
	 * @param targetutil
	 * @param bids
	 * @return the bid having opponent utility as close as possible to pOppUtil
	 * @throws Exception
	 */
	private Bid getBidWithUtil(double targetutil, ArrayList<BidPoint> bids) throws Exception 
	{
		BidPoint lBid=null;
		double lBidOppU=-1;
		for(BidPoint b : bids) {
			double util = b.utilityB;
			if(Math.abs(util-targetutil)<Math.abs(lBidOppU-targetutil)) { 
				lBid = b;
				lBidOppU=util;
			}		
		}
		return lBid.bid;
	}
	
	/**
	 * Search for a bid with a given target utility in opponent space according to
	 * the learned model and maximal utility for my self.
	 * Uses ALLOWED_UTILITY_DEVIATION to allow some deviation from the target utility
	 * 
	 * Wouter: modified 20nov, as it appears that bid spaces in practice have gaps.
	 * Awaiting Dmytro's feedback on this, I propose to 
	 * 
	 * @param pOppUtil - target utility in opponent's space 
	 * @return bid with maximal utility in own space and the target utility in the opponent's space
	 * @throws Exception if there is no bid (should be rare!)
	 */
	private Bid getMaxBidForOppUtil(double pOppUtil) throws Exception 
	{
		Bid lBid=null;
		double lUtility = -1;
		BidIterator lIter = new BidIterator(utilitySpace.getDomain());
//		int i=1;
		while(lIter.hasNext()) {
			Bid tmpBid = lIter.next();
//			System.out.println(tmpBid);
//			System.out.println(String.valueOf(i++));
			if(Math.abs(fOpponentModel.getNormalizedUtility(tmpBid)-pOppUtil)<ALLOWED_UTILITY_DEVIATION) {
				//double lTmpSim = fSimilarity.getSimilarity(tmpBid, pOppntBid);
				double lTmpUtility = utilitySpace.getUtility(tmpBid);
				if(lTmpUtility > lUtility) {
					lUtility= lTmpUtility ;
					lBid = tmpBid;
				}
			}				
		} //while
		if (lBid==null) throw new Exception("there is no bid with opponent utility "+pOppUtil);
		return lBid;
		
	}
	private Bid getTradeOff(double pUtility, Bid pOppntBid) throws Exception
	{
		Bid lBid=null;
		double lExpectedUtility = -100;
		BidIterator lIter = new BidIterator(utilitySpace.getDomain());
//		int i=1;
		while(lIter.hasNext()) {
			Bid tmpBid = lIter.next();
//			System.out.println(tmpBid);
//			System.out.println(String.valueOf(i++));
			if(Math.abs(utilitySpace.getUtility(tmpBid)-pUtility)<ALLOWED_UTILITY_DEVIATION) {
				//double lTmpSim = fSimilarity.getSimilarity(tmpBid, pOppntBid);
				double lTmpExpecteUtility = fOpponentModel.getNormalizedUtility(tmpBid);
				if(lTmpExpecteUtility > lExpectedUtility) {
					lExpectedUtility= lTmpExpecteUtility ;
					lBid = tmpBid;
				}
			}				
		} //while
		return lBid;
	}
	
	
	private Bid proposeNextBid(Bid pOppntBid) throws Exception
	{
		Bid lBid = null;
		switch(fStrategy) {
		case TIT_FOR_TAT:
			lBid = getNextBid(pOppntBid);
			break;
		case SMART:
			lBid = getNextBidSmart(pOppntBid);
			break;
		default:
			throw new Exception("unknown strategy "+fStrategy);
		}
		myLastBid = lBid;
		return  lBid;
	}

	
	public Action chooseAction()
	{
		Action lAction = null;
		ACTIONTYPE lActionType;
		Bid lOppntBid = null;
		
		try	{
			lActionType = getActionType(messageOpponent);
			switch (lActionType) { 
			case OFFER: // Offer received from opponent
				lOppntBid = ((Offer) messageOpponent).getBid();
				//if (fOpponentModel.haveSeenBefore(lOppntBid)) { lAction=myLastAction; break; }
				//double lDistance = calculateEuclideanDistanceUtilitySpace();
				if(myLastAction==null) dumpDistancesToLog(0);
				System.out.print("Updating beliefs ...");
				if(myPreviousBids.size()<8)	fOpponentModel.updateBeliefs(lOppntBid);
				dumpDistancesToLog(fRound++);
				System.out.println("Done!");
				if (myLastAction == null)
					// Other agent started, lets propose my initial bid.
					lAction = proposeInitialBid();
				else {
	                double offeredutil=utilitySpace.getUtility(lOppntBid);
	                double time=((new Date()).getTime()-startTime.getTime())/(1000.*totalTime);
	                double P=Paccept(offeredutil,time);
	                log("time="+time+" offeredutil="+offeredutil+" accept probability P="+P);
	               if (utilitySpace.getUtility(lOppntBid)*1.03 >= utilitySpace.getUtility(myLastBid)
	            	/*|| .05*P>Math.random()*/ )	   
	               {
						// Opponent bids equally, or outbids my previous bid, so lets accept
	                	lAction = new Accept(getAgentID());
	                	log("randomly accepted");
	                }
	                else {
	                	Bid lnextBid = proposeNextBid(lOppntBid);
	                	lAction=new Offer(getAgentID(),lnextBid);
	                	// Propose counteroffer. Get next bid.
	                	// Check if utility of the new bid is lower than utility of the opponent's last bid
	                	// if yes then accept last bid of the opponent.
	                	if (utilitySpace.getUtility(lOppntBid)*1.03 >= utilitySpace.getUtility(lnextBid))
	                	{
	                		// Opponent bids equally, or outbids my previous bid, so lets  accept
	                		lAction = new Accept(getAgentID());
	                		log("opponent's bid higher than util of my last bid! accepted");
	                	} 

	                }
	                //remember current bid of the opponent as its previous bid
	                fOpponentPreviousBid = lOppntBid;
				}
				break;
			case ACCEPT:
			case BREAKOFF:
				// nothing left to do. Negotiation ended, which should be checked by
				// Negotiator...
				break;
			default:
				// I am starting, but not sure whether Negotiator checks this, so
				// lets check also myLastAction...
				if (myLastAction == null) {
					dumpDistancesToLog(fRound++);				
					lAction = proposeInitialBid();
				} else
					// simply repeat last action
					lAction = myLastAction;
			break;
			}
		}
		catch (Exception e)
		{ 
			log("Exception in chooseAction:"+e.getMessage());
			e.printStackTrace();
			lAction = new Offer(getAgentID(), myLastBid);
		}
		myLastAction = lAction;
		if (myLastAction instanceof Offer)
			myPreviousBids.add( ((Offer)myLastAction).getBid());
		return lAction;
	}
	private ACTIONTYPE getActionType(Action lAction) {
		ACTIONTYPE lActionType = ACTIONTYPE.START;
		if (lAction instanceof Offer)
			lActionType = ACTIONTYPE.OFFER;
		else if (lAction instanceof Accept)
			lActionType = ACTIONTYPE.ACCEPT;
		else if (lAction instanceof EndNegotiation)
			lActionType = ACTIONTYPE.BREAKOFF;
		return lActionType;
	}
	private double getTargetUtility(double myUtility, double oppntUtility) {
		return myUtility -getConcessionFactor();
	}
	private double getConcessionFactor() {
		// The more the agent is willing to concess on its aspiration value, the
		// higher this factor.
		return CONCESSIONFACTOR;
	}
	
	/**
	 * Prints out debug information only if the fDebug = true
	 * @param pMessage - debug informaton to print
	 */
	private void log(String pMessage) {
		if(fDebug) 
			System.out.println(pMessage);
	}

	/**
	 * This function determines the accept probability for an offer.
	 * At t=0 it will prefer high-utility offers.
	 * As t gets closer to 1, it will accept lower utility offers with increasing probability.
	 * it will never accept offers with utility 0.
	 * @param u is the utility 
	 * @param t is the time as fraction of the total available time 
	 * (t=0 at start, and t=1 at end time)
	 * @return the probability of an accept at time t
	 * @throws Exception if you use wrong values for u or t.
	 * 
	 */
	private double Paccept(double u, double t1) throws Exception
	{
		double t=t1*t1*t1; // get more relaxed more to the end.
		if (u<0 || u>1.05) throw new Exception("utility "+u+" outside [0,1]");
		if (t<0 || t>1) throw new Exception("time "+t+" outside [0,1]");
		if (u>1.) u=1.;
		
		if (t==0.5) return u;
		return (u - 2.*u*t + 2.*(-1. + t + Math.sqrt(sq(-1. + t) + u*(-1. + 2*t))))/(-1. + 2*t);
	}
	
	private double sq(double x) { return x*x; }
	
	private double calculateEuclideanDistanceUtilitySpace(double[] pLearnedUtil, double[] pOpponentUtil) {		
		double lDistance = 0;
		try {
			for(int i=0;i<pLearnedUtil.length;i++)
				lDistance = lDistance + sq( pOpponentUtil[i]-pLearnedUtil[i]);
		} catch (Exception e) {				
			e.printStackTrace();
		}
		lDistance = lDistance / utilitySpace.getDomain().getNumberOfPossibleBids();
		return lDistance;
	}	
	private double calculateEuclideanDistanceWeghts(double[] pExpectedWeight) {
		double lDistance = 0;
		int i=0;
		try {
			for(Issue lIssue : utilitySpace.getDomain().getIssues()) {
				lDistance = lDistance +sq(fNegotiation.getOpponentWeight(this, lIssue.getNumber()) -pExpectedWeight[i]);
				i++;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return lDistance/(double)i;
	}
	
	private double calculatePearsonDistanceUtilitySpace(double[] pLearnedUtility, double[] pOpponentUtil) {
		double lDistance = 0;
		double lAverageLearnedUtil=0;
		double lAverageOriginalUtil=0;
		//calculate average values
		for(int i=0;i<pLearnedUtility.length;i++) {
				lAverageLearnedUtil = lAverageLearnedUtil + pLearnedUtility[i];
				lAverageOriginalUtil = lAverageOriginalUtil + pOpponentUtil[i];
		}
		lAverageLearnedUtil = lAverageLearnedUtil/(double)(utilitySpace.getDomain().getNumberOfPossibleBids());
		lAverageOriginalUtil = lAverageOriginalUtil/ (double)(utilitySpace.getDomain().getNumberOfPossibleBids());
		//calculate the distance itself
		double lSumX=0;
		double lSumY=0;
		for(int i=0;i<pLearnedUtility.length;i++) { 
				lDistance = lDistance + (pLearnedUtility[i]-lAverageLearnedUtil)*
										(pOpponentUtil[i]-lAverageOriginalUtil);
				lSumX = lSumX + sq(pLearnedUtility[i]-lAverageLearnedUtil);
				lSumY = lSumY + sq(pOpponentUtil[i]-lAverageOriginalUtil);
				
		}

		
		return  lDistance/(Math.sqrt(lSumX*lSumY));
	}
	
	private double calculatePearsonDistanceWeghts(double[] pExpectedWeight) {
		double lDistance = 0;
		double lAverageLearnedWeight=0;
		double lAverageOriginalWeight=0;
		int i=0;
		try {
			for(Issue lIssue : utilitySpace.getDomain().getIssues()) {
				lAverageLearnedWeight = lAverageLearnedWeight +pExpectedWeight[i];
				lAverageOriginalWeight = lAverageOriginalWeight + fNegotiation.getOpponentWeight(this, lIssue.getNumber());
				i++;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		lAverageLearnedWeight = lAverageLearnedWeight/(double)(i);
		lAverageOriginalWeight= lAverageOriginalWeight/ (double)(i);
		
		//calculate the distance itself
		i=0;
		double lSumX=0;
		double lSumY=0;
		try {
			for(Issue lIssue : utilitySpace.getDomain().getIssues()) {
				lDistance = lDistance +(fNegotiation.getOpponentWeight(this, lIssue.getNumber())- lAverageOriginalWeight)*(pExpectedWeight[i]-lAverageLearnedWeight);
				lSumX = lSumX + sq(fNegotiation.getOpponentWeight(this, lIssue.getNumber())- lAverageOriginalWeight);
				lSumY = lSumY + sq(pExpectedWeight[i]-lAverageLearnedWeight);
				i++;
			}		
		} catch (Exception e) {
			e.printStackTrace();
		}
			
		return lDistance/(Math.sqrt(lSumX*lSumY));
	}
	private double calculateRankingDistanceUtilitySpaceMonteCarlo(double[] pLearnedUtil, double[] pOpponentUtil) {
		double lDistance = 0;
		int lNumberOfPossibleBids = (int)(utilitySpace.getDomain().getNumberOfPossibleBids());
		int lNumberOfComparisons = 10000;
		for(int k=0;k<lNumberOfComparisons ;k++) {
			int i = (new Random()).nextInt(lNumberOfPossibleBids-1);
			int j = (new Random()).nextInt(lNumberOfPossibleBids-1);
			if(((pLearnedUtil[i]>pLearnedUtil[j])&&(pOpponentUtil[i]>pOpponentUtil[j]))||
			   ((pLearnedUtil[i]<pLearnedUtil[j])&&(pOpponentUtil[i]<pOpponentUtil[j]))||
			   ((pLearnedUtil[i]==pLearnedUtil[j])&&(pOpponentUtil[i]==pOpponentUtil[j]))) {
					
					} else
						lDistance++;
			
		}
		return ((double)lDistance)/((double)lNumberOfComparisons);
	}
	private double calculateRankingDistanceUtilitySpace(double[] pLearnedUtil, double[] pOpponentUtil) {

		double lDistance = 0;
		int lNumberOfPossibleBids = (int)(utilitySpace.getDomain().getNumberOfPossibleBids()); 

		try {		
			for(int i=0;i<lNumberOfPossibleBids-1;i++) {
				for(int j=i+1;j<lNumberOfPossibleBids;j++) {
					//if(i==j) continue;
					if (Math.signum(pLearnedUtil[i]-pLearnedUtil[j])!=Math.signum(pOpponentUtil[i]-pOpponentUtil[j]))
						lDistance++;
					
				} //for
			} //for
		} catch (Exception e) {				
			e.printStackTrace();
		}
		
		lDistance = 2 * lDistance / (utilitySpace.getDomain().getNumberOfPossibleBids()*(utilitySpace.getDomain().getNumberOfPossibleBids()));
		return lDistance;
	}
	private double calculateRankingDistanceWeghts(double pExpectedWeights[]) {
		double lDistance = 0;
		double[] lOriginalWeights = new double[utilitySpace.getDomain().getIssues().size()];
		int k=0;
		try {
			for(Issue lIssue : utilitySpace.getDomain().getIssues()) {
				lOriginalWeights[k] = fNegotiation.getOpponentWeight(this, lIssue.getNumber());
				k++;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		k=0;
		int nrOfIssues = utilitySpace.getDomain().getIssues().size();
		for(int i=0; i<nrOfIssues-1;i++) {			
			for(int j=i+1;j<nrOfIssues;j++) {
				k++;
				double tmpWeightLearned = pExpectedWeights[i];
				double tmpWeightOriginal = lOriginalWeights[i];
				double tmpWeight2Learned = pExpectedWeights[j];
				double tmpWeight2Original = lOriginalWeights[j];
				if(((tmpWeightLearned>tmpWeight2Learned)&&(tmpWeightOriginal>tmpWeight2Original))||
				   ((tmpWeightLearned<tmpWeight2Learned)&&(tmpWeightOriginal<tmpWeight2Original))||
				   ((tmpWeightLearned==tmpWeight2Learned)&&(tmpWeightOriginal==tmpWeight2Original)))
				{
					
				} else
							lDistance++;

			}			
		}		
		return ((double)lDistance)/((double)k);
	}
	
	protected void dumpDistancesToLog(int pRound) {
		if (fSkipDistanceCalc) return;	
		System.out.print(getName() + ": calculating distance between the learned space and the original one ...");
		
		double lExpectedWeights[] = new double[utilitySpace.getDomain().getIssues().size()];
		int i=0;
		for(Issue lIssue : utilitySpace.getDomain().getIssues()) {
			lExpectedWeights[i]=fOpponentModel.getExpectedWeight(i);
			i++;
		}	

		
		double pLearnedUtil[] = new double[(int)(utilitySpace.getDomain().getNumberOfPossibleBids())];
//		HashMap<Bid, Double> pLearnedSpace = new HashMap<Bid, Double>();
		BidIterator lIter = new BidIterator( utilitySpace.getDomain());
		i=0;
		while(lIter.hasNext()) {
			Bid lBid = lIter.next();
			try {
				pLearnedUtil[i] =fOpponentModel.getNormalizedUtility(lBid);
//				pLearnedSpace.put(lBid, new Double(pLearnedUtil[i]));
				 
			} catch (Exception e) {
				e.printStackTrace();
			}
			i++;
		}
		double pOpponentUtil[] = new double[(int)(utilitySpace.getDomain().getNumberOfPossibleBids())];
//		HashMap<Bid, Double> pOpponentSpace = new HashMap<Bid, Double>();
		lIter = new BidIterator( utilitySpace.getDomain());
		i=0;
		while(lIter.hasNext()) {
			Bid lBid = lIter.next();
			try {
				pOpponentUtil[i] = fNegotiation.getOpponentUtility(this, lBid);
//				pOpponentSpace.put(lBid, new Double(pOpponentUtil[i]));
			} catch (Exception e) {
				e.printStackTrace();
			}
			i++;
		}
		
		double lEuclideanDistUtil 		= calculateEuclideanDistanceUtilitySpace(pLearnedUtil,pOpponentUtil);
		double lEuclideanDistWeights 	= calculateEuclideanDistanceWeghts(lExpectedWeights);
		double lRankingDistUtil 		= 0;
		if((int)(utilitySpace.getDomain().getNumberOfPossibleBids())>100000) 
			lRankingDistUtil = calculateRankingDistanceUtilitySpaceMonteCarlo(pLearnedUtil, pOpponentUtil);
		else 
			lRankingDistUtil = calculateRankingDistanceUtilitySpace(pLearnedUtil, pOpponentUtil);
		double lRankingDistWeights 		= calculateRankingDistanceWeghts(lExpectedWeights);
		double lPearsonDistUtil			= calculatePearsonDistanceUtilitySpace(pLearnedUtil,pOpponentUtil);
		double lPearsonDistWeights		= calculatePearsonDistanceWeghts(lExpectedWeights);
		SimpleElement lLearningPerformance = new SimpleElement("learning_performance");
		lLearningPerformance.setAttribute("round", String.valueOf(pRound));
		lLearningPerformance.setAttribute("agent", getName());
		lLearningPerformance.setAttribute("euclidean_distance_utility_space", String.valueOf(lEuclideanDistUtil));
		lLearningPerformance.setAttribute("euclidean_distance_weights", String.valueOf(lEuclideanDistWeights));
		lLearningPerformance.setAttribute("ranking_distance_utility_space", String.valueOf(lRankingDistUtil));
		lLearningPerformance.setAttribute("ranking_distance_weights", String.valueOf(lRankingDistWeights));
		lLearningPerformance.setAttribute("pearson_distance_utility_space", String.valueOf(lPearsonDistUtil));
		lLearningPerformance.setAttribute("pearson_distance_weights", String.valueOf(lPearsonDistWeights));
		System.out.println("Done!");
		System.out.println(lLearningPerformance.toString());
		fNegotiation.addAdditionalLog(lLearningPerformance);
		
	}


	@Override
	public void cleanUp() {
		// TODO Auto-generated method stub
		super.cleanUp();
		fOpponentModel = null;
		myPreviousBids = null;
	}
}
