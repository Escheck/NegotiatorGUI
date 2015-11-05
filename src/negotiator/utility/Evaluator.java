package negotiator.utility;

import negotiator.Bid;
import negotiator.xml.SimpleElement;
import negotiator.issue.Objective;

/**
 * Evaluator is an object that translates discrete values into an evaluation value.
 * The UtilitySpace attaches it to an issue.
 * @author Dmytro
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
	
	/**
	 * Wouter: lockWeight does not actually lock setWeight or so. It merely is a flag
	 * affecting the behaviour of the normalize function in the utility space.
	 */
	public void lockWeight();
	
	/**
	 * Method to unlock a weight. A weight must be unlocked to modify it.
	 */
	public void unlockWeight();
	
	/**
	 * @return true if weight is locked.
	 */
	public boolean weightLocked();
	
	/**
	 * This method returns the utility of the value of an issue. Note that the value
	 * is not multiplied by the issue weight, and is therefore non-normalized.
	 * @param uspace preference profile
	 * @param bid in which the value is contained.
	 * @param index of the issue in the bid.
	 * @return utility of the value for an issue, not normalized by the issue weight.
	 * @throws Exception if problem, for instance illegal evaluation values.
	 */
	public Double getEvaluation(AdditiveUtilitySpace uspace, Bid bid, int index) throws Exception;
	
	/**
	 * @return type of evaluation function, for example EVALUATORTYPE.LINEAR.
	 */
	public EVALUATORTYPE getType();
	
	/**
	 * Method to 
	 * @param pRoot
	 */
	public void loadFromXML(SimpleElement pRoot);
	
	/** 
	 * Check whether the evaluator has enough information to make an evaluation.
	 * 
	 * @param whichObjective is the objective/issue to which this evaluator is attached.
	 * @return String describing lacking component, or null if the evaluator is complete. 
	 */
	public String isComplete(Objective whichObjective);
	
	/**
	 * @return clone of the current object.
	 */
	public Evaluator clone();
}
