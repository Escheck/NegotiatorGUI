package negotiator.config;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBException;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import negotiator.Deadline;
import negotiator.repository.MultiPartyProtocolRepItem;
import negotiator.repository.PartyRepItem;
import negotiator.repository.ProfileRepItem;

/**
 * Implementation of MultilateralTournamentConfigurationInterface. Extracted
 * from {@link GuiConfiguration}. This stores all information for a multilateral
 * tournament. Can be {@link #load(File)} and {@link #save(File)} to XML.
 *
 * @author W.Pasman 27jul15
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class MultilateralTournamentConfiguration implements
		MultilateralTournamentConfigurationInterface {

	@XmlElement(name = "deadline")
	private Deadline deadline;

	/**
	 * Holds the chosen protocol
	 */
	@XmlElement
	private MultiPartyProtocolRepItem protocolItem;

	/**
	 * Holds the chosen mediator, if any
	 */
	@XmlElement
	private PartyRepItem mediatorItem;

	/**
	 * Holds the list of chosen parties
	 */
	@XmlElementWrapper(name = "partyRepItems")
	@XmlElement(name = "item")
	private List<PartyRepItem> partyItems;

	/**
	 * Holds the list of chosen profiles
	 */
	@XmlElementWrapper(name = "partyProfileItems")
	@XmlElement(name = "item")
	private List<ProfileRepItem> partyProfileItems;

	/**
	 * Holds the number of session that should be run
	 */
	@XmlElement
	private int numSessions;

	/**
	 * Holds the mediator profile if any
	 */
	@XmlElement
	private ProfileRepItem mediatorProfile;

	/**
	 * Holds the number of agents per session
	 */
	@XmlElement
	private int numberOfAgentsPerSession;

	/**
	 * Holds whether repetition is allowed or not;
	 */
	@XmlElement
	private boolean repetitionAllowed;

	public MultilateralTournamentConfiguration() {
		partyItems = new ArrayList<PartyRepItem>();
		partyProfileItems = new ArrayList<ProfileRepItem>();
	}

	public MultilateralTournamentConfiguration(GuiConfiguration config) {
		setDeadline(config.getDeadline());
		this.mediatorItem = config.getMediatorItem();
		this.partyItems = new ArrayList<PartyRepItem>(config.getPartyItems());
		this.partyProfileItems = new ArrayList<ProfileRepItem>(
				config.getPartyProfileItems());
		setProtocolItem(config.getProtocolItem());
		this.protocolItem = config.getProtocolItem();
		this.numSessions = config.getNumSessions();
		this.mediatorProfile = config.getMediatorProfile();
		this.numberOfAgentsPerSession = config.getNumAgentsPerSession();
		this.repetitionAllowed = config.getRepetitionAllowed();
	}

	@Override
	public Deadline getDeadline() {
		return deadline;
	}

	public void setDeadline(Deadline dl) {
		deadline = dl;
	}

	/**
	 * Gets the list of party repository items.
	 *
	 * @return a list of all chosen parties
	 */
	@Override
	public List<PartyRepItem> getPartyItems() {
		return partyItems;
	}

	/**
	 * Sets the list of chosen parties
	 *
	 * @param agents
	 *            the list of all chosen parties
	 */
	@Override
	public void setPartyItems(List<PartyRepItem> agents) {
		this.partyItems = agents;
	}

	/**
	 * Gets the number of negotiation sessions to run
	 *
	 * @return the number of sessions
	 */
	@Override
	public int getNumSessions() {
		return numSessions;
	}

	/**
	 * Sets the number of negotiation sessions.
	 *
	 * @param numSessions
	 *            the number of sessions
	 */
	@Override
	public void setNumSessions(int numSessions) {
		this.numSessions = numSessions;
	}

	/**
	 * Gets the mediator
	 *
	 * @return the mediator or party or null if not set
	 */
	@Override
	public PartyRepItem getMediatorItem() {
		return mediatorItem;
	}

	/**
	 * Sets the mediator item
	 *
	 * @param mediatorItem
	 *            the mediator
	 */
	@Override
	public void setMediatorItem(PartyRepItem mediatorItem) {
		this.mediatorItem = mediatorItem;
	}

	/**
	 * Gets the number of negotiation sessions to run
	 *
	 * @return the number of sessions
	 */
	@Override
	public int getNumTournaments() {
		return numSessions;
	}

	/**
	 * Gets the protocol to run
	 *
	 * @return the protocol to run
	 */
	@Override
	public MultiPartyProtocolRepItem getProtocolItem() {
		return protocolItem;
	}

	@Override
	public void setProtocolItem(MultiPartyProtocolRepItem item) {
		protocolItem = item;
	}

	/**
	 * Gets the list of profiles used by the parties
	 *
	 * @return list of profiles used by the parties
	 */
	@Override
	public List<ProfileRepItem> getPartyProfileItems() {
		return partyProfileItems;
	}

	/**
	 * Sets the list of profiles used by the parties
	 *
	 * @param partyProfileItems
	 *            list of profiles used by the parties
	 */
	@Override
	public void setPartyProfileItems(List<ProfileRepItem> partyProfileItems) {
		this.partyProfileItems = partyProfileItems;
	}

	/**
	 * Gets the number of agents per session
	 *
	 * @return the number of agents per session
	 */
	@Override
	public int getNumAgentsPerSession() {
		return numberOfAgentsPerSession;
	}

	/**
	 * Sets the number of agents per session
	 *
	 * @param numAgents
	 *            number of agents
	 */
	@Override
	public void setNumAgentsPerSession(int numAgents) {
		numberOfAgentsPerSession = numAgents;
	}

	/**
	 * Gets whether repetition is allowed when generating combinations of
	 * agents.
	 *
	 * @return true if allowed
	 */
	@Override
	public boolean getRepetitionAllowed() {
		return repetitionAllowed;
	}

	/**
	 * Sets whether repetition is allowed for generating sessions for the
	 * current agent
	 *
	 * @param repetitionAllowed
	 *            true if repetition is allowed
	 */
	@Override
	public void setRepetitionAllowed(boolean repetitionAllowed) {
		this.repetitionAllowed = repetitionAllowed;
	}

	public ProfileRepItem getMediatorProfile() {
		return mediatorProfile;
	}

	public void setMediatorProfile(ProfileRepItem mediatorProfile) {
		this.mediatorProfile = mediatorProfile;
	}

	/**
	 * Load a new {@link MultilateralTournamentConfiguration} from file.
	 * 
	 * @param file
	 *            the file to load from
	 * @return the new {@link MultilateralTournamentConfiguration}.
	 * @throws JAXBException
	 */
	public static MultilateralTournamentConfiguration load(File file)
			throws JAXBException {
		return MultilateralTournamentsConfiguration.load(file).getTournaments()
				.get(0);
	}

	/**
	 * Save this to xml file
	 * 
	 * @param file
	 */
	public void save(File file) {
		List<MultilateralTournamentConfiguration> tournaments = new ArrayList<MultilateralTournamentConfiguration>();
		tournaments.add(this);
		new MultilateralTournamentsConfiguration(tournaments).save(file);
	}
}
