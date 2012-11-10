package agents;

import negotiator.Bid;
import negotiator.MultiMatchAgent;
import negotiator.actions.Action;
import negotiator.actions.Offer;

/**
 * Example of the SimpleMultiRoundAgent, or short SMRA.
 * SMRA is a simple agent which works initially offers a random
 * bid above a low breakoff. The breakoff is adapted based on the
 * success and quality of an agreement.
 * 
 * @author Mark Hendrikx
 */
public class SimpleMultiRoundAgent extends MultiMatchAgent {

	private double breakOff;
	
	/**
	 * Initializes the agent with a breakOff equal to 0.8.
	 */
	public void init() {
		breakOff = 0.8;
		System.out.println("SMRA - INIT");
	}
	
	public void beginSession() {
		System.out.println("SMRA - BEGIN SESSION " + (getSessionNumber() + 1) + " / " + getSessionsTotal());
	}

	/**
	 * If an agreement is reached, the breakoff is set to the received discounted utility;
	 * Else the breakoff value is decreased with a small constant.
	 */
	public void endSession(double result) {
		System.out.println("SMRA - END SESSION " + (getSessionNumber() + 1) + " / " + getSessionsTotal());
		if (getSessionNumber() < getSessionsTotal()) { // we don't care about after the last round
			if (result > breakOff) {
				this.breakOff = result;
				System.out.println("SMRA - INCREASED BREAKOFF TO " + breakOff);
			} else {
				this.breakOff -= 0.05;
				System.out.println("SMRA - DECREASED BREAKOFF TO " + breakOff);
			}
		}
	}

	public String getName() {
		return "SimpleMultiRoundAgent";
	}
	
	/**
	 * @return a random bid with a utility higher than the breakOff.
	 */
	@Override
	public Action chooseAction() {
		Bid bid = null;
		try {
			do {
				bid = utilitySpace.getDomain().getRandomBid();
			} while (utilitySpace.getUtility(bid) <= breakOff);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new Offer(bid);
	}
}