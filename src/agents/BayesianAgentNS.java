package agents;

import agents.bayesianopponentmodel.BayesianOpponentModelScalable;;

public class BayesianAgentNS extends BayesianAgent {

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "Bayesian Scalable";
	}

	@Override
	protected void prepareOpponentModel() {
		fOpponentModel = new BayesianOpponentModelScalable(utilitySpace);
	}

}
