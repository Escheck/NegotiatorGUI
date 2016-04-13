package negotiator.events;

import java.util.List;

import negotiator.Bid;

public class MultipartyNegotiationOfferEvent extends NegotiationEvent {
	private int round;
	private int turn;
	private double timeline; // current run time in seconds.
	private List<Double> partyUtilities;
	private Bid bid;
	private List<Double> agreementUtils;

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
	 * @param agreementUtils
	 *            the utilities of the {@link Bid} that the parties agreed on ,
	 *            or null if no agreement yet.
	 */
	public MultipartyNegotiationOfferEvent(Object source, Bid bid, int round,
			int turn, double time, List<Double> partyUtilities,
			List<Double> agreementUtils) {
		super(source);
		this.bid = bid;
		this.round = round;
		this.turn = turn;
		this.timeline = time;
		this.partyUtilities = partyUtilities;
		this.agreementUtils = agreementUtils;
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
	public List<Double> getPartyUtilities() {
		return partyUtilities;
	}

	/**
	 * 
	 * @return The bid the parties agreed on, or null if no such agreement.
	 */
	public List<Double> getAgreementUtils() {
		return agreementUtils;
	}

	public String toString() {
		return "MultipartyNegotiationOfferEvent[" + bid + " at " + round
				+ " round]";
	}

}
