package negotiator;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Contains the deadline which is a combination of time and rounds deadline. If
 * both are set, the deadline is reached when the first deadline (time or
 * rounds) is reached.
 * <p>
 * Deadline is a unmodifyable object.
 * 
 * @author David Festen
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
		return 3 * 60;
	}

	public String toString() {
		return "Deadline:" + valueString();
	}

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
			return 180;
		}
		return value;

	}
}