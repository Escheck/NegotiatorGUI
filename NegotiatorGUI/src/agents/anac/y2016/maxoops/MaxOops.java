/*
 * Author: Max W. Y. Lam (Aug 1 2015)
 * Version: Milestone 1
 * 
 * */

package agents.anac.y2016.maxoops;

import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.PriorityQueue;

import negotiator.AgentID;
import negotiator.Bid;
import negotiator.BidHistory;
import negotiator.BidIterator;
import negotiator.Deadline;
import negotiator.Domain;
import negotiator.actions.Accept;
import negotiator.actions.Action;
import negotiator.actions.EndNegotiation;
import negotiator.actions.Inform;
import negotiator.actions.NoAction;
import negotiator.actions.Offer;
import negotiator.bidding.BidDetails;
import negotiator.parties.AbstractNegotiationParty;
import negotiator.session.TimeLineInfo;
import negotiator.utility.AbstractUtilitySpace;

public class MaxOops extends AbstractNegotiationParty {

	public boolean DEBUG = false;

	// Debug
	public static PrintStream log1, log2, log3, error;
	public boolean inited = false;

	// Domain Information
	public Domain domain;
	public double delta, theta;
	public int numIssues, numBids, turn;
	public double maxUtil, minUtil, secMaxUtil;
	public double meanUtil, stdUtil, medianUtil, uqUtil, lqUtil;
	public Bid minBid, maxBid, secMaxBid, lastBid;
	public Action myLastAction, prevLastAction;
	public BidHistory myBidHistory;
	public BidHistory allBidHistory;
	public ArrayList<HashSet<Bid>> hashBids;

	// Opponents Information
	public int currentPartyID, numParties;
	public int opponentsMinNumDistinctAccepts, opponentsMaxNumDistinctAccepts;
	public int opponentsMinNumDistinctBids, opponentsMaxNumDistinctBids;
	public Action[] opponentsLastActions;
	public BidHistory[] opponentsBidHistories;
	public BidHistory[] opponentsDistinctBids;
	public BidHistory[] opponentsDistinctAccepts;

	// User-defined Information
	public AgentParameters params;
	public TFComponent TFC;
	public DMComponent DMC;

	public MaxOops() throws Exception {
		super();
	}

	public void init(AbstractUtilitySpace utilitySpace, Deadline deadlines,
			TimeLineInfo timeline, long randomSeed, AgentID id) {
		super.init(utilitySpace, deadlines, timeline, randomSeed, id);
		try {
			init();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void init() throws Exception {
		// Debug
		if (DEBUG) {
			String dir = System.getProperty("user.dir");
			log1 = new PrintStream(new FileOutputStream(dir + "/log1.txt",
					false));
			log2 = new PrintStream(new FileOutputStream(dir + "/log2.txt",
					false));
			log3 = new PrintStream(new FileOutputStream(dir + "/log3.txt",
					false));
			error = new PrintStream(new FileOutputStream(dir + "/error.txt",
					false));
			System.setErr(error);
		} else {
			log1 = log2 = log3 = System.out;
		}

		// Initialize Domain Information
		inited = true;
		opponentsMinNumDistinctAccepts = 0;
		opponentsMaxNumDistinctAccepts = 0;
		opponentsMinNumDistinctBids = 0;
		opponentsMaxNumDistinctBids = 0;
		myLastAction = null;
		prevLastAction = null;
		myBidHistory = new BidHistory();
		allBidHistory = new BidHistory();
		domain = utilitySpace.getDomain();
		numBids = (int) domain.getNumberOfPossibleBids();
		delta = utilitySpace.getDiscountFactor();
		theta = utilitySpace.getReservationValueUndiscounted();
		numIssues = utilitySpace.getMinUtilityBid().getIssues().size();
		minBid = utilitySpace.getMinUtilityBid();
		maxBid = utilitySpace.getMaxUtilityBid();
		lastBid = minBid;
		lqUtil = minUtil * 0.75 + maxUtil * 0.25;
		uqUtil = minUtil * 0.25 + maxUtil * 0.75;
		meanUtil = (minUtil + maxUtil) / 2.;
		minUtil = utilitySpace.getUtility(minBid);
		maxUtil = utilitySpace.getUtility(maxBid);
		secMaxUtil = minUtil;
		hashBids = new ArrayList<HashSet<Bid>>();
		for (int i = 0; i <= 100; i++) {
			hashBids.add(new HashSet<Bid>());
		}
		PriorityQueue<Double> sortedUtils = new PriorityQueue<Double>();
		int i = 0;
		BidIterator bidIterator = new BidIterator(domain);
		while (bidIterator.hasNext()) {
			Bid b = bidIterator.next();
			double util = utilitySpace.getUtility(b);
			int bidInd = (int) (util * 100);
			hashBids.get(bidInd).add(b);
			if (util > secMaxUtil && util < maxUtil) {
				secMaxUtil = util;
				secMaxBid = b;
			}
			sortedUtils.add(util);
			meanUtil += (util - meanUtil) / (i + 1);
			stdUtil = (i * stdUtil * stdUtil) / (i + 1);
			stdUtil = Math.sqrt(stdUtil + Math.pow(util - meanUtil, 2)
					/ (i + 1));
			i++;
		}
		while (sortedUtils.size() > (numBids * 3) / 4) {
			lqUtil = sortedUtils.remove();
		}
		while (sortedUtils.size() > numBids / 2) {
			medianUtil = sortedUtils.remove();
		}
		while (sortedUtils.size() > numBids / 4) {
			uqUtil = sortedUtils.remove();
		}
	}

	public long getCurrentTurn() {
		return this.turn;
	}

	public long getCurrentRound() {
		return this.turn / this.numParties;
	}

	public int getCurrentOpponent() {
		return this.getCurrentParty() - 1;
	}

	public int getCurrentParty() {
		return (int) (this.turn % this.numParties);
	}

	@Override
	public AgentID getPartyId() {
		return new AgentID("MaxOops");
	}

	@Override
	public String getDescription() {
		return "MaxOops";
	}

	@Override
	public void receiveMessage(AgentID sender, Action action) {
		super.receiveMessage(sender, action);
		if (sender == null) {
			turn = 0;
			Inform inform = (Inform) action;
			numParties = ((Integer) inform.getValue()).intValue();
			opponentsLastActions = new Action[numParties - 1];
			opponentsBidHistories = new BidHistory[numParties - 1];
			opponentsDistinctBids = new BidHistory[numParties - 1];
			opponentsDistinctAccepts = new BidHistory[numParties - 1];
			for (int i = 0; i < numParties - 1; i++) {
				opponentsLastActions[i] = null;
				opponentsBidHistories[i] = new BidHistory();
				opponentsDistinctBids[i] = new BidHistory();
				opponentsDistinctAccepts[i] = new BidHistory();
			}
			params = new AgentParameters();
			TFC = new TFComponent(this, timeline);
			DMC = new DMComponent(this, utilitySpace, timeline);
			return;
		}
		prevLastAction = action;
		int opponent = getCurrentOpponent();
		if (opponent < 0) {
			opponent += numParties - 1;
		}
		double time = timeline.getTime();
		opponentsLastActions[opponent] = action;
		if (action instanceof Offer) {
			Offer offer = (Offer) (action);
			Bid bid = offer.getBid();
			lastBid = bid;
			double util = utilitySpace.getUtility(bid);
			if (opponentsBidHistories[opponent].isEmpty()) {
				DMC.bidsOpt.payoffs[opponent].initWeights(bid);
			} else {
				DMC.bidsOpt.payoffs[opponent].updateWeights(bid);
			}
			opponentsBidHistories[opponent]
					.add(new BidDetails(bid, util, time));
			allBidHistory.add(new BidDetails(bid, util, time));
			TFC.recordUtility(util, opponent);
			if (opponentsDistinctBids[opponent].filterUtility(util).isEmpty()) {
				opponentsDistinctBids[opponent].add(new BidDetails(bid, util,
						time));
				opponentsMinNumDistinctBids = opponentsDistinctBids[opponent]
						.size();
				opponentsMaxNumDistinctBids = opponentsDistinctBids[opponent]
						.size();
				for (int i = 0; i < numParties - 1; i++) {
					if (i == opponent)
						continue;
					opponentsMinNumDistinctBids = Math.min(
							opponentsMinNumDistinctBids,
							opponentsDistinctBids[i].size());
					opponentsMaxNumDistinctBids = Math.max(
							opponentsMaxNumDistinctBids,
							opponentsDistinctBids[i].size());
				}
			}
		} else if (action instanceof Accept) {
			double util = utilitySpace.getUtility(lastBid);
			DMC.bidsOpt.payoffs[opponent].updateWeights(lastBid);
			if (opponentsDistinctAccepts[opponent].filterUtility(util)
					.isEmpty()) {
				opponentsDistinctAccepts[opponent].add(new BidDetails(lastBid,
						util, time));
				opponentsMinNumDistinctAccepts = opponentsDistinctAccepts[opponent]
						.size();
				opponentsMaxNumDistinctAccepts = opponentsDistinctAccepts[opponent]
						.size();
				for (int i = 0; i < numParties - 1; i++) {
					if (i == opponent)
						continue;
					opponentsMinNumDistinctAccepts = Math.min(
							opponentsMinNumDistinctAccepts,
							opponentsDistinctAccepts[i].size());
					opponentsMaxNumDistinctAccepts = Math.max(
							opponentsMaxNumDistinctAccepts,
							opponentsDistinctAccepts[i].size());
				}
			}
		}
		if (turn != 0)
			turn++;
	}

	@Override
	public Action chooseAction(List<Class<? extends Action>> possibleActions) {
		Action action = new NoAction(getPartyId());
		try {
			Bid nextBid = null;
			if (possibleActions.contains(Accept.class)
					|| possibleActions.contains(Offer.class)) {
				if (myLastAction == null) {
					nextBid = secMaxBid;
					if (maxUtil - secMaxUtil < (secMaxUtil - minUtil) * 1.1) {
						nextBid = maxBid;
					}
					action = new Offer(getPartyId(), nextBid);
				} else if (lastBid.equals(maxBid)) {
					System.out.println("Accepted!!!!");
					action = new Accept(getPartyId());
				} else {
					if (maxUtil - secMaxUtil < (secMaxUtil - minUtil) * 1.1
							&& lastBid.equals(secMaxBid)) {
						System.out.println("Accepted!!!!");
						action = new Accept(getPartyId());
					} else {
						if (DMC.termination()) {
							System.out.println("Terminated!!!!");
							action = new EndNegotiation(getPartyId());
						} else {
							if (DMC.acceptance()) {
								System.out.println("Accepted!!!!");
								action = new Accept(getPartyId());
							} else {
								nextBid = DMC.bidProposal();
								if (nextBid == null) {
									nextBid = secMaxBid;
									if (maxUtil - secMaxUtil < (secMaxUtil - minUtil) * 1.1) {
										nextBid = maxBid;
									}
								}
								action = new Offer(getPartyId(), nextBid);
							}
						}
					}
				}
			}
			turn++;
			if (nextBid != null) {
				allBidHistory.add(new BidDetails(nextBid, TFC.thresholdFunc(),
						timeline.getTime()));
			}
			TFC.recordUtility(TFC.thresholdFunc(), -1);
			myLastAction = action;
		} catch (Exception e) {
			System.out.println((new StringBuilder("chooseAction failed!!\n")));
			e.printStackTrace(System.out);
			e.printStackTrace();
		}
		return action;
	}

}