package negotiator.boaframework.offeringstrategy.anac2012.TheNegotiatorReloaded;

import negotiator.boaframework.NegotiationSession;
import negotiator.boaframework.OMStrategy;
import negotiator.boaframework.OpponentModel;

public class TimeManager {
	
	private NegotiationSession negoSession;
	private DomainAnalyzer domainAnalyzer;
	private BSPredictor bsPredictor;
	private double windowInterval;
	private int currentWindow;
	private double kalai;
	private StrategyTypes opponentStrategy;
	
	public TimeManager(NegotiationSession negoSession, OpponentModel opponentModel, OMStrategy omStrategy, int numberOfWindows) {
		this.negoSession = negoSession;
		this.domainAnalyzer = new DomainAnalyzer(negoSession.getUtilitySpace(), opponentModel, omStrategy);
		this.bsPredictor = new BSPredictor(negoSession, numberOfWindows);
		this.windowInterval = 180 / numberOfWindows;
		this.currentWindow = 0;
	}
	
	public boolean checkEndOfWindow() {
		if (Math.floor((negoSession.getTime() * 180) / windowInterval) != currentWindow) {		
			kalai = domainAnalyzer.calculateKalaiPoint();
			opponentStrategy = bsPredictor.calculateOpponentStrategy();
			currentWindow++;
			return true;
		}
		return false;
	}

	/**
	 * @return estimated Kalai point for agent A.
	 */
	public double getKalai() {
		return kalai;
	}
	
	/**
	 * @return estimated opponent's strategy (Conceder or Hardliner).
	 */
	public StrategyTypes getOpponentStrategy() {
		return opponentStrategy;
	}
}