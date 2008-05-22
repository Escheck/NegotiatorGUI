package agents;

import java.util.Date;

import negotiator.Agent;
import negotiator.actions.Action;
import negotiator.utility.UtilitySpace;

public class TimeDependentAgent extends Agent {

	@Override
	public Action chooseAction() {
		// TODO Auto-generated method stub
		return super.chooseAction();
	}

	@Override
	public void init(int sessionNumber, int sessionTotalNumber,
			Date startTimeP, Integer totalTimeP, UtilitySpace us) {
		// TODO Auto-generated method stub
		super.init(sessionNumber, sessionTotalNumber, startTimeP, totalTimeP, us);
	}

	@Override
	public void ReceiveMessage(Action opponentAction) {
		// TODO Auto-generated method stub
		super.ReceiveMessage(opponentAction);
	}

}
