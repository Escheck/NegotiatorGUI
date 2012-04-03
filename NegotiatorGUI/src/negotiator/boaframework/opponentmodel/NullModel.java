package negotiator.decoupledframework.opponentmodel;

import negotiator.Bid;
import negotiator.decoupledframework.OpponentModel;

/**
 * Placeholder to notify an agent that there is no opponent model available.
 * 
 * @author Mark Hendrikx
 */
public class NullModel extends OpponentModel {


	public void updateModel(Bid opponentBid) { }
	
	@Override
	public boolean isCompleteModel() {
		return false;
	}
}
