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
import negotiator.analysis.BidSpace;
import negotiator.protocol.alternatingoffers.AlternatingOffersBilateralAtomicNegoSession;
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
	public ArrayListXML<BidPoint> AgentABids;
	public ArrayListXML<BidPoint> AgentBBids;
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
	private final double distanceToNash;
	private final AlternatingOffersBilateralAtomicNegoSession alternatingOffersBilateralAtomicNegoSession;
	private final Action lastAction;

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
			ArrayList<BidPoint> AgentABidsP,
			ArrayList<BidPoint> AgentBBidsP,
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
		this.distanceToNash = distanceToNash;
		AgentABids=new ArrayListXML<BidPoint>(AgentABidsP);
		AgentBBids=new ArrayListXML<BidPoint>(AgentBBidsP);
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
			String oppName, String oppClass, String oppUtilSpaceName, Double agentAUtil,Double agentAUtilDiscount,Double agentAMaxUtil, ArrayListXML<BidPoint> bids, boolean addBids)
	{
		double minDemandedUtil;
		double fyu;
		double competitiveness;
		double cooperation;
		if (Global.LOG_COMPETITIVENESS)
		{
			minDemandedUtil = getMinDemandedUtil(agentX, bids);
			BidSpace bidSpace = alternatingOffersBilateralAtomicNegoSession.getBidSpace();
			fyu = getFYU(agentX, bidSpace);
			double yield = Math.max(minDemandedUtil, fyu);
			competitiveness = (yield - fyu) / (1 - fyu);
			cooperation = 1 - competitiveness;
		}
		
		OrderedSimpleElement outcome=new OrderedSimpleElement("resultsOfAgent");
		outcome.setAttribute("agent", agentX);
		outcome.setAttribute("agentName", agentName);
		outcome.setAttribute("agentClass", agentClass);
		outcome.setAttribute("utilspace", utilspacefilename);
		outcome.setAttribute("Opponent-agentName", oppName);
		outcome.setAttribute("Opponent-agentClass", oppClass);
		outcome.setAttribute("Opponent-utilspace", oppUtilSpaceName);
		outcome.setAttribute("finalUtility",""+agentAUtil);
		outcome.setAttribute("discountedUtility",""+agentAUtilDiscount);
		
		if (Global.LOG_COMPETITIVENESS)
		{
			outcome.setAttribute("minDemandedUtility",""+minDemandedUtil);
			outcome.setAttribute("FYU",""+fyu);
			outcome.setAttribute("cooperation",""+cooperation);
			System.out.println("cooperation: "+cooperation);
		}
		//		outcome.setAttribute("agentADiscUtil", "" + (agentX.equals("A") ? agentAutilityDiscount : ""));
		//		outcome.setAttribute("agentBDiscUtil", "" + (agentX.equals("B") ? agentButilityDiscount : ""));
		outcome.setAttribute("maxUtility",""+agentAMaxUtil);
		Double normalized=0.; if (agentAMaxUtil>0) { normalized = agentAUtil/agentAMaxUtil; }
		outcome.setAttribute("normalizedUtility",""+normalized);
		return outcome;
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
		
//		System.out.println("Pareto begin: " + bestOutcomeForA);
//		System.out.println("Pareto end: " + bestOutcomeForB);
//		System.out.println("So, FYU_" + agentX + " = " + fyu);
		return fyu;
	}

	/**
	 * Gets the smallest utility an agent was willing to ask
	 */
	private double getMinDemandedUtil(String agentX, ArrayListXML<BidPoint> bids)
	{
		Comparator<BidPoint> comp = null;
		if ("A".equals(agentX))
			comp = new BidPointSorterA();
		else if ("B".equals(agentX))
			comp = new BidPointSorterB();
		else
			System.err.println("Unknown agent " + agentX);
		
		BidPoint minDemandedBid = Collections.min(bids, comp);
		double minDemandedUtil = agentX.equals("A") ? minDemandedBid.utilityA : minDemandedBid.utilityB;
		System.out.println("minDemandedUtility:"+minDemandedUtil);
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
		outcome.setAttribute("timeOfAgreement", "" + time);
		outcome.setAttribute("bids", "" + (AgentABids.size() + AgentBBids.size()));
		outcome.setAttribute("domain", domainName);
		outcome.setAttribute("lastAction", "" + lastAction);

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
}
