package negotiator.parties;

import negotiator.AgentID;
import negotiator.Deadline;
import negotiator.Timeline;
import negotiator.utility.UtilitySpace;

/**
 * Base class for all mediator parties.
 *
 * @author David Festen
 */
public abstract class Mediator extends AbstractNegotiationParty {

	/**
	 * Initializes a new instance of the {@link Mediator} class.
	 *
	 * @param utilitySpace
	 *            The utility space used by this class
	 * @param deadlines
	 *            The deadlines for this session
	 * @param timeline
	 *            The time line (if time deadline) for this session, can be null
	 * @param randomSeed
	 *            The seed that should be used for all randomization (to be
	 *            reproducible)
	 */
	public Mediator(UtilitySpace utilitySpace, Deadline deadlines,
			Timeline timeline, long randomSeed) {
		super(utilitySpace, deadlines, timeline, randomSeed);
	}

	/**
	 * Gets the agent's unique id
	 * <p/>
	 * Each agent should contain a (unique) id. This id is used in log files to
	 * trace the agent's behavior. For all default implementations, this has
	 * either the format "ClassName" if only one such an agent exists (in case
	 * of mediator for example) or it has the format "ClassName@HashCode" if
	 * multiple agents of the same type can exists. You could also use the
	 * random hash used in the agent to identify it (making it easier to
	 * reproduce results).
	 *
	 * @return A uniquely identifying agent id
	 */
	@Override
	public AgentID getPartyId() {
		return new AgentID("Mediator");
	}
}
