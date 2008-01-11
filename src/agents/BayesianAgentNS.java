package agents;

import agents.bayesianopponentmodel.BayesianOpponentModel;

public class BayesianAgentNS extends BayesianAgent {

	@Override
	protected void prepareOpponentModel() {
		fOpponentModel = new BayesianOpponentModel(utilitySpace);
	}

}
