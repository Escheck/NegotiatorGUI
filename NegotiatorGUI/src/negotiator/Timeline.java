package negotiator;
/**
 * A time line, running from t = 0 (start) to t = 1 (deadline).
 */
public abstract class Timeline {
    protected boolean hasDeadline;


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
	
	public boolean hasDeadline() {
		return hasDeadline;
	}
	
	
}
