package negotiator.issue;

import negotiator.exceptions.ValueTypeError;

/**
*
* @author Koen Hindriks
* 
*/

public class PriceIssue extends Issue {
	
	// Class fields
	// Assumption 1: value ranges for price are shared between agents.
	// Assumption 2: price is a real-valued issue.
	RangeReal range;
	
	// Constructor
	public PriceIssue(String name, int issueNumber, ISSUETYPE issueType, double min, double max) {
		super(name, issueNumber, issueType);
		if (issueType!=ISSUETYPE.PRICE)
			System.out.println("Issue has wrong type!"); // TO DO: Define excecption.
		range = new RangeReal(min, max);
	}
	
	// Class method
	public boolean checkInRange(Value val) throws ValueTypeError {
		if (val instanceof ValuePrice)
			return ( ((ValuePrice)val).getValue() >= range.getLowerBound() && ((ValuePrice)val).getValue() <= range.getUpperBound());
		else throw new ValueTypeError();
	}
	
}