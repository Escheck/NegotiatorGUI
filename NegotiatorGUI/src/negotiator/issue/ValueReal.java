package negotiator.issue;

public class ValueReal implements Value {
	
	// Class fields
	double value;
	
	// Constructor
	public ValueReal(double r) {
		value = r;
	}
	
	// Class methods
	public ISSUETYPE getType() {
		return ISSUETYPE.REAL;
	}
	
	public double getValue() {
		return value;
	}
	
	public String getStringValue() {
		return Double.toString(value);
	}
	
	public boolean equals(Object pObject) {
		if(pObject instanceof ValueReal) {
			ValueReal val = (ValueReal)pObject;
			return  value==val.getValue();
		} else 
			if(pObject instanceof Double){
				double val = (Double) pObject;
				return value == val;
			} else
			return false;
	}

}
