package negotiator.issue;

public class ValueInteger extends Value {
	
	//	 Class fields
	int value;
	
	// Constructor
	public ValueInteger(ISSUETYPE type, int i) {
		super(type);
		value = i;
	}
	
	// Class methods
	public int getValue() {
		return value;
	}
	
	public String getStringValue() {
		return Integer.toString(value);
	}
	
	public boolean equals(ValueInteger val) {
		return (type==val.getType() && value==val.getValue());
	}

}
