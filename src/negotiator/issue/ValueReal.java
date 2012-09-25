package negotiator.issue;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author Dmytro Tykhonov
 */
@XmlRootElement
public class ValueReal extends Value {

	@XmlAttribute
	private double value;
	
	public ValueReal() { }
	
	public ValueReal(double r) {
		value = r;
	}
	
	public ISSUETYPE getType() {
		return ISSUETYPE.REAL;
	}
	
	public double getValue() {
		return value;
	}
	
	public void setValue(double value) {
		this.value = value;
	}
	
	public String toString() {
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