package negotiator.issue;

import java.io.Serializable;

/**
 * Specifies a generic value of an issue. This superclass needs to be extended
 * by a subclass.
 * <p>
 * Value objects are final and can not be modified after creation.
 */
public class Value implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1212374174018193000L;

	/**
	 * Empty constructor used to createFrom a new Value.
	 */
	public Value() {
	}

	/**
	 * @return type of the issue.
	 */
	public ISSUETYPE getType() {
		return ISSUETYPE.UNKNOWN;
	};

	public String toString() {
		return "unknown!";
	}
}
