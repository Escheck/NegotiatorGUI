package negotiator.boaframework.agent;

import java.io.Serializable;
import java.util.ArrayList;

import misc.Pair;
import negotiator.Agent;
import negotiator.Bid;
import negotiator.NegotiationResult;
import negotiator.actions.Accept;
import negotiator.actions.Action;
import negotiator.actions.EndNegotiation;
import negotiator.actions.Offer;
import negotiator.bidding.BidDetails;
import negotiator.boaframework.AcceptanceStrategy;
import negotiator.boaframework.Actions;
import negotiator.boaframework.NegotiationSession;
import negotiator.boaframework.OMStrategy;
import negotiator.boaframework.OfferingStrategy;
import negotiator.boaframework.OpponentModel;
import negotiator.boaframework.OutcomeSpace;
import negotiator.boaframework.SessionData;
import negotiator.boaframework.acceptanceconditions.other.Multi_AcceptanceCondition;
import negotiator.boaframework.opponentmodel.NoModel;

/**
 * This class describes a basic decoupled agent. The TheBOAagent class extends
 * this class and sets the required parameters.
 * 
 * For more information, see: Baarslag T., Hindriks K.V., Hendrikx M.,
 * Dirkzwager A., Jonker C.M. Decoupling Negotiating Agents to Explore the Space
 * of Negotiation Strategies. Proceedings of The Fifth International Workshop on
 * Agent-based Complex Automated Negotiations (ACAN 2012), 2012.
 * http://mmi.tudelft.nl/sites/default/files/boa.pdf
 * 
 * @author Tim Baarslag, Alex Dirkzwager, Mark Hendrikx
 */
public abstract class BOAagent extends Agent {
	/** Decides when to accept */
	protected AcceptanceStrategy acceptConditions;
	/** Decides what to offer */
	protected OfferingStrategy offeringStrategy;
	/** Approximates the utility of a bid for the opponent */
	protected OpponentModel opponentModel;
	/** Links to the negotiation domain */
	protected NegotiationSession negotiationSession;
	/** Selects which bid to send when using an opponent model */
	protected OMStrategy omStrategy;
	/** Store {@link Multi_AcceptanceCondition} outcomes */
	public ArrayList<Pair<Bid, String>> savedOutcomes;
	/** Contains the space of possible bids */
	protected OutcomeSpace outcomeSpace;

	/**
	 * Initializes the agent and creates a new negotiation session object.
	 */
	@Override
	public void init() {
		super.init();
		Serializable storedData = this.loadSessionData();
		SessionData sessionData;
		if (storedData == null) {
			sessionData = new SessionData();
		} else {
			sessionData = (SessionData) storedData;
		}
		negotiationSession = new NegotiationSession(sessionData, utilitySpace,
				timeline);
		agentSetup();
	}

	/**
	 * Method used to setup the agent. The method is called directly after
	 * initialization of the agent.
	 */
	public abstract void agentSetup();

	/**
	 * Sets the components of the decoupled agent.
	 * 
	 * @param ac
	 *            the acceptance strategy
	 * @param os
	 *            the offering strategy
	 * @param om
	 *            the opponent model
	 * @param oms
	 *            the opponent model strategy
	 */
	public void setDecoupledComponents(AcceptanceStrategy ac,
			OfferingStrategy os, OpponentModel om, OMStrategy oms) {
		acceptConditions = ac;
		offeringStrategy = os;
		opponentModel = om;
		omStrategy = oms;
	}

	/**
	 * Unique identifier for the BOA agent. The default method in agent does not
	 * suffice as all BOA agents have the same classpath.
	 */
	protected String getUniqueIdentifier() {
		return getName().hashCode() + "";
	}

	@Override
	public String getVersion() {
		return "1.0";
	}

	public abstract String getName();

	/**
	 * Stores the actions made by a partner. First, it stores the bid in the
	 * history, then updates the opponent model.
	 * 
	 * @param opponentAction
	 *            by opponent in current turn
	 */
	public void ReceiveMessage(Action opponentAction) {
		// 1. if the opponent made a bid
		if (opponentAction instanceof Offer) {
			Bid bid = ((Offer) opponentAction).getBid();
			// 2. store the opponent's trace
			try {
				BidDetails opponentBid = new BidDetails(bid, negotiationSession
						.getUtilitySpace().getUtility(bid),
						negotiationSession.getTime());
				negotiationSession.getOpponentBidHistory().add(opponentBid);
			} catch (Exception e) {
				e.printStackTrace();
			}
			// 3. if there is an opponent model, update it using the opponent's
			// bid
			if (opponentModel != null && !(opponentModel instanceof NoModel)) {
				if (omStrategy.canUpdateOM()) {
					opponentModel.updateModel(bid);
				} else {
					if (!opponentModel.isCleared()) {
						opponentModel.cleanUp();
					}
				}
			}
		}
	}

	/**
	 * Chooses an action to perform.
	 * 
	 * @return Action the agent performs
	 */
	@Override
	public Action chooseAction() {

		BidDetails bid;

		// if our history is empty, then make an opening bid
		if (negotiationSession.getOwnBidHistory().getHistory().isEmpty()) {
			bid = offeringStrategy.determineOpeningBid();
		} else {
			// else make a normal bid
			bid = offeringStrategy.determineNextBid();
			if (offeringStrategy.isEndNegotiation()) {
				return new EndNegotiation();
			}
		}

		// if the offering strategy made a mistake and didn't set a bid: accept
		if (bid == null) {
			System.out.println("Error in code, null bid was given");
			return new Accept();
		} else {
			offeringStrategy.setNextBid(bid);
		}

		// check if the opponent bid should be accepted
		Actions decision = Actions.Reject;
		if (!negotiationSession.getOpponentBidHistory().getHistory().isEmpty()) {
			decision = acceptConditions.determineAcceptability();
		}

		// check if the agent decided to break off the negotiation
		if (decision.equals(Actions.Break)) {
			System.out.println("send EndNegotiation");
			return new EndNegotiation();
		}
		// if agent does not accept, it offers the counter bid
		if (decision.equals(Actions.Reject)) {
			negotiationSession.getOwnBidHistory().add(bid);
			return new Offer(bid.getBid());
		} else {
			return new Accept();
		}
	}

	/**
	 * Returns the offering strategy of the agent.
	 * 
	 * @return offeringstrategy of the agent.
	 */
	public OfferingStrategy getOfferingStrategy() {
		return offeringStrategy;
	}

	/**
	 * Returns the opponent model of the agent.
	 * 
	 * @return opponent model of the agent.
	 */
	public OpponentModel getOpponentModel() {
		return opponentModel;
	}

	/**
	 * Returns the acceptance strategy of the agent.
	 * 
	 * @return acceptance strategy of the agent.
	 */
	public AcceptanceStrategy getAcceptanceStrategy() {
		return acceptConditions;
	}

	/**
	 * Method that first calls the endSession method of each component to update
	 * the session data and then stores the session data if it is not empty and
	 * is changed.
	 */
	public void endSession(NegotiationResult result) {
		offeringStrategy.endSession(result);
		acceptConditions.endSession(result);
		opponentModel.endSession(result);
		SessionData savedData = negotiationSession.getSessionData();
		if (!savedData.isEmpty() && savedData.isChanged()) {
			savedData.changesCommitted();
			saveSessionData(savedData);
		}
	}

	/**
	 * Clears the agent's variables.
	 */
	public void cleanUp() {
		offeringStrategy = null;
		acceptConditions = null;
		omStrategy = null;
		opponentModel = null;
		outcomeSpace = null;
		negotiationSession = null;
	}
}