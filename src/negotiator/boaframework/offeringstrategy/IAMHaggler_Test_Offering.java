package negotiator.boaframework.offeringstrategy;

import java.util.HashMap;

import negotiator.DiscreteTimeline;
import negotiator.bidding.BidDetails;
import negotiator.boaframework.NegotiationSession;
import negotiator.boaframework.OMStrategy;
import negotiator.boaframework.OfferingStrategy;
import negotiator.boaframework.OpponentModel;
import negotiator.boaframework.SortedOutcomeSpace;
import negotiator.boaframework.offeringstrategy.anac2011.iamhaggler2011.RandomBidCreator;
import negotiator.boaframework.opponentmodel.IAMHagglerOpponentConcessionModel;
import negotiator.tournament.TournamentConfiguration;
import Jama.Matrix;


public class IAMHaggler_Test_Offering extends OfferingStrategy {

	private IAMHagglerOpponentConcessionModel concessionModel;
	protected RandomBidCreator bidCreator;
	private int amountOfSamples;
	private BidDetails MAX_UTILITY_BID;
	private Matrix variances;
	private Matrix means;

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
			amountOfRegressions = 10;
			System.out.println("Using default " + amountOfRegressions + " for amount of regressions.");
		}
		if (parameters.containsKey("s")) {
			double value = parameters.get("s");
			amountOfSamples = (int) value;
		} else {
			amountOfSamples = TournamentConfiguration.getIntegerOption("deadline", 10) / 2;
			System.out.println("Using default " + amountOfSamples + " for amount of samples.");
		}
		
		concessionModel = new IAMHagglerOpponentConcessionModel((int) amountOfRegressions, negotiationSession.getUtilitySpace(), amountOfSamples);
		bidCreator = new RandomBidCreator();
		MAX_UTILITY_BID = negotiationSession.getMaxBidinDomain();
		SortedOutcomeSpace outcomespace = new SortedOutcomeSpace(negotiationSession.getUtilitySpace());
		negotiationSession.setOutcomeSpace(outcomespace);
	}

	@Override
	public BidDetails determineOpeningBid() {
		if(!negotiationSession.getOpponentBidHistory().isEmpty()){
			double myUndiscountedUtil = negotiationSession.getOpponentBidHistory().getLastBidDetails().getMyUndiscountedUtil();
			double time = negotiationSession.getTime();
			concessionModel.updateModel(myUndiscountedUtil, time);
			System.out.println("IAMHagglerOpponentConcessionModel initialized with u = " + myUndiscountedUtil + ", t = " + time);
			
		}
		return MAX_UTILITY_BID;
	}

	@Override
	public BidDetails determineNextBid() {

		double myUndiscountedUtil = negotiationSession.getOpponentBidHistory().getLastBidDetails().getMyUndiscountedUtil();
		double time = negotiationSession.getTime();
		int round = ((DiscreteTimeline) negotiationSession.getTimeline()).getRound();
		concessionModel.updateModel(myUndiscountedUtil, time);

		variances = concessionModel.getVariance();
		means = concessionModel.getMeans();
//		System.out.println();
//		System.out.println("Round " + round + (variances == null ? ". Estimates still null" : ""));
//		System.out.println("model has been updated with u = " + myUndiscountedUtil + ", at t = " + time + " (which was offered in round " + (round - 1) + ").");
//		if(variances != null){
//
//			DecimalFormat formatter = new DecimalFormat("#.########");
//			DecimalFormatSymbols dfs = new DecimalFormatSymbols();
//			dfs.setDecimalSeparator('.');
//			formatter.setDecimalFormatSymbols(dfs);
//			
//
//			System.out.println("Current time\tCurrent utility\tPrediction for time\tMean\tVariance\t2 SD\tMean\tMean - 2SD\tMean + 2SD");
//
//			for (int i = 0; i <= amountOfSamples; i++)
//			{
//				double var = variances.get(i, 0);
//				double sd = Math.sqrt(var);
//				double mean = means.get(i, 0);
//				double predForTime = ((double) i / (double) amountOfSamples);
//
//				System.out.println(time + "\t" + myUndiscountedUtil + "\t" + predForTime + "\t" + mean + "\t" + formatter.format(var) + "\t" + (2 * sd) + "\t" 
//						+ mean + "\t" + (mean - 2*sd) + "\t" + (mean + 2*sd));
//			}
//		}

		return MAX_UTILITY_BID;
	}
	
	public Matrix getMeans()
	{
		return means;
	}
	
	public Matrix getVariances()
	{
		return variances;
	}
	
	public int getAmountOfSamples()
	{
		return amountOfSamples;
	}
}
