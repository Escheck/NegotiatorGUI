package agents.nastyagent;

import java.util.List;

import negotiator.actions.Action;

/**
 * returns a null action
 * 
 * @author W.Pasman
 *
 */
public class NullActionInChoose extends NastyAgent {

	@Override
	public Action chooseAction(List<Class> possibleActions) {
		return null;
	}
}
