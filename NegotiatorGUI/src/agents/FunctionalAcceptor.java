package agents;

import java.util.ArrayList;

import negotiator.Bid;
import negotiator.DiscreteTimeline;

/**
 * This agent does not concede, but will accept anything equal to or above the reservation value.
 * For undiscounted domain only.
 */
public class FunctionalAcceptor extends TimeDependentAgent
{
	double rv  = -0.1;
	int ownTotalRounds = 0;
	private static ArrayList<Double> cutoffs;

	public void init()
	{
		rv = utilitySpace.getReservationValue();
		ownTotalRounds = (getTotalRounds() - 1)/2;

		cutoffs = new ArrayList<Double>(ownTotalRounds);
		for (int i = 0 ; i < ownTotalRounds ; i++ )
			cutoffs.add(bid(i+1));

		super.init();
	}

	
	@Override
	public double getE()
	{
		return 0;
	}
	
	@Override
	public String getName()
	{
		return "FunctionalAcceptor";
	}
	
	public double bid(int j)
	{
		if ( j == 1 )
			return 0.5 + 0.5 * rv;
		else
			return 0.5 + 0.5 *  Math.pow(bid(j-1), 2);
	} 
	
	public double functionalReservationValue()
	{
		//int ownTotalRounds = (getTotalRounds() - 1)/2;
		//System.out.println(" rv   = " + rv );
		//System.out.println(" ownTotalRounds   = " + ownTotalRounds );
		//System.out.println(" b("+ getOwnRoundsLeft() +") = " + cutoffs.get(getOwnRoundsLeft()) );

		boolean immediate = false;
		
		if (!immediate)
		{
			return cutoffs.get(getOwnRoundsLeft());
		}
		else
		{
			// immediate acceptance case
			return utilitySpace.getReservationValue();
		}
	}
	
	@Override
	public boolean isAcceptable(Bid plannedBid)
	{
		Bid opponentLastBid = getOpponentLastBid();
		if(getUtility(opponentLastBid) >= functionalReservationValue() )
			return true;
		return false;
	}
	
	
	// discrete rounds' methods
	public int getRound()          {     return ((DiscreteTimeline) timeline).getRound();           }
	public int getRoundsLeft()     {     return ((DiscreteTimeline) timeline).getRoundsLeft();      }
	public int getOwnRoundsLeft()  {     return ((DiscreteTimeline) timeline).getOwnRoundsLeft();   }
	public int getTotalRounds()    {     return ((DiscreteTimeline) timeline).getTotalRounds();     }
	public double getTotalTime()   {   	 return ((DiscreteTimeline) timeline).getTotalTime();       }

}
