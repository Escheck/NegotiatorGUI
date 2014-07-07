package negotiator;

import negotiator.actions.Action;
import negotiator.utility.UtilitySpace;

/**
 * #915 Agents of this type are compatible with the PocketNegotiator.
 * 
 * Agents in PocketNegotiator run on the server, in parallel with other threads
 * that may run the same agent. To be compatible, agents need to be thread safe
 * and run efficient in multi-threading situations. Also, they should not
 * attempt to do actions that violate security restrictions in a Tomcat
 * HttpServlet.
 * 
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
	 * with PN and not with Genius.
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
	 * Change own utility profile to the given one. Additionally, this should
	 * reset the agent such that its internal state (bid history, opponent
	 * model, etc) matches this new utility space.
	 * 
	 * @param us
	 *            the new {@link UtilitySpace}.
	 */
	void updateMyProfile(UtilitySpace us);

	/**
	 * Changes the opponent profile of this Agent to the given one. This makes
	 * only sense if this agent actively tries to model the opponent. This will
	 * be called only in the case where the user actively models the opponent as
	 * can be done with the PocketNegotiator.
	 * 
	 * @param us
	 *            the new opponent utility space.
	 */
	void updateOpponentProfile(UtilitySpace us);

}
