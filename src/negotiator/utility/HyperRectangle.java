package negotiator.utility;

import java.util.ArrayList;

import negotiator.Bid;
import negotiator.issue.ISSUETYPE;
import negotiator.issue.ValueInteger;


public class HyperRectangle extends Constraint{

	private ArrayList<Bound> boundlist;
	private double utilityValue;
	
	public HyperRectangle() {
		
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
		
		int issueValue;
		for (int i=0; i< boundlist.size(); i++) {
			issueValue= (int)((ValueInteger)bid.getValue(boundlist.get(i).getIssueIndex())).getValue();					
			if ( (boundlist.get(i).getMin() > issueValue) || (issueValue > boundlist.get(i).getMax()) )
				return 0.0;		
		}
		
		return utilityValue*weight;
	}
	
}
