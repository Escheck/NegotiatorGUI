package parties.in4010.q12015.group13;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;

import negotiator.AgentID;
import negotiator.Bid;
import negotiator.Deadline;
import negotiator.actions.Accept;
import negotiator.actions.Action;
import negotiator.actions.Offer;
import negotiator.issue.Issue;
import negotiator.issue.Value;
import negotiator.parties.AbstractNegotiationParty;
import negotiator.session.TimeLineInfo;
import negotiator.utility.AdditiveUtilitySpace;

/**
 * Multipary negotiator which tries to find bids that the opponent would accept,
 * but will still satisfy it's own utility requirements
 * 
 */
public class USBNAT extends AbstractNegotiationParty {

	HashMap<Object, FrequencyOpponentModel> opponents = new HashMap();
	HashMap<Object, ArrayList<Bid>> accepts = new HashMap();
	HashMap<Object, LinkedList<Bid>> rejects = new HashMap();
	Bid lastBid = null;
	double n = 0.1; // n for the frequency model
	ArrayList<Bid> allBids = null; // all possible bids (sorted on our utility)

	private double absoluteMinimum = 1;
	private final double tries = 10;// Sine periods
	private final double momentum = 0.05;
	private final double start = 0.6; // When we start negotiating
	private final int rejectsSize = 10;

	private int rounds = 0; // current round
	private boolean even = false; // used for giving max bids per 2 rounds

	private final int panic = 5; // How many rounds left till we start to panic

	@Override
	public void init(AdditiveUtilitySpace utilSpace, Deadline dl, TimeLineInfo tl,
			long randomSeed, AgentID agentId) {
		super.init(utilSpace, dl, tl, randomSeed, agentId);

		absoluteMinimum = Math.max(0.05,
				utilitySpace.getReservationValueUndiscounted());

		allBids = generateAllBids();
	}

	/**
	 * Utility function Combination of a line from 1 to absolute minimum and a
	 * sine
	 * 
	 * @param t
	 *            current time (from 0 to 1)
	 * @return The minimum utility we should go for at this time (ignoring other
	 *         agents)
	 */
	private double getMinUtility(double t) {
		double sin = Math.sin(tries * 2 * Math.PI * t + 1.5 * Math.PI);
		double half = (1 - absoluteMinimum) / 2 + absoluteMinimum;
		double dist = 1 - half;
		return 0.7 * (1 - (1 - absoluteMinimum) * t) + 0.3
				* (half + dist * sin);
	}

	/**
	 * @return For each opponent, the minimum utility we expect he would accept
	 */
	private HashMap<Object, Double> getMinUtils() {
		HashMap<Object, Double> ret = new HashMap(accepts.size());

		for (Entry<Object, ArrayList<Bid>> entry : accepts.entrySet()) {
			ArrayList<Bid> acc = entry.getValue();
			FrequencyOpponentModel model = opponents.get(entry.getKey());
			double min = 1;

			for (Bid b : acc) {
				double util = model.estimateUtility(b);

				if (util < min) {
					min = util;
				}
			}

			double maxRejected = 0;

			for (Bid rejected : rejects.get(entry.getKey())) {
				double util = model.estimateUtility(rejected);

				if (util > maxRejected) {
					maxRejected = util;
				}
			}

			ret.put(entry.getKey(), Math.max(min, maxRejected + momentum));
		}

		return ret;
	}

	/**
	 * 
	 * @param b
	 *            a bid
	 * @param minUtils
	 *            a hashmap generated by getMinUtils
	 * @return true iff we expect every other agent would accept this bid
	 */
	private boolean isAcceptable(Bid b, HashMap<Object, Double> minUtils) {
		for (Entry<Object, Double> entry : minUtils.entrySet()) {
			if (opponents.get(entry.getKey()).estimateUtility(b) < entry
					.getValue()) {
				return false;
			}
		}

		return true;
	}

	/**
	 * Generates the Best Acceptable Bid
	 * 
	 * @param minUtils
	 *            generated by getMinUtils
	 * @param myMin
	 *            own minimum utility
	 * @return The bid that maximizes our utility, but still would get accepted
	 *         by the other agents and has an utility (for us) of more than
	 *         myMin. Null if this bid does not exist.
	 */
	private Bid generateBAB(HashMap<Object, Double> minUtils, double myMin) {
		Iterator<Bid> it = allBids.iterator();

		while (it.hasNext()) {
			Bid b = it.next();

			if (!rejected(b)) {
				if (myMin > getUtility(b)) {
					return null;
				}

				if (isAcceptable(b, minUtils)) {
					return b;
				}
			}
		}

		return null;
	}

	// Max Bid over Minimum
	/**
	 * Generated the Maximum Bid over Minimum
	 * 
	 * @param minUtility
	 *            our minimum utility
	 * @return A bid that maximizes the (minimum) utility of the other agents
	 *         but still statisfies out minimum utility
	 */
	private Bid generateMBM(double minUtility) {
		double max = 0;
		Bid bestBid = allBids.get(0);

		for (Bid b : allBids) {
			if (getUtility(b) >= minUtility && !rejected(b)) {
				double min = 1;

				for (FrequencyOpponentModel model : opponents.values()) {
					double util = model.estimateUtility(b);

					if (util < min) {
						min = util;
					}
				}

				if (bestBid == null || max < min) {
					max = min;
					bestBid = b;
				}
			}
		}

		return bestBid;
	}

	/**
	 * Generate a bid using the info about our opponents
	 * 
	 * @return the bid
	 */
	private Bid generateBidJ() {
		double time = getTimeLine().getTime();

		if (time < start) {
			return allBids.get(0);
		}

		HashMap<Object, Double> minUtils = getMinUtils();
		double max = 0;

		for (Double util : minUtils.values()) {
			if (util > max) {
				max = util;
			}
		}

		double minUtility = Math.max(max - momentum,
				getMinUtility((time - start) / (1 - start)));

		Bid b = generateBAB(minUtils, minUtility);

		if (b != null) {
			return b;
		} else {
			return generateMBM(minUtility);
		}
	}

	/**
	 * Find maximum utility Nash Equilibrium
	 * 
	 * @return nash bid
	 */
	public Bid getNash() {
		double nash = -1;
		Bid ret = null;
		for (Bid option : allBids) {
			double nashv = getUtility(option);
			for (Entry<Object, FrequencyOpponentModel> entry : opponents
					.entrySet()) {
				nashv = nashv * entry.getValue().estimateUtility(option);
			}
			if (nashv > nash) {
				nash = nashv;
				ret = option;
			}

		}
		return ret;
	}

	/**
	 * @return maximum utility bid that everyone else has already accepted
	 */
	private Bid findMaxAccepted() {
		double max = 0;
		Bid ret = null;

		for (Bid b : allBids) {
			if (!rejected(b)) {
				double util = getUtility(b);

				if (util > max) {
					max = util;
					ret = b;
				}
			}
		}

		return ret;
	}

	/**
	 * @return our "panic" bid
	 */
	private Bid findPanicBid() {
		Bid max = findMaxAccepted();
		Bid nash = getNash();

		if (max == null || getUtility(nash) >= getUtility(max)) {
			max = nash;
		}

		return max;
	}

	private ArrayList<Bid> generateAllBids() {
		ArrayList<Issue> issues = utilitySpace.getDomain().getIssues();

		ArrayList<Bid> ret = new ArrayList();

		for (HashMap<Integer, Value> values : getAllBids(issues, 0)) {
			try {
				Bid bid = new Bid(utilitySpace.getDomain(), values);
				ret.add(bid);
			} catch (Exception ex) {
				System.err.println("Could not create bid");
				System.err.println(ex.getMessage());
			}
		}

		Collections.sort(ret, new Comparator<Bid>() {
			@Override
			public int compare(Bid a, Bid b) {
				if (getUtility(b) > getUtility(a)) {
					return 1;
				} else if (getUtility(b) == getUtility(a)) {
					return 0;
				} else {
					return -1;
				}
			}

		});

		return ret;
	}

	private static ArrayList<HashMap<Integer, Value>> getAllBids(
			ArrayList<Issue> issues, int from) {
		Issue issue = issues.get(from);

		ArrayList<HashMap<Integer, Value>> bids;

		if (from == issues.size() - 1) {
			bids = new ArrayList();
			bids.add(new HashMap());
		} else {
			bids = getAllBids(issues, from + 1);
		}

		ArrayList<Value> values = Util.getValues(issue);

		ArrayList<HashMap<Integer, Value>> ret = new ArrayList();

		for (Value v : values) {
			for (HashMap<Integer, Value> bid : bids) {
				HashMap<Integer, Value> newBid = new HashMap(bid);
				newBid.put(issue.getNumber(), v);
				ret.add(newBid);
			}
		}

		return ret;
	}

	@Override
	public Action chooseAction(List<Class<? extends Action>> list) {
		try {
			rounds++;

			// Panic Mode (try to ignore very long first rounds by checking if
			// we are already after start)
			double roundsLeft = Util.estimatedRoundsLeft(getTimeLine(), rounds);
			if (getTimeLine().getTime() >= start && roundsLeft <= panic) {
				if (roundsLeft <= 2 && getUtility(lastBid) > absoluteMinimum) {
					return new Accept();
				} else {
					Bid b = findPanicBid();
					if (getUtility(b) <= getUtility(lastBid)
							&& getUtility(lastBid) > absoluteMinimum) {
						return new Accept();
					} else if (getUtility(b) > absoluteMinimum) {
						return new Offer(b);
					}
				}
			}

			// Normal mode
			even = !even;
			Bid b = generateBidJ();
			if (getUtility(b) > getUtility(lastBid)) {
				b = even ? b : allBids.get(0);
				lastBid = b;
				return new Offer(b);
			} else {
				return new Accept();
			}
		} catch (Exception ex) {
			System.err.println("Exception in chooseAction: " + ex.getMessage());
			return new Accept();
		}
	}

	@Override
	public void receiveMessage(AgentID sender, Action action) {
		try {
			super.receiveMessage(sender, action);

			if (sender == null) {
				return;
			}

			if (!opponents.containsKey(sender)) {
				opponents.put(sender, new BetterFOM(getUtilitySpace()
						.getDomain(), n));
				accepts.put(sender, new ArrayList<Bid>());
				rejects.put(sender, new LinkedList<Bid>());
			}

			if (action instanceof Offer) {
				if (lastBid != null) {
					addReject(sender, lastBid);
				}
				lastBid = ((Offer) action).getBid();
				FrequencyOpponentModel OM = opponents.get(sender);
				OM.addBid(lastBid);

				accepts.get(sender).add(lastBid);
			} else if (action instanceof Accept) {
				accepts.get(sender).add(lastBid);
			}

		} catch (Exception ex) {
			System.err.println("Exception in receiveMessage: "
					+ ex.getMessage());
		}
	}

	public void addReject(Object sender, Bid b) {
		// Don't fill rejects with our maximum bid
		if (b.equals(allBids.get(0))) {
			return;
		}

		LinkedList<Bid> list = rejects.get(sender);

		list.addLast(b);

		if (list.size() > rejectsSize) {
			list.removeFirst();
		}
	}

	public boolean rejected(Bid b) {
		boolean rejected = false;
		Iterator<LinkedList<Bid>> it = rejects.values().iterator();

		while (it.hasNext() && !rejected) {
			rejected = it.next().contains(b);
		}

		return rejected;
	}
}
