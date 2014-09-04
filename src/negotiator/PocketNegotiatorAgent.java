package negotiator;

import negotiator.actions.Action;
import negotiator.utility.UtilitySpace;

/**
 * #915 Agents of this type are compatible with the PocketNegotiator.
 * 
 * Agents in PocketNegotiator run on the server, in parallel with other threads
 * that may run the same agent. To be compatible, agents need to be
 * <ul>
 * <li>thread safe
 * <li>run efficient in multi-threading situations.
 * <li>should not attempt to do actions that violate security restrictions in a
 * Tomcat HttpServlet. eg, read or write files
 * <li>should return different bids each time. Repeating bids may lead to PN
 * showing the message "I do not have further suggestions".
 * </ul>
 * <br>
 * A PocketNegotiatorAgent adds new updateProfile functions to the agent. These
 * functions allow PocketNegotiator to change the profile while the agent is
 * running. The agent must handle these new functions properly.
 * 
 * 
 * @author W.Pasman 24jun2014
 * 
 */
public interface PocketNegotiatorAgent {

	/**
	 * initializes the agent, with suggestions for utility space for mySide and
	 * otherSide. When this is called, the agent also knows that it is connected
	 * with PN and not with Genius. <br>
	 * If used from PN, {@link Agent#init()} and
	 * {@link Agent#internalInit(int, int, java.util.Date, Integer, Timeline, UtilitySpace, java.util.HashMap)}
	 * are never called.
	 * 
	 * @param mySide
	 * @param otherSide
	 * @param timeline
	 *            the {@link Timeline} keeping track of where we are in the
	 *            negotiation. We pass it here because the init may already need
	 *            it.
	 */
	void initPN(UtilitySpace mySide, UtilitySpace otherSide, Timeline timeline);

	/**
	 * the agent's opponent did an action. Inform the agent.
	 * 
	 * @param act
	 */
	public void handleAction(Action act);

	/**
	 * ask the agent for its next action.
	 * 
	 * @return next Action
	 */
	public Action getAction();

	/**
	 * Change own and other side utility profile to the given one. Additionally,
	 * this should reset the agent such that its internal state (bid history,
	 * opponent model, etc) matches this new utility space.
	 * 
	 * <br>
	 * 
	 * This one call allows to change both profiles. This is to avoid expensive
	 * useless computations if both spaces need update. If only one needs to be
	 * updated , you can pass null for the other.
	 * 
	 * @param myUtilities
	 *            the new {@link UtilitySpace} for the bot to use as his own
	 *            utility space.
	 * @param opponentUtilities
	 *            the new {@link UtilitySpace} for the bot to use as his
	 *            opponent utility space.
	 */
	void updateProfiles(UtilitySpace myUtilities, UtilitySpace opponentUtilities);

}
