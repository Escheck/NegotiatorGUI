package negotiator.decoupledframework.offeringstrategy;

import java.util.HashMap;
import java.util.List;

import misc.Range;
import negotiator.bidding.BidDetails;
import negotiator.decoupledframework.NegotiationSession;
import negotiator.decoupledframework.OMStrategy;
import negotiator.decoupledframework.OfferingStrategy;
import negotiator.decoupledframework.OpponentModel;
import negotiator.decoupledframework.OutcomeSpace;

/**
 * This is an abstract class used to implement a TimeDependentAgent Strategy adapted from [1]
 * 	[1]	S. Shaheen Fatima  Michael Wooldridge  Nicholas R. Jennings
 * 		Optimal Negotiation Strategies for Agents with Incomplete Information
 * 		http://eprints.ecs.soton.ac.uk/6151/1/atal01.pdf
 * 
 * @author Alex Dirkzwager, Mark Hendrikx
 */
public class TimeDependent_Offering extends OfferingStrategy {

	/** k \in [0, 1]. For k = 0 the agent starts with a bid of maximum utility */
	private static final double k = 0;
	private double Pmax;
	private double Pmin;
	private double e;
	
	public TimeDependent_Offering(){}
	
	public void init(NegotiationSession negoSession, OpponentModel model, OMStrategy oms, HashMap<String, Double> parameters) throws Exception {
		if (parameters.get("e") != null) {
			this.e = parameters.get("e");
			initializeAgent(negoSession, model, e);
		} else {
			throw new Exception("Constant \"e\" for the concession speed was not set.");
		}
	}

	
	private void initializeAgent(NegotiationSession negoSession, OpponentModel model, double e) {
		this.negotiationSession = negoSession;
		
		OutcomeSpace space = new OutcomeSpace();
		space.init(negotiationSession.getUtilitySpace());
		negotiationSession.setOutcomeSpace(space);
		
		Pmax = negoSession.getMaxBidinDomain().getMyUndiscountedUtil();
		Pmin = negoSession.getMinBidinDomain().getMyUndiscountedUtil();
		this.e = e;
	}

	@Override
	public BidDetails determineOpeningBid() {
		return determineNextBid();
	}

	@Override
	public BidDetails determineNextBid() {
		double time = negotiationSession.getTime();
		double utilityGoal = p(time);
		if (opponentModel == null || !opponentModel.isCompleteModel()) {
			nextBid = negotiationSession.getOutcomeSpace().getBidNearUtility(utilityGoal);
		} else {
			List<BidDetails> bidsInRange = negotiationSession.getOutcomeSpace().getBidsinRange(new Range(utilityGoal, 1.1));
			omStrategy.getBid(bidsInRange);
		}
		return nextBid;
	}
	
	/**
	 * From [1]:
	 * 
	 * A wide range of time dependent functions can be defined by varying the way in
	 * which f(t) is computed. However, functions must ensure that 0 <= f(t) <= 1,
	 * f(0) = k, and f(1) = 1.
	 * 
	 * That is, the offer will always be between the value range, 
	 * at the beginning it will give the initial constant and when the deadline is reached, it
	 * will offer the reservation value.
	 */
	public double f(double t) {
		double ft = k + (1 - k) * Math.pow(t, 1 / e);
		return ft;
	}

	/**
	 * Makes sure the target utility with in the acceptable range according to the domain
	 * @param t
	 * @return double
	 */
	public double p(double t) {
		return Pmin + (Pmax - Pmin) * (1 - f(t));
	}

	@Override
	public void agentReset() { }
}