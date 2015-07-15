package negotiator.events;

/**
 * This contains a message that occurs during a negotiation.
 * 
 * This was set to deprecated to indicate that it is not desirable to use this
 * event. This is considered undesirable because the listeners will not have any
 * indication what this event means (warning? fatal error? End of session? End
 * of tournament?) and hence can not properly handle this
 * 
 */
@Deprecated
public class LogMessageEvent extends NegotiationEvent {

	private static final long serialVersionUID = 7174221341200309069L;
	private String source;
	private String message;

	public LogMessageEvent(Object source, String pSource, String pMessage) {
		super(source);
		source = pSource;
		message = pMessage;
	}

	public String getSource() {
		return source;
	}

	public String getMessage() {
		return message;
	}
}
