/*
 * Issue.java
 *
 * Created on November 6, 2006, 1:10 PM
 *
 */

package negotiator.issue;

/**
 *
 * @author Koen Hindriks
 * 
 */

public class Issue extends Objective {
    
    // Class fields
	private String name;
	int issueNumber;
    
    // Constructor
    public Issue(String name, int issueNumber) {
        this.name = name;
        this.issueNumber = issueNumber;
    }
    
    // Class methods
    public String getName() {
        return name;
    }
    
    public ISSUETYPE getType() {
    	if (this instanceof IssueDiscrete)
    		return ISSUETYPE.DISCRETE;
// TODO: Remove.
//    	else if (this instanceof IssueDiscreteWCost)
//    		return ISSUETYPE.DISCRETEWCOST;
    	else if (this instanceof IssueInteger)
    		return ISSUETYPE.INTEGER;
    	else if (this instanceof IssueReal)
    		return ISSUETYPE.REAL;
// TODO: Remove.
//    	else if (this instanceof IssuePrice)
//    		return ISSUETYPE.PRICE;
    	else return null;
    }
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
	
	protected class RangeReal {
		
		// Class fields
		double lowerBound;
		double upperBound;
		
		// Constructor
		protected RangeReal(double min, double max) {
			if (min>max)
				System.out.println("Lower bound in real range exceeds upper bound!");
				// TO DO: Define exception.
			if (min==max) // issue warning.
				System.out.println("Lower bound equals upper bound in range.");
			lowerBound = min;
			upperBound = max;
		}
		
		// Class methods
		protected double getLowerBound() {
			return lowerBound;
		}
		
		protected double getUpperBound() {
			return upperBound;
		}

	}

}