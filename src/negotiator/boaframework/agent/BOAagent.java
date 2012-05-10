package negotiator.boaframework.agent;

import java.util.ArrayList;

import misc.Pair;

import negotiator.Agent;
import negotiator.Bid;
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
import negotiator.boaframework.OutcomeTuple;
import negotiator.boaframework.acceptanceconditions.Multi_AcceptanceCondition;
import negotiator.boaframework.opponentmodel.NullModel;

/**
 * This class describes a basic decoupled agent. The TheDecoupledAgent class extends
 * this class and sets the required parameters.
 * 
 * @author Alex Dirkzwager
 */
public abstract class BOAagent extends Agent {

	/** when to accept */
	protected AcceptanceStrategy acceptConditions;
	/** link to domain */
	protected NegotiationSession negotiationSession;
	/** what to offer */
	protected OfferingStrategy offeringStrategy;
	/**  used to determine the utility of a bid for the opponent */
	protected OpponentModel opponentModel;
	/** which bid to select using an opponent model */
	protected OMStrategy omStrategy;
	/** used to store MAC outcomes */
    public ArrayList<Pair<Bid, String>> savedOutcomes;
    /** space of possible bids */
    protected OutcomeSpace outcomeSpace;
    /** if this agent started */
    private boolean startingAgent;
    
    @Override
	public void init() {
		super.init();
		negotiationSession = new NegotiationSession(utilitySpace, timeline);
		agentSetup();
	}
	
	/**
	 * Method used to setup the agent. The method is called directly after
	 * initialization of the agent.
	 */
	public abstract void agentSetup();
	
	/**
	 * Set the components of the decoupled agent.
	 * 
	 * @param ac the acceptance strategy
	 * @param os the offering strategy
	 * @param om the opponent model
	 * @param oms the opponent model strategy
	 */
	public void setDecoupledComponents(AcceptanceStrategy ac, OfferingStrategy os, OpponentModel om, OMStrategy oms) {
		acceptConditions = ac;
		offeringStrategy = os;
		opponentModel = om;
		omStrategy = oms;
	}
	
	public static String getVersion() { return "1.0"; }
	
	public abstract String getName();
	
	/**
	 * Store the actions made by a partner.
	 * First store the bid in the history, then update the opponent model.
	 * 
	 * @param Action by opponent in current turn
	 */
	public void ReceiveMessage(Action opponentAction) {
		if(opponentAction instanceof Offer) {
			Bid bid = ((Offer)opponentAction).getBid();
			try {
				BidDetails opponentBid = new BidDetails(bid, negotiationSession.getUtilitySpace().getUtility(bid), negotiationSession.getTime());
				negotiationSession.getOpponentBidHistory().add(opponentBid);
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (opponentModel != null && !(opponentModel instanceof NullModel)) {
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
	 * Choose an action to perform.
	 * 
	 * @return Action the agent performs
	 */
	@Override
	public Action chooseAction() {

		BidDetails bid;
		
		// if our history is empty, then make an opening bid
		if(negotiationSession.getOwnBidHistory().getHistory().isEmpty()){
			bid = offeringStrategy.determineOpeningBid();
		} else {
			// else make a normal bid
			bid = offeringStrategy.determineNextBid();
		}
		
		// if the offering strategy made a mistake and didn't set a bid: accept
		if (bid == null) {
			System.out.println("Error in code, null bid was given");
			return new Accept(this.getAgentID());
		} else {
			offeringStrategy.setNextBid(bid);
		}
		
		// check if the opponent bid should be accepted
		Actions decision = Actions.Reject;
		if (!negotiationSession.getOpponentBidHistory().getHistory().isEmpty()) {
			decision = acceptConditions.determineAcceptability();
		} else {
			startingAgent = true;
		}
		
		if (decision.equals(Actions.Break)) {
			return new EndNegotiation(this.getAgentID());
		}
		//if agent does not accept, it offers the counter bid
		if(decision.equals(Actions.Reject)){
			negotiationSession.getOwnBidHistory().add(bid);
			return new Offer(this.getAgentID(), bid.getBid());
		} else {
			return new Accept(this.getAgentID());
		}
	}
	
	/**
	 * Returns the negotiation outcomes saved by the MAC.
	 * @return outcomes saved by MAC.
	 */
	public ArrayList<OutcomeTuple> getSavedOutcomes() {
		if(acceptConditions instanceof Multi_AcceptanceCondition) {
			if(((Multi_AcceptanceCondition) acceptConditions).getACList().isEmpty()){
				return ((Multi_AcceptanceCondition) acceptConditions).getOutcomes();
			} else{
				syncSavedOutcomes();
				return ((Multi_AcceptanceCondition) acceptConditions).getOutcomes();

			}
		}
		return null;
	}
	
	public OpponentModel getOpponentModel() {
		return opponentModel;
	}
	
	/**
	 * Sync the outcomes stored by the agent for the MAC.
	 */
	public void syncSavedOutcomes(){
		for(AcceptanceStrategy strat : ((Multi_AcceptanceCondition) acceptConditions).getACList()) {
			String name = strat.getClass().getSimpleName() + " " + ((Multi_AcceptanceCondition)acceptConditions).printParameters(strat);
			OutcomeTuple newTuple;
			if(negotiationSession.getTime() < 1.0){
				if(startingAgent)
					newTuple = new OutcomeTuple(negotiationSession.getOwnBidHistory().getLastBidDetails().getBid(), name, negotiationSession.getTime(), negotiationSession.getOwnBidHistory().size(), negotiationSession.getOpponentBidHistory().size());
				else
					newTuple = new OutcomeTuple(negotiationSession.getOwnBidHistory().getLastBidDetails().getBid(), name, negotiationSession.getTime(), negotiationSession.getOpponentBidHistory().size(), negotiationSession.getOwnBidHistory().size());
			}else {
				newTuple = new OutcomeTuple(null, name, 1, -1, -1);
			}
			((Multi_AcceptanceCondition) acceptConditions).getOutcomes().add(newTuple);
		}
		((Multi_AcceptanceCondition) acceptConditions).getACList().clear();
	}

	/**
	 * Clear the agent's variables.
	 */
	public void cleanUp() 
	{
		offeringStrategy = null;
		acceptConditions = null;
		omStrategy = null;
		opponentModel = null;
		outcomeSpace = null;
		negotiationSession = null;
	}
}