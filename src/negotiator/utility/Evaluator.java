package negotiator.utility;

import negotiator.Bid;
import negotiator.xml.SimpleElement;
import negotiator.issue.Objective;
/**
 * 
 * @author Dmytro?
 * 
 * Evaluator is an object that translates discrete values into an evaluation value.
 * The UtilitySpace attaches it to an issue.
 * It is saved if you save the utility space, using the setXML functions.
 *  
 */

public interface Evaluator {
	
	// Interface methods
	/**
	 * @return the weight associated with this
	 */
	public double getWeight();
	/**
	 * Sets the weigth with which an Objective or Issue is evaluated.
	 * @param wt The new weight.
	 */
	public void setWeight(double wt);
	
	/**Wouter: lockWeight does not actually lock setWeight or so. It merely is a flag
	 * affecting the behaviour of the normalize function in the utility space.
	 */
	public void lockWeight();
	
	public void unlockWeight();
	
	public boolean weightLocked();
	
	/** The getEvaluation method returns a scalar evaluation for a value in a bid.
	* Providing the complete bid as a paramater to the method allows for issue dependencies.
	* @throws exception if problem, for instance illegal evaluation values.
	*/
	public Object getEvaluation(UtilitySpace uspace, Bid bid, int index) throws Exception;
	
	public EVALUATORTYPE getType();
	
	public void loadFromXML(SimpleElement pRoot);
	
	public SimpleElement setXML(SimpleElement evalObj);
	
	/** 
	 * @param whichObjective is the objective/issue to which this evaluator is attached.
	 * @return String describing lacking component, or null if the evaluator is complete. 
	 */
	public String isComplete(Objective whichObjective);
	
}
