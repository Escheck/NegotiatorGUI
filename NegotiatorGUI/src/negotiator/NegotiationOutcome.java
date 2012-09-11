/*
 * NegotiationOutcome.java
 *
 * Created on November 21, 2006, 1:31 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package negotiator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import negotiator.actions.Action;
import negotiator.analysis.BidPoint;
import negotiator.analysis.BidPointSorterA;
import negotiator.analysis.BidPointSorterB;
import negotiator.analysis.BidPointTime;
import negotiator.analysis.BidSpace;
import negotiator.protocol.alternatingoffers.AlternatingOffersBilateralAtomicNegoSession;
import negotiator.qualitymeasures.OpponentModelMeasuresResults;
import negotiator.utility.UtilitySpace;
import negotiator.xml.OrderedSimpleElement;
import negotiator.xml.SimpleElement;

/**
 *
 * @author dmytro
 */
public class NegotiationOutcome {
	public int sessionNumber;
	public String agentAname; 	// the name of the agent
	public String agentBname;
	public String agentAclass;	// the class file cotnaining that agent.
	public String agentBclass; 
	public Double agentAutility;
	public Double agentButility;
	public Double agentAutilityDiscount;
	public Double agentButilityDiscount;
	public String ErrorRemarks; // non-null if something happens crashing the negotiation
	public ArrayListXML<BidPointTime> AgentABids;
	public ArrayListXML<BidPointTime> AgentBBids;
	public Double agentAmaxUtil;
	public Double agentBmaxUtil;
	public boolean agentAstarts; // true if A starts, false if B starts
	// IMPORTANT. Note that this boolean differs from the agentAStarts flag that indicates whether
	// agent A is FORCED to use as starting agent. agentA may still be chosen to start if
	// that flag is not set.
	public String agentAutilSpaceName;
	public String agentButilSpaceName;
	public SimpleElement additional;
	public List<String> extraNames = new ArrayList<String>();
	public List<String> extraValues = new ArrayList<String>();
	public double time;
	public String domainName;
	private final AlternatingOffersBilateralAtomicNegoSession alternatingOffersBilateralAtomicNegoSession;
	private final Action lastAction;
	private int runNr = 0;
	private OpponentModelMeasuresResults omMeasuresResults = null;

	/** Creates a new instance of NegotiationOutcome 
	 * @param alternatingOffersBilateralAtomicNegoSession 
	 * @param lastAction 
	 * @param string 
	 * @param time 
	 * @param distanceToNash 
	 * @param utilBDiscount 
	 * @param utilADiscount */
	public NegotiationOutcome(AlternatingOffersBilateralAtomicNegoSession alternatingOffersBilateralAtomicNegoSession, int sessionNumber, 
			Action lastAction, String agentAname,
			String agentBname,
			String agentAclass,
			String agentBclass,
			Double agentAutility,
			Double agentButility,
			Double agentAutilityDiscount,
			Double agentButilityDiscount,
			String err,
			ArrayList<BidPointTime> agentASize,
			ArrayList<BidPointTime> agentBSize,
			Double agentAmaxUtilP,
			Double agentBmaxUtilP,
			boolean startingWithA, // true if A starts, false if B starts
			String domainName,
			String agentAutilSpaceNameP,
			String agentButilSpaceNameP,
			SimpleElement additional, double time, double distanceToNash
	) 
	{
		this.alternatingOffersBilateralAtomicNegoSession = alternatingOffersBilateralAtomicNegoSession;
		this.sessionNumber = sessionNumber;
		this.lastAction = lastAction;
		this.agentAutility = agentAutility;
		this.agentButility = agentButility;
		this.agentAutilityDiscount = agentAutilityDiscount;
		this.agentButilityDiscount = agentButilityDiscount;
		this.agentAname = agentAname;
		this.agentBname = agentBname;
		this.agentAclass=agentAclass;
		this.agentBclass=agentBclass;
		this.domainName = domainName;
		this.additional = additional;
		AgentABids=new ArrayListXML<BidPointTime>(agentASize);
		AgentBBids=new ArrayListXML<BidPointTime>(agentBSize);
		ErrorRemarks=err;
		agentAmaxUtil=agentAmaxUtilP;
		agentBmaxUtil=agentBmaxUtilP;
		agentAstarts=startingWithA;
		agentAutilSpaceName=agentAutilSpaceNameP;
		agentButilSpaceName=agentButilSpaceNameP;
		this.time = time;
	}


	public String toString() {
		String startingagent="agentB"; if (agentAstarts) startingagent="agentA";
		return String.valueOf(sessionNumber) + " agentAName="+agentAname + " agentBName=" + agentBname + 
		" agentAutility="+agentAutility+ " agentButility="+agentButility+
		" agentAutilityDiscount="+agentAutilityDiscount+ " agentButilityDiscount="+agentButilityDiscount+
		" errors='"+ErrorRemarks+"'"+
		" agentAmaxUtil="+agentAmaxUtil+ " agentBmaxUtil="+agentBmaxUtil+
		" startingAgent="+startingagent+
		" agentAutilspacefilename="+agentAutilSpaceName+
		" agentButilspacefilename="+agentButilSpaceName +
		" agentAbids="+AgentABids+" agentBbids="+AgentBBids
		;

	}

	/**
	 * 
	 * @param agentX is "A" or "B"
	 * @param agentName is the given name to that agent.
	 * @param utilspacefilename is the filename holding the utility.xml file
	 * @param oppUtilSpaceName 
	 * @param oppClass 
	 * @param oppName 
	 * @param bids is the arraylist of bids made by that agent.
	 * @return
	 */
	SimpleElement resultsOfAgent(String agentX, String agentName, String agentClass, String utilspacefilename,
			String oppName, String oppClass, String oppUtilSpaceName, Double agentAUtil,Double agentAUtilDiscount,Double agentAMaxUtil, ArrayListXML<BidPointTime> bids, boolean addBids)
	{
		double minDemandedUtil;
		double fyu;
		double bsCR;
		double normACCR;
		double totalCR;
		double acCR;
		
		
		//double cooperation;
		if (Global.LOG_COMPETITIVENESS)
		{
			BidSpace bidSpace = alternatingOffersBilateralAtomicNegoSession.getBidSpace();
			fyu = getFYU(agentX, bidSpace);
			
			minDemandedUtil = getMinDemandedUtil(agentX, bids);
			bsCR = determineCR(agentX, bids, fyu, minDemandedUtil);
			
			double minUtil = agentX.equals("A") ? agentAutility : agentButility;
			totalCR =  determineCR(agentX, bids, fyu, minUtil);
			if(totalCR < bsCR)
				totalCR = bsCR;
			
			acCR = (totalCR - bsCR);
			
			if(1-bsCR == 0){
				normACCR = 0;
			} else{
				normACCR = acCR / (1-bsCR);
			}
			
		}
		
		OrderedSimpleElement outcome=new OrderedSimpleElement("resultsOfAgent");
		outcome.setAttribute("agent", agentX);
		outcome.setAttribute("agentName", agentName);
		
		if(agentName.contains("bs")) {


			outcome.setAttribute("offering_strategy", getOfferingStrategyName(agentName));
			outcome.setAttribute("acceptance_strategy", getAcceptanceStrategyName(agentName));
			outcome.setAttribute("opponent_model", getOpponentModelName(agentName));
		}
		
		outcome.setAttribute("agentClass", agentClass);
		outcome.setAttribute("utilspace", utilspacefilename);
		outcome.setAttribute("Opponent-agentName", oppName);
		outcome.setAttribute("Opponent-agentClass", oppClass);
		outcome.setAttribute("Opponent-utilspace", oppUtilSpaceName);
		outcome.setAttribute("finalUtility",""+agentAUtil);
		outcome.setAttribute("discountedUtility",""+agentAUtilDiscount);
		
		
		double bestAcceptableBid;
		double bestDiscountedAccepableBid;
		if(agentX.equals("A")){
			bestAcceptableBid = getMaxRecievedUtil(agentX, AgentBBids);
			bestDiscountedAccepableBid = getMaxDiscountedRecievedUtil(agentX, AgentBBids);
		} else {
			bestAcceptableBid = getMaxRecievedUtil(agentX, AgentABids);
			bestDiscountedAccepableBid = getMaxDiscountedRecievedUtil(agentX, AgentABids);
		}
		outcome.setAttribute("bestAcceptableBid", String.valueOf(bestAcceptableBid));
		outcome.setAttribute("bestDiscountedAccepableBid", String.valueOf(bestDiscountedAccepableBid));
		
		if (Global.LOG_COMPETITIVENESS)
		{
			outcome.setAttribute("minDemandedUtility", String.valueOf(minDemandedUtil));
			outcome.setAttribute("FYU",String.valueOf(fyu));
			outcome.setAttribute("Total_CR",String.valueOf(totalCR));
			outcome.setAttribute("BS_CR",String.valueOf(bsCR));
			outcome.setAttribute("AC_CR",String.valueOf(acCR));
			outcome.setAttribute("Normalized_AC_CR",String.valueOf(normACCR));


			//outcome.setAttribute("cooperation",""+cooperation);
		}
		
		
		
		if (omMeasuresResults != null) {
			if (agentX.equals("A")) {
				setOMMeasures(outcome);
			}
		}
		
		//		outcome.setAttribute("agentADiscUtil", "" + (agentX.equals("A") ? agentAutilityDiscount : ""));
		//		outcome.setAttribute("agentBDiscUtil", "" + (agentX.equals("B") ? agentButilityDiscount : ""));
		outcome.setAttribute("maxUtility",""+agentAMaxUtil);
		Double normalized=0.; if (agentAMaxUtil>0) { normalized = agentAUtil/agentAMaxUtil; }
		outcome.setAttribute("normalizedUtility",""+normalized);
		return outcome;
	}
	
	private void setOMMeasures(OrderedSimpleElement outcome) {
		outcome.setAttribute("Pearson_Correlation_Bids", omMeasuresResults.getPearsonCorrelationCoefficientOfBidsList().get(0) + "");
		outcome.setAttribute("Ranking_Distance_Bids", omMeasuresResults.getRankingDistanceOfBidsList().get(0) + "");
		outcome.setAttribute("Ranking_Distance_Issue_Weights", omMeasuresResults.getRankingDistanceOfIssueWeightsList().get(0) + "");
		outcome.setAttribute("Average_Difference_Bids", omMeasuresResults.getAverageDifferenceBetweenBidsList().get(0) + "");
		outcome.setAttribute("Average_Difference_Issue_Weights", omMeasuresResults.getAverageDifferenceBetweenIssueWeightsList().get(0) + "");	
		outcome.setAttribute("Kalai_Difference", omMeasuresResults.getKalaiDistanceList().get(0) + "");
		outcome.setAttribute("Nash_Difference", omMeasuresResults.getNashDistanceList().get(0) + "");	
		outcome.setAttribute("Difference_Pareto_Frontier", omMeasuresResults.getAverageDifferenceOfParetoFrontierList().get(0) + "");
		outcome.setAttribute("Percentage_Correct_Pareto_Bids", omMeasuresResults.getPercentageOfCorrectlyEstimatedParetoBidsList().get(0) + "");
		outcome.setAttribute("Percentage_Incorrect_Pareto_Bids", omMeasuresResults.getPercentageOfIncorrectlyEstimatedParetoBidsList().get(0) + "");
		outcome.setAttribute("Pareto_Frontier_Distance", omMeasuresResults.getParetoFrontierDistanceList().get(0) + "");
	}


	private double determineCR(String agentX, ArrayListXML<BidPointTime> bids, double fyu, double minUtil){
		double CR;
		if(minUtil != -1){
			double yield = Math.max(minUtil, fyu);
			double competitiveness = (yield - fyu) / (1 - fyu);
			CR = 1 - competitiveness;

		} else {
			CR = -1;
			System.out.println("Bids is Empty");

		}
		
		return CR;
	}
	
	public ArrayListXML<BidPointTime> getAgentABids() {
		return AgentABids;
	}

	public ArrayListXML<BidPointTime> getAgentBBids() {
		return AgentBBids;
	}

	/**
	 * Gets the Full Yield Utility of the agent. 
	 * Definition of FYU for agent A: 
	 * let X be the optimal bid for agent B (with utility 1). Then the utility of X for agent A is its FYU.
	 */
	public static double getFYU(String agentX, BidSpace bidSpace)
	{
		ArrayList<BidPoint> paretoFrontier = null;
		try
		{
			paretoFrontier = bidSpace.getParetoFrontier();
		} catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		BidPoint bestOutcomeForA = paretoFrontier.get(paretoFrontier.size() - 1);
		BidPoint bestOutcomeForB = paretoFrontier.get(0);
		double fyu = Double.NaN;
		if ("A".equals(agentX))
			fyu = bestOutcomeForB.utilityA;
		else if ("B".equals(agentX))
			fyu = bestOutcomeForA.utilityB;
		else
			System.err.println("Unknown agent " + agentX);

		return fyu;
	}
	
	/**
	 * Gets the largest discounted utility an agent was offered from the opponent
	 */
	private double getMaxDiscountedRecievedUtil(String agentX, ArrayListXML<BidPointTime> bids)
	{
		double discountFactorA = alternatingOffersBilateralAtomicNegoSession.getAgentAUtilitySpace().getDiscountFactor();
		double discountFactorB = alternatingOffersBilateralAtomicNegoSession.getAgentBUtilitySpace().getDiscountFactor();

		
		double maxRecievedDiscountedUtil = 0;
		if(!bids.isEmpty()){
			for(BidPointTime bidPointTime : bids){
				double discountedBidPoint;
				if(agentX.equals("A")){
					discountedBidPoint = UtilitySpace.discount(bidPointTime.utilityA, bidPointTime.time, discountFactorA);
				}else {
					discountedBidPoint = UtilitySpace.discount(bidPointTime.utilityB, bidPointTime.time, discountFactorB);
				}
				
				if(discountedBidPoint > maxRecievedDiscountedUtil){
					maxRecievedDiscountedUtil = bidPointTime.time;
				}
			
			}
		}
			
		return maxRecievedDiscountedUtil;
	}
	
	/**
	 * Gets the largest utility an agent was offered from the opponent
	 */
	private double getMaxRecievedUtil(String agentX, ArrayListXML<BidPointTime> bids)
	{
		Comparator<BidPoint> comp = null;
		if ("A".equals(agentX))
			comp = new BidPointSorterA();
		else if ("B".equals(agentX))
			comp = new BidPointSorterB();
		else
			System.err.println("Unknown agent " + agentX);
		
		double maxRecievedUtil;
		if(!bids.isEmpty()){

			BidPointTime minDemandedBid = Collections.max(bids, comp);

			 maxRecievedUtil = agentX.equals("A") ? minDemandedBid.utilityA : minDemandedBid.utilityB;
		}else { 
			maxRecievedUtil = -1;
		
		}
		return maxRecievedUtil;
	}

	
	/**
	 * Gets the smallest utility an agent was willing to ask
	 */
	private double getMinDemandedUtil(String agentX, ArrayListXML<BidPointTime> bids)
	{
		Comparator<BidPoint> comp = null;
		if ("A".equals(agentX))
			comp = new BidPointSorterA();
		else if ("B".equals(agentX))
			comp = new BidPointSorterB();
		else
			System.err.println("Unknown agent " + agentX);
		
		double minDemandedUtil;
		if(!bids.isEmpty()){
			//System.out.println("agentX: "+agentX);
			//System.out.println("lastAction: "+lastAction);


			BidPoint minDemandedBid = Collections.min(bids, comp);
			//System.out.println("minDemandedBid: "+minDemandedBid.utilityA);
			

			 minDemandedUtil = agentX.equals("A") ? minDemandedBid.utilityA : minDemandedBid.utilityB;
		}else { 
			minDemandedUtil = -1;
		
		}
		return minDemandedUtil;
	}
	
	public void addExtraAttribute(String name, String value)
	{
		extraNames.add(name);
		extraValues.add(value);
	}

	/**
	 * Does not include bid history in log file.
	 */
	public SimpleElement toXML()
	{
		return toXML(false);
	}

	/**
	 * Includes bid history in log file.
	 */
	public SimpleElement toXMLWithBids()
	{
		return toXML(true);
	}


	private SimpleElement toXML(boolean addBids)
	{
		OrderedSimpleElement outcome = new OrderedSimpleElement("NegotiationOutcome");
		outcome.setAttribute("currentTime", ""+Global.getCurrentTime());
		outcome.setAttribute("timeOfAgreement", "" + time);
		outcome.setAttribute("bids", "" + (AgentABids.size() + AgentBBids.size()));
		outcome.setAttribute("domain", domainName);
		outcome.setAttribute("lastAction", "" + lastAction);
		outcome.setAttribute("runNumber", runNr + "");

		outcome.addChildElement(resultsOfAgent("A",agentAname,agentAclass,agentAutilSpaceName, 
				agentBname, agentBclass, agentButilSpaceName,
				agentAutility,agentAutilityDiscount,agentAmaxUtil,AgentABids, addBids));
		outcome.addChildElement(resultsOfAgent("B",agentBname,agentBclass,agentButilSpaceName,
				agentAname, agentAclass, agentAutilSpaceName,
				agentButility,agentButilityDiscount,agentBmaxUtil,AgentBBids, addBids));
		if(additional!=null && !additional.isEmpty()) outcome.addChildElement(additional);
		if (ErrorRemarks != null) outcome.setAttribute("errors",ErrorRemarks);
		String startingagent="B"; if (agentAstarts) startingagent="A";
		outcome.setAttribute("startingAgent",startingagent);
		
		int i = 0;
		for (String extraName : extraNames)
		{
			String extraValue = extraValues.get(i);
			if (extraName != null && extraValue != null) 
				outcome.setAttribute(extraName, extraValue);
			i++;
		}
		
		if (addBids)
			outcome.addChildElement(bidsToXML());
		
		return outcome;
	}
	
	/**
	 * Method used for getting the name of an offering strategy component
	 * of a decoupled agent.
	 * 
	 * @param agentName
	 * @return name of bidding strategy
	 */
	private static String getOfferingStrategyName(String agentName) {
		int left = agentName.indexOf("bs:");
		int right = agentName.indexOf("as:");

		String agentOfferingStrategyName = agentName.substring(left + 3, right);
		agentOfferingStrategyName = agentOfferingStrategyName.trim();

		return agentOfferingStrategyName;
	}
	
	/**
	 * Method used for getting the name of an acceptance strategy component
	 * of a decoupled agent.
	 * 
	 * @param agentName
	 * @return name of acceptance strategy
	 */
	private static String getAcceptanceStrategyName(String agentName) {
		int left = agentName.indexOf("as:");
		int right = agentName.indexOf("om:");

		String agentAcceptanceStrategyName = agentName.substring(left + 3, right);
		agentAcceptanceStrategyName = agentAcceptanceStrategyName.trim();
		
		return agentAcceptanceStrategyName;
	}
	
	/**
	 * Method used for getting the name of an opponent model component
	 * of a decoupled agent.
	 * 
	 * @param agentName
	 * @return name of opponent model
	 */
	private static String getOpponentModelName(String agentName) {
		int left = agentName.indexOf("om:");
		int right = agentName.indexOf("oms:");

		String agentOpponentModelName = agentName.substring(left + 3, right);
		agentOpponentModelName = agentOpponentModelName.trim();
		
		return agentOpponentModelName;
	}

	private OrderedSimpleElement bidsToXML()
	{
		OrderedSimpleElement bids = new OrderedSimpleElement("bidHistory");

		final int total = Math.max(AgentABids.size(), AgentBBids.size());
		for (int i = 0; i < total; i++)
		{
			if (i < AgentABids.size())
			{
				BidPoint a = AgentABids.get(i);
				SimpleElement xmlBidpoint = new OrderedSimpleElement("bidpoint");
				xmlBidpoint.setAttribute("fromAgent", "A");
				xmlBidpoint.setAttribute("utilityA", String.valueOf(a.utilityA));
				xmlBidpoint.setAttribute("utilityB", String.valueOf(a.utilityB));
				bids.addChildElement(xmlBidpoint);
			}

			if (i < AgentBBids.size())
			{
				BidPoint b = AgentBBids.get(i);
				SimpleElement xmlBidpoint = new OrderedSimpleElement("bidpoint");
				xmlBidpoint.setAttribute("fromAgent", "B");
				xmlBidpoint.setAttribute("utilityA", String.valueOf(b.utilityA));
				xmlBidpoint.setAttribute("utilityB", String.valueOf(b.utilityB));
				bids.addChildElement(xmlBidpoint);
			}
		}
		return bids;
	}

	public int getRunNr() {
		return runNr;
	}

	public void setRunNr(int runNr) {
		this.runNr = runNr;
	}

	public boolean getAgentAFirst() {
		return agentAstarts;
	}


	public void setNegotiationOutcome(
			OpponentModelMeasuresResults omMeasuresResults) {
		this.omMeasuresResults  = omMeasuresResults;
	}
}
