package negotiator.utility;

import negotiator.Bid;
import negotiator.issue.*;
import negotiator.xml.SimpleElement;

import java.util.HashMap;

public class EvaluatorInteger implements Evaluator {
	
	// Class fields
	private double fweight; //the weight of the evaluated Objective or Issue.
	private boolean fweightLock;
	int lowerBound;
	int upperBound;
	EVALFUNCTYPE type;
	HashMap<Integer, Integer> fParam;
		
	public EvaluatorInteger() {
		fParam = new HashMap<Integer, Integer>();
		
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
	
	public Integer getEvaluation(UtilitySpace uspace, Bid bid, int index) {
		Integer lTmp = ((ValueInteger)bid.getValue(index)).getValue();
		switch(this.type) {
		case LINEAR:
			Double d = EVALFUNCTYPE.evalLinear(lTmp, this.fParam.get(1), this.fParam.get(0));
			if (d<0)
				d=0.0;
			else if (d>1)
				d=1.0;
			return d.intValue();
		case CONSTANT:
			return this.fParam.get(0);
		default:
			return -1;
		}	
	}
	
	public EVALUATORTYPE getType() {
		return EVALUATORTYPE.INTEGER;
	}
	
	public int getLowerBound() {
		return lowerBound;
	}
	
	public int getUpperBound() {
		return lowerBound;   //TODO check if this is ok.
	}	
	
	/**
	 * Sets the lower bound of this evaluator.
	 * @param lb The new lower bound
	 */
	public void setLowerBound(int lb) {
		lowerBound = lb;
	}
	
	/**
	 * Sets the upper bound of this evaluator.
	 * @param ub The new upper bound
	 */
	public void setUpperBound(int ub){
		upperBound = ub;
	}
	
	/**
	 * Sets the ftype of this evaluator
	 * @param ft The ftype, either <code>"linear"</code> or <code>"constant"</code>
	 */
	public void setftype(String ft){
		type = EVALFUNCTYPE.convertToType(ft);
	}
	
	/**
	 * Sets the linear parameter for this evaluator, and changes the ftype to linear.
	 * @param par0 The linear parameter
	 */
	public void setLinearParam(int par0){
		setftype("linear");
		fParam.put(new Integer(1), new Integer(par0) );
	}

	/**
	 * 
	 * @return The linear parameter of this Evaluator, or 0 if it doesn't exist.
	 */		
	public int getLinearParam(){
		try{
			return fParam.get(new Integer(1));
		}catch(Exception e){
			//do nothing
		}
		return 0;
	}
	/**
	 * Sets the constant parameter for this evaluetor, and changes the ftype to constant.
	 * @param par1 The constant parameter.
	 */
	public void setConstantParam(int par1){
		setftype("constant");
		fParam.put(new Integer(1), new Integer(par1));
	}

	/**
	 * 
	 * @return The constant parameter of this Evaluator, or 0 if it doesn't exist.
	 */	
	public int getConstantParam(){
		try{
			return fParam.get(new Integer(0));
		}catch(Exception e){
			//do nothing.
		}
		return 0;
	}
	
	public void loadFromXML(SimpleElement pRoot) {
		Object[] xml_item = ((SimpleElement)pRoot).getChildByTagName("range");
		this.lowerBound = Integer.valueOf(((SimpleElement)xml_item[0]).getAttribute("lowerbound"));
		this.upperBound = Integer.valueOf(((SimpleElement)xml_item[0]).getAttribute("lowerbound"));
		Object[] xml_items = ((SimpleElement)pRoot).getChildByTagName("evaluator");
		String ftype = ((SimpleElement)xml_items[0]).getAttribute("ftype");
		if (ftype!=null)
			this.type = EVALFUNCTYPE.convertToType(ftype);
		// TODO: define exception.
		switch(this.type) {
		case LINEAR:
			this.fParam.put(1, Integer.valueOf(((SimpleElement)xml_items[0]).getAttribute("parameter1")));
		case CONSTANT:
			this.fParam.put(0, Integer.valueOf(((SimpleElement)xml_items[0]).getAttribute("parameter0")));
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
