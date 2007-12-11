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

    /** Creates a new instance of NegotiationOutcome */
    public NegotiationOutcome(int sessionNumber, 
                    String agentAname,
                    String agentBname,
                    String agentAclass,
                    String agentBclass,
                    Double agentAutility,
                    Double agentButility,
                    String err,
                    ArrayList<BidPoint> AgentABidsP,
                    ArrayList<BidPoint> AgentBBidsP,
                    Double agentAmaxUtilP,
                    Double agentBmaxUtilP,
                    boolean startingWithA, // true if A starts, false if B starts
                    String agentAutilSpaceNameP,
                    String agentButilSpaceNameP
                    ) 
    {
        this.sessionNumber = sessionNumber;
        this.agentAutility = agentAutility;
        this.agentButility = agentButility;
        this.agentAname = agentAname;
        this.agentBname = agentBname;
        this.agentAclass=agentAclass;
        this.agentBclass=agentBclass;
        AgentABids=new ArrayListXML<BidPoint>(AgentABidsP);
        AgentBBids=new ArrayListXML<BidPoint>(AgentBBidsP);
        ErrorRemarks=err;
        agentAmaxUtil=agentAmaxUtilP;
        agentBmaxUtil=agentBmaxUtilP;
        agentAstarts=startingWithA;
        agentAutilSpaceName=agentAutilSpaceNameP;
        agentButilSpaceName=agentButilSpaceNameP;
    }
    
    
    public String toString() {
    	String startingagent="agentB"; if (agentAstarts) startingagent="agentA";
        return String.valueOf(sessionNumber) + " agentAName="+agentAname + " agentBName=" + agentBname + 
        " agentAutility="+agentAutility+ " agentButility="+agentButility+
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
    		Double agentAUtil,Double agentAMaxUtil, ArrayListXML<BidPoint> bids)
    {
    	SimpleElement outcome=new SimpleElement("resultsOfAgent");
    	outcome.setAttribute("agent", agentX);
    	outcome.setAttribute("agentName", agentName);
    	outcome.setAttribute("agentClass", agentClass);
    	outcome.setAttribute("utilspace", utilspacefilename);
    	outcome.setAttribute("finalUtility",""+agentAUtil);
    	outcome.setAttribute("maxUtility",""+agentAMaxUtil);
    	Double normalized=0.; if (agentAMaxUtil>0) { normalized=agentAUtil/agentAMaxUtil; }
    	outcome.setAttribute("normalizedUtility",""+normalized);

    	outcome.addChildElement(bids.toXML());
    	return outcome;
    }
    
    public SimpleElement toXML()
    {
    	SimpleElement outcome = new SimpleElement("NegotiationOutcome");
    	outcome.setAttribute("currentTime", ""+Main.getCurrentTime());
    	outcome.setAttribute("errors",ErrorRemarks);
    	String startingagent="agentB"; if (agentAstarts) startingagent="agentA";
    	outcome.setAttribute("startingAgent",startingagent);
    	outcome.addChildElement(resultsOfAgent("A",agentAname,agentAclass,agentAutilSpaceName,
    			agentAutility,agentAmaxUtil,AgentABids));
    	outcome.addChildElement(resultsOfAgent("B",agentBname,agentBclass,agentButilSpaceName,
    			agentButility,agentBmaxUtil,AgentBBids));
    
		return outcome;
    }
}
