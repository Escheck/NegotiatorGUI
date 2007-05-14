package negotiator.utility;

import negotiator.Bid;
import negotiator.issue.*;
import negotiator.xml.SimpleElement;

import java.util.HashMap;

public class EvaluatorInteger implements Evaluator {
	
	// Class fields
	int lowerBound;
	int upperBound;
	EVALFUNCTYPE type;
	HashMap<Integer, Integer> fParam;
		
	public EvaluatorInteger() {
		fParam = new HashMap<Integer, Integer>();
	}

	// Class methods
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
		return lowerBound;
	}	
	
	public void loadFromXML(SimpleElement pRoot) {
		Object[] xml_item = ((SimpleElement)pRoot).getChildByTagName("range");
		this.lowerBound = Integer.valueOf(((SimpleElement)xml_item[0]).getAttribute("lowerBound"));
		this.upperBound = Integer.valueOf(((SimpleElement)xml_item[0]).getAttribute("lowerBound"));
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
	
}
