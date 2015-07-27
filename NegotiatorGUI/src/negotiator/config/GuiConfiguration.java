package negotiator.config;

import java.lang.reflect.Constructor;

import negotiator.Domain;
import negotiator.exceptions.InstantiateException;
import negotiator.parties.NegotiationParty;
import negotiator.protocol.MultilateralProtocol;
import negotiator.repository.MultiPartyProtocolRepItem;
import negotiator.repository.PartyRepItem;
import negotiator.repository.ProfileRepItem;
import negotiator.repository.Repository;
import negotiator.session.Session;
import negotiator.tournament.TournamentGenerator;
import negotiator.utility.TournamentIndicesGenerator;
import negotiator.utility.UtilitySpace;

/**
 * Stores the configuration variables for the
 * {@link negotiator.gui.negosession.MultilateralUI} user interface. This class
 * is also readable by the {@link negotiator.session.TournamentManager} to start
 * a new tournament.
 *
 * @author David Festen
 * @modified W.Pasman #1103
 */
public class GuiConfiguration extends MultilateralTournamentConfiguration
		implements GuiConfigurationInterface {

	/**
	 * Holds the type of tournament
	 */
	private String tournamentType;

	/**
	 * Holds the session if generated
	 */
	private Session session;

	public GuiConfiguration() {

	}

	/**
	 * Initializes a new instance of the configuration class by using existing
	 * config. Creates a shallow copy of all lists and collections in the
	 * configuration.
	 *
	 * @param config
	 *            the configuration to make a copy of
	 * @throws InstantiateException
	 */
	public GuiConfiguration(GuiConfiguration config)
			throws InstantiateException {
		super(config);
		this.tournamentType = config.getTournamentType();
		// save(new File("test.xml")); // enable to create an example XML file
		// for a Configuration.
	}

	/**
	 * Creates a new Party from repository items
	 *
	 * @param partyRepItem
	 *            Party Repository item to createFrom party from
	 * @param profileRepItem
	 *            Profile Repository item to createFrom party from
	 * @return new Party
	 * @throws java.lang.NoSuchMethodException
	 *             If requested Party does not have a constructor accepting only
	 *             preference profiles
	 * @throws java.lang.ClassNotFoundException
	 *             If requested Party class can not be found.
	 * @throws java.lang.Exception
	 *             If
	 *             {@link negotiator.repository.Repository#copyFrom(negotiator.repository.Repository)}
	 *             throws an exception.
	 */
	public static NegotiationParty createFrom(PartyRepItem partyRepItem,
			ProfileRepItem profileRepItem) throws Exception {
		ClassLoader loader = ClassLoader.getSystemClassLoader();
		Class party = loader.loadClass(partyRepItem.getClassPath());

		Class[] paramTypes = { UtilitySpace.class };

		@SuppressWarnings("unchecked")
		Constructor partyConstructor = party.getConstructor(paramTypes);

		// System.out.println("Found the constructor: " + cons);

		UtilitySpace utilSpace = createFrom(profileRepItem);
		return (NegotiationParty) partyConstructor.newInstance(utilSpace);
	}

	/**
	 * Creates a new Party from repository items
	 *
	 * @param partyRepItem
	 *            Party Repository item to createFrom party from
	 * @param domain
	 *            Profile Repository item to createFrom party from
	 * @return new Party
	 * @throws java.lang.NoSuchMethodException
	 *             If requested Party does not have a constructor accepting only
	 *             preference profiles
	 * @throws java.lang.ClassNotFoundException
	 *             If requested Party class can not be found.
	 * @throws java.lang.Exception
	 *             If
	 *             {@link negotiator.repository.Repository#copyFrom(negotiator.repository.Repository)}
	 *             throws an exception.
	 */
	public static NegotiationParty createFrom(PartyRepItem partyRepItem,
			Domain domain) throws Exception {
		ClassLoader loader = ClassLoader.getSystemClassLoader();
		Class party = loader.loadClass(partyRepItem.getClassPath());

		Class[] paramTypes = { UtilitySpace.class };

		@SuppressWarnings("unchecked")
		Constructor partyConstructor = party.getConstructor(paramTypes);

		// System.out.println("Found the constructor: " + cons);

		UtilitySpace utilSpace = new UtilitySpace(domain);
		return (NegotiationParty) partyConstructor.newInstance(utilSpace);
	}

	/**
	 * Create a new instance of the Protocol object from a
	 * {@link MultiPartyProtocolRepItem}
	 *
	 * @param protocolRepItem
	 *            Item to create Protocol out of
	 * @return the created protocol.
	 * @throws InstantiateException
	 *             if failure occurs while constructing the rep item.
	 */
	public static MultilateralProtocol createFrom(
			MultiPartyProtocolRepItem protocolRepItem)
			throws InstantiateException {
		ClassLoader loader = ClassLoader.getSystemClassLoader();
		Class protocolClass;
		try {
			protocolClass = loader.loadClass(protocolRepItem.getClassPath());

			@SuppressWarnings("unchecked")
			Constructor protocolConstructor = protocolClass.getConstructor();

			return (MultilateralProtocol) protocolConstructor.newInstance();
		} catch (Exception e) {
			throw new InstantiateException("failed to instantiate "
					+ protocolRepItem, e);
		}

	}

	/**
	 * Create a new UtilitySpace from a ProfileRepItem. If
	 * {@link ProfileRepItem#getDomain()} returns new instead of an actual
	 * domain, this method also returns null.
	 *
	 * @param item
	 *            the item to create a UtilitySpace out of.
	 * @return the UtilitySpace corresponding to the item.
	 * @throws java.lang.Exception
	 *             If
	 *             {@link negotiator.repository.Repository#copyFrom(negotiator.repository.Repository)}
	 *             throws an exception.
	 */
	public static UtilitySpace createFrom(ProfileRepItem item) throws Exception {
		Domain domain = Repository.get_domain_repos().getDomain(
				item.getDomain());
		return Repository.get_domain_repos().getUtilitySpace(domain, item);
	}

	@Override
	public int getMediatorIndex() {
		return getPartyItems().indexOf(getMediatorItem());
	}

	@Override
	public void setMediatorIndex(int index) {
		setMediatorItem(getPartyItems().get(index));

	}

	@Override
	public MultilateralProtocol getProtocol() throws Exception {
		return createFrom(getProtocolItem());
	}

	/**
	 * Get the {@link negotiator.session.Session} object from this configuration
	 *
	 * @return Session object represented in this configuration
	 */
	public Session getSession() {
		// HACK move this to caller
		if (session == null) {
			session = new Session(getDeadline());
		}

		return session;
	}

	/**
	 * Get the type of tournament
	 *
	 * @return the type of tournament
	 */
	@Override
	public String getTournamentType() {
		return tournamentType;
	}

	/**
	 * Set the type of tournament
	 *
	 * @param type
	 *            the type of tournament
	 */
	@Override
	public void setTournamentType(String type) {
		tournamentType = type;
	}

	/**
	 * Get the list of participating {@link negotiator.parties.NegotiationParty}
	 * objects from this configuration
	 *
	 * @return list of party objects represented in this configuration or an
	 *         empty list if none
	 * @throws java.lang.NoSuchMethodException
	 *             If requested Party does not have a constructor accepting only
	 *             preference profiles
	 * @throws java.lang.ClassNotFoundException
	 *             If requested Party class can not be found.
	 * @throws java.lang.Exception
	 *             If
	 *             {@link negotiator.repository.Repository#copyFrom(negotiator.repository.Repository)}
	 *             throws an exception.
	 */
	public TournamentGenerator getPartiesGenerator() {

		TournamentIndicesGenerator indicesGenerator = new TournamentIndicesGenerator(
				getNumAgentsPerSession(), getPartyProfileItems().size(),
				getRepetitionAllowed(), getPartyItems().size());
		return new TournamentGenerator(this, indicesGenerator);
	}

	public int numSessionsPerTournament() {
		int nAgents = getPartyItems().size();
		int nProfiles = getPartyProfileItems().size();
		int perSession = getNumAgentsPerSession();

		int profileCombos = factorial(nProfiles)
				/ (factorial(perSession) * factorial(nProfiles - perSession));
		int agentCombos = getRepetitionAllowed() ? (int) Math.pow(nAgents,
				perSession) : factorial(nAgents)
				/ factorial(nAgents - perSession);

		return agentCombos * profileCombos;
	}

	private int factorial(int n) {
		return n <= 1 ? 1 : n * factorial(n - 1);
	}

}
