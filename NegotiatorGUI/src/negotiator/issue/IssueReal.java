package negotiator.issue;

/**
*
* @author Koen Hindriks
* 
*/

public class IssueReal extends Issue {
	
	// Class fields
	// Assumption 1: real-valued issues have a fixed range, with a lower and upper bound.
	// Assumption 2: value ranges for issue are shared between agents.
	RangeReal range;
	//use this value for discrete operations in the analysis
	//TODO make it template parameter
	private int fNumberOfDiscretizationSteps = 100;
	// Constructor
	public IssueReal(String name, int issueNumber, double min, double max) {
		super(name, issueNumber);
		range = new RangeReal(min, max);
	}
	
	// Class method
	public boolean checkInRange(ValueReal val) {
			return ( ((ValueReal)val).getValue() >= range.getLowerBound() && ((ValueReal)val).getValue() <= range.getUpperBound());
	}
	
	public final double getLowerBound() {
		return range.getLowerBound();
	}
	
	public final double getUpperBound() {
		return range.getUpperBound();
	}

	public int getNumberOfDiscretizationSteps() {
		return fNumberOfDiscretizationSteps;
	}

	public void setNumberOfDiscretizationSteps(int numberOfDiscretizationSteps) {
		fNumberOfDiscretizationSteps = numberOfDiscretizationSteps;
	}
}
