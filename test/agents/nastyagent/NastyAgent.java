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
import negotiator.session.Timeline;
import negotiator.utility.UtilitySpace;
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
	private UtilitySpace utilitySpace;
	private Timeline timeline;
	private Deadline deadlines;
	private AgentID partyId;

	@Override
	public void init(UtilitySpace utilitySpace, Deadline deadlines,
			Timeline timeline, long randomSeed) {
		this.utilitySpace = utilitySpace;
		this.timeline = timeline;
		this.deadlines = deadlines;

		BidIterator biter = new BidIterator(utilitySpace.getDomain());
		while (biter.hasNext())
			bids.add(biter.next());
		Collections.sort(bids, new BidComparator(utilitySpace));
		bidIterator = bids.iterator();

	}

	@Override
	public Action chooseAction(List<Class> possibleActions) {
		if (bidIterator.hasNext()) {
			return new Offer(bidIterator.next());
		}
		return null;
	}

	@Override
	public void receiveMessage(Object sender, Action arguments) {
	}

	@Override
	public double getUtility(Bid bid) {
		if (bid == null) {
			// utility is null if no bid
			return 0;
		} else if (timeline == null) {
			// return undiscounted utility if no timeline given
			return getUtility(bid);
		} else {
			// otherwise, return discounted utility
			return utilitySpace.getUtilityWithDiscount(bid, timeline);
		}
	}

	@Override
	public double getUtilityWithDiscount(Bid bid) {
		return utilitySpace.getUtilityWithDiscount(bid, timeline);
	}

	@Override
	public UtilitySpace getUtilitySpace() {
		return utilitySpace;
	}

	@Override
	public Timeline getTimeLine() {
		return timeline;
	}

	@Override
	public void setTimeLine(Timeline timeline) {
		this.timeline = timeline;

	}

	@Override
	public AgentID getPartyId() {
		return partyId == null ? new AgentID("" + getClass() + "@" + hashCode())
				: partyId;
	}

}
