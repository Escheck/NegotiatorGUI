package negotiator.events;

import negotiator.Agent;
import negotiator.actions.Action;


/** This class records details about an action of an agent. 
 * It is passed as event to interested parties (currently the logger&data display GUI).
 * 
 * 
 * If there is a time-out or other protocol error,
 * an additional EndNegotiation action will be created
 * by the NegotiationManager and sent to listener.
 * 
 * @author wouter
 *
 */
public class ActionEvent extends NegotiationEvent
{
	Agent actor;
	Action act;   				// Bid, Accept, etc.
	int round;					// integer 0,1,2,...: round in the overall bidding.
	long elapsedMilliseconds;	// milliseconds since start of nego. Using System.currentTimeMillis();
	double time;				// [0, 1] using Timeline
	double normalizedUtilityA;
	double normalizedUtilityB;
	double utilADiscount;
	double utilBDsicount;
	String errorRemarks;		// errors 
	
	public ActionEvent(Object source, Agent actorP,Action actP,int roundP,long elapsed, double t,
			double utilA,double utilB, double utilADiscount, double utilBDsicount, String remarks)
	{
		super(source);
		actor=actorP;
		act=actP;
		round=roundP;
		elapsedMilliseconds=elapsed;
		time=t;
		normalizedUtilityA=utilA;
		normalizedUtilityB=utilB;
		this.utilADiscount = utilADiscount;
		this.utilBDsicount = utilBDsicount;
		errorRemarks=remarks;
	}
	
	public double getUtilADiscount() {
		return utilADiscount;
	}

	public double getUtilBDsicount() {
		return utilBDsicount;
	}

	public String toString()
	{
		return "ActionEvent["+actor+","+act+","+round+","+elapsedMilliseconds+","+
		normalizedUtilityA+","+normalizedUtilityB+","+errorRemarks+"]";
	}

	public Agent getActor() {
		return actor;
	}

	public Action getAct() {
		return act;
	}

	public int getRound() {
		return round;
	}

	public long getElapsedMilliseconds() {
		return elapsedMilliseconds;
	}
	
	public double getTime()
	{
		return time;
	}

	public double getNormalizedUtilityA() {
		return normalizedUtilityA;
	}
	public String getAgentAsString() {
		return actor == null ? "null" : actor.getName();
	}

	public double getNormalizedUtilityB() {
		return normalizedUtilityB;
	}

	public String getErrorRemarks() {
		return errorRemarks;
	}
	
	
}
