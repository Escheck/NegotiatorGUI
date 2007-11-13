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
    public ArrayList<Bid> AgentABids;
    public ArrayList<Bid> AgentBBids;
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
        AgentABids=AgentABidsP;
        AgentBBids=AgentBBidsP;
        ErrorRemarks=err;
        agentAmaxUtil=agentAmaxUtilP;
        agentBmaxUtil=agentBmaxUtilP;
        agentAstarts=agentAstartsP;
        agentAutilSpaceName=agentAutilSpaceNameP;
        agentButilSpaceName=agentButilSpaceNameP;
    }
    
    
    public String toString() {
        return String.valueOf(sessionNumber) + " agentAName="+agentAname + " agentBName=" + agentBname + 
        " agentAutility="+agentAutility+ " agentButility="+agentButility+
        " errors='"+ErrorRemarks+"'"+
        " agentAbids="+AgentABids+" agentBbids="+AgentBBids+
        " agentAmaxUtil="+agentAmaxUtil+ " agentBmaxUtil="+agentBmaxUtil;

    }
   
}
