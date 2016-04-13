package negotiator.events;

import java.util.ArrayList;

import negotiator.Bid;

public class MultipartyNegotiationOfferEvent extends NegotiationEvent {
	private int round;
	private int turn;
	private double timeline; // current run time in seconds.
	private ArrayList<Double> partyUtilities;
	private boolean agreementFound;
	private Bid bid;

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
	 *            the discounted utils of the parties
	 * @param agreementFound
	 *            true iff the parties agreed
	 */
	public MultipartyNegotiationOfferEvent(Object source, Bid bid, int round,
			int turn, double time, ArrayList<Double> partyUtilities,
			boolean agreementFound) {
		super(source);
		this.bid = bid;
		this.round = round;
		this.turn = turn;
		this.timeline = time;
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
	 * @return the discounted utils of the parties
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
		return "MultipartyNegotiationOfferEvent[" + bid + " at " + round
				+ " round]";
	}

}
