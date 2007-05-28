package negotiator.utility;

import negotiator.Bid;
import negotiator.issue.*;
import negotiator.xml.SimpleElement;

import java.util.HashMap;

public class EvaluatorDiscrete implements Evaluator {
	
	// Class fields
	public double fweight; //the weight of the evaluated Objective or Issue.
	
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
	
	public double getCost(Value value) {
		if (fCost.get(value)!=null)
			return fCost.get(value);
		else return 0;
	}
	
	public double getMaxCost() {
		return maxCost;
	}
	
	public EVALUATORTYPE getType() {
		return EVALUATORTYPE.DISCRETE;
	}
	
	public void loadFromXML(SimpleElement pRoot) {
		Object[] xml_items = ((SimpleElement)pRoot).getChildByTagName("item");
		int nrOfValues = xml_items.length;
		double cost;
		ValueDiscrete value;
				
		for(int j=0;j<nrOfValues;j++) {
            value = new ValueDiscrete(((SimpleElement)xml_items[j]).getAttribute("value"));
            this.fEval.put(value, Double.valueOf(((SimpleElement)xml_items[j]).getAttribute("evaluation")));
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
	
}
