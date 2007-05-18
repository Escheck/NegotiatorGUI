package negotiator.issue;

/**
*
* @author Koen Hindriks
* 
*/

public class IssueInteger extends Issue {
	
	// Class fields
	// Assumption 1: integer-valued issues have a fixed range, with a lower and upper bound.
	// Assumption 2: value ranges for issue are shared between agents.
	// Assumption 3: step size for integer valued issue is 1.
	RangeInt range;
	
	// Constructors
	public IssueInteger(String name, int issueNumber, int min, int max) {
		super(name, issueNumber);
		if (min>max)
			System.out.println("Minimum bound exceeds maximum bound in integer-valued issue!");
		range = new RangeInt(min, max);
	}
	
	public IssueInteger(String name, int issueNumber, int min, int max, Objective objParent) {
		super(name, issueNumber, objParent);
		if (min>max)
			System.out.println("Minimum bound exceeds maximum bound in integer-valued issue!");
		range = new RangeInt(min, max);
	}
	
	// Class method
	public boolean checkInRange(ValueInteger val) {
			return ( ((ValueInteger)val).getValue() >= range.getLowerBound() && ((ValueInteger)val).getValue() <= range.getUpperBound());
	}
	
	public final int getLowerBound() {
		return range.getLowerBound();
	}
	
	public final int getUpperBound() {
		return range.getUpperBound();
	}
	
}