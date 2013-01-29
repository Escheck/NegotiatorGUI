package negotiator.boaframework.offeringstrategy.anac2012;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import negotiator.Bid;
import negotiator.BidIterator;
import negotiator.Timeline;
import negotiator.bidding.BidDetails;
import negotiator.boaframework.NegotiationSession;
import negotiator.boaframework.OMStrategy;
import negotiator.boaframework.OfferingStrategy;
import negotiator.boaframework.OpponentModel;
import negotiator.boaframework.offeringstrategy.anac2012.CUHKAgent.OpponentBidHistory;
import negotiator.boaframework.sharedagentstate.anac2012.CUHKAgentSAS;
import negotiator.issue.Issue;
import negotiator.issue.IssueDiscrete;
import negotiator.issue.IssueInteger;
import negotiator.issue.IssueReal;
import negotiator.issue.Value;
import negotiator.issue.ValueInteger;
import negotiator.issue.ValueReal;
import negotiator.utility.UtilitySpace;
import agents.anac.y2012.CUHKAgent.OwnBidHistory;

/**
 * This is the decoupled Bidding Strategy of CUHKAgent
 * Note that the Opponent Model was not decoupled and thus
 * is integrated into this strategy
 * 
 * DEFAULT OM: Own
 * 
 * This agent determines a set of candidate bids each. As the agent does not concede
 * significantly, using an OM is not expected to result in a gain.
 * 
 * If OM is none, then a random bid is selected from the set of candidateBids.
 * If OM is default, then the agents own OM is used.
 * If OM is otherwise, the set OM is used.
 * 
 * @author Alex Dirkzwager, Mark Hendrikx
 */
public class CUHKAgent_Offering extends OfferingStrategy {

    private BidDetails opponentBid = null;
    private double maximumOfBid;
    private OwnBidHistory ownBidHistory;
    private OpponentBidHistory opponentBidHistory;
    private double minimumUtilityThreshold;
    private double discountingFactor;
    private double concedeToDiscountingFactor_original;
    private double minConcedeToDiscountingFactor;
    private ArrayList<ArrayList<Bid>> bidsBetweenUtility;
    private double alpha1;//the larger alpha is, the more tough the agent is.
    private Bid bid_maximum_utility;//the bid with the maximum utility over the utility space.
    private UtilitySpace utilitySpace;
    private Timeline timeline;
    
    private Random random;
	private final boolean TEST_EQUIVALENCE = true;

    public CUHKAgent_Offering() { }
	
	public CUHKAgent_Offering(NegotiationSession negoSession, OpponentModel model, OMStrategy oms) throws Exception {
		init(negoSession, model, oms, null);
	}
	
	/**
	 * Init required for the BOA Framework.
	 */
	@Override
	public void init(NegotiationSession negoSession, OpponentModel model, OMStrategy oms, HashMap<String, Double> parameters) throws Exception {
	    super.init(negoSession, model, oms, parameters);
		helper = new CUHKAgentSAS(negotiationSession);
		
	    utilitySpace = negoSession.getUtilitySpace();
	    timeline = negoSession.getTimeline();
	    try {
            maximumOfBid = this.utilitySpace.getDomain().getNumberOfPossibleBids();
            ownBidHistory = new OwnBidHistory();
            opponentBidHistory = new OpponentBidHistory(opponentModel, omStrategy);
            bidsBetweenUtility = new ArrayList<ArrayList<Bid>>();
            this.bid_maximum_utility = utilitySpace.getMaxUtilityBid();
            this.minConcedeToDiscountingFactor = 0.08;//0.1;
            this.discountingFactor = 1;
      
            if (utilitySpace.getDiscountFactor() <= 1D && utilitySpace.getDiscountFactor() > 0D) {
                this.discountingFactor = utilitySpace.getDiscountFactor();
            }
            
            if(TEST_EQUIVALENCE){
            	random = new Random(100);
            } else {
            	random = new Random();
            }
            this.chooseUtilityThreshold();

            this.calculateBidsBetweenUtility();
            this.chooseConcedeToDiscountingDegree();
            
            this.opponentBidHistory.initializeDataStructures(utilitySpace.getDomain());
            ((CUHKAgentSAS) helper).setTimeLeftAfter(negoSession.getTimeline().getCurrentTime());
            this.alpha1 = 2;  
        } catch (Exception e) {
            System.out.println("initialization error" + e.getMessage());
        }
	}
	    
	    
	@Override
	public BidDetails determineOpeningBid() {
		return determineNextBid();
	}

	@Override
	public BidDetails determineNextBid() {
        Bid bidToOffer = null;
        try {
            // System.out.println("i propose " + debug + " bid at time " + timeline.getTime());
            ((CUHKAgentSAS) helper).setTimeLeftBefore(timeline.getCurrentTime());
            Bid bid = null;
            //we propose first and propose the bid with maximum utility
            if (negotiationSession.getOpponentBidHistory().getHistory().isEmpty()) {
                bid = this.bid_maximum_utility;
                bidToOffer = bid;
            } else if (negotiationSession.getOpponentBidHistory().size() >= 1) {//the opponent propose first and we response secondly
            	opponentBid = negotiationSession.getOpponentBidHistory().getLastBidDetails();
                //update opponent model first
                this.opponentBidHistory.updateOpponentModel(opponentBid.getBid(), utilitySpace.getDomain(), this.utilitySpace);
                this.updateConcedeDegree();
                //update the estimation
                if (ownBidHistory.numOfBidsProposed() == 0) {
                    //bid = utilitySpace.getMaxUtilityBid();
                    bid = this.bid_maximum_utility;
                    System.out.println("Decoupled bid1: " + bid);

                    bidToOffer = bid;
                } else {//other conditions
                	//System.out.println("Decoupled Conditions: " + (timeline.getTime() > 0.9985 && ((CUHKAgentSAS) helper).estimateRoundLeft(true) < 5));
                    if (((CUHKAgentSAS) helper).estimateTheRoundsLeft(false,true) > 10) {//still have some rounds left to further negotiate (the major negotiation period)
                        bid = BidToOffer();
                        //System.out.println("Decoupled bid1: " + bid);
                       //we expect that the negotiation is over once we select a bid from the opponent's history.
                        if (((CUHKAgentSAS)helper).isConcedeToOpponent() == true) {
                            // bid = opponentBidHistory.chooseBestFromHistory(this.utilitySpace);
                            bid = opponentBidHistory.getBestBidInHistory();
                            //System.out.println("Decoupled bid2: " + bid);

                            bidToOffer = bid;
                            //System.out.println("we offer the best bid in the history and the opponent should accept it");
                            ((CUHKAgentSAS)helper).setToughAgent(true);
                            ((CUHKAgentSAS)helper).setConcedeToOpponent(false);
                        } else {
                            bidToOffer = bid;
                            ((CUHKAgentSAS)helper).setToughAgent(false);
                            //System.out.println("i propose " + debug + " bid at time " + timeline.getTime());
                        }
                    } else {//this is the last chance and we concede by providing the opponent the best offer he ever proposed to us
                        //in this case, it corresponds to an opponent whose decision time is short
                    	System.out.println("Decoupled Test: " + (timeline.getTime() > 0.9985 && ((CUHKAgentSAS) helper).estimateTheRoundsLeft(false,true) < 5));

                        if (timeline.getTime() > 0.9985 && ((CUHKAgentSAS) helper).estimateTheRoundsLeft(false,true) < 5) {
                            //bid = opponentBidHistory.chooseBestFromHistory(this.utilitySpace);
                            bid = opponentBidHistory.getBestBidInHistory();
                            System.out.println("Decoupled bid3: " + bid);

                            //this is specially designed to avoid that we got very low utility by searching between an acceptable range (when the domain is small)
                            if (this.utilitySpace.getUtility(bid) < 0.85) {
                                List<Bid> candidateBids = this.getBidsBetweenUtility(((CUHKAgentSAS)helper).getMaximumUtility() - 0.15, (((CUHKAgentSAS)helper).getMaximumUtility()- 0.02));
                                //if the candidate bids do not exsit and also the deadline is approaching in next round, we concede.
                                //if (candidateBids.size() == 1 && timeline.getTime()>0.9998) {
                                //we have no chance to make a new proposal before the deadline
                                if (((CUHKAgentSAS) helper).estimateTheRoundsLeft(false,true) < 2) {
                                    bid = opponentBidHistory.getBestBidInHistory();
                                    //System.out.printlned bid3: " + bid);

                                    System.out.println("test I " + utilitySpace.getUtility(bid));
                                } else {
                                    bid = opponentBidHistory.ChooseBid(candidateBids, this.utilitySpace.getDomain(), this.utilitySpace);
                                    System.out.println("Decoupled bid4: " + bid);

                                }
                                if (bid == null) {
                                    bid = opponentBidHistory.getBestBidInHistory();
                                    System.out.println("Decoupled bid5: " + bid);

                                }
                            }

                            if (((CUHKAgentSAS) helper).isToughAgent() == false) {
                                bidToOffer = bid;
                                //this.toughAgent = true;
                                System.out.println("this is really the last chance" + bid.toString() + " with utility of " + utilitySpace.getUtility(bid));
                            }
                            
                            //in this case, it corresponds to the situation that we encounter an opponent who needs more computation to make decision each round
                        } else {//we still have some time to negotiate, 
                            //and be tough by sticking with the lowest one in previous offer history.
                            // we also have to make the decisin fast to avoid reaching the deadline before the decision is made
                            //bid = ownBidHistory.GetMinBidInHistory();//reduce the computational cost
                            bid = BidToOffer();
                            System.out.println("Decoupled bid6: " + bid);

                            //System.out.println("test----------------------------------------------------------" + timeline.getTime());
                            bidToOffer = bid;
                            //System.out.println("we have to be tough now" + bid.toString() + " with utility of " + utilitySpace.getUtility(bid));
                        }
                    }
                }
            }
            //System.out.println("i propose " + debug + " bid at time " + timeline.getTime());
            this.ownBidHistory.addBid(bid, utilitySpace);
           ((CUHKAgentSAS) helper).setTimeLeftAfter(timeline.getCurrentTime());
            ((CUHKAgentSAS) helper).estimateTheRoundsLeft(false,false);//update the estimation
            //System.out.println(this.utilitythreshold + "-***-----" + this.timeline.getElapsedSeconds());
        } catch (Exception e) {
            System.out.println("Exception in ChooseAction:" + e.getMessage());
            System.out.println(((CUHKAgentSAS) helper).estimateTheRoundsLeft(false,false));
            //action = new Accept(getAgentID()); // accept if anything goes wrong.
            //bidToOffer = new EndNegotiation(getAgentID()); //terminate if anything goes wrong.
        }
        
        try {
			nextBid = new BidDetails(bidToOffer, negotiationSession.getUtilitySpace().getUtility(bidToOffer));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return nextBid;
    }

    /*
     * principle: randomization over those candidate bids to let the opponent
     * have a better model of my utility profile return the bid to be offered in
     * the next round
     */
    private Bid BidToOffer() {
        Bid bidReturned = null;
        int test = 0;
        double decreasingAmount_1 = 0.05;
        double decreasingAmount_2 = 0.25;
        try {

            double maximumOfBid = (((CUHKAgentSAS)helper).getMaximumUtility());//utilitySpace.getUtility(utilitySpace.getMaxUtilityBid());
            double minimumOfBid;
            //used when the domain is very large.
            //make concession when the domin is large
            if (this.discountingFactor == 1 && this.maximumOfBid > 3000) {
                minimumOfBid = (((CUHKAgentSAS)helper).getMaximumUtility()) - decreasingAmount_1;
                //make further concession when the deadline is approaching and the domain is large
                if (this.discountingFactor > 1 - decreasingAmount_2 && this.maximumOfBid > 10000 && timeline.getTime() >= 0.98) {
                    minimumOfBid = (((CUHKAgentSAS)helper).getMaximumUtility()) - decreasingAmount_2;
                }
                if (((CUHKAgentSAS) helper).getUtilitythreshold() > minimumOfBid) {
                	((CUHKAgentSAS) helper).setUtilitythreshold(minimumOfBid);
                }
            }/*else if (this.discountingFactor > 1 - decreasingAmount_3 && this.maximumOfBid >= 100000 && this.maximumOfBid < 300000) {
            minimumOfBid = this.MaximumUtility - decreasingAmount_3;
            } else if (this.discountingFactor > 1 - decreasingAmount_4 && this.maximumOfBid >= 300000) {
            minimumOfBid = this.MaximumUtility - decreasingAmount_4;
            }*/ else {//the general case
                if (timeline.getTime() <= ((CUHKAgentSAS) helper).getConcedeToDiscountingFactor()) {
                    double minThreshold = (maximumOfBid * this.discountingFactor) / Math.pow(this.discountingFactor, ((CUHKAgentSAS) helper).getConcedeToDiscountingFactor());
                    ((CUHKAgentSAS) helper).setUtilitythreshold(maximumOfBid - (maximumOfBid - minThreshold) * Math.pow((timeline.getTime() / ((CUHKAgentSAS) helper).getConcedeToDiscountingFactor()), alpha1));
                } else {
                	((CUHKAgentSAS) helper).setUtilitythreshold((maximumOfBid * this.discountingFactor) / Math.pow(this.discountingFactor, timeline.getTime()));
                }
                minimumOfBid = ((CUHKAgentSAS) helper).getUtilitythreshold();
            }

            /*
             * if(minimumOfBid < 0.9 && this.guessOpponentType == false){
             * if(this.opponentBidHistory.getSize() <= 2){ this.opponentType =
             * 1;//tough opponent alpha1 = 2; } else{ this.opponentType = 0;
             * alpha1 = 4; } this.guessOpponentType = true;//we only guess the
             * opponent type once here System.out.println("we guess the opponent
             * type is "+this.opponentType); }
             */

            //choose from the opponent bid history first to reduce calculation time            
            Bid bestBidOfferedByOpponent = opponentBidHistory.getBestBidInHistory();
            if (utilitySpace.getUtility(bestBidOfferedByOpponent) >= ((CUHKAgentSAS) helper).getUtilitythreshold() || utilitySpace.getUtility(bestBidOfferedByOpponent) >= minimumOfBid) {
                return bestBidOfferedByOpponent;
            }
            List<Bid> candidateBids = this.getBidsBetweenUtility(minimumOfBid, maximumOfBid);
            test = candidateBids.size();
            bidReturned = opponentBidHistory.ChooseBid(candidateBids, this.utilitySpace.getDomain(), this.utilitySpace);
            if (bidReturned == null) {
                System.out.println("no bid is searched warning");
                bidReturned = this.utilitySpace.getMaxUtilityBid();
            }
        } catch (Exception e) {
            System.out.println(e.getMessage() + "exception in method BidToOffer");
        }
        // System.out.println("the current threshold is " + this.utilitythreshold + " with the value of alpha1 is  " + alpha1);
        return bidReturned;
    }

 
    
    /*
     * pre-processing to save the computational time each round
     */
    private void calculateBidsBetweenUtility() {
        BidIterator myBidIterator = new BidIterator(this.utilitySpace.getDomain());

        try {
            // double maximumUtility = utilitySpace.getUtility(utilitySpace.getMaxUtilityBid());
            double maximumUtility = (((CUHKAgentSAS)helper).getMaximumUtility());
            double minUtility = this.minimumUtilityThreshold;
            int maximumRounds = (int) ((maximumUtility - minUtility) / 0.01);
            //initalization for each arraylist storing the bids between each range
            for (int i = 0; i < maximumRounds; i++) {
                ArrayList<Bid> BidList = new ArrayList<Bid>();
                // BidList.add(this.bid_maximum_utility);
                this.bidsBetweenUtility.add(BidList);
            }
            this.bidsBetweenUtility.get(maximumRounds - 1).add(this.bid_maximum_utility);
            //note that here we may need to use some trick to reduce the computation cost (to be checked later);
            //add those bids in each range into the corresponding arraylist
            int limits = 0;
            if (this.maximumOfBid < 20000) {
                while (myBidIterator.hasNext()) {
                    Bid b = myBidIterator.next();
                    for (int i = 0; i < maximumRounds; i++) {
                        if (utilitySpace.getUtility(b) <= (i + 1) * 0.01 + minUtility && utilitySpace.getUtility(b) >= i * 0.01 + minUtility) {
                            this.bidsBetweenUtility.get(i).add(b);
                            break;
                        }
                    }
                    //limits++;
                }
            } else {
                while (limits <= 20000) {
                    Bid b = this.RandomSearchBid();
                    for (int i = 0; i < maximumRounds; i++) {
                        if (utilitySpace.getUtility(b) <= (i + 1) * 0.01 + minUtility && utilitySpace.getUtility(b) >= i * 0.01 + minUtility) {
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
        Bid bid = null;

        for (Issue lIssue : issues) {
            switch (lIssue.getType()) {
                case DISCRETE:
                	
                    IssueDiscrete lIssueDiscrete = (IssueDiscrete) lIssue;
                    int optionIndex = random.nextInt(lIssueDiscrete.getNumberOfValues());
                    values.put(lIssue.getNumber(),
                            lIssueDiscrete.getValue(optionIndex));
                    break;
                case REAL:
                    IssueReal lIssueReal = (IssueReal) lIssue;
                    int optionInd = random.nextInt(lIssueReal.getNumberOfDiscretizationSteps() - 1);
                    values.put(
                            lIssueReal.getNumber(),
                            new ValueReal(lIssueReal.getLowerBound() + (lIssueReal.getUpperBound() - lIssueReal.getLowerBound()) * (double) (optionInd) / (double) (lIssueReal.getNumberOfDiscretizationSteps())));
                    break;
                case INTEGER:
                    IssueInteger lIssueInteger = (IssueInteger) lIssue;
                    int optionIndex2 = lIssueInteger.getLowerBound() + random.nextInt(lIssueInteger.getUpperBound() - lIssueInteger.getLowerBound());
                    values.put(lIssueInteger.getNumber(), new ValueInteger(
                            optionIndex2));
                    break;
                default:
                    throw new Exception("issue type " + lIssue.getType() + " not supported");
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
            //this.minimumUtilityThreshold = 0.85;
            this.minimumUtilityThreshold = 0;//this.MaximumUtility - 0.09;
        }
    }
    /*
     * determine concede-to-time degree based on the discounting factor.
     */

    private void chooseConcedeToDiscountingDegree() {
        double alpha = 0;
        double beta = 1.5;//1.3;//this value controls the rate at which the agent concedes to the discouting factor.
        //the larger beta is, the more the agent makes concesions.
        // if (utilitySpace.getDomain().getNumberOfPossibleBids() > 100) {
       /* if (this.maximumOfBid > 100) {
        beta = 2;//1.3;
        } else {
        beta = 1.5;
        }*/
        //the vaule of beta depends on the discounting factor (trade-off between concede-to-time degree and discouting factor)
        if (this.discountingFactor > 0.75) {
            beta = 1.8;
        } else if (this.discountingFactor > 0.5) {
            beta = 1.5;
        } else {
            beta = 1.2;
        }
        alpha = Math.pow(this.discountingFactor, beta);
        ((CUHKAgentSAS) helper).setConcedeToDiscountingFactor(this.minConcedeToDiscountingFactor + (1 - this.minConcedeToDiscountingFactor) * alpha);
        this.concedeToDiscountingFactor_original = ((CUHKAgentSAS) helper).getConcedeToDiscountingFactor();
        System.out.println("concedeToDiscountingFactor is " + ((CUHKAgentSAS) helper).getConcedeToDiscountingFactor() + "current time is " + timeline.getTime());
    }
    /*
     * update the concede-to-time degree based on the predicted toughness degree
     * of the opponent
     */

    private void updateConcedeDegree() {
        double gama = 10;
        double weight = 0.1;
        double opponnetToughnessDegree = this.opponentBidHistory.getConcessionDegree();
        //this.concedeToDiscountingFactor = this.concedeToDiscountingFactor_original * (1 + opponnetToughnessDegree);
        ((CUHKAgentSAS) helper).setConcedeToDiscountingFactor( this.concedeToDiscountingFactor_original + weight * (1 - this.concedeToDiscountingFactor_original) * Math.pow(opponnetToughnessDegree, gama));
        if ( ((CUHKAgentSAS) helper).getConcedeToDiscountingFactor() >= 1) {
        	((CUHKAgentSAS) helper).setConcedeToDiscountingFactor(1);
        }
        // System.out.println("concedeToDiscountingFactor is " + this.concedeToDiscountingFactor + "current time is " + timeline.getTime() + "original concedetodiscoutingfactor is " + this.concedeToDiscountingFactor_original);
    }
}




