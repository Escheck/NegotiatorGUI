package negotiator.boaframework;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.HashSet;

import misc.Pair;
import negotiator.boaframework.repository.BOAagentRepository;
import negotiator.tournament.Tournament;

/**
 * I think this stores parameter values for generating BOA agent settings, for
 * use in {@link Tournament}. However, these are also stored in the
 * {@link BOAagentRepository} and in {@link BOAcomponent}, where we would expect
 * no such generating stuff.
 * 
 * Class used to store the information of a BOA parameter. If lower and higher
 * bound is used, it also requires a step size and all in-between values in the
 * range are being generated immediately. Basically, what is stored is
 * [Lowerbound:Stepsize:Upperbound]. [1:5:20] = {1, 6, 11, 16}.
 * 
 * Please report bugs to author.
 * 
 * @author Mark Hendrikx (m.j.c.hendrikx@student.tudelft.nl)
 * @version 16-01-12
 */
public class BOAparameter implements Serializable {

	private static final long serialVersionUID = 2555736049221913613L;
	/** Name of the parameter. */
	private String name;
	/** Lowerbound of the specified range. */
	private BigDecimal low;
	/** Upperbound of the specified range. */
	private BigDecimal high;
	/** Step size of the specified range. */
	private BigDecimal step;
	/** set of separate values which the specified variable should attain */
	private HashSet<Pair<String, BigDecimal>> valuePairs;
	/** description of the parameter */
	private String description;

	/**
	 * Describes a parameter for a BOA component. A parameter consists of a
	 * name, and the possible values for the parameter.
	 * 
	 * @param name
	 *            of the parameter.
	 * @param low
	 *            value of the range.
	 * @param high
	 *            value of the range.
	 * @param step
	 *            of the range.
	 */
	public BOAparameter(String name, BigDecimal low, BigDecimal high,
			BigDecimal step) {
		this.name = name;
		this.low = low;
		this.high = high;
		this.step = step;
		description = "";
		generatePairs();
	}

	/**
	 * Describes a parameter for a BOA component. A parameter consists of a name
	 * and a description. The value of the parameter is set to a default value.
	 * 
	 * @param name
	 * @param defaultValue
	 * @param description
	 */
	public BOAparameter(String name, BigDecimal defaultValue, String description) {
		this.name = name;
		this.description = description;
		this.low = defaultValue;
		this.high = defaultValue;
		this.step = BigDecimal.ONE;
		generatePairs();
	}

	/**
	 * Describes a parameter for a decoupled component. A parameter consists of
	 * a name, a description, and the possible values for the parameter.
	 * 
	 * @param name
	 *            of the parameter.
	 * @param low
	 *            value of the range.
	 * @param high
	 *            value of the range.
	 * @param step
	 *            of the range.
	 * @param description
	 *            of the parameter.
	 */
	public BOAparameter(String name, BigDecimal low, BigDecimal high,
			BigDecimal step, String description) {
		this.name = name;
		this.low = low;
		this.high = high;
		this.step = step;
		this.description = description;
		generatePairs();
	}

	/**
	 * Generates the set of all possible configurations for the parameter given
	 * the range and step size of the component.
	 */
	private void generatePairs() {
		valuePairs = new HashSet<Pair<String, BigDecimal>>();
		for (BigDecimal value = low; value.compareTo(high) <= 0; value = value
				.add(step)) {
			valuePairs.add(new Pair<String, BigDecimal>(name, value));
		}
	}

	/**
	 * Returns all values of the parameters which satisfy
	 * [Lowerbound:Stepsize:Upperbound].
	 * 
	 * @return possible values for the parameter specified.
	 */
	public HashSet<Pair<String, BigDecimal>> getValuePairs() {
		return valuePairs;
	}

	/**
	 * @return name of the parameter.
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return value for the lowerbound.
	 */
	public BigDecimal getLow() {
		return low;
	}

	/**
	 * @return upperbound of the range.
	 */
	public BigDecimal getHigh() {
		return high;
	}

	/**
	 * @return stepsize of the range.
	 */
	public BigDecimal getStep() {
		return step;
	}

	public String toString() {
		if (!name.equals("null")) {
			if (low.compareTo(high) == 0) {
				/*
				 * without doubleValue we get a crazy number of digits
				 */
				return name + ": " + low.doubleValue();
			}
			return name + ": [" + low + " : " + step + " : " + high + "]";
		} else {
			return "";
		}
	}

	/**
	 * @return description of the parameter.
	 */
	public String getDescription() {
		return description;
	}

	public String toXML() {
		return "<parameter name=\"" + name + "\" default=\"" + high
				+ "\" description=\"" + description + "\"/>";
	}
}