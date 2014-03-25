package negotiator.boaframework;

import java.io.Serializable;
import java.util.HashMap;

import negotiator.Bid;
import negotiator.NegotiationResult;
import negotiator.issue.Issue;
import negotiator.protocol.BilateralAtomicNegotiationSession;
import negotiator.utility.UtilitySpace;

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

	/** Reference to the negotiation state */
	protected NegotiationSession negotiationSession;
	/** Reference to the estimated opponent's utility state */
	protected UtilitySpace opponentUtilitySpace;
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
		this.negotiationSession = negotiationSession;
		opponentUtilitySpace = new UtilitySpace(
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
		opponentUtilitySpace = new UtilitySpace(
				negotiationSession.getUtilitySpace());
		cleared = false;
	}

	/**
	 * Method used to update the opponent model.
	 * 
	 * @param opponentBid
	 */
	public void updateModel(Bid opponentBid) {
		updateModel(opponentBid, negotiationSession.getTime());
	}

	/**
	 * Method used to update the opponent model.
	 * 
	 * @param bid
	 *            to update the model with.
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
	public UtilitySpace getOpponentUtilitySpace() {
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
	public void setOpponentUtilitySpace(UtilitySpace opponentUtilitySpace) {
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

	/**
	 * Method used to store data that should be accessible in the next
	 * negotiation session on the same scenario. This method can be called
	 * during the negotiation, but it makes more sense to call it in the
	 * endSession method.
	 * 
	 * @param object
	 *            to be saved by this component.
	 */
	public final void storeData(Serializable object) {
		negotiationSession.setData(ComponentsEnum.OPPONENTMODEL, object);
	}

	/**
	 * Method used to load the saved object, possibly created in a previous
	 * negotiation session. The method returns null when such an object does not
	 * exist yet.
	 * 
	 * @return saved object or null when not available.
	 */
	public final Serializable loadData() {
		return negotiationSession.getData(ComponentsEnum.OPPONENTMODEL);
	}

	/**
	 * Method called at the end of the negotiation. Ideal location to call the
	 * storeData method to update the data to be saved.
	 * 
	 * @param result
	 *            of the negotiation.
	 */
	public void endSession(NegotiationResult result) {
	}
}
