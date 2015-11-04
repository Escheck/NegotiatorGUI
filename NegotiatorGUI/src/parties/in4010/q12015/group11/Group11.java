package parties.in4010.q12015.group11;

import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import negotiator.AgentID;
import negotiator.Bid;
import negotiator.BidHistory;
import negotiator.BidIterator;
import negotiator.Deadline;
import negotiator.actions.Accept;
import negotiator.actions.Action;
import negotiator.actions.Offer;
import negotiator.bidding.BidDetails;
import negotiator.issue.Issue;
import negotiator.issue.IssueDiscrete;
import negotiator.issue.Objective;
import negotiator.issue.ValueDiscrete;
import negotiator.parties.AbstractNegotiationParty;
import negotiator.session.TimeLineInfo;
import negotiator.utility.Evaluator;
import negotiator.utility.EvaluatorDiscrete;
import negotiator.utility.UtilitySpace;

/************************************************
 * Assignment AI Technique - Negotiation Agent By: J.K. van Schoubroeck
 * (4329996) H.H. Choiri (4468457) T. Smit (4242785)
 ************************************************/

public class Group11 extends AbstractNegotiationParty {

	// opponent model & bidding history for each opponent agent
	private HashMap<Object, UtilitySpace> opponentUtilitySpace = new HashMap<Object, UtilitySpace>();
	private HashMap<Object, BidHistory> bidHistory = new HashMap<Object, BidHistory>();

	private Bid lastBid;

	private int amountOfIssues;
	private double learningRate = 0.2;
	private int learnValueAddition;

	@Override
	public void init(UtilitySpace utilSpace, Deadline dl, TimeLineInfo tl,
			long randomSeed, AgentID agentId) {

		super.init(utilSpace, dl, tl, randomSeed, agentId);
		lastBid = new Bid(utilSpace.getDomain());
	}

	@Override
	public Action chooseAction(List<Class<? extends Action>> validActions) {

		if (bidHistory.isEmpty()) {
			// Opening Bid
			try {
				System.out.println("opening bid util:"
						+ getUtility(utilitySpace.getMaxUtilityBid()));
				return new Offer(utilitySpace.getMaxUtilityBid());
			} catch (Exception e) {
				System.out.println("Fail to send offer");
				e.printStackTrace();
			}
		}
		Bid nextBid = determineNextBid();
		double nextMyBidUtil = getUtility(nextBid);
		// double time = getTimeLine().getTime();

		// use AC Next approach, and accept if this agent get the highest
		// utility
		if ((getUtility(lastBid) >= nextMyBidUtil) && isGetHighest(lastBid)) {
			return new Accept();
		}

		return new Offer(nextBid);
	}

	@Override
	public void receiveMessage(AgentID sender, Action action) {
		super.receiveMessage(sender, action);
		Bid currentOpBid = Action.getBidFromAction(action);
		if (!(sender == null) && getUtility(currentOpBid) > 0) {
			lastBid = currentOpBid;
			try {
				// System.out.println("Sender is "+sender.toString());
				// Add the bid to the bidding history
				BidHistory bidH = new BidHistory();
				if (bidHistory.containsKey(sender.toString())) {
					bidH = bidHistory.get(sender.toString());
				}
				bidH.add(new BidDetails(currentOpBid, getUtility(currentOpBid)));
				bidHistory.put(sender.toString(), bidH);
			} catch (Exception e) {
				System.out.println("Error to add bid history of "
						+ sender.toString());
				e.printStackTrace();
			}
			updateModel(sender, currentOpBid, getTimeLine().getTime());
		}
	}

	public void updateModel(Object sender, Bid opponentBid, double time) {

		// use time-dependent learning value. 10*(1-t)
		learnValueAddition = (int) Math
				.round((1 - getTimeLine().getTime()) * 10);

		// This function handles the Opponent modelling
		BidHistory bidHist = bidHistory.get(sender.toString());
		if (!opponentUtilitySpace.containsKey(sender.toString())) {
			// initialize opponent model's weight
			UtilitySpace oppUSpace = new UtilitySpace(getUtilitySpace());
			amountOfIssues = oppUSpace.getDomain().getIssues().size();
			for (Entry<Objective, Evaluator> e : oppUSpace.getEvaluators()) {
				// set the issue weights equally to 1/#OfIssues
				oppUSpace.unlock(e.getKey());
				e.getValue().setWeight(1D / (double) amountOfIssues);
				try {
					// set all value weights to 10
					for (ValueDiscrete vd : ((IssueDiscrete) e.getKey())
							.getValues())
						((EvaluatorDiscrete) e.getValue())
								.setEvaluation(vd, 10);
				} catch (Exception ex) {
					System.out.println("Fail to initialize opponent model");
					ex.printStackTrace();
				}
			}
			opponentUtilitySpace.put(sender.toString(), oppUSpace);
			System.out.println("Opponent model successfully created");
			return;
		} else if (bidHist.size() < 2) {
			return;
		}

		// Update existing opponent model
		int numberOfUnchanged = 0;
		BidDetails oppBid = bidHist.getHistory().get(bidHist.size() - 1);
		BidDetails prevOppBid = bidHist.getHistory().get(bidHist.size() - 2);
		HashMap<Integer, Integer> lastDiffSet = determineDifference(sender,
				prevOppBid, oppBid);

		// count the number of changes in value
		for (Integer i : lastDiffSet.keySet()) {
			if (lastDiffSet.get(i) == 0)
				numberOfUnchanged++;
		}

		double goldenValue = learningRate / (double) amountOfIssues;
		double totalSum = 1D + goldenValue * (double) numberOfUnchanged;
		double maximumWeight = 1D - ((double) amountOfIssues) * goldenValue
				/ totalSum;

		UtilitySpace oppUSpace = opponentUtilitySpace.get(sender.toString());

		// update and normalize issue weights
		for (Integer i : lastDiffSet.keySet()) {
			if (lastDiffSet.get(i) == 0
					&& oppUSpace.getWeight(i) < maximumWeight)
				oppUSpace.setWeight(oppUSpace.getDomain().getObjective(i),
						(oppUSpace.getWeight(i) + goldenValue) / totalSum);
			else
				oppUSpace.setWeight(oppUSpace.getDomain().getObjective(i),
						oppUSpace.getWeight(i) / totalSum);
		}

		// update the issue value weights
		try {
			for (Entry<Objective, Evaluator> e : oppUSpace.getEvaluators()) {
				((EvaluatorDiscrete) e.getValue())
						.setEvaluation(
								oppBid.getBid().getValue(
										((IssueDiscrete) e.getKey())
												.getNumber()),
								(learnValueAddition + ((EvaluatorDiscrete) e
										.getValue())
										.getEvaluationNotNormalized(((ValueDiscrete) oppBid
												.getBid().getValue(
														((IssueDiscrete) e
																.getKey())
																.getNumber())))));
			}
			opponentUtilitySpace.put(sender.toString(), oppUSpace);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private HashMap<Integer, Integer> determineDifference(Object sender,
			BidDetails first, BidDetails second) {
		// get the value differences between 2 bids
		HashMap<Integer, Integer> diff = new HashMap<Integer, Integer>();
		try {
			for (Issue i : opponentUtilitySpace.get(sender.toString())
					.getDomain().getIssues()) {
				diff.put(i.getNumber(), (((ValueDiscrete) first.getBid()
						.getValue(i.getNumber())).equals((ValueDiscrete) second
						.getBid().getValue(i.getNumber()))) ? 0 : 1);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return diff;
	}

	public Bid determineNextBid() {
		// This function handles Bidding strategy
		Bid bestBid;
		double time = getTimeLine().getTime();
		double utilityGoal;
		// calculate target utility
		utilityGoal = (1 - Math.pow(time, 2)) * 0.5 + 0.5;
		if (utilityGoal < 0.85) {
			utilityGoal = 0.85;
		}

		if (time < 0.6) {
			utilityGoal = (1 - Math.pow(time, 2)) * 0.5 + 0.5;
		} else {
			utilityGoal = 0.85;
		}
		try {
			bestBid = getBidNearUtility(utilityGoal, 0.05);
			return bestBid;
		} catch (Exception e) {
			System.out.println("Fail tp get bid near utility");
			e.printStackTrace();
		}

		return null;
	}

	private Bid getBidNearUtility(double target, double delta) throws Exception {
		// System.out.println("target utility:"+target+", delta: "+delta);
		// This function searches the best bid based on target utility and
		// tolerance
		BidIterator iter = new BidIterator(utilitySpace.getDomain());
		Bid bestBid = null;
		double maxOpUtil = -1;
		while (iter.hasNext()) {
			Bid nBid = iter.next();
			// check all bids
			try {
				double currMyU = getUtility(nBid);
				if (Math.abs(currMyU - target) < delta) {
					// bid's utility is in the range, check the opponent's
					// utility
					double oppUtil = 0;
					boolean Iwin = true;
					for (Entry<Object, UtilitySpace> opU : opponentUtilitySpace
							.entrySet()) {
						// sum all opponent's utility
						double currOpU = opU.getValue().getUtility(nBid);
						// make sure agent's utility is the highest
						if (Iwin && (currMyU >= currOpU)) {
							oppUtil += currOpU;
						} else {
							Iwin = false;
							break;
						}
					}

					if (Iwin && (oppUtil > maxOpUtil)) {
						// choose maximum opponent total utility
						bestBid = nBid;
						maxOpUtil = oppUtil;
					}
				}
			} catch (Exception e) {
				System.out.println("Fail to get opponent utility space 2");
				e.printStackTrace();
			}
		}
		if (maxOpUtil == -1) {
			// searching file, add the tolerance value
			return getBidNearUtility(target, delta + 0.05);
		}
		return bestBid;
	}

	public boolean isGetHighest(Bid bid) {
		double myUtil = getUtility(bid);
		// compare the utility with opponent's utility and check if the agent
		// gets the highest utility
		for (Entry<Object, UtilitySpace> opU : opponentUtilitySpace.entrySet()) {
			try {
				if (myUtil < opU.getValue().getUtility(bid)) {
					return false;
				}
			} catch (Exception e) {
				System.out.println("Fail to get opponent utility space 3");
				e.printStackTrace();
			}
		}

		return true;
	}

	@Override
	public String getDescription() {
		return "Agent11 - Multi party negotiation agent";
	}

}