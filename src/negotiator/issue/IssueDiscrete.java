package negotiator.issue;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Vector;

import negotiator.exceptions.ValueTypeError;
import negotiator.xml.SimpleElement;
/**
*
* @author Koen Hindriks
* 
*/

public class IssueDiscrete extends Issue {
		
	// Class fields
	private int numberOfValues;
	//TODO Use DiscreteValue here??
	//TODO use ArrayList
	//ValueDiscrete issueValues[];
	Vector<ValueDiscrete> issueValues;
	
		
	// Constructors
	
	public IssueDiscrete(String name, int issueNumber, String values[]) {
		super(name, issueNumber);		
		numberOfValues = values.length;
	    issueValues = new Vector<ValueDiscrete>();
	    for(int i=0; i<numberOfValues;i++) {
	        issueValues.add(new ValueDiscrete(values[i]));
	    }
	}	
	
	public IssueDiscrete(String name, int issueNumber, String values[], Objective objParent) {
		super(name, issueNumber, objParent);		
		numberOfValues = values.length;
		issueValues = new Vector<ValueDiscrete>();
	    for(int i=0; i<numberOfValues;i++) {
	        issueValues.add(new ValueDiscrete(values[i]));
	    }
	}
	
	// Class methods
	public int getNumberOfValues() {
	    return numberOfValues;
	}
		
	public ValueDiscrete getValue(int index) {
		return (ValueDiscrete)issueValues.get(index);
	}
	
	public String getStringValue(int index) {
		return ((ValueDiscrete)issueValues.get(index)).getValue();
	}
	    
	public int getValueIndex(String value) {
	    for(int i=0;i<numberOfValues;i++)
	        if(issueValues.get(i).getStringValue().equals(value)) {
	            return i;
	        }
	    return -1;
	}
	
	/**
	 * Removes all values from this Issue.
	 *
	 */
	public void clear(){
		issueValues.clear();
	}
	
	
	public boolean checkInRange(ValueDiscrete val) {
			return (getValueIndex(((ValueDiscrete)val).getValue())!=-1);
	}
	
	/**
	 * Gives an enumeration over all values in this discrete issue.
	 * @return An enumeration containing <code>valueDiscrete</code>
	 */
	public Enumeration getValues(){
		return issueValues.elements();
	}
	
	/**
	 * Returns a SimpleElement representation of this issue.
	 * @return The SimpleElement with this issues attributes
	 */
	public SimpleElement toXML(){
		SimpleElement thisIssue = new SimpleElement("issue");
		thisIssue.setAttribute("name", getName());
		thisIssue.setAttribute("index", ""+getNumber());
		thisIssue.setAttribute("etype", "discrete");
		thisIssue.setAttribute("type", "discrete");
		thisIssue.setAttribute("vtype", "discrete");
		//TODO find some way of putting the items in. Probably in much the same way as weights.
		for(int item_ind = 0; item_ind < numberOfValues; item_ind++){
			SimpleElement thisItem = new SimpleElement("item");
			thisItem.setAttribute("index", "" + (item_ind +1)); //One off error?
			thisItem.setAttribute("value", issueValues.get(item_ind).getStringValue());
			thisIssue.addChildElement(thisItem);
 		}
		return thisIssue;
		
	}
	
}