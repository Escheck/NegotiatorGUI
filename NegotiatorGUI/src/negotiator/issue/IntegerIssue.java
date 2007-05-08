package negotiator.issue;

import negotiator.exceptions.ValueTypeError;

/**
*
* @author Koen Hindriks
* 
*/

public class IntegerIssue extends Issue {
	
	// Class fields
	// Assumption 1: integer-valued issues have a fixed range, with a lower and upper bound.
	// Assumption 2: value ranges for issue are shared between agents.
	// Assumption 3: step size for integer valued issue is 1.
	RangeInt range;
	
	// Constructor
	public IntegerIssue(String name, int issueNumber, ISSUETYPE issueType, int min, int max) {
		super(name, issueNumber, issueType);
		if (issueType!=ISSUETYPE.INTEGER)
			System.out.println("Issue has wrong type!"); // TO DO: Define excecption.
		if (min>max)
			System.out.println("Minimum bound exceeds maximum bound in integer-valued issue!");
		range = new RangeInt(min, max);
	}
	
	// Class method
	public boolean checkInRange(Value val) throws ValueTypeError {
		if (val instanceof ValueInteger)
			return ( ((ValueInteger)val).getValue() >= range.getLowerBound() && ((ValueInteger)val).getValue() <= range.getUpperBound());
		else throw new ValueTypeError();
	}
	
}