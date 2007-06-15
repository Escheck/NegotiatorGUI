package negotiator.utility;

import negotiator.Bid;
import negotiator.issue.*;
import negotiator.xml.SimpleElement;

import java.util.HashMap;

public class EvaluatorObjective implements Evaluator {
	
	// Class fields
	public double fweight; //the weight of the evaluated Objective or Issue.
	
	public EvaluatorObjective() {
		fweight = 0; //needs to be set later on.
	}

	// Class methods
	public double getWeight(){
		return fweight;
	}
	
	public void setWeight(double wt){
		fweight = wt;
	}
	
	public Double getEvaluation(UtilitySpace uspace, Bid bid, int index) {
		return 0.0; //TODO hdevos: Do what here, evaluate the bid for it's children?
	}
	
	public Double getEvaluation(ValueDiscrete value) {
		return 0.0;  //TODO hdevos: Do what here, only it's children have values. Or so i gather.
	}
	
	public double getCost(Value value) {
		return 0.0;  //TODO hdevos: Eh.. what value?
	}
	
	public double getMaxCost() {
		return 0.0;  //TODO hdevos: Same here.
	}
	
	public EVALUATORTYPE getType() {
		return EVALUATORTYPE.OBJECTIVE;
	}
	
	public void loadFromXML(SimpleElement pRoot) {
		//do nothing, we have no issues to load atm.
	}
	
}
