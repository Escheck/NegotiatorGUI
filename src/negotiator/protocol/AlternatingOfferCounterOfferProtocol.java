package negotiator.protocol;

import negotiator.Bid;
import negotiator.actions.*;
import negotiator.exceptions.NegotiationPartyTimeoutException;
import negotiator.parties.NegotiationParty;
import negotiator.session.Round;
import negotiator.session.Session;
import negotiator.session.Turn;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

/**
 * Implementation of an alternating offer protocol using offer/counter-offer.
 * <p/>
 * Protocol:
 * <pre>
 * The first agent makes an offer
 * Other agents can accept or make a counter-offer
 *
 * If no agent makes a counter-offer, the negotiation end with this offer.
 * Otherwise, the process continues until reaching deadline or agreement.
 * </pre>
 *
 * @author David Festen
 * @author Reyhan
 */
public class AlternatingOfferCounterOfferProtocol extends ProtocolAdapter {

    /**
     * Get all possible actions for given party in a given session.
     *
     * @param parties
     * @param session the current state of this session
     * @return A list of possible actions
     */

    /**
     * Defines the round structure.
     * <pre>
     * The first agent makes an offer
     * Other agents can accept or make a counter-offer
     * </pre>
     *
     * @param parties The parties currently participating
     * @param session The complete session history
     * @return A list of possible actions
     */
    @Override
    public Round getRoundStructure(List<NegotiationParty> parties, Session session) {
        Round round = new Round();

        for (NegotiationParty party : parties) {
            // Each party can either accept the outstanding offer, or propose a counteroffer.
            round.addTurn(new Turn(party, Accept.class, Offer.class, EndNegotiation.class));
        }


        if (session.getRoundNumber() == 0) {
            // If this is the first party in the first round, it can not accept.
            round.getTurns().get(0).removeValidAction(Accept.class);
        }

        // return round structure
        return round;
    }

    /**
     * Will return the current agreement.
     *
     * @param session The complete session history up to this point
     * @return The agreed upon bid or null if no agreement
     */
    @Override
    public Bid getCurrentAgreement(Session session, List<NegotiationParty> parties) {

        if (session.getMostRecentAction() instanceof Accept) {
            int numActionsFinalRound = session.getMostRecentRound().getActions().size();
            if (numActionsFinalRound > 1) {
                // return the action before the 'accept'
                return ((Offer) session.getMostRecentRound().getActions().get(numActionsFinalRound - 2)).getBid();
            } else {
                // accepted offer was last offer of previous round
                return ((Offer) session.getRounds().get(session.getRoundNumber() - 2).getMostRecentAction()).getBid();
            }
        } else {
            return null;
        }
    }

    /**
     * If all agents accept the most recent offer, than this negotiation ends.
     * Also, when any agent ends the negotiation (EndNegotiationAction) the negotiation ends
     *
     * @param session the current state of this session
     * @return true if the protocol is finished
     */
    @Override
    public boolean isFinished(Session session, List<NegotiationParty> parties) {
        return getCurrentAgreement(session, parties) != null || session.getMostRecentAction() instanceof EndNegotiation;
    }

    /**
     * Get a map of parties that are listening to each other.
     *
     * @return who is listening to who
     */
    @Override
    public Map<NegotiationParty, List<NegotiationParty>> getActionListeners(
            List<NegotiationParty> parties) {

        // create a new map of parties
        Map<NegotiationParty, List<NegotiationParty>> map = new HashMap<NegotiationParty, List<NegotiationParty>>();

        // for each party add each other party
        for (NegotiationParty listener : parties) {
            ArrayList<NegotiationParty> talkers = new ArrayList<NegotiationParty>();
            for (NegotiationParty talker : parties) {
                if (talker != listener) {
                    talkers.add(talker);
                }
            }
            map.put(listener, talkers);
        }

        return map;
    }

    @Override
    public void beforeSession(Session session, final List<NegotiationParty> parties) throws InterruptedException, ExecutionException, NegotiationPartyTimeoutException {
        super.beforeSession(session, parties);
        for (final NegotiationParty party : parties) {
            try {
                getExecutor().execute(new Callable<Object>() {
                    @Override
                    public Object call() throws Exception {
                        party.receiveMessage("Protocol", new Inform("NumberOfAgents", parties.size()));

                        return null;
                    }
                });
            } catch (TimeoutException e) {
                String msg = String.format("Negotiating party %s timed out in receiveMessage() method.", party.getPartyId());
                throw new NegotiationPartyTimeoutException(party, msg, e);
            }
        }
    }
}
