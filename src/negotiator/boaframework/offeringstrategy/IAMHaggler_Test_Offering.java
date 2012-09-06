package negotiator.boaframework.offeringstrategy;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;

import Jama.Matrix;

import negotiator.Bid;
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
		concessionModel = new IAMHagglerOpponentConcessionModel(25, negotiationSession.getUtilitySpace());
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
		
		Matrix variences = concessionModel.getVariance();
		if(variences != null){

		StringWriter variancesWriter = new StringWriter();
		PrintWriter variancesPrintWriter = new PrintWriter(variancesWriter);
		variences.print(variancesPrintWriter, 10, 4);
		System.out.println("variances: " + variancesWriter.getBuffer().toString());
		System.out.println("Variance at 0: " + concessionModel.getVarianceAt(0));
		System.out.println("Variance at 25: " + concessionModel.getVarianceAt(25));
		
		Matrix means = concessionModel.getMeans();
		StringWriter meanWriter = new StringWriter();
		PrintWriter meanPrintWriter = new PrintWriter(meanWriter);
		means.print(meanPrintWriter, 10, 4);
		System.out.println("means: " + meanWriter.getBuffer().toString());
		System.out.println("Means at 0: " + concessionModel.getMeanAt(0));
		System.out.println("Means at 25: " + concessionModel.getMeanAt(25));
		}
		
		double targetUtil = 0.75;

		
		
		
		//double opponentUtility = negotiationSession.getOpponentBidHistory().getLastBidDetails().getMyUndiscountedUtil();
		//double targetUtil = IAMhagglerConcession.getTarget(opponentUtility, negotiationSession.getTime());
		
		//System.out.println("TestHaggler targetUtil:" + targetUtil);
		Bid bid = bidCreator.getBid(negotiationSession.getUtilitySpace(), targetUtil - 0.25, targetUtil +0.25);
		try {
			nextBid = new BidDetails(bid, negotiationSession.getUtilitySpace().getUtility(bid));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return nextBid;
	}

}
