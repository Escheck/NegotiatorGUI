package negotiator.boaframework.offeringstrategy;

import java.util.HashMap;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import negotiator.Bid;
import negotiator.DiscreteTimeline;
import negotiator.bidding.BidDetails;
import negotiator.boaframework.IAMhaggler_Concession;
import negotiator.boaframework.NegotiationSession;
import negotiator.boaframework.OMStrategy;
import negotiator.boaframework.OfferingStrategy;
import negotiator.boaframework.OpponentModel;
import negotiator.boaframework.offeringstrategy.anac2011.iamhaggler2011.BidCreator;
import negotiator.boaframework.offeringstrategy.anac2011.iamhaggler2011.RandomBidCreator;
import negotiator.boaframework.opponentmodel.IAMHagglerOpponentConcessionModel;
import Jama.Matrix;


public class IAMHaggler_Test_Offering extends OfferingStrategy {

	private IAMHagglerOpponentConcessionModel concessionModel;
	protected BidCreator bidCreator;
	private int amountOfSamples;

	public IAMHaggler_Test_Offering() { }

	public IAMHaggler_Test_Offering(NegotiationSession negoSession, OpponentModel model, OMStrategy oms) throws Exception {
		init(negoSession, model, oms, null);
	}

	@Override
	public void init(NegotiationSession negotiationSession, OpponentModel opponentModel, OMStrategy omStrategy, HashMap<String, Double> parameters) throws Exception {
		super.init(negotiationSession, opponentModel, omStrategy, parameters);
		this.negotiationSession = negotiationSession;
		double amountOfRegressions;
		if (parameters.containsKey("r")) {
			amountOfRegressions = parameters.get("r");
		} else {
			System.out.println("Using default 10 for amount of regressions.");
			amountOfRegressions = 10;
		}
		if (parameters.containsKey("s")) {
			double value = parameters.get("s");
			amountOfSamples = (int) value;
		} else {
			amountOfSamples = 100;
			System.out.println("Using default 100 for amount of samples.");
		}
		
		concessionModel = new IAMHagglerOpponentConcessionModel((int) amountOfRegressions, negotiationSession.getUtilitySpace(), amountOfSamples);
		bidCreator = new RandomBidCreator();
	}

	@Override
	public BidDetails determineOpeningBid() {
		if(!negotiationSession.getOpponentBidHistory().isEmpty()){
			double myUndiscountedUtil = negotiationSession.getOpponentBidHistory().getLastBidDetails().getMyUndiscountedUtil();
			double time = negotiationSession.getTime();
			concessionModel.updateModel(myUndiscountedUtil, time);
			System.out.println("IAMHagglerOpponentConcessionModel initialized with u = " + myUndiscountedUtil + ", t = " + time);
			
		}
		return negotiationSession.getMaxBidinDomain();
	}

	@Override
	public BidDetails determineNextBid() {

		double myUndiscountedUtil = negotiationSession.getOpponentBidHistory().getLastBidDetails().getMyUndiscountedUtil();
		double time = negotiationSession.getTime();
		concessionModel.updateModel(myUndiscountedUtil, time);
		System.out.println("IAMHagglerOpponentConcessionModel updated with u = " + myUndiscountedUtil + ", t = " + time);

		Matrix variances = concessionModel.getVariance();
		Matrix means = concessionModel.getMeans();
		int round = ((DiscreteTimeline) negotiationSession.getTimeline()).getRound();
		System.out.println();
		System.out.println("Round " + round + (variances == null ? ". Estimates still null" : ""));
		if(variances != null){

			DecimalFormat formatter = new DecimalFormat("#.########");
			DecimalFormatSymbols dfs = new DecimalFormatSymbols();
			dfs.setDecimalSeparator('.');
			formatter.setDecimalFormatSymbols(dfs);
			

			System.out.println("Current time\tCurrent utility\tPrediction for time\tMean\tVariance\t2 SD\tMean\tMean - 2SD\tMean + 2SD");

			for (int i = 0; i <= amountOfSamples; i++)
			{
				double var = variances.get(i, 0);
				double sd = Math.sqrt(var);
				double mean = means.get(i, 0);
				double predForTime = ((double) i / (double) amountOfSamples);

				System.out.println(time + "\t" + myUndiscountedUtil + "\t" + predForTime + "\t" + mean + "\t" + formatter.format(var) + "\t" + (2 * sd) + "\t" 
						+ mean + "\t" + (mean - 2*sd) + "\t" + (mean + 2*sd));
			}
		}

		double targetUtil = 1;




		//double opponentUtility = negotiationSession.getOpponentBidHistory().getLastBidDetails().getMyUndiscountedUtil();
		//double targetUtil = IAMhagglerConcession.getTarget(opponentUtility, negotiationSession.getTime());

		//System.out.println("TestHaggler targetUtil:" + targetUtil);
		Bid bid = bidCreator.getBid(negotiationSession.getUtilitySpace(), targetUtil, targetUtil +0.25);
		try {
			nextBid = new BidDetails(bid, negotiationSession.getUtilitySpace().getUtility(bid));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return nextBid;
	}

}
