package negotiator.issue;

import negotiator.xml.SimpleElement;
import misc.RangeInt;

/**
 * Specific type of issue which specifies an integer range [min, max]. An example is the price
 * of a car, assuming that the price can only be specified in whole euro's.
 * 
 * Assumption 1: integer-valued issues have a fixed range, with a lower and upper bound.
 * Assumption 2: value ranges for issue are shared between agents.
 * Assumption 3: step size for integer valued issue is 1.
 *
 * @author Tim Baarslag & Koen Hindriks & Dmytro Tykhonov 
*/
public class IssueInteger extends Issue {

	private RangeInt range;
	
	/**
	 * Create a new issue issue given the name of the issue, its unique ID,
	 * and the value range [min, max].
	 * 
	 * @param name of the issue.
	 * @param issueNumber uniqueID of the isue.
	 * @param min minimum value of the range of values.
	 * @param max maximum value of the range of values.
	 */
	public IssueInteger(String name, int issueNumber, int min, int max) {
		super(name, issueNumber);
		if (min>max)
			System.out.println("Minimum bound exceeds maximum bound in integer-valued issue!");
		range = new RangeInt(min, max);
	}
	
	/**
	 * Create a new issue issue given the name of the issue, its unique ID, its parent,
	 * and the value range [min, max].
	 * 
	 * @param name of the issue.
	 * @param issueNumber uniqueID of the isue.
	 * @param min minimum value of the range of values.
	 * @param max maximum value of the range of values.
	 * @param objParent parent objective of the issue.
	 */
	public IssueInteger(String name, int issueNumber, int min, int max, Objective objParent) {
		super(name, issueNumber, objParent);
		if (min>max)
			System.out.println("Minimum bound exceeds maximum bound in integer-valued issue!");
		range = new RangeInt(min, max);
	}
	
	public boolean checkInRange(Value val) {
		return ( ((ValueInteger)val).getValue() >= range.getLowerbound() && 
				((ValueInteger)val).getValue() <= range.getUpperbound());
	}
	
	/**
	 * @return lowest valid value of the value range.
	 */
	public final int getLowerBound() {
		return range.getLowerbound();
	}
	
	/**
	 * @return highest valid value of the value range.
	 */
	public final int getUpperBound() {
		return range.getUpperbound();
	}
	
	/**
	 * @param upperbound to which the upperbound of the value range must be set.
	 * @return true is valid upperbound.
	 */
	public boolean setUpperBound(int upperbound){
		if (upperbound > range.getLowerbound()){
			range.setUpperbound(upperbound);
			return true;
		} else{
			System.out.println("Minimum bound exceeds maximum bound in integer-valued issue!");
			return false;
		}
		
	}

	/**
	 * @param lowerbound to which the lowerbound of the value range must be set.
	 * @return true is valid lowerbound.
	 */
	public boolean setLowerBound(int lowerbound){
		if (lowerbound < range.getUpperbound()){
			range.setLowerbound(lowerbound);
			return true;
		} else{
			System.out.println("Minimum bound exceeds maximum bound in integer-valued issue!");
			return false;
		}
	}
	
	/**
	 * Returns a SimpleElement representation of this issue.
	 * @return The SimpleElement with this issues attributes
	 */
	public SimpleElement toXML(){
		SimpleElement thisIssue = new SimpleElement("issue");
		thisIssue.setAttribute("name", getName());
		thisIssue.setAttribute("index", ""+getNumber());
		thisIssue.setAttribute("type", "integer");
		thisIssue.setAttribute("etype", "integer");
		thisIssue.setAttribute("vtype", "integer");
		//TODO set range, upperBound and lowerBound items.
		SimpleElement thisRange = new SimpleElement("range");
		thisRange.setAttribute("lowerbound", ""+getLowerBound());
		thisRange.setAttribute("upperbound", ""+getUpperBound());
		thisIssue.addChildElement(thisRange);
		return thisIssue;
		
	}
	
	@Override
	public ISSUETYPE getType() {
		return ISSUETYPE.INTEGER;
	}
	
	@Override
	public String convertToString() {
		return "integer";
	}
}