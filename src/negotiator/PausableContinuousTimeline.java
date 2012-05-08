package negotiator;

public class PausableContinuousTimeline extends ContinuousTimeline {

	private long timeAtPause;
	private long totalPausedTime;
	
	public PausableContinuousTimeline(int totalSecs) {
		super(totalSecs);
	}
	
	/** 
	 * Gets the elapsed time in seconds. 
	 * Use {@link #getTime()} for a more generic version.
	 */
	public double getElapsedSeconds()
	{
		long t2;
		if (paused) {
			t2 = timeAtPause;
		} else {
			t2 = System.nanoTime();
		}
		return ((t2 - startTime - totalPausedTime) / 1000000000.0);
	}
	
	/** 
	 * Gets the elapsed time in seconds. 
	 * Use {@link #getTime()} for a more generic version.
	 */
	public double getElapsedMilliSeconds()
	{
		long t2;
		if (paused) {
			t2 = timeAtPause;
		} else {
			t2 = System.nanoTime();
		}
		return ((t2 - startTime - totalPausedTime) / 1000000.0);
	}

	public static void main(String[] args)
	{
		PausableContinuousTimeline timeline = new PausableContinuousTimeline(3);
		System.out.println("Elapsed: " + timeline.getElapsedSeconds() + " seconds");	
		try {
			timeline.pause();
			Thread.sleep(2000);
			timeline.resume();
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("Elapsed: " + timeline.getElapsedSeconds() + " seconds");			
		try {
			timeline.pause();
			Thread.sleep(2000);
			timeline.resume();
		} catch (Exception e) {
			e.printStackTrace();
		}
		timeline.printElapsedSeconds();
		while (!timeline.isDeadlineReached()) {
			System.out.println("Elapsed: " + timeline.getElapsedSeconds() + " seconds");
		}
	}
	
	public void pause() {
		if (!paused) {
			paused = true;
			timeAtPause = System.nanoTime();
		}
	}
	
	public void resume() {
		if (paused) {
			totalPausedTime += (System.nanoTime() - timeAtPause);
			paused = false;
		}
	}
}