package negotiator.utility;

import java.util.ArrayList;

import negotiator.Bid;

public abstract class HyperRectangle extends Constraint{
	
	public abstract ArrayList<Bound> getBoundlist();		
	public abstract void setBoundlist(ArrayList<Bound> boundlist);
	public abstract double getUtilityValue();
	public abstract void setUtilityValue(double utilityValue);
	public abstract double getUtility(Bid bid) throws Exception;		

}
