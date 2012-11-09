package negotiator.boaframework.offeringstrategy.anac2011;

import java.util.HashMap;
import negotiator.Bid;
import negotiator.bidding.BidDetails;
import negotiator.boaframework.NegotiationSession;
import negotiator.boaframework.OMStrategy;
import negotiator.boaframework.OfferingStrategy;
import negotiator.boaframework.OpponentModel;
import negotiator.boaframework.sharedagentstate.anac2011.GahboninhoSAS;

/**
 * This is the decoupled Offering Strategy for Gahboninho (ANAC2011)
 * The code was taken from the ANAC2011 Gahboninho and adapted to work within the BOA framework
 * 
 * Decoupling Negotiating Agents to Explore the Space of Negotiation Strategies
 * T. Baarslag, K. Hindriks, M. Hendrikx, A. Dirkzwager, C.M. Jonker
 * 
 * @author Mark Hendrikx
 */
public class Gahboninho_Offering extends OfferingStrategy {
	final int PlayerCount = 8;
	private boolean WereBidsFiltered = false;
	private int RoundCount = 0;
	
	private int TotalFirstActions = 40;

	public void init(NegotiationSession domainKnow, OpponentModel model, OMStrategy omStrategy, HashMap<String, Double> parameters)
			throws Exception {
		super.init(domainKnow, model, omStrategy, parameters);
		helper = new GahboninhoSAS(negotiationSession, model, omStrategy);
	}

	@Override
	public BidDetails determineOpeningBid() {
		return determineNextBid();
	}
	
	@Override
	public BidDetails determineNextBid() {
		BidDetails previousOpponentBid = null;
		BidDetails opponentBid = negotiationSession.getOpponentBidHistory().getLastBidDetails();
		int histSize = negotiationSession.getOpponentBidHistory().getHistory().size();
		if (histSize >= 2) {
			previousOpponentBid = negotiationSession.getOpponentBidHistory().getHistory().get(histSize - 1);
		}

		if (opponentBid != null) {
			
			if (previousOpponentBid != null) {
				try {
					((GahboninhoSAS) helper).getIssueManager().ProcessOpponentBid(opponentBid.getBid());
					((GahboninhoSAS) helper).getOpponentModel().UpdateImportance(opponentBid.getBid());
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else {
				try {
					((GahboninhoSAS) helper).getIssueManager().learnBids(opponentBid.getBid());
				}
				catch (Exception e) {
					e.printStackTrace();
				}
			}
			double threshold = ((GahboninhoSAS) helper).getIssueManager().GetMinimumUtilityToAccept();
			((GahboninhoSAS) helper).getIssueManager().setMinimumUtilForAcceptance(threshold);
		}
		
		try {
			// on the first few rounds don't get tempted so fast
			

			++RoundCount;
			if (WereBidsFiltered == false
					&& (negotiationSession.getTime() > ((GahboninhoSAS) helper).getIssueManager().GetDiscountFactor() * 0.9 || 
							negotiationSession.getTime() + 3 * ((GahboninhoSAS) helper).getIssueManager().getBidsCreationTime() > 1)) {
				WereBidsFiltered = true;

				int DesiredBidcount = (int) (RoundCount * (1 - negotiationSession.getTime()));

				if (((GahboninhoSAS) helper).getIssueManager().getBids().size() > 200) {
					((GahboninhoSAS) helper).getIssueManager().setBids(((GahboninhoSAS) helper).getOpponentModel().FilterBids(((GahboninhoSAS) helper).getIssueManager().getBids(), DesiredBidcount));
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		// on the first time we act offer max bid
		if (previousOpponentBid == null) {
			try {
				((GahboninhoSAS) helper).getIssueManager().AddMyBidToStatistics(((GahboninhoSAS) helper).getIssueManager().getMaxBid());
			} catch (Exception e) {
				e.printStackTrace();
			}
			Bid maxBid = ((GahboninhoSAS) helper).getIssueManager().getMaxBid();
			
			try {
				return new BidDetails(maxBid, negotiationSession.getUtilitySpace().getUtility(maxBid), negotiationSession.getTime());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		Bid myBid;
		if (((GahboninhoSAS) helper).getFirstActions() >= 0 && negotiationSession.getTime() < 0.15) {
			// on first few bids let the opponent learn some more about our preferences

			double utilDecrease = (1 - 0.925) / TotalFirstActions;

			myBid = ((GahboninhoSAS) helper).getIssueManager().GenerateBidWithAtleastUtilityOf(0.925 + utilDecrease
					* ((GahboninhoSAS) helper).getFirstActions());
			((GahboninhoSAS) helper).decrementFirstActions();
		} else {
			myBid = ((GahboninhoSAS) helper).getIssueManager().GenerateBidWithAtleastUtilityOf(((GahboninhoSAS) helper).getIssueManager().GetNextRecommendedOfferUtility());

			if (((GahboninhoSAS) helper).getIssueManager().getInFrenzy() == true)
				myBid = ((GahboninhoSAS) helper).getIssueManager().getBestEverOpponentBid();
		}

		try {
			((GahboninhoSAS) helper).getIssueManager().AddMyBidToStatistics(myBid);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		try {
			return new BidDetails(myBid, negotiationSession.getUtilitySpace().getUtility(myBid), negotiationSession.getTime());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}