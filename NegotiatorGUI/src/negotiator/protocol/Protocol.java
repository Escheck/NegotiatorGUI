package negotiator.protocol;

import negotiator.Bid;
import negotiator.actions.Action;
import negotiator.exceptions.NegotiationPartyTimeoutException;
import negotiator.parties.NegotiationParty;
import negotiator.session.ExecutorWithTimeout;
import negotiator.session.Round;
import negotiator.session.Session;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * This interface defines a protocol that should be executed by the
 * {@link negotiator.session.SessionManager}. There also exists an
 * {@link ProtocolAdapter} which implements all the methods
 * in this interface with default values. *
 * <p/>
 * For some examples on Alternating offer protocols, please refer to:
 * {@link negotiator.protocol.AlternatingOfferConsensusProtocol}
 * {@link negotiator.protocol.AlternatingOfferCounterOfferProtocol}
 * {@link negotiator.protocol.AlternatingOfferMajorityVotingProtocol}
 * <p/>
 * For an example on Mediator protocols, please refer to:
 * {@link negotiator.protocol.MediatorProtocol}
 *
 * @author David Festen
 */
public interface Protocol {

    /**
     * Get the structure of the current round. Each round, this method receives a list of all the
     * {@link negotiator.parties.NegotiationParty} and the complete {@link Session} which can be
     * used to diversify the round
     * structure at some point during the session.
     *
     * @param parties The parties currently participating
     * @param session The complete session history
     * @return A list of possible actions
     */
    Round getRoundStructure(List<NegotiationParty> parties, Session session);

    /**
     * This will get called just before the session starts. If some initialization needs to be
     * done by the protocol, it can be done here.
     *
     * @param session The session instance that will be used for the session
     * @param parties The parties that will participate in the session
     */
    void beforeSession(Session session, List<NegotiationParty> parties) throws NegotiationPartyTimeoutException, ExecutionException, InterruptedException;

    /**
     * This will get called just after ending the session. If the protocol needs to do some post
     * session steps, it can be done here.
     *
     * @param session The session instance that was used for the session
     * @param parties The parties that participated in the session
     */
    void afterSession(Session session, List<NegotiationParty> parties);

    /**
     * Apply the action according to the protocol. All actions done by all agents come through this
     * method. If protocol needs to adapt anything according to actions, it can be handled here.
     *
     * @param action  action to apply
     * @param session the current state of this session
     */
    void applyAction(Action action, Session session);

    /**
     * Check if the protocol is done or still busy. If this method returns true, the
     * {@link negotiator.session.SessionManager} will not start a new {@link Round} after the
     * current one. It will however finish all the turns described in the
     * {@link #getRoundStructure(java.util.List, negotiator.session.Session)} method.
     *
     * @param session the current state of this session
     * @return true if the protocol is finished
     */
    boolean isFinished(Session session, List<NegotiationParty> parties);

    /**
     * Get a map of parties that are listening to each other's response
     *
     * @param parties The parties involved in the current negotiation
     * @return A map where the key is a {@link NegotiationParty} that is responding to a
     * {@link NegotiationParty#chooseAction(List)} event, and the value is a list of
     * {@link NegotiationParty}s that are listening to that key party's response.
     */
    Map<NegotiationParty, List<NegotiationParty>> getActionListeners(List<NegotiationParty> parties);

    /**
     * This method should return the current agreement.
     * <p/>
     * Some protocols only have an agreement at the negotiation session, make sure that this method
     * returns null until the end of the session in that case, because this method might be queried
     * at intermediary steps.
     *
     * @param session The complete session history up to this point
     * @param parties The parties involved in the current negotiation
     * @return The agreed upon bid or null if no agreement
     */
    Bid getCurrentAgreement(Session session, List<NegotiationParty> parties);

    /**
     * Gets the number of parties that currently agree to the offer. For protocols that either have
     * an agreement or not, you can set this number to 0 until an agreement is found, and then set
     * this value to the number of parties.
     *
     * @param session the current state of this session
     * @param parties The parties currently participating
     * @return the number of parties agreeing to the current agreement
     */
    int getNumberOfAgreeingParties(Session session, List<NegotiationParty> parties);


    /**
     * Overwrites the rest of the protocol and sets the protocol state to finish
     */
    void endNegotiation();

    /**
     * Overwrites the rest of the protocol and sets the protocol state to finish
     *
     * @param reason Optionally give a reason why the protocol is finished.
     */
    void endNegotiation(String reason);

    /**
     * Gets the executor used to box actions that agents can influence.
     * This counts the action towards agent's time and prevents it from stalling.
     */
    ExecutorWithTimeout getExecutor();

    /**
     * Sets the executor used to box actions that agents can influence.
     * This counts the action towards agent's time and prevents it from stalling.
     */
    void setExecutor(ExecutorWithTimeout executor);
}
