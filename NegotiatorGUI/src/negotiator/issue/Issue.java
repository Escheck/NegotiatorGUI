/*
 * Issue.java
 *
 * Created on November 6, 2006, 1:10 PM
 *
 */

package negotiator.issue;

import negotiator.xml.SimpleElement;

/**
 * Class {@link Issue} represents a negotiation issue to be settled in a negotiation. 
 * Issues in a domain are identified by unique <code>index</code> field.
 *
 * @author Tim Baarslag & Dmytro Tykhonov
 * 
 */
public abstract class Issue extends Objective {
    
    // Constructor
    public Issue(String name, int issueNumber) {
        super(null, name, issueNumber);
    }
    
    public Issue (String name, int issueNumber, Objective parent) {
    	super(parent, name, issueNumber);
    }
    
    public abstract ISSUETYPE getType();
    
	//
    /**
     * Converts ISSUETYPE enumeration to a string. Reverse functiong for 
     * convertToType method. Used to save issue to XML file.
     * 
     * Remark: Added by Dmytro on 09/05/2007 
     * 
     * @param pType - issue type
     * @return corresponding string representation
     */
    public static String convertToString(ISSUETYPE pType) {
    	//If typeString is null for some reason (i.e. not spceified in the XML template
    	// then we assume that we have DISCRETE type
    	switch(pType) {
    	case DISCRETE:
    		return "discrete";
// TODO: Remove    		
//    	case PRICE:
//    		return "price";		
    	case INTEGER:
    		return "integer";		
    	case REAL:
    		return "real";	
    	default: return "";
    	}
       	// TODO: Define corresponding exception.

    }
    
    public boolean checkInRange(Value val) {
    	return false;
    }
    
    //  Inner range classes for integers and reals
	protected class RangeInt {
		
		// Class fields
		int lowerBound;
		int upperBound;
		
		// Constructor
		public RangeInt(int min, int max) {
			if (min>max)
				System.out.println("Lower bound in real range exceeds upper bound!");
				// TO DO: Define exception.
			if (min==max) // issue warning.
				System.out.println("Lower bound equals upper bound in range.");
			lowerBound = min;
			upperBound = max;
		}
		
		// Class methods
		public int getLowerBound() {
			return lowerBound;
		}
		
		public int getUpperBound() {
			return upperBound;
		}

	}
	
	/**
	 * Overrides addChild from Objective to do nothing, since Issues can't have children. This
	 * method simply returns without doing anything. 
	 * @param newObjective gets negated.
	 */
	public void addChild(Objective newObjective) { }
	
	/**
	 * Returns a SimpleElement representation of this issue.
	 * @return The SimpleElement with this issues name and index.
	 */
	public SimpleElement toXML(){
		SimpleElement thisIssue = new SimpleElement("issue");
		thisIssue.setAttribute("name", getName());
		thisIssue.setAttribute("index", ""+getNumber());
		return thisIssue;
		
	}
}