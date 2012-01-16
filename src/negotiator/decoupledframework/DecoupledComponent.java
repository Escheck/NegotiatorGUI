package negotiator.decoupledframework;

import java.io.Serializable;
import java.util.HashMap;

/**
 * Creates a decoupled component consisting of the classname of the component,
 * the type of the component, and all parameters.
 * 
 * Please report bugs to author.
 * 
 * @author Mark Hendrikx (m.j.c.hendrikx@student.tudelft.nl)
 * @version 16-01-12
 */
public class DecoupledComponent implements Serializable {
	
	private static final long serialVersionUID = 9055936213274664445L;
	private String classname;
	private String type;
	private HashMap<String, Double> parameters;

	public DecoupledComponent(String classname, String type, HashMap<String, Double> strategyParam) {
		this.classname = classname;
		this.type = type;
		this.parameters = strategyParam;
	}
	
	public DecoupledComponent(String classname, String type) {
		this.classname = classname;
		this.type = type;
		this.parameters = new HashMap<String, Double>();
	}
	
	public void addParameter(String name, double value) {
		parameters.put(name, value);
	}

	public String getClassname() {
		return classname;
	}

	public void setClassname(String classname) {
		this.classname = classname;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public HashMap<String, Double> getParameters() {
		return parameters;
	}

	public void setParameters(HashMap<String, Double> parameters) {
		this.parameters = parameters;
	}
	
	public String toString() {
		String params = "";
		if (parameters.size() > 0) {
			params = parameters.toString();
		}
		return type + ": " + classname + " " + params;
	}
}