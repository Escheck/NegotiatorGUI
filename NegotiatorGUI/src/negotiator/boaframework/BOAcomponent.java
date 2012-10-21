package negotiator.boaframework;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Creates a BOA component consisting of the classname of the component,
 * the type of the component, and all parameters.
 * 
 * Please report bugs to author.
 * 
 * @author Mark Hendrikx (m.j.c.hendrikx@student.tudelft.nl)
 * @version 16-01-12
 */
public class BOAcomponent implements Serializable {
	
	private static final long serialVersionUID = 9055936213274664445L;
	/** Classname of the component */
	private String classname;
	/** Type of the component, for example "as" for acceptance condition */
	private String type;
	/** Parameters which should be used to initialize the component upon creation */
	private HashMap<String, BigDecimal> parameters;

	/**
	 * Creates a BOA component consisting of the classname of the components, the type,
	 * and the parameters with which the component should be loaded.
	 * 
	 * @param classname of the component.
	 * @param type of the component (for example bidding strategy).
	 * @param strategyParam parameters of the component.
	 */
	public BOAcomponent(String classname, String type, HashMap<String, BigDecimal> strategyParam) {
		this.classname = classname;
		this.type = type;
		this.parameters = strategyParam;
	}
	
	/**
	 * Variant of the main constructor in which it is assumed that the component has no
	 * parameters.
	 * 
	 * @param classname of the component.
	 * @param type of the component (for example bidding strategy).
	 * @param type
	 */
	public BOAcomponent(String classname, String type) {
		this.classname = classname;
		this.type = type;
		this.parameters = new HashMap<String, BigDecimal>();
	}
	
	/**
	 * Add a parameter to the set of parameters of this component.
	 * 
	 * @param name of the parameter.
	 * @param value of the parameter.
	 */
	public void addParameter(String name, BigDecimal value) {
		parameters.put(name, value);
	}

	/**
	 * @return name of the class of the component.
	 */
	public String getClassname() {
		return classname;
	}

	/**
	 * @return type of the component.
	 */
	public String getType() {
		return type;
	}
	
	/**
	 * @return parameters of the component.
	 */
	public HashMap<String, Double> getParameters() {
		return decreaseAccuracy(parameters);
	}
	
	private HashMap<String, Double> decreaseAccuracy(HashMap<String, BigDecimal> parameters) {
		Iterator<Entry<String, BigDecimal>> it = parameters.entrySet().iterator();
		HashMap<String, Double> map = new HashMap<String, Double>();
	    while (it.hasNext()) {
	        Map.Entry<String, BigDecimal> pairs = (Entry<String, BigDecimal>)it.next();
	        map.put(pairs.getKey(), pairs.getValue().doubleValue());
	    }
	    return map;
	}

	public String toString() {
		String params = "";
		if (parameters.size() > 0) {
			params = parameters.toString();
		}
		return type + ": " + classname + " " + params;
	}
}