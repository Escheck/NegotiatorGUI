package negotiator;

public enum DeadlineType {
	TIME, ROUND;

	public String units() {
		return this == TIME ? "s" : "rounds";
	}
}
