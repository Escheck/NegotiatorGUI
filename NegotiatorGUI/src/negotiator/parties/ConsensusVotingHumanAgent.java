package negotiator.parties;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import javax.swing.JOptionPane;

import negotiator.AgentID;
import negotiator.Bid;
import negotiator.Deadline;
import negotiator.SupportedNegotiationSetting;
import negotiator.actions.Accept;
import negotiator.actions.Action;
import negotiator.actions.EndNegotiation;
import negotiator.actions.Offer;
import negotiator.actions.OfferForVoting;
import negotiator.session.Timeline;
import negotiator.utility.UtilitySpace;
import agents.EnterBidDialogAcceptReject;
import agents.EnterBidDialogInterface;
import agents.EnterBidDialogOfferForVoting;

/**
 * @author W.Pasman, modified version of Dmytro's UIAgent
 */
public class ConsensusVotingHumanAgent extends AbstractNegotiationParty {
	private Action opponentAction = null;
	private EnterBidDialogInterface ui = null;
	private Bid myPreviousBid = null;
	private Queue<Bid> offers = new LinkedList<Bid>();

	/**
	 * One agent will be kept alive over multiple sessions. Init will be called
	 * at the start of each negotiation session.
	 */
	public void init(UtilitySpace utilitySpace, Deadline deadlines,
			Timeline timeline, long randomSeed) {
		super.init(utilitySpace, deadlines, timeline, randomSeed);
		System.out.println("init UIAgent");

		System.out.println("closing old dialog of ");
		if (ui != null) {
			ui.dispose();
			ui = null;
		}
		System.out.println("old  dialog closed. Trying to open new dialog. ");
		try {
			ui = new EnterBidDialogOfferForVoting(this, null, true,
					utilitySpace);
		} catch (Exception e) {
			System.out.println("Problem in UIAgent2.init:" + e.getMessage());
			e.printStackTrace();
		}
		System.out.println("finished init of UIAgent2");

		partyId = new AgentID("Party ID");
	}

	@Override
	public void receiveMessage(Object sender, Action arguments) {
		this.opponentAction = arguments;

		if (opponentAction instanceof OfferForVoting) {
			offers.offer(((OfferForVoting) opponentAction).getBid());
		}

		// if (opponentAction instanceof Accept && sender != this) {
		// JOptionPane.showMessageDialog(null,
		// "" + sender + " accepted your last offer.");
		// }

		if (opponentAction instanceof EndNegotiation) {
			JOptionPane.showMessageDialog(null, "" + sender
					+ " canceled the negotiation session");
		}
	}

	@Override
	public Action chooseAction(List<Class> possibleActions) {
		if (ui != null) {
			ui.dispose();
			ui = null;
		}
		try {
			if (possibleActions.contains(Accept.class)) {
				Bid topic = offers.poll();
				ui = new EnterBidDialogAcceptReject(this, null, true,
						utilitySpace, topic);
			} else {
				ui = new EnterBidDialogOfferForVoting(this, null, true,
						utilitySpace);
			}

		} catch (Exception e) {
			System.out.println("Problem in UIAgent2.init:" + e.getMessage());
			e.printStackTrace();
		}
		System.out.println("ui.getClass().toString() = "
				+ ui.getClass().toString());
		Action action = ui.askUserForAction(opponentAction, myPreviousBid);
		// System.out.println("action = " + action);
		if ((action != null) && (action instanceof Offer)) {
			myPreviousBid = ((Offer) action).getBid();
		}
		return action;
	}

	public SupportedNegotiationSetting getSupportedNegotiationSetting() {
		return SupportedNegotiationSetting.getLinearUtilitySpaceInstance();
	}
}
