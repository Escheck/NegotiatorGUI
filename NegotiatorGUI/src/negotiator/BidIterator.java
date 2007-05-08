package negotiator;

import java.util.Iterator;

import negotiator.issue.Issue;

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
//			TODO how to loop through the Real Issue? Discretization?			
/*			if(lNewIndexes [i]<lIssue.getNumberOfValues()-1) {
				lNewIndexes [i]++;
				break;
			} else {
				lNewIndexes [i]=0;
			}*/
		}
		return lNewIndexes;
	}
	public Bid next() {
		// TODO Auto-generated method stub
		Bid lBid =null;
		int[] lNextIndexes = makeNextIndexes();
		if(fInit)
			fInit=false;
		else
			fValuesIndexes = lNextIndexes;
		try {
//			TODO how to loop through the Real Issue? Discretization?
			//lBid = new Bid(fDomain, fValuesIndexes);
		} catch (Exception e) {
			e.printStackTrace();
		}		
		return lBid;
	}

	public void remove() {
		// TODO Auto-generated method stub
		
	}

}
