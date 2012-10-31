package negotiator.boaframework.offeringstrategy.anac2012;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Random;
import negotiator.Bid;
import negotiator.actions.Action;
import negotiator.actions.Offer;
import negotiator.bidding.BidDetails;
import negotiator.boaframework.NegotiationSession;
import negotiator.boaframework.OMStrategy;
import negotiator.boaframework.OfferingStrategy;
import negotiator.boaframework.OpponentModel;
import negotiator.boaframework.sharedagentstate.anac2012.AgentMRSAS;
import negotiator.issue.Issue;
import negotiator.issue.IssueDiscrete;
import negotiator.issue.IssueInteger;
import negotiator.issue.IssueReal;
import negotiator.issue.Value;
import negotiator.issue.ValueDiscrete;
import negotiator.issue.ValueInteger;
import negotiator.issue.ValueReal;
import negotiator.utility.UtilitySpace;

/**
 * This is the decoupled Bidding Strategy of AgentMR
 * Note that the Opponent Model was not decoupled and thus
 * is integrated into this strategy
 * @author Alex Dirkzwager
 *
 */

public class AgentMR_Offering extends OfferingStrategy{
	
	private boolean EQUIVALENCE_TEST = true;
	private Random random100;	
	private ArrayList<Double> observationUtility = new ArrayList<Double>();
	private HashMap<Bid, Double> bidTables = new HashMap<Bid, Double>();
	private static boolean firstOffer;
	private static boolean forecastTime = true;
	private static boolean discountFactor;
	private static BidDetails previousPartnerBid = null;
	private static BidDetails offereMaxBid = null;
	private static double offereMaxUtility;
	private int currentBidNumber = 0;
	private int lastBidNumber = 1;
	private UtilitySpace utilitySpace;
	private boolean alreadyDone = false;

	public AgentMR_Offering() { }
	
	public AgentMR_Offering(NegotiationSession negoSession, OpponentModel model, OMStrategy oms) throws Exception {
		init(negoSession, model, oms, null);
	}
	
	/**
	 * Init required for the Decoupled Framework.
	 */
	@Override
	public void init(NegotiationSession negoSession, OpponentModel model, OMStrategy oms, HashMap<String, Double> parameters) throws Exception {
		super.init(negoSession, model, omStrategy, parameters);
		helper = new AgentMRSAS(negotiationSession);
		firstOffer = true;
		try {
			utilitySpace = negoSession.getUtilitySpace();
			getDiscountFactor();
			getReservationFactor();

			Bid b = negoSession.getMaxBidinDomain().getBid();
			bidTables.put(b, getUtility(b));
			((AgentMRSAS) helper).getBidRunk().add(b);
			if (discountFactor) { 
				((AgentMRSAS) helper).setSigmoidGain(-3.0);
				((AgentMRSAS) helper).setPercent(0.55);
			} else { 
				((AgentMRSAS) helper).setSigmoidGain(-5.0);
				((AgentMRSAS) helper).setPercent(0.70);
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

	@Override
	public BidDetails determineOpeningBid() {

		return determineNextBid();
	}

	@Override
	public BidDetails determineNextBid() {
		if(negotiationSession.getOpponentBidHistory().getHistory().isEmpty()){
			if(!alreadyDone){
				((AgentMRSAS) helper).updateMinimumBidUtility(0);
				alreadyDone = true;
			//System.out.println("Decoupled minimumBidUtility1: " + ((AgentMRSAS) helper).getMinimumBidUtility());
			}
			BidDetails maxBid = negotiationSession.getMaxBidinDomain();
			//System.out.println("Decoupled firstBid: " + maxBid.getBid());

			return negotiationSession.getMaxBidinDomain();
			
		}
		try {
			BidDetails partnerBid;
			if(firstOffer){
				partnerBid = negotiationSession.getOpponentBidHistory().getHistory().get(0);
			}else {
				//int size = negotiationSession.getOpponentBidHistory().size();
				partnerBid = negotiationSession.getOpponentBidHistory().getLastBidDetails();
				//System.out.println("Decoupled partnerBid: " + partnerBid.getBid());
			}
				
				
				// get current time
				double time = negotiationSession.getTime();
				//System.out.println("test: " + negotiationSession.getDiscountedUtility(negotiationSession.getOpponentBidHistory().getFirstBidDetails().getBid(), negotiationSession.getOpponentBidHistory().getFirstBidDetails().getTime()));
				double offeredutil; 
				if (discountFactor) {
					offeredutil = getUtility(partnerBid.getBid())
							* (1 / Math.pow(negotiationSession.getUtilitySpace().getDiscountFactor(),
									time));
					//System.out.println("Decoupled discount factor: ");

				} else {
					offeredutil = getUtility(partnerBid.getBid());

				}
				//System.out.println(firstOffer);
				if (firstOffer) {
					//System.out.println("Decoupled partnerBid: " + partnerBid.getBid());
					//System.out.println("Decoupled offeredutil: " + offeredutil);

					previousPartnerBid = partnerBid; // ç›¸æ‰‹ã�®é�ŽåŽ»Bidã‚’ã‚»ãƒƒãƒˆ
					offereMaxBid = partnerBid; // ç›¸æ‰‹ã�®æœ€å¤§Bidã‚’ã‚»ãƒƒãƒˆ
					offereMaxUtility = offeredutil; // ç›¸æ‰‹ã�®æœ€å¤§BidUtilã‚’ã‚»ãƒƒãƒˆ
					((AgentMRSAS) helper).setFirstOffereUtility(offeredutil); // ç›¸æ‰‹ã�®åˆ�æœŸBidUtilã‚’ã‚»ãƒƒãƒˆ
					//System.out.println("Decoupled firstOffereUtility: " + ((AgentMRSAS) helper).getFirstOffereUtility());

					observationUtility.add(offeredutil); // addObservation
					if (offeredutil > 0.5) { // é€“æ¸›çŽ‡ã‚»ãƒƒãƒˆ
						((AgentMRSAS) helper).setP(0.90);
					} else {
						((AgentMRSAS) helper).setP(0.80);
					}
					firstOffer = !firstOffer; // åˆ�å›žã‚ªãƒ•ã‚¡ãƒ¼å‡¦ç�†çµ‚äº†
				}
				((AgentMRSAS) helper).updateMinimumBidUtility(time); 

				// Bid
				if (partnerBid.equals(previousPartnerBid)) { // ç›¸æ‰‹ã�®æ��ç¤ºã�Œå¤‰ã‚�ã‚‰ã�ªã�„å ´å�ˆ
					if (currentBidNumber > 0 && 0.5 > 0.65) {
						currentBidNumber--; // ç¢ºçŽ‡çš„ã�«Bidã‚’ç§»å‹•
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

				// forecasting
				if ((time > 0.5) && forecastTime) {
					updateSigmoidFunction();
					forecastTime = !forecastTime;
				}


			
					if (offereMaxUtility > ((AgentMRSAS) helper).getMinimumBidUtility()) {
						//System.out.println("Decoupled NextBid1: " + offereMaxBid);

						nextBid = offereMaxBid;
					}
					else if (time > 0.985) {
						if (offereMaxUtility > ((AgentMRSAS) helper).getReservation()) {
							nextBid =offereMaxBid;
							//System.out.println("Decoupled NextBid2: " + offereMaxBid);
						} else { 
							Bid nBid = ((AgentMRSAS) helper).getBidRunk().get(((AgentMRSAS) helper).getBidRunk().size() - lastBidNumber);
							nextBid = new BidDetails(nBid, negotiationSession.getUtilitySpace().getUtility(nBid));
							//System.out.println("Decoupled NextBid3: " + nextBid);

							lastBidNumber++;
						}
					}
					else {
						//System.out.println("Decoupled offeredutil: " + offeredutil);
						//System.out.println("Decoupled getMinimumOffereDutil: " + ((AgentMRSAS) helper).getMinimumOffereDutil());
						if(offeredutil > ((AgentMRSAS) helper).getMinimumOffereDutil()) {
						HashMap<Bid, Double> getBids = getBidTable(1);
						if (getBids.size() >= 1) {
							// BidTable
							currentBidNumber = 0;
							((AgentMRSAS) helper).getBidRunk().clear();
							bidTables = getBids;
							sortBid(getBids); // Sort BidTable
						} else { // é€šå¸¸ã�®Bidæ¤œç´¢
							getBids = getBidTable(2);
							if (getBids.size() >= 1) {
								sortBid(getBids); // Sort BidTable
								Bid maxBid = getMaxBidUtility(getBids);
								currentBidNumber = ((AgentMRSAS) helper).getBidRunk().indexOf(maxBid);
								//System.out.println("Decoupled currentBidNumberChange0");

							}
						}
						Bid nBid = ((AgentMRSAS) helper).getBidRunk().get(currentBidNumber);
						nextBid = new BidDetails(nBid, negotiationSession.getUtilitySpace().getUtility(nBid));
						//System.out.println("Decoupled NextBid4: " + nextBid.getBid());

 
						//System.out.println("Decoupled Condition: " + (currentBidNumber + 1 < ((AgentMRSAS) helper).getBidRunk().size()));
						if (currentBidNumber + 1 < ((AgentMRSAS) helper).getBidRunk().size()) {
							//System.out.println("Decoupled currentBidNumberChange1");

							currentBidNumber++;
						}
						
					} else {
						HashMap<Bid, Double> getBids = getBidTable(2);
						//System.out.println("Decoupled getBids.size(): " + getBids.size());

						if (getBids.size() >= 1) {
							sortBid(getBids); // Sort BidTable
							Bid maxBid = getMaxBidUtility(getBids);
							currentBidNumber = ((AgentMRSAS) helper).getBidRunk().indexOf(maxBid);
						//	System.out.println("Decoupled maxBid: " + maxBid);

							
						}
//						System.out.println("Decoupled currentBidNumber: " + currentBidNumber);

						Bid nBid = ((AgentMRSAS) helper).getBidRunk().get(currentBidNumber);
						nextBid = new BidDetails(nBid, negotiationSession.getUtilitySpace().getUtility(nBid));
						
						//System.out.println("Decoupled NextBid5: " + nextBid.getBid());
						//System.out.println("Decoupled BidRunkSize: " + ((AgentMRSAS) helper).getBidRunk().size());
						if (currentBidNumber + 1 < ((AgentMRSAS) helper).getBidRunk().size()) {
						//	System.out.println("Decoupled currentBidNumberChange2");
							currentBidNumber++;
						} else {
							currentBidNumber = 0;
						}
					}
				previousPartnerBid = partnerBid;
					}			
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			
		return nextBid;
		
	}

	private void getReservationFactor() {
		if (utilitySpace.getReservationValue() != null) {
			((AgentMRSAS) helper).setReservation(utilitySpace.getReservationValue());
		}
	}

	private void getDiscountFactor() {
		if (utilitySpace.getDiscountFactor() > 0.0) {
			discountFactor = true; // å‰²å¼•çŽ‡æœ‰ã‚Š
		} else
			discountFactor = false; // å‰²å¼•çŽ‡ç„¡ã�—
	}

	private void newupdateSigmoidFunction() {
		double latestObservation = observationUtility.get(observationUtility.size() - 1);
		double concessionPercent = Math.abs(latestObservation - ((AgentMRSAS) helper).getFirstOffereUtility()) / (1.0 - ((AgentMRSAS) helper).getFirstOffereUtility());
		double modPercent = Math.abs(((AgentMRSAS) helper).getMinimumOffereDutil() - ((AgentMRSAS) helper).getFirstOffereUtility()) / (1.0 - ((AgentMRSAS) helper).getFirstOffereUtility());

		if (modPercent < concessionPercent) {
			((AgentMRSAS) helper).setPercent(concessionPercent);
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

	
	/**
	 * BidTable
	 *
	 * @param bidTable
	 */
	private void sortBid(final HashMap<Bid, Double> getBids) {

		for (Bid bid : getBids.keySet()) {
			bidTables.put(bid, getUtility(bid));
			((AgentMRSAS) helper).getBidRunk().add(bid); // Add bidRunk
		}

		if(!EQUIVALENCE_TEST){
		// Bidã‚½ãƒ¼ãƒˆå‡¦ç�†
			Collections.sort(((AgentMRSAS) helper).getBidRunk(), new Comparator<Bid>() {
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
						standardBid = utilitySpace.getMaxUtilityBid(); // è‡ªåˆ†ã�®æœ€é«˜å€¤
					} else if (flag == 1) {
						standardBid = negotiationSession.getOpponentBidHistory().getLastBid(); 
					} else {
						standardBid = ((AgentMRSAS) helper).getBidRunk().get(currentBidNumber);
					}
					standardBid = clone(standardBid);
					standardBid.setValue(lIssue.getNumber(), value);
					double utility = getUtility(standardBid);
					//System.out.println("Decoupled minimumBidUtility: " + ((AgentMRSAS) helper).getMinimumBidUtility());
					if ((utility > ((AgentMRSAS) helper).getMinimumBidUtility())
							&& (!((AgentMRSAS) helper).getBidRunk().contains(standardBid))) {
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

	public double getUtility(Bid bid)
    {
    	return negotiationSession.getUtilitySpace().getUtilityWithDiscount(bid, negotiationSession.getTimeline());
    }

	
	private void updateSigmoidFunction() {
		int observationSize = observationUtility.size();
		double latestObservation = observationUtility.get(observationSize - 1); // æœ€æ–°ã�®ç›¸æ‰‹BidUtil
		double concessionPercent = Math.abs(latestObservation - ((AgentMRSAS) helper).getFirstOffereUtility()) / (1.0 - ((AgentMRSAS) helper).getFirstOffereUtility());

		if (discountFactor) {
			if ((concessionPercent < 0.20) ||
					(observationSize < 3)) {
				((AgentMRSAS) helper).setPercent(0.35);
				((AgentMRSAS) helper).setSigmoidGain(-2);
			} else {
				((AgentMRSAS) helper).setPercent(0.45);
			}
		} else {
			if ((concessionPercent < 0.20) ||
					(observationSize < 3)) {
				((AgentMRSAS) helper).setPercent(0.50);
				((AgentMRSAS) helper).setSigmoidGain(-4);
			} else if (concessionPercent > 0.60) { 
				((AgentMRSAS) helper).setPercent(0.80);
				((AgentMRSAS) helper).setSigmoidGain(-6);
			} else {
				((AgentMRSAS) helper).setPercent(0.60);
			}
		}
	}

}
