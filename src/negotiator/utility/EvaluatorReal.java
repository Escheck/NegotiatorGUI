package negotiator.utility;

import negotiator.Bid;
import negotiator.issue.*;
import negotiator.xml.SimpleElement;

import java.util.HashMap;

public class EvaluatorReal implements Evaluator {
	
	// Class fields
	private double fweight; //the weight of the evaluated Objective or Issue.
	private boolean fweightLock;	
	double lowerBound;
	double upperBound;
	EVALFUNCTYPE type;
	HashMap<Integer, Double> fParam;
		
	public EvaluatorReal() {
		fParam = new HashMap<Integer, Double>();
		
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
		double utility;
		double value = ((ValueReal)bid.getValue(index)).getValue();
		switch(this.type) {
		case LINEAR:
			utility = EVALFUNCTYPE.evalLinear(value, this.fParam.get(1), this.fParam.get(0));
			if (utility<0)
				utility = 0;
			else if (utility > 1)
				utility = 1;
			return utility;
		case CONSTANT:
			return this.fParam.get(0);
		default:
			return -1.0;
		}	
	}
	public double getValueByEvaluation(double pUtility) {
		double lValue = 0;
		switch(this.type) {
		case LINEAR:
			lValue= EVALFUNCTYPE.evalLinearRev(pUtility, this.fParam.get(1), this.fParam.get(0));
			if (lValue<getLowerBound() )
				lValue = getLowerBound();
			else if (lValue > getUpperBound())
				lValue = getUpperBound();
			return lValue;
		case CONSTANT:
			return this.fParam.get(0);
		default:
			return -1.0;
		}	
		
	}
	public EVALUATORTYPE getType() {
		return EVALUATORTYPE.REAL;	
	}
	
	public EVALFUNCTYPE getFuncType(){
		return this.type;
	}
	
	public double getLowerBound() {
		return lowerBound;
	}
	
	public double getUpperBound() {
		return lowerBound; //TODO hdv is this ok?
	}	
	
	/**
	 * Sets the lower bound for this evaluator.
	 * @param lf The new lower bound.
	 */
	public void setLowerBound(double lf){
		lowerBound = lf;
	}
	
	/**
	 * Sets the upper bound for this evaluator
	 * @param ub The new upper bound
	 */
	public void setUpperBound(double ub){
		upperBound = ub;
	}
	
	/**
	 * Sets the evaluator function type.
	 * @param ft The new type, either <code>"linear"</code> or <code>"constant"</code>
	 */
	public void setftype(String ft){
		type = EVALFUNCTYPE.convertToType(ft);
	}
	
	/**
	 * Sets the linear parameter for the evaluation function and changes the ftype of
	 * this evaluator to "linear".
	 * @param par1 The new linear evaluation parameter.
	 */
	public void setLinearParam(double par1){
		setftype("linear");
		fParam.put(new Integer(1), new Double(par1));
	}
	
	/**
	 * 
	 * @return The linear parameter of this Evaluator, or 0 if it doesn't exist.
	 */
	public double getLinearParam(){
		try{
			return fParam.get(new Integer(1));
		}catch(Exception e){
			System.out.println("Linear parameter does not exist");
			e.printStackTrace();
		}
		return 0;
	}
	
	/**
	 * Sets the constant parameter for the evaluation function and changes the ftype of this
	 * evaluator to "constant"
	 * @param par0 The new constant evalutation parameter.
	 */
	public void setConstantParam(double par0){
		setftype("constant");
		fParam.put(new Integer(0), new Double(par0));
	}

	/**
	 * 
	 * @return The constant parameter of this Evaluator, or 0 if it doesn't exist.
	 */	
	public double getConstantParam(){
		try{
			return fParam.get(new Integer(1));
		}catch(Exception e){
			System.out.println("Linear parameter does not exist");
			e.printStackTrace();
		}
		return 0;
	}
	public void loadFromXML(SimpleElement pRoot) {
		Object[] xml_item = ((SimpleElement)pRoot).getChildByTagName("range");
		this.lowerBound = Double.valueOf(((SimpleElement)xml_item[0]).getAttribute("lowerbound"));
		this.upperBound = Double.valueOf(((SimpleElement)xml_item[0]).getAttribute("upperbound"));
		Object[] xml_items = ((SimpleElement)pRoot).getChildByTagName("evaluator");
		String ftype = ((SimpleElement)xml_items[0]).getAttribute("ftype");
		if (ftype!=null)
			this.type = EVALFUNCTYPE.convertToType(ftype);
		// TODO: define exception.
		switch(this.type) {
		case LINEAR:
			this.fParam.put(1, Double.valueOf(((SimpleElement)xml_items[0]).getAttribute("parameter1")));
			break;
		case CONSTANT:
			this.fParam.put(0, Double.valueOf(((SimpleElement)xml_items[0]).getAttribute("parameter0")));
			break;
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
