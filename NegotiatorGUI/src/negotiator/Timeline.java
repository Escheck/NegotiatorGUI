package negotiator;


/**
 * A time line, running from t = 0 (start) to t = 1 (deadline).
 */
public abstract class Timeline {
    protected boolean hasDeadline;
    protected boolean paused = false;
    /** In a time-based protocol, time passes within a round. In contrast,
     * in a rounds-based protocol time only passes when the action is presented.
     */
    protected enum Type {
        Time,
        Rounds;
    }

	/**
	 * Gets the time, running from t = 0 (start) to t = 1 (deadline).
	 * The time is normalized, so agents need not be concerned with the actual internal clock. 
	 * Please use {@link Agent#wait(double)} for pausing the agent.
	 */
	public abstract double getTime();
	
	public abstract double getTotalTime();
	
	public abstract double getCurrentTime();
		
	public abstract void printTime();
	
	public boolean isDeadlineReached() {
		return hasDeadline && (getTime() >= 1.0);
	}
	
	public void pause() throws Exception {
		throw new Exception("This timeline can not be paused and resumed.");
	}
	
	public void resume() throws Exception {
		throw new Exception("This timeline can not be paused and resumed.");
	}
	
	public Type getType() {
		return Type.Time;
	}
	
	public boolean isPaused() {
		return paused;
	}
	
	public boolean hasDeadline() {
		return hasDeadline;
	}
}