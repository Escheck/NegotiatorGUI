package agents.anac.y2016.caduceus.agents.ParsAgent;

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
public class ParsAgent extends AbstractNegotiationParty {

	agents.anac.y2015.ParsAgent.ParsAgent realParsAgent = new agents.anac.y2015.ParsAgent.ParsAgent();

	@Override
	public void init(AbstractUtilitySpace utilSpace, Deadline dl,
			TimeLineInfo tl, long randomSeed, AgentID agentId) {
		super.init(utilSpace, dl, tl, randomSeed, agentId);

		realParsAgent.init(utilSpace, dl, tl, randomSeed, agentId);

	}

	@Override
	public Action chooseAction(List<Class<? extends Action>> list) {
		return realParsAgent.chooseAction(list);

	}

	@Override
	public void receiveMessage(AgentID sender, Action arguments) {
		realParsAgent.receiveMessage(sender, arguments);
	}
}
