package negotiator;
/**
 * A time line, running from t = 0 (start) to t = 1 (deadline).
 */
public final class Timeline
{
	private final int totalSeconds;
    private final long startTime;

    /**
     * Create a timeline of {@link #totalSeconds} number of seconds.
     */
	public Timeline(int totalSecs)
	{	 
		totalSeconds = totalSecs;
    	startTime = System.nanoTime();
    	System.out.println("Started time line of " + totalSecs + " seconds.");
	}
	
	private double getElapsedSeconds()
	{
		long t2 = System.nanoTime();
		return ((t2 - startTime) / 1000000000.0);
	}
	
	public long getTotalMiliseconds()
	{
		return 1000 * totalSeconds;
	}
	
	/** 
	 * Print time in seconds
	 */
	public void printRealTime()
	{
		System.out.println("Elapsed: " + getElapsedSeconds() + " seconds");
	}
	
	/** 
	 * Print time, running from t = 0 (start) to t = 1 (deadline).
	 */
	public void printTime()
	{
		System.out.println("t = " + getTime());
	}
	
	/**
	 * Get the time, running from t = 0 (start) to t = 1 (deadline).
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
		return getTime() >= 1;
	}
	
	public static void main(String[] args)
	{
		Timeline timeline = new Timeline(60);
		while (true)
		{
			timeline.printRealTime();
			timeline.printTime();			
		}
	}
}
