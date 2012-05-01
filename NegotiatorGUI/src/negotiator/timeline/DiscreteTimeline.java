package negotiator.timeline;

import negotiator.Agent;

/**
 * Implementation of the timeline in which time is divided in rounds.
 * Time does not pass within a round. Note that requesting the total
 * time is in this case weird.
 */
public class DiscreteTimeline extends Timeline
{
    private int totalRounds;
    private int cRound;

    /**
     * Creates a timeline with a deadline of {@link #totalSeconds} number of seconds.
     */
	public DiscreteTimeline(int totalRounds)
	{	 
		// makes more sense to add one, as in this case "totalrounds" is still before the deadline as the 
		// deadline is on time >= 1.0
		this.totalRounds = totalRounds + 1;
    	hasDeadline = true;
    	cRound = 1;
	}
	
	/** 
	 * Prints time in seconds
	 */
	public void printRoundElapsed()
	{
		System.out.println("Elapsed Rounds: " + cRound);
	}
	
	/** 
	 * Prints time, running from t = 0 (start) to t = 1 (deadline).
	 */
	@Override
	public void printTime()
	{
		System.out.println("t = " + getTime());
	}
	
	/**
	 * Gets the time, running from t = 0 (start) to t = 1 (deadline).
	 * The time is normalized, so agents need not be concerned with the actual internal clock. 
	 * Please use {@link Agent#wait(double)} for pausing the agent.
	 */
	@Override
	public double getTime()
	{
		double t = (double)cRound / (double)totalRounds;
		if (t > 1)
			t = 1;
		return t;
	}
	
	public void increment() {
		cRound++;
	}

	/**
	 * The DiscreteTimeline does not have to be paused/resumed, as time does
	 * not pass within a round.
	 */
	public void pause() { }
	
	/**
	 * The DiscreteTimeline does not have to be paused/resumed, as time does
	 * not pass within a round.
	 */
	public void resume() { }
	
	/**
	 * This a hack because in Agent.java a method sleep requires this
	 */
	@Override
	public double getTotalTime() {
		return totalRounds;
	}

	@Override
	public double getCurrentTime() {
		return cRound;
	}
}
