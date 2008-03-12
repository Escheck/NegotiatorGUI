package agents;
import java.util.Date;

import negotiator.*;
import negotiator.issue.*;
import negotiator.actions.Accept;
import negotiator.actions.Action;
import negotiator.actions.EndNegotiation;
import negotiator.actions.Offer;
import negotiator.utility.UtilitySpace;
import agents.qoagent2.*;
public class QOAgent extends Agent {
	private enum ACTIONTYPE { START, OFFER, ACCEPT, BREAKOFF };
	private agents.qoagent2.QOAgent m_QOAgent;
	
	@Override
	public Action chooseAction() {
		
		Action action = null;
		m_QOAgent.
		return action;
	}

	@Override
	public void init(int sessionNumber, int sessionTotalNumber,
			Date startTimeP, Integer totalTimeP, UtilitySpace us) {

		super.init(sessionNumber, sessionTotalNumber, startTimeP, totalTimeP, us);
		m_QOAgent = new agents.qoagent2.QOAgent();
	}

	@Override
	public void ReceiveMessage(Action opponentAction) {
		String sMessage= "";
		Action lAction = null;
		ACTIONTYPE lActionType;
		Bid lOppntBid = null;
		lActionType = getActionType(opponentAction);
		switch (lActionType) {
		case OFFER: // Offer received from opponent
			try {
				lOppntBid = ((Offer) opponentAction).getBid();
				sMessage = "type counter offer source 1 target 2 tag 2 issueSet ";
				for(Issue lIssue: utilitySpace.getDomain().getIssues()) {
					sMessage = sMessage + lOppntBid.getValue(lIssue.getNumber())+"*"+lIssue.getName();
				}
				sMessage = sMessage + "*";
			} catch (Exception e) {
				e.printStackTrace();
			}
			break;
		case ACCEPT: // Presumably, opponent accepted last bid, but let's check...
		case BREAKOFF:
			// nothing left to do. Negotiation ended, which should be checked by
			// Negotiator...
			break;
		default:
			break;
		}
		m_QOAgent.receivedMessage(sMessage);
	}
	private ACTIONTYPE getActionType(Action lAction) {
		ACTIONTYPE lActionType = ACTIONTYPE.START;
		if (lAction instanceof Offer)
			lActionType = ACTIONTYPE.OFFER;
		else if (lAction instanceof Accept)
			lActionType = ACTIONTYPE.ACCEPT;
		else if (lAction instanceof EndNegotiation)
			lActionType = ACTIONTYPE.BREAKOFF;
		return lActionType;
	}

}
