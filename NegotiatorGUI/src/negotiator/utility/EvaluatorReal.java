package negotiator.utility;

import negotiator.Bid;
import negotiator.issue.*;
import negotiator.xml.SimpleElement;

import java.util.HashMap;

public class EvaluatorReal implements Evaluator {
	
	// Class fields
	public double fweight; //the weight of the evaluated Objective or Issue.
	
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
	
	public double getLowerBound() {
		return lowerBound;
	}
	
	public double getUpperBound() {
		return lowerBound;
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
		case CONSTANT:
			this.fParam.put(0, Double.valueOf(((SimpleElement)xml_items[0]).getAttribute("parameter0")));
		}
		
	}
	
}
