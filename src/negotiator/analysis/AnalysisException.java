package negotiator.analysis;

import negotiator.exceptions.NegotiatorException;

/**
 * Exception illustrating that calculating a feature of the bidspace
 * (for example the Nash point) went wrong.
 */
public class AnalysisException extends NegotiatorException {

	private static final long serialVersionUID = 3591281849194003876L;

	/**
	 * Error message to be reported.
	 * @param message shown as error.
	 */
	public AnalysisException(String message) {
		super(message);
	}
}