package negotiator.issue;

public abstract class Value {

	// Class fields
	ISSUETYPE type;
	
	// Class constructors
	public Value(ISSUETYPE issueType) {
		this.type = issueType;
	}
	
	// Class methods
	public ISSUETYPE getType() {
		return type;
	}
	
	public static Value makeValue(ISSUETYPE type, String s) {
		return new ValueDiscrete(type, s);
	}
	
	public static Value makeValue(ISSUETYPE type, int i) {
		if (type==ISSUETYPE.INTEGER)
			return new ValueInteger(type, i);
		else {
			System.out.println("Mismatch in value type!"); // TO DO: Exception handling.
			return null;
		}
	}
	
	public static Value makeValue(ISSUETYPE type, double i) {
		switch(type) {
		case REAL:
			return new ValueReal(type, i);
		case PRICE:
			return new ValuePrice(type, i);
		default:
			System.out.println("Mismatch in value type!"); // TO DO: Exception handling.
			return null;
		}
	}
	
	public String getStringValue() {
		return "";
	}
	
	public boolean equals(Value val) {
		return (type == val.getType());
	}
	
}
