package negotiator.parties;

import negotiator.AgentID;
import negotiator.Bid;
import negotiator.Timeline;
import negotiator.actions.Action;
import negotiator.utility.UtilitySpace;

import java.util.List;

/**
 * Represents a Party or Agent used in negotiation. {@link NegotiationParty} will replace
 * {@link negotiator.Agent} as party can be used for bilateral as well as multilateral cases. If
 * you are using a protocol that inherits from {@link negotiator.protocol.Protocol} you
 * should also use {@link NegotiationParty} and not {@link negotiator.Agent}.
 *
 * @author David Festen
 */
public interface NegotiationParty
{
    /**
     * When this class is called, it is expected that the Party chooses one of the actions from the
     * possible action list and returns an instance of the chosen action. This class is only called
     * if this {@link NegotiationParty} is in the
     * {@link negotiator.protocol.Protocol#getRoundStructure(List, negotiator.session.Session)}.
     *
     * @param possibleActions List of all actions possible.
     * @return The chosen action
     */
    Action chooseAction(List<Class> possibleActions);

    /**
     * This method is called when an observable action is performed. Observable actions are defined
     * in {@link negotiator.protocol.Protocol#getActionListeners(List)}
     *
     * @param sender    The initiator of the action
     * @param arguments The action performed
     */
    void receiveMessage(Object sender, Action arguments);

    /**
     * Gets the agents utility for a given bid.
     *
     * @param bid The bid to get the utility of
     * @return the utility that the agent has for the given bid
     */
    double getUtility(Bid bid);

    /**
     * Gets the agents utility for a given bid, taking into account a discount factor if present.
     *
     * @param bid The bid to get the utility of
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
     * @return The timeline object or null if no timeline object (no time constraints) set
     */
    Timeline getTimeLine();

    /**
     * Sets the timeline object
     *
     * @param timeline The timeline to set
     */
    void setTimeLine(Timeline timeline);

    /**
     * Gets the agent's unique id
     * <p/>
     * Each agent should contain a (unique) id. This id is used in log files to trace the agent's
     * behavior. For all default implementations, this has either the format "ClassName" if only
     * one such an agent exists (in case of mediator for example) or it has the format
     * "ClassName@HashCode" if multiple agents of the same type can exists. You could also use the
     * random hash used in the agent to identify it (making it easier to reproduce results).
     *
     * @return A uniquely identifying agent id
     */
    AgentID getPartyId();

    /**
     * Sets the agent's unique id
     * <p/>
     * Each agent should contain a (unique) id. This id is used in log files to trace the agent's
     * behavior. For all default implementations, this has either the format "ClassName" if only
     * one such an agent exists (in case of mediator for example) or it has the format
     * "ClassName@HashCode" if multiple agents of the same type can exists. You could also use the
     * random hash used in the agent to identify it (making it easier to reproduce results).
     *
     * @param id The new id for this agent.
     */
    void setPartyId(AgentID id);
}

