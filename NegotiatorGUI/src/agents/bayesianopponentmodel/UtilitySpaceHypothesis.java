package agents.bayesianopponentmodel;

import java.util.ArrayList;

import negotiator.Bid;
import negotiator.Domain;
import negotiator.issue.Issue;
import negotiator.utility.UtilitySpace;

public class UtilitySpaceHypothesis extends Hypothesis {
	private WeightHypothesis fWeightHyp;
	private EvaluatorHypothesis[] fEvalHyp;
	private Domain fDomain;
	private UtilitySpace fUS;
	ArrayList<Issue> issues;
	
	public UtilitySpaceHypothesis(Domain pDomain,
								  UtilitySpace pUS,
								  WeightHypothesis pWeightHyp,
								  EvaluatorHypothesis[] pEvalHyp) {
		fUS = pUS;
		fDomain = pDomain;
		issues =  fDomain.getIssues();
		fWeightHyp = pWeightHyp;
		fEvalHyp = pEvalHyp;		
	}

	public Domain getDomain() {
		return fDomain;
	}

	public UtilitySpace getUtilitySpace() {
		return fUS;
	}
	
	public EvaluatorHypothesis[] getEvalHyp() {
		return fEvalHyp;
	}

	public WeightHypothesis getHeightHyp() {
		return fWeightHyp;
	}
	public double getUtility(Bid pBid) {
		double u=0;
		 
		for(int k=0;k<fEvalHyp.length;k++) {
			try
			{
				u = u + fWeightHyp.getWeight(k)*fEvalHyp[k].getEvaluator().getEvaluation(fUS, pBid,issues.get(k).getNumber());
			} catch (Exception e) {System.out.println("Exception in UtilSpaceHypo.getUtil:"+e.getMessage()+". using 0"); 
				e.printStackTrace();
			}
		}
		return u;
		
	}
	public String toString() {
		String lResult = "";
		lResult += fWeightHyp.toString();
		for(EvaluatorHypothesis lHyp : fEvalHyp) {
			lResult += lHyp.toString()+";";
		}
		lResult += String.format("%1.5f", getProbability());
		return lResult;
	}
}
