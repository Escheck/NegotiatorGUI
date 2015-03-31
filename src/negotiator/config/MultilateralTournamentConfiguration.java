package negotiator.config;

import negotiator.protocol.Protocol;
import negotiator.session.Session;
import negotiator.tournament.TournamentGenerator;

/**
 * Stores the configuration variables for the from
 * {@link negotiator.gui.negosession.MultilateralUI}
 *
 * @author David Festen
 */
public interface MultilateralTournamentConfiguration {

	/**
	 * Get the {@link negotiator.session.Session} object from this
	 * configurations
	 * 
	 * @return Session object represented in this configuration
	 */
	Session getSession();

	/**
	 * Get the {@link negotiator.protocol.Protocol} object from this
	 * configuration
	 * 
	 * @return Session object represented in this configuration
	 */
	Protocol getProtocol() throws Exception;

	/**
	 * Get the list of participating {@link negotiator.parties.NegotiationParty}
	 * objects from this configuration
	 * 
	 * @return list of party objects represented in this configuration or an
	 *         empty list if none
	 */
	TournamentGenerator getPartiesGenerator();

	/**
	 * Gets the number of negotiation sessions to run
	 *
	 * @return the number of sessions
	 */
	int getNumTournaments();
}
