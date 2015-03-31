package negotiator.parties;

import java.util.List;

import javax.swing.JOptionPane;

import negotiator.AgentID;
import negotiator.Bid;
import negotiator.Deadline;
import negotiator.SupportedNegotiationSetting;
import negotiator.Timeline;
import negotiator.Vote;
import negotiator.actions.Action;
import negotiator.actions.InformVotingResult;
import negotiator.actions.OfferForVoting;
import negotiator.utility.UtilitySpace;
import agents.EnterBidDialogAcceptance;

/**
 * modified version of W.Pasman's modified version of Dmytro's UIAgent
 *
 * @author David Festen
 */
public class MediatorHumanNegotiationParty extends AbstractNegotiationParty {
	private Action opponentAction = null;
	private EnterBidDialogAcceptance ui = null;
	private Bid mostRecentAgreement = null;
	private Bid mostRecentOffer = null;

	/**
	 * One agent will be kept alive over multiple sessions. Init will be called
	 * at the start of each nego session.
	 */
	public MediatorHumanNegotiationParty(UtilitySpace utilitySpace,
			Deadline deadlines, Timeline timeline, long randomSeed) {
		super(utilitySpace, deadlines, timeline, randomSeed);
		System.out.println("init UIAgent");

		System.out.println("closing old dialog of ");
		if (ui != null) {
			ui.dispose();
			ui = null;
		}
		System.out.println("old  dialog closed. Trying to open new dialog. ");
		try {
			ui = new EnterBidDialogAcceptance(this, null, true, utilitySpace);
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
			mostRecentOffer = ((OfferForVoting) opponentAction).getBid();
		}

		if (opponentAction instanceof InformVotingResult
				&& ((InformVotingResult) opponentAction).getVotingResult()
						.equals(Vote.ACCEPT)) {
			mostRecentAgreement = mostRecentOffer;
			System.out.println("mostRecentAgreement = " + mostRecentAgreement);
			JOptionPane
					.showMessageDialog(
							null,
							"The offer is accepted. You can continue to "
									+ "accept/reject new offers to find a better agreement.");
		}
	}

	@Override
	public Action chooseAction(List<Class> possibleActions) {
		return ui.askUserForAction(opponentAction, mostRecentOffer,
				mostRecentAgreement);
	}

	public SupportedNegotiationSetting getSupportedNegotiationSetting() {
		return SupportedNegotiationSetting.getLinearUtilitySpaceInstance();
	}
}
