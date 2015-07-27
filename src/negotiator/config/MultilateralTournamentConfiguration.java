package negotiator.config;

import negotiator.Deadline;
import negotiator.protocol.MultilateralProtocol;
import negotiator.tournament.TournamentGenerator;

/**
 * Stores the configuration variables for the from
 * {@link negotiator.gui.negosession.MultilateralUI}
 *
 * @author David Festen
 */
public interface MultilateralTournamentConfiguration {

	/**
	 * @return Deadline for all sessions.
	 */
	Deadline getDeadline();

	/**
	 * Get the {@link MultilateralProtocol} object from this configuration
	 * 
	 * @return Session object represented in this configuration
	 */
	MultilateralProtocol getProtocol() throws Exception;

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

	/**
	 * Get the number of agents in each session.
	 */
	int getNumAgentsPerSession();
}
