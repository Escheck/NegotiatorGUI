package negotiator.boaframework.omstrategy;

import java.util.ArrayDeque;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import negotiator.bidding.BidDetails;
import negotiator.boaframework.NegotiationSession;
import negotiator.boaframework.OMStrategy;
import negotiator.boaframework.OpponentModel;

/**
 * Opponent Model Strategy
 */
public final class TheFawkes_OMS extends OMStrategy
{
    private ArrayDeque<Double> lastTen;
    private int secondBestCounter = 1;
    
	/**
	 * Empty constructor used for reflexion. Note this constructor assumes that init
	 * is called next.
	 */
	public TheFawkes_OMS() { }

	/**
	 * Normal constructor used to initialize the OfferBestN opponent model strategy.
	 * @param negotiationSession symbolizing the negotiation state.
	 * @param model opponent model strategy used by this opponent model strategy.
	 * @param n amount of best bids from which a random bid is selected.
	 */
	public TheFawkes_OMS(NegotiationSession negotiationSession, OpponentModel model) {
		initializeAgent(negotiationSession, model);
	}

    @Override
    public void init( NegotiationSession nSession, OpponentModel oppModel, HashMap<String, Double> parameters ) throws Exception
    {
    	initializeAgent( nSession, oppModel );
    }
    
    private void initializeAgent(NegotiationSession negotiationSession, OpponentModel model) {
    	try {
			super.init( negotiationSession, model );
	        this.lastTen = new ArrayDeque<Double>( 11 );

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

    @Override
    public BidDetails getBid( List<BidDetails> list )
    { // gets as input a List with bid that have an utily u', given formula 14
        Collections.sort( list, new Comparing( this.model ) );
        BidDetails opponentBestBid = list.get( 0 );
        boolean allEqual = true;

        for( double bid : this.lastTen )
        {
            if( bid != opponentBestBid.getMyUndiscountedUtil() )
            { // Use our own undiscounted util to check if we're effectively offering the same thing
                allEqual = false;
            }
        }
        if( allEqual )
        { // Offer the second best bid when we're offering the same every time... does this work, and if so does it need expansion?
            this.secondBestCounter++;
            if( list.size() > 1 )
            {
                opponentBestBid = list.get( 1 );
            }
        }

        this.lastTen.addLast( opponentBestBid.getMyUndiscountedUtil() );
        if( this.lastTen.size() > 10 )
        {
            this.lastTen.removeFirst();
        }

        return opponentBestBid;
    }

    public int getSecondBestCount()
    {
        return this.secondBestCounter;
    }

    @Override
    public boolean canUpdateOM()
    {
        return true; // other variants mess up the whole code apparantley?!
    }

    private final static class Comparing implements Comparator<BidDetails>
    { // Sort according to what we think are the best bids for the opponent (best at the head of the list)
        private final OpponentModel model;

        protected Comparing( OpponentModel model )
        {
            this.model = model;
        }

        @Override
        public int compare( final BidDetails a, BidDetails b )
        {
            double evalA = this.model.getBidEvaluation( a.getBid() );
            double evalB = this.model.getBidEvaluation( b.getBid() );
            if( evalA < evalB )
            {
                return 1;
            }
            else if( evalA > evalB )
            {
                return -1;
            }
            else
            {
                return 0;
            }
        }
    }
}
