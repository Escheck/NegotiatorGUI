package negotiator.boaframework;

import java.io.Serializable;
import java.util.ArrayList;

import negotiator.Bid;
import negotiator.BidHistory;
import negotiator.Domain;
import negotiator.bidding.BidDetails;
import negotiator.issue.Issue;
import negotiator.session.Timeline;
import negotiator.utility.AdditiveUtilitySpace;

/**
 * This is a class which manages all the negotiation session pertinent
 * information to a single agent.
 * 
 * @author Alex Dirkzwager, Mark Hendrikx
 */
public class NegotiationSession {

	/** Optional outcomespace which should be set manually. */
	protected OutcomeSpace outcomeSpace;
	/** History of bids made by the opponent. */
	protected BidHistory opponentBidHistory;
	/** History of bids made by the agent. */
	protected BidHistory ownBidHistory;
	/** Reference to the negotiation domain. */
	protected Domain domain;
	/** Reference to the agent's preference profile for the domain. */
	protected AdditiveUtilitySpace utilitySpace;
	/** Reference to the timeline. */
	protected Timeline timeline;

	private SessionData sessionData;

	/**
	 * Special constructor used by the NegotiationSessionWrapper. Do not use
	 * this constructor for other purposes.
	 */
	protected NegotiationSession() {
	}

	/**
	 * Create a negotiation session which is used to keep track of the
	 * negotiation state.
	 * 
	 * @param utilitySpace
	 *            of the agent.
	 * @param timeline
	 *            of the current negotiation.
	 */
	public NegotiationSession(SessionData sessionData,
			AdditiveUtilitySpace utilitySpace, Timeline timeline) {
		this(sessionData, utilitySpace, timeline, null);
	}

	/**
	 * Create a negotiation session which is used to keep track of the
	 * negotiation state.
	 * 
	 * @param utilitySpace
	 *            of the agent.
	 * @param timeline
	 *            of the current negotiation.
	 * @param outcomeSpace
	 *            representation of the possible outcomes.
	 */
	public NegotiationSession(SessionData sessionData,
			AdditiveUtilitySpace utilitySpace, Timeline timeline,
			OutcomeSpace outcomeSpace) {
		this.sessionData = sessionData;
		this.utilitySpace = utilitySpace;
		this.timeline = timeline;
		this.domain = utilitySpace.getDomain();
		this.opponentBidHistory = new BidHistory();
		this.ownBidHistory = new BidHistory();
		this.outcomeSpace = outcomeSpace;
		sessionData = new SessionData();
	}

	/**
	 * Returns the bidding history of the opponent.
	 * 
	 * @return bidding history of the opponent.
	 */
	public BidHistory getOpponentBidHistory() {
		return opponentBidHistory;
	}

	/**
	 * Returns the bidding history of the agent.
	 * 
	 * @return bidding history of the agent.
	 */
	public BidHistory getOwnBidHistory() {
		return ownBidHistory;
	}

	/**
	 * Returns the discount factor of the utilityspace. Each utilityspace has a
	 * unique discount factor.
	 * 
	 * @return discount factor of the utilityspace.
	 */
	public double getDiscountFactor() {
		return utilitySpace.getDiscountFactor();
	}

	/**
	 * @return issues of the domain.
	 */
	public ArrayList<Issue> getIssues() {
		return domain.getIssues();
	}

	/**
	 * @return timeline of the negotiation.
	 */
	public Timeline getTimeline() {
		return timeline;
	}

	/**
	 * Returns the normalized time (t = [0,1])
	 * 
	 * @return normalized time.
	 */
	public double getTime() {
		return timeline.getTime();
	}

	/**
	 * Returns the negotiation domain.
	 * 
	 * @return domain of the negotiation.
	 */
	public Domain getDomain() {
		if (utilitySpace != null) {
			return utilitySpace.getDomain();
		}
		return null;
	}

	/**
	 * Returns the utilityspace of the agent.
	 * 
	 * @return utilityspace of the agent.
	 */
	public AdditiveUtilitySpace getUtilitySpace() {
		return utilitySpace;
	}

	/**
	 * Returns the space of possible outcomes in the domain. The returned value
	 * may be null.
	 * 
	 * @return outcomespace if available.
	 */
	public OutcomeSpace getOutcomeSpace() {
		return outcomeSpace;
	}

	/**
	 * Method used to set the outcomespace. Setting an outcomespace makes method
	 * such as getMaxBidinDomain much more efficient.
	 * 
	 * @param outcomeSpace
	 *            to be set.
	 */
	public void setOutcomeSpace(OutcomeSpace outcomeSpace) {
		this.outcomeSpace = outcomeSpace;
	}

	/**
	 * Returns the best bid in the domain. If the outcomespace is set, it is
	 * used in this step. Else a highly inefficient method is used.
	 * 
	 * @return bid with lowest highest possible utility.
	 */
	public BidDetails getMaxBidinDomain() {
		BidDetails maxBid = null;
		if (outcomeSpace == null) {
			try {
				Bid maximumBid = utilitySpace.getMaxUtilityBid();
				maxBid = new BidDetails(maximumBid,
						utilitySpace.getUtility(maximumBid), -1);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			maxBid = outcomeSpace.getMaxBidPossible();
		}
		return maxBid;
	}

	/**
	 * Returns the worst bid in the domain. If the outcomespace is set, it is
	 * used in this step. Else a highly inefficient method is used.
	 * 
	 * @return bid with lowest possible utility.
	 */
	public BidDetails getMinBidinDomain() {
		BidDetails minBid = null;
		if (outcomeSpace == null) {
			try {
				Bid minimumBidBid = utilitySpace.getMinUtilityBid();
				minBid = new BidDetails(minimumBidBid,
						utilitySpace.getUtility(minimumBidBid), -1);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			minBid = outcomeSpace.getMinBidPossible();
		}
		return minBid;
	}

	/**
	 * Method used o store the data of a component. For agent programming please
	 * use the storeData() method of the BOA component.
	 * 
	 * @param component
	 *            from which the data is stored.
	 * @param data
	 *            to be stored.
	 */
	public void setData(ComponentsEnum component, Serializable data) {
		sessionData.setData(component, data);
	}

	/**
	 * Method used to load the data saved by a component. For agent programming
	 * please use the loadData() method of the BOA component.
	 * 
	 * @param component
	 *            from which the data is requested.
	 * @return data saved by the component.
	 */
	public Serializable getData(ComponentsEnum component) {
		return sessionData.getData(component);
	}

	/**
	 * Returns the discounted utility of a bid given the bid and the time at
	 * which it was offered.
	 * 
	 * @param bid
	 *            which discount utility is requested.
	 * @param time
	 *            at which the bid was offered.
	 * @return discounted utility of the given bid at the given time.
	 */
	public double getDiscountedUtility(Bid bid, double time) {
		return utilitySpace.getUtilityWithDiscount(bid, time);
	}

	public SessionData getSessionData() {
		return sessionData;
	}
}