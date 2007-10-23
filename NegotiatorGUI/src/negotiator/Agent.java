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
    protected	Domain			domain;
    protected   UtilitySpace    utilitySpace;
    //private NegotiationTemplate fNT;
    
    //TODO: Check if we realy need this declaration!
    // Some declarations for 'ease of use' when defining agents.
    public static final ISSUETYPE discrete = ISSUETYPE.DISCRETE;
    public static final ISSUETYPE integer = ISSUETYPE.DISCRETE;
    public static final ISSUETYPE real = ISSUETYPE.REAL;

    
    /** Creates a new instance of Agent */
    /* WARNING!!! Wouter: utilitySpace will be null for some time!!! 
     * this may crash attempts to show info in the GUI window!!
     */
    public Agent() {
        this.fName = null;
    }
    
    protected void init(int sessionNumber, int sessionTotalNumber, Domain d) {
    	domain=d;
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
        utilitySpace = new UtilitySpace(domain, fileName);
        return;
    }
    
    public final void setName(String pName) {
        if(this.fName==null) this.fName = pName;
        return;
    }
    
    public final double getUtility(Bid bid) throws Exception
    {
        return utilitySpace.getUtility(bid);
    }
    
    public final Bid getMaxUtilityBid() throws Exception
    {
    	return utilitySpace.getMaxUtilityBid();
    }
    
    public final Domain getDomain() {
        return domain;
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
