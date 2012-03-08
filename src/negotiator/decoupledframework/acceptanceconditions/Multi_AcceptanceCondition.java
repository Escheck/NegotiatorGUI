package negotiator.decoupledframework.acceptanceconditions;

import java.util.ArrayList;
import java.util.HashMap;
import negotiator.Bid;
import negotiator.decoupledframework.AcceptanceStrategy;
import negotiator.decoupledframework.NegotiationSession;
import negotiator.decoupledframework.OfferingStrategy;
import negotiator.decoupledframework.OutcomeTuple;

/**
 * The MAC is a tool which allows to test many acceptance strategies in the
 * same negotiation trace. Each AC generates an outcome, which is saved separately.
 * Note that while this tool allows to test a large amount of AC's in the same trace,
 * there is a computational cost. Therefore we recommend to use at most 50 AC's.
 * 
 * @author Alex Dirkzwager
 */
public class Multi_AcceptanceCondition extends AcceptanceStrategy {

	// list of acceptance strategies to checked for acceptance
	private ArrayList<AcceptanceStrategy> ACList;
	// list of outcomes; an outcome is added when an AC accepts, or the negotiation ended
	private ArrayList<OutcomeTuple> outcomes;
	
	public Multi_AcceptanceCondition() { }

	@Override
	public void init(NegotiationSession negoSession, OfferingStrategy strat, HashMap<String, Double> parameters) throws Exception {
		this.negotiationSession = negoSession;
		outcomes = new ArrayList<OutcomeTuple> ();
		ACList = new ArrayList<AcceptanceStrategy>();
		
		// EXAMPLE: add 36 variants of the AC_CombiV4 acceptance condition
		for(int d = 0; d <= 35; d += 1) {
			ACList.add(new AC_CombiV4(negoSession, strat, 1, 0, 0.85 + d * 0.01, 0, 0.95));
		}	
	}
	
	/**
	 * The main method of the MAC. This method ensures that every acceptance condition is
	 * checked, and that the outcome for each AC is saved.
	 */
	@Override
	public boolean determineAcceptability() {
		boolean startingAgent = false;
		
		if(!startingAgent && negotiationSession.getOpponentBidHistory().getHistory().isEmpty()){
			startingAgent = true;
		}
		
		ArrayList<AcceptanceStrategy> acceptors = new ArrayList<AcceptanceStrategy>();
		for (AcceptanceStrategy a : ACList) {
			if (a.determineAcceptability()) {
				Bid lastOpponentBid = negotiationSession.getOpponentBidHistory().getLastBidDetails().getBid();
				String name = a.getClass().getSimpleName() + " " + printParameters(a);
				double time = negotiationSession.getTime();
				System.out.println(name + " accepted Bid Starting: " + startingAgent);

				OutcomeTuple outcome;
				if(startingAgent) {
					outcome = new OutcomeTuple(lastOpponentBid, name, time, negotiationSession.getOwnBidHistory().size(), negotiationSession.getOpponentBidHistory().size());
				}else {
					outcome = new OutcomeTuple(lastOpponentBid, name, time, negotiationSession.getOpponentBidHistory().size(), negotiationSession.getOwnBidHistory().size());

				}
				outcomes.add(outcome);
				acceptors.add(a);
			}
		}
		ACList.removeAll(acceptors);
		
		return (ACList.isEmpty());
	}
	
	public ArrayList<OutcomeTuple> getOutcomes() {
		return outcomes;
	}
	
	public ArrayList<AcceptanceStrategy> getACList() {
		return ACList;
	}

	public String printParameters(AcceptanceStrategy a) {
		return a.printParameters();
	}

	@Override
	public String printParameters() {
		return null;
	}
}
