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
		private static int OwntotalSessions;
		protected static HashMap<Integer, Value> values;
		private static ArrayList<Double> Bids;
		protected static Issue pie;
		private Action actionOfPartner = null;

/**
 * computation of the bib for round j
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
public abstract double GetReservationValue(double arg) throws Exception;

/**
 * Init is called when a next session starts with the same opponent.
 **/
public void init()
{
			try 
			{
				OwntotalSessions = (GetTotalRounds() - 1)/2;
		        pie  =  utilitySpace.getDomain().getIssues().get(0);	 // unique issue
				
				print_("=====================================================================");
				print_("   OwntotalSessions = " + OwntotalSessions);
				print_("   issue name = " + pie);
				print_("   issue type = " + pie.getType());
				
				rv = GetReservationValue(rv); // sets rvB
				
				Bids = new ArrayList<Double>(OwntotalSessions);

				for (int i = 0 ; i < OwntotalSessions ; i++ )
					Bids.add(bid(i+1));

				print_(" OwntotalSessions = " + OwntotalSessions);

				for (int i = 0 ; i < OwntotalSessions ; i++ )
					print_( " \t B[" + i + "] = " + Bids.get(i));
					
				print_("\n=====================================================================");
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
				print_("Exception in ChooseAction:"+e.getMessage());
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
				print_("Problem with received bid:"+e.getMessage()+". cancelling bidding");
			}
			return (new Offer(getAgentID(), nextBid));
		}
		
// discrete rounds' methods
public int GetRound()          {     return ((DiscreteTimeline) timeline).getRound();           }
public int GetRoundsLeft()     {     return ((DiscreteTimeline) timeline).getRoundsLeft();      }
public int GetOwnRoundsLeft()  {     return ((DiscreteTimeline) timeline).getOwnRoundsLeft();   }
public int GetTotalRounds()    {     return ((DiscreteTimeline) timeline).getTotalRounds();     }
public double GetTotalTime()   {   	 return ((DiscreteTimeline) timeline).getTotalTime();       }
// trace 
void print_(String s) {System.out.println("############  " + s);}

/**
 * 
 **/
	private Bid getOptimalBid() throws Exception
	{
		print_("############   B's  ####################################");
		print_(" Round         = " + GetRound() );
		print_(" RoundsLeft    = " + GetRoundsLeft() );
		print_(" OwnRoundsLeft = " + GetOwnRoundsLeft() );
		print_(" TotalRounds   = " + GetTotalRounds() );
		print_(" TotalTime     = " + GetTotalTime() );

	    double min = 1.0;
		Value OptValue = null;
		for (Integer key : new TreeMap<Integer, Value>(values).keySet())
		{ 
		    if ( Math.abs( Bids.get(GetOwnRoundsLeft()) - (double) key/partitions ) < min )
	        {
	        		min = Math.abs( Bids.get(GetOwnRoundsLeft()) - (double) key/partitions );
	        		OptValue = values.get(key);
	        }
		}
		
	    HashMap<Integer, Value> OptVals = new HashMap<Integer, Value>();
	    OptVals.put(pie.getNumber(), OptValue);
	    return new Bid(utilitySpace.getDomain(), OptVals); // optimal bid

	}

		
}

// End 