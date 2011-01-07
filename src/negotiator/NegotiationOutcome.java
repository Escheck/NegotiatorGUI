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

import negotiator.analysis.BidPoint;
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
	public double time;

	/** Creates a new instance of NegotiationOutcome 
	 * @param time 
	 * @param utilBDiscount 
	 * @param utilADiscount */
	public NegotiationOutcome(int sessionNumber, 
			String agentAname,
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
			String agentAutilSpaceNameP,
			String agentButilSpaceNameP,
			SimpleElement additional, double time
	) 
	{
		this.sessionNumber = sessionNumber;
		this.agentAutility = agentAutility;
		this.agentButility = agentButility;
		this.agentAutilityDiscount = agentAutilityDiscount;
		this.agentButilityDiscount = agentButilityDiscount;
		this.agentAname = agentAname;
		this.agentBname = agentBname;
		this.agentAclass=agentAclass;
		this.agentBclass=agentBclass;
		this.additional = additional;
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
	 * @param bids is the arraylist of bids made by that agent.
	 * @return
	 */
	SimpleElement resultsOfAgent(String agentX,String agentName, String agentClass, String utilspacefilename,
			Double agentAUtil,Double agentAUtilDiscount,Double agentAMaxUtil, ArrayListXML<BidPoint> bids, boolean addBids)
	{
		OrderedSimpleElement outcome=new OrderedSimpleElement("resultsOfAgent");
		outcome.setAttribute("agent", agentX);
		outcome.setAttribute("agentName", agentName);
		outcome.setAttribute("agentClass", agentClass);
		outcome.setAttribute("utilspace", utilspacefilename);
		outcome.setAttribute("finalUtility",""+agentAUtil);
		outcome.setAttribute("discountedUtility",""+agentAUtilDiscount);
		//		outcome.setAttribute("agentADiscUtil", "" + (agentX.equals("A") ? agentAutilityDiscount : ""));
		//		outcome.setAttribute("agentBDiscUtil", "" + (agentX.equals("B") ? agentButilityDiscount : ""));
		outcome.setAttribute("maxUtility",""+agentAMaxUtil);
		Double normalized=0.; if (agentAMaxUtil>0) { normalized = agentAUtil/agentAMaxUtil; }
		outcome.setAttribute("normalizedUtility",""+normalized);
		return outcome;
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

		outcome.addChildElement(resultsOfAgent("A",agentAname,agentAclass,agentAutilSpaceName,
				agentAutility,agentAutilityDiscount,agentAmaxUtil,AgentABids, addBids));
		outcome.addChildElement(resultsOfAgent("B",agentBname,agentBclass,agentButilSpaceName,
				agentButility,agentButilityDiscount,agentBmaxUtil,AgentBBids, addBids));
		if(additional!=null && !additional.isEmpty()) outcome.addChildElement(additional);
		if (ErrorRemarks != null) outcome.setAttribute("errors",ErrorRemarks);
		String startingagent="B"; if (agentAstarts) startingagent="A";
		outcome.setAttribute("startingAgent",startingagent);
		
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
