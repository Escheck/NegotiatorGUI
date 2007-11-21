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

import negotiator.xml.SimpleElement;

/**
 *
 * @author dmytro
 */
public class NegotiationOutcome {
    public int sessionNumber;
    public String agentAname;
    public String agentBname;
    public String agentAutility;
    public String agentButility;
    public String ErrorRemarks; // non-null if something happens crashing the negotiation
    public ArrayListXML<Bid> AgentABids;
    public ArrayListXML<Bid> AgentBBids;
    public Double agentAmaxUtil;
    public Double agentBmaxUtil;
    public boolean agentAstarts; // true if A starts, false if B starts
    public String agentAutilSpaceName;
    public String agentButilSpaceName;

    /** Creates a new instance of NegotiationOutcome */
    public NegotiationOutcome(int sessionNumber, 
                    String agentAname,
                    String agentBname,
                    String agentAutility,
                    String agentButility,
                    String err,
                    ArrayList<Bid> AgentABidsP,
                    ArrayList<Bid> AgentBBidsP,
                    Double agentAmaxUtilP,
                    Double agentBmaxUtilP,
                    boolean agentAstartsP, // true if A starts, false if B starts
                    String agentAutilSpaceNameP,
                    String agentButilSpaceNameP
                    ) 
    {
        this.sessionNumber = sessionNumber;
        this.agentAutility = agentAutility;
        this.agentButility = agentButility;
        this.agentAname = agentAname;
        this.agentBname = agentBname;    
        AgentABids=new ArrayListXML<Bid>(AgentABidsP);
        AgentBBids=new ArrayListXML<Bid>(AgentBBidsP);
        ErrorRemarks=err;
        agentAmaxUtil=agentAmaxUtilP;
        agentBmaxUtil=agentBmaxUtilP;
        agentAstarts=agentAstartsP;
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
    SimpleElement resultsOfAgent(String agentX,String agentName, String utilspacefilename,
    		String agentAUtil,String agentAMaxUtil, ArrayListXML<Bid> bids)
    {
    	SimpleElement outcome=new SimpleElement("resultsOfAgent");
    	outcome.setAttribute("agent", agentX);
    	outcome.setAttribute("agentName", agentName);
    	outcome.setAttribute("utilspace", utilspacefilename);
    	outcome.setAttribute("finalUtility",agentAUtil);
    	outcome.setAttribute("maxUtility",agentAMaxUtil);
    	outcome.addChildElement(bids.toXML());
    	return outcome;
    }
    
    public SimpleElement toXML()
    {
    	SimpleElement outcome = new SimpleElement("NegotiationOutcome");
    	outcome.setAttribute("errors",ErrorRemarks);
    	String startingagent="agentB"; if (agentAstarts) startingagent="agentA";
    	outcome.setAttribute("startingAgent",startingagent);
    	outcome.addChildElement(resultsOfAgent("A",agentAname,agentAutilSpaceName,
    			agentAutility,""+agentAmaxUtil,AgentABids));
    	outcome.addChildElement(resultsOfAgent("B",agentBname,agentButilSpaceName,
    			agentButility,""+agentBmaxUtil,AgentBBids));
    
		return outcome;
    }
}
