package negotiator.issue;

public class ValuePrice extends Value {
	
	// Class fields
	double value;
	
	// Constructor
	public ValuePrice(ISSUETYPE type, double p) {
		super(type);
		value = p;
	}
	
	// Class methods
	public double getValue() {
		return value;
	}
	
	public String getStringValue() {
		return Double.toString(value);
	}
	
	public boolean equals(ValuePrice val) {
		return (type==val.getType() && value==val.getValue());
	}

}
