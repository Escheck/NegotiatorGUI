package agents.nastyagent;

import java.util.List;

import negotiator.Deadline;
import negotiator.actions.Action;
import negotiator.session.Timeline;
import negotiator.utility.UtilitySpace;

/**
 * returns a null action
 * 
 * @author W.Pasman
 *
 */
public class NullActionInChoose extends NastyAgent {

	public NullActionInChoose(UtilitySpace utilitySpace, Deadline deadlines,
			Timeline timeline, long randomSeed) {
		super(utilitySpace, deadlines, timeline, randomSeed);
	}

	@Override
	public Action chooseAction(List<Class> possibleActions) {
		return null;
	}
}
