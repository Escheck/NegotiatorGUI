package negotiator.boaframework.acceptanceconditions.anac2011;

import java.util.HashMap;
import negotiator.bidding.BidDetails;
import negotiator.boaframework.AcceptanceStrategy;
import negotiator.boaframework.Actions;
import negotiator.boaframework.NegotiationSession;
import negotiator.boaframework.OfferingStrategy;
import negotiator.boaframework.sharedagentstate.anac2011.GahboninhoSAS;

/**
 * @author Mark Hendrikx and Alex Dirkzwager
 * 
 * In the original version there is a bug in the opponent model.
 * In practice, it is only updated once.
 *
 */
public class AC_Gahboninho extends AcceptanceStrategy {
	
	private boolean activeHelper = false;
	private boolean done = false;
	
	public AC_Gahboninho(NegotiationSession negotiationSession,
			OfferingStrategy offeringStrategy) {
		initializeAgent(negotiationSession,offeringStrategy);
	}
	
	@Override
	public void init(NegotiationSession negoSession, OfferingStrategy strat, HashMap<String, Double> parameters) throws Exception {
		initializeAgent(negotiationSession,offeringStrategy);
		
	}
	

	public void initializeAgent(NegotiationSession negoSession, OfferingStrategy strat) {
		this.negotiationSession = negoSession;
		this.offeringStrategy = strat;
		if (offeringStrategy.getHelper() != null && offeringStrategy.getHelper().getName().equals("Gahboninho")) {
			helper = offeringStrategy.getHelper();
			
		} else {
			helper = new GahboninhoSAS(negotiationSession, null, null);
			activeHelper = true;
		}
	}
	
	@Override
	public Actions determineAcceptability() {
		BidDetails opponentBid = negotiationSession.getOpponentBidHistory().getLastBidDetails();
		
		if(activeHelper){
			if(negotiationSession.getOpponentBidHistory().getHistory().size() < 2){
				try {
					
					((GahboninhoSAS) helper).getIssueManager().ProcessOpponentBid(opponentBid.getBid());
					((GahboninhoSAS) helper).getOpponentModel().UpdateImportance(opponentBid.getBid());
				} catch (Exception e) {
					e.printStackTrace();
				}
				
			} else {
				try {
					if (!done) {
						((GahboninhoSAS) helper).getIssueManager().learnBids(opponentBid.getBid());
						done = true;
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		
		if (((GahboninhoSAS) helper).getFirstActions() > 0 && opponentBid != null
				&& opponentBid.getMyUndiscountedUtil() > 0.95) {
			return Actions.Accept;
		}
		
		if (opponentBid != null && opponentBid.getMyUndiscountedUtil() >= ((GahboninhoSAS) helper).getIssueManager().getMinimumUtilForAcceptance()) {
			return Actions.Accept;
		}
		return Actions.Reject;
	}
}
