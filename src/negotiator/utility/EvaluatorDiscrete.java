package negotiator.utility;

import negotiator.Bid;
import negotiator.issue.*;
import negotiator.xml.SimpleElement;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.HashMap;
import java.util.Map;
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
	private Integer evalMax= null;
	public EvaluatorDiscrete() {
		fEval = new HashMap<ValueDiscrete, Integer>();
		fCost = new HashMap<ValueDiscrete, Double>();
		
		fweight = 0;
	} 

	/**
	 * @returns the weight for this evaluator, a value between 0 and 1.
	 */	
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
	
	private void calcEvalMax() throws Exception{
		if (fEval==null) throw new NullPointerException("fEval==null");
		Collection<Integer> alts=fEval.values();
		Integer maximum=null;
		for (Integer d: alts) if (maximum==null || d>maximum) maximum=d;
		if (maximum==null) throw new Exception("no evaluators avaliable, can't get max");
		if (maximum<0) throw new Exception("Internal error: values <0 in evaluators.");
		evalMax = maximum;
	}
	
	/**
	 * @author W.Pasman
	 * @return the largest evaluation value available
	 * @throws exception if there are no alternatives.
	 */
	public Integer getEvalMax() throws Exception
	{
		if(evalMax==null) {
			calcEvalMax();
			return evalMax;
		} else return evalMax;
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
	public Double getEvaluation(UtilitySpace uspace, Bid bid, int ID) throws Exception
	{
		//Added by Dmytro on 09/05/2007
		return normalize(fEval.get(((ValueDiscrete)bid.getValue(ID))));
	}
	
	public Double getEvaluation(ValueDiscrete altP) throws Exception 
	{
		return normalize(fEval.get(altP));
	}
		
	/** 
	 * @author W.Pasman
	 * @param EvalValueL
	 * @return normalized EvalValue
	 * @throws exception if no evaluators or illegal values in evaluator.
	 * 
	 * ASSUMED that Max value is at least 1, becaues EVERY evaluatordiscrete is at least 1.
	 */
	public Double normalize(Integer EvalValueL) throws Exception
	{
		if (EvalValueL==null) throw new NullPointerException("EvalValuel=null");
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
	/*
	public void set_Value(String name, double evaluation, double cost){
		Double valEval = fEval.get(new ValueDiscrete(name));
		if(valEval == 0)		{
		Value val = new ValueDiscrete(name);
		}
		fEval.put((ValueDiscrete)val, new Double(evaluation));
		if(maxCost < cost){
			maxCost = cost;
			fCost.put((ValueDiscrete)val, new Double(cost));
		}
	}
	*/
	
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
		try {
			Integer i = getEvalMax();
		} catch (Exception e) {
			e.printStackTrace();
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

	/**
	 * throws exception if problem with computation.
	 * @return  the cost of issue with given id. May throw if the bid is incomplete or utilityspace has problems
	 */
	public Double getCost(UtilitySpace uspace, Bid bid, int index) throws Exception
	{
		if (bid==null) throw new NullPointerException("bid=null, cant compute cost");
		ValueDiscrete val=(ValueDiscrete)bid.getValue(index);
		if (val==null) throw new NullPointerException("bid "+index+" has no value");
		Double cost= fCost.get(val);
		if (cost==null) throw new NullPointerException("no cost associated with value "+val);
		return cost;
	}

	public void addEvaluation (ValueDiscrete pValue, Integer pEval) {
		this.fEval.put(pValue, pEval);
		try {
			calcEvalMax();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	/**
	 * @return value that has maximum utility
	 */
	public Value getMaxValue() {
		  Iterator it = fEval.entrySet().iterator();
		  Integer lTmp = Integer.MIN_VALUE;
		  ValueDiscrete lValue = null;
	        while (it.hasNext()) {
	        	Map.Entry<ValueDiscrete, Integer> field = (Map.Entry<ValueDiscrete, Integer>) (it.next());
	        	if(field.getValue()>lTmp) {
	        		lValue = field.getKey();
	        		lTmp = field.getValue();
	        	}
	        } 
		return lValue;
	}

	public Value getMinValue() {
		  Iterator it = fEval.entrySet().iterator();
		  Integer lTmp = Integer.MAX_VALUE;
		  ValueDiscrete lValue = null;
	        while (it.hasNext()) {
	        	Map.Entry<ValueDiscrete, Integer> field = (Map.Entry<ValueDiscrete, Integer>) (it.next());
	        	if(field.getValue()<lTmp) {
	        		lValue = field.getKey();
	        		lTmp = field.getValue();
	        	}

	        } 
		return lValue;

	}
	
	public EvaluatorDiscrete clone()
	{
		EvaluatorDiscrete ed=new EvaluatorDiscrete();
		ed.setWeight(fweight);
		ed.setMaxCost(getMaxCost());
		try{
			for (ValueDiscrete val:fEval.keySet())
				ed.setEvaluation(val, fEval.get(val));
			for (ValueDiscrete val:fCost.keySet())
				ed.setCost(val, fCost.get(val));
		}
		catch (Exception e)  { System.out.println("INTERNAL ERR. clone fails"); }

		return ed;
	}
	
	public void showStatistics()
	{
		System.out.println("weight="+getWeight()+" min="+getMinValue()+" max="+getMaxValue());		
	}
}
