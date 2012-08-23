package agents.anac.y2012.IAMhaggler2012.agents2011;

import negotiator.Agent;

public abstract class VersionIndependentAgent extends Agent implements VersionIndependentAgentInterface {
	
	@Override
	public double getTime() {
		return timeline.getTime();
	}

	@Override
	public double adjustDiscountFactor(double discountFactor) {
		return discountFactor;
	}

	@Override
	public void setOpponentTime(long time) {
	}

	@Override
	public void setOurTime(long time) {
	}
}
