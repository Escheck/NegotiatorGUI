package agents.anac.y2016.Caduceus.agents.RandomDance;

import java.util.List;

import negotiator.AgentID;
import negotiator.Deadline;
import negotiator.actions.Action;
import negotiator.parties.AbstractNegotiationParty;
import negotiator.session.TimeLineInfo;
import negotiator.utility.AbstractUtilitySpace;

/**
 * Created by tdgunes on 30/03/16.
 */
public class RandomDance extends AbstractNegotiationParty {

	agents.anac.y2015.RandomDance.RandomDance realRandomDance = new agents.anac.y2015.RandomDance.RandomDance();

	@Override
	public void init(AbstractUtilitySpace utilSpace, Deadline dl,
			TimeLineInfo tl, long randomSeed, AgentID agentId) {
		super.init(utilSpace, dl, tl, randomSeed, agentId);

		realRandomDance.init(utilSpace, dl, tl, randomSeed, agentId);

	}

	@Override
	public Action chooseAction(List<Class<? extends Action>> list) {
		return realRandomDance.chooseAction(list);

	}

	@Override
	public void receiveMessage(AgentID sender, Action arguments) {
		realRandomDance.receiveMessage(sender, arguments);
	}
}
