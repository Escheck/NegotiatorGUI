package negotiator.boaframework.repository;

import java.net.MalformedURLException;

import negotiator.Global;
import negotiator.boaframework.AcceptanceStrategy;
import negotiator.boaframework.BOA;
import negotiator.boaframework.ComponentsEnum;
import negotiator.boaframework.OMStrategy;
import negotiator.boaframework.OfferingStrategy;
import negotiator.boaframework.OpponentModel;

/**
 * Class used to represent an item in the BOArepository. An item in the BOA
 * repository has a classPath and may have a tooltip.
 * 
 * @author Mark Hendrikx
 */
public class BOArepItem implements Comparable<BOArepItem> {
	/** Name of the item */
	private String name;
	/** Classpath of the item in the repository */
	private String classPath;

	private ComponentsEnum type;

	public BOArepItem(String name, String classPath, ComponentsEnum type) {
		this.name = name;
		this.classPath = classPath;
		this.type = type;
	}

	/**
	 * @return classpath of the BOA component.
	 */
	public String getClassPath() {
		return classPath;
	}

	public String getName() {
		return name;
	}

	public String toString() {
		String output = name + " " + classPath;
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
		result += element + " description=\"" + name + "\" classpath=\""
				+ classPath + "\"";
		result += "/>\n";
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

	/**
	 * Load the {@link BOA} object. This may return a {@link OfferingStrategy},
	 * {@link AcceptanceStrategy}, {@link OpponentModel}, or {@link OMStrategy}
	 * depending on the type.
	 * 
	 * @return
	 * @throws MalformedURLException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws ClassNotFoundException
	 */
	public BOA getInstance() throws MalformedURLException,
			InstantiationException, IllegalAccessException,
			ClassNotFoundException {
		return (BOA) Global.loadObject(classPath);

	}
}