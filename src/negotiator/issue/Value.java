package negotiator.issue;

/**
 * Specifies a generic value of an issue. This superclass needs to
 * be extended by a subclass.
 */
public class Value {
	
	/**
	 * Empty constructor used to create a new Value.
	 */
	public Value() { }
	
	/**
	 * @return type of the issue.
	 */
	public ISSUETYPE getType() {return ISSUETYPE.UNKNOWN;};
	
	public String toString() { return "unknown!";}
}
