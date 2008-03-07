package agents;
import java.util.Date;

import negotiator.*;
import negotiator.actions.Action;
import negotiator.utility.UtilitySpace;

public class QOAgent extends Agent {

	@Override
	public Action chooseAction() {

	}

	@Override
	public void init(int sessionNumber, int sessionTotalNumber,
			Date startTimeP, Integer totalTimeP, UtilitySpace us) {

		super.init(sessionNumber, sessionTotalNumber, startTimeP, totalTimeP, us);
	}

	@Override
	public void ReceiveMessage(Action opponentAction) {
	}

}
