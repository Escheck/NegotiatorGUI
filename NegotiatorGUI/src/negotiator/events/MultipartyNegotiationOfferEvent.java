package negotiator.events;


import java.util.ArrayList;

import negotiator.Party;
import negotiator.actions.Action;



public class MultipartyNegotiationOfferEvent extends NegotiationEvent
{
	private int round;				
	private double timeline; // [0, 1] using Timeline
	private Action action;   // action also keeps the party id 
	private ArrayList<Double> partyUtilities;
	private boolean agreementFound;
	
	public MultipartyNegotiationOfferEvent(Object source, int round, double time, Action action,	ArrayList<Double> partyUtilities, boolean agreementFound)
	{
		super(source);
		this.round=round;
		this.timeline=time;
		this.action=action;
		this.partyUtilities=partyUtilities;
		this.agreementFound=agreementFound;				
	}
	
	public int getRound() {
		return round;
	}
	
	public double getTimeline() {
		return timeline;
	}
	
	public Action getAction() {
		return action;
	}

	public ArrayList<Double> getPartyUtilities() {
		return partyUtilities;
	}
	
	public boolean getAgreementFound() {
		return agreementFound;
	}

	public String toString()
	{
		return "ActionEvent["+action.getAgent()+","+ action +" at "+round+" round]";
	}
	
}
