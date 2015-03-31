package anac2015.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import negotiator.Bid;
import negotiator.Deadline;
import negotiator.Timeline;
import negotiator.actions.Accept;
import negotiator.actions.Action;
import negotiator.actions.Offer;
import negotiator.issue.Issue;
import negotiator.issue.IssueDiscrete;
import negotiator.parties.AbstractTimeDependentNegotiationParty;
import negotiator.utility.UtilitySpace;

/**
 * This is ParsAgent party.
 */
public class ParsAgent extends AbstractTimeDependentNegotiationParty {
	Bid lastBid;
	Action lastAction;
	String oppAName;
	String oppBName;
	int round;
	double myutility = 0.8d;
	boolean Imfirst = false;
	Boolean withDiscount = null;
	boolean fornullAgent = false;
	ArrayList<BidUtility> opponentAB = new ArrayList<BidUtility>();
	OpponentPreferences oppAPreferences = new OpponentPreferences();
	OpponentPreferences oppBPreferences = new OpponentPreferences();

	/**
	 * Please keep this constructor. This is called by genius.
	 *
	 * @param utilitySpace
	 *            Your utility space.
	 * @param deadlines
	 *            The deadlines set for this negotiation.
	 * @param timeline
	 *            Value counting from 0 (start) to 1 (end).
	 * @param randomSeed
	 *            If you use any randomization, use this seed for it.
	 */
	public ParsAgent(UtilitySpace utilitySpace, Deadline deadlines,
			Timeline timeline, long randomSeed) {
		// Make sure that this constructor calls it's parent.
		super(utilitySpace, deadlines, timeline, randomSeed);
		if (deadlines.isTime()) {
			round = deadlines.getTotalTime();
		}
		if (deadlines.isRounds()) {
			round = deadlines.getTotalRounds();
		}
		try {
			Thread.sleep(10 * 1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * Each round this method gets called and ask you to accept or offer. The
	 * first party in the first round is a bit different, it can only propose an
	 * offer.
	 *
	 * @param validActions
	 *            Either a list containing both accept and offer or only offer.
	 * @return The chosen action.
	 */
	@Override
	public Action chooseAction(List<Class> validActions) {
		try {
			Thread.sleep(30 * 1000);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		round--;
		try {
			if (lastBid == null) {
				Imfirst = true;
				Bid b = getMybestBid(utilitySpace.getMaxUtilityBid(), 0);
				return new Offer(getPartyId(), b);
			} else if (round == 0) {
				if (Imfirst) {
					return new Offer(getPartyId(), offerMyNewBid());
				} else if (utilitySpace.getUtility(lastBid) > 0.6d) {
					return new Accept(getPartyId());
				}
			} else {
				if (lastAction instanceof Accept) {
					if (utilitySpace.getUtility(lastBid) > getMyutility()) {
						return new Accept(getPartyId());
					} else {
						Bid b = offerMyNewBid();
						return new Offer(getPartyId(), b);
					}
				} else {
					if (utilitySpace.getUtility(lastBid) > getMyutility()) {
						return new Accept(getPartyId());
					} else {
						Bid b = offerMyNewBid();
						if (utilitySpace.getUtility(b) < getMyutility())
							return new Offer(getPartyId(), getMybestBid(
									utilitySpace.getMaxUtilityBid(), 0));
						else
							return new Offer(getPartyId(), b);

					}
				}
			}
		} catch (Exception e) {
			System.out.println("Error Occured " + e.getMessage());
		}
		Bid mb = null;
		try {
			mb = utilitySpace.getMaxUtilityBid();
			return new Offer(getPartyId(), getMybestBid(mb, 0));
		} catch (Exception e) {
			try {
				return new Offer(getPartyId(), mb);
			} catch (Exception e2) {

			}
		}
		return new Accept(getPartyId());
	}

	/**
	 * All offers proposed by the other parties will be received as a message.
	 * You can use this information to your advantage, for example to predict
	 * their utility.
	 *
	 * @param sender
	 *            The party that did the action.
	 * @param action
	 *            The action that party did.
	 */
	@Override
	public void receiveMessage(Object sender, Action action) {
		// try {
		// Thread.sleep(10 * 1000);
		// } catch (InterruptedException e1) {
		// // TODO Auto-generated catch block
		// e1.printStackTrace();
		// }
		super.receiveMessage(sender, action);
		String agentName = sender.toString();
		// action.getAgent() != null ? action.getAgent().toString() :
		// fornullAgent ? "null1" : "null2";
		fornullAgent = !fornullAgent;
		if (action != null && action instanceof Offer) {
			Bid newBid = ((Offer) action).getBid();
			if (withDiscount == null) {
				try {
					if (utilitySpace.getUtilityWithDiscount(newBid, timeline) != utilitySpace
							.getUtility(newBid)) {
						withDiscount = Boolean.TRUE;
					} else
						withDiscount = Boolean.FALSE;
				} catch (Exception e) {
					System.out.println("Exception " + e.getMessage());
				}
			}
			BidUtility opBid;
			try {
				opBid = new BidUtility(newBid, utilitySpace.getUtility(newBid),
						System.currentTimeMillis());

				if (oppAName != null && oppAName.equals(agentName)) {
					addBidToList(oppAPreferences.getOpponentBids(), opBid);

				} else if (oppBName != null && oppBName.equals(agentName)) {
					addBidToList(oppBPreferences.getOpponentBids(), opBid);
				} else if (oppAName == null) {
					oppAName = agentName;
					oppAPreferences.getOpponentBids().add(opBid);
				} else if (oppBName == null) {
					oppBName = agentName;
					oppBPreferences.getOpponentBids().add(opBid);
				}
				calculateParamForOpponent(
						(oppAName.equals(agentName) ? oppAPreferences
								: oppBPreferences), newBid);
				lastBid = newBid;
			} catch (Exception e) {
				// System.out.println("Exception 33 " + e.getMessage());
			}
		} else if (action != null && action instanceof Accept) {
			BidUtility opBid = null;
			try {
				opBid = new BidUtility(lastBid,
						utilitySpace.getUtility(lastBid),
						System.currentTimeMillis());
			} catch (Exception e) {
				// System.out.println("Exception  44" + e.getMessage());
			}
			addBidToList(opponentAB, opBid);
		}
		lastAction = action;

		// Here you can listen to other parties' messages
	}

	public int MyBestValue(int issueindex) {
		ArrayList<Issue> dissues = utilitySpace.getDomain().getIssues();
		Issue isu = dissues.get(issueindex);
		HashMap map = new HashMap();
		double maxutil = 0d;
		int maxvalIndex = 0;
		try {
			map = utilitySpace.getMaxUtilityBid().getValues();
		} catch (Exception e) {
			System.out.println("Exception 3323  " + e.getMessage());
		}
		if (isu instanceof IssueDiscrete) {
			IssueDiscrete is = (IssueDiscrete) isu;
			for (int num = 0; num < is.getNumberOfValues(); ++num) {
				map.put(new Integer(issueindex + 1), is.getValue(num));
				Bid temp;
				double u = 0d;
				try {
					temp = new Bid(utilitySpace.getDomain(), map);
					u = utilitySpace.getUtility(temp);
				} catch (Exception e) {
					System.out.println("Exception 98989  " + e.getMessage());
				}
				if (u > maxutil) {
					maxutil = u;
					maxvalIndex = num;
				}
				break;

			}
		}
		return maxvalIndex;
	}

	public Bid offerMyNewBid() {
		Bid bidNN = null;
		if (opponentAB != null && opponentAB.size() != 0)
			bidNN = getNNBid(opponentAB);
		try {
			if (bidNN == null
					|| utilitySpace.getUtility(bidNN) < getMyutility()) {
				ArrayList isues = getMutualIssues();
				HashMap map = new HashMap();
				Bid bid;
				ArrayList<Issue> dissues = utilitySpace.getDomain().getIssues();
				for (int i = 0; i < isues.size(); ++i) {
					ArrayList keyVal = (ArrayList) isues.get(i);
					if (keyVal != null
							&& dissues.get(i) instanceof IssueDiscrete) {
						IssueDiscrete is = (IssueDiscrete) dissues.get(i);
						for (int num = 0; num < is.getNumberOfValues(); ++num) {
							if (is.getValue(num).toString()
									.equals(keyVal.get(0).toString())) {
								map.put(new Integer(i + 1), is.getValue(num));
								break;
							}

						}

					} else if (keyVal == null
							&& dissues.get(i) instanceof IssueDiscrete) {
						IssueDiscrete is = (IssueDiscrete) dissues.get(i);
						map.put(new Integer(i + 1), is.getValue(MyBestValue(i)));

					} else if (keyVal != null) {
						map.put(new Integer(i + 1), keyVal.get(0));
					}

				}
				try {
					bid = new Bid(utilitySpace.getDomain(),
							(HashMap) map.clone());
					if (utilitySpace.getUtility(bid) > getMyutility())
						return bid;
					else
						return getMybestBid(utilitySpace.getMaxUtilityBid(), 0);
				} catch (Exception e) {
					System.out.println("Exception 55 " + e.getMessage());
				}
			} else
				return bidNN;
		} catch (Exception e) {
			// System.out.println("Exception 121212 == " + e.getMessage());
		}
		return null;
	}

	public ArrayList getMutualIssues() {
		ArrayList mutualList = new ArrayList();
		ArrayList<Issue> dissues = utilitySpace.getDomain().getIssues();
		int twocycle = 2;
		while (twocycle > 0) {
			mutualList = new ArrayList();
			for (int i = 0; i < dissues.size(); ++i) {
				if (oppAPreferences.getRepeatedissue().get(
						dissues.get(i).getName()) != null) {
					HashMap vals = (HashMap) oppAPreferences.getRepeatedissue()
							.get(dissues.get(i).getName());
					HashMap valsB = (HashMap) oppBPreferences
							.getRepeatedissue().get(dissues.get(i).getName());

					Object[] keys = vals.keySet().toArray();
					int[] max = new int[] { 0, 0 };
					Object[] maxkey = new Object[] { null, null };
					for (int j = 0; j < keys.length; ++j) {
						Integer temp = (Integer) vals.get(keys[j]);
						if (temp.intValue() > max[0]) {
							max[0] = temp.intValue();
							maxkey[0] = keys[j];
						} else if (temp.intValue() > max[1]) {
							max[1] = temp.intValue();
							maxkey[1] = keys[j];
						}
					}
					if (valsB != null) {
						Object[] keysB = valsB.keySet().toArray();
						int[] maxB = new int[] { 0, 0 };
						;
						Object[] maxkeyB = new Object[] { null, null };
						for (int j = 0; j < keysB.length; ++j) {
							Integer temp = (Integer) valsB.get(keysB[j]);
							if (temp.intValue() > maxB[0]) {
								maxB[0] = temp.intValue();
								maxkeyB[0] = keysB[j];
							} else if (temp.intValue() > maxB[1]) {
								maxB[1] = temp.intValue();
								maxkeyB[1] = keysB[j];
							}
						}
						if (twocycle == 2) {
							if (maxkey[0] != null && maxkeyB[0] != null
									&& maxkey[0].equals(maxkeyB[0])) {
								ArrayList l = new ArrayList();
								l.add(maxkey[0]);
								l.add(maxB[0]);
								l.add(max[0]);
								mutualList.add(i, l);
							} else
								mutualList.add(i, null);
						} else {
							boolean insideloop = true;
							for (int m = 0; insideloop && m < 2; ++m) {
								for (int z = 0; insideloop && z < 2; ++z) {
									if (maxkey[m] != null && maxkeyB[z] != null
											&& maxkey[m].equals(maxkeyB[z])) {
										ArrayList l = new ArrayList();
										l.add(maxkey[m]);
										l.add(maxB[z]);
										l.add(max[m]);
										mutualList.add(i, l);
										insideloop = false;
									}
								}
							}
							if (insideloop)
								mutualList.add(i, null);

						}
					} else {
						mutualList.add(i, null);
						oppBPreferences.getRepeatedissue().put(
								dissues.get(i).getName(), new HashMap());
					}
				} else {
					oppAPreferences.getRepeatedissue().put(
							dissues.get(i).getName(), new HashMap());
					mutualList.add(i, null);
				}
				if (((HashMap) oppAPreferences.getRepeatedissue().get(
						dissues.get(i).getName())).size() == 0
						|| ((HashMap) oppAPreferences.getRepeatedissue().get(
								dissues.get(i).getName())).size() == 0) {
					twocycle--;
				}
			}
			if (twocycle != 0) {
				twocycle--;
				if (opponentAB.size() == 0) {
					float nullval = 0.0f;
					for (int i = 0; i < mutualList.size(); ++i) {
						if (mutualList.get(i) != null) {
							++nullval;
						}
					}

					nullval = nullval / mutualList.size();
					if (nullval >= 0.5)
						twocycle--;
				} else
					twocycle--;
			}
		}
		return mutualList;
	}

	public Bid getNNBid(ArrayList<BidUtility> oppAB) {
		ArrayList<Issue> dissues = utilitySpace.getDomain().getIssues();
		Bid maxBid = null;
		double maxutility = 0d;
		int size = 0;
		int exloop = 0;
		Bid newBid;
		while (exloop < dissues.size()) {
			int bi = chooseBestIssue();
			size = 0;
			while (oppAB != null && oppAB.size() > size) {
				Bid b = oppAB.get(size).getBid();
				newBid = new Bid(b);
				try {
					HashMap vals = b.getValues();
					vals.put(bi, getRandomValue(dissues.get(bi - 1)));
					newBid = new Bid(utilitySpace.getDomain(), vals);
					if (utilitySpace.getUtility(newBid) > getMyutility()
							&& utilitySpace.getUtility(newBid) > maxutility) {
						maxBid = new Bid(newBid);
						maxutility = utilitySpace.getUtility(maxBid);
					}

				} catch (Exception e) {
					System.out.println("Exception 66 " + e.getMessage());
				}
				size++;
			}
			exloop++;
		}
		return maxBid;
	}

	public int chooseBestIssue() {
		double random = Math.random();
		double sumWeight = 0d;

		for (int i = utilitySpace.getDomain().getIssues().size(); i > 0; --i) {
			sumWeight += utilitySpace.getWeight(i);
			if (sumWeight > random)
				return i;
		}
		return 0;
	}

	public int chooseWorstIssue() {
		double random = Math.random() * 100;
		double sumWeight = 0d;
		int minin = 1;
		double min = 1.0d;
		for (int i = utilitySpace.getDomain().getIssues().size(); i > 0; --i) {
			sumWeight += 1.0d / utilitySpace.getWeight(i);
			if (utilitySpace.getWeight(i) < min) {
				min = utilitySpace.getWeight(i);
				minin = i;
			}
			if (sumWeight > random)
				return i;
		}
		return minin;
	}

	public Bid getMybestBid(Bid sugestBid, int time) {
		ArrayList<Issue> dissues = utilitySpace.getDomain().getIssues();
		Bid newBid = new Bid(sugestBid);
		int index = chooseWorstIssue();
		boolean loop = true;
		while (loop) {
			newBid = new Bid(sugestBid);
			try {
				HashMap map = newBid.getValues();
				map.put(index, getRandomValue(dissues.get(index - 1)));
				newBid = new Bid(utilitySpace.getDomain(), map);
				if (utilitySpace.getUtility(newBid) > getMyutility()) {
					return newBid;
				}
			} catch (Exception e) {
				// System.out.println("Exception in my best bid : " +
				// e.getMessage());
				loop = false;
			}
		}
		return newBid;
	}

	public void addBidToList(ArrayList<BidUtility> mybids, BidUtility newbid) {
		int index = mybids.size();
		for (int i = 0; i < mybids.size(); ++i) {
			if (mybids.get(i).getUtility() <= newbid.getUtility()) {
				if (!mybids.get(i).getBid().equals(newbid.getBid()))
					index = i;
				else
					return;
			}
		}
		mybids.add(index, newbid);

	}

	public void calculateParamForOpponent(OpponentPreferences op, Bid bid) {
		ArrayList<Issue> dissues = utilitySpace.getDomain().getIssues();
		HashMap bidVal = bid.getValues();
		Object[] keys = bidVal.keySet().toArray();

		for (int i = 0; i < dissues.size(); ++i) {
			if (op.getRepeatedissue().get(dissues.get(i).getName()) != null) {

				HashMap vals = (HashMap) op.getRepeatedissue().get(
						dissues.get(i).getName());

				try {
					if (vals.get(bidVal.get(keys[i])) != null) {
						Integer repet = (Integer) vals.get(bidVal.get(keys[i]));
						repet = repet + 1;
						vals.put(bidVal.get(keys[i]), repet);
					} else {
						vals.put(bidVal.get(keys[i]), new Integer(1));
					}
				} catch (Exception e) {
					// System.out.println("Exception 88 " + e.getMessage());
				}
			} else {
				HashMap h = new HashMap();
				// op.getRepeatedissue().get(dissues.get(i).getName());
				try {

					h.put(bidVal.get(keys[i]), new Integer(1));
				} catch (Exception e) {
					// System.out.println("Exception 99 " + e.getMessage());
				}
				op.getRepeatedissue().put(dissues.get(i).getName(), h);
			}
		}

	}

	public void setMyutility(double myutility) {
		this.myutility = myutility;
	}

	public double getMyutility() {
		myutility = getTargetUtility();
		if (myutility < 0.7)
			return 0.7d;
		return myutility;
	}

	public double getE() {
		if (withDiscount)
			return 0.25d;
		return 0.19d;
	}

	private class OpponentPreferences {
		private HashMap repeatedissue = new HashMap();
		private ArrayList selectedValues;
		ArrayList<BidUtility> opponentBids = new ArrayList<BidUtility>();

		public void setRepeatedissue(HashMap repeatedissue) {
			this.repeatedissue = repeatedissue;
		}

		public HashMap getRepeatedissue() {
			return repeatedissue;
		}

		public void setSelectedValues(ArrayList selectedValues) {
			this.selectedValues = selectedValues;
		}

		public ArrayList getSelectedValues() {
			return selectedValues;
		}

		public void setOpponentBids(ArrayList<BidUtility> opponentBids) {
			this.opponentBids = opponentBids;
		}

		public ArrayList<BidUtility> getOpponentBids() {
			return opponentBids;
		}
	}

	private class BidUtility {
		private Bid bid;
		private double utility;
		private long time;

		BidUtility(Bid b, double u, long t) {
			this.bid = b;
			this.utility = u;
			this.time = t;
		}

		BidUtility(BidUtility newbid) {
			this.bid = newbid.getBid();
			this.utility = newbid.getUtility();
			this.time = newbid.getTime();
		}

		public void setBid(Bid bid) {
			this.bid = bid;
		}

		public Bid getBid() {
			return bid;
		}

		public void setUtility(double utility) {
			this.utility = utility;
		}

		public double getUtility() {
			return utility;
		}

		public void setTime(long time) {
			this.time = time;
		}

		public long getTime() {
			return time;
		}
	}

}
