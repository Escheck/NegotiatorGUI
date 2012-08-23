package agents.anac.y2012.TheNegotiatorReloaded;

/**
 * @author Colin Williams
 * 
 */
public class WeightHypothesis extends Hypothesis {

	double weight;

	public void setWeight(double weight) {
		this.weight = weight;
	}

	public double getWeight() {
		return this.weight;
	}

	public String toString() {
		return String.format("%1.2f", this.weight) + ";";
	}
}
