package negotiator.session;


/**
 * Error that will be thrown when we fail to fetch data from repository XML
 * files.
 */
public class RepositoryException extends Exception {
	/**
	 * Initializes a new instance of the {@link RepositoryException} class.
	 *
	 * @param instigator
	 *            The party that did an invalid action.
	 */
	public RepositoryException(String message, Throwable cause) {
		super(message, cause);
	}

}
