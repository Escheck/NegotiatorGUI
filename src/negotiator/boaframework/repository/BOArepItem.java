package negotiator.boaframework.repository;

import java.util.ArrayList;

import negotiator.boaframework.BOAparameter;

/**
 * Class used to represent an item in the BOArepository.
 * An item in the BOA repository has a classPath and may have a tooltip.
 * 
 * @author Mark Hendrikx
 */
public class BOArepItem {
	/** Name of the item */
	private String name;
	/** Classpath of the item in the repository */
	private String classPath;
	/** Collection of parameters, their description and their default */
	private ArrayList<BOAparameter> parameters;
	
	public BOArepItem(String classPath, ArrayList<BOAparameter> parameters) {
		this.classPath = classPath;
		this.parameters = parameters;
	}
	
	public BOArepItem(String name, String classPath) {
		this.name = name;
		this.classPath = classPath;
		this.parameters = new ArrayList<BOAparameter>();
	}
	
	public void addParameter(BOAparameter parameter) {
		parameters.add(parameter);
	}
	
	/**
	 * @return classpath of the BOA component.
	 */
	public String getClassPath() {
		return classPath;
	}

	public ArrayList<BOAparameter> getParameters() {
		return parameters;
	}
	
	public String getName() {
		return name;
	}
	
	public String toString() {
		String output = name + " " + classPath + " ";
		for (BOAparameter parameter : parameters) {
			output += "PARAMETER: " + parameter.toString() + " ";
		}
		return output;
	}
}