package negotiator.boaframework.opponentmodel;

import negotiator.Bid;
import negotiator.boaframework.OpponentModel;

/**
 * Placeholder to notify an agent that there is no opponent model available.
 * 
 * Tim Baarslag, Koen Hindriks, Mark Hendrikx, Alex Dirkzwager and Catholijn M. Jonker.
 * Decoupling Negotiating Agents to Explore the Space of Negotiation Strategies
 * 
 * @author Mark Hendrikx
 */
public class NoModel extends OpponentModel {


	public void updateModel(Bid opponentBid, double time) { }
	
	@Override
	public String getName() {
		return "No Model";
	}
}
