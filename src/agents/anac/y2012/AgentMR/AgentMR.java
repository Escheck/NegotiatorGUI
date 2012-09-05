package agents.anac.y2012.AgentMR;

import java.util.*;

import negotiator.*;
import negotiator.actions.*;
import negotiator.issue.*;

/**
 * @author W.Pasman Some improvements over the standard SimpleAgent.
 */
public class AgentMR extends Agent {
	
	private boolean EQUIVALENCE_TEST = true;
	private Random random100;
	
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
			//System.out.println("Original minimumBidUtility1: " + minimumBidUtility);
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
			if(EQUIVALENCE_TEST){
				random100 = new Random(100);
			} else {
				random100 = new Random();
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
				//System.out.println("Original partnerBid: " + partnerBid);

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

				// Ã¥Ë†ï¿½Ã¥â€ºÅ¾Ã£â€šÂªÃ£Æ’â€¢Ã£â€šÂ¡Ã£Æ’Â¼Ã¥â€¡Â¦Ã§ï¿½â€ 
				if (firstOffer) {
					//System.out.println("Original partnerBid: " + partnerBid);
					//System.out.println("Original offeredutil: " + offeredutil);

					previousPartnerBid = partnerBid;
					offereMaxBid = partnerBid; 
					offereMaxUtility = offeredutil; 
					firstOffereUtility = offeredutil; 
					//System.out.println("Original firstOffereUtility: " + firstOffereUtility);

					observationUtility.add(offeredutil); // addObservation
					if (offeredutil > 0.5) { 
						p = 0.90;
					} else {
						p = 0.80;
					}
					firstOffer = !firstOffer; // Ã¥Ë†ï¿½Ã¥â€ºÅ¾Ã£â€šÂªÃ£Æ’â€¢Ã£â€šÂ¡Ã£Æ’Â¼Ã¥â€¡Â¦Ã§ï¿½â€ Ã§Âµâ€šÃ¤Âºâ€ 
				}

				updateMinimumBidUtility(time); // Ã©â€“Â¾Ã¥â‚¬Â¤(MBU)Ã¥â€¡Â¦Ã§ï¿½â€ 

				// BidÃ§Â§Â»Ã¥â€¹â€¢Ã¥â€¡Â¦Ã§ï¿½â€ 
				if (partnerBid.equals(previousPartnerBid)) { // Ã§â€ºÂ¸Ã¦â€°â€¹Ã£ï¿½Â®Ã¦ï¿½ï¿½Ã§Â¤ÂºÃ£ï¿½Å’Ã¥Â¤â€°Ã£â€šï¿½Ã£â€šâ€°Ã£ï¿½ÂªÃ£ï¿½â€žÃ¥Â Â´Ã¥ï¿½Ë†
					if (currentBidNumber > 0 && 0.5 > 0.65) {
						currentBidNumber--; // Ã§Â¢ÂºÃ§Å½â€¡Ã§Å¡â€žÃ£ï¿½Â«BidÃ£â€šâ€™Ã§Â§Â»Ã¥â€¹â€¢
					}
				}

				// Ã§â€ºÂ¸Ã¦â€°â€¹Ã£ï¿½Â®Ã¦Å“â‚¬Ã¥Â¤Â§BidÃ£â€šâ€™Ã¦â€ºÂ´Ã¦â€“Â°
				if (offeredutil > offereMaxUtility) {
					offereMaxBid = partnerBid;
					offereMaxUtility = offeredutil;
					// addObservation
					observationUtility.add(offeredutil);
					if ((time > 0.5) && !discountFactor) {
						newupdateSigmoidFunction();
					}
				}

				// forecastingÃ¥â€¡Â¦Ã§ï¿½â€ 
				if ((time > 0.5) && forecastTime) {
					updateSigmoidFunction();
					forecastTime = !forecastTime;
				}

				double P = Paccept(offeredutil, time);
				//System.out.println("Orig condition1: " + (P > MINIMUM_ACCEPT_P));
				//System.out.println("Orig condition2: " + (offeredutil > minimumBidUtility));
				//System.out.println("Orig condition3: " + bidRunk.contains(partnerBid));

				
				// AcceptÃ¥â€¡Â¦Ã§ï¿½â€ 
				// 1. AcceptPÃ£ï¿½Â®Ã¤Â¸â€¹Ã©â„¢ï¿½Ã£â€šâ€™Ã¨Â¶â€¦Ã£ï¿½Ë†Ã£ï¿½Å¸Ã¥Â Â´Ã¥ï¿½Ë†
				// 2. Ã©â€“Â¾Ã¥â‚¬Â¤(MBU)Ã£â€šâ€™Ã¨Â¶â€¦Ã£ï¿½Ë†Ã£ï¿½Å¸BidÃ£ï¿½Å’Ã¦ï¿½ï¿½Ã§Â¤ÂºÃ£ï¿½â€¢Ã£â€šÅ’Ã£ï¿½Å¸Ã¥Â Â´Ã¥ï¿½Ë†
				// 3. Ã¦â€”Â¢Ã£ï¿½Â«Ã£ï¿½â€œÃ£ï¿½Â¡Ã£â€šâ€°Ã£ï¿½Å’BidÃ£â€šâ€™Ã£ï¿½â€”Ã£ï¿½Å¸BidÃ£ï¿½Å’Ã¦ï¿½ï¿½Ã§Â¤ÂºÃ£ï¿½â€¢Ã£â€šÅ’Ã£ï¿½Å¸Ã¥Â Â´Ã¥ï¿½Ë†
				if ((P > MINIMUM_ACCEPT_P) || (offeredutil > minimumBidUtility)
						|| bidRunk.contains(partnerBid)) {
					action = new Accept(getAgentID());
				} else {
					// Ã§â€ºÂ¸Ã¦â€°â€¹Ã¦ï¿½ï¿½Ã§Â¤ÂºÃ£ï¿½Â®Ã¦Å“â‚¬Ã¥Â¤Â§BidUtiltyÃ£ï¿½Å’MBUÃ¤Â»Â¥Ã¤Â¸Å Ã£ï¿½ÂªÃ£â€šâ€°Ã£ï¿½ï¿½Ã£â€šÅ’Ã£â€šâ€™BidÃ£ï¿½â€”Ã§Â¶Å¡Ã£ï¿½â€˜Ã£â€šâ€¹
					// Ã¦â€”Â©Ã¦Å“Å¸Ã¥ï¿½Ë†Ã¦â€žï¿½Ã¥Â½Â¢Ã¦Ë†ï¿½Ã£â€šâ€™Ã¥â€ºÂ³Ã£â€šâ€¹
					if (offereMaxUtility > minimumBidUtility) {
						//System.out.println("Origional NextBid1: " + offereMaxBid);

						action = new Offer(getAgentID(), offereMaxBid);
					}
					// Ã¦Å“â‚¬Ã§Âµâ€šBidÃ¥â€¡Â¦Ã§ï¿½â€ 
					// Ã§â€¢â„¢Ã¤Â¿ï¿½Ã¤Â¾Â¡Ã¦Â Â¼Ã£ï¿½Å’Ã£ï¿½ÂªÃ£ï¿½â€˜Ã£â€šÅ’Ã£ï¿½Â°Ã¥ï¿½Ë†Ã¦â€žï¿½Ã¥Â½Â¢Ã¦Ë†ï¿½Ã£â€šâ€™Ã¦Å“â‚¬Ã¥â€žÂªÃ¥â€¦Ë†Ã£ï¿½Â¨Ã£ï¿½â„¢Ã£â€šâ€¹
					else if (time > 0.985) {
						// Ã§â€ºÂ¸Ã¦â€°â€¹Ã£ï¿½Â®Ã¦Å“â‚¬Ã¥Â¤Â§BidÃ£â€šâ€™BidÃ£ï¿½â€”Ã£â‚¬ï¿½Ã¥ï¿½Ë†Ã¦â€žï¿½Ã¥Â½Â¢Ã¦Ë†ï¿½
						if (offereMaxUtility > reservation) {
							//System.out.println("Origional NextBid2: " + offereMaxBid);

							action = new Offer(getAgentID(), offereMaxBid);
						} else { // Ã§â€ºÂ¸Ã¦â€°â€¹Ã£ï¿½Â®Ã¦Å“â‚¬Ã¥Â¤Â§BidUtilÃ£ï¿½Å’Ã§â€¢â„¢Ã¤Â¿ï¿½Ã¤Â¾Â¡Ã¦Â Â¼Ã¤Â»Â¥Ã¤Â¸â€¹Ã£ï¿½ÂªÃ£â€šâ€°Ã¨â€¡ÂªÃ¥Ë†â€ Ã£ï¿½Â®Ã¦Å“â‚¬Ã¥Â¤Â§Ã£ï¿½Â®Ã¨Â­Â²Ã¦Â­Â©BidÃ£â€šâ€™Ã¨Â¡Å’Ã£ï¿½â€ 
							action = new Offer(getAgentID(),
									bidRunk.get(bidRunk.size() - lastBidNumber));
							//System.out.println("Origional NextBid3: " + 	bidRunk.get(bidRunk.size() - lastBidNumber));

							lastBidNumber++;
						}
					}
					// Ã©â‚¬Å¡Ã¥Â¸Â¸BidÃ¥â€¡Â¦Ã§ï¿½â€ 
					// Ã§â€ºÂ¸Ã¦â€°â€¹Ã£ï¿½Â®Ã¦ï¿½ï¿½Ã§Â¤ÂºBidÃ£ï¿½Å’Ã¤Â¸â‚¬Ã¥Â®Å¡Ã¤Â»Â¥Ã¤Â¸Å Ã£â€šâ€™Ã¨Â¶â€¦Ã£ï¿½Ë†Ã£ï¿½Å¸Ã¥Â Â´Ã¥ï¿½Ë†
					else	{	
						
						//System.out.println("Original offeredutil: " + offeredutil);
						//System.out.println("Original getMinimumOffereDutil: " + minimumOffereDutil);
						
						if (offeredutil > minimumOffereDutil) {
						HashMap<Bid, Double> getBids = getBidTable(1);
						// Ã§â€ºÂ¸Ã¦â€°â€¹Ã£ï¿½Â®BidÃ£ï¿½Â®Ã¨Â¿â€˜Ã£ï¿½ï¿½Ã£ï¿½Â«MBUÃ¤Â»Â¥Ã¤Â¸Å Ã£ï¿½Â®BidÃ£ï¿½Å’Ã¥Â­ËœÃ¥Å“Â¨Ã£ï¿½â€”Ã£ï¿½Å¸Ã¥Â Â´Ã¥ï¿½Ë†
						if (getBids.size() >= 1) {
							// BidTableÃ£â€šâ€™Ã¥Ë†ï¿½Ã¦Å“Å¸Ã¥Å’â€“Ã£Æ’Â»Ã¥â€ ï¿½Ã¦Â§â€¹Ã§Â¯â€°
							currentBidNumber = 0;
							bidRunk.clear();
							bidTables = getBids;
							sortBid(getBids); // Sort BidTable
						} else { // Ã©â‚¬Å¡Ã¥Â¸Â¸Ã£ï¿½Â®BidÃ¦Â¤Å“Ã§Â´Â¢
							getBids = getBidTable(2);
							if (getBids.size() >= 1) {
								sortBid(getBids); // Sort BidTable
								Bid maxBid = getMaxBidUtility(getBids);
								// Ã¨Â¦â€¹Ã£ï¿½Â¤Ã£ï¿½â€˜Ã£ï¿½Å¸BidÃ£ï¿½Â®Ã£ï¿½â€ Ã£ï¿½Â¡Ã¦Å“â‚¬Ã¥Â¤Â§Ã£ï¿½Â®Ã§â€¢ÂªÃ¥ï¿½Â·Ã£â€šâ€™Ã¥â€°Â²Ã£â€šÅ Ã¥Â½â€œÃ£ï¿½Â¦
								currentBidNumber = bidRunk.indexOf(maxBid);
								//System.out.println("Original currentBidNumberChange0");

							}
						}
						action = new Offer(getAgentID(),
								bidRunk.get(currentBidNumber));
						//System.out.println("Origional NextBid4: " + bidRunk.get(currentBidNumber));


						//System.out.println("Original Condition: " + (currentBidNumber + 1 < bidRunk.size()));

						if (currentBidNumber + 1 < bidRunk.size()) {
							//System.out.println("Original currentBidNumberChange1");

							currentBidNumber++;
						}
					} else {
						HashMap<Bid, Double> getBids = getBidTable(2);
						//System.out.println("Original getBids.size(): " + getBids.size());

						if (getBids.size() >= 1) {
							sortBid(getBids); // Sort BidTable
							Bid maxBid = getMaxBidUtility(getBids);
							//System.out.println("Original maxBid: " + maxBid);

							//System.out.println("Change2 currentBidNumber");

							currentBidNumber = bidRunk.indexOf(maxBid);
							//System.out.println("Original maxBid: " + maxBid);

						}
						//System.out.println("Origional currentBidNumber: " +currentBidNumber);

						action = new Offer(getAgentID(),
								bidRunk.get(currentBidNumber));
						//System.out.println("Origional NextBid5: " + bidRunk.get(currentBidNumber));
						if (currentBidNumber + 1 < bidRunk.size()) {
							//System.out.println("Original currentBidNumberChange2");

							currentBidNumber++;
						} else {
							currentBidNumber = 0;
						}
					}
				}
				// Ã§â€ºÂ¸Ã¦â€°â€¹Ã£ï¿½Â®Ã¤Â¸â‚¬Ã£ï¿½Â¤Ã¥â€°ï¿½Ã£ï¿½Â®BidÃ£â€šâ€™Ã¨Â¨ËœÃ¦â€ Â¶Ã£ï¿½â€”Ã£ï¿½Â¦Ã£ï¿½Å Ã£ï¿½ï¿½
				previousPartnerBid = partnerBid;
			}}
		} catch (Exception e) {
			e.printStackTrace();
			action = new Accept(getAgentID()); // best guess if things go wrong.
		}
		return action;
	}

	// Ã§â€¢â„¢Ã¤Â¿ï¿½Ã¤Â¾Â¡Ã¦Â Â¼
	private void getReservationFactor() {
		if (utilitySpace.getReservationValue() != null) {
			reservation = utilitySpace.getReservationValue();
		}
	}

	// Ã¥â€°Â²Ã¥Â¼â€¢Ã§Å½â€¡Ã£ï¿½Â®Ã¦Å“â€°Ã§â€žÂ¡
	private void getDiscountFactor() {
		if (utilitySpace.getDiscountFactor() > 0.0) {
			discountFactor = true; // Ã¥â€°Â²Ã¥Â¼â€¢Ã§Å½â€¡Ã¦Å“â€°Ã£â€šÅ 
		} else
			discountFactor = false; // Ã¥â€°Â²Ã¥Â¼â€¢Ã§Å½â€¡Ã§â€žÂ¡Ã£ï¿½â€”
	}

	// forecastingÃ¥â€¡Â¦Ã§ï¿½â€ 
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
		double latestObservation = observationUtility.get(observationSize - 1); // Ã¦Å“â‚¬Ã¦â€“Â°Ã£ï¿½Â®Ã§â€ºÂ¸Ã¦â€°â€¹BidUtil
		double concessionPercent = Math.abs(latestObservation - firstOffereUtility) / (1.0 - firstOffereUtility);
		
		if (discountFactor) {
			// AgentÃ¨Â­Â²Ã¦Â­Â©Ã£ï¿½ÂªÃ£ï¿½â€”Ã£ï¿½Â¨Ã¥Ë†Â¤Ã¦â€“Â­
			if ((concessionPercent < 0.20) ||
					(observationSize < 3)) {
				percent = 0.35;
				sigmoidGain = -2;
			} else {
				percent = 0.45;
			}
		} else {
			// AgentÃ¨Â­Â²Ã¦Â­Â©Ã£ï¿½ÂªÃ£ï¿½â€”Ã£ï¿½Â¨Ã¥Ë†Â¤Ã¦â€“Â­
			if ((concessionPercent < 0.20) ||
					(observationSize < 3)) {
				percent = 0.50;
				sigmoidGain = -4;
			} else if (concessionPercent > 0.60) { // AgentÃ¨Â­Â²Ã¦Â­Â©Ã¥Â¤Â§
				percent = 0.80;
				sigmoidGain = -6;
			} else {
				percent = 0.60;
			}
		}
	}

	// Ã§â€ºÂ¸Ã¦â€°â€¹Ã£ï¿½Â®Ã¦ï¿½ï¿½Ã§Â¤ÂºBidÃ£ï¿½â€¹Ã£â€šâ€°Ã¦Å“â‚¬Ã¥Â¤Â§Ã£ï¿½Â®Ã£â€šâ€šÃ£ï¿½Â®Ã£â€šâ€™Ã¨Â¿â€�Ã£ï¿½â„¢
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
		alpha = (1.0 - firstOffereUtility) * percent; // Ã¥Ë†ï¿½Ã¦Å“Å¸UtilityÃ£ï¿½Â¨Ã£ï¿½Â®Ã¥Â·Â®x%
		//System.out.println("Original percent: " + percent);
		//System.out.println("Original alpha: " + alpha);
		//System.out.println("Original firstOffereUtility: " + firstOffereUtility);


		// Ã§â€ºÂ¸Ã¦â€°â€¹Ã£ï¿½Â®Ã¥Ë†ï¿½Ã¦Å“Å¸Utility+alphaÃ£â€šâ€™MBUÃ¤Â¸â€¹Ã©â„¢ï¿½Ã£ï¿½Â¨Ã£ï¿½â€”Ã£ï¿½Å¸sigmoidFuncÃ£ï¿½Â®Ã¥Â®Å¡Ã§Â¾Â©Ã¥Å¸Å¸Ã£â€šâ€™Ã¨Â¨Ë†Ã§Â®â€”
		double mbuInfimum = firstOffereUtility + alpha;
		//System.out.println("Original firstOffereUtility: " + firstOffereUtility);


		if (mbuInfimum >= 1.0) {
			mbuInfimum = 0.999; // 0Ã©â„¢Â¤Ã§Â®â€”Ã¥Â¯Â¾Ã§Â­â€“
		} else if (mbuInfimum <= 0.70) {
			mbuInfimum = 0.70; // MODÃ¤Â¸â€¹Ã©â„¢ï¿½
		}
		sigmoidX = 1 - ((1 / sigmoidGain) * Math.log(mbuInfimum / (1 - mbuInfimum)));

		minimumBidUtility = 1 - (1 / (1 + Math.exp(sigmoidGain
				* (time - sigmoidX)))); // Ã£â€šÂ·Ã£â€šÂ°Ã£Æ’Â¢Ã£â€šÂ¤Ã£Æ’â€°Ã©â€“Â¢Ã¦â€¢Â°Ã£ï¿½Â§Ã¦Å½Â¨Ã§Â§Â»
		//System.out.println("Original sigmoidX: " + sigmoidX);
		//System.out.println("Original sigmoidGain: " + sigmoidGain);


		if (minimumBidUtility < reservation) { // Ã§â€¢â„¢Ã¤Â¿ï¿½Ã¤Â¾Â¡Ã¦Â Â¼Ã¤Â»Â¥Ã¤Â¸â€¹Ã£ï¿½Â«Ã£ï¿½Â¯Ã¤Â¸â€¹Ã£ï¿½â€™Ã£ï¿½ÂªÃ£ï¿½â€ž
			minimumBidUtility = reservation;
		}

		// Ã§â€ºÂ¸Ã¦â€°â€¹Ã¨Â¨Â±Ã¥Â®Â¹BidÃ¤Â¸â€¹Ã©â„¢ï¿½(MOD)
		minimumOffereDutil =  minimumBidUtility * p;


	}

	/**
	 * BidTableÃ£â€šâ€™Ã©â„¢ï¿½Ã©Â â€ Ã£â€šÂ½Ã£Æ’Â¼Ã£Æ’Ë†
	 *
	 * @param bidTable
	 */
	private void sortBid(final HashMap<Bid, Double> getBids) {

		for (Bid bid : getBids.keySet()) {
			bidTables.put(bid, getUtility(bid));
			bidRunk.add(bid); // Add bidRunk
		}
		if(!EQUIVALENCE_TEST){

			// BidÃ£â€šÂ½Ã£Æ’Â¼Ã£Æ’Ë†Ã¥â€¡Â¦Ã§ï¿½â€ 
			Collections.sort(bidRunk, new Comparator<Bid>() {
				@Override
				public int compare(Bid o1, Bid o2) {
					return (int) Math.ceil(-(bidTables.get(o1) - bidTables.get(o2)));
				}
			});
	
		}
	}
	private Bid clone(Bid source) throws Exception {
		HashMap<Integer, Value> hash = new HashMap<Integer, Value>();
		for (Issue i : utilitySpace.getDomain().getIssues()) {
			hash.put(i.getNumber(), source.getValue(i.getNumber()));
		}
		return new Bid(utilitySpace.getDomain(), hash);
	}

	/**
	 * BidÃ£ï¿½â€¹Ã£â€šâ€°Ã¨Â¿â€˜Ã£ï¿½â€žÃ£ï¿½â€¹Ã£ï¿½Â¤UtilityÃ£ï¿½Å’Ã©Â«ËœÃ£ï¿½â€žÃ£â€šâ€šÃ£ï¿½Â®Ã£â€šâ€™Ã¦Å½Â¢Ã§Â´Â¢
	 *
	 * @param maxBid
	 * @return
	 * @throws Exception
	 */
	private HashMap<Bid, Double> getBidTable(int flag) throws Exception {
		HashMap<Bid, Double> getBids = new HashMap<Bid, Double>();

		//Random randomnr = new Random();
		ArrayList<Issue> issues = utilitySpace.getDomain().getIssues();
		Bid standardBid = null;

		for (Issue lIssue : issues) {
			switch (lIssue.getType()) {
			case DISCRETE:
				IssueDiscrete lIssueDiscrete = (IssueDiscrete) lIssue;
				for (ValueDiscrete value : lIssueDiscrete.getValues()) {
					if (flag == 0) {
						standardBid = utilitySpace.getMaxUtilityBid(); // Ã¨â€¡ÂªÃ¥Ë†â€ Ã£ï¿½Â®Ã¦Å“â‚¬Ã©Â«ËœÃ¥â‚¬Â¤
					} else if (flag == 1) {
						standardBid = ((Offer) actionOfPartner).getBid(); // Ã§â€ºÂ¸Ã¦â€°â€¹Ã£ï¿½Â®Bid
					} else {
						standardBid = bidRunk.get(currentBidNumber);
					}
					standardBid = clone(standardBid);
					standardBid.setValue(lIssue.getNumber(), value);
					double utility = getUtility(standardBid);
					//System.out.println("Original minimumBidUtility: " + minimumBidUtility);

					if ((utility > minimumBidUtility)
							&& (!bidRunk.contains(standardBid))) {
						getBids.put(standardBid, utility);
					}
				}
				break;
			case REAL:
				IssueReal lIssueReal = (IssueReal)lIssue;
				int optionInd = random100.nextInt(lIssueReal.getNumberOfDiscretizationSteps()-1);
				Value pValue = new ValueReal(lIssueReal.getLowerBound() + (lIssueReal.getUpperBound()-lIssueReal.getLowerBound())*(double)(optionInd)/(double)(lIssueReal.getNumberOfDiscretizationSteps()));
				standardBid.setValue(lIssueReal.getNumber(), pValue);
				double utility = getUtility(standardBid);
				getBids.put(standardBid, utility);
				break;
			case INTEGER:
				IssueInteger lIssueInteger = (IssueInteger)lIssue;
				int optionIndex2 = lIssueInteger.getLowerBound() + random100.nextInt(lIssueInteger.getUpperBound()-lIssueInteger.getLowerBound());
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
