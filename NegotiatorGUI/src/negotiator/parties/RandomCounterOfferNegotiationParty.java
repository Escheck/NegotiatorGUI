package negotiator.parties;

import negotiator.AgentID;
import negotiator.Bid;
import negotiator.DeadlineType;
import negotiator.Timeline;
import negotiator.actions.Accept;
import negotiator.actions.Action;
import negotiator.actions.EndNegotiation;
import negotiator.actions.Offer;
import negotiator.utility.UtilitySpace;

import java.util.List;
import java.util.Map;

/**
 * Basic voting implementation: this agent accepts and rejects offers with a 50% chance.
 * <p/>
 * The class was created as part of a series of agents used to understand the api better
 *
 * @author David Festen
 */
public class RandomCounterOfferNegotiationParty extends AbstractNegotiationParty
{
    /**
     * Initializes a new instance of the {@link negotiator.parties.RandomCounterOfferNegotiationParty} class.
     *
     * @param utilitySpace The utility space used by this class
     * @param deadlines    The deadlines for this session
     * @param timeline     The time line (if time deadline) for this session, can be null
     * @param randomSeed   The seed that should be used for all randomization (to be reproducible)
     */
    public RandomCounterOfferNegotiationParty(UtilitySpace utilitySpace, Map<DeadlineType, Object>
            deadlines, Timeline timeline, long randomSeed)
    {

        super(utilitySpace, deadlines, timeline, randomSeed);
    }

    /**
     * If placing offers: do random offer
     * if voting: accept/reject with a 50% chance on both
     *
     * @param possibleActions List of all actions possible.
     * @return The chosen action
     */
    @Override
    public Action chooseAction(List<Class> possibleActions)
    {

        // if we can not accept, we need to do random voting
        if (!possibleActions.contains(Accept.class))
        {
            return new Offer(getPartyId(), generateRandomBid());
        }

        // else do 10/90: random offer, accept
        return timeline.getTime() >= 0.9999999
                ? new Accept(getPartyId())
                : new Offer(getPartyId(), generateRandomNonZeroBid());
    }

    private Bid generateRandomNonZeroBid()
    {
        Bid randomBid;
        double util;
        do
        {
            randomBid = generateRandomBid();
            try
            {
                util = utilitySpace.getUtility(randomBid);
            } catch (Exception e)
            {
                util = 0.0;
            }
        }
        while (util < 0.1);
        return randomBid;
    }

    /**
     * Processes action messages received by a given sender.
     *
     * @param sender    The initiator of the action
     * @param arguments The action performed
     */
    @Override
    public void receiveMessage(Object sender, Action arguments)
    {
        // not used
    }

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
    @Override
    public AgentID getPartyId()
    {
        return (partyId == null ? new AgentID(String.format("Random#%4s", hashCode())) : partyId);
    }
}
