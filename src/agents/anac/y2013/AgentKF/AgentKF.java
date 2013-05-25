package agents.anac.y2013.AgentKF;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import java.io.Serializable;
import negotiator.Agent;
import negotiator.Bid;
import negotiator.BidHistory;
import negotiator.actions.Accept;
import negotiator.actions.Action;
import negotiator.actions.Offer;
import negotiator.actions.EndNegotiation;
import negotiator.bidding.BidDetails;
import negotiator.issue.Issue;
import negotiator.issue.IssueDiscrete;
import negotiator.issue.IssueInteger;
import negotiator.issue.IssueReal;
import negotiator.issue.Value;
import negotiator.issue.ValueInteger;
import negotiator.issue.ValueReal;

public class AgentKF extends Agent {

	// I want to print "state" when I print a message about saving data.
	private String state;
	private Action partner;
	private HashMap<Bid, Double> offeredBidMap;
	private double target;
	private double bidTarget; 
	private double bidReduction; 
	private double sum;
	private double sum2; 
	private int rounds;
	private double tremor; 
	private int MaxLoopNum; 
	
	private BidHistory currSessOppBidHistory = new BidHistory();
	private BidHistory prevSessOppBidHistory = new BidHistory();
	private double MINIMUM_BID_UTILITY; 
	private double MinAutoAcceptUtil;
	private boolean FinalPhase;
	private double PrevMean;


	public void init() {
		// System.out.println("debug : ----- Initialize -----");

		offeredBidMap = new HashMap<Bid, Double>();
		target = 1.0;
		bidTarget = 1.0;
		bidReduction = 0.01;
		sum = 0.0;
		sum2 = 0.0;
		rounds = 0;
		tremor = 2.0;
		MinAutoAcceptUtil = 0.8;
		MaxLoopNum = 1000;
		PrevMean = 0;
		FinalPhase = false;

		MINIMUM_BID_UTILITY = utilitySpace.getReservationValueUndiscounted();

		myBeginSession();
	}

	public void myBeginSession() {
		//System.out.println("Starting match num: " + sessionNr);

		//---- Code for trying save and load functionality
		//     First try to load saved data
		//---- Loading from agent's function "loadSessionData"
		Serializable prev = this.loadSessionData();
		if (prev != null){
			prevSessOppBidHistory = (BidHistory)prev;
			//.println("---------/////////// NEW  NEW  NEW /////////////----------");
			//System.out.println("The size of the previous BidHistory is: " + prevSessOppBidHistory.size());
			currSessOppBidHistory = prevSessOppBidHistory;
			PrevMean = prevSessOppBidHistory.getAverageDiscountedUtility(utilitySpace);
		}
		else{
			// If didn't succeed, it means there is no data for this preference profile
			// in this domain.
			//System.out.println("There is no history yet.");
		}
	}

	public static String getVersion() {
		return "1.1";
	}
	
	@Override
	public String getName()
	{
		return "AgentKF";
	}


	public void ReceiveMessage(Action opponentAction) {
		// System.out.println("debug : ----- ReceiveMessage -----");
		partner = opponentAction;
		if(opponentAction instanceof Offer) {
			Bid bid = ((Offer)opponentAction).getBid();
			// 2. store the opponent's trace
			try {
				BidDetails opponentBid = new BidDetails(bid, utilitySpace.getUtility(bid), timeline.getTime());
				currSessOppBidHistory.add(opponentBid);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public Action chooseAction() {
		Action action = null;
		try {
			if (partner == null) {
				action = selectBid();
			}
			if (partner instanceof Offer) {
				Bid offeredBid = ((Offer) partner).getBid();

				double p = acceptProbability(offeredBid);

				if(utilitySpace.getUtility(offeredBid) > MinAutoAcceptUtil){
					p = 1.0;
				}
				
				if(rounds % 500 == 0){
					tremor += adjustTremor(timeline.getCurrentTime());
					//System.out.println(tremor);
				}
				
				
				if(timeline.getCurrentTime() > 0.85){
					BidHistory FinalBidHistory = currSessOppBidHistory.filterBetweenTime(timeline.getCurrentTime(), 1.0);
					double FinalAvg = FinalBidHistory.getAverageUtility();
					if(FinalAvg < sum/rounds) {
						FinalPhase = true;
					}
				}

				if (p > Math.random()) {
					//System.out.println("debug : Choose Action => Accept - " + p);
					action = new Accept(getAgentID());
				} else {
					// System.out.println("debug : Choose Action => Select Bid");
					action = selectBid();
				}
				//---- Code for trying save and load functionality
				///////////////////////////////////
				state = "Opponet Send the Bid ";
				tryToSaveAndPrintState();
				//System.out.println("Save " + state);
				///////////////////////////////////
			}
			if(partner instanceof EndNegotiation){
				//---- Code for trying save and load functionality///////////////////////////////////
				state = "Got EndNegotiation from opponent. ";
				tryToSaveAndPrintState();
				//System.out.println(state);
				///////////////////////////////////
			}
		} catch (Exception e) {
			//System.out.println("Exception in ChooseAction:" + e.getMessage());
			//---- Code for trying save and load functionality
			///////////////////////////////////
			state = "Got Exception. ";
			tryToSaveAndPrintState();
			//System.out.println(state);
			///////////////////////////////////
			action = new Accept(getAgentID());
		}
		return action;
	}

	//---- Code for trying save and load functionality
	private void tryToSaveAndPrintState() {

		//---- Saving from agent's function "saveSessionData"
		if (currSessOppBidHistory.size() < Math.pow(10, 5)){
			this.saveSessionData(currSessOppBidHistory);
			//System.out.println(state + "The size of the BidHistory I'm saving is: " + currSessOppBidHistory.size());
		}
	}


	private Action selectBid() {
		// System.out.println("debug : ----- Select Bid -----");
		Bid nextBid = null;
		double time = timeline.getTime();

		ArrayList<Bid> bidTemp = new ArrayList<Bid>();

		for (Bid bid : offeredBidMap.keySet()) {
			if (offeredBidMap.get(bid) > target) {
				bidTemp.add(bid);
			}
		}

		int size = bidTemp.size();
		if (size > 0) {
			// System.out.println("debug : hit effective bid = " + size);
			int sindex = (int) Math.floor(Math.random() * size);
			// System.out.println("debug : select index " + sindex);
			nextBid = bidTemp.get(sindex);
		} else {
			double searchUtil = 0.0;
			// System.out.println("debug : no hit ");
			try {
				int loop = 0;
				boolean NotFind = true;
				ArrayList<Bid> AltNextBid = new ArrayList<Bid>();
					while (loop < MaxLoopNum) {/*searchUtil < bidTarget*/
						if (loop == MaxLoopNum-1 & NotFind) {
							bidTarget -= bidReduction;
							loop = 0;
							// System.out.println("debug : challenge fail, targetUtility reset = " + targetUtility);
						}
						Bid altNextBid = searchBid();
						searchUtil = utilitySpace.getUtilityWithDiscount(altNextBid,time);
						if (searchUtil >= bidTarget){
							NotFind = false;
							AltNextBid.add(altNextBid);
						}
						loop++;
					}
					
					double minUtil = Double.MAX_VALUE;
					Bid minBid = null;
					for (int i=0; i<AltNextBid.size();i++){
						Bid bufBid = AltNextBid.get(i);
						Double bufUtil = utilitySpace.getUtilityWithDiscount(bufBid, time);
						if (minUtil > bufUtil){
							minUtil = bufUtil;
							minBid = bufBid;
						}else if (minUtil == bufUtil){
							BidHistory simHistory = currSessOppBidHistory.filterBetweenUtility(MINIMUM_BID_UTILITY, 1.0);
							if(this.similarBid(simHistory, bufBid) < this.similarBid(simHistory, minBid)){
								minBid = bufBid;
							}
						}
					nextBid = minBid;
				}
			} catch (Exception e) {
				// System.out.println("Problem with received bid:" +
				// e.getMessage() + ". cancelling bidding");
			}
		}

		if (nextBid == null) {
			// System.out.println("debug : emergency accept");
			return (new Accept(getAgentID()));
		}
		return (new Offer(getAgentID(), nextBid));
	}

	private int similarBid(BidHistory theHistory, Bid theBid) throws Exception{
		int Value = Integer.MAX_VALUE;
		ArrayList<BidDetails> AltList = (ArrayList<BidDetails>) theHistory.getNBestBids(theHistory.size()-1);
		for(int i=0; i<AltList.size(); i++){
			Bid targetBid = AltList.get(i).getBid();
			ArrayList<Issue> issues = utilitySpace.getDomain().getIssues();
			for (Issue lIssue : issues) {
				switch (lIssue.getType()) {
				case DISCRETE:
					IssueDiscrete lIssueDiscrete = (IssueDiscrete) lIssue;
					double weight_d = utilitySpace.getWeight(lIssueDiscrete.getNumber());
					if (theBid.getValue(lIssueDiscrete.getNumber()) == targetBid.getValue(lIssueDiscrete.getNumber()))
						Value += 1.0*weight_d;
					break;
				case REAL:
					IssueReal lIssueReal = (IssueReal) lIssue;
					double weight_r = utilitySpace.getWeight(lIssueReal.getNumber());
					if (theBid.getValue(lIssueReal.getNumber()) == targetBid.getValue(lIssueReal.getNumber()))
						Value += 1.0*weight_r;
					break;
				case INTEGER:
					IssueInteger lIssueInteger = (IssueInteger) lIssue;
					double weight_i = utilitySpace.getWeight(lIssueInteger.getNumber());
					if (theBid.getValue(lIssueInteger.getNumber()) == targetBid.getValue(lIssueInteger.getNumber()))
						Value += 1.0*weight_i;
					break;
				default:
					throw new Exception("issue type " + lIssue.getType()
							+ " not supported by SimpleAgent2");
				}
			}
		}
		return Value;

	}

	private Bid searchBid() throws Exception {
		HashMap<Integer, Value> values = new HashMap<Integer, Value>();
		ArrayList<Issue> issues = utilitySpace.getDomain().getIssues();
		Random randomnr = new Random();

		Bid bid = null;

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
						+ randomnr.nextInt(lIssueInteger.getUpperBound()
								- lIssueInteger.getLowerBound());
				values.put(lIssueInteger.getNumber(), new ValueInteger(
						optionIndex2));
				break;
			default:
				throw new Exception("issue type " + lIssue.getType()
						+ " not supported by SimpleAgent2");
			}
		}
		bid = new Bid(utilitySpace.getDomain(), values);
		return bid;
	}
	
	private double adjustTremor(double time){
		if (currSessOppBidHistory.isEmpty()){
			return 0.0;
		}else{
			double avg = sum/rounds;
			//double histry_avg = currSessOppBidHistory.getAverageDiscountedUtility(utilitySpace);
			double histry_avg = currSessOppBidHistory.filterBetweenTime(0.0,timeline.getCurrentTime()).getAverageUtility();
			if (avg > histry_avg){
				return 0.3;
			}else{
				return -0.3;
			}
		}
	}


	double acceptProbability(Bid offeredBid) throws Exception {
		double time = timeline.getTime();
		double offeredUtility = utilitySpace.getUtilityWithDiscount(offeredBid,time);
		offeredBidMap.put(offeredBid, offeredUtility);

		sum += offeredUtility;
		sum2 += offeredUtility * offeredUtility;
		rounds++;

		double mean = sum / rounds;
		mean = 0.7*mean + 0.3*PrevMean;
		
		double variance = (sum2 / rounds) - (mean * mean);

		double deviation = Math.sqrt(variance * 12);
		if (Double.isNaN(deviation)) {
			deviation = 0.0;
		}

		double t = time * time * time;

		if (offeredUtility < 0 || offeredUtility > 1.05) {
			throw new Exception("utility " + offeredUtility + " outside [0,1]");
		}

		if (t < 0 || t > 1) {
			throw new Exception("time " + t + " outside [0,1]");
		}

		if (offeredUtility > 1.) {
			offeredUtility = 1;
		}

		double estimateMax = mean + ((1 - mean) * deviation);

		double alpha = 1 + tremor + (10 * mean) - (2 * tremor * mean);
		double beta = alpha + (Math.random() * tremor) - (tremor / 2);

		double preTarget = 1 - (Math.pow(time, alpha) * (1 - estimateMax));
		double preTarget2 = 1 - (Math.pow(time, beta) * (1 - estimateMax));

		double ratio = (deviation + 0.1) / (1 - preTarget);
		if (Double.isNaN(ratio) || ratio > 2.0) {
			ratio = 2.0;
		}

		double ratio2 = (deviation + 0.1) / (1 - preTarget2);
		if (Double.isNaN(ratio2) || ratio2 > 2.0) {
			ratio2 = 2.0;
		}

		target = ratio * preTarget + 1 - ratio;
		bidTarget = ratio2 * preTarget2 + 1 - ratio2;

		double m = t * (-300) + 400;
		if (target > estimateMax) {
			double r = target - estimateMax;
			double f = 1 / (r * r);
			if (f > m || Double.isNaN(f))
				f = m;
			double app = r * f / m;
			target = target - app;
		} else {
			target = estimateMax;
		}

		if (bidTarget > estimateMax) {
			double r = bidTarget - estimateMax;
			double f = 1 / (r * r);
			if (f > m || Double.isNaN(f))
				f = m;
			double app = r * f / m;
			bidTarget = bidTarget - app;
		} else {
			bidTarget = estimateMax;
		}

		// test code for Discount Factor
		if (FinalPhase){
		double discount_utility = utilitySpace.getUtilityWithDiscount(
                offeredBid, time);
        double discount_ratio = discount_utility / offeredUtility;
        if (!Double.isNaN(discount_utility)) {
            target *= discount_ratio;
            bidTarget *= discount_ratio;
        }
        //System.out.printf("%f, %f, %f, %f, %f, %f %n", time, estimateMax, target, offeredUtility, discount_utility, discount_ratio);
		}
		// test code for Discount Factor

		double utilityEvaluation = offeredUtility - estimateMax;
		double satisfy = offeredUtility - target;

		double p = (Math.pow(time, alpha) / 5) + utilityEvaluation + satisfy;
		if (p < 0.1) {
			p = 0.0;
		}
		// System.out.println("debug : n = " + n);
		// System.out.println("debug : Mean = " + mean);
		// System.out.println("debug : Variance = " + variance);
		// System.out.println("debug : Deviation = " + deviation);
		// System.out.println("debug : Time = " + time);
		// System.out.println("debug : Estimate Max = " + estimateMax);
		// System.out.println("debug : Bid Target = " + bidTarget);
		// System.out.println("debug : Eval Target = " + target);
		// System.out.println("debug : Offered Utility = " + offeredUtility);
		// System.out.println("debug : Accept Probability= " + p);
		// System.out.println("debug : Utility Evaluation = " + utilityEvaluation);
		// System.out.println("debug : Ssatisfy = " + satisfy);

		return p;
	}
}