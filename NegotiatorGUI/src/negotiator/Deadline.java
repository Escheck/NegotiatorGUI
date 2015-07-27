package negotiator;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Contains the deadline which is a combination of time and rounds deadline. If
 * both are set, the deadline is reached when the first deadline (time or
 * rounds) is reached.
 * 
 * @author David Festen
 * @author W.Pasman
 */
@XmlRootElement
public class Deadline {

	@XmlAttribute
	private final Integer totalTime;
	@XmlAttribute
	private final Integer totalRounds;

	public Deadline(Integer time, Integer rounds) {
		totalTime = time;
		totalRounds = rounds;
	}

	/**
	 * returns the total time available. If <=0 then the total time is undefined
	 * and will be ignored.
	 */
	public Integer getTotalTime() {
		return totalTime;
	}

	/**
	 * returns the total rounds available. If <=0 then the total rounds is
	 * undefined and will be ignored.
	 */
	public Integer getTotalRounds() {
		return totalRounds;
	}

	/**
	 * @return true iff the time deadline is >0 and engaged
	 */
	public boolean isTime() {
		return totalTime > 0;
	}

	/**
	 * @return true iff the rounds deadline is >0 and engaged
	 */
	public boolean isRounds() {
		return totalRounds > 0;
	}

	/**
	 * get the default time-out for function calls in the agents.
	 * 
	 * @return
	 */
	public Integer getDefaultTimeout() {
		return 3 * 60;
	}

	/**
	 * Get time out in seconds, or default timeout if time not set.
	 * 
	 * @return time out in seconds, always >0.
	 */
	public Integer getTimeOrDefaultTimeout() {
		if (totalTime > 0) {
			return totalTime;
		}
		return getDefaultTimeout();
	}
}