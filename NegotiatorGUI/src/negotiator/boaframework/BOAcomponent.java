package negotiator.boaframework;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Creates a BOA component consisting of the classname of the component, the
 * type of the component, and all parameters.
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
	private ComponentsEnum type;
	/**
	 * Parameters which should be used to initialize the component upon creation
	 */
	private HashMap<String, BigDecimal> parametervalues;

	private ArrayList<BOAparameter> orgParam;

	/**
	 * Creates a BOA component consisting of the classname of the components,
	 * the type, and the parameters with which the component should be loaded.
	 * 
	 * @param classname
	 *            of the component. Note, this is not checked at all. We now
	 *            also accept absolute file path to a .class file.
	 * @param type
	 *            of the component (for example bidding strategy).
	 * @param strategyParam
	 *            parameters of the component.
	 */
	public BOAcomponent(String classname, ComponentsEnum type,
			HashMap<String, BigDecimal> strategyParam) {
		this.classname = classname;
		this.type = type;
		this.parametervalues = strategyParam;
	}

	/**
	 * Variant of the main constructor in which it is assumed that the component
	 * has no parameters.
	 * 
	 * @param classname
	 *            of the component. Note, this is not checked at all. We now
	 *            also accept absolute file path to a .class file.
	 * @param type
	 *            of the component (for example bidding strategy).
	 */
	public BOAcomponent(String classname, ComponentsEnum type) {
		this.classname = classname;
		this.type = type;
		this.parametervalues = new HashMap<String, BigDecimal>();
	}

	/**
	 * Variant of the main constructor in which it is assumed that the component
	 * has no parameters. In addition a backup is made of the original
	 * BigDecimal specification of the parameters. This is used to avoid
	 * rounding errors in the GUI.
	 * 
	 * @param classname
	 *            of the component. Note, this is not checked at all. We now
	 *            also accept absolute file path to a .class file.
	 * @param type
	 *            of the component (for example bidding strategy).
	 * @param orgParam
	 *            backup of original parameters
	 */
	public BOAcomponent(String classname, ComponentsEnum type,
			ArrayList<BOAparameter> orgParam) {
		this.classname = classname;
		this.type = type;
		this.parametervalues = new HashMap<String, BigDecimal>();
		this.orgParam = orgParam;
	}

	/**
	 * Add a parameter to the set of parameters of this component.
	 * 
	 * @param name
	 *            of the parameter.
	 * @param value
	 *            of the parameter.
	 */
	public void addParameter(String name, BigDecimal value) {
		parametervalues.put(name, value);
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
	public ComponentsEnum getType() {
		return type;
	}

	/**
	 * @return parameters of the component.
	 */
	public HashMap<String, Double> getParameters() {
		return decreaseAccuracy(parametervalues);
	}

	/**
	 * @return original parameters as specified in the GUI.
	 */
	public HashMap<String, BigDecimal> getFullParameters() {
		return parametervalues;
	}

	private HashMap<String, Double> decreaseAccuracy(
			HashMap<String, BigDecimal> parameters) {
		Iterator<Entry<String, BigDecimal>> it = parameters.entrySet()
				.iterator();
		HashMap<String, Double> map = new HashMap<String, Double>();
		while (it.hasNext()) {
			Map.Entry<String, BigDecimal> pairs = (Entry<String, BigDecimal>) it
					.next();
			map.put(pairs.getKey(), pairs.getValue().doubleValue());
		}
		return map;
	}

	/**
	 * @return the full parameters objects used to specify each parameter.
	 */
	public ArrayList<BOAparameter> getOriginalParameters() {
		return orgParam;
	}

	public void setOriginalParameter(ArrayList<BOAparameter> param) {
		this.orgParam = param;
	}

	public String toString() {
		String params = "";
		if (parametervalues.size() > 0) {
			ArrayList<String> keys = new ArrayList<String>(parametervalues.keySet());
			Collections.sort(keys);
			params = "{";
			for (int i = 0; i < keys.size(); i++) {
				params += keys.get(i) + "=" + parametervalues.get(keys.get(i));
				if (i < keys.size() - 1) {
					params += ", ";
				}
			}
			params += "}";
		}
		String shortType = "unknown";
		switch (type) {
		case BIDDINGSTRATEGY:
			shortType = "bs";
			break;
		case ACCEPTANCESTRATEGY:
			shortType = "as";
			break;
		case OPPONENTMODEL:
			shortType = "om";
			break;
		case OMSTRATEGY:
			shortType = "oms";
			break;
		}

		return shortType + ": " + classname + " " + params;
	}
}