/** 
 * 	OptimalBidder: using the optimal stopping rule (cutoffs) for bidding
 *  @author rafik		
 **/


package agents;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeMap;

import negotiator.Agent;
import negotiator.Bid;
import negotiator.DiscreteTimeline;
import negotiator.actions.Action;
import negotiator.actions.Offer;
import negotiator.issue.Issue;
import negotiator.issue.Value;

public abstract class OptimalBidder extends Agent 
{	
	private static double rv = -1.0; 
	protected static int partitions;
	private static int ownTotalRounds;
	protected static HashMap<Integer, Value> values;
	private static ArrayList<Double> bids;
	protected static Issue pie;
	private Action actionOfPartner = null;

	/**
	 * computation of the bid for round j
	 * @param round j
	 * @return bid value
	 **/
	public abstract double bid(int j); 

	/**
	 * depending on the agent's utility space, the reservation 
	 * value will have to be acquired in a specific manner 
	 * @param double arg
	 * @return double rv
	 * @throws Exception 
	 **/
	public abstract double getReservationValue(double arg) throws Exception;

	/**
	 * Init is called when a next session starts with the same opponent.
	 **/
	public void init()
	{
		try 
		{
			ownTotalRounds = (getTotalRounds() - 1)/2;
			pie  =  utilitySpace.getDomain().getIssues().get(0);	 // unique issue

			print("=====================================================================");
			print("   ownTotalRounds = " + ownTotalRounds);
			print("   issue name       = " + pie);
			print("   issue type       = " + pie.getType());

			rv = getReservationValue(rv); // sets rvB

			bids = new ArrayList<Double>(ownTotalRounds);

			for (int i = 0 ; i < ownTotalRounds ; i++ )
				bids.add(bid(i+1));

			print("   Bids : ");

			for (int i = 0 ; i < ownTotalRounds ; i++ )
				print( "\tB[" + i + "] = " + bids.get(i));

			print("\n=====================================================================");
		}
		catch (Exception e) 
		{   
			e.printStackTrace(); 
		}	
	}

	public static String getVersion() 
	{
		return "2.0 (Genius 4.2)"; 
	}

	@Override
	public String getName()
	{
		return "OptimalBidder";
	}

	public void ReceiveMessage(Action opponentAction) 
	{
		actionOfPartner = opponentAction;
	}


	public Action chooseAction()
	{
		Action action = null;
		try 
		{ 
			if ( actionOfPartner == null )
			{
				action = chooseOptimalBidAction();
			}
			if ( actionOfPartner instanceof Offer )
			{
				action = chooseOptimalBidAction();
			}
		} 
		catch (Exception e) 
		{ 
			print("Exception in ChooseAction:"+e.getMessage());
		}
		return action;
	}

	/**
	 * Wrapper for getOptimalBid, for convenience
	 * @return new Bid()
	 **/
	private Action chooseOptimalBidAction() 
	{
		Bid nextBid = null ;
		try 
		{
			nextBid = getOptimalBid(); 
		}
		catch (Exception e) 
		{ 
			print("Problem with received bid: <" + e.getMessage() + ">. Cancelling bidding");
			
			System.out.println("\t\t\t\tErrrrr!   => " + nextBid);			
			System.exit(0);
			
		}

		System.out.println("\t\t\t\t>>>>>>>>>>>>>>>>>>> ");
		System.out.println("\t\t\t\t> " + nextBid);
		System.out.println("\t\t\t\t>>>>>>>>>>>>>>>>>>> \n");
		
		return (new Offer(getAgentID(), nextBid));
	}

	// discrete rounds' methods
	public int getRound()          {     return ((DiscreteTimeline) timeline).getRound();           }
	public int getRoundsLeft()     {     return ((DiscreteTimeline) timeline).getRoundsLeft();      }
	public int getOwnRoundsLeft()  {     return ((DiscreteTimeline) timeline).getOwnRoundsLeft();   }
	public int getTotalRounds()    {     return ((DiscreteTimeline) timeline).getTotalRounds();     }
	public double getTotalTime()   {   	 return ((DiscreteTimeline) timeline).getTotalTime();       }
	// trace 
	void print(String s) {System.out.println("############  " + s);}

	/**
	 * 
	 *
	 */
	private Bid getOptimalBid() throws Exception
	{
		print("############   B's  ####################################");
		print(" Round         = " + getRound() );
		print(" RoundsLeft    = " + getRoundsLeft() );
		print(" OwnRoundsLeft = " + getOwnRoundsLeft() );
		print(" TotalRounds   = " + getTotalRounds() );
		print(" TotalTime     = " + getTotalTime() );

		double min = 1.0;
		int roundsleft = 0;
		Value optValue = null;

		print(" bids.size = " + bids.size() );
		print(" getOwnRoundsLeft = " + getOwnRoundsLeft() );

		for (Integer key : new TreeMap<Integer, Value>(values).keySet())
		{ 
			roundsleft = getOwnRoundsLeft();
			
			Double targetBid = bids.get(roundsleft);
			double piePartition = (double) key/partitions;

			if ( Math.abs( targetBid - piePartition ) < min )
			{
				min = Math.abs( targetBid - piePartition );
				optValue = values.get(key);
			}
		}
		
		HashMap<Integer, Value> optVals = new HashMap<Integer, Value>();
		optVals.put(pie.getNumber(), optValue);
		Bid ToBid = new Bid(utilitySpace.getDomain(), optVals);

		print(" ToBid = " + ToBid );
		

		return ToBid; // optimal bid

	}
}

// End 