package negotiator.issue;

import java.util.ArrayList;

import negotiator.exceptions.ValueTypeError;

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
	ValueDiscrete issueValues[];
	
		
	// Constructors
	
	public IssueDiscrete(String name, int issueNumber, String values[]) {
		super(name, issueNumber);		
		numberOfValues = values.length;
	    issueValues = new ValueDiscrete[numberOfValues];
	    for(int i=0; i<numberOfValues;i++) {
	        issueValues[i] = new ValueDiscrete(values[i]);
	    }
	}	
	
	public IssueDiscrete(String name, int issueNumber, String values[], Objective objParent) {
		super(name, issueNumber, objParent);		
		numberOfValues = values.length;
	    issueValues = new ValueDiscrete[numberOfValues];
	    for(int i=0; i<numberOfValues;i++) {
	        issueValues[i] = new ValueDiscrete(values[i]);
	    }
	}
	
	// Class methods
	public int getNumberOfValues() {
	    return numberOfValues;
	}
		
	public ValueDiscrete getValue(int index) {
		return (ValueDiscrete)issueValues[index];
	}
	
	public String getStringValue(int index) {
		return ((ValueDiscrete)issueValues[index]).getValue();
	}
	    
	public int getValueIndex(String value) {
	    for(int i=0;i<numberOfValues;i++)
	        if(issueValues[i].getStringValue().equals(value)) {
	            return i;
	        }
	    return -1;
	}
	
	public boolean checkInRange(ValueDiscrete val) {
			return (getValueIndex(((ValueDiscrete)val).getValue())!=-1);
	}
	
}