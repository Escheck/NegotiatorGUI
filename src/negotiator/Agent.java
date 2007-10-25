/*
 * Agent.java
 *
 * Created on November 6, 2006, 9:52 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package negotiator;

import negotiator.actions.Action;
import negotiator.issue.ISSUETYPE;
/*
import negotiator.issue.Issue;
import negotiator.issue.IssueDiscrete;
import negotiator.issue.IssueInteger;
import negotiator.issue.IssueReal;
import negotiator.issue.Value;
import negotiator.issue.ValueDiscrete;
import negotiator.issue.ValueInteger;
import negotiator.issue.ValueReal;
import negotiator.utility.EvaluatorDiscrete;
*/
import negotiator.utility.UtilitySpace;

import java.util.Date;

/**
 *
 * @author Dmytro Tykhonov
 * 
 */

//TODO: Maybe move this class to negotiator.agents package?
public class Agent {
    private     String          fName=null;
    //protected	Domain			domain; // domain is inside the utility space.
    protected   UtilitySpace    utilitySpace;
    protected	Date			startTime;
    //private NegotiationTemplate fNT; Wouter: agents should not get access to that
    
    //TODO: Check if we realy need this declaration!
    // Some declarations for 'ease of use' when defining agents.
    public static final ISSUETYPE discrete = ISSUETYPE.DISCRETE;
    public static final ISSUETYPE integer = ISSUETYPE.DISCRETE;
    public static final ISSUETYPE real = ISSUETYPE.REAL;

    
    /** Creates a new instance of Agent */
    /* WARNING!!! Wouter: utilitySpace is null for now.
     * Therefore we do not yet create any windows.
     * Also, note that constructor of Agent has to have an empty parameter list,
     * because there is no class loader with parameterized constructor
     */
    public Agent() {
    }
    
    protected void init(int sessionNumber, int sessionTotalNumber, Date startTimeP, UtilitySpace us) {
        startTime=startTimeP;
    	utilitySpace=us;
        return;
    }
    
    public void ReceiveMessage(Action opponentAction) {
        return;
    }
    
    public Action chooseAction() {
        return null;
    }
    
    public String getName() {
        return fName;
    }

    
    public final void setName(String pName) {
        if(this.fName==null) this.fName = pName;
        return;
    }
    
    
    /**
     * @author W.Pasman
     * determine if this agent is communicating with the user about nego steps.
     * @return true if a human user is directly communicating with the agent in order
     * to steer the nego. This flag is used to determine the timeout for the
     * negotiation (larger with human users).
     */
    public boolean isUIAgent() { return false; }
}
