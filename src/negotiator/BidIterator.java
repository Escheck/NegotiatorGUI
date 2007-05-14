package negotiator;

import java.util.Iterator;

import negotiator.issue.ISSUETYPE;
import negotiator.issue.*;

public class BidIterator implements Iterator {
	protected Domain fDomain;
	protected int fNumberOfIssues;
	protected int[] fValuesIndexes;
	protected boolean fInit ;
	public BidIterator(Domain pDomain) {
		fDomain = pDomain;
		fInit=true;
		fNumberOfIssues = fDomain.getNumberOfIssues();
		fValuesIndexes= new int[fNumberOfIssues ];
		for(int i=0;i<fNumberOfIssues ;i++) {
			fValuesIndexes[i]=0;			
		}

	}
	public boolean hasNext() {
		int[] lNextIndexes = makeNextIndexes();
		boolean result=false;
		if(fInit) {
			return true;
		} else {
			for(int i=0;i<fNumberOfIssues;i++)
				if(lNextIndexes[i]!=0) {				
					result = true;
					break;
				}
			return result;
		}
//		return fHasNext;
	}
	private int[] makeNextIndexes() {
		int[] lNewIndexes = new int[fNumberOfIssues];
		for(int i=0;i<fNumberOfIssues;i++) 
			lNewIndexes [i] = fValuesIndexes[i];
		for(int i=0;i<fNumberOfIssues;i++) {
			Issue lIssue = fDomain.getIssue(i);
//			to loop through the Real and Price Issues we use discretization
			int lNumberOfValues=0;
			switch(lIssue.getType()) {
			case INTEGER:
				IssueInteger lIssueInteger =(IssueInteger)lIssue;
				lNumberOfValues = lIssueInteger.getUpperBound()-lIssueInteger.getLowerBound()+1;				
			case REAL: 
				IssueReal lIssueReal =(IssueReal)lIssue;
				lNumberOfValues = lIssueReal.getNumberOfDiscretizationSteps();
				break;
			case DISCRETE:
				IssueDiscrete lIssueDiscrete = (IssueDiscrete)lIssue;
				lNumberOfValues = lIssueDiscrete.getNumberOfValues();
				break;
/* Removed by DT because KH removed PRICE
 * 
 			case PRICE:
				IssuePrice lIssuePrice = (IssuePrice)lIssue;
				lNumberOfValues = lIssuePrice.getNumberOfDiscretizationSteps();
				break;*/
			}// switch
			if(lNewIndexes [i]<lNumberOfValues-1) {
				lNewIndexes [i]++;
				break;
			} else {
				lNewIndexes [i]=0;
			}
			
		}//for
		return lNewIndexes;
	}
	public Bid next() {
		Bid lBid =null;
		int[] lNextIndexes = makeNextIndexes();
		if(fInit)
			fInit=false;
		else
			fValuesIndexes = lNextIndexes;
		try {
			Value[] lValues = new Value[fNumberOfIssues];
			for(int i=0;i<fNumberOfIssues;i++) {
				Issue lIssue = fDomain.getIssue(i);
				double lOneStep;
				switch(lIssue.getType()) {
				//TODO: COMPLETE add cases for all types of issues
				case INTEGER:
					IssueInteger lIssueInteger =(IssueInteger)lIssue;
					lValues[i]= new ValueInteger(lIssueInteger.getLowerBound()+fValuesIndexes[i]);
				case REAL: 
					IssueReal lIssueReal =(IssueReal)lIssue;
					lOneStep = (lIssueReal.getUpperBound()-lIssueReal.getLowerBound())/lIssueReal.getNumberOfDiscretizationSteps();
					lValues[i]= new ValueReal(lIssueReal.getLowerBound()+lOneStep*fValuesIndexes[i]);
					break;
					/* Removed by DT because KH removed PRICE
					 * 
					
				case PRICE: 
					IssuePrice lIssuePrice=(IssuePrice)lIssue;
					lOneStep = (lIssuePrice.getUpperBound()-lIssuePrice.getLowerBound())/lIssuePrice.getNumberOfDiscretizationSteps();
					lValues[i]= new ValueReal(lIssuePrice.getLowerBound()+lOneStep*fValuesIndexes[i]);
					break;
*/					
				case DISCRETE:
					IssueDiscrete lIssueDiscrete = (IssueDiscrete)lIssue;
					lValues[i] = lIssueDiscrete.getValue(fValuesIndexes[i]);
					break;
				}// switch
			}//for				
			lBid = new Bid(fDomain, lValues);
		} catch (Exception e) {
			e.printStackTrace();
		}		
		return lBid;
	}

	public void remove() {
		// TODO Auto-generated method stub
		
	}

}
