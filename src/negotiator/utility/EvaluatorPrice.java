package negotiator.utility;

import negotiator.Bid;
import negotiator.issue.*;
import negotiator.xml.SimpleElement;

public class EvaluatorPrice implements Evaluator {
	
	// Class fields
	double lowerBound;
	double upperBound;
	double maxMargin = -1;
	double rationalityfactor=0;
	
	public EvaluatorPrice() {
	}

	// Class methods
	public Double getEvaluation(UtilitySpace uspace, Bid bid, int index) {
		int nrOfEvals;
		double price, costs = 0, maxCost = 0, profit, utility;
		
		price = ((ValueReal)bid.getValue(index)).getValue();
		
		// Collect costs for discrete-valued associated with other issues in bid.
		nrOfEvals = uspace.getNrOfEvaluators();
		for (int i=0; i<nrOfEvals; i++) {
			if (uspace.getEvaluator(i).getType()==EVALUATORTYPE.DISCRETE) {
				costs += ((EvaluatorDiscrete)uspace.getEvaluator(i)).getCost(bid.getValue(i));
				maxCost += ((EvaluatorDiscrete)uspace.getEvaluator(i)).getMaxCost();
			}
		}
		
		// Compute profit.
		// The variable lowerBound represents the basic costs associated with any deal.
		// Selling for a price less than lowerBound would mean making a loss.
		profit = price - costs - lowerBound;
		
		// Compute maximal margin if not known yet.
		if (maxMargin==-1) {
			maxMargin = (upperBound - lowerBound - maxCost)/lowerBound;
		}
		utility = profit/(maxMargin*lowerBound);
		if (utility<0)
			utility = 0;
		else if (utility>1)
			utility = 1;
		return utility;
	}
	public ValueReal getValueByEvaluation(UtilitySpace uspace, Bid bid, double pTargetUtility) {
		double lPrice = 0;
		double costs = 0, maxCost = 0, profit, utility;
		// Collect costs for discrete-valued associated with other issues in bid.
		int nrOfEvals = uspace.getNrOfEvaluators();
		for (int i=0; i<nrOfEvals; i++) {
			if (uspace.getEvaluator(i).getType()==EVALUATORTYPE.DISCRETE) {
				costs += ((EvaluatorDiscrete)uspace.getEvaluator(i)).getCost(bid.getValue(i));
				maxCost += ((EvaluatorDiscrete)uspace.getEvaluator(i)).getMaxCost();
			}
		}
		
		// Compute profit.
		// The variable lowerBound represents the basic costs associated with any deal.
		// Selling for a price less than lowerBound would mean making a loss.

		//ASSUMPTION: maxMargin is computed
		// Compute maximal margin if not known yet.
		if (maxMargin==-1) {
			maxMargin = (upperBound - lowerBound - maxCost)/lowerBound;
		}
//		utility = profit/(maxMargin*lowerBound);
//		profit = price - costs - lowerBound;
		profit = pTargetUtility*maxMargin*lowerBound;
		lPrice = profit+costs+lowerBound; 
		if (lPrice<lowerBound)
			lPrice= lowerBound;
		else if (lPrice>upperBound)
			lPrice= upperBound;
		return new ValueReal(lPrice);
		
	}
	
	public EVALUATORTYPE getType() {
		return EVALUATORTYPE.PRICE;
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
		xml_item = ((SimpleElement)pRoot).getChildByTagName("rationality_factor");
		this.rationalityfactor = Double.valueOf(((SimpleElement)xml_item[0]).getAttribute("value"));
	}
	
}
