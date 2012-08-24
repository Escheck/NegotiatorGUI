package negotiator.boaframework.sharedagentstate.anac2011;

import negotiator.Timeline;
import negotiator.boaframework.NegotiationSession;
import negotiator.boaframework.OMStrategy;
import negotiator.boaframework.OpponentModel;
import negotiator.boaframework.SharedAgentState;
import negotiator.boaframework.sharedagentstate.anac2011.gahboninho.GahboninhoOM;
import negotiator.boaframework.sharedagentstate.anac2011.gahboninho.IssueManager;
import negotiator.utility.UtilitySpace;

public class GahboninhoSAS extends SharedAgentState {
	private GahboninhoOM om;
	private IssueManager im;
	private UtilitySpace utilSpace;
	private Timeline timeline;
	private int firstActions = 40;
	private OMStrategy omStrategy;
	private OpponentModel opponentModel;
	private NegotiationSession negotiationSession;
	
	public GahboninhoSAS (NegotiationSession negoSession, OpponentModel model, OMStrategy oms) {
		this.opponentModel = model;
		this.omStrategy = oms;
		this.negotiationSession = negoSession;
		this.utilSpace = negoSession.getUtilitySpace();
		this.timeline = negoSession.getTimeline();
		initObjects();
		NAME = "Gahboninho";
	}
	
	public void initObjects() {
		om = new GahboninhoOM(utilSpace, timeline);
		im = new IssueManager(negotiationSession, opponentModel, omStrategy, timeline, om);
		im.setNoise(im.getNoise() * im.GetDiscountFactor());
	}
	
	public GahboninhoOM getOpponentModel() {
		return om;
	}
	
	public IssueManager getIssueManager() {
		return im;
	}

	public int getFirstActions() {
		return firstActions;
	}

	public void decrementFirstActions() {
		--firstActions;
	}
}