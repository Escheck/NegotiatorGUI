package negotiator.boaframework;

import java.io.Serializable;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import negotiator.boaframework.repository.BOAagentRepository;
import negotiator.boaframework.repository.BOArepItem;

/**
 * Creates a BOA component consisting of the classname of the component, the
 * type of the component, and all parameters. FIXME this creates nothing. It
 * seems just to contain info that can be used to createFrom a BOA component.
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

	/**
	 * Creates a BOA component consisting of the classname of the components,
	 * the type, and the parameters with which the component should be loaded.
	 * 
	 * @param classname
	 *            of the component. Note, this is not checked at all. We now
	 *            also accept absolute file path to a .class file.
	 * @param type
	 *            of the component (for example bidding strategy).
	 * @param values
	 *            parameters of the component.
	 */
	public BOAcomponent(String classname, ComponentsEnum type,
			HashMap<String, BigDecimal> values) {
		if (values == null) {
			throw new NullPointerException("values==null");
		}
		this.classname = classname;
		this.type = type;
		this.parametervalues = values;
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

	public String toString() {
		String params = "";
		if (parametervalues.size() > 0) {
			ArrayList<String> keys = new ArrayList<String>(
					parametervalues.keySet());
			Collections.sort(keys);
			params = "{";
			for (int i = 0; i < keys.size(); i++) {
				// use doubleValue to keep #digits in string lower
				params += keys.get(i) + "="
						+ parametervalues.get(keys.get(i)).doubleValue();
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

	/**
	 * Fetches the original parameters from (a temporary instance of) the actual
	 * component.
	 * 
	 * @return
	 * @throws MalformedURLException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws ClassNotFoundException
	 */
	public Set<BOAparameter> getOriginalParameters()
			throws MalformedURLException, InstantiationException,
			IllegalAccessException, ClassNotFoundException {
		return getRepItem().getInstance().getParameters();
	}

	/**
	 * Find back this in the repository. CHECK why don't we use the repository
	 * item all along?
	 * 
	 * @return
	 */
	private BOArepItem getRepItem() {
		BOAagentRepository repo = BOAagentRepository.getInstance();
		switch (type) {
		case ACCEPTANCESTRATEGY:
			return repo.getAcceptanceStrategyRepItem(classname);
		case BIDDINGSTRATEGY:
			return repo.getBiddingStrategyRepItem(classname);
		case OMSTRATEGY:
			return repo.getOpponentModelStrategyRepItem(classname);
		case OPPONENTMODEL:
			return repo.getOpponentModelRepItem(classname);
		default:
			throw new IllegalStateException(
					"BOAcomponent with unknown type encountered:" + type);
		}
	}
}