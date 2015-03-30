package negotiator.parties;

import negotiator.AgentID;
import negotiator.Bid;
import negotiator.DeadlineType;
import negotiator.Feedback;
import negotiator.Timeline;
import negotiator.Vote;
import negotiator.actions.Action;
import negotiator.actions.GiveFeedback;
import negotiator.actions.InformVotingResult;
import negotiator.actions.OfferForFeedback;
import negotiator.actions.OfferForVoting;
import negotiator.actions.VoteForOfferAcceptance;
import negotiator.utility.UtilitySpace;

import java.util.List;
import java.util.Map;

/**
 * Hill climber implementation for the mediator protocol with feedback.
 * <p/>
 * This implementation was adapted from Reyhan's original code and refitted in the new framework
 * <p/>
 * <u>Possible bug:</u><br>
 * Possibly, there is a small bug in this code, which you will encounter when running this agent for
 * an extended period of time (i.e. minutes)
 *
 * @author David Festen
 * @author Reyhan (Orignal code)
 */
public class FeedbackHillClimber extends AbstractNegotiationParty {
    private double lastBidUtility;
    private double lastAcceptedUtility;
    private double currentBidUtility;
    private Feedback currentFeedback;
    private Vote currentVote;
    private boolean voteTime;

    /**
     * Initializes a new instance of the {@link negotiator.Party} class.
     *
     * @param utilitySpace The utility space used by this class
     * @param deadlines    The deadlines for this session
     * @param timeline     The time line (if time deadline) for this session, can be null
     * @param randomSeed   The seed that should be used for all randomization (to be reproducible)
     */
    public FeedbackHillClimber(UtilitySpace utilitySpace, Map<DeadlineType, Object> deadlines,
                               Timeline timeline, long randomSeed) {
        super(utilitySpace, deadlines, timeline, randomSeed);
        lastBidUtility = 0.0;
        lastAcceptedUtility = 0.0;
        currentBidUtility = 0.0;
        currentFeedback = Feedback.SAME;
        voteTime = false;
    }

    /**
     * When this class is called, it is expected that the Party chooses one of the actions from the
     * possible action list and returns an instance of the chosen action. This class is only called
     * if this {@link negotiator.Party} is in the {@link negotiator.protocol
     * .Protocol#getRoundStructure(java.util.List, negotiator.session.Session)}.
     *
     * @param possibleActions List of all actions possible.
     * @return The chosen action
     */
    @Override
    public Action chooseAction(List<Class> possibleActions) {
        if (voteTime) {
            return (new VoteForOfferAcceptance(getPartyId(), currentVote));
        } else {
            return (new GiveFeedback(getPartyId(), currentFeedback));
        }
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

        if (arguments instanceof InformVotingResult) {
            // update the utility of last accepted bid by all
            if (((InformVotingResult) arguments).getVotingResult() == Vote.ACCEPT) {
                lastAcceptedUtility = currentBidUtility;
            }
            return;
        }

        Bid receivedBid = Action.getBidFromAction(arguments);
        if (receivedBid == null) {
            return;
        }

        if (deadlines.containsKey(DeadlineType.TIME)) {
            currentBidUtility = getUtilityWithDiscount(receivedBid);
        } else {
            currentBidUtility = getUtility(receivedBid);
        }

        if (arguments instanceof OfferForFeedback) {
            currentFeedback = Feedback.madeupFeedback(lastBidUtility, currentBidUtility);
            voteTime = false;
        }
        if (arguments instanceof OfferForVoting) {
            voteTime = true;
            if (lastAcceptedUtility <= currentBidUtility) {
                currentVote = Vote.ACCEPT;
            } else {
                currentVote = Vote.REJECT;
            }
        }

        lastBidUtility = currentBidUtility;
    }

    @Override
    public AgentID getPartyId() {
        return new AgentID("FeedbackHillClimber@" + hashCode());
    }
}
