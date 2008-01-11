package agents.similarity;

import java.util.ArrayList;

import negotiator.Bid;
import negotiator.Domain;
import negotiator.xml.SimpleElement;

public class SimilarityFunction {

	private double fWeights[];
	private ArrayList<Criteria> fCriteria;
	private Domain fDomain;
	public SimilarityFunction(Domain pDomain) {
		fDomain = pDomain;
		fCriteria = new ArrayList<Criteria>();
	}
	
	public double getSimilarityValue(Bid pMyBid, Bid pOpponentBid) {
		double lResult=0;
		for (int i=0 ; i<fCriteria.size();i++) {
			Criteria lCriteria = fCriteria.get(i);
			lResult += fWeights[i]*(1-Math.abs(lCriteria.getValue(pMyBid)-lCriteria.getValue(pOpponentBid)));
		}
		return lResult;
	}
    public void loadFromXML(SimpleElement pRoot, int pIssueIndex) {
    	Object[] lXMLCriteriaFn = pRoot.getChildByTagName("criteria_function");
    	fWeights = new double[lXMLCriteriaFn.length];
    	//read similarity functions
    	for(int i=0;i<lXMLCriteriaFn.length;i++) {
    		//TODO: DT: finish loading from XML for CriteriaDiscrete
			//load weights
			fWeights[i] =Double.valueOf(((SimpleElement)(lXMLCriteriaFn[i])).getAttribute("weight"));			
			Criteria lCriteria  =null;
    		switch(fDomain.getObjective(pIssueIndex).getType()) {
    		case REAL:
    			lCriteria  = new CriteriaReal(fDomain, pIssueIndex);
    			lCriteria.loadFromXML((SimpleElement)(lXMLCriteriaFn[i]));
    			fCriteria.add(lCriteria );
    			break;
    		case DISCRETE:
    			lCriteria  = new CriteriaDiscrete(pIssueIndex);
    			lCriteria.loadFromXML((SimpleElement)(lXMLCriteriaFn[i]));
    			fCriteria.add(lCriteria );
    			break;
    		}
    	}
    }

}
