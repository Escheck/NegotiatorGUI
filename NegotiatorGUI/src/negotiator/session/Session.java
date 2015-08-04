package negotiator.session;

import static java.lang.Math.pow;

import java.util.ArrayList;

import negotiator.ContinuousTimeline;
import negotiator.Deadline;
import negotiator.DeadlineType;
import negotiator.DiscreteTimeline;
import negotiator.actions.Action;
import negotiator.protocol.MultilateralProtocol;

/**
 * Represents a negotiation session. A {@link Session} consists of {@link Round}
 * with in turn consists of {@link Turn}. From this session object some
 * information about the current negotiation can be extracted. Important is the
 * {@link Session#startNewRound(Round)} method which is used to add a new round
 * to the session. At this moment (05-08-2014) adding new rounds to the session
 * is the responsibility of the {@link SessionManager}.
 *
 * @author David Festen
 */
public class Session {
	/**
	 * Holds the round objects of which this instance consists
	 */
	private ArrayList<Round> rounds;

	/**
	 * Holds the deadline constraints
	 */
	private Deadline deadlines;

	/**
	 * Holds a timestamp of the time started (used for runtime calculations)
	 * startTime should be updated using {@code System.nanoTime()}
	 */
	private long startTime;

	/**
	 * Holds a timestamp of the time started (used for runtime calculations)
	 * stopTime should be updated using {@code System.nanoTime()}
	 */
	private long stopTime;

	/**
	 * Holds a value to indicate whether the timer is running
	 */
	private boolean timerRunning;

	private Timeline timeline;

	/**
	 * Create a new instance of the session object. This should normally only
	 * happen once every session. See also the class documentation of
	 * {@link Session}.
	 *
	 * @param deadlines
	 *            Map of deadline constraints
	 */
	public Session(Deadline deadlines) {
		this.rounds = new ArrayList<Round>();
		this.deadlines = deadlines;
		switch (deadlines.getType()) {
		case ROUND:
			this.timeline = new DiscreteTimeline(deadlines.getValue());
			break;
		case TIME:
			this.timeline = new ContinuousTimeline(deadlines.getValue());
			break;
		}
	}

	/**
	 * Gets the deadline constraints
	 *
	 * @return a map of deadline types and values
	 */
	public Deadline getDeadlines() {
		return deadlines;
	}

	/**
	 * Sets the deadline constrains
	 *
	 * @param deadlines
	 *            a map of deadline types and values
	 */
	public void setDeadlines(Deadline deadlines) {
		this.deadlines = deadlines;
	}

	/**
	 * Updates the timestamp of this {@link Session}. Use just before starting
	 * the negotiation for most accurate timing. Timing is used in for example
	 * time deadline constraints. But might also be used in log messages as well
	 * as statistics.
	 */
	public void startTimer() {
		startTime = System.nanoTime();
		timerRunning = true;
		if (timeline instanceof ContinuousTimeline) {
			((ContinuousTimeline) timeline).reset();
		}
	}

	/**
	 * Updates the timestamp of this {@link Session}. Use just after finish the
	 * negotiation for most accurate timing. Timing is used in for example time
	 * deadline constraints. But might also be used in log messages as well as
	 * statistics. If you need to manually set it, consider using the
	 * {@link #setRuntimeInNanoSeconds(long)} function.
	 */
	public void stopTimer() {
		stopTime = System.nanoTime();
		timerRunning = false;
	}

	/**
	 * Gets the number of rounds currently in this session. When
	 * {@link #startNewRound(Round)} is called a new round will be added. Each
	 * round already includes all its {@link Turn}s, but some turns might not
	 * yet been done.
	 *
	 * @return list of rounds
	 */
	public ArrayList<Round> getRounds() {
		return rounds;
	}

	/**
	 * Get the most recent round.
	 *
	 * @return The last round of the {@link #getRounds()} method.
	 */
	public Round getMostRecentRound() {
		return rounds.get(rounds.size() - 1);
	}

	/**
	 * Add a round to this session. Make sure it contains all the turns
	 * necessary to execute the rounds. Normally the new round will be created
	 * by using
	 * {@link MultilateralProtocol#getRoundStructure(java.util.List, Session)}
	 *
	 *
	 * @param round
	 *            The round to add to this session.
	 */
	public void startNewRound(Round round) {
		rounds.add(round);
	}

	/**
	 * Get the current round number. one-based (meaning first round is round 1)
	 *
	 * @return Integer representing the round number
	 */
	public int getRoundNumber() {
		return rounds.size();
	}

	/**
	 * Get the current turn number. Counting restarts with every round.
	 *
	 * @return Integer representing the current turn within the round.
	 */
	public int getTurnNumber() {
		return getMostRecentRound().getActions().size();
	}

	/**
	 * Check whether this is the first round (round 1).
	 *
	 * @return true if {@link #getRoundNumber()} equals 1
	 */
	public boolean isFirstRound() {
		return getRoundNumber() == 1;
	}

	/**
	 * Get the most recently executed action.
	 *
	 * @return The most recent action of the most recent round
	 */
	public Action getMostRecentAction() {
		return getMostRecentRound().getMostRecentAction();
	}

	/**
	 * Check whether one of the deadlines is reached.
	 *
	 * @return true if a deadline is reached
	 */
	public boolean isDeadlineReached() {
		boolean deadlineReached = false;

		if (deadlines.getType() == DeadlineType.TIME) {
			int timeDeadlineInSeconds = deadlines.getValue();
			double timeRanInSeconds = getRuntimeInSeconds();
			deadlineReached |= timeRanInSeconds > timeDeadlineInSeconds;
		}

		if (deadlines.getType() == DeadlineType.ROUND) {
			int roundsDeadline = (Integer) deadlines.getValue();
			deadlineReached |= getRoundNumber() > roundsDeadline;
		}

		return deadlineReached;
	}

	public long getRuntimeInNanoSeconds() {
		if (timerRunning)
			return System.nanoTime() - startTime;
		else
			return stopTime - startTime;
	}

	public double getRuntimeInSeconds() {
		return (double) getRuntimeInNanoSeconds() / pow(10, 9); // ns -> s ( /
																// 10^9 )
	}

	public void setRuntimeInNanoSeconds(long nanoSeconds) {
		stopTime = System.nanoTime();
		startTime = stopTime - nanoSeconds;
	}

	public void setRuntimeInSeconds(double seconds) {
		setRuntimeInNanoSeconds(Math.round(seconds * pow(10, 9)));
	}

	public boolean isTimerRunning() {
		return timerRunning;
	}

	public Timeline getTimeline() {
		return timeline;
	}

	public void setTimeline(Timeline timeline) {
		this.timeline = timeline;
	}

	public Session copy() {
		return new Session(deadlines);
	}
}
