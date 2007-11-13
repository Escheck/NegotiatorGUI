package agents;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

import negotiator.Agent;
import negotiator.Bid;
import negotiator.BidIterator;
import negotiator.Main;
import negotiator.actions.Accept;
import negotiator.actions.Action;
import negotiator.actions.EndNegotiation;
import negotiator.actions.Offer;
import agents.bayesianopponentmodel.*;
import negotiator.issue.Value;
import negotiator.issue.ValueReal;
import negotiator.utility.UtilitySpace;
import negotiator.xml.SimpleElement;
import negotiator.Domain;


public class BayesianAgent extends Agent {

	private Action messageOpponent;
	//private int nrOfIssues;
	private Bid myLastBid = null;
	private Action myLastAction = null;
	private Bid fOpponentPreviousBid = null;
//	private Similarity fSimilarity;
	private static final double BREAK_OFF_POINT = 0.5;
	private enum ACTIONTYPE { START, OFFER, ACCEPT, BREAKOFF };
	private enum STRATEGY {SMART, SERIAL, RESPONSIVE, RANDOM};
	private STRATEGY fStrategy = STRATEGY.SMART;
	private int fSmartSteps;
	private BayesianOpponentModel4 fOpponentModel;	
	private static final double CONCESSIONFACTOR = 0.03;
	private static final double ALLOWED_UTILITY_DEVIATION = 0.05;
	private static final int NUMBER_OF_SMART_STEPS = 2; 
	
	private boolean fDebug = true;
	// Class constructor
	public BayesianAgent() {
		super();
	}

	public void init(int sessionNumber, int sessionTotalNumber, Date startTimeP, 
			Integer totalTimeP, UtilitySpace us) {
		super.init(sessionNumber, sessionTotalNumber, startTimeP,totalTimeP,us);
		messageOpponent = null;
		myLastBid = null;
		myLastAction = null;
		fSmartSteps = 0;
		fOpponentModel = new BayesianOpponentModel4(utilitySpace);
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
		return new Offer(this, lBid);
	}
	/**
	 * 
	 * @param pOppntBid
	 * @return
	 * @throws Exception
	 */
	private Bid getNextBid(Bid pOppntBid) throws Exception 
	{
		log("Get next bid ...");
		Bid lNextBid = null;
		//
		if(fOpponentPreviousBid==null) {
			try {
				log("My second bid. No second bid of opponent is available.");
				lNextBid = getBidOfRadius(myLastBid, 0.1);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			//try to match opponent's concession in his space
			log("Try to match opponent's concession in his space:");
			double lDeltaOppUtil = fOpponentModel.getExpectedUtility(pOppntBid)-
								   fOpponentModel.getExpectedUtility(fOpponentPreviousBid);
			double lTargetUtilInOpponentSpace = fOpponentModel.getExpectedUtility(myLastBid) - lDeltaOppUtil;
			log("Opp's change in utility:" + String.format("%1.5f", lDeltaOppUtil));
			log("My target utility in opp's utility space:" + String.format("%1.5f", lTargetUtilInOpponentSpace));
			//find a bid around lTargetUtilInOpponentSpace that maximizes my utility
			try {
				ArrayList<Bid> lPareto = buildParetoFrontier();				
				lNextBid = getMaxBidForOppUtilUsingPareto(lTargetUtilInOpponentSpace, lPareto);
				if(utilitySpace.getUtility(lNextBid)<utilitySpace.getUtility(myLastBid)) {
					//try to make a smart move
					log("Try to make a smart move");
					lNextBid = getSmartBid(myLastBid);
					if(lNextBid==null) {
						// if not possible make a concession using a circle of lDeltaOppUtil radius
						log("Smart move is impossible. Make a concession using a circle of radius " +  String.format("%1.5f", lDeltaOppUtil));
						// build Pareto Frontier

						lNextBid = getBidOfRadiusOnFrontier(lPareto, myLastBid, Math.abs(lDeltaOppUtil));
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return lNextBid;
	}
	
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
				double lTmpExpecteUtility = fOpponentModel.getExpectedUtility(tmpBid);
				if(lTmpExpecteUtility > lExpectedUtility) {
					lExpectedUtility= lTmpExpecteUtility ;
					lBid = tmpBid;
				}
			}				
		} //while
		//check if really found a better bid. if not return null
		if(fOpponentModel.getExpectedUtility(lBid)>(fOpponentModel.getExpectedUtility(pBid)+0.04))
			return lBid;
		else
			return null;
		
	}
	/**
	 * Finds a Pareto efficient bid on the circle of radius pRadius with a center in pBid bid. 
	 * @param pBid - bid that defines the circle 
	 * @param pRadius - radius of the circle
	 * @return a Pareto efficient bid on the circle 
	 * @throws Exception
	 */
	private Bid getBidOfRadius(Bid pBid, double pRadius) throws Exception {
		Bid lBid=null;		
		BidIterator lIter = new BidIterator(utilitySpace.getDomain());
//		ArrayList<Bid> lCircle = new ArrayList<Bid>();
		double pBidOppU = fOpponentModel.getExpectedUtility(pBid);
		double pBidMyU  = utilitySpace.getUtility(pBid);		
		while(lIter.hasNext()) {
			Bid tmpBid = lIter.next();
			double tmpBidOppU = fOpponentModel.getExpectedUtility(tmpBid);
			double tmpBidMyU  = utilitySpace.getUtility(tmpBid);
			double lRSquare   = (tmpBidOppU-pBidOppU)*(tmpBidOppU-pBidOppU)+
					   		    (tmpBidMyU - pBidMyU)*(tmpBidMyU - pBidMyU);
			if(Math.abs(lRSquare-pRadius*pRadius)<ALLOWED_UTILITY_DEVIATION) {
//				lCircle.add(tmpBid);
				if(lBid==null) {
					lBid = tmpBid;
				} else {
					if((tmpBidMyU>utilitySpace.getUtility(lBid))&&
					   (tmpBidOppU>fOpponentModel.getExpectedUtility(lBid))) 
					lBid = tmpBid;
				}
			}				
		} //while
		return lBid;
	}
	private Bid getBidOfRadiusOnFrontier(ArrayList<Bid> pFrontier, Bid pBid, double pRadius) throws Exception {
		Bid lBid=null;				
//		ArrayList<Bid> lCircle = new ArrayList<Bid>();
		double pBidOppU = fOpponentModel.getExpectedUtility(pBid);
		double pBidMyU  = utilitySpace.getUtility(pBid);		
		double lRadius = -1;
		for(Bid tmpBid : pFrontier) {
			double tmpBidOppU = fOpponentModel.getExpectedUtility(tmpBid);
			double tmpBidMyU  = utilitySpace.getUtility(tmpBid);
			double lRSquare   = (tmpBidOppU-pBidOppU)*(tmpBidOppU-pBidOppU)+
					   		    (tmpBidMyU - pBidMyU)*(tmpBidMyU - pBidMyU);
			if(lBid==null) {
				lBid = tmpBid;
				lRadius = Math.sqrt(lRSquare); 
			} else {
				if((tmpBidMyU<pBidMyU)&&(Math.abs(lRadius-pRadius)>Math.abs(Math.sqrt(lRSquare)-pRadius))) { 
					lBid = tmpBid;
					lRadius = Math.sqrt(lRSquare);
				}
			}
				
		} //while
		return lBid;
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
	private Bid getMaxBidForOppUtilUsingPareto(double pOppUtil, ArrayList<Bid> pFrontier) throws Exception 
	{
		Bid lBid=null;
		double lBidOppU=-1;
//		ArrayList<Bid> lCircle = new ArrayList<Bid>();
		for(Bid tmpBid : pFrontier) {
			double tmpBidOppU = fOpponentModel.getExpectedUtility(tmpBid);
			if(Math.abs(tmpBidOppU-pOppUtil)<Math.abs(lBidOppU-pOppUtil)) { 
				lBid = tmpBid;
				lBidOppU=tmpBidOppU;
			}		
		} //while
		return lBid;
		
	}
	
	/**
	 * Search for a bid with a given target utility in opponent space according to
	 * the learned model and maximal utility for my self.
	 * Uses ALLOWED_UTILITY_DEVIATION to allow some deviation from the target utility
	 * 
	 * @param pOppUtil - target utility in opponent's space 
	 * @return bid with maximal utility in own space and the target utility in the opponent's space
	 * @throws Exception
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
			if(Math.abs(fOpponentModel.getExpectedUtility(tmpBid)-pOppUtil)<ALLOWED_UTILITY_DEVIATION) {
				//double lTmpSim = fSimilarity.getSimilarity(tmpBid, pOppntBid);
				double lTmpUtility = utilitySpace.getUtility(tmpBid);
				if(lTmpUtility > lUtility) {
					lUtility= lTmpUtility ;
					lBid = tmpBid;
				}
			}				
		} //while
		return lBid;
		
	}
	private Bid getTradeOff(double pUtility, Bid pOppntBid) throws Exception
	{
		Bid lBid=null;
		double lExpectedUtility = -1;
		BidIterator lIter = new BidIterator(utilitySpace.getDomain());
//		int i=1;
		while(lIter.hasNext()) {
			Bid tmpBid = lIter.next();
//			System.out.println(tmpBid);
//			System.out.println(String.valueOf(i++));
			if(Math.abs(utilitySpace.getUtility(tmpBid)-pUtility)<ALLOWED_UTILITY_DEVIATION) {
				//double lTmpSim = fSimilarity.getSimilarity(tmpBid, pOppntBid);
				double lTmpExpecteUtility = fOpponentModel.getExpectedUtility(tmpBid);
				if(lTmpExpecteUtility > lExpectedUtility) {
					lExpectedUtility= lTmpExpecteUtility ;
					lBid = tmpBid;
				}
			}				
		} //while
		return lBid;
	}
	private Action proposeNextBid(Bid pOppntBid) throws Exception
	{
		Bid lBid = null;
		switch(fStrategy) {
		case SMART:
			lBid = getNextBid(pOppntBid);
			break;
		}
		myLastBid = lBid;
		return new Offer(this, lBid);
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
				fOpponentModel.updateBeliefs(lOppntBid);
				if (myLastAction == null)
					// Other agent started, lets propose my initial bid.
					lAction = proposeInitialBid();
				else {
/*	                double offeredutil=utilitySpace.getUtility(lOppntBid);
	                double time=((new Date()).getTime()-startTime.getTime())/(1000.*totalTime);
	                double P=Paccept(offeredutil,time);
	                if (P>Math.random()) 				*/	
					if (utilitySpace.getUtility(lOppntBid)*1.05 >= utilitySpace
						.getUtility(myLastBid))
					// Opponent bids equally, or outbids my previous bid, so lets
					// accept
	                	lAction = new Accept(this);
	                else {
	                	// Propose counteroffer. Get next bid.
	                	lAction = proposeNextBid(lOppntBid);
	                	// Check if utility of the new bid is lower than utility of the opponent's last bid
	                	// if yes then accept last bid of the opponent.
	                	if (utilitySpace.getUtility(lOppntBid)*1.05 >= utilitySpace.getUtility(myLastBid))
	                		// Opponent bids equally, or outbids my previous bid, so lets
	                		// accept
	                		lAction = new Accept(this);
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
				if (myLastAction == null)
					lAction = proposeInitialBid();
				else
					// simply repeat last action
					lAction = myLastAction;
			break;
			}
		}
		catch (Exception e)
		{ 
			System.out.println("Exception in chooseAction:"+e.getMessage());
			e.printStackTrace();
			lAction = new Offer(this, myLastBid);
		}
		myLastAction = lAction;
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
	 * Builds Pareto frontier for the negotiation template
	 * 
	 */
	public ArrayList<Bid> buildParetoFrontier() {
		log("Building Pareto Frontier...");
		//loadAgentsUtilitySpaces();
		ArrayList<Bid> lPareto=new ArrayList<Bid>();
		BidIterator lBidIter = new BidIterator(utilitySpace.getDomain());
		while(lBidIter.hasNext()) {
			Bid lBid = lBidIter.next();
			//log("checking bid "+lBid.toString());
			try {
				if(!checkSolutionVSParetoFrontier(lPareto,lBid)) continue;
			} catch (Exception e) {
				e.printStackTrace();
			}
			try {
				if(checkSolutionVSOtherBids(lBid)) lPareto.add(lBid);
			} catch (Exception e) {
				e.printStackTrace();
			}
			
		}
		//sortParetoFrontier();    	
		log("Finished building Pareto Frontier.");
		return lPareto;
	}
	/**
	 * Checks the bid against current Pareto set
	 * 
	 * @param pBid
	 * @return true if bid is located to the North-East from the current Pareto frontier.
	 * @throws Exception
	 */
	private boolean checkSolutionVSParetoFrontier(ArrayList<Bid>pPareto, Bid pBid) throws Exception {
		boolean lIsStillASolution = true;
		for (Iterator<Bid> lBidIter = pPareto.iterator(); lBidIter.hasNext();) {
			Bid lBid = lBidIter.next();
//			System.out.println("checking bid "+lBid.indexesToString() +" vs " + pBid.indexesToString());    	      
			if((utilitySpace.getUtility(pBid)<utilitySpace.getUtility(lBid))&&
					(fOpponentModel.getExpectedUtility(pBid)<fOpponentModel.getExpectedUtility(lBid)))
				return false;
		}
		return lIsStillASolution;
	}
	/**
	 * Checks the bid against all other bids in the space.
	 * 
	 * @param pBid
	 * @return true if bid is Pareto efficient
	 * @throws Exception
	 */
	private boolean checkSolutionVSOtherBids(Bid pBid) throws Exception {
		boolean lIsStillASolution = true;
		BidIterator lBidIter = new BidIterator(utilitySpace.getDomain());
		while(lBidIter.hasNext()) {
			Bid lBid = lBidIter.next();
//			System.out.println("checking bid "+lBid.indexesToString() +" vs " + pBid.indexesToString());
			if((utilitySpace.getUtility(pBid)<utilitySpace.getUtility(lBid))&&
					(fOpponentModel.getExpectedUtility(pBid)<fOpponentModel.getExpectedUtility(lBid)))
				return false;
		}
		return lIsStillASolution;
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
	private double Paccept(double u, double t) throws Exception
	{
		if (u<0 || u>1.05) throw new Exception("utility "+u+" outside [0,1]");
		if (t<0 || t>1) throw new Exception("time "+t+" outside [0,1]");
		
		if (t==0.5) return u;
		return (u - 2.*u*t + 2.*(-1. + t + Math.sqrt(sq(-1. + t) + u*(-1. + 2*t))))/(-1. + 2*t);
	}
	
	private double sq(double x) { return x*x; }
	
}
