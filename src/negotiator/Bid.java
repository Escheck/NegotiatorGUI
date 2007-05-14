/*
 * Bid.java
 *
 * Created on November 6, 2006, 10:24 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package negotiator;

// import negotiator.xml.SimpleElement;
import negotiator.exceptions.BidDoesNotExistInDomainException;
import negotiator.issue.Issue;
import negotiator.issue.Value;
import negotiator.issue.ValueDiscrete;
import negotiator.issue.ValueInteger;
import negotiator.issue.ValueReal;
import negotiator.xml.SimpleElement;

/**
 *
 * @author Dmytro Tykhonov & Koen Hindriks
 * 
 */

public final class Bid {
	
	// Class fields
    Domain fDomain;
    private Value fValues[];
    
    // Constructor
    public Bid(Domain pDomain, Value[] pValues) {
        this.fDomain = pDomain; // THIS NEEDS A CHECK!
        
        // Check if indexes are ok
        int nrOfIssues = fDomain.getNumberOfIssues();
        try {
        	if (pValues.length!=nrOfIssues)
        		throw new BidDoesNotExistInDomainException();
        	//FIXME: DT: I am not sure if this check has sense now...Does it?
/*            for(int i=0; i<nrOfIssues; i++) {
                if(!fDomain.getIssue(i).checkInRange(pValues[i]))
                	throw new BidDoesNotExistInDomainException();*/            
        } catch(BidDoesNotExistInDomainException e) {
            System.out.println("Bid not within domain range.");
        }
        this.fValues = pValues;
    }
    
    // Class methods
 //   public Bid(Domain pDomain, SimpleElement pXMLBid) {
 //   	fDomain = pDomain;
 //   	fValues = new Value[pDomain.getNumberOfIssues()];
 //   	SimpleElement[] lXMLIssues = (SimpleElement[])(pXMLBid.getChildByTagName("issues"));
 //   	for(int i=0;i<lXMLIssues.length;i++) {   		
 //   		SimpleElement lXMLItem = 
 //   			(SimpleElement)(lXMLIssues[i].getChildByTagName("item"))[0];
 //   		fValues[Integer.valueOf(lXMLIssues[i].getAttribute("index"))-1] = 
 //   			Integer.valueOf(lXMLItem.getAttribute("index"));
 //   	}
 //   }
    
    public Value getValue(int issueIndex) {
        return fValues[issueIndex];
    }
    public void setValue(int issueIdex, Value pValue) {
    	if(fValues[issueIdex].getType()==pValue.getType()) {
    		fValues[issueIdex] = pValue;
    	} /* TODO Throw an excpetion.
    	else
    		throw new BidDoesNotExistInDomainException();*/
    }
    public String toString() {
        String s = "<< Bid = \n";
        for(int i=0;i<fValues.length;i++)
            s+= fDomain.getIssue(i).getName() + ": " + 
                fValues[i].getStringValue()+ "\n";
        s+= ">>\n";
        return s;
    }
    
    public boolean equals(Bid pBid) {
    	for(int i=0;i<fValues.length;i++) {
    		if(!fValues[i].equals(pBid.getValue(i))) {
    			return false;
    		}
    	}
    	return true;
    }

// DOES NO LONGER APPLY
//    public String indexesToString() {
//    	String result ="";
//    	for(int i=0;i<fValues.length;i++) {
//    		result += String.valueOf(fValues[i])+";";
//    	}
//    	return result;
//    }
    // TODO re-do the save/load XML for bids
    public Bid(Domain pDomain, SimpleElement pXMLBid) {
    	fDomain = pDomain;
    	fValues = new Value[pDomain.getNumberOfIssues()];
    	//fValuesIndexes = new int[pDomain.getNumberOfIssues()];
    	Object[] lXMLIssues = (pXMLBid.getChildByTagName("issue"));
    	Value lValue = null;
    	SimpleElement lXMLItem;
    	String lTmp ;
    	for(int i=0;i<lXMLIssues.length;i++) { 
    		switch(fDomain.getIssue(i).getType()) {
    		case DISCRETE:
    			lXMLItem = 
    				(SimpleElement)(((SimpleElement)lXMLIssues[i]).getChildByTagName("item"))[0];
    			lTmp = lXMLItem.getAttribute("value");
//    			fValuesIndexes[Integer.valueOf(((SimpleElement)lXMLIssues[i]).getAttribute("index"))-1] = 
//    				Integer.valueOf();
    			lValue = new ValueDiscrete(lTmp);
    			break;
    		case INTEGER:
    			lXMLItem = 
    				(SimpleElement)(((SimpleElement)lXMLIssues[i]).getChildByTagName("value"))[0];
    			lTmp = lXMLItem.getText();
    			lValue = new ValueInteger(Integer.valueOf(lTmp));
    			break;
    			
//    		case PRICE:
//    			lXMLItem = 
//    				(SimpleElement)(((SimpleElement)lXMLIssues[i]).getChildByTagName("value"))[0];
//    			lTmp = lXMLItem.getText();
//    			lValue = new ValuePrice(Double.valueOf(lTmp));
//    			break;
    		case REAL:
    			lXMLItem = 
    				(SimpleElement)(((SimpleElement)lXMLIssues[i]).getChildByTagName("value"))[0];
    			lTmp = lXMLItem.getText();
    			lValue = new ValueReal(Double.valueOf(lTmp));
    			break;
    			
        		//TODO:COMPLETED: DT implement Bid(Domain, SimpleElement) in Bid for the rest of the issue/value types
    			//TODO: DT add bid validation w.r.t. Domain, throw an exception BidDoesNotExist 
    		}//switch
    		fValues[i] = lValue;
    	}
    }
    
    public SimpleElement toXML() {
    	SimpleElement lXMLBid = new SimpleElement("bid");
    	for(int i=0;i<fValues.length;i++) {
    		SimpleElement lXMLIssue = new SimpleElement("issue");
    		lXMLIssue.setAttribute("type", Issue.convertToString(fDomain.getIssue(i).getType()));
    		lXMLIssue.setAttribute("index", String.valueOf(i+1));
    		lXMLBid.addChildElement(lXMLIssue);
    		SimpleElement lXMLItem=null;
    		
    		switch(fValues[i].getType()) {
    		case DISCRETE:
    			ValueDiscrete lDiscVal = (ValueDiscrete)(fValues[i]);    			
        		lXMLItem = new SimpleElement("item");
        		lXMLItem.setAttribute("value", lDiscVal.getValue());
        		break;
        		//TODO: COMPLETE DT implement toXML method in Bid for the rest of the issue/value types
    		case INTEGER:
    			ValueInteger lIntVal = (ValueInteger)(fValues[i]);    			
        		lXMLItem = new SimpleElement("value");
        		lXMLItem.setText(String.valueOf(lIntVal.getValue()));
        		break;
    		case REAL:
    			ValueReal lRealVal = (ValueReal)(fValues[i]);    			
        		lXMLItem = new SimpleElement("value");
        		lXMLItem.setText(String.valueOf(lRealVal.getValue()));
        		break;
//    		case PRICE:
//    			ValueReal lPriceVal = (ValueReal)(fValues[i]);    			
//        		lXMLValue = new SimpleElement("value");
//        		lXMLValue.setText(String.valueOf(lPriceVal.getValue()));
//        		break;
    		}
    		lXMLIssue.addChildElement(lXMLItem);
    	}
    	return lXMLBid;
    }
    //TODO can we save indexes to Strings?
/*    public String indexesToString() {
    	String result ="";
    	for(int i=0;i<fValuesIndexes.length;i++) {
    		result += String.valueOf(fValuesIndexes[i])+";";
    	}
    	return result;
    }*/
}
