package negotiator.agents.BayesianOpponentModel;

import java.util.ArrayList;
import java.util.Collections;

import negotiator.Bid;
import negotiator.Domain;
import negotiator.issue.*;
import negotiator.utility.*;

public class BayesianOpponentModel3 {
	
	private Domain fDomain;
	private UtilitySpace fUS;
//	private WeightHypothesis[] fWeightHyps;
	private ArrayList<ArrayList<EvaluatorHypothesis>> fEvaluatorHyps;
//	private ArrayList<EvaluatorHypothesis[]> fEvalHyps;
	private ArrayList<Bid> fBiddingHistory;
//	private ArrayList<UtilitySpaceHypothesis> fUSHyps;
	private double fPreviousBidUtility;
	public BayesianOpponentModel3(UtilitySpace pUtilitySpace) {
		//
		fPreviousBidUtility = 1;
		fDomain = pUtilitySpace.getDomain();
		fUS = pUtilitySpace;
		fBiddingHistory = new ArrayList<Bid>();
/*		int lNumberOfHyps = factorial(fDomain.getNumberOfIssues());
		fWeightHyps = new WeightHypothesis[lNumberOfHyps];
		//generate all possible ordering combinations of the weights
		int index = 0;
		double[] P = new double[fDomain.getNumberOfIssues()];
		//take care of weights normalization
		for(int i=0; i<fDomain.getNumberOfIssues();i++) P[i] = (i+1)/((double)((fDomain.getNumberOfIssues()+1)*2));
		//build all possible orderings of the weights from P
		antilex(new Integer(index), fWeightHyps, P, fDomain.getNumberOfIssues()-1);
		//set uniform probability distribution to the weights hyps
		for(int i=0;i<fWeightHyps.length;i++) fWeightHyps[i].setProbability((double)1/lNumberOfHyps);
		//generate all possible hyps of evaluation functions
	*/	
		fEvaluatorHyps =  new ArrayList<ArrayList<EvaluatorHypothesis>> ();
		int lTotalTriangularFns = 4;
		int lNumberOfWeights = 10;		
		for(int i =0; i<fUS.getNrOfEvaluators();i++) {
			switch(fUS.getEvaluator(i).getType()) {
			case PRICE:
				ArrayList<EvaluatorHypothesis> lEvalHyps = new ArrayList<EvaluatorHypothesis>();
				fEvaluatorHyps.add(lEvalHyps);
				//EvaluatorReal lEval = (EvaluatorReal)(fUS.getEvaluator(i));
				IssueReal lIssuePrice = (IssueReal)(fDomain.getIssue(i));
				//make hyps over evaluation functions with different max levels
				for(int n=1;n<=lNumberOfWeights;n++) {
					double lMaxValue = ((double)n)/lNumberOfWeights;
					//uphill
					EvaluatorReal lHypEval = new EvaluatorReal();
					lHypEval.setUpperBound(lIssuePrice.getUpperBound());
					lHypEval.setLowerBound(lIssuePrice.getLowerBound());
					lHypEval.setType(EVALFUNCTYPE.LINEAR);
					lHypEval.addParam(1, lMaxValue/(lHypEval.getUpperBound()-lHypEval.getLowerBound()));
					lHypEval.addParam(0, -lMaxValue*lHypEval.getLowerBound()/(lHypEval.getUpperBound()-lHypEval.getLowerBound()));				
					EvaluatorHypothesis lEvaluatorHypothesis = new EvaluatorHypothesis (lHypEval);
					lEvaluatorHypothesis.setProbability((double)1/3);
					lEvaluatorHypothesis.setDesc("uphill");
					lEvalHyps.add(lEvaluatorHypothesis);
					//downhill
					lHypEval = new EvaluatorReal();
					lHypEval.setUpperBound(lIssuePrice.getUpperBound());
					lHypEval.setLowerBound(lIssuePrice.getLowerBound());				
					lHypEval.setType(EVALFUNCTYPE.LINEAR);				
					lHypEval.addParam(1, -lMaxValue/(lHypEval.getUpperBound()-lHypEval.getLowerBound()));
					lHypEval.addParam(0, lMaxValue+lMaxValue*lHypEval.getLowerBound()/(lHypEval.getUpperBound()-lHypEval.getLowerBound()));				
					lEvaluatorHypothesis = new EvaluatorHypothesis (lHypEval);
					lEvaluatorHypothesis.setProbability((double)1/3);
					lEvalHyps.add(lEvaluatorHypothesis);
					lEvaluatorHypothesis.setDesc("downhill");
					//	triangular
					lHypEval = new EvaluatorReal();
					lHypEval.setUpperBound(lIssuePrice.getUpperBound());
					lHypEval.setLowerBound(lIssuePrice.getLowerBound());
					lHypEval.setType(EVALFUNCTYPE.TRIANGULAR_VARIABLE_TOP);
					lHypEval.addParam(0, lHypEval.getLowerBound());
					lHypEval.addParam(1, lHypEval.getUpperBound());
					lHypEval.addParam(2, lHypEval.getLowerBound()+(lHypEval.getUpperBound()-lHypEval.getLowerBound())/2);
					lHypEval.addParam(3, lMaxValue);
					lEvaluatorHypothesis = new EvaluatorHypothesis (lHypEval);
					lEvaluatorHypothesis.setProbability((double)1/3);
					lEvalHyps.add(lEvaluatorHypothesis);
					lEvaluatorHypothesis.setDesc("triangular");
				}
				break;
				
			case REAL:
				lEvalHyps = new ArrayList<EvaluatorHypothesis>();				
				fEvaluatorHyps.add(lEvalHyps);
				//EvaluatorReal lEval = (EvaluatorReal)(fUS.getEvaluator(i));
				IssueReal lIssue = (IssueReal)(fDomain.getIssue(i));				
				for(int n=1;n<=lNumberOfWeights;n++) {
					double lMaxValue = ((double)n)/lNumberOfWeights;
					//uphill
					EvaluatorReal lHypEval = new EvaluatorReal();
					lHypEval.setUpperBound(lIssue.getUpperBound());
					lHypEval.setLowerBound(lIssue.getLowerBound());
					lHypEval.setType(EVALFUNCTYPE.LINEAR);
					lHypEval.addParam(1, lMaxValue/(lHypEval.getUpperBound()-lHypEval.getLowerBound()));
					lHypEval.addParam(0, -lMaxValue*lHypEval.getLowerBound()/(lHypEval.getUpperBound()-lHypEval.getLowerBound()));				
					EvaluatorHypothesis lEvaluatorHypothesis = new EvaluatorHypothesis (lHypEval);
					lEvaluatorHypothesis.setProbability((double)1/3);
					lEvaluatorHypothesis.setDesc("uphill");
					lEvalHyps.add(lEvaluatorHypothesis);
					//downhill
					lHypEval = new EvaluatorReal();
					lHypEval.setUpperBound(lIssue.getUpperBound());
					lHypEval.setLowerBound(lIssue.getLowerBound());				
					lHypEval.setType(EVALFUNCTYPE.LINEAR);				
					lHypEval.addParam(1, -lMaxValue/(lHypEval.getUpperBound()-lHypEval.getLowerBound()));
					lHypEval.addParam(0, lMaxValue+lMaxValue * lHypEval.getLowerBound()/(lHypEval.getUpperBound()-lHypEval.getLowerBound()));				
					lEvaluatorHypothesis = new EvaluatorHypothesis (lHypEval);
					lEvaluatorHypothesis.setProbability((double)1/3);
					lEvalHyps.add(lEvaluatorHypothesis);
					lEvaluatorHypothesis.setDesc("downhill");
					for(int k=1;k<=lTotalTriangularFns;k++) {
						//triangular
						lHypEval = new EvaluatorReal();
						lHypEval.setUpperBound(lIssue.getUpperBound());
						lHypEval.setLowerBound(lIssue.getLowerBound());
						lHypEval.setType(EVALFUNCTYPE.TRIANGULAR_VARIABLE_TOP);
						lHypEval.addParam(0, lHypEval.getLowerBound());
						lHypEval.addParam(1, lHypEval.getUpperBound());
						lHypEval.addParam(2, lHypEval.getLowerBound()+k*(lHypEval.getUpperBound()-lHypEval.getLowerBound())/(lTotalTriangularFns+1));
						lHypEval.addParam(3, lMaxValue);
						lEvaluatorHypothesis = new EvaluatorHypothesis (lHypEval);
						lEvaluatorHypothesis.setProbability((double)1/3);
						lEvalHyps.add(lEvaluatorHypothesis);
						lEvaluatorHypothesis.setDesc("triangular");
					}
					for(int k=0;k<lEvalHyps.size();k++) {
						lEvalHyps.get(k).setProbability((double)1/lEvalHyps.size());
					}
				}
				break;
			case DISCRETE:
				lEvalHyps = new ArrayList<EvaluatorHypothesis>();
				fEvaluatorHyps.add(lEvalHyps);
				//EvaluatorReal lEval = (EvaluatorReal)(fUS.getEvaluator(i));
				IssueDiscrete lDiscIssue = (IssueDiscrete)(fDomain.getIssue(i));
				lNumberOfWeights = 10;
				for(int n=1;n<=lNumberOfWeights;n++) {
					double lMaxValue = ((double)n)/lNumberOfWeights;				
					//uphill
					EvaluatorDiscrete lDiscreteEval = new EvaluatorDiscrete();
					for(int j=0;j<lDiscIssue.getNumberOfValues();j++) 
						lDiscreteEval.addEvaluation(lDiscIssue.getValue(j), j/(lDiscIssue.getNumberOfValues()-1));
					EvaluatorHypothesis lEvaluatorHypothesis = new EvaluatorHypothesis (lDiscreteEval );
					lEvaluatorHypothesis.setProbability((double)1/3);
					lEvaluatorHypothesis.setDesc("uphill");
					lEvalHyps.add(lEvaluatorHypothesis);
					//	downhill
					lDiscreteEval = new EvaluatorDiscrete();
					for(int j=0;j<lDiscIssue.getNumberOfValues();j++) 
						lDiscreteEval.addEvaluation(lDiscIssue.getValue(j), 1-j/(lDiscIssue.getNumberOfValues()-1));
					lEvaluatorHypothesis = new EvaluatorHypothesis (lDiscreteEval);
					lEvaluatorHypothesis.setProbability((double)1/3);
					lEvalHyps.add(lEvaluatorHypothesis);
					lEvaluatorHypothesis.setDesc("downhill");
					double lEval;				
					lTotalTriangularFns = lDiscIssue.getNumberOfValues()-1;
					for(int k=1;k<=lTotalTriangularFns;k++) {
						//triangular
						lDiscreteEval = new EvaluatorDiscrete();						
						for(int j=0;j<lDiscIssue.getNumberOfValues();j++)
							if(j<k) {
								lEval = (double)j/k*lMaxValue;
								lDiscreteEval.addEvaluation(lDiscIssue.getValue(j), lEval);							
							} else {
								lEval = (1-(double)(j-k)/(lDiscIssue.getNumberOfValues()-1-k))*lMaxValue;
								lDiscreteEval.addEvaluation(lDiscIssue.getValue(j), lEval);
							}
						lEvaluatorHypothesis = new EvaluatorHypothesis (lDiscreteEval);
						lEvaluatorHypothesis.setProbability((double)1/3);
						lEvalHyps.add(lEvaluatorHypothesis);
						lEvaluatorHypothesis.setDesc("triangular");
					}//for
					for(int k=0;k<lEvalHyps.size();k++) {
						lEvalHyps.get(k).setProbability((double)1/lEvalHyps.size());
					}
				}
				break;
			}//switch
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
		//TODO: check this condition
		if(pPreviousBidUtility<pUtility) return 0;
		else {
			double lSigma = 0.45;
			double x = (pPreviousBidUtility - pUtility)/pPreviousBidUtility ; 
			double lResult = 1/(lSigma*Math.sqrt(2*Math.PI)) *Math.exp(-(x*x)/(2*lSigma*lSigma));			
			return lResult;
		}
	}
	public double getExpectedEvaluationValue(Bid pBid, int pIssueNumber) {
		double lExpectedEval = 0;
		for(int j=0;j<fEvaluatorHyps.get(pIssueNumber).size();j++) {
			lExpectedEval += fEvaluatorHyps.get(pIssueNumber).get(j).getProbability() *
								(Double)(fEvaluatorHyps.get(pIssueNumber).get(j).getEvaluator().getEvaluation(fUS, pBid,pIssueNumber));
		}
		return lExpectedEval;
		
	}
	/*public double getExpectedWeight(int pIssueNumber) {
		double lExpectedWeight = 0;
		for(int i=0;i<fWeightHyps.length;i++) {
			lExpectedWeight += fWeightHyps[i].getProbability()*fWeightHyps[i].getWeight(pIssueNumber);
		}
		return lExpectedWeight;
	}*/
	private double getPartialUtility(Bid pBid, int pIssueIndex) {
		double lPartialUtility = 0;		
			//calculate partial utility w/o issue pIssueIndex		
				for(int j=0;j<fDomain.getNumberOfIssues();j++) {
					if(pIssueIndex==j) continue;
					lPartialUtility  = lPartialUtility + getExpectedEvaluationValue(pBid, j);
				}
		return lPartialUtility;
	}
	public void updateBeliefs(Bid pBid) {
		fBiddingHistory.add(pBid);
				
		//do not update the bids if it is the first bid		
		if(fBiddingHistory.size()>1) {
			//update the weights
			//1. calculate the normalization factor
			double lN = 0;
/*			for(int i=0;i<fWeightHyps.length;i++) {
				double lUtility =0;
				for(int j=0;j<fDomain.getNumberOfIssues();j++) {				
					lUtility+= fWeightHyps[i].getWeight(j)*getExpectedEvaluationValue(pBid, j);
				}
				lN += fWeightHyps[i].getProbability()*conditionalDistribution(lUtility, fPreviousBidUtility);
			}
			//2. update probabilities
			for(int i=0;i<fWeightHyps.length;i++) {
				double lUtility =0;
				for(int j=0;j<fDomain.getNumberOfIssues();j++) {				
					lUtility+= fWeightHyps[i].getWeight(j)*getExpectedEvaluationValue(pBid, j);
				}
				fWeightHyps[i].setProbability(fWeightHyps[i].getProbability()*conditionalDistribution(lUtility, fPreviousBidUtility)/lN);
			}*/
			lN = 0;
			for(int i=0;i<fDomain.getNumberOfIssues();i++) {
				//1. calculate the normalization factor				
				for(int j=0;j<fEvaluatorHyps.get(i).size();j++) {
					EvaluatorHypothesis lHyp =fEvaluatorHyps.get(i).get(j); 
					lN += lHyp.getProbability()*conditionalDistribution(getPartialUtility(pBid, i)+(Double)(lHyp.getEvaluator().getEvaluation(fUS, pBid, i)), fPreviousBidUtility);					
				}
				//2. update probabilities				
				for(int j=0;j<fEvaluatorHyps.get(i).size();j++) {
					EvaluatorHypothesis lHyp =fEvaluatorHyps.get(i).get(j); 
					lHyp.setProbability(lHyp.getProbability()*conditionalDistribution(getPartialUtility(pBid, i)+(Double)(lHyp.getEvaluator().getEvaluation(fUS, pBid, i)), fPreviousBidUtility)/lN);					
				}
			}			
			
		} else {
			//do not update the weights
			
			for(int i=0;i<fDomain.getNumberOfIssues();i++) {
				double lN = 0;
				//1. calculate the normalization factor				
				for(int j=0;j<fEvaluatorHyps.get(i).size();j++) {
					EvaluatorHypothesis lHyp =fEvaluatorHyps.get(i).get(j); 
					lN += lHyp.getProbability()*conditionalDistribution(getPartialUtility(pBid, i)+(Double)(lHyp.getEvaluator().getEvaluation(fUS, pBid, i)), fPreviousBidUtility);					
				}
				for(int j=0;j<fEvaluatorHyps.get(i).size();j++) {
					EvaluatorHypothesis lHyp =fEvaluatorHyps.get(i).get(j); 
					lHyp.setProbability(lHyp.getProbability()*conditionalDistribution(getPartialUtility(pBid, i)+(Double)(lHyp.getEvaluator().getEvaluation(fUS, pBid, i)), fPreviousBidUtility)/lN);					
				}

			}			
		} //if
		
//		System.out.println(getMaxHyp().toString());
		//calculate utility of the next partner's bid according to the concession functions
		fPreviousBidUtility = fPreviousBidUtility-0.05;
	}
	
/*	public double getExpectedUtility(Bid pBid) {
		double lExpectedUtility = 0;
		for(int i=0;i<fUSHyps.size();i++) {
			UtilitySpaceHypothesis lUSHyp = fUSHyps.get(i);
			double p = lUSHyp.getProbability();
			double u = lUSHyp.getUtility(pBid);
			lExpectedUtility += p*u;
		}
		return lExpectedUtility;
		
	}
	
	*/
	public double getExpectedUtility(Bid pBid) {
		//calculate expected utility
		double u = 0;
		for(int k=0;k<fDomain.getNumberOfIssues();k++) {				
			u = u +getExpectedEvaluationValue(pBid, k);
		
		}
		return u;
	}

}
