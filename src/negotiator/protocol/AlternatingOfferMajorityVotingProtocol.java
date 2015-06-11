package negotiator.protocol;

import negotiator.Bid;
import negotiator.actions.Accept;
import negotiator.actions.Action;
import negotiator.actions.Offer;
import negotiator.actions.OfferForVoting;
import negotiator.actions.Reject;
import negotiator.parties.NegotiationParty;
import negotiator.session.Round;
import negotiator.session.Session;
import negotiator.session.Turn;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of an alternating offer protocol using majority voting
 * <p/>
 * Protocol:
 * <pre>
 * Round 1 (offers): Each agent makes an offer
 * Round 2 (voting): Each agent votes for each offer on the table
 *
 * The offer that is supported by the most parties, will stay on the table.
 * If a new offer has more supporting parties, it overwrites the old offer.
 * This protocol always has some agreement.
 *
 * When deadline reached, the most recent agreement will be considered the final agreement.
 * </pre>
 *
 * @author David Festen
 * @author Reyhan
 */
public class AlternatingOfferMajorityVotingProtocol extends MultilateralProtocolAdapter {

    /**
     * Holds the most recently accepted offer. i.e. The offer with the most support
     */
    private Offer mostRecentlyAcceptedOffer;

    /**
     * Holds the number of parties that voted for the most recently accepted offer.
     */
    private int mostRecentlyAcceptedOfferVoteCount;

    /**
     * Holds the total number of parties in this session (used for checking if everyone agrees).
     */
    private int numberOfAgents;

    /**
     * Get the structure of the current round. Each round, this method receives a list of all the
     * {@link negotiator.parties.NegotiationParty} and the complete {@link negotiator.session
     * .Session} which can be used to diversify the round
     * structure at some point during the session.
     *
     * @param parties The parties currently participating
     * @param session The complete session history
     * @return A list of possible actions
     */
    @Override
    public Round getRoundStructure(List<NegotiationParty> parties, Session session) {
        Round round = new Round();

        if (isVotingRound(session)) {
            // request an offer from each party
            for (NegotiationParty party : parties) {
                round.addTurn(new Turn(party, OfferForVoting.class));
            }
        } else {
            ArrayList<Class> acceptOrReject = new ArrayList<Class>(2);
            acceptOrReject.add(Accept.class);
            acceptOrReject.add(Reject.class);

            for (NegotiationParty ignored : parties) {
                for (NegotiationParty votingParty : parties) {
                    round.addTurn(new Turn(votingParty, acceptOrReject));
                }
            }
        }
        numberOfAgents = parties.size();
        return round;
    }

    /**
     * Check if the protocol is done or still busy. If this method returns true, the
     * {@link negotiator.session.SessionManager} will not start a new {@link negotiator.session
     * .Round} after the
     * current one. It will however finish all the turns described in the
     * {@link #getRoundStructure(java.util.List, negotiator.session.Session)} method.
     *
     * @param session the current state of this session
     * @return true if the protocol is finished
     */
    @Override
    public boolean isFinished(Session session, List<NegotiationParty> parties) {
        if (isVotingRound(session)) {
            Round votingRound = session.getMostRecentRound();
            Round offerRound = session.getRounds().get(session.getRoundNumber() - 2);
            acceptedOffer(votingRound, offerRound);
        }

        // if everyone accepts a vote, we're done, otherwise continue
        return mostRecentlyAcceptedOfferVoteCount == numberOfAgents;
    }

    /**
     * Gets the most recent agreement.
     *
     * @param session The complete session history up to this point
     * @return The agreed upon bid or null if no agreement
     */
    @Override
    public Bid getCurrentAgreement(Session session, List<NegotiationParty> parties) {
        int round = session.getRoundNumber();

        // if less then two rounds, no accepted offer
        if (round < 2) {
            return null;
        }

        // if not in voting round, offset round number by one
        if (!isVotingRound(session)) {
            round--;
        }

        Round votingRound = session.getRounds().get(round - 1);
        Round offerRound = session.getRounds().get(round - 2);
        Offer acceptedOffer = acceptedOffer(votingRound, offerRound);
        return acceptedOffer == null ? null : acceptedOffer.getBid();
    }

    /**
     * Returns whether this is a voting round.
     *
     * @param session the current state of this session
     * @return true is this is an even round
     */
    private boolean isVotingRound(Session session) {
        return session.getRoundNumber() % 2 == 0;
    }

    /**
     * returns the first offer with more support than the current one, or null if no such offer.
     *
     * @param votingRound the round with the voting (expected number of turns is agent# * agent#)
     * @param offerRound  the round with the offers (expected number of turns is agent#)
     * @return The accepted offer if such an offer exists, null otherwise
     */
    private Offer acceptedOffer(Round votingRound, Round offerRound) {
        int numOffers = numberOfAgents;
        List<Turn> turns = votingRound.getTurns();
        List<Action> voteActions = offerRound.getActions();

        // for each block of offers
        for (int i = 0; i < numOffers * numOffers; i += numOffers) {
            int offerNumber = (i / numOffers);
            int votes = 0;
            // for each vote on this offer
            for (int j = i; j < i + numOffers; j++) {
                // count the vote
                if (turns.get(j).getAction() instanceof Accept) {
                    votes++;
                }
            }
            // if enough votes, accept bid
            if (votes > mostRecentlyAcceptedOfferVoteCount) {
                mostRecentlyAcceptedOffer = (Offer) voteActions.get(offerNumber);
                mostRecentlyAcceptedOfferVoteCount = votes;
                System.out.println("    New most recently accepted bid (votes="
                        + mostRecentlyAcceptedOfferVoteCount + "/" + numberOfAgents + "): "
                        + mostRecentlyAcceptedOffer);
            }
        }

        return mostRecentlyAcceptedOffer;
    }

    /**
     * Gets the number of parties that currently agree to the offer.
     *
     * @param session the current state of this session
     * @param parties The parties currently participating
     * @return the number of parties agreeing to the current agreement
     */
    @Override
    public int getNumberOfAgreeingParties(Session session, List<NegotiationParty> parties) {
        return mostRecentlyAcceptedOfferVoteCount;
    }
}
