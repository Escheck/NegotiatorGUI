package negotiator.session;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import negotiator.actions.Action;
import negotiator.parties.NegotiationPartyInternal;

/**
 * Represents a single turn in the negotiation session. {@link Turn} objects are
 * contained in {@link Round} objects which are in their turn contained in a
 * {@link Session} object. A single Turn is executed by a single
 * {@link negotiator.parties.NegotiationParty}. A party is however, allowed to
 * have multiple turns in a single round.
 *
 * @author David Festen
 * @modified W.Pasman #1067
 */
public class Turn {
	/**
	 * Holds a list of action classes which can be executed this turn
	 */
	private final ArrayList<Class<? extends Action>> validActions;

	/**
	 * The party which should execute this turn
	 */
	private NegotiationPartyInternal party;

	/**
	 * After the party executed the turn, this holds the action executed
	 */
	private Action action;

	/**
	 * Initializes a new instance of the turn class. See also the {@link Turn}
	 * class itself for more information on usage.
	 *
	 * @param party
	 *            The party that should execute this turn
	 */
	public Turn(NegotiationPartyInternal party) {
		this.party = party;
		this.validActions = new ArrayList<Class<? extends Action>>();
	}

	/**
	 * Initializes a new instance of the turn class. See also the {@link Turn}
	 * class itself for more information on usage.
	 *
	 * @param party
	 *            The party that should execute this turn
	 * @param validActions
	 *            Valid {@link Action} classes that can be executed this turn
	 */
	public Turn(NegotiationPartyInternal party,
			Class<? extends Action>... validActions) {
		this.party = party;
		this.validActions = new ArrayList<Class<? extends Action>>(
				Arrays.asList(validActions));
	}

	/**
	 * Initializes a new instance of the turn class. See also the {@link Turn}
	 * class itself for more information on usage.
	 *
	 * @param party
	 *            The party that should execute this turn
	 * @param validActions
	 *            Valid {@link Action} classes that can be executed this turn
	 */
	public Turn(NegotiationPartyInternal party,
			Collection<Class<? extends Action>> validActions) {
		this.party = party;
		this.validActions = new ArrayList<Class<? extends Action>>(validActions);
	}

	/**
	 * Get the party which should execute this turn
	 *
	 * @return the {@link negotiator.parties.NegotiationParty} that should do
	 *         this turn.
	 */
	public NegotiationPartyInternal getParty() {
		return party;
	}

	/**
	 * Get (copy of) all valid actions for this turn.
	 *
	 * @return the list of {@link Action} classes valid this turn
	 */
	public ArrayList<Class<? extends Action>> getValidActions() {
		return new ArrayList<Class<? extends Action>>(validActions);
	}

	/**
	 * Gets the action executed this turn
	 *
	 * @return The executed action or {@code Null} if turn not done yet.
	 */
	public Action getAction() {
		return action;
	}

	/**
	 * Sets the action executed this turn.
	 *
	 * @param action
	 *            The action that was executed.
	 */
	public void setAction(Action action) {
		this.action = action;
	}
}