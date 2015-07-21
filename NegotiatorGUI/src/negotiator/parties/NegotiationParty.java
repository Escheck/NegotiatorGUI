package negotiator.parties;

import java.util.List;

import negotiator.AgentID;
import negotiator.Bid;
import negotiator.Deadline;
import negotiator.actions.Action;
import negotiator.protocol.MultilateralProtocol;
import negotiator.session.Timeline;
import negotiator.utility.UtilitySpace;

/**
 * Represents a Party or Agent used in negotiation. {@link NegotiationParty}
 * replaces {@link negotiator.Agent}. This can be used for bilateral as well as
 * multilateral negotiations. If you are using a protocol that inherits from
 * {@link MultilateralProtocol} you should also use {@link NegotiationParty} and
 * not {@link negotiator.Agent}.
 * <p>
 * <em>IMPORTANT</em> Implementors of this class must have a public no-argument
 * constructor. In fact we recommend not to implement any constructor at all.
 * Initialization will be done through
 * {@link #init(UtilitySpace, Deadline, Timeline, long)}.
 * <p>
 * All time that {@link NegotiationParty}s spend in their code, including the
 * time spent in their constructor and init calls, is subtracting from the total
 * available time.
 * 
 *
 * @author David Festen
 * @modified W.Pasman 21jul15
 */
public interface NegotiationParty {
	/**
	 * This is the first call made to a NegotiationParty after its
	 * instantiation. Tells which utility space and timeline we are running in.
	 * This is called one time only.
	 *
	 * @param utilSpace
	 *            (a copy of/readonly version of) the {@link UtilitySpace} to be
	 *            used for this session.
	 * @param timeline
	 *            The TimeLineIn that governs the current session.
	 * @throws RuntimeException
	 *             if init fails.
	 */
	public void init(UtilitySpace utilSpace, Deadline deadline,
			Timeline timeline, long randomSeed);

	/**
	 * When this class is called, it is expected that the Party chooses one of
	 * the actions from the possible action list and returns an instance of the
	 * chosen action. This class is only called if this {@link NegotiationParty}
	 * is in the
	 * {@link MultilateralProtocol#getRoundStructure(List, negotiator.session.Session)}
	 * .
	 *
	 * @param possibleActions
	 *            List of all actions possible.
	 * @return The chosen action
	 */
	public Action chooseAction(List<Class> possibleActions);

	/**
	 * This method is called when an observable action is performed. Observable
	 * actions are defined in
	 * {@link MultilateralProtocol#getActionListeners(List)}
	 *
	 * @param sender
	 *            The initiator of the action
	 * @param arguments
	 *            The action performed
	 */
	void receiveMessage(Object sender, Action arguments);

	/**
	 * Gets the agents utility for a given bid.
	 *
	 * @param bid
	 *            The bid to get the utility of
	 * @return the utility that the agent has for the given bid
	 */
	double getUtility(Bid bid);

	/**
	 * Gets the agents utility for a given bid, taking into account a discount
	 * factor if present.
	 *
	 * @param bid
	 *            The bid to get the utility of
	 * @return the utility that the agent has for the given bid
	 */
	double getUtilityWithDiscount(Bid bid);

	/**
	 * Gets the agent's utility space.
	 *
	 * @return the agent's utility space
	 */
	UtilitySpace getUtilitySpace();

	/**
	 * Gets the timeline for this agent.
	 *
	 * @return The timeline object or null if no timeline object (no time
	 *         constraints) set
	 */
	Timeline getTimeLine();

	/**
	 * Sets the timeline object
	 *
	 * @param timeline
	 *            The timeline to set
	 */
	void setTimeLine(Timeline timeline);

	/**
	 * Gets the agent's unique id
	 * <p/>
	 * Each agent should contain a (unique) id. This id is used in log files to
	 * trace the agent's behavior. For all default implementations, this has
	 * either the format "ClassName" if only one such an agent exists (in case
	 * of mediator for example) or it has the format "ClassName@HashCode" if
	 * multiple agents of the same type can exists. You could also use the
	 * random hash used in the agent to identify it (making it easier to
	 * reproduce results).
	 *
	 * @return A uniquely identifying agent id
	 */
	AgentID getPartyId();

	/**
	 * Sets the agent's unique id
	 * <p/>
	 * Each agent should contain a (unique) id. This id is used in log files to
	 * trace the agent's behavior. For all default implementations, this has
	 * either the format "ClassName" if only one such an agent exists (in case
	 * of mediator for example) or it has the format "ClassName@HashCode" if
	 * multiple agents of the same type can exists. You could also use the
	 * random hash used in the agent to identify it (making it easier to
	 * reproduce results).
	 *
	 * @param id
	 *            The new id for this agent.
	 */
	// void setPartyId(AgentID id);
}
