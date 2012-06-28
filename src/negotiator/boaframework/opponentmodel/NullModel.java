package negotiator.boaframework.opponentmodel;

import negotiator.Bid;
import negotiator.boaframework.OpponentModel;

/**
 * Placeholder to notify an agent that there is no opponent model available.
 * 
 * @author Mark Hendrikx
 */
public class NullModel extends OpponentModel {


	public void updateModel(Bid opponentBid, double time) { }
	
	@Override
	public String getName() {
		return "No Model";
	}
}
