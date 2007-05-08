package negotiator.issue;

import com.sun.org.omg.SendingContext.CodeBasePackage.ValueDescSeqHelper;

public class ValueDiscrete extends Value {

	// Class fields
	String value;
	
	// Constructor
	//TODO Remove ISSUETYPE
	public ValueDiscrete(ISSUETYPE type, String s) {
		super(type);
		value = s;
	}
	
	// Class methods
	public String getValue() {
		return value;
	}
	
	public String getStringValue() {
		return value;
	}
	
	public boolean equals(Object pObject) {
		if(pObject instanceof ValueDiscrete) {
			ValueDiscrete val = (ValueDiscrete)pObject;
			return (type==val.getType() && value==val.getValue());
		} else 
			if(pObject instanceof String ){
				String val = (String) pObject;
				return (value.equals(val));
			} else				
			return false;
	}
	
}
