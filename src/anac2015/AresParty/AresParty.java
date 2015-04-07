package anac2015.AresParty;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

//import negotiator.Agent;
import negotiator.Bid;
import negotiator.BidIterator;
import negotiator.Deadline;
import negotiator.Timeline;
import negotiator.actions.Accept;
import negotiator.actions.Action;
import negotiator.actions.EndNegotiation;
import negotiator.actions.Offer;
import negotiator.issue.ISSUETYPE;
import negotiator.issue.Issue;
import negotiator.issue.IssueDiscrete;
import negotiator.issue.IssueInteger;
import negotiator.issue.IssueReal;
import negotiator.issue.Value;
import negotiator.issue.ValueDiscrete;
import negotiator.issue.ValueInteger;
import negotiator.issue.ValueReal;
//import negotiator.AresParty2015.ComparableBid;
import negotiator.parties.AbstractNegotiationParty;
import negotiator.utility.EvaluatorInteger;
import negotiator.utility.EvaluatorReal;
import negotiator.utility.UtilitySpace;

/**
 *
 */
public class AresParty extends AbstractNegotiationParty {

	private double totalTime;
	private Action ActionOfOpponent = null;
	private double maximumOfBid;
	private OwnBidHistory ownBidHistory;
	private OpponentBidHistory opponentBidHistory;
	private double minimumUtilityThreshold;
	private double utilitythreshold;
	private double MaximumUtility;
	private double timeLeftBefore;
	private double timeLeftAfter;
	private double maximumTimeOfOpponent;
	private double maximumTimeOfOwn;
	private double discountingFactor;
	private double concedeToDiscountingFactor;
	private double concedeToDiscountingFactor_original;
	private double minConcedeToDiscountingFactor;
	private ArrayList<ArrayList<Bid>> bidsBetweenUtility;
	private boolean concedeToOpponent;
	private boolean toughAgent; // if we propose a bid that was proposed by the
								// opponnet, then it should be accepted.
	// private double alpha1;//the larger alpha is, the more tough the agent is.
	private Bid bid_maximum_utility;// the bid with the maximum utility over the
									// utility space.
	private double reservationValue;
	private Object myparty = null;
	private Object party = null;
	private int NoOfParty = -1;
	private ArrayList<Object> BidsOrder = new ArrayList<Object>();

	public boolean halfSucInv = false;
	public boolean halfSucNone = false;
	public Bid lastBidReal = null; // the last bid received. If it is an accept,
									// it refers to which bid has been agreed
									// upon
	public Bid lastBidNP = null; // the offer from the opponent next to us
									// (opponent 1)
	private OpponentBidHistory opponentBidHistory2; // the bid history of
													// opponent 2
	private int curRounds = 0;
	private ArrayList<ComparableBid> relevantBids;

	private ArrayList<HashMap<Object, Double>> opponentUtilityEstimator;
	private HashMap<Bid, Integer> previousOpponentBids;
	private Bid lastBid = null;
	private ArrayList<Double> maxValue;
	private int lastPropose = 0;
	private Bid BestOpponentBid = null;
	private double firstOpponentBidUtility = 0;
	private double EstimatedRTT = 0;
	private double devRTT = 0;
	private double prevTime = 0;

	Action actionOfPartner = null;
	Action maxUAction = null;

	public AresParty(UtilitySpace utilitySpace, Deadline deadlines,
			Timeline timeline, long randomSeed) {
		super(utilitySpace, deadlines, timeline, randomSeed);
		myparty = getPartyId();

		try {
			maximumOfBid = this.utilitySpace.getDomain()
					.getNumberOfPossibleBids();
			ownBidHistory = new OwnBidHistory();
			opponentBidHistory = new OpponentBidHistory();
			bidsBetweenUtility = new ArrayList<ArrayList<Bid>>();
			this.bid_maximum_utility = utilitySpace.getMaxUtilityBid();
			this.utilitythreshold = utilitySpace
					.getUtility(bid_maximum_utility); // initial utility
														// threshold
			this.MaximumUtility = this.utilitythreshold;
			this.timeLeftAfter = 0;
			this.timeLeftBefore = 0;
			this.totalTime = timeline.getTotalTime();
			this.maximumTimeOfOpponent = 0;
			this.maximumTimeOfOwn = 0;
			this.minConcedeToDiscountingFactor = 0.08;// 0.1;
			this.discountingFactor = 1;
			if (utilitySpace.getDiscountFactor() <= 1D
					&& utilitySpace.getDiscountFactor() > 0D) {
				this.discountingFactor = utilitySpace.getDiscountFactor();
			}
			this.chooseUtilityThreshold();
			this.calculateBidsBetweenUtility();
			this.chooseConcedeToDiscountingDegree();
			this.opponentBidHistory.initializeDataStructures(utilitySpace
					.getDomain());
			this.timeLeftAfter = timeline.getCurrentTime();
			this.concedeToOpponent = false;
			this.toughAgent = false;
			// this.alpha1 = 2;
			this.reservationValue = 0;
			if (utilitySpace.getReservationValue() > 0) {
				this.reservationValue = utilitySpace.getReservationValue();
			}
		} catch (Exception e) {
			System.out.println("initialization error" + e.getMessage());
		}

		// if( this.maximumOfBid <= 8000 ){
		ArrayList<Bid> allBids = GetDiscreteBids();
		relevantBids = new ArrayList<ComparableBid>();
		for (Bid b : allBids) {
			try {
				relevantBids.add(new ComparableBid(b, utilitySpace
						.getUtility(b)));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		Collections.sort(relevantBids);
		// }

		previousOpponentBids = new HashMap<Bid, Integer>();
		maxUAction = new Offer(this.BestOpponentBid);

		Serializable s = null;
		maxValue = new ArrayList<Double>();
		if (s != null) {
			opponentUtilityEstimator = (ArrayList<HashMap<Object, Double>>) s;
			int i = 0;
			for (HashMap<Object, Double> issue : opponentUtilityEstimator) {
				maxValue.add(0.0);
				for (double curr : issue.values()) {
					if (maxValue.get(i) < curr)
						maxValue.set(i, curr);
				}
				i++;
			}
		} else {
			opponentUtilityEstimator = new ArrayList<HashMap<Object, Double>>();

			ArrayList<Issue> issues = this.utilitySpace.getDomain().getIssues();
			for (Issue issue : issues) {
				int max_i = opponentUtilityEstimator.size();
				opponentUtilityEstimator.add(new HashMap<Object, Double>());
				if (issue.getType() == ISSUETYPE.DISCRETE) {
					for (ValueDiscrete vd : ((IssueDiscrete) issue).getValues()) {
						opponentUtilityEstimator.get(max_i).put(vd, 0.0);
					}
				} else if (issue.getType() == ISSUETYPE.INTEGER) {
					int k = Math.min(10, ((IssueInteger) issue).getUpperBound()
							- ((IssueInteger) issue).getLowerBound());
					for (int i = 0; i <= k; i++) {
						opponentUtilityEstimator.get(max_i).put(i, 0.0);
					}
				} else if (issue.getType() == ISSUETYPE.REAL) {
					int k = 10;
					for (int i = 0; i < k; i++) {
						opponentUtilityEstimator.get(max_i).put(i + 0.0, 0.0);
					}
				}
				maxValue.add(0.0);
			}
		}

	}

	@Override
	public Action chooseAction(List<Class> validActions) {
		Action action = null;

		curRounds++;
		Bid bid = null;

		if (BidsOrder.size() != NoOfParty)
			BidsOrder.add(myparty);

		try {
			// System.out.println("i propose " + debug + " bid at time " +
			// timeline.getTime());
			this.timeLeftBefore = timeline.getCurrentTime();

			if (this.discountingFactor <= 0.5 && this.reservationValue >= 0.45) {
				return new EndNegotiation();
			}

			// we propose first and propose the bid with maximum utility
			if (!validActions.contains(Accept.class)) {
				bid = this.bid_maximum_utility;
				action = new Offer(bid);
			} else if (!halfSucInv && !halfSucNone) {// the opponent propose
														// first and we response
														// secondly

				// update the estimation
				if (curRounds <= 10) {
					// bid = utilitySpace.getMaxUtilityBid();
					bid = this.bid_maximum_utility;
					action = new Offer(bid);
					// System.out.println("test0-0,"+ownBidHistory.numOfBidsProposed()
					// );
				} else {// other conditions

					// System.out.println("test0-1");
					if (!HaveMoreTimeToBid(true)) {// still have some rounds
													// left to further negotiate
													// (the major negotiation
													// period)

						try {
							if (utilitySpace.getUtility(BestOpponentBid) <= utilitySpace
									.getUtility(lastBid)) {
								if (utilitySpace.getUtility(lastBid) > this.reservationValue)
									return new Accept();
							} else if (utilitySpace.getUtility(BestOpponentBid) > this.reservationValue) {
								bid = BestOpponentBid;
								action = new Offer(BestOpponentBid);
							} else {
								return maxUAction;
							}
						} catch (Exception e) {
							e.printStackTrace();
							return maxUAction;
						}
					}

					if (!HaveMoreTimeToBid(false)) {

						try {
							if (utilitySpace.getUtility(lastBid) > this.reservationValue)
								return new Accept();
						} catch (Exception e) {
						}

						return maxUAction;
					}

				}

				double alpha = getHardness();
				ComparableBid cb = relevantBids.get(0); //
				double best; // best bid's utility
				try {
					best = alpha * cb.utility + (1 - alpha)
							* GetEstimatedOpponentUtility(cb.bid);
				} catch (Exception e1) {
					e1.printStackTrace();
					best = 0.0;
				}
				int j = Math.max(1, lastPropose - 3000);
				for (int i = j; i < relevantBids.size()
						&& i < lastPropose + 7000; i++) {
					double curr;
					ComparableBid currBid = relevantBids.get(i);

					try {
						curr = alpha * currBid.utility + (1 - alpha)
								* GetEstimatedOpponentUtility(currBid.bid);
					} catch (Exception e) {
						e.printStackTrace();
						curr = 0.0;
					}

					if (curr > best) {
						cb = currBid;
						best = curr;
					}
				}

				try {
					if (lastBid != null
							&& utilitySpace.getUtility(lastBid) > cb.utility) //

						action = new Accept();
					else if (cb.utility < this.reservationValue) {
						bid = this.bid_maximum_utility;
						action = maxUAction;
					} else {
						bid = cb.bid;
						action = new Offer(bid);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}

				if (action == null) {

					try {
						bid = this.bid_maximum_utility;
						action = new Offer(bid);
					} catch (Exception e) {
					}
				}

			} else if (!halfSucInv && halfSucNone) {

				// System.out.println("test0-2");
				if (curRounds <= 10) {
					// bid = utilitySpace.getMaxUtilityBid();
					bid = this.bid_maximum_utility;
					action = new Offer(bid);
				} else {// other conditions
					if (estimateRoundLeft(true) > 6
							|| timeline.getTime() <= 0.9985) {// still have some
																// rounds left
																// to further
																// negotiate
																// (the major
																// negotiation
																// period)

						double alpha = getHardness();
						ComparableBid cb = relevantBids.get(0); //
						double best; // best bid's utility
						try {
							best = alpha * cb.utility + (1 - alpha)
									* GetEstimatedOpponentUtility(cb.bid);
						} catch (Exception e1) {
							e1.printStackTrace();
							best = 0.0;
						}
						int j = Math.max(1, lastPropose - 3000);
						for (int i = j; i < relevantBids.size()
								&& i < lastPropose + 7000; i++) {
							double curr;
							ComparableBid currBid = relevantBids.get(i);

							try {
								curr = alpha
										* currBid.utility
										+ (1 - alpha)
										* GetEstimatedOpponentUtility(currBid.bid);
							} catch (Exception e) {
								e.printStackTrace();
								curr = 0.0;
							}

							if (curr > best) {
								cb = currBid;
								best = curr;
							}
						}

						try {
							if (lastBid != null
									&& utilitySpace.getUtility(lastBid) > cb.utility) //

								action = new Accept();
							else if (cb.utility < this.reservationValue) {
								bid = this.bid_maximum_utility;
								action = maxUAction;
							} else {
								bid = cb.bid;
								action = new Offer(bid);
							}
						} catch (Exception e) {
							e.printStackTrace();
						}

						if (action == null) {

							try {
								bid = this.bid_maximum_utility;
								action = new Offer(bid);
							} catch (Exception e) {
							}
						}

					} else {// this is the last chance and we concede by
							// providing the opponent the best offer he ever
							// proposed to us
						// in this case, it corresponds to an opponent whose
						// decision time is short

						if (timeline.getTime() >= 0.9985
								&& estimateRoundLeft(true) < 5) {
							// bid =
							// opponentBidHistory.chooseBestFromHistory(this.utilitySpace);
							bid = opponentBidHistory.getBestBidInHistory();
							// this is specially designed to avoid that we got
							// very low utility by searching between an
							// acceptable range (when the domain is small)
							if (this.utilitySpace.getUtility(bid) < 0.85) {
								// List<Bid> candidateBids =
								// this.getBidsBetweenUtility(this.MaximumUtility
								// - 0.15, this.MaximumUtility - 0.02);
								// if the candidate bids do not exsit and also
								// the deadline is approaching in next round, we
								// concede.
								// if (candidateBids.size() == 1 &&
								// timeline.getTime()>0.9998) {
								// we have no chance to make a new proposal
								// before the deadline
								if (this.estimateRoundLeft(true) < 2) {
									bid = opponentBidHistory
											.getBestBidInHistory();
									System.out.println("test I "
											+ utilitySpace.getUtility(bid));
								} else {
									// bid =
									// opponentBidHistory.ChooseBid(candidateBids,
									// this.utilitySpace.getDomain());
									bid = BidToOffer();
								}
								if (bid == null) {
									bid = opponentBidHistory
											.getBestBidInHistory();
								}
							}
							Boolean IsAccept = AcceptOpponentOffer(lastBidNP,
									bid);
							Boolean IsTerminate = TerminateCurrentNegotiation(bid);
							if (IsAccept && !IsTerminate) {
								action = new Accept();
								System.out.println("accept the offer");
							} else if (IsTerminate && !IsAccept) {
								action = new EndNegotiation();
								System.out
										.println("we determine to terminate the negotiation");
							} else if (IsTerminate && IsAccept) {
								if (this.utilitySpace.getUtility(lastBidNP) > this.reservationValue) {
									action = new Accept();
									System.out
											.println("we accept the offer RANDOMLY");
								} else {
									action = new EndNegotiation();
									System.out
											.println("we determine to terminate the negotiation RANDOMLY");
								}
							} else {
								if (this.toughAgent == true) {
									action = new Accept();
									System.out
											.println("the opponent is tough and the deadline is approching thus we accept the offer");
								} else {
									action = new Offer(bid);
									// this.toughAgent = true;
									System.out
											.println("this is really the last chance"
													+ bid.toString()
													+ " with utility of "
													+ utilitySpace
															.getUtility(bid));
								}
							}
							// in this case, it corresponds to the situation
							// that we encounter an opponent who needs more
							// computation to make decision each round
						} else {// we still have some time to negotiate,
							// and be tough by sticking with the lowest one in
							// previous offer history.
							// we also have to make the decision fast to avoid
							// reaching the deadline before the decision is made
							// bid = ownBidHistory.GetMinBidInHistory();//reduce
							// the computational cost

							bid = BidToOffer();

							// System.out.println("test----------------------------------------------------------"
							// + timeline.getTime());
							Boolean IsAccept = AcceptOpponentOffer(lastBidNP,
									bid);
							Boolean IsTerminate = TerminateCurrentNegotiation(bid);
							if (IsAccept && !IsTerminate) {
								action = new Accept();
								System.out.println("accept the offer");
							} else if (IsTerminate && !IsAccept) {
								action = new EndNegotiation();
								System.out
										.println("we determine to terminate the negotiation");
							} else if (IsAccept && IsTerminate) {
								if (this.utilitySpace.getUtility(lastBidNP) > this.reservationValue) {
									action = new Accept();
									System.out
											.println("we accept the offer RANDOMLY");
								} else {
									action = new EndNegotiation();
									System.out
											.println("we determine to terminate the negotiation RANDOMLY");
								}
							} else {
								action = new Offer(bid);
								// System.out.println("we have to be tough now"
								// + bid.toString() + " with utility of " +
								// utilitySpace.getUtility(bid));
							}
						}
					}
				}

			} else if (halfSucInv && !halfSucNone) {

				// update the estimation
				if (curRounds <= 10) {
					// bid = utilitySpace.getMaxUtilityBid();
					bid = this.bid_maximum_utility;
					action = new Offer(bid);
					// System.out.println("test0-0,"+ownBidHistory.numOfBidsProposed()
					// );
				} else {// other conditions

					// System.out.println("test0-1");
					if (!HaveMoreTimeToBid(true)) {// still have some rounds
													// left to further negotiate
													// (the major negotiation
													// period)

						try {
							if (utilitySpace.getUtility(BestOpponentBid) <= utilitySpace
									.getUtility(lastBid)) {
								if (utilitySpace.getUtility(lastBid) > this.reservationValue)
									return new Accept();
							} else if (utilitySpace.getUtility(BestOpponentBid) > this.reservationValue) {
								bid = BestOpponentBid;
								action = new Offer(BestOpponentBid);
							} else {
								return maxUAction;
							}
						} catch (Exception e) {
							e.printStackTrace();
							return maxUAction;
						}

					}

					if (!HaveMoreTimeToBid(false)) {

						try {
							if (utilitySpace.getUtility(lastBid) > this.reservationValue)
								return new Accept();
						} catch (Exception e) {
						}

						return maxUAction;
					}

				}

				double alpha = getHardness();
				ComparableBid cb = relevantBids.get(0); //
				double best; // best bid's utility
				try {
					best = alpha * cb.utility + (1 - alpha)
							* GetEstimatedOpponentUtility(cb.bid);
				} catch (Exception e1) {
					e1.printStackTrace();
					best = 0.0;
				}
				int j = Math.max(1, lastPropose - 3000);
				for (int i = j; i < relevantBids.size()
						&& i < lastPropose + 7000; i++) {
					double curr;
					ComparableBid currBid = relevantBids.get(i);

					try {
						curr = alpha * currBid.utility + (1 - alpha)
								* GetEstimatedOpponentUtility(currBid.bid);
					} catch (Exception e) {
						e.printStackTrace();
						curr = 0.0;
					}

					if (curr > best) {
						cb = currBid;
						best = curr;
					}
				}

				try {
					if (lastBid != null
							&& utilitySpace.getUtility(lastBid) > cb.utility) //

						action = new Accept();
					else if (cb.utility < this.reservationValue) {
						bid = this.bid_maximum_utility;
						action = maxUAction;
					} else {
						bid = cb.bid;
						action = new Offer(bid);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}

				if (action == null) {

					try {
						bid = this.bid_maximum_utility;
						action = new Offer(bid);
					} catch (Exception e) {
					}
				}

			} else {
				bid = ownBidHistory.getLastBid();
				action = new Offer(bid);
				System.out.println("Exception in Decision-making" + myparty);
			}

			// System.out.println("i propose " + debug + " bid at time " +
			// timeline.getTime());

			this.timeLeftAfter = timeline.getCurrentTime();
			this.estimateRoundLeft(false);// update the estimation
			// System.out.println(this.utilitythreshold + "-***-----" +
			// this.timeline.getElapsedSeconds());
		} catch (Exception e) {
			System.out.println("Exception in ChooseAction: " + e.getMessage()
					+ "," + myparty);
			System.out.println(estimateRoundLeft(false));
			// action = new Accept( ); // accept if anything goes wrong.

			/*
			 * if( timeline.getTime() < 0.95 ){ action = new Offer(
			 * this.ownBidHistory.getLastBid()); }else{ if ( reservationValue >
			 * getUtility(lastBidReal) ){ action = new EndNegotiation();
			 * //terminate if anything goes wrong. }else{ action = new Accept();
			 * } }
			 */

			bid = this.bid_maximum_utility;
			action = new Offer(bid);

			if (getUtility(lastBidReal) >= this.utilitythreshold)
				action = new Accept();

			if (getUtility(this.opponentBidHistory.getBestBidInHistory()) + 0.05 > this.utilitythreshold) {
				bid = this.opponentBidHistory.getBestBidInHistory();
				action = new Offer(bid);
			}

			if (getUtility(this.opponentBidHistory2.getBestBidInHistory()) + 0.05 > this.utilitythreshold) {
				bid = this.opponentBidHistory2.getBestBidInHistory();
				action = new Offer(bid);
			}

			if (timeline.getTime() > 0.998
					&& getUtility(lastBidReal) + 0.06 >= this.utilitythreshold) {
				bid = lastBidReal;
				action = new Offer(bid);
			}

			if (timeline.getCurrentTime() > timeline.getTotalTime() * 1.3) {
				System.out.println("Negotiation time running out!"
						+ timeline.getCurrentTime() + ","
						+ timeline.getTotalTime() + myparty);

				if (getUtility(lastBidReal) > this.reservationValue * 1.1) {
					action = new Accept();
				} else {
					action = new EndNegotiation();
				}
			}

		}

		this.ownBidHistory.addBid(bid, utilitySpace);

		// if ( this.discountingFactor <=0.5 && this.reservationValue >=0.42 )
		// action = new EndNegotiation();

		if (timeline.getCurrentTime() > timeline.getTotalTime() * 1.3) {
			System.out.println("Negotiation time running out!"
					+ timeline.getCurrentTime() + "," + timeline.getTotalTime()
					+ myparty);

			if (getUtility(lastBidReal) > this.reservationValue * 1.1) {
				action = new Accept();
			} else {
				action = new EndNegotiation();
			}
		}

		if (action == null)
			action = new Offer(ownBidHistory.getLastBid());

		// System.out.println("check,"+timeline.getTime()+","+this.utilitythreshold);

		return action;
	}

	@Override
	public void receiveMessage(Object sender, Action opponentAction) {

		super.receiveMessage(sender, opponentAction);

		this.ActionOfOpponent = opponentAction;
		this.party = sender;

		if (NoOfParty == -1) {
			NoOfParty = getNumberOfParties();

			if (NoOfParty >= 3) {
				opponentBidHistory2 = new OpponentBidHistory();
			}

		} else {

			if (BidsOrder.size() != NoOfParty)
				BidsOrder.add(sender);

			if (NoOfParty == 2) {

				if (ActionOfOpponent instanceof Offer) {
					// Bid partnerBid = ((Offer) ActionOfOpponent).getBid();
					// double offeredUtilFromOpponent = getUtility(partnerBid);

					this.opponentBidHistory.updateOpponentModel(
							((Offer) ActionOfOpponent).getBid(),
							utilitySpace.getDomain(), this.utilitySpace);
					this.updateConcedeDegree();

				} else if (ActionOfOpponent instanceof Accept) {
					// receive an accept from an opponent over an offer, which
					// may not be ours.

				} else if (ActionOfOpponent instanceof EndNegotiation) {
					// receive an accept from an opponent over an offer, which
					// may not be ours.

					/*
					 * this.opponentBidHistory.updateOpponentModel(
					 * ownBidHistory.getLastBid(), utilitySpace.getDomain(),
					 * this.utilitySpace);
					 */
					System.out.println("Some leaves the negotiation"
							+ sender.toString());
				} else {

					System.out
							.println("unexpected action" + myparty.toString());
				}

			} else { // the number of party is larger than 2

				// lastBidReal = null; // the last bid received. If it is an
				// accept, it refers to which bid has been agreed upon
				// lastBidNP = null;

				if (ActionOfOpponent instanceof Offer) {
					// Bid partnerBid = ((Offer) ActionOfOpponent).getBid();
					// double offeredUtilFromOpponent = getUtility(partnerBid);

					if (BidsOrder.size() == NoOfParty
							&& (BidsOrder.indexOf(sender) - 1 + BidsOrder
									.size()) % BidsOrder.size() == BidsOrder
									.indexOf(myparty)) {

						this.opponentBidHistory.updateOpponentModel(
								((Offer) ActionOfOpponent).getBid(),
								utilitySpace.getDomain(), this.utilitySpace);
						this.updateConcedeDegree();

						lastBidNP = ((Offer) ActionOfOpponent).getBid();
						halfSucInv = false;
						// System.out.println("record+"+sender.toString());
					} else {
						// record the other agents' offers
						halfSucNone = false;

						this.opponentBidHistory2.updateOpponentModel(
								((Offer) ActionOfOpponent).getBid(),
								utilitySpace.getDomain(), this.utilitySpace);
						this.updateConcedeDegree2();

						// System.out.println("record other agents, "+timeline.getTime());
					}

					lastBidReal = ((Offer) ActionOfOpponent).getBid();

				} else if (ActionOfOpponent instanceof Accept) {
					// receive an accept from an opponent over an offer, which
					// may not be ours.

					/*
					 * this.opponentBidHistory.updateOpponentModel(
					 * ownBidHistory.getLastBid(), utilitySpace.getDomain(),
					 * this.utilitySpace);
					 */

					if ((BidsOrder.indexOf(sender) - 1 + BidsOrder.size())
							% BidsOrder.size() == BidsOrder.indexOf(myparty)) {

						halfSucInv = true; // partner next to our agent accepts
											// our offer

					} else {
						halfSucNone = true;
					}

					System.out
							.println("Some sends accept," + sender.toString());

				} else if (ActionOfOpponent instanceof EndNegotiation) {
					// receive an accept from an opponent over an offer, which
					// may not be ours.

					/*
					 * this.opponentBidHistory.updateOpponentModel(
					 * ownBidHistory.getLastBid(), utilitySpace.getDomain(),
					 * this.utilitySpace);
					 */
					System.out.println("Some leaves the negotiation,"
							+ sender.toString());
				} else {

					System.out
							.println("unexpected action" + myparty.toString());
				}

			}

		}

		double currRTT = timeline.getTime() - prevTime;
		prevTime = timeline.getTime();
		EstimatedRTT = 0.6 * EstimatedRTT + 0.4 * currRTT;
		devRTT = 0.75 * devRTT + 0.25 * Math.abs(currRTT - EstimatedRTT);

		Bid b = Action.getBidFromAction(opponentAction);
		if (b == null)
			return;

		Integer i = previousOpponentBids.get(b);
		if (i == null) {
			i = 0;
			AddOpponentBidToModel(b, false);
		}
		previousOpponentBids.put(b, i + 1);

		if (lastBid == null) {

			try {
				double minimumRelevant = Math.max(utilitySpace.getUtility(b),
						utilitySpace.getReservationValueUndiscounted());
				while (relevantBids.size() > 0
						&& (utilitySpace.getUtility(relevantBids
								.get(relevantBids.size() - 1).bid) < minimumRelevant))
					relevantBids.remove(relevantBids.size() - 1);
				BestOpponentBid = b;

				firstOpponentBidUtility = utilitySpace.getUtility(b);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		lastBid = b;
		try {
			if (utilitySpace.getUtility(BestOpponentBid) < utilitySpace
					.getUtility(b))
				BestOpponentBid = b;
		} catch (Exception e) {
		}

	}

	/*
	 * principle: randomization over those candidate bids to let the opponent
	 * have a better model of my utility profile return the bid to be offered in
	 * the next round
	 */
	private Bid BidToOffer() {
		Bid bidReturned = null;
		double decreasingAmount_1 = 0.05;
		double decreasingAmount_2 = 0.1;
		double decreasingAmount_3 = 0.15;
		double decreasingAmount_4 = 0.2;
		double decreasingAmount_5 = 0.25;
		double decreasingAmount_6 = 0.3;
		double decreasingAmount_7 = 0.35;

		// System.out.println("test0 in BidtoOffers");

		try {

			double maximumOfBid = this.MaximumUtility;// utilitySpace.getUtility(utilitySpace.getMaxUtilityBid());
			double minimumOfBid = 0;

			if (this.discountingFactor >= 0.9 && this.reservationValue >= 0.5) {

				//
				if (this.maximumOfBid <= 200) {

					minimumOfBid = this.MaximumUtility - decreasingAmount_5
							* timeline.getTime();

				} else if (this.maximumOfBid <= 3000) {

					if (timeline.getTime() < 0.995) {
						minimumOfBid = this.MaximumUtility;
					} else {
						minimumOfBid = this.MaximumUtility - decreasingAmount_5
								* Math.pow(timeline.getTime(), 2.5);
					}

				} else {

					if (timeline.getTime() < 0.995) {
						minimumOfBid = this.MaximumUtility;
					} else {
						minimumOfBid = this.MaximumUtility - decreasingAmount_5
								* Math.pow(timeline.getTime(), 5);
					}
				}

			} else if (this.discountingFactor >= 0.9
					&& this.reservationValue >= 0.25
					&& this.reservationValue < 0.5) {

				//
				if (this.maximumOfBid <= 200) {

					minimumOfBid = this.MaximumUtility - decreasingAmount_5
							* timeline.getTime();

				} else if (this.maximumOfBid <= 3000) {

					if (timeline.getTime() < 0.995) {
						minimumOfBid = this.MaximumUtility;
					} else {
						minimumOfBid = this.MaximumUtility - decreasingAmount_5
								* Math.pow(timeline.getTime(), 2.5);
					}

				} else {

					if (timeline.getTime() < 0.995) {
						minimumOfBid = this.MaximumUtility;
					} else {
						minimumOfBid = this.MaximumUtility - decreasingAmount_5
								* Math.pow(timeline.getTime(), 5);
					}
				}

			} else if (this.discountingFactor >= 0.9
					&& this.reservationValue < 0.25) {

				if (this.maximumOfBid <= 200) {

					minimumOfBid = this.MaximumUtility - decreasingAmount_5
							* timeline.getTime();

				} else if (this.maximumOfBid <= 3000) {

					if (timeline.getTime() < 0.985) {
						minimumOfBid = this.MaximumUtility;
					} else {
						minimumOfBid = this.MaximumUtility - decreasingAmount_5
								* Math.pow(timeline.getTime(), 2.5);
					}

				} else {

					if (timeline.getTime() < 0.985) {
						minimumOfBid = this.MaximumUtility;
					} else {
						minimumOfBid = this.MaximumUtility - decreasingAmount_5
								* Math.pow(timeline.getTime(), 5);
					}
				}

				// System.out.println("test2 in BidtoOffers,"+minimumOfBid);

			} else if (this.discountingFactor >= 0.75
					&& this.reservationValue >= 0.5) {

				if (timeline.getTime() <= this.concedeToDiscountingFactor) {
					double minThreshold = (maximumOfBid * this.discountingFactor)
							/ Math.pow(this.discountingFactor,
									this.concedeToDiscountingFactor);

					if (minThreshold < this.reservationValue)
						minThreshold = this.reservationValue;

					this.utilitythreshold = maximumOfBid
							- (maximumOfBid - minThreshold)
							* Math.pow(
									(timeline.getTime() / this.concedeToDiscountingFactor),
									3);
				} else {
					// this.utilitythreshold = (maximumOfBid *
					// this.discountingFactor) /
					// Math.pow(this.discountingFactor, timeline.getTime());
					this.utilitythreshold = maximumOfBid
							- (maximumOfBid - 0.7)
							* Math.pow(timeline.getTime(),
									this.discountingFactor * 4);
				}

			} else if (this.discountingFactor >= 0.75
					&& this.reservationValue >= 0.25
					&& this.reservationValue < 0.5) {

				if (timeline.getTime() <= this.concedeToDiscountingFactor) {
					double minThreshold = (maximumOfBid * this.discountingFactor)
							/ Math.pow(this.discountingFactor,
									this.concedeToDiscountingFactor);

					if (minThreshold < this.reservationValue)
						minThreshold = this.reservationValue;

					this.utilitythreshold = maximumOfBid
							- (maximumOfBid - minThreshold)
							* Math.pow(
									(timeline.getTime() / this.concedeToDiscountingFactor),
									3);
				} else {
					// this.utilitythreshold = (maximumOfBid *
					// this.discountingFactor) /
					// Math.pow(this.discountingFactor, timeline.getTime());
					double miniT = this.reservationValue;

					if (miniT < 0.7)
						miniT = 0.7;

					this.utilitythreshold = maximumOfBid
							- (maximumOfBid - miniT)
							* Math.pow(timeline.getTime(),
									this.discountingFactor * 5);
				}

			} else if (this.discountingFactor >= 0.75
					&& this.reservationValue < 0.25) {

				if (timeline.getTime() <= this.concedeToDiscountingFactor) {
					double minThreshold = (maximumOfBid * this.discountingFactor)
							/ Math.pow(this.discountingFactor,
									this.concedeToDiscountingFactor);

					if (minThreshold < this.reservationValue)
						minThreshold = this.reservationValue;

					this.utilitythreshold = maximumOfBid
							- (maximumOfBid - minThreshold)
							* Math.pow(
									(timeline.getTime() / this.concedeToDiscountingFactor),
									3);
				} else {
					// this.utilitythreshold = (maximumOfBid *
					// this.discountingFactor) /
					// Math.pow(this.discountingFactor, timeline.getTime());
					this.utilitythreshold = maximumOfBid
							- (maximumOfBid - 0.68)
							* Math.pow(timeline.getTime(),
									this.discountingFactor * 6);
				}

			} else if (this.discountingFactor >= 0.0
					&& this.reservationValue >= 0.5) {

				if (timeline.getTime() <= this.concedeToDiscountingFactor) {
					double minThreshold = (maximumOfBid * this.discountingFactor)
							/ Math.pow(this.discountingFactor,
									this.concedeToDiscountingFactor);

					if (minThreshold < this.reservationValue)
						minThreshold = this.reservationValue;

					this.utilitythreshold = maximumOfBid
							- (maximumOfBid - minThreshold)
							* Math.pow(
									(timeline.getTime() / this.concedeToDiscountingFactor),
									2);
				} else {
					// this.utilitythreshold = (maximumOfBid *
					// this.discountingFactor) /
					// Math.pow(this.discountingFactor, timeline.getTime());
					this.utilitythreshold = maximumOfBid
							- (maximumOfBid - 0.67)
							* Math.pow(timeline.getTime(),
									this.discountingFactor * 2);
				}

			} else if (this.discountingFactor >= 0.0
					&& this.reservationValue >= 0.25
					&& this.reservationValue < 0.5) {

				if (timeline.getTime() <= this.concedeToDiscountingFactor) {
					double minThreshold = (maximumOfBid * this.discountingFactor)
							/ Math.pow(this.discountingFactor,
									this.concedeToDiscountingFactor);

					if (minThreshold < this.reservationValue)
						minThreshold = this.reservationValue;

					this.utilitythreshold = maximumOfBid
							- (maximumOfBid - minThreshold)
							* Math.pow(
									(timeline.getTime() / this.concedeToDiscountingFactor),
									2);
				} else {
					// this.utilitythreshold = (maximumOfBid *
					// this.discountingFactor) /
					// Math.pow(this.discountingFactor, timeline.getTime());
					this.utilitythreshold = maximumOfBid
							- (maximumOfBid - 0.64)
							* Math.pow(timeline.getTime(),
									this.discountingFactor * 2);
				}

			} else if (this.discountingFactor >= 0.0
					&& this.reservationValue < 0.25) {

				if (timeline.getTime() <= this.concedeToDiscountingFactor) {
					double minThreshold = (maximumOfBid * this.discountingFactor)
							/ Math.pow(this.discountingFactor,
									this.concedeToDiscountingFactor);

					if (minThreshold < this.reservationValue)
						minThreshold = this.reservationValue;

					this.utilitythreshold = maximumOfBid
							- (maximumOfBid - minThreshold)
							* Math.pow(
									(timeline.getTime() / this.concedeToDiscountingFactor),
									2);
				} else {
					// this.utilitythreshold = (maximumOfBid *
					// this.discountingFactor) /
					// Math.pow(this.discountingFactor, timeline.getTime());
					this.utilitythreshold = maximumOfBid
							- (maximumOfBid - 0.62)
							* Math.pow(timeline.getTime(),
									this.discountingFactor * 1.5);
				}

			}

			if (this.utilitythreshold < minimumOfBid) {
				this.utilitythreshold = minimumOfBid;
			} else {
				minimumOfBid = this.utilitythreshold;
			}

			/*
			 * if(minimumOfBid < 0.9 && this.guessOpponentType == false){
			 * if(this.opponentBidHistory.getSize() <= 2){ this.opponentType =
			 * 1;//tough opponent alpha1 = 2; } else{ this.opponentType = 0;
			 * alpha1 = 4; } this.guessOpponentType = true;//we only guess the
			 * opponent type once here System.out.println("we guess the opponent
			 * type is "+this.opponentType); }
			 */

			// choose from the opponent bid history first to reduce calculation
			// time
			Bid bestBidOfferedByOpponent = opponentBidHistory
					.getBestBidInHistory();

			if (bestBidOfferedByOpponent == null) {
				System.out.println("best opp bid is null");
			} else {

				if (getUtility(bestBidOfferedByOpponent) >= this.utilitythreshold
						|| getUtility(bestBidOfferedByOpponent) >= minimumOfBid) {
					// System.out.println("test if the comparison is ok");
					return bestBidOfferedByOpponent;
				}
			}

			bidReturned = genRanBid(minimumOfBid, maximumOfBid);

			/*
			 * List<Bid> candidateBids =
			 * this.getBidsBetweenUtility(minimumOfBid, maximumOfBid);
			 * 
			 * if ( candidateBids.size() != 0 ){ bidReturned =
			 * opponentBidHistory.ChooseBid(candidateBids,
			 * this.utilitySpace.getDomain()); }else{ bidReturned =
			 * this.ownBidHistory.getLastBid(); }
			 */

			if (bidReturned == null) {
				System.out.println("no bid is searched warning");
				bidReturned = this.utilitySpace.getMaxUtilityBid();
			}
		} catch (Exception e) {
			System.out.println(e.getMessage()
					+ "exception in method BidToOffer");
			bidReturned = this.bid_maximum_utility;
			return bidReturned;
		}
		// System.out.println("the current threshold is " +
		// this.utilitythreshold + " with the value of alpha1 is  " + alpha1);
		return bidReturned;
	}

	/*
	 * decide whether to accept the current offer or not
	 */
	private boolean AcceptOpponentOffer(Bid opponentBid, Bid ownBid) {
		double currentUtility = 0;
		double nextRoundUtility = 0;
		double maximumUtility = 0;
		this.concedeToOpponent = false;

		if (opponentBid == null || ownBid == null)
			return false;

		try {
			// currentUtility = this.utilitySpace.getUtility(opponentBid);
			currentUtility = getUtility(opponentBid);
			maximumUtility = this.MaximumUtility;// utilitySpace.getUtility(utilitySpace.getMaxUtilityBid());
		} catch (Exception e) {
			System.out.println(e.getMessage()
					+ "Exception in method AcceptOpponentOffer part 1");
		}
		try {
			nextRoundUtility = this.utilitySpace.getUtility(ownBid);
		} catch (Exception e) {
			System.out.println(e.getMessage()
					+ "Exception in method AcceptOpponentOffer part 2");
		}
		// System.out.println(this.utilitythreshold +"at time "+
		// timeline.getTime());
		if (currentUtility >= this.utilitythreshold
				|| currentUtility >= nextRoundUtility) {
			return true;
		} else {
			// if the current utility with discount is larger than the predicted
			// maximum utility with discount
			// then accept it.
			double predictMaximumUtility = maximumUtility
					* this.discountingFactor;
			// double currentMaximumUtility =
			// this.utilitySpace.getUtilityWithDiscount(opponentBidHistory.chooseBestFromHistory(utilitySpace),
			// timeline);
			double currentMaximumUtility = this.utilitySpace
					.getUtilityWithDiscount(
							opponentBidHistory.getBestBidInHistory(), timeline);
			if (currentMaximumUtility > predictMaximumUtility
					&& timeline.getTime() > this.concedeToDiscountingFactor) {
				try {
					// if the current offer is approximately as good as the best
					// one in the history, then accept it.
					if (utilitySpace.getUtility(opponentBid) >= utilitySpace
							.getUtility(opponentBidHistory
									.getBestBidInHistory()) - 0.01) {
						System.out
								.println("he offered me "
										+ currentMaximumUtility
										+ " we predict we can get at most "
										+ predictMaximumUtility
										+ "we concede now to avoid lower payoff due to conflict");
						return true;
					} else {
						this.concedeToOpponent = true;
						return false;
					}
				} catch (Exception e) {
					System.out
							.println("exception in Method AcceptOpponentOffer");
					return true;
				}
				// retrieve the opponent's biding history and utilize it
			} else if (currentMaximumUtility > this.utilitythreshold
					* Math.pow(this.discountingFactor, timeline.getTime())) {
				try {
					// if the current offer is approximately as good as the best
					// one in the history, then accept it.
					if (utilitySpace.getUtility(opponentBid) >= utilitySpace
							.getUtility(opponentBidHistory
									.getBestBidInHistory()) - 0.01) {
						return true;
					} else {
						System.out.println("test"
								+ utilitySpace.getUtility(opponentBid)
								+ this.utilitythreshold);
						this.concedeToOpponent = true;
						return false;
					}
				} catch (Exception e) {
					System.out
							.println("exception in Method AcceptOpponentOffer");
					return true;
				}
			} else {
				return false;
			}
		}
	}

	/*
	 * decide whether or not to terminate now
	 */
	private boolean TerminateCurrentNegotiation(Bid ownBid) {
		double currentUtility = 0;
		double nextRoundUtility = 0;
		double maximumUtility = 0;
		this.concedeToOpponent = false;

		if (ownBid == null)
			return false;

		try {
			currentUtility = this.reservationValue;
			nextRoundUtility = this.utilitySpace.getUtility(ownBid);
			maximumUtility = this.MaximumUtility;
		} catch (Exception e) {
			System.out.println(e.getMessage()
					+ "Exception in method TerminateCurrentNegotiation part 1");
		}

		if (currentUtility >= this.utilitythreshold
				|| currentUtility >= nextRoundUtility) {
			return true;
		} else {
			// if the current reseravation utility with discount is larger than
			// the predicted maximum utility with discount
			// then terminate the negotiation.
			double predictMaximumUtility = maximumUtility
					* this.discountingFactor;
			double currentMaximumUtility = this.utilitySpace
					.getReservationValueWithDiscount(timeline);
			// System.out.println("the current reserved value is "+
			// this.reservationValue+" after discounting is "+currentMaximumUtility);
			if (currentMaximumUtility > predictMaximumUtility
					&& timeline.getTime() > this.concedeToDiscountingFactor) {
				return true;
			} else {
				return false;
			}
		}
	}

	/*
	 * estimate the number of rounds left before reaching the deadline @param
	 * opponent @return
	 */

	private int estimateRoundLeft(boolean opponent) {
		double round;
		if (opponent == true) {
			if (this.timeLeftBefore - this.timeLeftAfter > this.maximumTimeOfOpponent) {
				this.maximumTimeOfOpponent = this.timeLeftBefore
						- this.timeLeftAfter;
			}
		} else {
			if (this.timeLeftAfter - this.timeLeftBefore > this.maximumTimeOfOwn) {
				this.maximumTimeOfOwn = this.timeLeftAfter
						- this.timeLeftBefore;
			}
		}
		if (this.maximumTimeOfOpponent + this.maximumTimeOfOwn == 0) {
			System.out.println("divided by zero exception");
		}
		round = (this.totalTime - timeline.getCurrentTime())
				/ (this.maximumTimeOfOpponent + this.maximumTimeOfOwn);
		// System.out.println("current time is " + timeline.getElapsedSeconds()
		// + "---" + round + "----" + this.maximumTimeOfOpponent);
		return ((int) (round));
	}

	/*
	 * pre-processing to save the computational time each round
	 */
	private void calculateBidsBetweenUtility() {
		BidIterator myBidIterator = new BidIterator(
				this.utilitySpace.getDomain());

		try {
			// double maximumUtility =
			// utilitySpace.getUtility(utilitySpace.getMaxUtilityBid());
			double maximumUtility = this.MaximumUtility;
			double minUtility = this.minimumUtilityThreshold;
			int maximumRounds = (int) ((maximumUtility - minUtility) / 0.01);
			// initalization for each arraylist storing the bids between each
			// range
			for (int i = 0; i < maximumRounds; i++) {
				ArrayList<Bid> BidList = new ArrayList<Bid>();
				// BidList.add(this.bid_maximum_utility);
				this.bidsBetweenUtility.add(BidList);
			}
			this.bidsBetweenUtility.get(maximumRounds - 1).add(
					this.bid_maximum_utility);
			// note that here we may need to use some trick to reduce the
			// computation cost (to be checked later);
			// add those bids in each range into the corresponding arraylist
			int limits = 0;
			if (this.maximumOfBid < 20000) {
				while (myBidIterator.hasNext()) {
					Bid b = myBidIterator.next();
					for (int i = 0; i < maximumRounds; i++) {
						if (utilitySpace.getUtility(b) <= (i + 1) * 0.01
								+ minUtility
								&& utilitySpace.getUtility(b) >= i * 0.01
										+ minUtility) {
							this.bidsBetweenUtility.get(i).add(b);
							break;
						}
					}
					// limits++;
				}
			} else {
				while (limits <= 20000) {
					Bid b = this.RandomSearchBid();
					for (int i = 0; i < maximumRounds; i++) {
						if (utilitySpace.getUtility(b) <= (i + 1) * 0.01
								+ minUtility
								&& utilitySpace.getUtility(b) >= i * 0.01
										+ minUtility) {
							this.bidsBetweenUtility.get(i).add(b);
							break;
						}
					}
					limits++;
				}
			}
		} catch (Exception e) {
			System.out.println("Exception in calculateBidsBetweenUtility()");
			e.printStackTrace();
		}
	}

	private Bid RandomSearchBid() throws Exception {
		HashMap<Integer, Value> values = new HashMap<Integer, Value>();
		ArrayList<Issue> issues = utilitySpace.getDomain().getIssues();
		Random random = new Random();
		Bid bid = null;

		for (Issue lIssue : issues) {
			switch (lIssue.getType()) {
			case DISCRETE:
				IssueDiscrete lIssueDiscrete = (IssueDiscrete) lIssue;
				int optionIndex = random.nextInt(lIssueDiscrete
						.getNumberOfValues());
				values.put(lIssue.getNumber(),
						lIssueDiscrete.getValue(optionIndex));
				break;
			case REAL:
				IssueReal lIssueReal = (IssueReal) lIssue;
				int optionInd = random.nextInt(lIssueReal
						.getNumberOfDiscretizationSteps() - 1);
				values.put(
						lIssueReal.getNumber(),
						new ValueReal(lIssueReal.getLowerBound()
								+ (lIssueReal.getUpperBound() - lIssueReal
										.getLowerBound())
								* (double) (optionInd)
								/ (double) (lIssueReal
										.getNumberOfDiscretizationSteps())));
				break;
			case INTEGER:
				IssueInteger lIssueInteger = (IssueInteger) lIssue;
				int optionIndex2 = lIssueInteger.getLowerBound()
						+ random.nextInt(lIssueInteger.getUpperBound()
								- lIssueInteger.getLowerBound());
				values.put(lIssueInteger.getNumber(), new ValueInteger(
						optionIndex2));
				break;
			default:
				throw new Exception("issue type " + lIssue.getType()
						+ " not supported");
			}
		}
		bid = new Bid(utilitySpace.getDomain(), values);
		return bid;
	}

	/*
	 * Get all the bids within a given utility range.
	 */
	private List<Bid> getBidsBetweenUtility(double lowerBound, double upperBound) {
		List<Bid> bidsInRange = new ArrayList<Bid>();
		try {
			int range = (int) ((upperBound - this.minimumUtilityThreshold) / 0.01);
			int initial = (int) ((lowerBound - this.minimumUtilityThreshold) / 0.01);
			// System.out.println(range+"---"+initial);
			for (int i = initial; i < range; i++) {
				bidsInRange.addAll(this.bidsBetweenUtility.get(i));
			}
			if (bidsInRange.isEmpty()) {
				bidsInRange.add(this.bid_maximum_utility);
			}
		} catch (Exception e) {
			System.out.println("Exception in getBidsBetweenUtility");
			e.printStackTrace();
		}
		return bidsInRange;
	}

	/*
	 * determine the lowest bound of our utility threshold based on the
	 * discounting factor we think that the minimum utility threshold should not
	 * be related with the discounting degree.
	 */
	private void chooseUtilityThreshold() {
		double discountingFactor = this.discountingFactor;
		if (discountingFactor >= 0.9) {
			this.minimumUtilityThreshold = 0;// this.MaximumUtility - 0.09;
		} else {
			// this.minimumUtilityThreshold = 0.85;
			this.minimumUtilityThreshold = 0;// this.MaximumUtility - 0.09;
		}
	}

	/*
	 * determine concede-to-time degree based on the discounting factor.
	 */

	private void chooseConcedeToDiscountingDegree() {
		double alpha = 0;
		double beta = 1.5;// 1.3;//this value controls the rate at which the
							// agent concedes to the discouting factor.
		// the larger beta is, the more the agent makes concesions.
		// if (utilitySpace.getDomain().getNumberOfPossibleBids() > 100) {
		/*
		 * if (this.maximumOfBid > 100) { beta = 2;//1.3; } else { beta = 1.5; }
		 */
		// the vaule of beta depends on the discounting factor (trade-off
		// between concede-to-time degree and discouting factor)
		if (this.discountingFactor > 0.75) {
			beta = 1.8;
		} else if (this.discountingFactor > 0.5) {
			beta = 1.5;
		} else {
			beta = 1.2;
		}
		alpha = Math.pow(this.discountingFactor, beta);
		this.concedeToDiscountingFactor = this.minConcedeToDiscountingFactor
				+ (1 - this.minConcedeToDiscountingFactor) * alpha;
		this.concedeToDiscountingFactor_original = this.concedeToDiscountingFactor;
		System.out.println("concedeToDiscountingFactor is "
				+ this.concedeToDiscountingFactor + "current time is "
				+ timeline.getTime());
	}

	/*
	 * update the concede-to-time degree based on the predicted toughness degree
	 * of the opponent
	 */

	private void updateConcedeDegree() {
		double gama = 10;
		double weight = 0.1;
		double opponnetToughnessDegree = this.opponentBidHistory
				.getConcessionDegree();
		// this.concedeToDiscountingFactor =
		// this.concedeToDiscountingFactor_original * (1 +
		// opponnetToughnessDegree);
		this.concedeToDiscountingFactor = this.concedeToDiscountingFactor_original
				+ weight
				* (1 - this.concedeToDiscountingFactor_original)
				* Math.pow(opponnetToughnessDegree, gama);
		if (this.concedeToDiscountingFactor >= 1) {
			this.concedeToDiscountingFactor = 1;
		}
		// System.out.println("concedeToDiscountingFactor is " +
		// this.concedeToDiscountingFactor + "current time is " +
		// timeline.getTime() + "original concedetodiscoutingfactor is " +
		// this.concedeToDiscountingFactor_original);
	}

	private void updateConcedeDegree2() {
		double gama = 10;
		double weight = 0.1;
		double opponnetToughnessDegree = this.opponentBidHistory2
				.getConcessionDegree();
		// this.concedeToDiscountingFactor =
		// this.concedeToDiscountingFactor_original * (1 +
		// opponnetToughnessDegree);
		this.concedeToDiscountingFactor = this.concedeToDiscountingFactor_original
				+ weight
				* (1 - this.concedeToDiscountingFactor_original)
				* Math.pow(opponnetToughnessDegree, gama);
		if (this.concedeToDiscountingFactor >= 1) {
			this.concedeToDiscountingFactor = 1;
		}
		// System.out.println("concedeToDiscountingFactor is " +
		// this.concedeToDiscountingFactor + "current time is " +
		// timeline.getTime() + "original concedetodiscoutingfactor is " +
		// this.concedeToDiscountingFactor_original);
	}

	private Bid genRanBid(double min, double max) {
		Bid bid = null;

		if (min < this.reservationValue)
			min = this.reservationValue;
		if (max > this.MaximumUtility)
			max = this.MaximumUtility;

		if (this.maximumOfBid <= 5000) {

			int head = 0;
			int rear = 0;

			/*
			 * if ( counter >= relevantBids.size()) counter = 0;
			 * 
			 * ComparableBid temp = relevantBids.get(counter); counter++; newbid
			 * = temp.bid;
			 * 
			 * if ( counter > 50){ newbid =
			 * currhistory.getBestOppBid(currhistory.mapping[0]); }
			 */

			for (int i = 0; relevantBids.get(i).utility > max; i++) {
				head = i;
			}
			// System.out.println("new ran offers! head "+head);
			for (int i = head; relevantBids.get(i).utility > min; i++) {
				rear = i;
			}
			// System.out.println("new ran offers! rear"+rear);
			Random random = new Random();

			int s = 0;

			try {
				s = random.nextInt(rear) % (rear - head + 1) + head;
			} catch (Exception e) {
				s = 0;
				// System.out.println("exception in genRanBid, rear is "+rear);
			}

			// System.out.println("new ran offers! s"+s);

			return relevantBids.get(s).bid;

		} else {

			HashMap<Integer, Value> values = new HashMap<Integer, Value>(); // pairs
			// <issuenumber,chosen
			// value
			// string>
			ArrayList<Issue> issues = utilitySpace.getDomain().getIssues();
			int counter = 0;
			int limit = 1000;
			double fmax = max;
			Random randomnr = new Random();

			do {
				for (Issue lIssue : issues) {
					switch (lIssue.getType()) {
					case DISCRETE:
						IssueDiscrete lIssueDiscrete = (IssueDiscrete) lIssue;
						int optionIndex = randomnr.nextInt(lIssueDiscrete
								.getNumberOfValues());
						values.put(lIssue.getNumber(),
								lIssueDiscrete.getValue(optionIndex));
						break;
					case REAL:
						IssueReal lIssueReal = (IssueReal) lIssue;
						int optionInd = randomnr.nextInt(lIssueReal
								.getNumberOfDiscretizationSteps() - 1);
						values.put(
								lIssueReal.getNumber(),
								new ValueReal(
										lIssueReal.getLowerBound()
												+ (lIssueReal.getUpperBound() - lIssueReal
														.getLowerBound())
												* (double) (optionInd)
												/ (double) (lIssueReal
														.getNumberOfDiscretizationSteps())));
						break;
					case INTEGER:
						IssueInteger lIssueInteger = (IssueInteger) lIssue;
						int optionIndex2 = lIssueInteger.getLowerBound()
								+ randomnr.nextInt(lIssueInteger
										.getUpperBound()
										- lIssueInteger.getLowerBound());
						values.put(lIssueInteger.getNumber(), new ValueInteger(
								optionIndex2));
						break;
					default:
						//
					}
				}

				try {
					bid = new Bid(utilitySpace.getDomain(), values);
				} catch (Exception e) {
					System.out.println("error in generating random bids");
				}

				counter++;
				if (counter > limit) {
					limit = limit + 500;
					fmax += 0.005;
					// return mBidHistory.getMyLastBid();
				}

				if (counter > 5000)
					return ownBidHistory.getLastBid();

			} while (getUtility(bid) < min || getUtility(bid) > fmax);

			return bid;
		}

	}

	private ArrayList<Bid> GetDiscreteBids() {
		ArrayList<Bid> bids = new ArrayList<Bid>();
		HashMap<Integer, Value> issusesFirstValue = new HashMap<Integer, Value>();

		// initial bids list - contains only one bid.
		for (Issue issue : utilitySpace.getDomain().getIssues()) {
			Value v = null;
			if (issue.getType() == ISSUETYPE.INTEGER)
				v = new ValueInteger(((IssueInteger) issue).getLowerBound());
			else if (issue.getType() == ISSUETYPE.REAL)
				v = new ValueReal(((IssueReal) issue).getLowerBound());
			else if (issue.getType() == ISSUETYPE.DISCRETE)
				v = ((IssueDiscrete) issue).getValue(0);
			issusesFirstValue.put(issue.getNumber(), v);
		}
		try {
			bids.add(new Bid(utilitySpace.getDomain(), issusesFirstValue));
		} catch (Exception e) {
			return null;
		}

		for (Issue issue : utilitySpace.getDomain().getIssues()) { // for every
																	// issue
			ArrayList<Bid> tempBids = new ArrayList<Bid>(); // create a list of
															// bids
			ArrayList<Value> issueValues = new ArrayList<Value>();
			if (issue.getType() == ISSUETYPE.DISCRETE) {
				ArrayList<ValueDiscrete> valuesD = (ArrayList<ValueDiscrete>) ((IssueDiscrete) issue)
						.getValues(); // get list of options/values for this
										// issue
				for (Value v : valuesD) {
					issueValues.add(v);
				}
			} else if (issue.getType() == ISSUETYPE.INTEGER) {
				int k = Math.min(10, ((IssueInteger) issue).getUpperBound()
						- ((IssueInteger) issue).getLowerBound());
				for (int i = 0; i <= k; i++) {
					ValueInteger vi = (ValueInteger) GetRepresentorOfBucket(i,
							issue, k, true);
					issueValues.add(vi);
				}
			} else if (issue.getType() == ISSUETYPE.REAL) {
				int k = 10;
				for (int i = 0; i <= k; i++) {
					ValueReal vr = (ValueReal) GetRepresentorOfBucket(i, issue,
							k, false);
					issueValues.add(vr);
				}
			}

			for (Bid bid : bids) { // for each bid seen so far (init bids list)
				for (Value value : issueValues) { // for every value
					HashMap<Integer, Value> bidValues = new HashMap<Integer, Value>(); // make
																						// new
																						// ("empty")
																						// bid
																						// -
																						// only
																						// values.
					for (Issue issue1 : utilitySpace.getDomain().getIssues())
						// go over all issues
						try {
							bidValues.put(issue1.getNumber(),
									bid.getValue(issue1.getNumber())); // each
																		// issue
																		// is
																		// entered
						} catch (Exception e) {
							e.printStackTrace();
						}
					bidValues.put(issue.getNumber(), value);
					try {
						Bid newBid = new Bid(utilitySpace.getDomain(),
								bidValues);
						tempBids.add(newBid);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
			bids = tempBids;
		}
		return bids;
	}

	private Value GetRepresentorOfBucket(int bucket, Issue issue, int k,
			boolean isInteger) {
		double ans = 0;

		if (isInteger) {
			EvaluatorInteger ei = new EvaluatorInteger();
			boolean upperIsTheBest = ei.getEvaluation(((IssueInteger) issue)
					.getUpperBound()) > ei.getEvaluation(((IssueInteger) issue)
					.getLowerBound());
			if (upperIsTheBest) {
				if (bucket < k) {
					ans = ((double) (bucket + 1)) / k;
					ans = ans
							* (((IssueInteger) issue).getUpperBound() - ((IssueInteger) issue)
									.getLowerBound())
							+ ((IssueInteger) issue).getLowerBound() - 1;
				} else
					ans = ((IssueInteger) issue).getUpperBound();
			} else {
				ans = ((double) (bucket)) / k;
				ans = ans
						* (((IssueInteger) issue).getUpperBound() - ((IssueInteger) issue)
								.getLowerBound())
						+ ((IssueInteger) issue).getLowerBound();
			}
			return new ValueInteger((int) Math.round(ans));
		}

		EvaluatorReal ei = new EvaluatorReal();
		boolean upperIsTheBest = ei.getEvaluation(((IssueReal) issue)
				.getUpperBound()) > ei.getEvaluation(((IssueReal) issue)
				.getLowerBound());
		if (upperIsTheBest) {
			if (bucket < k) {
				ans = ((double) (bucket + 1)) / k;
				ans = ans
						* (((IssueReal) issue).getUpperBound() - ((IssueReal) issue)
								.getLowerBound())
						+ ((IssueReal) issue).getLowerBound();
			} else
				ans = ((IssueReal) issue).getUpperBound();
		} else {
			ans = ((double) (bucket)) / k;
			ans = ans
					* (((IssueReal) issue).getUpperBound() - ((IssueReal) issue)
							.getLowerBound())
					+ ((IssueReal) issue).getLowerBound();
		}
		return new ValueReal(ans);

	}

	private void AddOpponentBidToModel(Bid b, boolean isAgreed) {
		// go over every issue of the bid
		for (int i = 0; i < utilitySpace.getDomain().getIssues().size(); i++) {
			// extract the value of that issue
			Object v = null;
			Issue issue = (Issue) utilitySpace.getDomain().getIssue(i);
			Value v1 = null;
			try {
				v1 = b.getValue(issue.getNumber());
			} catch (Exception e) {
				e.printStackTrace();
				return;
			}

			if (issue.getType() == ISSUETYPE.DISCRETE) {
				v = (ValueDiscrete) v1;
			} else if (issue.getType() == ISSUETYPE.INTEGER) {
				// throw the value to the closest bucket
				int currValue = ((ValueInteger) v1).getValue();
				int k = Math.min(10, ((IssueInteger) issue).getUpperBound()
						- ((IssueInteger) issue).getLowerBound());
				int bucket = (int) Math
						.round((double) (currValue - ((IssueInteger) issue)
								.getLowerBound())
								/ (((IssueInteger) issue).getUpperBound() - ((IssueInteger) issue)
										.getLowerBound()) * k);
				v = bucket; // not a real value - it just the "bucket name"
			} else if (issue.getType() == ISSUETYPE.REAL) {
				// throw the value to the closest bucket
				double currValue = ((ValueReal) v1).getValue();
				int k = 10;
				int bucket = (int) Math
						.round((currValue - ((IssueInteger) issue)
								.getLowerBound())
								/ (((IssueInteger) issue).getUpperBound() - ((IssueInteger) issue)
										.getLowerBound()) * k);
				v = bucket + 0.0; // not a real value - it just the
									// "bucket name"
			} else
				return;

			// get the previous data and enter the new one (add 1-t)
			HashMap<Object, Double> hm = opponentUtilityEstimator.get(i);
			Double d = null;
			if (hm.containsKey(v))
				d = hm.get(v);
			else
				d = 0.0;
			double currData;
			if (isAgreed)
				currData = d + (maxValue.get(i) - d) / 2;
			else
				currData = d + 1.0 - timeline.getTime();
			opponentUtilityEstimator.get(i).put(v, currData);

			// set max value (for later normalization)
			if (currData > maxValue.get(i))
				maxValue.set(i, currData);

		}
	}

	private boolean HaveMoreTimeToBid(boolean wantToMakeTwoProposals) {
		if (wantToMakeTwoProposals
				&& 1 - timeline.getTime() > 2 * EstimatedRTT + devRTT) {
			return true;
		}
		if (!wantToMakeTwoProposals
				&& 1 - timeline.getTime() > EstimatedRTT + devRTT) {
			return true;
		}
		return false;
	}

	private double getHardness() {
		double alpha = 0, x = timeline.getTime(), y = utilitySpace
				.getDiscountFactor() <= 0.0 ? 0.0 : 1 - utilitySpace
				.getDiscountFactor();
		double weight = (1 - firstOpponentBidUtility) * 2 / 3;
		alpha = 1 - weight * Math.pow(x, 65) - (1 - weight) * Math.pow(y, 3);
		alpha = alpha / (x * y + 1);

		return alpha;
	}

	private double GetEstimatedOpponentUtility(Bid b) {
		double d = 0.0;
		int count = 0;
		// go over all issues
		for (HashMap<Object, Double> h : opponentUtilityEstimator) {
			try {
				// add the normalize utility of that bid's value
				Issue issue = utilitySpace.getDomain().getIssues().get(count);
				int i = issue.getNumber(); // issue id
				if (issue.getType() == ISSUETYPE.DISCRETE)
					d += h.get((ValueDiscrete) b.getValue(i))
							/ maxValue.get(count);
				else if (issue.getType() == ISSUETYPE.INTEGER) {
					Value v = b.getValue(i);
					ValueInteger vi = (ValueInteger) v;
					d += h.get(vi.getValue()) / maxValue.get(count);
				}

				else if (issue.getType() == ISSUETYPE.REAL) {
					Value v = b.getValue(i);
					ValueReal vr = (ValueReal) v;
					d += h.get(vr.getValue()) / maxValue.get(count);
				}
			} catch (Exception e) {
				return count == 0 ? d : d / (double) count;
			}
			count++;
		}
		// make an average of all utilities (assuming weight is equal).
		return d / (double) count;
	}

}
