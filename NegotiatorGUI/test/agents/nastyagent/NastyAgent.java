package agents.nastyagent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import negotiator.Agent;
import negotiator.AgentID;
import negotiator.Bid;
import negotiator.BidIterator;
import negotiator.Deadline;
import negotiator.actions.Action;
import negotiator.actions.Offer;
import negotiator.parties.NegotiationParty;
import negotiator.protocol.MultilateralProtocolAdapter;
import negotiator.protocol.StackedAlternatingOffersProtocol;
import negotiator.session.TimeLineInfo;
import negotiator.utility.AdditiveUtilitySpace;
import agents.BidComparator;

/**
 * NastyAgent is an agent with nasty behaviour: throws, returns silly actions,
 * goes to sleep for long times. This is for testing if Genius is robust for
 * such cases. If not nasty, this agent just places bids ordered by decreasing
 * utility.
 * <p>
 * This is an abstract class without any actual nastyness. This is because
 * {@link NegotiationParty} does not support parameterization anymore (unlike
 * {@link Agent}. The actual (non-abstract) implementations do the nastyness.
 * 
 * @author W.Pasman.
 * 
 */
public abstract class NastyAgent implements NegotiationParty {

	ArrayList<Bid> bids = new ArrayList<Bid>(); // the bids that we MAY place.
	Iterator<Bid> bidIterator; // next bid that we can place. Iterator over
								// bids.

	@Override
	public void init(AdditiveUtilitySpace utilitySpace, Deadline deadlines,
			TimeLineInfo timeline, long randomSeed, AgentID id) {

		BidIterator biter = new BidIterator(utilitySpace.getDomain());
		while (biter.hasNext())
			bids.add(biter.next());
		Collections.sort(bids, new BidComparator(utilitySpace));
		bidIterator = bids.iterator();

	}

	@Override
	public Action chooseAction(List<Class<? extends Action>> possibleActions) {
		if (bidIterator.hasNext()) {
			return new Offer(bidIterator.next());
		}
		return null;
	}

	@Override
	public void receiveMessage(AgentID sender, Action arguments) {
	}

	@Override
	public String getDescription() {
		return this.getClass().getSimpleName();
	}

	@Override
	public Class<? extends MultilateralProtocolAdapter> getProtocol() {
		return StackedAlternatingOffersProtocol.class;
	}

}
