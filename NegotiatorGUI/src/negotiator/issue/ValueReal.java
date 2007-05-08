package negotiator.issue;

public class ValueReal extends Value {
	
	// Class fields
	double value;
	
	// Constructor
	public ValueReal(ISSUETYPE type, double r) {
		super(type);
		value = r;
	}
	
	// Class methods
	public double getValue() {
		return value;
	}
	
	public String getStringValue() {
		return Double.toString(value);
	}
	
	public boolean equals(ValueReal val) {
		return (type==val.getType() && value==val.getValue());
	}

}
