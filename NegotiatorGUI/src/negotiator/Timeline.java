package negotiator;
/**
 * A time line, running from t = 0 (start) to t = 1 (deadline).
 */
public final class Timeline
{
	private final int totalSeconds;
    private final long startTime;

	public Timeline(int totalSecs)
	{	 
		totalSeconds = totalSecs;
		System.out.println("Start time line of " + totalSecs + " seconds.");
    	startTime = System.nanoTime();
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
