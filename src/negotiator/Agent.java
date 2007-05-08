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
import negotiator.issue.Value;

/**
 *
 * @author dmytro
 */

public class Agent {
    private     String          fName;
    protected   UtilitySpace    utilitySpace;
    private NegotiationTemplate fNT;
    
    // Some declarations for 'ease of use' when defining agents.
    public static final ISSUETYPE discrete = ISSUETYPE.DISCRETE;
    
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
    
    public final NegotiationTemplate getNegotiationTemplate() {
        return fNT;
    }
    
    // Some declarations for 'ease of use' when defining agents.
    public static Value makeValue(ISSUETYPE type, String s) {
    	return Value.makeValue(type, s);
    }
    
    public static Value makeValue(ISSUETYPE type, int i) {
    	return Value.makeValue(type, i);
    }
    
    public static Value makeValue(ISSUETYPE type, double r) {
    	return Value.makeValue(type, r);
    }
    
}
