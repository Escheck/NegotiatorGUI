package negotiator.utility;

import negotiator.Bid;
import negotiator.issue.*;
import negotiator.xml.SimpleElement;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Collection;

/**
 * 
 * @author wouter
 * Since 8oct07: only POSITIVE integer values acceptable as evaluation value.
 */
public class EvaluatorDiscrete implements Evaluator {
	
	// Class fields
	private double fweight; //the weight of the evaluated Objective or Issue.
	private boolean fweightLock; 
	private HashMap<ValueDiscrete, Integer> fEval;
	private HashMap<ValueDiscrete, Double> fCost;
	private double maxCost = 0;
	
	public EvaluatorDiscrete() {
		fEval = new HashMap<ValueDiscrete, Integer>();
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
	
	/**
	 * @return the non-normalized evaluation. 
	 * Or null if value is not an alternative.
	 */
	public Integer getValue(ValueDiscrete alternativeP)
	{
		return fEval.get(alternativeP);
	}
	

	
	/**
	 * @author W.Pasman
	 * @return the largest alternative available
	 * returns null if there are no alternatives.
	 */
	public Integer getEvalMax()
	{
		Collection<Integer> alts=fEval.values();
		Integer maximum=null;
		for (Integer d: alts) if (maximum==null || d>maximum) maximum=d;

		return maximum;
	}
	
	
	/**
	 * @param the utilityspace settings, the complete bid and the idnumber of the issue to be evaluated
	 * @return the normalized evaluation value.
	 * @author Koen, Dmytro
	 * modified W.Pasman 8oct07: now normalization happens here.
	 * 
	 * TODO Wouter: this function seems weird. 
	 * The function evaluates "bid[idnumber]" as a discrete evaluator.
	 * BUT if bid[idnumber] is not a discrete evaluator in the first place, very weird things may happen.
	 */
	public Double getEvaluation(UtilitySpace uspace, Bid bid, int index) throws Exception
	{
		//Added by Dmytro on 09/05/2007
		return normalize(fEval.get(((ValueDiscrete)bid.getValue(index))));
	}
	
	public Double getEvaluation(ValueDiscrete altP) throws Exception 
	{
		return normalize(fEval.get(altP));
	}
		
	/** 
	 * @author W.Pasman
	 * @param EvalValueL
	 * @return normalized EvalValue
	 * 
	 * ASSUMED that Max value is at least 1, becaues EVERY evaluatordiscrete is at least 1.
	 */
	public Double normalize(Integer EvalValueL)
	{
		return EvalValueL.doubleValue()/getEvalMax().doubleValue(); // this will throw if problem.
	}
	
	/** 
	 * 
	 * @param the alternative name 
	 * @return cost, null if no cost available.
	 */
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
	public void setEvaluation(Value val, int utility ) throws Exception
	{
		if (utility<0) throw new Exception("utility values have to be >0");
		fEval.put((ValueDiscrete)val, new Integer(utility));		
	}

	/**
	 * Sets the cost for value <code>val</code>. If the value doesn't exist yet in this Evaluator,
	 * add it as well. Note that here isn't an evaluation for it if we add it through here.
	 * @param val The value to have it's cost set/modified
	 * @param cost The new cost of the value.
	 */
	public void setCost(ValueDiscrete val, Double cost)
	{
		//Wouter: I don't get this code...
    	//  why not set the cost if it is smaller than maxCost??

		// if(maxCost < cost){
		//	maxCost = cost;
		//	fCost.put((ValueDiscrete)val, new Double(cost));
		//}
		fCost.put(val, cost);
		if (cost>maxCost) maxCost=cost;
	}
	

	
	/**
	 * wipe evaluation values and cost.
	 */
	public void clear(){
		fEval.clear();
		fCost.clear();
		
	}
	
	public void loadFromXML(SimpleElement pRoot)
	{
		Object[] xml_items = ((SimpleElement)pRoot).getChildByTagName("item");
		int nrOfValues = xml_items.length;
		double cost;
		ValueDiscrete value;
				
		for(int j=0;j<nrOfValues;j++) {
            value = new ValueDiscrete(((SimpleElement)xml_items[j]).getAttribute("value"));
            String evaluationStr = ((SimpleElement)xml_items[j]).getAttribute("evaluation");
            if(evaluationStr != null){
            	try {
            		this.fEval.put(value, Integer.valueOf(evaluationStr));
            	}
            	catch (Exception e) { System.out.println("Problem reading XML file: "+e.getMessage());}
            }          
            String sCost = ((SimpleElement)xml_items[j]).getAttribute("cost");
            if (sCost!=null) {
            	//cost = Double.valueOf(sCost);
            	setCost((ValueDiscrete)value,Double.valueOf(sCost));
            	
            	// Wouter: sorry but I don't get the following old code at all....
            	// first, that check against maxCost is already done in setCost. And second, why not set the cost if it is smaller than maxCost??
            	// if (maxCost<cost) { maxCost = cost; setCost(value, cost);} 
            }
            String descStr=((SimpleElement)xml_items[j]).getAttribute("description");
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
	
	public String isComplete(Objective whichobj )
	{
		try
		{
			if (!(whichobj instanceof IssueDiscrete))
				throw new Exception("this discrete evaluator is associated with something of type "+whichobj.getClass());
			// check that each issue value has an evaluator.
			IssueDiscrete issue=(IssueDiscrete)whichobj;
			ArrayList<ValueDiscrete>  values=issue.getValues();
			for (ValueDiscrete value: values) 
				if (fEval.get(value)==null) throw new Exception("the value "+value+" has no evaluation in the objective ");
		}
		catch (Exception e)
		{ return  "Problem with objective "+whichobj.getName()+":" + e.getMessage();}
		return null;
	}

	
}
