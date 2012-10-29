package negotiator;

/**
 * Special type of agent which is aware of rounds. In a tournament it can happen that a
 * specific session is repeated multiple times. In this case, a normal agent is unaware
 * and is simply reset each new session round. However, a MultiRoundAgent is aware of this
 * fact and can therefore can use information learned in a previous negotiation session round.
 * 
 * Basically, init is only called once and each new session round beginSession is called again.
 * If a session is repeated two times the sequency becomes: init, beginSessionRound, endSessionRound,
 * beginSessionRound, endSessionRound.
 *
 * @author Mark Hendrikx
 */
public abstract class MultiRoundAgent extends Agent {
	/**
     * In contrast to a normal agent, can remember information during rounds.
     * @return true
     */
    public boolean isMultiRoundsCompatible() { return true; }
    
    /**
     * Method which is called at the beginning of a round of a session.
     */
    public void beginSessionRound() { }
}
