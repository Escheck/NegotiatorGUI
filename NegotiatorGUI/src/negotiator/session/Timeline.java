package negotiator.session;


/**
 * A time line, running from t = 0 (start) to t = 1 (deadline).
 */
public abstract class Timeline 
{
    protected boolean hasDeadline;
    protected boolean paused = false;
    /** In a time-based protocol, time passes within a round. In contrast,
     * in a rounds-based protocol time only passes when the action is presented.
     */
    public enum Type {
        Time,
        Rounds;
    }

	/**
	 * Gets the time, running from t = 0 (start) to t = 1 (deadline).
	 * The time is normalized, so agents need not be concerned with the actual internal clock. 
	 *
	 * @return current time in the interval [0, 1].
	 */
	public abstract double getTime();
	
	/**
	 * @return amount of time in seconds, or amount of rounds depending on timeline type.
	 */
	public abstract double getTotalTime();
	
	/**
	 * @return amount of seconds passed, or amount of rounds passed depending on the timeline type.
	 */
	public abstract double getCurrentTime();
		
	/**
	 * Print the current time.
	 */
	public abstract void printTime();
	
	/**
	 * @return true if deadline is reached.
	 */
	public boolean isDeadlineReached() {
		return hasDeadline && (getTime() >= 1.0);
	}
	
	/**
	 * Method used to pause the timeline. This method is only available if it is
	 * enabled in the Global.
	 * 
	 * @throws Exception
	 */
	public void pause() throws Exception {
		throw new Exception("This timeline can not be paused and resumed.");
	}
	
	/**
	 * Method used to resume the timeline. This method is only available if it is
	 * enabled in the Global.
	 * 
	 * @throws Exception
	 */
	public void resume() throws Exception {
		throw new Exception("This timeline can not be paused and resumed.");
	}
	
	/**
	 * @return type of time: Type.Time or Type.Rounds.
	 */
	public Type getType() {
		return Type.Time;
	}
	
	/**
	 * @return true if timeline is paused.
	 */
	public boolean isPaused() {
		return paused;
	}
}