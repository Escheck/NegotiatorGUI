package negotiator.utility;

import negotiator.Bid;
import negotiator.issue.*;
import negotiator.xml.SimpleElement;

import java.util.HashMap;

public class EvaluatorDiscrete implements Evaluator {
	
	// Class fields
	private double fweight; //the weight of the evaluated Objective or Issue.
	private boolean fweightLock; 
	private HashMap<ValueDiscrete, Double> fEval;
	private HashMap<ValueDiscrete, Double> fCost;
	private double maxCost = 0;
	
	public EvaluatorDiscrete() {
		fEval = new HashMap<ValueDiscrete, Double>();
		fCost = new HashMap<ValueDiscrete, Double>();
		
		fweight = 0;
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
	
	/**
	 * 
	 * @return The state of the weightlock.
	 */
	public boolean weightLocked(){
		return fweightLock;
	}
	
	public Double getEvaluation(UtilitySpace uspace, Bid bid, int index) {
		//Added by Dmytro on 09/05/2007
		Double lTmp =fEval.get(((ValueDiscrete)bid.getValue(index)));
		if(lTmp == null) 
			return -1.0;
		else return lTmp;
		//End of Added by Dmytro
	}
	
	public Double getEvaluation(ValueDiscrete value) {
		//Added by Dmytro on 09/05/2007
		Double lTmp =fEval.get(value);
		if(lTmp == null) 
			return -1.0;
		else return lTmp;
		//End of Added by Dmytro
	}
	
	public Double getCost(Value value) {
		return fCost.get(value); // return null if no cost set for this issue.
		//if (fCost.get(value)!=null)
		//	return fCost.get(value);
		//else return 0;
	}
	
	public double getMaxCost() {
		return maxCost;
	}
	
	public EVALUATORTYPE getType() {
		return EVALUATORTYPE.DISCRETE;
	}
	
	/**
	 * Sets the maximum cost
	 * @param mc
	 */
	public void setMaxCost(double mc){
		maxCost = mc;
	}
	
	/**
	 * Adds a valueDiscrete with evaluation and cost to this Evaluator. Sets maxCost to cost if 
	 * it turns out that the new cost is higher than the maximum.
	 * 
	 * @param name The name of the ValueDiscrete.
	 * @param evaluation The evaluation of the value
	 * @param cost The cost.
	 */
	public void set_Value(String name, double evaluation, double cost){
	/*	Double valEval = fEval.get(new ValueDiscrete(name));
		if(valEval == 0)		{
		Value val = new ValueDiscrete(name);
		}
		fEval.put((ValueDiscrete)val, new Double(evaluation));
		if(maxCost < cost){
			maxCost = cost;
			fCost.put((ValueDiscrete)val, new Double(cost));
		}
	*/	
	}
	
	/**
	 * Sets the evaluation for Value <code>val</code>. If this value doesn't exist yet in this Evaluator,
	 * adds it as well.
	 * 
	 * @param val The value to add or have its evaluation modified.
	 * @param evaluation The new evaluation.
	 */
	public void setEvaluation(Value val, double evaluation ){
		fEval.put((ValueDiscrete)val, new Double(evaluation));		
	}

	/**
	 * Sets the cost for value <code>val</code>. If the value doesn't exist yet in this Evaluator,
	 * add it as well. Note that here isn't an evaluation for it if we add it through here.
	 * @param val The value to have it's cost set/modified
	 * @param cost The new cost of the value.
	 */
	public void setCost(Value val, double cost){
		if(maxCost < cost){
			maxCost = cost;
			fCost.put((ValueDiscrete)val, new Double(cost));
		}
	}
	
	public void clear(){
		fEval.clear();
		fCost.clear();
		
	}
	
	public void loadFromXML(SimpleElement pRoot) {
		Object[] xml_items = ((SimpleElement)pRoot).getChildByTagName("item");
		int nrOfValues = xml_items.length;
		double cost;
		ValueDiscrete value;
				
		for(int j=0;j<nrOfValues;j++) {
            value = new ValueDiscrete(((SimpleElement)xml_items[j]).getAttribute("value"));
            String evaluationStr = ((SimpleElement)xml_items[j]).getAttribute("evaluation");
            if(evaluationStr != null){
            	this.fEval.put(value, Double.valueOf(evaluationStr));
            }
            String sCost = ((SimpleElement)xml_items[j]).getAttribute("cost");
            if (sCost!=null) {
            	cost = Double.valueOf(sCost);
            	if (maxCost<cost) {
            		maxCost = cost;
            		this.fCost.put(value, cost);
            	}
            }
            else // by default set cost to 0?
            	this.fCost.put(value, 0.0);
        }
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
