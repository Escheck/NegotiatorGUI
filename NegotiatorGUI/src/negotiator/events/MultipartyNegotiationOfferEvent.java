package negotiator.events;

import java.util.ArrayList;

import negotiator.actions.Action;

public class MultipartyNegotiationOfferEvent extends NegotiationEvent {
	private int round;
	private int turn;
	private double timeline; // current run time in seconds.
	private Action action; // action also keeps the party id
	private ArrayList<Double> partyUtilities;
	private boolean agreementFound;

	/**
	 * @param source
	 *            the source = owner of the logger
	 * @param round
	 *            the current round number
	 * @param turn
	 *            the turn within the round
	 * @param time
	 *            the current run time in seconds
	 * @param action
	 *            the most recent action in the nego
	 * @param partyUtilities
	 *            the undiscounted utils of the parties
	 * @param agreementFound
	 *            true iff the parties agreed
	 */
	public MultipartyNegotiationOfferEvent(Object source, int round, int turn,
			double time, Action action, ArrayList<Double> partyUtilities,
			boolean agreementFound) {
		super(source);
		this.round = round;
		this.turn = turn;
		this.timeline = time;
		this.action = action;
		this.partyUtilities = partyUtilities;
		this.agreementFound = agreementFound;
	}

	/**
	 * 
	 * @return the current round number
	 */
	public int getRound() {
		return round;
	}

	/**
	 * 
	 * @return the current turn within this round
	 */
	public int getTurn() {
		return turn;
	}

	/**
	 * 
	 * @return the current run time (in seconds since start of this round)
	 */
	public double getTimeline() {
		return timeline;
	}

	/**
	 * 
	 * @return the most recent action in the nego
	 */
	public Action getAction() {
		return action;
	}

	/**
	 * 
	 * @return the undiscounted utils of the parties
	 */
	public ArrayList<Double> getPartyUtilities() {
		return partyUtilities;
	}

	/**
	 * 
	 * @return true iff the parties agreed
	 */
	public boolean getAgreementFound() {
		return agreementFound;
	}

	public String toString() {
		return "ActionEvent[" + action.getAgent() + "," + action + " at "
				+ round + " round]";
	}

}
