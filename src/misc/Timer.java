package misc;


public final class Timer 
{
    private final long t;

	public Timer()
	{	 
		System.out.println("Start timer");
    	t = System.nanoTime();
	}
	
	public Timer(boolean smoel)
	{	 
    	t = System.nanoTime();
	}

	/** Print tijd verstreken in secondes
	 * 
	 */
	public void printTime()
	{
		long t2 = System.nanoTime();
		System.out.println("Elapsed: " + (t2 - t)/1000000000 + " seconds");
	}
	
	public void printTime(String label)
	{
		long t2 = System.nanoTime();
		System.out.println("Elapsed at "+label+": " + (t2 - t)/1000000000 + " seconds");
	}
	
	public long elapsed()
	{
		long t2 = System.nanoTime();
		return t2 - t;
	}
	
	public void printTimeNano()
	{
		long t2 = System.nanoTime();
		System.out.println("Elapsed: " + (t2 - t)+ " nanoseconds.");
	}
	
	public void printTimeMili()
	{
		long t2 = System.nanoTime();
		System.out.println("Elapsed: " + (t2 - t)/1000000 + " ms.");
	}
	
	public void printTimeMili(String label)
	{
		long t2 = System.nanoTime();
		System.out.println("Elapsed at "+label+": " + (t2 - t)/1000000 + " ms.");
	}
	
	public long getTimeSec()
	{
		long t2 = System.nanoTime();
		return (t2 - t)/1000000000;
	}
	
	public long getTimeMili()
	{
		long t2 = System.nanoTime();
		return (t2 - t)/1000000;
	}

	public void printTimeMicro() 
	{
		long t2 = System.nanoTime();
		System.out.println("Elapsed: " + (t2 - t)/1000 + " micros.");
	}

	public long getTimeMicro()
	{
		long t2 = System.nanoTime();
		return (t2 - t)/1000;
	}

	public void printTimeNano(String label)
	{
		long t2 = System.nanoTime();
		System.out.println("Elapsed at "+label+": " + (t2 - t) + " ns.");
	}
}
