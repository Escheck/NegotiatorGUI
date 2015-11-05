package negotiator.boaframework;

import negotiator.Bid;
import negotiator.issue.Issue;
import negotiator.protocol.BilateralAtomicNegotiationSession;
import negotiator.utility.AdditiveUtilitySpace;

import java.io.Serializable;
import java.util.HashMap;

/**
 * Describes an opponent model of an agent of the BOA framework.
 * 
 * Tim Baarslag, Koen Hindriks, Mark Hendrikx, Alex Dirkzwager and Catholijn M.
 * Jonker. Decoupling Negotiating Agents to Explore the Space of Negotiation
 * Strategies
 * 
 * @author Alex Dirkzwager, Mark Hendrikx
 */
public abstract class OpponentModel extends BOA {

	/** Reference to the estimated opponent's utility state */
	protected AdditiveUtilitySpace opponentUtilitySpace;
	/** Boolean to indicate that the model has been cleared to free resources */
	private boolean cleared;

	/**
	 * Initializes the model. The init method should always be called after
	 * creating an opponent model.
	 * 
	 * @param negotiationSession
	 *            reference to the state of the negotiation
	 * @param parameters
	 * @throws Exception
	 */
	public void init(NegotiationSession negotiationSession,
			HashMap<String, Double> parameters) throws Exception {
		super.init(negotiationSession);
		opponentUtilitySpace = new AdditiveUtilitySpace(
				negotiationSession.getUtilitySpace());
		cleared = false;
	}

	/**
	 * Alternative init method to initialize the model without setting
	 * parameters.
	 * 
	 * @param negotiationSession
	 *            reference to the state of the negotiation
	 */
	public void init(NegotiationSession negotiationSession) {
		this.negotiationSession = negotiationSession;
		opponentUtilitySpace = new AdditiveUtilitySpace(
				negotiationSession.getUtilitySpace());
		cleared = false;
	}

	/**
	 * Method used to receiveMessage the opponent model.
	 * 
	 * @param opponentBid
	 */
	public void updateModel(Bid opponentBid) {
		updateModel(opponentBid, negotiationSession.getTime());
	}

	/**
	 * Method used to receiveMessage the opponent model.
	 * 
	 * @param bid
	 *            to receiveMessage the model with.
	 * @param time
	 *            at which the bid was offered.
	 */
	public abstract void updateModel(Bid bid, double time);

	/**
	 * Determines the utility of a bid according to the preference profile.
	 * 
	 * @param bid
	 *            of which the utility is calculated.
	 * @return Utility of the bid
	 */
	public double getBidEvaluation(Bid bid) {
		try {
			return opponentUtilitySpace.getUtility(bid);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return -1;
	}

	/**
	 * @return the estimated utility space of the opponent
	 */
	public AdditiveUtilitySpace getOpponentUtilitySpace() {
		return opponentUtilitySpace;
	}

	/**
	 * Method which may be overwritten by an opponent model to get access to the
	 * opponent's utilityspace.
	 * 
	 * @param fNegotiation
	 */
	public void setOpponentUtilitySpace(
			BilateralAtomicNegotiationSession fNegotiation) {
	}

	/**
	 * Method which may be overwritten by an opponent model to get access to the
	 * opponent's utilityspace.
	 * 
	 * @param opponentUtilitySpace
	 */
	public void setOpponentUtilitySpace(AdditiveUtilitySpace opponentUtilitySpace) {
	}

	/**
	 * Returns the weight of a particular issue in the domain.
	 * 
	 * @param issue
	 *            from which the weight should be returned
	 * @return weight of the given issue
	 */
	public double getWeight(Issue issue) {
		return opponentUtilitySpace.getWeight(issue.getNumber());
	}

	/**
	 * @return set of all estimated issue weights.
	 */
	public double[] getIssueWeights() {
		double estimatedIssueWeights[] = new double[negotiationSession
				.getUtilitySpace().getDomain().getIssues().size()];
		int i = 0;
		for (Issue issue : negotiationSession.getUtilitySpace().getDomain()
				.getIssues()) {
			estimatedIssueWeights[i] = getWeight(issue);
			i++;
		}
		return estimatedIssueWeights;
	}

	/**
	 * Removes references to the objects used by the opponent model.
	 */
	public void cleanUp() {
		negotiationSession = null;
		cleared = true;
	}

	/**
	 * @return if the opponent model is in a usable state.
	 */
	public boolean isCleared() {
		return cleared;
	}

	/**
	 * @return name of the opponent model.
	 */
	public String getName() {
		return "Default";
	}

	@Override
	public final void storeData(Serializable object) {
		negotiationSession.setData(ComponentsEnum.OPPONENTMODEL, object);
	}

	@Override
	public final Serializable loadData() {
		return negotiationSession.getData(ComponentsEnum.OPPONENTMODEL);
	}

}
