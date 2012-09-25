package negotiator.issue;

import negotiator.xml.SimpleElement;
import misc.RangeInt;

/**
 * Assumption 1: integer-valued issues have a fixed range, with a lower and upper bound.
 * Assumption 2: value ranges for issue are shared between agents.
 * Assumption 3: step size for integer valued issue is 1.
 *
 * @author Tim Baarslag & Koen Hindriks & Dmytro Tykhonov 
*/
public class IssueInteger extends Issue {

	private RangeInt range;
	
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
	
	public boolean checkInRange(ValueInteger val) {
		return ( ((ValueInteger)val).getValue() >= range.getLowerbound() && 
				((ValueInteger)val).getValue() <= range.getUpperbound());
	}
	
	public final int getLowerBound() {
		return range.getLowerbound();
	}
	
	public final int getUpperBound() {
		return range.getUpperbound();
	}
	
	public final boolean setUpperBound(int up){
		if(up > range.getLowerbound()){
			range.setUpperbound(up);
			return true;
		}else{
			System.out.println("Minimum bound exceeds maximum bound in integer-valued issue!");
			return false;
		}
		
	}
	
	public final boolean setLowerBound(int lo){
		if(lo < range.getUpperbound()){
			range.setLowerbound(lo);
			return true;
		}else{
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