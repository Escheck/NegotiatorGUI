package negotiator.parties;

import negotiator.AgentID;
import negotiator.Bid;
import negotiator.exceptions.NegotiatorException;
import negotiator.repository.PartyRepItem;
import negotiator.repository.ProfileRepItem;
import negotiator.session.RepositoryException;
import negotiator.session.Session;
import negotiator.session.Timeline;
import negotiator.utility.UtilitySpace;

/**
 * Only for use in the core. Keeps a NegotiationParty along with core-private
 * information.
 * 
 * @modified W.Pasman 21jul15
 */
public class NegotiationPartyInternal {
	private NegotiationParty party;
	private UtilitySpace utilitySpace;
	private Session session;
	private AgentID agentId;

	// private final Deadline deadline;
	//

	/**
	 * Constructor.
	 * 
	 * @param partyRepItem
	 * @param profileRepItem
	 * @param session
	 * @param agentID
	 *            the agentId to use, or null if auto ID is ok. For all default
	 *            implementations, this has either the format "ClassName" if
	 *            only one such an agent exists (in case of mediator for example
	 *            [mediator always has the name "mediator"]) or it has the
	 *            format "ClassName@HashCode" if multiple agents of the same
	 *            type can exists. You could also use the random hash used in
	 *            the agent to identify it (making it easier to reproduce
	 *            results).
	 * @throws RepositoryException
	 * @throws NegotiatorException
	 */
	public NegotiationPartyInternal(PartyRepItem partyRepItem,
			ProfileRepItem profileRepItem, Session session, AgentID agentID)
			throws RepositoryException, NegotiatorException {
		this.session = session;
		this.agentId = agentID;
		init(partyRepItem, profileRepItem, session);
	}

	/**
	 * Creates a new {@link NegotiationParty} from repository items and
	 * initializes it.
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
	private NegotiationParty init(PartyRepItem partyRepItem,
			ProfileRepItem profileRepItem, Session session)
			throws RepositoryException, NegotiatorException {
		utilitySpace = profileRepItem.create();
		long randomSeed = System.currentTimeMillis();
		party = createInstance(partyRepItem, profileRepItem, session);
		party.init(new UtilitySpace(utilitySpace), session.getDeadlines(),
				session.getTimeline(), randomSeed, getAgentId());
		return party;
	}

	/**
	 * Gets the party id for this party. party must have been set.
	 *
	 * @return The uniquely identifying party id.
	 */
	public AgentID getAgentId() {
		if (party == null) {
			throw new IllegalStateException("party is not initialized.");
		}
		if (agentId == null) {
			agentId = new AgentID(party.getClass().getSimpleName() + "@"
					+ hashCode());
		}
		return agentId;
	}

	/**
	 * Creates a new Party from repository items, but does not yet call init.
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
	@SuppressWarnings("unchecked")
	private NegotiationParty createInstance(PartyRepItem partyRepItem,
			ProfileRepItem profileRepItem, Session session)
			throws RepositoryException, NegotiatorException {
		Exception exception = null;
		String extraMessage = "";

		ClassLoader loader = ClassLoader.getSystemClassLoader();
		Class<? extends NegotiationParty> partyClass;
		try {
			partyClass = (Class<? extends NegotiationParty>) loader
					.loadClass(partyRepItem.getClassPath());

			return (NegotiationParty) partyClass.getConstructor().newInstance();
		} catch (NoSuchMethodException e) {
			extraMessage = ": no public default constructor was found";
			exception = e;
		} catch (Exception e) {
			exception = e;
		}

		// if we get here there was an exception.
		throw new NegotiatorException(
				"An exception occured while creating agent "
						+ partyRepItem.getName() + " using profile "
						+ profileRepItem + extraMessage, exception);
	}

	/**
	 * Get the agent implementation.
	 *
	 * @return
	 */
	public NegotiationParty getParty() {
		return party;
	}

	/**
	 * Gets the utility for the given bid.
	 *
	 * @param bid
	 *            The bid to get the utility for
	 * @return A double value between [0, 1] (inclusive) that represents the
	 *         bids utility
	 */
	public double getUtility(Bid bid) {
		try {
			// throws exception if bid incomplete or not in utility space
			return bid == null ? 0 : utilitySpace.getUtility(bid);
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}

	/**
	 * Gets the agents utility for a given bid, taking into account a discount
	 * factor if present.
	 *
	 * @param bid
	 *            The bid to get the utility of
	 * @return the utility that the agent has for the given bid
	 */
	public double getUtilityWithDiscount(Bid bid) {
		if (bid == null) {
			// utility is null if no bid
			return 0;
		} else if (session.getTimeline() == null) {
			// return undiscounted utility if no timeline given
			return getUtility(bid);
		} else {
			// otherwise, return discounted utility
			return utilitySpace.getUtilityWithDiscount(bid,
					session.getTimeline());
		}

	}

	/**
	 * Gets the agent's utility space.
	 *
	 * @return the agent's utility space
	 */
	public UtilitySpace getUtilitySpace() {
		return utilitySpace;
	}

	/**
	 * Gets the timeline for this agent.
	 *
	 * @return The timeline object or null if no timeline object (no time
	 *         constraints) set
	 */
	public Timeline getTimeLine() {
		return session.getTimeline();
	}

	/**
	 * Gets the agent's unique id
	 * <p/>
	 * Each agent should contain a (unique) id. This id is used in log files to
	 * trace the agent's behavior.
	 *
	 * @return A uniquely identifying agent id
	 */
	public AgentID getPartyId() {
		return agentId;
	}

	/**
	 * Get the session that this party is using.
	 * 
	 * @return {@link Session}.
	 */
	public Session getSession() {
		return session;
	}

}
