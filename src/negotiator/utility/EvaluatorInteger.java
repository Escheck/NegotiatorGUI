package negotiator.utility;

import java.text.DecimalFormat;

import negotiator.Bid;
import negotiator.issue.Objective;
import negotiator.issue.ValueInteger;
import negotiator.xml.SimpleElement;

/**
 * This class is used to convert the value of an integer issue to a utility.
 * This object stores the range of the issue and a linear function mapping each
 * value to a utility. Note that this utility is not yet normalized by the issue
 * weight and is therefore in the range [0,1].
 * 
 * @author Wouter Pasman
 */
public class EvaluatorInteger implements Evaluator {
	
	// Class fields
	private double fweight; //the weight of the evaluated Objective or Issue.
	private boolean fweightLock;
	private int lowerBound;
	private int upperBound;
	EVALFUNCTYPE type;
	private double slope = 0.0;
	private double offset = 0.0;
	private DecimalFormat f = new DecimalFormat("0.0000");
	
	/**
	 * Creates a new integer evaluator with weight 0 and
	 * no values.
	 */
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
	
	public Double getEvaluation(AdditiveUtilitySpace uspace, Bid bid, int index) {
		Integer lTmp = null;
		try {
			lTmp = ((ValueInteger)bid.getValue(index)).getValue();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return getEvaluation(lTmp);
	}
	
	/**
	 * @param value of an issue.
	 * @return utility of the given value (range: [0,1]).
	 */
	public Double getEvaluation(int value) {
		double utility;		

		utility = EVALFUNCTYPE.evalLinear(value - lowerBound, slope, offset);
		if (utility<0)
			utility = 0;
		else if (utility > 1)
			utility = 1;
		return utility;
	}
	
	public EVALUATORTYPE getType() {
		return EVALUATORTYPE.INTEGER;
	}
	
	/**
	 * @return evaluation function type.
	 */
	public EVALFUNCTYPE getFuncType(){
		return this.type;
	}
	
	/**
	 * @return lowerbound of the integer issue.
	 */
	public int getLowerBound() {
		return lowerBound;
	}
	
	/**
	 * @return higherbound of the integer issue.
	 */
	public int getUpperBound() {
		return upperBound;
	}	
	
	/**
	 * @return lowest possible utility value.
	 */
	public double getUtilLowestValue() {
		return offset;
	}

	/**
	 * @return highest possible utility value.
	 */
	public double getUtilHighestValue() {
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

	/**
	 * Specifies the linear utility function of the issue by giving the
	 * utility of the lowest value and the highest value.
	 * 
	 * @param utilLowInt utility of the lowest vale.
	 * @param utilHighInt utility of the highest value.
	 */
	public void setLinearFunction(double utilLowInt,
			double utilHighInt) {
		slope = (utilHighInt - utilLowInt) / (-lowerBound + upperBound);
		offset = utilLowInt;
	}
	
	public void loadFromXML(SimpleElement pRoot) {
		
		//RA: Change lowerbound and upperbound as attributes
		this.lowerBound=Integer.valueOf(((SimpleElement)pRoot).getAttribute("lowerbound"));
		this.upperBound=Integer.valueOf(((SimpleElement)pRoot).getAttribute("upperbound"));
		
		//RA begin
		//Object[] xml_item = ((SimpleElement)pRoot).get.getChildByTagName("range");
		//this.lowerBound = Integer.valueOf(((SimpleElement)xml_item[0]).getAttribute("lowerbound"));
		//this.upperBound = Integer.valueOf(((SimpleElement)xml_item[0]).getAttribute("upperbound"));
		//RA end
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
		return null;
	}

	/**
	 * @return slope of the linear utility function.
	 */
	public double getSlope() {
		return slope;
	}

	/**
	 * @return slope of the linear utility function.
	 */
	@Deprecated
	public double getLinearParam() {
		return getSlope();
	}
	
	/**
	 * Sets the slope of the linear utility function.
	 * @param slope of the linear utility function.
	 */
	public void setSlope(double slope) {
		this.slope = slope;
	}

	/**
	 * Sets the slope of the linear utility function.
	 * @param slope of the linear utility function.
	 */
	@Deprecated
	public void setLinearParam(double slope) {
		setSlope(slope);
	}
	
	/**
	 * @return offset of the linear utility function.
	 */
	public double getOffset() {
		return offset;
	}
	
	/**
	 * @return offset of the linear utility function.
	 */
	@Deprecated
	public double getConstantParam() {
		return getOffset();
	}

	/**
	 * Sets the offset of the linear utility function.
	 * @param offset of the linear utility function.
	 */
	public void setOffset(double offset) {
		this.offset = offset;
	}
	
	/**
	 * Sets the offset of the linear utility function.
	 * @param offset of the linear utility function.
	 */
	@Deprecated
	public void setConstantParam(double offset) {
		setOffset(offset);
	}

	public String toString() {
		return "{Integer: offset=" + f.format(offset) + " slope=" + f.format(slope) + " range=[" + lowerBound + ":" + upperBound + "]}";
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
}