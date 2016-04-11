package agents.anac.y2016.caduceus.agents.kawaii;

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
public class kawaii extends AbstractNegotiationParty {

	agents.anac.y2015.fairy.kawaii realkawaii = new agents.anac.y2015.fairy.kawaii();

	@Override
	public void init(AbstractUtilitySpace utilSpace, Deadline dl,
			TimeLineInfo tl, long randomSeed, AgentID agentId) {
		super.init(utilSpace, dl, tl, randomSeed, agentId);

		realkawaii.init(utilSpace, dl, tl, randomSeed, agentId);

	}

	@Override
	public Action chooseAction(List<Class<? extends Action>> list) {
		return realkawaii.chooseAction(list);

	}

	@Override
	public void receiveMessage(AgentID sender, Action arguments) {
		realkawaii.receiveMessage(sender, arguments);
	}
}
