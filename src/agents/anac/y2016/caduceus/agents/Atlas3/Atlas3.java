package agents.anac.y2016.caduceus.agents.Atlas3;

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
public class Atlas3 extends AbstractNegotiationParty {

	agents.anac.y2015.Atlas3.Atlas3 realAtlas = new agents.anac.y2015.Atlas3.Atlas3();

	@Override
	public void init(AbstractUtilitySpace utilSpace, Deadline dl,
			TimeLineInfo tl, long randomSeed, AgentID agentId) {
		super.init(utilSpace, dl, tl, randomSeed, agentId);

		realAtlas.init(utilSpace, dl, tl, randomSeed, agentId);

	}

	@Override
	public Action chooseAction(List<Class<? extends Action>> list) {
		return realAtlas.chooseAction(list);

	}

	@Override
	public void receiveMessage(AgentID sender, Action arguments) {
		realAtlas.receiveMessage(sender, arguments);
	}
}
