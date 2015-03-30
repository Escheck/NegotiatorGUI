package negotiator.session;

import negotiator.actions.Action;

import java.util.ArrayList;

/**
 * Represents a single round in a negotiation session.
 * A {@link Round} consists of {@link Turn} which may or may not have an {@link Action}.  Rounds are
 * contained in the {@link Session} object.
 *
 * @author David Festen
 */
public class Round
{
    /**
     * holds the {@link Turn} objects of this round
     */
    private ArrayList<Turn> turns;

    /**
     * Creates a new instance of the {@link Round} object.
     */
    public Round()
    {
        turns = new ArrayList<Turn>();
    }

    /**
     * Creates a new instance of the {@link Round} object.
     * This version of the constructor creates a shallow copy of the turns.
     *
     * @param round An existing round object.
     */
    public Round(Round round)
    {
        turns = round.getTurns();
    }

    /**
     * Gets the turns in this round. See the {@link Turn} object for more information.
     *
     * @return the {@link Turn} objects in this round
     */
    public ArrayList<Turn> getTurns()
    {
        return turns;
    }

    /**
     * Gets the actions in done in this round. If a turn is not executed, it shouldn't have an
     * action. This means that in practice, you can use this method if you want to know the executed
     * actions of this turn, even while it is still busy.
     *
     * @return A list of all actions done this turn.
     */
    public ArrayList<Action> getActions()
    {
        // collect all turns
        ArrayList<Turn> turns = getTurns();

        // extract the actions
        ArrayList<Action> actions = new ArrayList<Action>(turns.size());
        for (Turn turn : turns)
            if (turn.getAction() != null)
                actions.add(turn.getAction());

        // return the actions
        return actions;
    }

    /**
     * Create a shallow copy of the Round.
     * Using the {@link #Round(Round)} constructor.
     *
     * @return A copy of this round object.
     */
    public Round copy()
    {
        return new Round(this);
    }

    /**
     * Add a turn to this round. See {@link Turn} for more information.
     *
     * @param turn the turn to add
     */
    public void addTurn(Turn turn)
    {
        turns.add(turn);
    }

    /**
     * get the last item of the {@link #getActions()} list, which in practice should be the most
     * recent action of this round.
     *
     * @return The most recently executed action in this round.
     */
    public Action getMostRecentAction()
    {
        ArrayList<Action> actions = getActions();
        return actions.get(actions.size() - 1);
    }
}
