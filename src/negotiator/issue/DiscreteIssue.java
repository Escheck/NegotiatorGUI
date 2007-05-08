package negotiator.issue;

import java.util.ArrayList;

import negotiator.exceptions.ValueTypeError;

/**
*
* @author Koen Hindriks
* 
*/

public class DiscreteIssue extends Issue {
		
	// Class fields
	private int numberOfValues;
	//TODO Use DiscreteValue here??
	//TODO use ArrayList
	Value issueValues[];
	
		
	// Constructor

	// TODO: check why do we need issueType in this constructor and associated check!!! Remove issueType
	public DiscreteIssue(String name, int issueNumber, ISSUETYPE issueType, String values[]) {
		super(name, issueNumber, issueType);
		if (issueType!=ISSUETYPE.DISCRETE)
			System.out.println("Issue has wrong type!"); // TODO: Define excecption.
		numberOfValues = values.length;
	    issueValues = new ValueDiscrete[numberOfValues];
	    for(int i=0; i<numberOfValues;i++) {
	        issueValues[i] = new ValueDiscrete(issueType, values[i]);
	    }
	}
		
	
	// Class methods
	public int getNumberOfValues() {
	    return numberOfValues;
	}
		
	public String getValue(int index) {
		return ((ValueDiscrete)issueValues[index]).getValue();
	}
	    
	public int getValueIndex(String value) {
	    for(int i=0;i<numberOfValues;i++)
	        if(issueValues[i].getStringValue().equals(value)) {
	            return i;
	        }
	    return -1;
	}
	
	public boolean checkInRange(Value val) throws ValueTypeError {
		if (val instanceof ValueDiscrete)
			return (getValueIndex(((ValueDiscrete)val).getValue())!=-1);
		else throw new ValueTypeError();
	}
	
}