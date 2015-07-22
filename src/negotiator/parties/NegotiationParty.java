package negotiator.parties;

import java.util.List;

import negotiator.AgentID;
import negotiator.Deadline;
import negotiator.actions.Action;
import negotiator.protocol.MultilateralProtocol;
import negotiator.session.TimeLineInfo;
import negotiator.session.Timeline;
import negotiator.utility.UtilitySpace;

/**
 * Base interface for Negotiation parties. All parties must minimally implement
 * this interface. This can be used for bilateral as well as multilateral
 * negotiations. <br>
 * All time that {@link NegotiationParty}s spend in their code, including the
 * time spent in their constructor and init calls, is subtracting from the total
 * available time.
 * 
 * <h1>IMPORTANT</h1> Implementors of this class must have a public no-argument
 * constructor. In fact we recommend not to implement any constructor at all.
 * Initialization will be done through
 * {@link #init(UtilitySpace, Deadline, Timeline, long)}.
 * 
 * <h1>history</h1> {@link NegotiationParty} replaces {@link negotiator.Agent} .
 * If you are using a protocol that inherits from {@link MultilateralProtocol}
 * you should also use {@link NegotiationParty} and not {@link negotiator.Agent}
 * .
 * 
 * 
 *
 * @author David Festen
 * @author W.Pasman 21jul15
 * 
 * @modified W.Pasman removed all but essential code. Parties can extend
 *           {@link AbstractNegotiationParty} to get more support from the
 *           parent class.
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
	 * @param agentID
	 *            the agent's ID.
	 * @throws RuntimeException
	 *             if init fails.
	 */
	public void init(UtilitySpace utilSpace, Deadline deadline,
			TimeLineInfo timeline, long randomSeed, AgentID agentID);

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

}
