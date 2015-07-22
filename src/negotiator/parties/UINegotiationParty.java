package negotiator.parties;

import java.util.List;

import javax.swing.JOptionPane;

import negotiator.AgentID;
import negotiator.Bid;
import negotiator.Deadline;
import negotiator.SupportedNegotiationSetting;
import negotiator.actions.Accept;
import negotiator.actions.Action;
import negotiator.actions.EndNegotiation;
import negotiator.actions.Offer;
import negotiator.session.TimeLineInfo;
import negotiator.utility.UtilitySpace;
import agents.EnterBidDialog2;

/**
 * @author W.Pasman, modified version of Dmytro's UIAgent
 */
public class UINegotiationParty extends AbstractNegotiationParty {

	private Action opponentAction = null;

	private EnterBidDialog2 ui = null;

	private Bid myPreviousBid = null;
	private Bid mostRecentBid;

	/**
	 * One agent will be kept alive over multiple sessions. Init will be called
	 * at the start of each nego session.
	 */
	@Override
	public void init(UtilitySpace utilitySpace, Deadline deadlines,
			TimeLineInfo timeline, long randomSeed, AgentID id) {
		super.init(utilitySpace, deadlines, timeline, randomSeed, id);
		System.out.println("init UIAgent");

		System.out.println("closing old dialog of ");
		if (ui != null) {
			ui.dispose();
			ui = null;
		}
		System.out.println("old  dialog closed. Trying to open new dialog. ");
		try {
			ui = new EnterBidDialog2(this, null, true, utilitySpace, true);
		} catch (Exception e) {
			System.out.println("Problem in UIAgent2.init:" + e.getMessage());
			e.printStackTrace();
		}
		System.out.println("finished init of UIAgent2");

	}

	@Override
	public void receiveMessage(Object sender, Action arguments) {
		this.opponentAction = arguments;
		if (opponentAction instanceof Offer) {
			mostRecentBid = ((Offer) opponentAction).getBid();
		}

		if (opponentAction instanceof Accept) {
			JOptionPane.showMessageDialog(null,
					"Opponent accepted your last offer.");
		}

		if (opponentAction instanceof EndNegotiation) {
			JOptionPane.showMessageDialog(null,
					"Opponent canceled the negotiation session");
		}
	}

	@Override
	public Action chooseAction(List<Class> possibleActions) {
		Action action = ui.askUserForAction(opponentAction, myPreviousBid,
				mostRecentBid);
		if ((action != null) && (action instanceof Offer)) {
			myPreviousBid = ((Offer) action).getBid();
		}
		return action;
	}

	public SupportedNegotiationSetting getSupportedNegotiationSetting() {
		return SupportedNegotiationSetting.getLinearUtilitySpaceInstance();
	}
}
