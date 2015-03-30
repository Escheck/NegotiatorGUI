package negotiator.config;

import negotiator.DeadlineType;
import negotiator.Domain;
import negotiator.parties.NegotiationParty;
import negotiator.repository.MultiPartyProtocolRepItem;
import negotiator.repository.PartyRepItem;
import negotiator.repository.ProfileRepItem;
import negotiator.repository.Repository;
import negotiator.session.Session;
import negotiator.protocol.Protocol;
import negotiator.tournament.TournamentGenerator;
import negotiator.utility.TournamentIndicesGenerator;
import negotiator.utility.UtilitySpace;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Stores the configuration variables for the {@link negotiator.gui.negosession.MultilateralUI} user
 * interface. This class is also readable by the {@link negotiator.session.TournamentManager} to
 * start a new tournament.
 *
 * @author David Festen
 */
public class Configuration implements GuiConfiguration, MultilateralTournamentConfiguration
{
    /**
     * Holds the deadline constraints
     */
    private HashMap<DeadlineType, Object> deadlines;

    /**
     * Holds the chosen mediator, if any
     */
    private PartyRepItem mediatorItem;

    /**
     * Holds the list of chosen parties
     */
    private List<PartyRepItem> partyItems;

    /**
     * Holds the list of chosen profiles
     */
    private List<ProfileRepItem> partyProfileItems;

    /**
     * Holds the chosen protocol
     */
    private MultiPartyProtocolRepItem protocolItem;

    /**
     * Holds the number of session that should be run
     */
    private int numSessions;

    /**
     * Holds the type of tournament
     */
    private String tournamentType;

    /**
     * Holds the index of the mediator in the party list
     */
    private int mediatorIndex;

    /**
     * Holds the mediator profile if any
     */
    private ProfileRepItem mediatorProfile;

    /**
     * Holds the number of agents per session
     */
    private int numberOfAgentsPerSession;

    /**
     * Holds whether repetition is allowed or not;
     */
    private boolean repetitionAllowed;

    /**
     * Holds the session if generated
     */
    private Session session;

    /**
     * Initializes a new instance of the configuration class.
     */
    public Configuration()
    {
        deadlines = new HashMap<DeadlineType, Object>();
        partyItems = new ArrayList<PartyRepItem>();
        partyProfileItems = new ArrayList<ProfileRepItem>();
        mediatorIndex = 0; // defaults the mediator to the first item in the list
    }

    /**
     * Initializes a new instance of the configuration class by using existing config.
     * Creates a shallow copy of all lists and collections in the configuration.
     *
     * @param config the configuration to make a copy of
     */
    public Configuration(Configuration config)
    {
        this.deadlines = new HashMap<DeadlineType, Object>(config.getDeadlines());
        this.mediatorItem = config.getMediatorItem();
        this.partyItems = new ArrayList<PartyRepItem>(config.getPartyItems());
        this.partyProfileItems = new ArrayList<ProfileRepItem>(config.getPartyProfileItems());
        this.protocolItem = config.getProtocolItem();
        this.numSessions = config.getNumSessions();
        this.tournamentType = config.getTournamentType();
        this.mediatorIndex = config.getMediatorIndex();
        this.mediatorProfile = config.getMediatorProfile();
        this.numberOfAgentsPerSession = config.getNumAgentsPerSession();
        this.repetitionAllowed = config.getRepetitionAllowed();
    }

    /**
     * Creates a new Party from repository items
     *
     * @param partyRepItem   Party Repository item to createFrom party from
     * @param profileRepItem Profile Repository item to createFrom party from
     * @return new Party
     * @throws java.lang.NoSuchMethodException  If requested Party does not have a constructor accepting only preference profiles
     * @throws java.lang.ClassNotFoundException If requested Party class can not be found.
     * @throws java.lang.Exception              If {@link negotiator.repository.Repository#copyFrom(negotiator.repository.Repository)} throws an exception.
     */
    public static NegotiationParty createFrom(PartyRepItem partyRepItem, ProfileRepItem profileRepItem)
            throws Exception
    {
        ClassLoader loader = ClassLoader.getSystemClassLoader();
        Class party = loader.loadClass(partyRepItem.getClassPath());

        Class[] paramTypes = {UtilitySpace.class};

        @SuppressWarnings("unchecked")
        Constructor partyConstructor = party.getConstructor(paramTypes);

        // System.out.println("Found the constructor: " + cons);

        UtilitySpace utilSpace = createFrom(profileRepItem);
        return (NegotiationParty) partyConstructor.newInstance(utilSpace);
    }

    /**
     * Creates a new Party from repository items
     *
     * @param partyRepItem Party Repository item to createFrom party from
     * @param domain       Profile Repository item to createFrom party from
     * @return new Party
     * @throws java.lang.NoSuchMethodException  If requested Party does not have a constructor accepting only preference profiles
     * @throws java.lang.ClassNotFoundException If requested Party class can not be found.
     * @throws java.lang.Exception              If {@link negotiator.repository.Repository#copyFrom(negotiator.repository.Repository)} throws an exception.
     */
    public static NegotiationParty createFrom(PartyRepItem partyRepItem, Domain domain)
            throws Exception
    {
        ClassLoader loader = ClassLoader.getSystemClassLoader();
        Class party = loader.loadClass(partyRepItem.getClassPath());

        Class[] paramTypes = {UtilitySpace.class};

        @SuppressWarnings("unchecked")
        Constructor partyConstructor = party.getConstructor(paramTypes);

        // System.out.println("Found the constructor: " + cons);

        UtilitySpace utilSpace = new UtilitySpace(domain);
        return (NegotiationParty) partyConstructor.newInstance(utilSpace);
    }

    /**
     * Create a new instance of the Protocol object from a {@link MultiPartyProtocolRepItem}
     *
     * @param protocolRepItem Item to create Protocol out of
     * @return the created protocol.
     * @throws java.lang.Exception If {@link negotiator.repository.Repository#copyFrom(negotiator.repository.Repository)} throws an exception.
     */
    public static Protocol createFrom(MultiPartyProtocolRepItem protocolRepItem) throws Exception
    {
        ClassLoader loader = ClassLoader.getSystemClassLoader();
        Class protocolClass = loader.loadClass(protocolRepItem.getClassPath());

        @SuppressWarnings("unchecked")
        Constructor protocolConstructor = protocolClass.getConstructor();

        return (Protocol) protocolConstructor.newInstance();
    }

    /**
     * Create a new UtilitySpace from a ProfileRepItem. If {@link ProfileRepItem#getDomain()}
     * returns new instead of an actual domain, this method also returns null.
     *
     * @param item the item to create a UtilitySpace out of.
     * @return the UtilitySpace corresponding to the item.
     * @throws java.lang.Exception If {@link negotiator.repository.Repository#copyFrom(negotiator.repository.Repository)} throws an exception.
     */
    public static UtilitySpace createFrom(ProfileRepItem item) throws Exception
    {
        Domain domain = Repository.get_domain_repos().getDomain(item.getDomain());
        return Repository.get_domain_repos().getUtilitySpace(domain, item);
    }

    /**
     * Gets the mediator index in the agent list
     *
     * @return the index of the mediator
     */
    @Override
    public int getMediatorIndex()
    {
        return mediatorIndex;
    }

    /**
     * Sets the mediator index in the agent list
     *
     * @param index the index to use
     */
    @Override
    public void setMediatorIndex(int index)
    {
        mediatorIndex = index;
    }

    /**
     * Gets the deadline map
     *
     * @return A Hashmap of deadline keys and their values
     */
    @Override
    public HashMap<DeadlineType, Object> getDeadlines()
    {
        return deadlines;
    }

    /**
     * Sets the deadline map
     *
     * @param deadlines a map of deadline keys and their values
     */
    @Override
    public void setDeadlines(HashMap<DeadlineType, Object> deadlines)
    {
        this.deadlines = deadlines;
    }

    /**
     * Gets the list of party repository items.
     *
     * @return a list of all chosen parties
     */
    @Override
    public List<PartyRepItem> getPartyItems()
    {
        return partyItems;
    }

    /**
     * Sets the list of chosen parties
     *
     * @param agents the list of all chosen parties
     */
    @Override
    public void setPartyItems(List<PartyRepItem> agents)
    {
        this.partyItems = agents;
    }

    /**
     * Gets the mediator
     *
     * @return the mediator or party or null if not set
     */
    @Override
    public PartyRepItem getMediatorItem()
    {
        return mediatorItem;
    }

    /**
     * Sets the mediator item
     *
     * @param mediatorItem the mediator
     */
    @Override
    public void setMediatorItem(PartyRepItem mediatorItem)
    {
        this.mediatorItem = mediatorItem;
    }

    /**
     * Gets the number of negotiation sessions to run
     *
     * @return the number of sessions
     */
    @Override
    public int getNumSessions()
    {
        return numSessions;
    }

    /**
     * Sets the number of negotiation sessions.
     *
     * @param numSessions the number of sessions
     */
    @Override
    public void setNumSessions(int numSessions)
    {
        this.numSessions = numSessions;
    }

    /**
     * Gets the number of negotiation sessions to run
     *
     * @return the number of sessions
     */
    @Override
    public int getNumTournaments()
    {
        return numSessions;
    }

    /**
     * Get the type of tournament
     *
     * @return the type of tournament
     */
    @Override
    public String getTournamentType()
    {
        return tournamentType;
    }

    /**
     * Set the type of tournament
     *
     * @param type the type of tournament
     */
    @Override
    public void setTournamentType(String type)
    {
        tournamentType = type;
    }

    /**
     * Gets the protocol to run
     *
     * @return the protocol to run
     */
    @Override
    public MultiPartyProtocolRepItem getProtocolItem()
    {
        return protocolItem;
    }

    /**
     * Sets the protocol to run.
     *
     * @param protocolItem the protocol to run
     */
    @Override
    public void setProtocolItem(MultiPartyProtocolRepItem protocolItem)
    {
        this.protocolItem = protocolItem;
    }

    /**
     * Gets the list of profiles used by the parties
     *
     * @return list of profiles used by the parties
     */
    @Override
    public List<ProfileRepItem> getPartyProfileItems()
    {
        return partyProfileItems;
    }

    /**
     * Sets the list of profiles used by the parties
     *
     * @param partyProfileItems list of profiles used by the parties
     */
    @Override
    public void setPartyProfileItems(List<ProfileRepItem> partyProfileItems)
    {
        this.partyProfileItems = partyProfileItems;
    }

    /**
     * Gets the number of agents per session
     *
     * @return the number of agents per session
     */
    @Override
    public int getNumAgentsPerSession()
    {
        return numberOfAgentsPerSession;
    }

    /**
     * Sets the number of agents per session
     *
     * @param numAgents number of agents
     */
    @Override
    public void setNumAgentsPerSession(int numAgents)
    {
        numberOfAgentsPerSession = numAgents;
    }

    /**
     * Gets whether repetition is allowed when generating combinations of agents.
     *
     * @return true if allowed
     */
    @Override
    public boolean getRepetitionAllowed()
    {
        return repetitionAllowed;
    }

    /**
     * Sets whether repetition is allowed for generating sessions for the current agent
     *
     * @param repetitionAllowed true if repetition is allowed
     */
    @Override
    public void setRepetitionAllowed(boolean repetitionAllowed)
    {
        this.repetitionAllowed = repetitionAllowed;
    }

    /**
     * Get the {@link negotiator.session.Session} object from this configuration
     *
     * @return Session object represented in this configuration
     */
    @Override
    public Session getSession()
    {
        if (session == null) {
            session = new Session(deadlines);
        }

        return session;
    }

    /**
     * Get the {@link negotiator.protocol.Protocol} object from this configuration
     *
     * @return Session object represented in this configuration
     * @throws java.lang.NoSuchMethodException  If requested Party does not have a constructor accepting only preference profiles
     * @throws java.lang.ClassNotFoundException If requested Party class can not be found.
     * @throws java.lang.Exception              If {@link negotiator.repository.Repository#copyFrom(negotiator.repository.Repository)} throws an exception.
     */
    @Override
    public Protocol getProtocol() throws Exception
    {
        return createFrom(getProtocolItem());
    }

    /**
     * Get the list of participating {@link negotiator.parties.NegotiationParty} objects from this configuration
     *
     * @return list of party objects represented in this configuration or an empty list if none
     * @throws java.lang.NoSuchMethodException  If requested Party does not have a constructor accepting only preference profiles
     * @throws java.lang.ClassNotFoundException If requested Party class can not be found.
     * @throws java.lang.Exception              If {@link negotiator.repository.Repository#copyFrom(negotiator.repository.Repository)} throws an exception.
     */
    @Override
    public TournamentGenerator getPartiesGenerator() throws Exception
    {
        List<Integer> indices = new ArrayList<Integer>(getPartyItems().size());
        for (int i = 0; i < getPartyItems().size(); i++) indices.add(i);

        TournamentIndicesGenerator indicesGenerator = new TournamentIndicesGenerator(
                getNumAgentsPerSession(),
                getPartyProfileItems().size(),
                getRepetitionAllowed(),
                indices);
        return new TournamentGenerator(this, indicesGenerator);
    }

    public ProfileRepItem getMediatorProfile()
    {
        return mediatorProfile;
    }

    public void setMediatorProfile(ProfileRepItem mediatorProfile)
    {
        this.mediatorProfile = mediatorProfile;
    }
}
