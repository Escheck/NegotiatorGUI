package negotiator.utility;

import negotiator.Bid;
import negotiator.issue.*;
import negotiator.xml.SimpleElement;

public class EvaluatorInteger implements Evaluator {
	
	// Class fields
	private double fweight; //the weight of the evaluated Objective or Issue.
	private boolean fweightLock;
	private int lowerBound;
	private int upperBound;
	EVALFUNCTYPE type;
	private double slope = 0.0;
	private double offset = 0.0;
		
	public EvaluatorInteger() {
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
		Integer lTmp = null;
		try {
			lTmp = ((ValueInteger)bid.getValue(index)).getValue();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return getEvaluation(lTmp);
	}
	
	public Double getEvaluation(int pValue) {
		double utility;		

		utility = EVALFUNCTYPE.evalLinear(pValue - lowerBound, slope, offset);
		if (utility<0)
			utility = 0;
		else if (utility > 1)
			utility = 1;
		return utility;
	}
	
	public EVALUATORTYPE getType() {
		return EVALUATORTYPE.INTEGER;
	}
	
	public EVALFUNCTYPE getFuncType(){
		return this.type;
	}
	
	public int getLowerBound() {
		return lowerBound;
	}
	
	public int getUpperBound() {
		return upperBound;
	}	
	
	public double getUtilLowestValue() {
		if (slope == 0.0 && offset == 0.0) {
			return 0;
		}
		return offset;
	}

	public double getUtilHeighestValue() {
		if (slope == 0.0 && offset == 0.0) {
			return 0;
		}
		return (offset + slope * (upperBound - lowerBound));
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

	public void setLinearFunction(double utilLowInt,
			double utilHighInt) {
		slope = (utilHighInt - utilLowInt) / (-lowerBound + upperBound);
		offset = utilLowInt;
	}
	
	public void loadFromXML(SimpleElement pRoot) {
		Object[] xml_item = ((SimpleElement)pRoot).getChildByTagName("range");
		this.lowerBound = Integer.valueOf(((SimpleElement)xml_item[0]).getAttribute("lowerbound"));
		this.upperBound = Integer.valueOf(((SimpleElement)xml_item[0]).getAttribute("upperbound"));
		Object[] xml_items = ((SimpleElement)pRoot).getChildByTagName("evaluator");
		if(xml_items.length != 0){
			this.slope = Double.valueOf(((SimpleElement)xml_items[0]).getAttribute("slope"));
			this.offset = Double.valueOf(((SimpleElement)xml_items[0]).getAttribute("offset"));
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
		//TODO: implement isComplete in the EvaluatorInteger
		return null;
	}

	public double getSlope() {
		return slope;
	}

	public void setSlope(double slope) {
		this.slope = slope;
	}

	public double getOffset() {
		return offset;
	}

	public void setOffset(double offset) {
		this.offset = offset;
	}

	public Double getCost(UtilitySpace uspace, Bid bid, int index) throws Exception
	{
		throw new Exception("getCost not implemented for EvaluatorInteger");
	}

	public EvaluatorInteger clone()
	{
		EvaluatorInteger ed=new EvaluatorInteger();
		//ed.setType(type);
		ed.setWeight(fweight);
		ed.type = type; 
		ed.setUpperBound(upperBound);
		ed.setLowerBound(lowerBound);
		ed.slope = slope;
		ed.offset = offset;
		return ed;
	}

	@Override
	public void showStatistics() {
		// TODO Auto-generated method stub
	}
}