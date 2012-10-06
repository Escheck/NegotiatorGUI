package negotiator.utility;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import negotiator.Bid;
import negotiator.issue.IssueDiscrete;
import negotiator.issue.Objective;
import negotiator.issue.Value;
import negotiator.issue.ValueDiscrete;
import negotiator.xml.SimpleElement;

/**
 * @author W. Pasman
 * Since 8oct07: only POSITIVE integer values acceptable as evaluation value.
 */
public class EvaluatorDiscrete implements Evaluator {
	
	// Class fields
	private double fweight; //the weight of the evaluated Objective or Issue.
	private boolean fweightLock; 
	private HashMap<ValueDiscrete, Integer> fEval;
	private Integer evalMax= null;
	public EvaluatorDiscrete() {
		fEval = new HashMap<ValueDiscrete, Integer>();
		fweight = 0;
	} 

	/**
	 * @return the weight for this evaluator, a value between 0 and 1.
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
		if (maximum==null) throw new Exception("no evaluators available, can't get max");
		if (maximum<0) throw new Exception("Internal error: values <0 in evaluators.");
		evalMax = maximum;
	}
	
	/**
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
	 * modified W.Pasman 8oct07: now normalization happens here.
	 * 
	 * TODO Wouter: this function seems weird. 
	 * The function evaluates "bid[idnumber]" as a discrete evaluator.
	 * BUT if bid[idnumber] is not a discrete evaluator in the first place, very weird things may happen.
	 */
	public Double getEvaluation(UtilitySpace uspace, Bid bid, int ID) throws Exception
	{
		//Added by Dmytro on 09/05/2007
		return normalize(fEval.get((ValueDiscrete)bid.getValue(ID)));
	}
	
	public Double getEvaluation(ValueDiscrete altP) throws Exception 
	{
		return normalize(fEval.get(altP));
	}
	public Integer getEvaluationNotNormalized(UtilitySpace uspace, Bid bid, int ID) throws Exception
	{
		return fEval.get(((ValueDiscrete)bid.getValue(ID)));
	}
	public Integer getEvaluationNotNormalized(ValueDiscrete altP) throws Exception 
	{
		return fEval.get(altP);
	}
	
	/** 
	 * @param EvalValueL
	 * @return normalized EvalValue
	 * @throws exception if no evaluators or illegal values in evaluator.
	 * 
	 * ASSUMED that Max value is at least 1, becaues EVERY evaluatordiscrete is at least 1.
	 */
	public Double normalize(Integer EvalValueL) throws Exception
	{
		if (EvalValueL==null) throw new NullPointerException("EvalValuel=null");
		if (getEvalMax().doubleValue()<0.00001) return new Double(0); else
		return EvalValueL.doubleValue()/getEvalMax().doubleValue(); // this will throw if problem.
	}
	
	public EVALUATORTYPE getType() {
		return EVALUATORTYPE.DISCRETE;
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
		calcEvalMax();
	}
	
	/**
	 * wipe evaluation values.
	 */
	public void clear(){
		fEval.clear();
	}
	
	public void loadFromXML(SimpleElement pRoot)
	{
		Object[] xml_items = ((SimpleElement)pRoot).getChildByTagName("item");
		int nrOfValues = xml_items.length;
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
            ((SimpleElement)xml_items[j]).getAttribute("description");
        }
		try {
			getEvalMax();
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
			List<ValueDiscrete>  values=issue.getValues();
			for (ValueDiscrete value: values) 
				if (fEval.get(value)==null) throw new Exception("the value "+value+" has no evaluation in the objective ");
		}
		catch (Exception e)
		{ return  "Problem with objective "+whichobj.getName()+":" + e.getMessage();}
		return null;
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
		  Iterator<Map.Entry<ValueDiscrete, Integer>> it = fEval.entrySet().iterator();
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
		  Iterator<Map.Entry<ValueDiscrete, Integer>> it = fEval.entrySet().iterator();
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
		try{
			for (ValueDiscrete val:fEval.keySet())
				ed.setEvaluation(val, fEval.get(val));
		}
		catch (Exception e)  { System.out.println("INTERNAL ERR. clone fails"); }

		return ed;
	}
	
	public void showStatistics()
	{
		System.out.println("weight="+getWeight()+" min="+getMinValue()+" max="+getMaxValue());		
	}

	public Set<ValueDiscrete> getValues() {
		return fEval.keySet();
	}
		@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((evalMax == null) ? 0 : evalMax.hashCode());
		result = prime * result + ((fEval == null) ? 0 : fEval.hashCode());
		long temp;
		temp = Double.doubleToLongBits(fweight);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + (fweightLock ? 1231 : 1237);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		EvaluatorDiscrete other = (EvaluatorDiscrete) obj;
		if (evalMax == null) {
			if (other.evalMax != null)
				return false;
		} else if (!evalMax.equals(other.evalMax))
			return false;
		if (fEval == null) {
			if (other.fEval != null)
				return false;
		} else if (!fEval.equals(other.fEval))
			return false;
		if (Double.doubleToLongBits(fweight) != Double
				.doubleToLongBits(other.fweight))
			return false;
		if (fweightLock != other.fweightLock)
			return false;
		return true;
	}
}
