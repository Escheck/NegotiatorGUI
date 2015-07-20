package negotiator.tournament;

import static negotiator.utility.UTILITYSPACETYPE.getUtilitySpaceType;

import java.io.FileNotFoundException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import negotiator.AgentID;
import negotiator.Deadline;
import negotiator.Domain;
import negotiator.Timeline;
import negotiator.config.Configuration;
import negotiator.exceptions.NegotiatorException;
import negotiator.parties.NegotiationParty;
import negotiator.protocol.MultilateralProtocol;
import negotiator.repository.MultiPartyProtocolRepItem;
import negotiator.repository.PartyRepItem;
import negotiator.repository.ProfileRepItem;
import negotiator.repository.Repository;
import negotiator.session.RepositoryException;
import negotiator.session.Session;
import negotiator.utility.ConstraintUtilitySpace;
import negotiator.utility.NonlinearUtilitySpace;
import negotiator.utility.TournamentIndicesGenerator;
import negotiator.utility.UTILITYSPACETYPE;
import negotiator.utility.UtilitySpace;

/**
 * This class will generate the party lists for each negotiation in the
 * tournament. The reason for this generator is that the number of parties can
 * become quite large, which means that at some point, we are unable to
 * enumerate them all. This class will only give the next list parties to
 * overcome that limitation.
 * 
 * <p>
 * Agents in a tournament must be of class {@link NegotiationParty}.
 *
 * @author Festen
 */
public class TournamentGenerator {
	/**
	 * Holds the configuration used by this tournament generator
	 */
	final Configuration config;

	/**
	 * Holds the indices used by this tournament generator
	 */
	final Iterator<List<Integer>> indicesIterator;

	/**
	 * Creates a new instance of the {@link TournamentGenerator} class.
	 *
	 * @param config
	 *            The configuration used for the tournaments
	 * @param indicesGenerator
	 *            The indices generator used to generate lists of parties
	 */
	public TournamentGenerator(Configuration config,
			TournamentIndicesGenerator indicesGenerator) {
		this.config = config;
		this.indicesIterator = indicesGenerator.iterator();
	}

	/**
	 * Creates a new Party from repository items
	 *
	 * @param partyRepItem
	 *            Party Repository item to createFrom party from
	 * @param profileRepItem
	 *            Profile Repository item to createFrom party from
	 * @return new Party
	 * @throws RepositoryException
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
			ProfileRepItem profileRepItem, Session session)
			throws RepositoryException, NegotiatorException {
		UtilitySpace utilitySpace = createFrom(profileRepItem);

		ClassLoader loader = ClassLoader.getSystemClassLoader();
		Class party;
		try {
			party = loader.loadClass(partyRepItem.getClassPath());

			Class[] paramTypes = { UtilitySpace.class, Deadline.class,
					Timeline.class, long.class };

			@SuppressWarnings("unchecked")
			Constructor partyConstructor = party.getConstructor(paramTypes);

			long randomSeed = System.currentTimeMillis();

			return (NegotiationParty) partyConstructor.newInstance(
					utilitySpace, session.getDeadlines(),
					session.getTimeline(), randomSeed);
		} catch (ClassNotFoundException e) {
			throw new NegotiatorException("Problem creating agent "
					+ partyRepItem + " using profile " + profileRepItem, e);
		} catch (NoSuchMethodException e) {
			throw new NegotiatorException(
					"Agent  "
							+ partyRepItem
							+ "has no constructor taking parameters (UtilitySpace, Deadline, Timeline, long)",
					e);
		} catch (SecurityException e) {
			throw new NegotiatorException("Problem creating agent "
					+ partyRepItem + " using profile " + profileRepItem, e);
		} catch (InstantiationException e) {
			throw new NegotiatorException("Problem creating agent "
					+ partyRepItem + " using profile " + profileRepItem, e);
		} catch (IllegalAccessException e) {
			throw new NegotiatorException("Problem creating agent "
					+ partyRepItem + " using profile " + profileRepItem, e);
		} catch (IllegalArgumentException e) {
			throw new NegotiatorException("Problem creating agent "
					+ partyRepItem + " using profile " + profileRepItem, e);
		} catch (InvocationTargetException e) {
			throw new NegotiatorException("Problem creating agent "
					+ partyRepItem + " using profile " + profileRepItem, e);
		}
	}

	/**
	 * Create a new instance of the Protocol object from a
	 * {@link negotiator.repository.MultiPartyProtocolRepItem}
	 *
	 * @param protocolRepItem
	 *            Item to create Protocol out of
	 * @return the created protocol.
	 * @throws java.lang.Exception
	 *             If
	 *             {@link negotiator.repository.Repository#copyFrom(negotiator.repository.Repository)}
	 *             throws an exception.
	 */
	public static MultilateralProtocol createFrom(
			MultiPartyProtocolRepItem protocolRepItem) throws Exception {
		ClassLoader loader = ClassLoader.getSystemClassLoader();
		Class protocolClass = loader.loadClass(protocolRepItem.getClassPath());

		@SuppressWarnings("unchecked")
		Constructor protocolConstructor = protocolClass.getConstructor();

		return (MultilateralProtocol) protocolConstructor.newInstance();
	}

	/**
	 * Create a new UtilitySpace from a ProfileRepItem. If
	 * {@link ProfileRepItem#getDomain()} returns new instead of an actual
	 * domain, this method also returns null.
	 *
	 * @param item
	 *            the item to create a UtilitySpace out of.
	 * @return the UtilitySpace corresponding to the item.
	 * @throws FileNotFoundException
	 * @throws RepositoryException
	 * @throws java.lang.Exception
	 *             If
	 *             {@link negotiator.repository.Repository#copyFrom(negotiator.repository.Repository)}
	 *             throws an exception.
	 */
	public static UtilitySpace createFrom(ProfileRepItem item)
			throws RepositoryException {
		Domain domain;
		try {
			domain = Repository.get_domain_repos().getDomain(item.getDomain());

			return Repository.get_domain_repos().getUtilitySpace(domain, item);
		} catch (Exception e) {
			throw new RepositoryException("File not found for " + item, e);
		}
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
			Domain domain, UTILITYSPACETYPE type, Session session)
			throws Exception {
		if (type == null) {
			type = UTILITYSPACETYPE.LINEAR;
		}

		ClassLoader loader = ClassLoader.getSystemClassLoader();
		Class party = loader.loadClass(partyRepItem.getClassPath());

		Class[] paramTypes = { UtilitySpace.class, Deadline.class,
				Timeline.class, long.class };

		@SuppressWarnings("unchecked")
		Constructor partyConstructor = party.getConstructor(paramTypes);

		// System.out.println("Found the constructor: " + cons);

		UtilitySpace utilitySpace;
		switch (type) {
		case NONLINEAR:
			utilitySpace = new NonlinearUtilitySpace(domain);
			break;
		case CONSTRAINT:
			utilitySpace = new ConstraintUtilitySpace(domain);
			break;
		default:
			utilitySpace = new UtilitySpace(domain);
		}

		long randomSeed = System.currentTimeMillis();
		return (NegotiationParty) partyConstructor.newInstance(utilitySpace,
				session.getDeadlines(), session.getTimeline(), randomSeed);
	}

	/**
	 * Generate the list of parties in the given session
	 *
	 * @param partyRepItems
	 *            The repository items for parties
	 * @param profileRepItems
	 *            The repository items for profiles
	 * @param mediatorIndex
	 *            The index of the mediator in the returned list (usually this
	 *            is 0)
	 * @param mediatorRepItem
	 *            The mediator repository item (or null)
	 * @param mediatorProfileRepItem
	 *            The mediator repository profile (or null)
	 * @param session
	 *            The session used fot this session
	 * @return The list of parties for this session
	 * @throws NegotiatorException
	 *             if agent can not be created
	 * @throws RepositoryException
	 *             if repository describing agent can not be loaded
	 */
	public static List<NegotiationParty> generateSessionParties(
			List<PartyRepItem> partyRepItems,
			List<ProfileRepItem> profileRepItems, List<AgentID> partyIds,
			int mediatorIndex, PartyRepItem mediatorRepItem,
			ProfileRepItem mediatorProfileRepItem, Session session)
			throws RepositoryException, NegotiatorException {

		return generateSessionParties(
				generateIntegerList(partyRepItems.size()), partyRepItems,
				profileRepItems, partyIds, mediatorIndex, mediatorRepItem,
				mediatorProfileRepItem, session);
	}

	/**
	 * Generate the list of parties in the given session
	 *
	 * @param partyIndices
	 *            The indices used for generating the session
	 * @param partyRepItems
	 *            The repository items for parties
	 * @param profileRepItems
	 *            The repository items for profiles
	 * @param mediatorIndex
	 *            The index of the mediator in the returned list (usually this
	 *            is 0)
	 * @param mediatorRepItem
	 *            The mediator repository item (or null)
	 * @param mediatorProfileRepItem
	 *            The mediator repository profile (or null)
	 * @param session
	 *            The session used fot this session
	 * @return The list of parties for this session
	 * @throws NegotiatorException
	 *             if agent can not be created
	 * @throws RepositoryException
	 *             if repository describing agent can not be loaded
	 */
	public static List<NegotiationParty> generateSessionParties(
			List<Integer> partyIndices, List<PartyRepItem> partyRepItems,
			List<ProfileRepItem> profileRepItems, List<AgentID> partyIds,
			int mediatorIndex, PartyRepItem mediatorRepItem,
			ProfileRepItem mediatorProfileRepItem, Session session)
			throws RepositoryException, NegotiatorException {

		List<NegotiationParty> parties = new ArrayList<NegotiationParty>();

		for (int i = 0; i < profileRepItems.size(); i++) {
			if (partyIndices.get(i) != null && partyIndices.get(i) >= 0) {
				PartyRepItem partyRepItem = partyRepItems.get(partyIndices
						.get(i));
				ProfileRepItem profileRepItem = profileRepItems.get(i);
				NegotiationParty party = createFrom(partyRepItem,
						profileRepItem, session);
				parties.add(party);
			}
		}
		NegotiationParty mediator = generateMediator(mediatorRepItem,
				mediatorProfileRepItem, profileRepItems.get(0), session);
		if (mediator != null)
			parties.add(mediatorIndex, mediator);

		if (partyIds != null)
			for (int i = 0; i < parties.size(); i++)
				parties.get(i).setPartyId(partyIds.get(i));

		return parties;
	}

	/**
	 * Generates a list of integer values. for example 5 -> [0,1,2,3,4]
	 *
	 * @param upToExclusive
	 *            The number to generate the list from
	 * @return a list of integers starting at 0 and going up to, but not
	 *         including the given number.
	 */
	public static List<Integer> generateIntegerList(int upToExclusive) {
		List<Integer> integerList = new ArrayList<Integer>(upToExclusive);

		for (int i = 0; i < upToExclusive; i++)
			integerList.add(i);

		return integerList;
	}

	/**
	 * Generates a mediator Party from the given information
	 *
	 * @param mediatorRepItem
	 *            The mediator repository item
	 * @param mediatorProfileRepItem
	 *            The mediator repository profile (or null)
	 * @param alternativeProfileRepItem
	 *            If no profile item is given, this is used to get the domain
	 * @param session
	 *            The session used fot this session
	 * @return A mediator party instance
	 */
	protected static NegotiationParty generateMediator(
			PartyRepItem mediatorRepItem,
			ProfileRepItem mediatorProfileRepItem,
			ProfileRepItem alternativeProfileRepItem, Session session) {
		try {
			// If mediator set and mediator profile set, add to default mediator
			// spot
			if (mediatorRepItem != null) {
				// if mediator profile also set, we'll create it add it at the
				// appropriate index
				if (mediatorProfileRepItem != null) {
					return createFrom(mediatorRepItem, mediatorProfileRepItem,
							session);
				}
				// if mediator has no profile, we'll base it on one of the
				// agents domains, the
				// assumption here is quite reasonable; all agents operate in
				// the same domain.
				else {
					UTILITYSPACETYPE utilType = extractUtilitySpaceType(alternativeProfileRepItem);
					Domain someAgentsDomain = createFrom(
							alternativeProfileRepItem).getDomain();
					return createFrom(mediatorRepItem, someAgentsDomain,
							utilType, session);
				}
			}
			// no mediator item, return null
			else
				return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	protected static UTILITYSPACETYPE extractUtilitySpaceType(
			ProfileRepItem profile) {
		return getUtilitySpaceType(profile.getURL().getFile());
	}

	/**
	 * Generates a list of parties at the given indices.
	 *
	 * @param indices
	 *            The indices of the parties to include
	 * @return The list of parties at the given instances
	 * @throws NegotiatorException
	 *             if agent can not be created
	 * @throws RepositoryException
	 *             if repository describing agent can not be loaded
	 */
	List<NegotiationParty> generateSessionParties(List<Integer> indices)
			throws RepositoryException, NegotiatorException {
		return generateSessionParties(indices, config.getPartyItems(),
				config.getPartyProfileItems(), null, config.getMediatorIndex(),
				config.getMediatorItem(), config.getMediatorProfile(),
				config.getSession());
	}

	/**
	 * returns next list of negotiating parties for the next session.
	 *
	 * @return an Iterator.
	 * @throws NegotiatorException
	 *             if agent can not be created
	 * @throws RepositoryException
	 *             if repository describing agent can not be loaded
	 */
	public List<NegotiationParty> next() throws RepositoryException,
			NegotiatorException {
		List<Integer> indices = indicesIterator.next();
		return generateSessionParties(indices);
	}

	public boolean hasNext() {
		return indicesIterator.hasNext();
	}

}
