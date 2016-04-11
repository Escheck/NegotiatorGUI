package agents.anac.y2016.Farma;

import java.util.List;

import negotiator.AgentID;
import negotiator.Bid;
import negotiator.Deadline;
import negotiator.actions.Accept;
import negotiator.actions.Action;
import negotiator.actions.EndNegotiation;
import negotiator.actions.Inform;
import negotiator.actions.Offer;
import negotiator.parties.AbstractNegotiationParty;
import negotiator.session.TimeLineInfo;
import negotiator.utility.AbstractUtilitySpace;
import agents.anac.y2016.Farma.etc.bidSearch;
import agents.anac.y2016.Farma.etc.negotiationInfo;
import agents.anac.y2016.Farma.etc.negotiationStrategy;

/**
 * This is your negotiation party.
 */

public class Farma extends AbstractNegotiationParty {
	private TimeLineInfo timeLineInfo; // タイムライン
	private AbstractUtilitySpace utilitySpace;
	private negotiationInfo negotiationInfo; // 交渉情報
	private bidSearch bidSearch; // 合意案候補の探索
	private negotiationStrategy negotiationStrategy; // 交渉戦略
	private Bid offeredBid = null; // 最近提案された合意案候補

	private boolean isPrinting = false; // デバッグ用

	@Override
	public void init(AbstractUtilitySpace utilitySpace, Deadline deadLine,
			TimeLineInfo timeLineInfo, long randomSeed, AgentID agentId) {
		if (isPrinting)
			System.out.println("*** Farma2016 v1.0 ***");

		this.timeLineInfo = timeLineInfo;
		this.utilitySpace = utilitySpace;
		negotiationInfo = new negotiationInfo(utilitySpace, isPrinting);
		negotiationStrategy = new negotiationStrategy(utilitySpace,
				negotiationInfo, isPrinting);

		try {
			bidSearch = new bidSearch(utilitySpace, negotiationInfo, isPrinting);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Each round this method gets called and ask you to accept or offer. The
	 * first party in the first round is a bit different, it can only propose an
	 * offer.
	 *
	 * @param validActions
	 *            Either a list containing both accept and offer or only offer.
	 * @return The chosen action.
	 */
	@Override
	public Action chooseAction(List<Class<? extends Action>> validActions) {
		double time = timeLineInfo.getTime(); // 現在の時刻
		negotiationInfo.updateRound();

		// Acceptの判定
		if (validActions.contains(Accept.class)
				&& negotiationStrategy.selectAccept(offeredBid, time)) {
			return new Accept();
		}

		// EndNegotiationの判定
		if (negotiationStrategy.selectEndNegotiation(time)) {
			return new EndNegotiation();
		}

		// 他のプレイヤーに新たなBidをOffer
		Bid offerBid = bidSearch.getBid(
				utilitySpace.getDomain().getRandomBid(),
				negotiationStrategy.getThreshold(timeLineInfo.getTime()));

		negotiationInfo.updateMyOfferedBids(offerBid);
		negotiationInfo.updateMyBidHistory(offerBid);
		// 自分の統計情報の更新
		try {
			negotiationInfo.updateMyNegotiatingInfo(offerBid);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new Offer(offerBid);
	}

	/**
	 * All offers proposed by the other parties will be received as a message.
	 * You can use this information to your advantage, for example to predict
	 * their utility.
	 *
	 * @param sender
	 *            The party that did the action. Can be null.
	 * @param action
	 *            The action that party did.
	 */
	@Override
	public void receiveMessage(AgentID sender, Action action) {
		// プレイヤーのアクションを受信
		super.receiveMessage(sender, action);

		double time = timeLineInfo.getTime(); // 現在の時刻

		if (isPrinting) {
			System.out.println("Sender:" + sender + ", Action:" + action);
		}

		if (action != null) {
			if (action instanceof Inform
					&& ((Inform) action).getName() == "NumberOfAgents"
					&& ((Inform) action).getValue() instanceof Integer) {
				Integer opponentsNum = (Integer) ((Inform) action).getValue();
				negotiationInfo.updateOpponentsNum(opponentsNum);

				if (isPrinting) {
					System.out.println("NumberofNegotiator:"
							+ negotiationInfo.getNegotiatorNum());
				}
			}

			if (action instanceof Accept) {
				// 初出の交渉者は初期化
				if (!negotiationInfo.getOpponents().contains(sender)) {
					negotiationInfo.initOpponent(sender);
				}
				// Bid別のAccept数の更新
				negotiationInfo.updateBidAcceptNum(sender, offeredBid, time);
				// SenderごとのAccept数（Offerを除く）の更新
				negotiationInfo.updateOpponentsAcceptNum(sender, time);
			}

			if (action instanceof Offer) {
				// 初出の交渉者は初期化
				if (!negotiationInfo.getOpponents().contains(sender)) {
					negotiationInfo.initOpponent(sender);
				}
				offeredBid = ((Offer) action).getBid(); // 提案された合意案候補

				// 交渉情報を更新
				try {
					negotiationInfo.updateOfferedValueNum(sender, offeredBid,
							time);
					negotiationInfo
							.updateBidAcceptNum(sender, offeredBid, time);
					negotiationInfo.updateInfo(sender, offeredBid);

					bidSearch.shiftBidSearch(offeredBid);
				} catch (Exception e) {
					System.out.println("交渉情報の更新に失敗しました");
					e.printStackTrace();
				}
			}
			if (action instanceof EndNegotiation) {
			}
		}
	}

	@Override
	public String getDescription() {
		return "Farma";
	}

}
