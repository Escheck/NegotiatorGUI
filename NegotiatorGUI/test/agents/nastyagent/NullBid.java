package agents.nastyagent;

import java.util.List;

import negotiator.actions.Action;
import negotiator.actions.Offer;

/**
 * returns a deliberately miscrafted bid that contains an integer value where a
 * discrete is expected, or the other way round. The idea is that the opponent
 * may call getUtility on it and then cause a class cast exception.
 * 
 * @author W.Pasman 2nov15
 *
 */
public class NullBid extends NastyAgent {
	@Override
	public Action chooseAction(List<Class<? extends Action>> possibleActions) {
		return new Offer(null);
	}
}
