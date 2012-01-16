package misc;


/**
 * This is a tuple class which is used to pass on a target utility range 
 * @author Alex Dirkzwager
 *
 */
public class Range {

	private double lowerbound;
	private double upperbound;
	
	public Range(double lowerbound, double upperbound){
		this.lowerbound = lowerbound;
		this.upperbound = upperbound;
	}
	
	public double getUpperbound(){
		return upperbound;
	}
	
	public double getLowerbound(){
		return lowerbound;
	}
	
	public void setUpperbound(double ubound){
		upperbound = ubound;
	}
	
	public void setLowerbound(double lbound){
		lowerbound = lbound;
	}
}
