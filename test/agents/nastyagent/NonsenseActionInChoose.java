package agents.nastyagent;

import java.util.List;

import negotiator.actions.Action;

/**
 * returns a nonsense action
 * 
 * @author W.Pasman 20jul15
 *
 */
public class NonsenseActionInChoose extends NastyAgent {
	@Override
	public Action chooseAction(List<Class<? extends Action>> possibleActions) {
		return new NonsenseAction();
	}
}

class NonsenseAction extends Action {
	@Override
	public String toString() {
		return null;
	}

}
