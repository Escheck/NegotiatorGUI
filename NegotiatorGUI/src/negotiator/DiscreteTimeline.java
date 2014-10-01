package negotiator;

/**
 * Implementation of the timeline in which time is divided in rounds. Time does
 * not pass within a round. Note that requesting the total time is in this case
 * undefined.
 * 
 * NOTICE: DiscreteTimeline assumes each action of a player a 'round'. Normally
 * this would be called a 'turn' I think.
 */
public class DiscreteTimeline extends Timeline {
	/**
	 * Total number of rounds +1. With 3 rounds, this is set to 4. DOC: This is
	 * HACK: it is because it 'makes more sense to add one, as in this case
	 * "totalrounds" is still before the deadline as the deadline is on time >=
	 * 1.0'
	 */
	private int totalRounds;
	/**
	 * Current round. E.g. with 3 rounds, it takes the values 1, 2, 3, and on 4
	 * is the deadline.
	 */
	protected int cRound;

	/**
	 * Creates a timeline with a deadline of {@link #totalRounds} number of
	 * rounds.
	 */
	public DiscreteTimeline(int totalRounds) {
		// makes more sense to add one, as in this case "totalrounds" is still
		// before the deadline as the
		// deadline is on time >= 1.0
		this.totalRounds = totalRounds + 1;
		hasDeadline = true;
		cRound = 1;
	}

	/**
	 * Prints time in seconds
	 */
	public void printRoundElapsed() {
		System.out.println("Elapsed Rounds: " + cRound);
	}

	/**
	 * Prints time, running from t = 0 (start) to t = 1 (deadline).
	 */
	@Override
	public void printTime() {
		System.out.println("t = " + getTime());
	}

	/**
	 * Gets the time, running from t = 0 (start) to t = 1 (deadline). The time
	 * is normalized, so agents need not be concerned with the actual internal
	 * clock. Please use {@link Agent#wait(double)} for pausing the agent.
	 */
	@Override
	public double getTime() {
		if (Global.AAMAS_2014_EXPERIMENTS && totalRounds == 3) {
			if (cRound == 1 || cRound == 2)
				return 0.99;
			else if (cRound == 3) // deadline
				return 1;
			else
				new IllegalStateException("Illegal time " + cRound);
		}
		double t = (double) cRound / (double) totalRounds;
		if (t > 1)
			t = 1;
		return t;
	}

	/**
	 * go to next round. <br>
	 * <b>WARNING</b> increment does not check against the upper limit
	 * {@link #totalRounds}. Therefore, it can become more than the total roudns
	 * available.
	 */
	public void increment() {
		cRound++;
	}

	/**
	 * The DiscreteTimeline does not have to be paused/resumed, as time does not
	 * pass within a round.
	 */
	public void pause() {
	}

	/**
	 * The DiscreteTimeline does not have to be paused/resumed, as time does not
	 * pass within a round.
	 */
	public void resume() {
	}

	/**
	 * This a hack because in Agent.java a method sleep requires this
	 */
	@Override
	public double getTotalTime() {
		return totalRounds;
	}

	/**
	 * Starting to count from 1, until the total amount of rounds.
	 * <b>WARNING</b> this can return numbers > {@link #totalRounds} as well.
	 */
	public int getRound() {
		return cRound;
	}

	/**
	 * 
	 * @return rounds left. Decreases with every turn. If down to 0, it means
	 *         that this is the last round (turn). If < 0, it means that the
	 *         deadline has been passed already.
	 */
	public int getRoundsLeft() {
		return totalRounds - cRound - 1;
	}

	/**
	 * get total number of rounds +1 Be careful, this is actually the
	 * totalRounds+1!
	 *
	 * @return total rounds +1.
	 */
	public int getTotalRounds() {
		return totalRounds;
	}

	/**
	 * The total number of rounds for ourself. Be careful, this is not equal to
	 * the initializing value! <b>WARNING</b>:: gives incorrect value if odd
	 * number of rounds remaining.
	 * 
	 */
	public int getOwnTotalRounds() {
		return (int) Math.floor(getTotalRounds() / 2);
	}

	/**
	 * The number of rounds left for ourself. This is half of the total number
	 * of rounds left. In the last round, this is 0. <b>WARNING</b>: may return
	 * negative values if we pass beyond the total number of planned rounds.
	 */
	public int getOwnRoundsLeft() {
		return (int) Math.floor(getRoundsLeft() / 2);
	}

	public Type getType() {
		return Type.Rounds;
	}

	@Override
	public double getCurrentTime() {
		return cRound;
	}
}