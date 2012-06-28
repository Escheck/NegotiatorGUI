package negotiator.boaframework.opponentmodel;

import negotiator.Bid;
import negotiator.Domain;
import negotiator.boaframework.OpponentModel;
import negotiator.utility.UtilitySpace;

/**
 * Some opponent models do not use the UtilitySpace-object. Using this
 * object a UtilitySpace-object can be created for each opponent model.
 * 
 * @author Mark Hendrikx
 */
public class UtilitySpaceAdapter extends UtilitySpace {
	
	private OpponentModel opponentModel;
		
	public UtilitySpaceAdapter(OpponentModel opponentModel, Domain domain) {
		this.opponentModel = opponentModel;
		this.domain = domain;
	}
	
	public double getUtility(Bid b)
	{ 
		double u=0.;
		try {
			u = opponentModel.getBidEvaluation(b);
		} 
		catch (Exception e) {
			e.printStackTrace();
			System.err.println("getNormalizedUtility failed. returning 0");
			u = 0.0;
		}
		return u;
	} 
}