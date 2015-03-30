package negotiator.session;

import negotiator.actions.Action;
import negotiator.parties.NegotiationParty;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

/**
 * Represents a single turn in the negotiation session. {@link Turn} objects are contained in
 * {@link Round} objects which are in their turn contained in a {@link Session} object. A single
 * Turn is executed by a single {@link negotiator.parties.NegotiationParty}. A party is however, allowed to have multiple turns in
 * a single round.
 *
 * @author David Festen
 */
public class Turn
{
    /**
     * Holds a list of action classes which can be executed this turn
     */
    private ArrayList<Class> validActions;

    /**
     * The party which should execute this turn
     */
    private NegotiationParty party;

    /**
     * After the party executed the turn, this holds the action executed
     */
    private Action action;

    /**
     * Initializes a new instance of the turn class. See also the {@link Turn} class itself for more
     * information on usage.
     *
     * @param party The party that should execute this turn
     */
    public Turn(NegotiationParty party)
    {
        this.party = party;
        this.validActions = new ArrayList<Class>();
    }

    /**
     * Initializes a new instance of the turn class. See also the {@link Turn} class itself for more
     * information on usage.
     *
     * @param party       The party that should execute this turn
     * @param validActions Valid {@link Action} classes that can be executed this turn
     */
    public Turn(NegotiationParty party, Class... validActions)
    {
        this.party = party;
        this.validActions = new ArrayList<Class>(Arrays.asList(validActions));
    }

    /**
     * Initializes a new instance of the turn class. See also the {@link Turn} class itself for more
     * information on usage.
     *
     * @param party       The party that should execute this turn
     * @param validActions Valid {@link Action} classes that can be executed this turn
     */
    public Turn(NegotiationParty party, Collection<Class> validActions)
    {
        this.party = party;
        this.validActions = new ArrayList<Class>(validActions);
    }

    /**
     * Get the party which should execute this turn
     *
     * @return the {@link negotiator.parties.NegotiationParty} that should do this turn.
     */
    public NegotiationParty getParty()
    {
        return party;
    }

    /**
     * Add a valid action to this turn
     *
     * @param action The {@link Action} class to add
     */
    public void addValidAction(Class action)
    {
        validActions.add(action);
    }

    /**
     * Remove a valid action from this turn
     *
     * @param action The {@link Action} class to remove
     */
    public void removeValidAction(Class action)
    {
        validActions.remove(action);
    }

    /**
     * Get all valid actions for this turn
     *
     * @return the list of {@link Action} classes valid this turn
     */
    public ArrayList<Class> getValidActions()
    {
        return validActions;
    }

    /**
     * Removes all valid actions from this class. Just make sure to add at least one new one,
     * otherwise the agent behavior is undefined.
     */
    public void clearValidActions()
    {
        validActions.clear();
    }

    /**
     * Gets the action executed this turn
     *
     * @return The executed action or {@code Null} if turn not done yet.
     */
    public Action getAction()
    {
        return action;
    }

    /**
     * Sets the action executed this turn.
     *
     * @param action The action that was executed.
     */
    public void setAction(Action action)
    {
        this.action = action;
    }
}
