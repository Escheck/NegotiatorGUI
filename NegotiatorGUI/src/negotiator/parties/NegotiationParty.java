package negotiator.parties;

import java.util.List;

import negotiator.AgentID;
import negotiator.Deadline;
import negotiator.actions.Action;
import negotiator.protocol.MultilateralProtocol;
import negotiator.protocol.StackedAlternatingOffersProtocol;
import negotiator.session.TimeLineInfo;
import negotiator.session.Timeline;
import negotiator.utility.AbstractUtilitySpace;
import negotiator.utility.AdditiveUtilitySpace;

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
 * {@link #init(AdditiveUtilitySpace, Deadline, Timeline, long)}.
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
	 *            (a copy of/readonly version of) the
	 *            {@link AdditiveUtilitySpace} to be used for this session.
	 * @param timeline
	 *            The {@link TimeLineInfo} about current session.
	 * @param agentID
	 *            the {@link AgentID}.
	 * @throws RuntimeException
	 *             if init fails.
	 */
	public void init(AbstractUtilitySpace utilSpace, Deadline deadline,
			TimeLineInfo timeline, long randomSeed, AgentID agentID);

	/**
	 * When this function is called, it is expected that the Party chooses one
	 * of the actions from the possible action list and returns an instance of
	 * the chosen action.
	 *
	 * @param possibleActions
	 *            List of all actions possible.
	 * @return The chosen {@link Action}.
	 */
	public Action chooseAction(List<Class<? extends Action>> possibleActions);

	/**
	 * This method is called when another {@link NegotiationParty} chose an
	 * {@link Action}.
	 *
	 * @param sender
	 *            The initiator of the action.This is either the AgentID, or
	 *            null if the sender is not an agent (e.g., the protocol).
	 * @param arguments
	 *            The action performed
	 */
	void receiveMessage(AgentID sender, Action arguments);

	/**
	 * @return a human-readable description for this party
	 */
	public String getDescription();

	/**
	 * Get the protocol that this party supports.
	 * 
	 * @return the actual supported {@link MultilateralProtocol}, usually
	 *         {@link StackedAlternatingOffersProtocol}.
	 */
	public Class<? extends MultilateralProtocol> getProtocol();

}
