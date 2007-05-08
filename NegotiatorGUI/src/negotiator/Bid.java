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
import negotiator.exceptions.ValueTypeError;
import negotiator.issue.Value;
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
            for(int i=0; i<nrOfIssues; i++) {
                if(!fDomain.getIssue(i).checkInRange(pValues[i]))
                	throw new BidDoesNotExistInDomainException();
            }
        } catch(ValueTypeError e) {
            System.out.println("Values do not match value type.");
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
/*    public Bid(Domain pDomain, SimpleElement pXMLBid) {
    	fDomain = pDomain;
    	fValuesIndexes = new int[pDomain.getNumberOfIssues()];
    	Object[] lXMLIssues = (pXMLBid.getChildByTagName("issues"));
    	for(int i=0;i<lXMLIssues.length;i++) {   		
    		SimpleElement lXMLItem = 
    			(SimpleElement)(((SimpleElement)lXMLIssues[i]).getChildByTagName("item"))[0];
    		fValuesIndexes[Integer.valueOf(((SimpleElement)lXMLIssues[i]).getAttribute("index"))-1] = 
    			Integer.valueOf(lXMLItem.getAttribute("index"));    		
    	}
    }
    
    
    public SimpleElement toXML() {
    	SimpleElement lXMLBid = new SimpleElement("bid");
    	for(int i=0;i<fValuesIndexes.length;i++) {
    		SimpleElement lXMLIssue = new SimpleElement("issue");
    		lXMLIssue.setAttribute("index", String.valueOf(i+1));
    		lXMLBid.addChildElement(lXMLIssue);
    		SimpleElement lXMLItem = new SimpleElement("item");
    		lXMLItem.setAttribute("index", String.valueOf(fValuesIndexes[i]+1));
    		lXMLIssue.addChildElement(lXMLItem);
    	}
    	return lXMLBid;
    }*/
    //TODO can we save indexes to Strings?
/*    public String indexesToString() {
    	String result ="";
    	for(int i=0;i<fValuesIndexes.length;i++) {
    		result += String.valueOf(fValuesIndexes[i])+";";
    	}
    	return result;
    }*/
}
