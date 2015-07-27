package negotiator.config;

import negotiator.protocol.MultilateralProtocol;

/**
 * This is the configuration object used by the user interface. It stores all
 * the data used there.
 *
 * @author David Festen
 */
public interface GuiConfigurationInterface {

	/**
	 * Gets the mediator index in the agent list
	 *
	 * @return the index of the mediator
	 */
	int getMediatorIndex();

	/**
	 * Sets the mediator index in the agent list
	 *
	 * @param index
	 *            the index to use
	 */
	void setMediatorIndex(int index);

	/**
	 * Get the type of tournament
	 *
	 * @return the type of tournament
	 */
	String getTournamentType();

	/**
	 * Set the type of tournament
	 *
	 * @param type
	 *            the type of tournament
	 */
	void setTournamentType(String type);

	MultilateralProtocol getProtocol() throws Exception;

}
