package negotiator.utility;

import negotiator.Bid;

public class Constraint {

	protected double weight=1.0;
	
	//The following method will be overridden by super classes (classes extending "Constraint" e.g. InclusiveHyperRectangle)
	
	public double getUtility(Bid bid) throws Exception {
		return 0.0;
	}
	
	public double getWeight() {
		return weight;
	}

	public void setWeight(double weight) {
		this.weight = weight;
	}
}