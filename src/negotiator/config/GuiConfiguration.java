package negotiator.config;

import java.util.List;

import negotiator.Deadline;
import negotiator.repository.MultiPartyProtocolRepItem;
import negotiator.repository.PartyRepItem;
import negotiator.repository.ProfileRepItem;

/**
 * This is the configuration object used by the user interface. It stores all
 * the data used there.
 *
 * @author David Festen
 */
public interface GuiConfiguration {

	/**
	 * Gets the mediator profile
	 *
	 * @return the mediator's ProfileRepItem or null if not existing
	 */
	public ProfileRepItem getMediatorProfile();

	/**
	 * Sets the mediator profile
	 *
	 * @param mediatorProfile
	 *            the mediator's profile rep item or null if not existing
	 */
	public void setMediatorProfile(ProfileRepItem mediatorProfile);

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
	 * Gets the deadline map
	 *
	 * @return Deadline for one or multiple sessions in the configuration.
	 */
	Deadline getDeadlines();

	/**
	 * Sets the deadline map
	 *
	 * @param deadlines
	 *            a map of deadline keys and their values
	 */
	void setDeadlines(Deadline deadlines);

	/**
	 * Gets the list of party repository items.
	 *
	 * @return a list of all chosen parties
	 */
	List<PartyRepItem> getPartyItems();

	/**
	 * Sets the list of chosen parties
	 *
	 * @param agents
	 *            the list of all chosen parties
	 */
	void setPartyItems(List<PartyRepItem> agents);

	/**
	 * Gets the mediator
	 *
	 * @return the mediator or party or null if not set
	 */
	PartyRepItem getMediatorItem();

	/**
	 * Sets the mediator item
	 *
	 * @param mediatorItem
	 *            the mediator
	 */
	void setMediatorItem(PartyRepItem mediatorItem);

	/**
	 * Gets the number of negotiation sessions to run
	 *
	 * @return the number of sessions
	 */
	int getNumSessions();

	/**
	 * Sets the number of negotiation sessions.
	 *
	 * @param numRounds
	 *            the number of sessions
	 */
	void setNumSessions(int numRounds);

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

	/**
	 * Gets the protocol to run
	 *
	 * @return the protocol to run
	 */
	MultiPartyProtocolRepItem getProtocolItem();

	/**
	 * Sets the protocol to run.
	 *
	 * @param protocolItem
	 *            the protocol to run
	 */
	void setProtocolItem(MultiPartyProtocolRepItem protocolItem);

	/**
	 * Gets the list of profiles used by the parties
	 *
	 * @return list of profiles used by the parties
	 */
	List<ProfileRepItem> getPartyProfileItems();

	/**
	 * Sets the list of profiles used by the parties
	 *
	 * @param partyProfileItems
	 *            list of profiles used by the parties
	 */
	void setPartyProfileItems(List<ProfileRepItem> partyProfileItems);

	/**
	 * Gets the number of agents per session
	 *
	 * @return the number of agents per session
	 */
	int getNumAgentsPerSession();

	/**
	 * Sets the number of agents per session
	 *
	 * @param numAgents
	 *            number of agents
	 */
	void setNumAgentsPerSession(int numAgents);

	/**
	 * Gets whether repetition is allowed when generating combinations of
	 * agents.
	 * 
	 * @return true if allowed
	 */
	boolean getRepetitionAllowed();

	/**
	 * Sets whether repetition is allowed for generating sessions for the
	 * current agent
	 *
	 * @param repetitionAllowed
	 *            true if repetition is allowed
	 */
	void setRepetitionAllowed(boolean repetitionAllowed);

}
