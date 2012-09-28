package negotiator.boaframework.offeringstrategy;

import java.util.HashMap;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

import Jama.Matrix;

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


public class IAMHaggler_Test_Offering extends OfferingStrategy {

	private static final int SLOTS = 25;
	private IAMhaggler_Concession IAMhagglerConcession;
	private IAMHagglerOpponentConcessionModel concessionModel;
	protected BidCreator bidCreator;


	public IAMHaggler_Test_Offering() { }

	public IAMHaggler_Test_Offering(NegotiationSession negoSession, OpponentModel model, OMStrategy oms) throws Exception {
		init(negoSession, model, oms, null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see agents.southampton.SouthamptonAgent#init()
	 */
	@Override
	public void init(NegotiationSession negotiationSession, OpponentModel opponentModel, OMStrategy omStrategy, HashMap<String, Double> parameters) throws Exception {
		super.init(negotiationSession, opponentModel, omStrategy, parameters);
		this.negotiationSession = negotiationSession;
		concessionModel = new IAMHagglerOpponentConcessionModel(SLOTS, negotiationSession.getUtilitySpace());
		IAMhagglerConcession = new IAMhaggler_Concession(negotiationSession.getUtilitySpace());
		bidCreator = new RandomBidCreator();
	}

	@Override
	public BidDetails determineOpeningBid() {
		if(!negotiationSession.getOpponentBidHistory().isEmpty()){
			concessionModel.updateModel(negotiationSession.getOpponentBidHistory().getLastBidDetails().getMyUndiscountedUtil(), negotiationSession.getTime());
		}
		return negotiationSession.getMaxBidinDomain();
	}

	@Override
	public BidDetails determineNextBid() {

		concessionModel.updateModel(negotiationSession.getOpponentBidHistory().getLastBidDetails().getMyUndiscountedUtil(), negotiationSession.getTime());

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

			System.out.println("Mean\tVariance\t2 SD\tMean\tMean - 2SD\tMean + 2SD");
			for (int i = 0; i <= SLOTS; i++)
			{
				double var = variances.get(i, 0);
				double sd = Math.sqrt(var);
				double mean = means.get(i, 0);

				System.out.println(mean + "\t" + formatter.format(var) + "\t" + (2 * sd) + "\t" 
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
