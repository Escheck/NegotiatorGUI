package negotiator;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Contains the deadline - either rounds based or time based.
 * <p>
 * Deadline is a final object and can be serialized to xml.
 * 
 * @author W.Pasman
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Deadline {

	@XmlElement
	private final Integer value;
	@XmlElement
	private final DeadlineType type;

	/**
	 * Default timeout in seconds
	 */
	private final Integer DEFAULT_TIME_OUT = 180;

	/**
	 * Create default value.
	 */
	public Deadline() {
		value = 180;
		type = DeadlineType.TIME;
	};

	public Deadline(int val, DeadlineType tp) {
		if (val <= 0) {
			throw new IllegalArgumentException("value must be >0 but got "
					+ val);
		}
		if (tp == null) {
			throw new NullPointerException("type is null");
		}
		value = val;
		type = tp;
	}

	/**
	 * Get the total value
	 * 
	 * @return
	 */
	public int getValue() {
		return value;
	}

	/**
	 * Get the type of this deadline.
	 * 
	 * @return
	 */
	public DeadlineType getType() {
		return type;
	}

	/**
	 * get the default time-out for function calls in the agents.
	 * 
	 * @return
	 */
	public Integer getDefaultTimeout() {
		return DEFAULT_TIME_OUT;
	}

	public String toString() {
		return "Deadline:" + valueString();
	}

	/**
	 * @return just the value of this deadline, eg "10s".
	 */
	public String valueString() {
		return value + type.units();
	}

	/**
	 * get the time, or a default time time-out. This is needed to determine the
	 * time-out for code execution with {@link DeadlineType#ROUND} deadlines.
	 * 
	 * @return
	 */
	public int getTimeOrDefaultTimeout() {
		if (type == DeadlineType.ROUND) {
			return DEFAULT_TIME_OUT;
		}
		return value;

	}
}