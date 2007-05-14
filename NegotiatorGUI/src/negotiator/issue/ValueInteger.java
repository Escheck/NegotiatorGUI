package negotiator.issue;

public class ValueInteger implements Value {
	
	//	 Class fields
	int value;
	
	// Constructor
	public ValueInteger(int i) {
		value = i;
	}
	
	// Class methods
	public ISSUETYPE getType() {
		return ISSUETYPE.INTEGER;
	}
	
	public int getValue() {
		return value;
	}
	
	public String getStringValue() {
		return Integer.toString(value);
	}
	
	public boolean equals(Object pObject) {
		if(pObject instanceof ValueInteger) {
			ValueInteger val = (ValueInteger)pObject;
			return  value==val.getValue();
		} else 
			if(pObject instanceof Integer){
				int val = (Integer) pObject;
				return value == val;
			} else				
			return false;
	}

}
