package negotiator.utility;

import java.util.HashMap;

import negotiator.Bid;

public class ConditionalZeroOutcomeConstraint extends ZeroOutcomeContraint {

	private HashMap<Integer,String> checkList;
	
	public ConditionalZeroOutcomeConstraint(){
		this.checkList=new HashMap<Integer,String>();
	}
	
	@Override
	public void addContraint(Integer issueIndex, String conditionToBeCheck) {
		this.checkList.put(issueIndex, conditionToBeCheck);
	}
	
	
	@Override
	public boolean willGetZeroOutcomeUtility(Bid bid) throws Exception {
		
		Integer[] indices=new Integer[2];
		int count=0;
		for (Integer index : this.checkList.keySet()) {
			indices[count++]=index;
		}		
		
		if ((this.checkList.get(indices[0]).equals("numeric=positive"))  && (Integer.valueOf(bid.getValue(indices[0]).toString()) >0))   {
			if (!bid.getValue(indices[1]).toString().contains(this.checkList.get(indices[1])) )
				return true;
		} else if ((this.checkList.get(indices[0]).equals("numeric=negative"))  && (Integer.valueOf(bid.getValue(indices[0]).toString()) <0))   {
			
			if (!bid.getValue(indices[1]).toString().contains(this.checkList.get(indices[1])) )
				return true;
		
		}
			return false;
	}
	
		
}