package negotiator.group6;

import negotiator.Bid;
import negotiator.issue.Value;

public interface IOpponentModel {

	public Value getValue(Integer issueId);
	public void learnWeights(Bid bid);
	
}
