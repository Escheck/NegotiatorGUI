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
import negotiator.issue.Issue;
import negotiator.issue.IssueDiscrete;
import negotiator.issue.IssueInteger;
import negotiator.issue.IssueReal;
import negotiator.issue.Value;
import negotiator.issue.ValueDiscrete;
import negotiator.issue.ValueInteger;
import negotiator.issue.ValueReal;
import negotiator.utility.EvaluatorDiscrete;
import negotiator.utility.UtilitySpace;

/**
 *
 * @author Dmytro Tykhonov
 * 
 */

//TODO: Maybe move this class to negotiator.agents package?
public class Agent {
    private     String          fName;
    protected   UtilitySpace    utilitySpace;
    private NegotiationTemplate fNT;
    
    //TODO: Check if we realy need this declaration!
    // Some declarations for 'ease of use' when defining agents.
    public static final ISSUETYPE discrete = ISSUETYPE.DISCRETE;
    public static final ISSUETYPE integer = ISSUETYPE.DISCRETE;
    public static final ISSUETYPE real = ISSUETYPE.REAL;

    
    /** Creates a new instance of Agent */
    public Agent() {
        this.fName = null;
    }
    
    protected void init(int sessionNumber, int sessionTotalNumber, NegotiationTemplate nt) {
        this.fNT = nt;
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
    
    public void loadUtilitySpace(String fileName) {
        //load the utility space
        utilitySpace = new UtilitySpace(fNT.getDomain(), fileName);
        return;
    }
    
    public final void setName(String pName) {
        if(this.fName==null) this.fName = pName;
        return;
    }
    
    public final double getUtility(Bid bid) {
        return utilitySpace.getUtility(bid);
    }
    
    public final Bid getMaxUtilityBid() {
    	return utilitySpace.getMaxUtilityBid();
    }
    
    public final NegotiationTemplate getNegotiationTemplate() {
        return fNT;
    }
    
}
