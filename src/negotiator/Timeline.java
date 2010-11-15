package negotiator;
/**
 * A time line, running from t = 0 (start) to t = 1 (deadline).
 */
public final class Timeline
{
	private final int totalSeconds;
    private final long startTime;
    private final boolean hasDeadline;

    /**
     * Creates a timeline with a deadline of {@link #totalSeconds} number of seconds.
     */
	public Timeline(int totalSecs)
	{	 
		totalSeconds = totalSecs;
    	startTime = System.nanoTime();
    	hasDeadline = true;
    	System.out.println("Started time line of " + totalSecs + " seconds.");
	}
	
	/** 
	 * Gets the elapsed time in seconds. 
	 * Use {@link #getTime()} for a more generic version.
	 */
	public double getElapsedSeconds()
	{
		long t2 = System.nanoTime();
		return ((t2 - startTime) / 1000000000.0);
	}
	
	/** 
	 * Gets the total negotiation time in miliseconds
	 */
	public long getTotalMiliseconds()
	{
		return 1000 * totalSeconds;
	}
	
	/**
	 * Gets the total negotiation time in seconds
	 */
	public long getTotalSeconds()
	{
		return totalSeconds;
	}
	
	/** 
	 * Prints time in seconds
	 */
	public void printElapsedSeconds()
	{
		System.out.println("Elapsed: " + getElapsedSeconds() + " seconds");
	}
	
	/** 
	 * Prints time, running from t = 0 (start) to t = 1 (deadline).
	 */
	public void printTime()
	{
		System.out.println("t = " + getTime());
	}
	
	/**
	 * Gets the time, running from t = 0 (start) to t = 1 (deadline).
	 * The time is normalized, so agents need not be concerned with the actual internal clock. 
	 * Please use {@link Agent#wait(double)} for pausing the agent.
	 */
	public double getTime()
	{
		double t = getElapsedSeconds() / (double) totalSeconds;
		if (t > 1)
			t = 1;
		return t;
	}
	
	public boolean isDeadlineReached()
	{
		return hasDeadline && (getTime() >= 1);
	}
	
	public static void main(String[] args)
	{
		Timeline timeline = new Timeline(60);
		while (true)
		{
			timeline.printElapsedSeconds();
			timeline.printTime();			
		}
	}
	
	public boolean hasDeadline()
	{
		return hasDeadline;
	}
}
