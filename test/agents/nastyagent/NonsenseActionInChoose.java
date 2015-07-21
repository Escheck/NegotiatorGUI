package agents.nastyagent;

import java.util.List;

import negotiator.Deadline;
import negotiator.actions.Action;
import negotiator.session.Timeline;
import negotiator.utility.UtilitySpace;

/**
 * returns a nonsense action
 * 
 * @author W.Pasman 20jul15
 *
 */
public class NonsenseActionInChoose extends NastyAgent {

	public NonsenseActionInChoose(UtilitySpace utilitySpace,
			Deadline deadlines, Timeline timeline, long randomSeed) {
		super(utilitySpace, deadlines, timeline, randomSeed);
	}

	@Override
	public Action chooseAction(List<Class> possibleActions) {
		return new NonsenseAction();
	}
}

class NonsenseAction extends Action {

	@Override
	public String toString() {
		return null;
	}

}
