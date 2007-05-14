/*
 * MyAgent.java
 *
 * Created on November 6, 2006, 9:55 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package negotiator.agents;

import java.util.Collections;
import java.util.Comparator;
import java.util.Vector;
import negotiator.*;
import negotiator.actions.*;
import negotiator.issue.*;
// import negotiator.exceptions.*;

/**
 *
 * @author Dmytro Tykhonov & Koen Hindriks
 * 
 */
public class SimpleAgent extends Agent{
    private Action actionOfPartner;
    private int sessionNumber;
    private int sessionTotalNumber;
    private int[] myPreviousBidIndex;
    private Bid myPreviousBid;
    private static final double BREAK_OFF_POINT = 0.5;
 
    /** Creates a new instance of MyAgent */

    public SimpleAgent() {
        super();        
        return;
    }
    @Override
	protected void init(int sessionNumber, int sessionTotalNumber, NegotiationTemplate nt) {
        super.init (sessionNumber, sessionTotalNumber, nt);
        this.sessionNumber = sessionNumber;
        this.sessionTotalNumber = sessionTotalNumber;
        actionOfPartner = null;
        myPreviousBid = null;
        return;
    }

    @Override
	public void ReceiveMessage(Action opponentAction) {
        this.actionOfPartner = opponentAction;
        return;
    }
    
    private Bid getNextBid() {
        Value[] values = new Value[getNegotiationTemplate().getDomain().getNumberOfIssues()];       
        for(int i=0;i<getNegotiationTemplate().getDomain().getNumberOfIssues();i++) {
        	Issue lIssue = getNegotiationTemplate().getDomain().getIssue(i);        	
			switch(lIssue.getType()) {
			case INTEGER:
	            int numberOfOptions = 
	            	((IssueInteger)lIssue).getUpperBound()- 
	            	((IssueInteger)lIssue).getLowerBound()+1; //do not include "unspecified"
	            int optionIndex = Double.valueOf(java.lang.Math.random()*(numberOfOptions)).intValue();
	            if (optionIndex >= numberOfOptions) optionIndex= numberOfOptions-1;
	            System.out.println(optionIndex);
	            values[i]= new ValueInteger(((IssueInteger)lIssue).getLowerBound()+optionIndex);
			case REAL: 
				IssueReal lIssueReal =(IssueReal)lIssue;
				double lOneStep = (lIssueReal.getUpperBound()-lIssueReal.getLowerBound())/lIssueReal.getNumberOfDiscretizationSteps();
	            numberOfOptions =lIssueReal.getNumberOfDiscretizationSteps();
	            optionIndex = Double.valueOf(java.lang.Math.random()*(numberOfOptions)).intValue();
	            if (optionIndex >= numberOfOptions) optionIndex= numberOfOptions-1;
				values[i]= new ValueReal(lIssueReal.getLowerBound()+lOneStep*optionIndex);
				break;
			case DISCRETE:
				IssueDiscrete lIssueDiscrete = (IssueDiscrete)lIssue;
	            numberOfOptions =lIssueDiscrete.getNumberOfValues();
	            optionIndex = Double.valueOf(java.lang.Math.random()*(numberOfOptions)).intValue();
	            if (optionIndex >= numberOfOptions) optionIndex= numberOfOptions-1;
				values[i]= lIssueDiscrete.getValue(optionIndex);
				break;
			}// switch
		}//for
        return new Bid(getNegotiationTemplate().getDomain(),values);
    }
    
    private Action chooseNextAction() {
        Bid nextBid ;
        nextBid = getNextBid();
        myPreviousBid = nextBid;
        if (nextBid == null) return (new EndNegotiation(this));                
        return (new Offer(this, nextBid));
    }
    
    @Override
	public Action chooseAction() {
        Action action = null;

            if(actionOfPartner==null) {
                //
                action = chooseNextAction();
            } else {
                if(actionOfPartner instanceof Offer) {
                    Bid partnerBid = ((Offer)actionOfPartner).getBid();
                    if(myPreviousBid!=null)
                        if(utilitySpace.getUtility(partnerBid)==utilitySpace.getUtility(myPreviousBid))
                            action = new Accept(this, partnerBid);
                        else action = chooseNextAction();                   
                    else {
                        action = chooseNextAction();                   
                    }
                }
            }
        try { 
            Thread.sleep(1000);
        } catch (Exception e) {
        
        }
        return action;
    }
    
    @Override
	public void loadUtilitySpace(String fileName) {
        utilitySpace = new SimpleUtilitySpace(getNegotiationTemplate().getDomain(), fileName);
        return;
    }  

}
