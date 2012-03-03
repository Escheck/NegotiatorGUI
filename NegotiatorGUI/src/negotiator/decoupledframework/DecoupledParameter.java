package negotiator.decoupledframework;

import java.util.HashSet;
import misc.Pair;

/**
 * Class used to store the information of a decoupled parameter.
 * 
 * Please report bugs to author.
 * 
 * @author Mark Hendrikx (m.j.c.hendrikx@student.tudelft.nl)
 * @version 16-01-12
 */
public class DecoupledParameter {
	
	private String name;
	private double low;
	private double high;
	private double step;
	private HashSet<Pair<String, Double>> valuePairs;
	
	/**
	 * Describes a parameter for a decoupled component.
	 * A parameter consists of a name, and the possible values for the parameter.
	 * @param name of the parameter
	 * @param low value of the range
	 * @param high value of the range
	 * @param step in the range
	 */
	public DecoupledParameter(String name, double low, double high, double step) {
		this.name = name;
		this.low = low;
		this.high = high;
		this.step = step;
		generatePairs();
	}

	/**
	 * Generates the set of all possible configurations for the parameter
	 * given the range and step size of the component.
	 */
	private void generatePairs() {
		valuePairs = new HashSet<Pair<String, Double>>();
		for (double value = low; value <= high; value += step) {
			valuePairs.add(new Pair<String, Double>(name, value));
		}
	}
	
	public HashSet<Pair<String, Double>> getValuePairs() {
		return valuePairs;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public double getLow() {
		return low;
	}

	public void setLow(double low) {
		this.low = low;
	}

	public double getHigh() {
		return high;
	}

	public void setHigh(double high) {
		this.high = high;
	}

	public double getStep() {
		return step;
	}

	public void setStep(double step) {
		this.step = step;
	}
	
	public String toString() {
		if (!name.equals("null")) {
			return name + ": [" + low + " : " + step + " : " + high + "]";
		} else {
			return "";
		}
	}
}