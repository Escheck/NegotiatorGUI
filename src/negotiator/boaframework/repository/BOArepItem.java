package negotiator.boaframework.repository;

import java.util.ArrayList;

import negotiator.boaframework.BOAparameter;
import negotiator.boaframework.ComponentsEnum;

/**
 * Class used to represent an item in the BOArepository.
 * An item in the BOA repository has a classPath and may have a tooltip.
 * 
 * @author Mark Hendrikx
 */
public class BOArepItem implements Comparable<BOArepItem> {
	/** Name of the item */
	private String name;
	/** Classpath of the item in the repository */
	private String classPath;
	/** Collection of parameters, their description and their default */
	private ArrayList<BOAparameter> parameters;
	
	private ComponentsEnum type;
	
	public BOArepItem(String name, String classPath, ComponentsEnum type) {
		this.name = name;
		this.classPath = classPath;
		this.parameters = new ArrayList<BOAparameter>();
		this.type = type;
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
	
	public String toXML() {
		String result = "\t\t<";
		String element = "";
		if (type == ComponentsEnum.BIDDINGSTRATEGY) {
			element = "biddingstrategy";
		} else if (type == ComponentsEnum.ACCEPTANCESTRATEGY) {
			element += "acceptancecondition";
		} else if (type == ComponentsEnum.OPPONENTMODEL) {
			element += "opponentmodel";
		} else {
			element += "omstrategy";
		}
		result += element + " description=\"" + name + "\" classpath=\"" + classPath + "\"";
		if (parameters.size() == 0) {
			result += "/>\n";
		} else {
			result += ">\n";
			for (BOAparameter param : parameters) {
				result += "\t\t\t" + param.toXML() + "\n";
			}
			result += "\t\t" + "</" + element + ">\n";
		}
		return result;
	}

	public ComponentsEnum getType() {
		return type;
	}
	
	public String getTypeString() {
		String result;
		switch (type) {
		case BIDDINGSTRATEGY:
			result = "Bidding strategy";
		break;
		case OPPONENTMODEL:
			result = "Opponent model";
		break;
		case ACCEPTANCESTRATEGY:
			result = "Acceptance strategy";
		break;
		case OMSTRATEGY:
			result = "Opponent model strategy";
		break;
		default:
			result = "Unknown type";
			break;
		}
		return result;
	}

	@Override
	public int compareTo(BOArepItem rep2) {
		if (this.type.ordinal() < rep2.type.ordinal()) {
			return -1; // -1 means that THIS goes before rep2
		}
		if (this.type.ordinal() > rep2.type.ordinal()) {
			return 1;
		}
		if (this.type.ordinal() == rep2.type.ordinal()) {
			return String.CASE_INSENSITIVE_ORDER.compare(this.name, rep2.name);
		}
		return 0;
	}
}