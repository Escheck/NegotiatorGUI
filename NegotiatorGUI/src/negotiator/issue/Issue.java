package negotiator.issue;

import negotiator.xml.SimpleElement;

/**
 * Class {@link Issue} represents a negotiation issue to be settled in a negotiation. 
 * Issues in a domain are identified by unique <code>index</code> field.
 *
 * @author Tim Baarslag & Dmytro Tykhonov
 */
public abstract class Issue extends Objective {
    
    // Constructor
    public Issue(String name, int issueNumber) {
        super(null, name, issueNumber);
    }
    
    public Issue (String name, int issueNumber, Objective parent) {
    	super(parent, name, issueNumber);
    }
    
    public abstract ISSUETYPE getType();
    
    /**
     * @return corresponding string representation
     */
    public abstract String convertToString();
    
    public boolean checkInRange(Value val) {
    	return false;
    }
	
	/**
	 * Overrides addChild from Objective to do nothing, since Issues can't have children. This
	 * method simply returns without doing anything. 
	 * @param newObjective gets negated.
	 */
	public void addChild(Objective newObjective) { }
	
	/**
	 * Returns a SimpleElement representation of this issue.
	 * @return The SimpleElement with this issues name and index.
	 */
	public SimpleElement toXML(){
		SimpleElement thisIssue = new SimpleElement("issue");
		thisIssue.setAttribute("name", getName());
		thisIssue.setAttribute("index", ""+getNumber());
		return thisIssue;
	}
}