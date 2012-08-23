package agents.anac.y2012.AgentMR;

import java.util.*;

import negotiator.*;
import negotiator.actions.*;
import negotiator.issue.*;

/**
 * @author W.Pasman Some improvements over the standard SimpleAgent.
 */
public class AgentMR extends Agent {
	private Action actionOfPartner = null;
	private ArrayList<Bid> bidRunk = new ArrayList<Bid>();
	private ArrayList<Double> observationUtility = new ArrayList<Double>();
	private HashMap<Bid, Double> bidTables = new HashMap<Bid, Double>();
	private static final double MINIMUM_ACCEPT_P = 0.965;
	private static boolean firstOffer;
	private static boolean forecastTime = true;
	private static boolean discountFactor;
	private static double minimumBidUtility;
	private static double minimumOffereDutil;
	private static Bid previousPartnerBid = null;
	private static Bid offereMaxBid = null;
	private static double offereMaxUtility;
	private static double firstOffereUtility;
	private int currentBidNumber = 0;
	private int lastBidNumber = 1;
	private double sigmoidGain;
	private double sigmoidX;
	private double reservation = 0.0;
	private double alpha;
	private double percent;
	private double p = 0.90;

	/**
	 * init is called when a next session starts with the same opponent.
	 */
	public void init() {
		try {
			firstOffer = true;
			getDiscountFactor();
			getReservationFactor();
			updateMinimumBidUtility(0);
			Bid b = utilitySpace.getMaxUtilityBid();
			bidTables.put(b, getUtility(b));
			bidRunk.add(b);
			if (discountFactor) {
				sigmoidGain = -3;
				percent = 0.55;
			} else {
				sigmoidGain = -5;
				percent = 0.70;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static String getVersion() {
		return "1.2";
	}
	
	@Override
	public String getName() {
		return "AgentMR";
	}

	public void ReceiveMessage(Action opponentAction) {
		actionOfPartner = opponentAction;
	}

	public Action chooseAction() {
		Action action = null;

		try {
			if (actionOfPartner == null) {
			
				action = new Offer(getAgentID(),
						utilitySpace.getMaxUtilityBid());
			}
			if (actionOfPartner instanceof Offer) {
				Bid partnerBid = ((Offer) actionOfPartner).getBid();

				// get current time
				double time = timeline.getTime();

				double offeredutil; 
				if (discountFactor) {
					offeredutil = getUtility(partnerBid)
							* (1 / Math.pow(utilitySpace.getDiscountFactor(),
									time));
				} else {
					offeredutil = getUtility(partnerBid);
				}

				if (firstOffer) {
					previousPartnerBid = partnerBid;
					offereMaxBid = partnerBid;
					offereMaxUtility = offeredutil;
					firstOffereUtility = offeredutil;

					observationUtility.add(offeredutil); // addObservation
					if (offeredutil > 0.5) {
						p = 0.90;
					} else {
						p = 0.80;
					}
					firstOffer = !firstOffer;
				}

				updateMinimumBidUtility(time);
				
				if (partnerBid.equals(previousPartnerBid)) {
					if (currentBidNumber > 0 && 0.5 > 0.65) {
						currentBidNumber--;
					}
				}

				if (offeredutil > offereMaxUtility) {
					offereMaxBid = partnerBid;
					offereMaxUtility = offeredutil;
					// addObservation
					observationUtility.add(offeredutil);
					if ((time > 0.5) && !discountFactor) {
						newupdateSigmoidFunction();
					}
				}

				if ((time > 0.5) && forecastTime) {
					updateSigmoidFunction();
					forecastTime = !forecastTime;
				}

				double P = Paccept(offeredutil, time);
				if ((P > MINIMUM_ACCEPT_P) || (offeredutil > minimumBidUtility)
						|| bidRunk.contains(partnerBid)) {
					action = new Accept(getAgentID());
				} else {
					if (offereMaxUtility > minimumBidUtility) {
						//System.out.println("Origional NextBid1: " + offereMaxBid);

						action = new Offer(getAgentID(), offereMaxBid);
					} else if (time > 0.985) {
						
						if (offereMaxUtility > reservation) {
							
							action = new Offer(getAgentID(), offereMaxBid);
						} else {
							action = new Offer(getAgentID(),
									bidRunk.get(bidRunk.size() - lastBidNumber));
							//System.out.println("Origional NextBid3: " + 	bidRunk.get(bidRunk.size() - lastBidNumber));

							lastBidNumber++;
						}
					} else	{	
						
						//System.out.println("Original offeredutil: " + offeredutil);
						//System.out.println("Original getMinimumOffereDutil: " + minimumOffereDutil);
						
						if (offeredutil > minimumOffereDutil) {
						HashMap<Bid, Double> getBids = getBidTable(1);
						
						if (getBids.size() >= 1) {
							currentBidNumber = 0;
							bidRunk.clear();
							bidTables = getBids;
							sortBid(getBids); // Sort BidTable
						} else {
							getBids = getBidTable(2);
							if (getBids.size() >= 1) {
								sortBid(getBids); // Sort BidTable
								Bid maxBid = getMaxBidUtility(getBids);
								currentBidNumber = bidRunk.indexOf(maxBid);
							}
						}
						action = new Offer(getAgentID(),
								bidRunk.get(currentBidNumber));
						//System.out.println("Origional NextBid4: " + bidRunk.get(currentBidNumber));


						//System.out.println("Original Condition1: " + (currentBidNumber + 1 < bidRunk.size()));

						if (currentBidNumber + 1 < bidRunk.size()) {
						//	System.out.println("Origional currentBidNumber1: " + bidRunk.size());

							currentBidNumber++;
						}
					} else {
						HashMap<Bid, Double> getBids = getBidTable(2);
						if (getBids.size() >= 1) {
							sortBid(getBids); // Sort BidTable
							Bid maxBid = getMaxBidUtility(getBids);
							currentBidNumber = bidRunk.indexOf(maxBid);
						}
						//System.out.println("Origional currentBidNumber: " +currentBidNumber);

						action = new Offer(getAgentID(),
								bidRunk.get(currentBidNumber));
						//System.out.println("Origional NextBid5: " + bidRunk.get(currentBidNumber));
						if (currentBidNumber + 1 < bidRunk.size()) {
						//	System.out.println("Original Condition2: " + (currentBidNumber + 1 < bidRunk.size()));

							currentBidNumber++;
						} else {
							currentBidNumber = 0;
						}
					}
				}
				previousPartnerBid = partnerBid;
			}}
		} catch (Exception e) {
			e.printStackTrace();
			action = new Accept(getAgentID());
		}
		return action;
	}

	private void getReservationFactor() {
		if (utilitySpace.getReservationValue() != null) {
			reservation = utilitySpace.getReservationValue();
		}
	}

	private void getDiscountFactor() {
		if (utilitySpace.getDiscountFactor() > 0.0) {
			discountFactor = true;
		} else
			discountFactor = false;
	}

	private void newupdateSigmoidFunction() {
		double latestObservation = observationUtility.get(observationUtility.size() - 1);
		double concessionPercent = Math.abs(latestObservation - firstOffereUtility) / (1.0 - firstOffereUtility);
		double modPercent = Math.abs(minimumOffereDutil - firstOffereUtility) / (1.0 - firstOffereUtility);

		if (modPercent < concessionPercent) {
			percent = concessionPercent;
		}
	}

	private void updateSigmoidFunction() {
		int observationSize = observationUtility.size();
		double latestObservation = observationUtility.get(observationSize - 1); // æœ€æ–°ã�®ç›¸æ‰‹BidUtil
		double concessionPercent = Math.abs(latestObservation - firstOffereUtility) / (1.0 - firstOffereUtility);

		if (discountFactor) {

			if ((concessionPercent < 0.20) ||
					(observationSize < 3)) {
				percent = 0.35;
				sigmoidGain = -2;
			} else {
				percent = 0.45;
			}
		} else {

			if ((concessionPercent < 0.20) ||
					(observationSize < 3)) {
				percent = 0.50;
				sigmoidGain = -4;
			} else if (concessionPercent > 0.60) {
				percent = 0.80;
				sigmoidGain = -6;
			} else {
				percent = 0.60;
			}
		}
	}

	private Bid getMaxBidUtility(HashMap<Bid, Double> bidTable) {
		Double maxBidUtility = 0.0;
		Bid maxBid = null;
		for (Bid b : bidTable.keySet()) {
			if (getUtility(b) > maxBidUtility) {
				maxBidUtility = getUtility(b);
				maxBid = b;
			}
		}
		return maxBid;
	}

	private void updateMinimumBidUtility(double time) {
		alpha = (1.0 - firstOffereUtility) * percent;
		double mbuInfimum = firstOffereUtility + alpha;



		if (mbuInfimum >= 1.0) {
			mbuInfimum = 0.999;
		} else if (mbuInfimum <= 0.70) {
			mbuInfimum = 0.70;
		}
		sigmoidX = 1 - ((1 / sigmoidGain) * Math.log(mbuInfimum / (1 - mbuInfimum)));

		minimumBidUtility = 1 - (1 / (1 + Math.exp(sigmoidGain
				* (time - sigmoidX))));


		if (minimumBidUtility < reservation) {
			minimumBidUtility = reservation;
		}

		minimumOffereDutil =  minimumBidUtility * p;


	}

	/**
	 * BidTableã‚’é™�é †ã‚½ãƒ¼ãƒˆ
	 *
	 * @param bidTable
	 */
	private void sortBid(final HashMap<Bid, Double> getBids) {

		for (Bid bid : getBids.keySet()) {
			bidTables.put(bid, getUtility(bid));
			bidRunk.add(bid); // Add bidRunk
		}

		Collections.sort(bidRunk, new Comparator<Bid>() {
			@Override
			public int compare(Bid o1, Bid o2) {
				return (int) Math.ceil(-(bidTables.get(o1) - bidTables.get(o2)));
			}
		});
	}

	private Bid clone(Bid source) throws Exception {
		HashMap<Integer, Value> hash = new HashMap<Integer, Value>();
		for (Issue i : utilitySpace.getDomain().getIssues()) {
			hash.put(i.getNumber(), source.getValue(i.getNumber()));
		}
		return new Bid(utilitySpace.getDomain(), hash);
	}

	/**
	 * Bid
	 *
	 * @param maxBid
	 * @return
	 * @throws Exception
	 */
	private HashMap<Bid, Double> getBidTable(int flag) throws Exception {
		HashMap<Bid, Double> getBids = new HashMap<Bid, Double>();
		Random randomnr = new Random(200);

		//Random randomnr = new Random();
		ArrayList<Issue> issues = utilitySpace.getDomain().getIssues();
		Bid standardBid = null;

		for (Issue lIssue : issues) {
			switch (lIssue.getType()) {
			case DISCRETE:
				IssueDiscrete lIssueDiscrete = (IssueDiscrete) lIssue;
				for (ValueDiscrete value : lIssueDiscrete.getValues()) {
					if (flag == 0) {
						standardBid = utilitySpace.getMaxUtilityBid();
					} else if (flag == 1) {
						standardBid = ((Offer) actionOfPartner).getBid();
					} else {
						standardBid = bidRunk.get(currentBidNumber);
					}
					standardBid = clone(standardBid);
					standardBid.setValue(lIssue.getNumber(), value);
					double utility = getUtility(standardBid);
					if ((utility > minimumBidUtility)
							&& (!bidRunk.contains(standardBid))) {
						getBids.put(standardBid, utility);
					}
				}
				break;
			case REAL:
				IssueReal lIssueReal = (IssueReal)lIssue;
				int optionInd = randomnr.nextInt(lIssueReal.getNumberOfDiscretizationSteps()-1);
				Value pValue = new ValueReal(lIssueReal.getLowerBound() + (lIssueReal.getUpperBound()-lIssueReal.getLowerBound())*(double)(optionInd)/(double)(lIssueReal.getNumberOfDiscretizationSteps()));
				standardBid.setValue(lIssueReal.getNumber(), pValue);
				double utility = getUtility(standardBid);
				getBids.put(standardBid, utility);
				break;
			case INTEGER:
				IssueInteger lIssueInteger = (IssueInteger)lIssue;
				int optionIndex2 = lIssueInteger.getLowerBound() + randomnr.nextInt(lIssueInteger.getUpperBound()-lIssueInteger.getLowerBound());
				Value pValue2 = new ValueInteger(optionIndex2);
				standardBid.setValue(lIssueInteger.getNumber(), pValue2);
				double utility2 = getUtility(standardBid);
				getBids.put(standardBid, utility2);
				break;
			default: throw new Exception("issue type "+lIssue.getType()+" not supported by AgentMR");
			}
		}

		return getBids;
	}

	/**
	 * This function determines the accept probability for an offer. At t=0 it
	 * will prefer high-utility offers. As t gets closer to 1, it will accept
	 * lower utility offers with increasing probability. it will never accept
	 * offers with utility 0.
	 *
	 * @param u
	 *            is the utility
	 * @param t
	 *            is the time as fraction of the total available time (t=0 at
	 *            start, and t=1 at end time)
	 * @return the probability of an accept at time t
	 * @throws Exception
	 *             if you use wrong values for u or t.
	 *
	 */
	double Paccept(double u, double t1) throws Exception {
		double t = t1 * t1 * t1; // steeper increase when deadline approaches.
		if (u < 0 || u > 1.05)
			throw new Exception("utility " + u + " outside [0,1]");
		// normalization may be slightly off, therefore we have a broad boundary
		// up to 1.05
		if (t < 0 || t > 1)
			throw new Exception("time " + t + " outside [0,1]");
		if (u > 1.)
			u = 1;
		if (t == 0.5)
			return u;
		return (u - 2. * u * t + 2. * (-1. + t + Math.sqrt(sq(-1. + t) + u
				* (-1. + 2 * t))))
				/ (-1. + 2 * t);
	}

	double sq(double x) {
		return x * x;
	}
}
