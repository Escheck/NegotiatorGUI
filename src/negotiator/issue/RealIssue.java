package negotiator.issue;

import negotiator.exceptions.ValueTypeError;

/**
*
* @author Koen Hindriks
* 
*/

public class RealIssue extends Issue {
	
	// Class fields
	// Assumption 1: real-valued issues have a fixed range, with a lower and upper bound.
	// Assumption 2: value ranges for issue are shared between agents.
	RangeReal range;
	
	// Constructor
	public RealIssue(String name, int issueNumber, ISSUETYPE issueType, double min, double max) {
		super(name, issueNumber, issueType);
		if (issueType!=ISSUETYPE.REAL)
			System.out.println("Issue has wrong type!"); // TO DO: Define excecption.
		range = new RangeReal(min, max);
	}
	
	// Class method
	public boolean checkInRange(Value val) throws ValueTypeError {
		if (val instanceof ValueReal)
			return ( ((ValueReal)val).getValue() >= range.getLowerBound() && ((ValueReal)val).getValue() <= range.getUpperBound());
		else throw new ValueTypeError();
	}
}
