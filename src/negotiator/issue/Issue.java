/*
 * Issue.java
 *
 * Created on November 6, 2006, 1:10 PM
 *
 */

package negotiator.issue;

import negotiator.exceptions.ValueTypeError;

/**
 *
 * @author Koen Hindriks
 * 
 */

public class Issue {
    
    // Class fields
	private String name;
	private ISSUETYPE type;
	int issueNumber;
    
    // Constructor
    public Issue(String name, int issueNumber, ISSUETYPE issueType) {
        this.name = name;
        this.issueNumber = issueNumber;
        this.type = issueType;
    }
    
    // Class methods
    public String getName() {
        return name;
    }
    
    public ISSUETYPE getType() {
    	return type;
    }
    
    public static ISSUETYPE convertToType(String typeString) {
    	if (typeString.equalsIgnoreCase("price"))
        	return ISSUETYPE.PRICE;
        else if (typeString.equalsIgnoreCase("integer"))
        	return ISSUETYPE.INTEGER;
        else if (typeString.equalsIgnoreCase("real"))
        	return ISSUETYPE.REAL;
        else if (typeString.equalsIgnoreCase("discrete"))
        	return ISSUETYPE.DISCRETE;
        else {
        	// Type specified incorrectly!
        	System.out.println("Type specified incorrectly.");
        	// For now return DISCRETE type.
        	return ISSUETYPE.DISCRETE;
        	// TO DO: Define corresponding exception.
        }
    }
    
    public boolean checkInRange(Value val) throws ValueTypeError {
    	return false;
    }
    
    //  Inner range classes for integers and reals
	protected class RangeInt {
		
		// Class fields
		int lowerBound;
		int upperBound;
		
		// Constructor
		public RangeInt(int min, int max) {
			if (min<max)
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
			if (min<max)
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