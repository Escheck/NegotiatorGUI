package negotiator.parties;

import negotiator.AgentID;
import negotiator.DeadlineType;
import negotiator.Timeline;
import negotiator.Vote;
import negotiator.actions.Action;
import negotiator.actions.InformVotingResult;
import negotiator.actions.OfferForVoting;
import negotiator.actions.VoteForOfferAcceptance;
import negotiator.utility.UtilitySpace;

import java.util.List;
import java.util.Map;

/**
 * Implementation of a party that uses hill climbing strategy to get to an agreement.
 * <p/>
 * This party should be run with {@link negotiator.protocol.MediatorProtocol}
 *
 * @author David Festen
 * @author Reyhan
 */
public class HillClimber extends AbstractNegotiationParty {

    private double lastAcceptedBidUtility;

    private double lastReceivedBidUtility;

    private Vote currentVote;

    /**
     * Initializes a new instance of the {@link HillClimber} class.
     *
     * @param utilitySpace The utility space used by this class
     * @param deadlines    The deadlines for this session
     * @param timeline     The time line (if time deadline) for this session, can be null
     * @param randomSeed   The seed that should be used for all randomization (to be reproducible)
     */
    public HillClimber(UtilitySpace utilitySpace, Map<DeadlineType, Object> deadlines,
                       Timeline timeline, long randomSeed) {
        super(utilitySpace, deadlines, timeline, randomSeed);
        lastAcceptedBidUtility = 0;
        lastReceivedBidUtility = 0;
        currentVote = Vote.REJECT;
    }


    /**
     * When this class is called, it is expected that the Party chooses one of the actions from the
     * possible action list and returns an instance of the chosen action. This class is only called
     * if this {@link NegotiationParty} is in the {@link negotiator.protocol
     * .Protocol#getRoundStructure(java.util.List, negotiator.session.Session)}.
     *
     * @param possibleActions List of all actions possible.
     * @return The chosen action
     */
    @Override
    public Action chooseAction(List<Class> possibleActions) {
        return new VoteForOfferAcceptance(getPartyId(), currentVote);
    }

    /**
     * This method is called when an observable action is performed. Observable actions are defined
     * in {@link negotiator.protocol.Protocol#getActionListeners(java.util.List)}
     *
     * @param sender    The initiator of the action
     * @param arguments The action performed
     */
    @Override
    public void receiveMessage(Object sender, Action arguments) {
        if (arguments instanceof OfferForVoting) {
            lastReceivedBidUtility = getUtility(((OfferForVoting) arguments).getBid());
            double reservationValue = (timeline == null)
                    ? utilitySpace.getReservationValue()
                    : utilitySpace.getReservationValueWithDiscount(timeline);

            if (lastReceivedBidUtility < reservationValue) {
                currentVote = Vote.REJECT;
            } else {
                currentVote = lastReceivedBidUtility >= lastAcceptedBidUtility ? Vote.ACCEPT :
                        Vote.REJECT;
            }
        } else if (arguments instanceof InformVotingResult) {
            if (((InformVotingResult) arguments).getVotingResult() == Vote.ACCEPT) {
                lastAcceptedBidUtility = lastReceivedBidUtility;
            }
        }
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
     * @return An uniquely identifying agent id
     */
    @Override
    public AgentID getPartyId() {
        return partyId == null ? new AgentID("HillClimber@" + hashCode()) : partyId;
    }
}
