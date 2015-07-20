package agents;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

import negotiator.Agent;
import negotiator.Bid;
import negotiator.BidIterator;
import negotiator.actions.Action;
import negotiator.actions.Offer;

/**
 * NastyAgent is an agent with nasty behaviour: throws throwables, returns silly
 * actions, goes to sleep for long times. This is for testing if Genius is
 * robust for such cases. If not nasty, this agent just places bids ordered by
 * decreasing utility.
 * 
 * @author W.Pasman.
 * 
 */
public class NastyAgent extends Agent {

	ArrayList<Bid> bids = new ArrayList<Bid>(); // the bids that we MAY place.
	Iterator<Bid> bidIterator; // next bid that we can place. Iterator over
								// bids.

	/**
	 * Timing of the nasty actions. Just add these as keys. The values have to
	 * be the number.
	 */
	public enum Timing {
		/** When init is called. Parameter is ignored. */
		WHENINIT,
		/** when received Nth opponent action. 1 is first receive. */
		WHENRECEIVE,
		/** when we have to choose our Nth action. 1 is first action. */
		WHENCHOOSE
	};

	/**
	 * Nasty actions for the agent. This action is executed when the
	 * {@link Timing} triggers.
	 *
	 */
	public enum Act {
		/**
		 * Throw an exception as specified in the parameter. The parameter must
		 * be full.path.to.the.class. No parameters can be used for the
		 * constructor.
		 */
		THROW,
		/**
		 * sleep time (seconds) as specified in parameter.
		 */
		SLEEP,
		/**
		 * return an object as specified in the parameter.
		 */
		RETURN, SYSEXIT
	};

	@Override
	public void init() {
		BidIterator biter = new BidIterator(utilitySpace.getDomain());
		while (biter.hasNext())
			bids.add(biter.next());
		Collections.sort(bids, new BidComparator(utilitySpace));
		bidIterator = bids.iterator();
	}

	@Override
	public void ReceiveMessage(Action opponentAction) {
		System.out.println(strategyParameters);
	}

	@Override
	public Action chooseAction() {
		if (bidIterator.hasNext()) {
			return new Offer(bidIterator.next());
		}
		return null;
	}

}
