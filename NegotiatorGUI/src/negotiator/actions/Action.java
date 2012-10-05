package negotiator.actions;

/**
 * Class which symbolizes a high level action.
 * 
 * @author Tim Baarslag and Dmytro Tykhonov
 */
public abstract class Action {

    /**
     * Empty constructor used for inheritance.
     */
	public Action() {}
	
    /**
     * Enforces that actions implements a string-representation.
     */
    public abstract String toString();   
}