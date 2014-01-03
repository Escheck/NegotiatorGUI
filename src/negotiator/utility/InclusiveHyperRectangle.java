package negotiator.utility;

import java.util.ArrayList;

import negotiator.Bid;
import negotiator.issue.ValueInteger;


public class InclusiveHyperRectangle extends HyperRectangle{

	private ArrayList<Bound> boundlist;
	private double utilityValue;
	private boolean isAllBidsAcceptable;
	
	public InclusiveHyperRectangle() {
		this.isAllBidsAcceptable=false;
	}
	
	public InclusiveHyperRectangle(boolean isAllOkay) {
		this.isAllBidsAcceptable=true;
	}
	
	public ArrayList<Bound> getBoundlist() {
		return boundlist;
	}
	public void setBoundlist(ArrayList<Bound> boundlist) {
		this.boundlist = boundlist;
	}
	public double getUtilityValue() {
		return utilityValue;
	}
	public void setUtilityValue(double utilityValue) {
		this.utilityValue = utilityValue;
	}
	
	@Override
	public double getUtility(Bid bid) throws Exception {
	
		if (this.isAllBidsAcceptable==true) //if there is no constraint at all
			return (utilityValue*weight);
		
		int issueValue;
		for (int i=0; i< boundlist.size(); i++) {
			issueValue= (int)((ValueInteger)bid.getValue(boundlist.get(i).getIssueIndex())).getValue();					
			if ( (boundlist.get(i).getMin() > issueValue) || (issueValue > boundlist.get(i).getMax()) )
				return 0.0;		
		}
		
		return utilityValue*weight;
	}

	public boolean isAllBidsOkay() {
		return isAllBidsAcceptable;
	}

	public void setAllBidsOkay(boolean isAllBidsOkay) {
		this.isAllBidsAcceptable = isAllBidsOkay;
	}
	
}
