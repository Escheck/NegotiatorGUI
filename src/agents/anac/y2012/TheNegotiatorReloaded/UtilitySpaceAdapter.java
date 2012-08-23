package agents.anac.y2012.TheNegotiatorReloaded;

import negotiator.Bid;
import negotiator.Domain;
import negotiator.utility.UtilitySpace;

public class UtilitySpaceAdapter extends UtilitySpace {
	
	private OpponentModel opponentModel;
		
	public UtilitySpaceAdapter(OpponentModel opponentModel, Domain domain) {
		this.opponentModel = opponentModel;
		this.domain = domain;
	}
	
	public double getUtility(Bid b)
	{ 
		double u=0.;
		try { u = opponentModel.getBidEvaluation(b); } 
		catch (Exception e) {
			e.printStackTrace();
			System.out.println("getNormalizedUtility failed. returning 0");u=0.;}
		return u;
	} 
}

