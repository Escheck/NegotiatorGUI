package negotiator;
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
		this.totalRounds = totalRounds;
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
