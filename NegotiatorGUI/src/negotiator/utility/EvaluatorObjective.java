package negotiator.utility;

import negotiator.Bid;
import negotiator.issue.*;
import negotiator.xml.SimpleElement;

import java.util.HashMap;

public class EvaluatorObjective implements Evaluator {
	
	// Class fields
	private double fweight; //the weight of the evaluated Objective or Issue.
	private boolean fweightLock;	
	private boolean hasWeightP; // added Wouter, 11 oct 2007
	
	public EvaluatorObjective() {
		fweight = 0; //needs to be set later on.
	}

	// Class methods
	public double getWeight(){
		return fweight;
	}
	
	public void setWeight(double wt){
		fweight = wt;
	}


	/**
	 * Locks the weight of this Evaluator.
	 */
	public void lockWeight(){
		fweightLock = true;
	}
	
	/**
	 * Unlock the weight of this evaluator.
	 *
	 */
	public void unlockWeight(){
		fweightLock = false;
	}
	
	public void setHasWeight(boolean doesHaveWeight)
	{ hasWeightP=doesHaveWeight; }
	
	
	public boolean getHasWeight() { return hasWeightP; }
	
	
	
	/**
	 * 
	 * @return The state of the weightlock.
	 */
	public boolean weightLocked(){
		return fweightLock;
	}

	public Double getEvaluation(UtilitySpace uspace, Bid bid, int index) {
		return 0.0; //TODO hdevos: Do what here, evaluate the bid for it's children?
	}
	
	public Double getEvaluation(ValueDiscrete value) {
		return 0.0;  //TODO hdevos: Do what here, only it's children have values. Or so i gather.
	}
	
	public double getCost(Value value) {
		return 0.0;  //TODO hdevos: Eh.. what value?
	}
	
	public double getMaxCost() {
		return 0.0;  //TODO hdevos: Same here.
	}
	
	public EVALUATORTYPE getType() {
		return EVALUATORTYPE.OBJECTIVE;
	}
	
	public void loadFromXML(SimpleElement pRoot) {
		//do nothing, we have no issues to load atm.
	}
	
	/**
	 * Sets weights and evaluator properties for the object in SimpleElement representation that is passed to it.
	 * @param evalObj The object of which to set the evaluation properties.
	 * @return The modified simpleElement with all evaluator properties set.
	 */
	public SimpleElement setXML(SimpleElement evalObj){
		
		
		return evalObj;
	}
}
