package misc;

/**
 * This is a tuple class which is used to pass on an integer range.
 */
public class RangeInt {
	
	// Class fields
	int lowerbound;
	int upperbound;
	
	// Constructor
	public RangeInt(int min, int max) {
		if (min>max)
			System.out.println("Lower bound in real range exceeds upper bound!");
		if (min==max) // issue warning.
			System.out.println("Lower bound equals upper bound in range.");
		lowerbound = min;
		upperbound = max;
	}
	
	public int getLowerbound() {
		return lowerbound;
	}
	
	public int getUpperbound() {
		return upperbound;
	}
	
	public void setUpperbound(int ubound){
		upperbound = ubound;
	}
	
	public void setLowerbound(int lbound){
		lowerbound = lbound;
	}
}