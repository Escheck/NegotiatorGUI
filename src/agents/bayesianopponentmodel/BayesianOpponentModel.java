package agents.bayesianopponentmodel;

import java.util.ArrayList;
import java.util.Collections;


import negotiator.Bid;
import negotiator.Domain;
import negotiator.issue.*;
import negotiator.utility.*;

public class BayesianOpponentModel extends OpponentModel{
	
	private UtilitySpace fUS;
	private WeightHypothesis[] fWeightHyps;
	private ArrayList<ArrayList<EvaluatorHypothesis>> fEvaluatorHyps;
	private ArrayList<EvaluatorHypothesis[]> fEvalHyps;	
	private ArrayList<UtilitySpaceHypothesis> fUSHyps;
	private boolean fUseMostProbableHypsOnly = true;
	private ArrayList<UtilitySpaceHypothesis> fMostProbableUSHyps;	
	private double fPreviousBidUtility;
	private double EXPECTED_CONCESSION_STEP = 0.035;
	private double SIGMA = 0.25;
	private boolean USE_DOMAIN_KNOWLEDGE = false;
	ArrayList<Issue> issues;
	
	public BayesianOpponentModel(UtilitySpace pUtilitySpace) {
		//
		if (pUtilitySpace==null) throw new NullPointerException("pUtilitySpace=null");
		fDomain = pUtilitySpace.getDomain();
		fPreviousBidUtility = 1;
		fUS = pUtilitySpace;
		fBiddingHistory = new ArrayList<Bid>();
		issues=fDomain.getIssues();
		int lNumberOfHyps = factorial(issues.size());
		fWeightHyps = new WeightHypothesis[lNumberOfHyps+1];
		//generate all possible ordering combinations of the weights
		int index = 0;
		double[] P = new double[issues.size()];
		//take care of weights normalization
		for(int i=0; i<issues.size();i++) P[i] = (i+1)/((double)((issues.size()*(fDomain.getIssues().size()+1))/2.0));
		//build all possible orderings of the weights from P
		antilex(new Integer(index), fWeightHyps, P, fDomain.getIssues().size()-1);
		//add the all equal hyp
		WeightHypothesis allEqual = new WeightHypothesis(fDomain);
		for(int i=0;i< issues.size();i++)
			allEqual.setWeight(i, 1./((double)(issues.size())));
		//set uniform probability distribution to the weights hyps
		fWeightHyps[fWeightHyps.length-1] = allEqual;
		for(int i=0;i<fWeightHyps.length;i++) fWeightHyps[i].setProbability(1./fWeightHyps.length);
		//generate all possible hyps of evaluation functions
		fEvaluatorHyps =  new ArrayList<ArrayList<EvaluatorHypothesis>> ();
		int lTotalTriangularFns = 1;
		for(int i =0; i<fUS.getNrOfEvaluators();i++) {
			switch(fUS.getEvaluator(issues.get(i).getNumber()).getType()) {
			case PRICE:
				ArrayList<EvaluatorHypothesis> lEvalHyps = new ArrayList<EvaluatorHypothesis>();
				fEvaluatorHyps.add(lEvalHyps);
				//EvaluatorReal lEval = (EvaluatorReal)(fUS.getEvaluator(i));
				IssueReal lIssuePrice = (IssueReal)(fDomain.getIssue(i));
				//uphill
				EvaluatorReal lHypEval = new EvaluatorReal();
				lHypEval.setUpperBound(lIssuePrice.getUpperBound());
				lHypEval.setLowerBound(lIssuePrice.getLowerBound());
				lHypEval.setType(EVALFUNCTYPE.LINEAR);
				lHypEval.addParam(1, 1.0/(lHypEval.getUpperBound()-lHypEval.getLowerBound()));
				lHypEval.addParam(0, -lHypEval.getLowerBound()/(lHypEval.getUpperBound()-lHypEval.getLowerBound()));				
				EvaluatorHypothesis lEvaluatorHypothesis = new EvaluatorHypothesis (lHypEval);
				lEvaluatorHypothesis.setDesc("uphill");
				lEvalHyps.add(lEvaluatorHypothesis);
				//downhill
				lHypEval = new EvaluatorReal();
				lHypEval.setUpperBound(lIssuePrice.getUpperBound());
				lHypEval.setLowerBound(lIssuePrice.getLowerBound());				
				lHypEval.setType(EVALFUNCTYPE.LINEAR);				
				lHypEval.addParam(1, -1./(lHypEval.getUpperBound()-lHypEval.getLowerBound()));
				lHypEval.addParam(0, 1.+lHypEval.getLowerBound()/(lHypEval.getUpperBound()-lHypEval.getLowerBound()));				
				lEvaluatorHypothesis = new EvaluatorHypothesis (lHypEval);
				lEvalHyps.add(lEvaluatorHypothesis);
				lEvaluatorHypothesis.setDesc("downhill");
				//triangular
				for(int k=1;k<=lTotalTriangularFns;k++) {
					//triangular
					lHypEval = new EvaluatorReal();
					lHypEval.setUpperBound(lIssuePrice.getUpperBound());
					lHypEval.setLowerBound(lIssuePrice.getLowerBound());
					lHypEval.setType(EVALFUNCTYPE.TRIANGULAR);
					lHypEval.addParam(0, lHypEval.getLowerBound());
					lHypEval.addParam(1, lHypEval.getUpperBound());
					lHypEval.addParam(2, lHypEval.getLowerBound()+(double)k*(lHypEval.getUpperBound()-lHypEval.getLowerBound())/(lTotalTriangularFns+1));				
					lEvaluatorHypothesis = new EvaluatorHypothesis (lHypEval);
					lEvalHyps.add(lEvaluatorHypothesis);
					lEvaluatorHypothesis.setDesc("triangular");
				}
				for(int k=0;k<lEvalHyps.size();k++) {
					lEvalHyps.get(k).setProbability((double)1./lEvalHyps.size());
				}
				break;
				
			case REAL:
				lEvalHyps = new ArrayList<EvaluatorHypothesis>();
				fEvaluatorHyps.add(lEvalHyps);
				//EvaluatorReal lEval = (EvaluatorReal)(fUS.getEvaluator(i));
				IssueReal lIssue = (IssueReal)(fDomain.getIssue(i));
				if(USE_DOMAIN_KNOWLEDGE) {
					//uphill
					lHypEval = new EvaluatorReal();
					lHypEval.setUpperBound(lIssue.getUpperBound());
					lHypEval.setLowerBound(lIssue.getLowerBound());
					lHypEval.setType(EVALFUNCTYPE.LINEAR);
					lHypEval.addParam(1, 1./(lHypEval.getUpperBound()-lHypEval.getLowerBound()));
					lHypEval.addParam(0, -lHypEval.getLowerBound()/(lHypEval.getUpperBound()-lHypEval.getLowerBound()));				
					lEvaluatorHypothesis = new EvaluatorHypothesis (lHypEval);
					lEvaluatorHypothesis.setDesc("uphill");
					lEvalHyps.add(lEvaluatorHypothesis);
					
				} else {
				//uphill
				lHypEval = new EvaluatorReal();
				lHypEval.setUpperBound(lIssue.getUpperBound());
				lHypEval.setLowerBound(lIssue.getLowerBound());
				lHypEval.setType(EVALFUNCTYPE.LINEAR);
				lHypEval.addParam(1, 1./(lHypEval.getUpperBound()-lHypEval.getLowerBound()));
				lHypEval.addParam(0, -lHypEval.getLowerBound()/(lHypEval.getUpperBound()-lHypEval.getLowerBound()));				
				lEvaluatorHypothesis = new EvaluatorHypothesis (lHypEval);
				lEvaluatorHypothesis.setDesc("uphill");
				lEvalHyps.add(lEvaluatorHypothesis);
				//downhill
				lHypEval = new EvaluatorReal();
				lHypEval.setUpperBound(lIssue.getUpperBound());
				lHypEval.setLowerBound(lIssue.getLowerBound());				
				lHypEval.setType(EVALFUNCTYPE.LINEAR);				
				lHypEval.addParam(1, -1.0/(lHypEval.getUpperBound()-lHypEval.getLowerBound()));
				lHypEval.addParam(0, 1.0+lHypEval.getLowerBound()/(lHypEval.getUpperBound()-lHypEval.getLowerBound()));				
				lEvaluatorHypothesis = new EvaluatorHypothesis (lHypEval);
				lEvalHyps.add(lEvaluatorHypothesis);
				lEvaluatorHypothesis.setDesc("downhill");
				//triangular
				for(int k=1;k<=lTotalTriangularFns;k++) {
					//triangular
					lHypEval = new EvaluatorReal();
					lHypEval.setUpperBound(lIssue.getUpperBound());
					lHypEval.setLowerBound(lIssue.getLowerBound());
					lHypEval.setType(EVALFUNCTYPE.TRIANGULAR);
					lHypEval.addParam(0, lHypEval.getLowerBound());
					lHypEval.addParam(1, lHypEval.getUpperBound());
					lHypEval.addParam(2, lHypEval.getLowerBound()+(double)k*(lHypEval.getUpperBound()-lHypEval.getLowerBound())/(lTotalTriangularFns+1));
					lEvaluatorHypothesis = new EvaluatorHypothesis (lHypEval);
					lEvaluatorHypothesis.setProbability((double)1/3);
					lEvalHyps.add(lEvaluatorHypothesis);
					lEvaluatorHypothesis.setDesc("triangular");
				}
				}
				for(int k=0;k<lEvalHyps.size();k++) {
					lEvalHyps.get(k).setProbability((double)1/lEvalHyps.size());
				}
				
				break;
			case DISCRETE:
				lEvalHyps = new ArrayList<EvaluatorHypothesis>();
				fEvaluatorHyps.add(lEvalHyps);
				//EvaluatorReal lEval = (EvaluatorReal)(fUS.getEvaluator(i));
				IssueDiscrete lDiscIssue = (IssueDiscrete)(fDomain.getIssue(i));
				if(USE_DOMAIN_KNOWLEDGE) {
					//uphill
					EvaluatorDiscrete lDiscreteEval = new EvaluatorDiscrete();
					for(int j=0;j<lDiscIssue.getNumberOfValues();j++) 
						lDiscreteEval.addEvaluation(lDiscIssue.getValue(j),	1000*j);
					lEvaluatorHypothesis = new EvaluatorHypothesis (lDiscreteEval );
					lEvaluatorHypothesis.setDesc("uphill");
					lEvalHyps.add(lEvaluatorHypothesis);
					
				} else {
				//uphill
				EvaluatorDiscrete lDiscreteEval = new EvaluatorDiscrete();
				for(int j=0;j<lDiscIssue.getNumberOfValues();j++) 
					lDiscreteEval.addEvaluation(lDiscIssue.getValue(j), 1000*j+1);
				lEvaluatorHypothesis = new EvaluatorHypothesis (lDiscreteEval );
				lEvaluatorHypothesis.setDesc("uphill");
				lEvalHyps.add(lEvaluatorHypothesis);
				//downhill
				lDiscreteEval = new EvaluatorDiscrete();
				for(int j=0;j<lDiscIssue.getNumberOfValues();j++) 
					lDiscreteEval.addEvaluation(lDiscIssue.getValue(j), 1000*(lDiscIssue.getNumberOfValues()-j-1)+1);
				lEvaluatorHypothesis = new EvaluatorHypothesis (lDiscreteEval);
				lEvalHyps.add(lEvaluatorHypothesis);
				lEvaluatorHypothesis.setDesc("downhill");
				//triangular
				lDiscreteEval = new EvaluatorDiscrete();
				int halfway=lDiscIssue.getNumberOfValues()/2;
				for(int j=0;j<lDiscIssue.getNumberOfValues();j++)
					if(j<halfway)
						lDiscreteEval.addEvaluation(lDiscIssue.getValue(j),1000*j+1);
								//(double)j/(((double)(lDiscIssue.getNumberOfValues()-2))/2));
					else
						lDiscreteEval.addEvaluation(lDiscIssue.getValue(j), 
								1000*(lDiscIssue.getNumberOfValues()-j-1)+1);
								//1.0-(j-((double)(lDiscIssue.getNumberOfValues())-1)/2)/(((double)(lDiscIssue.getNumberOfValues())-1)-((double)(lDiscIssue.getNumberOfValues())-1)/2));
				lEvaluatorHypothesis = new EvaluatorHypothesis (lDiscreteEval);
				lEvalHyps.add(lEvaluatorHypothesis);
				lEvaluatorHypothesis.setDesc("triangular");
				}
				break;
				
			}
		}
		buildEvaluationHyps();
		buildUniformHyps();
	}
	

	
	private void buildUniformHyps() {
		fUSHyps = new ArrayList<UtilitySpaceHypothesis>();
		for(int i=0;i<fWeightHyps.length;i++) {
			//EvaluatorHypothesis[] lEvalHyps = new EvaluatorHypothesis[fUS.getNrOfEvaluators()];
			for(int j=0;j<fEvalHyps.size();j++) {
				UtilitySpaceHypothesis lUSHyp = new UtilitySpaceHypothesis(fDomain, fUS, fWeightHyps[i], fEvalHyps.get(j));
				fUSHyps.add(lUSHyp);
			}
		}
		//normalize intial utilities
		for(int i=0;i<fUSHyps.size();i++) {
			fUSHyps.get(i).setProbability(1/(double)(fUSHyps.size()));
		}
		
	}
	private void reverse(double[] P, int m) {
		int i=0, j=m;
		while(i<j) {
			//swap elements i and j
			double lTmp = P[i];
			P[i]=P[j];
			P[j] = lTmp;
			++i;
			--j;
		}
	}
	private Integer antilex(Integer index, WeightHypothesis[] hyps, double[] P, int m) {
		if(m==0) {
			WeightHypothesis lWH = new WeightHypothesis(fDomain);
			for(int i=0; i<P.length;i++) lWH.setWeight(i, P[i]);
			hyps[index] = lWH;
			index++;
		} else {
			for(int i=0;i<=m;i++) {
				index = antilex(index, hyps, P, m-1);
				if(i<m) {
					//swap elements i and m
					double lTmp = P[i];
					P[i]=P[m];
					P[m] = lTmp;
					reverse(P, m-1);
				} //if
			}
		}
		return index;		
	}
	private double conditionalDistribution(double pUtility, double pPreviousBidUtility) {
		//TODO: check this conditionb
		if(pPreviousBidUtility<pUtility) return 0;
		else {
			
			double x = (pPreviousBidUtility - pUtility)/pPreviousBidUtility ; 
			double lResult = 1/(SIGMA*Math.sqrt(2*Math.PI)) *Math.exp(-(x*x)/(2*SIGMA*SIGMA));			
			return lResult;
		}
	}
	public void updateBeliefs(Bid pBid) throws Exception{
		fBiddingHistory.add(pBid);
		//calculate full probability for the given bid
		double lFullProb = 0;
		double lMaxProb = 0;
		for(int i=0;i<fUSHyps.size();i++) {
			UtilitySpaceHypothesis hyp =fUSHyps.get(i);
			double condDistrib = hyp.getProbability()*conditionalDistribution(fUSHyps.get(i).getUtility(pBid), fPreviousBidUtility);
			lFullProb += condDistrib;
			if(condDistrib>lMaxProb) lMaxProb = condDistrib;
			hyp.setProbability(condDistrib);
		}
		if(fUseMostProbableHypsOnly )  fMostProbableUSHyps = new ArrayList<UtilitySpaceHypothesis>();
		//update the weights hyps and evaluators hyps
		double lMostProbableHypFullProb = 0;
		for(int i=0;i<fUSHyps.size();i++) {			
			UtilitySpaceHypothesis hyp = fUSHyps.get(i);
			double normalizedProbability =hyp.getProbability()/lFullProb; 
			hyp.setProbability(normalizedProbability);
			if(fUseMostProbableHypsOnly)
				if(normalizedProbability>lMaxProb*0.75/lFullProb) {
					fMostProbableUSHyps.add(hyp);
					lMostProbableHypFullProb += normalizedProbability;
				}
		}
		if(fUseMostProbableHypsOnly) { 
			for(int i=0;i<fMostProbableUSHyps.size();i++) {			
				UtilitySpaceHypothesis hyp = fMostProbableUSHyps.get(i);
				double normalizedProbability =hyp.getProbability()/lMostProbableHypFullProb; 
				hyp.setProbability(normalizedProbability);
			}
		}
		
/*		sortHyps();
		for(int i=0;i<10;i++) {
			System.out.println(fUSHyps.get(i).toString());
		}
	*/	
		System.out.println(getMaxHyp().toString());
		//calculate utility of the next partner's bid according to the concession functions
		fPreviousBidUtility = fPreviousBidUtility-EXPECTED_CONCESSION_STEP;
		//findMinMaxUtility();
	}
	
	private void sortHyps() {
		Collections.sort(fUSHyps, new HypsComparator());
	}
	
	private void buildEvaluationHypsRecursive(ArrayList<EvaluatorHypothesis[]> pHyps,
											  EvaluatorHypothesis[] pEval,
											  int m) {
		if(m==0) {			 
			ArrayList<EvaluatorHypothesis> lEvalHyps = fEvaluatorHyps.get(fUS.getNrOfEvaluators()-1);
			for (int i=0;i<lEvalHyps.size();i++) {
				pEval[fUS.getNrOfEvaluators()-1] = lEvalHyps.get(i);
				EvaluatorHypothesis[] lTmp = new EvaluatorHypothesis[fUS.getNrOfEvaluators()];
				//copy to temp array
				for(int j =0;j<lTmp.length;j++)
					lTmp[j] = pEval[j]; 
				pHyps.add(lTmp);
			}
		} else {
			ArrayList<EvaluatorHypothesis> lEvalHyps = fEvaluatorHyps.get(fUS.getNrOfEvaluators()-m-1);
			for (int i=0;i<lEvalHyps.size();i++) {
				pEval[fUS.getNrOfEvaluators()-m-1] = lEvalHyps.get(i);
				buildEvaluationHypsRecursive(pHyps, pEval, m-1);
			}			
		}
	}
	
	private void buildEvaluationHyps() {
		fEvalHyps = new ArrayList<EvaluatorHypothesis[]>();
		EvaluatorHypothesis[] lTmp = new EvaluatorHypothesis[fUS.getNrOfEvaluators()];
		buildEvaluationHypsRecursive(fEvalHyps, lTmp, fUS.getNrOfEvaluators()-1);		
	}
	public double getExpectedUtility(Bid pBid)  throws Exception{
		double lExpectedUtility = 0;
		if(fUseMostProbableHypsOnly &&(fMostProbableUSHyps!=null)) {
			for(int i=0;i<fMostProbableUSHyps.size();i++) {
				UtilitySpaceHypothesis lUSHyp = fMostProbableUSHyps.get(i);
				double p = lUSHyp.getProbability();
				double u = lUSHyp.getUtility(pBid);
				lExpectedUtility += p*u;
			}			
		} else { 
			for(int i=0;i<fUSHyps.size();i++) {
				UtilitySpaceHypothesis lUSHyp = fUSHyps.get(i);
				double p = lUSHyp.getProbability();
				double u = lUSHyp.getUtility(pBid);
				lExpectedUtility += p*u;
			}
		}
		return lExpectedUtility;
		
	}
	
	public double getExpectedWeight(int pIssueNumber) {
		double lExpectedWeight = 0;
		for(int i=0;i<fUSHyps.size();i++) {
			UtilitySpaceHypothesis lUSHyp = fUSHyps.get(i);
			double p = lUSHyp.getProbability();
			double u = lUSHyp.getHeightHyp().getWeight(pIssueNumber);
			lExpectedWeight += p*u;
		}
		return lExpectedWeight;
	}	

	
	private UtilitySpaceHypothesis getMaxHyp() {
		UtilitySpaceHypothesis lHyp = fUSHyps.get(0);
		for(int i=0; i<fUSHyps.size();i++) {
			if(lHyp.getProbability()<fUSHyps.get(i).getProbability()) 
				lHyp = fUSHyps.get(i);
		}
		return lHyp;
	}
/*	public double getExpectedUtility(Bid pBid) {
		double lExpectedUtility = 0;
		for(int i=0;i<fWeightHyps.length;i++) {
			WeightHypothesis lWeightHyp = fWeightHyps[i];
			double p = lWeightHyp.getProbability();
			double u = 0;
			for(int j=0;j<fEvalHyps.size();j++) {
				EvaluatorHypothesis[] lHyp = fEvalHyps.get(j);
				//calculate evaluation value and probability
				for(int k=0;k<lHyp.length;k++) {
					p = p*lHyp[k].getProbability();
					u = u + lWeightHyp.getWeight(k)*(Double)(lHyp[k].getEvaluator().getEvaluation(fUS, pBid, k));
				}
				lExpectedUtility = lExpectedUtility+ p*u;
			}
		}
		return 0;
	}*/
//	Evaluate n!
	private int factorial( int n )
	{
		if( n <= 1 )     // base case
			return 1;
		else
			return n * factorial( n - 1 );
	}
	

	protected  class HypsComparator implements java.util.Comparator
	{
		public int compare(Object o1,Object o2) throws ClassCastException
		{
			if(!(o1 instanceof UtilitySpaceHypothesis)) {
				throw new ClassCastException();
			}
			if(!(o2 instanceof UtilitySpaceHypothesis)) {
				throw new ClassCastException();
			}
			double d1 = ((UtilitySpaceHypothesis)o1).getProbability();
			double d2 = ((UtilitySpaceHypothesis)o2).getProbability();

			if (d1 > d2) {
				return -1; 
			}
			else if (d1 < d2) {
				return 1; 
			}
			else {
				return 0;
			}
		}
	}     
	
}
