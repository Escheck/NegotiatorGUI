package negotiator.events;

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
