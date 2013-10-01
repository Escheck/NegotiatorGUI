/** 
 * 	OptimalBidder: using the optimal stopping rule (cutoffs) for bidding. 
 *  Estimates rounds based on own/opponent actions.	
 *  
 *  TODO define as an optimal bidding agent template
 * 
 * @author rafik		
 ************************************************************************************************************************************/

// no need for the bids array? just compute it dynamically based on the left rounds... since we know the total number of rounds

package agents;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeMap;
import negotiator.Agent;
import negotiator.Bid;
import negotiator.actions.Accept;
import negotiator.actions.Action;
import negotiator.actions.Offer;
import negotiator.issue.Issue;
import negotiator.issue.IssueDiscrete;
import negotiator.issue.Value;
import negotiator.issue.ValueDiscrete;
import negotiator.utility.Evaluator;
import negotiator.utility.EvaluatorDiscrete;

public class OptimalBidder extends Agent 
{	
		private static double rvB; 
		private static int n, totalSessions;
		private static HashMap<Integer, Value> values;
		private static Issue pie;
		private Action actionOfPartner = null;
		
/**************************************************************************************************************
  TODO Define as public abstract double bid(); 
 */
 public double bid(int j)
 {
		return 0.5 * (   (j==1) ? rvB : sq(bid(j-1))   ) + 0.5;  
 }
		 
/********************************************************************************************************************************
 * init is called when a next session starts with the same opponent.
 */
 public void init()
{

			try 
			{
				totalSessions =  getSessionsTotal();
				print_("=====================================================================");
			
			// {{
			
				boolean flag = true;
		        int i, nvalues = -1;
		        
		        rvB  =  0.0;
		        n    =  1000;
				pie  =  utilitySpace.getDomain().getIssues().get(0);	// pie is the issue
				
				print_("   issue name = " + pie);
				print_("   issue type = " + pie.getType());
				
				switch ( pie.getType() ) //  get/set rvB...
				{
					case DISCRETE:
					{
						IssueDiscrete discrete_pie = (IssueDiscrete) pie;
						nvalues = discrete_pie.getNumberOfValues();
						print_("   nvalues = " + nvalues);	
						
						values = new HashMap<Integer, Value>(nvalues);

						for ( i = 0 ; i < nvalues ; i++ )
						{
							ValueDiscrete value = discrete_pie.getValue(i);
							// evaluation 
						    Evaluator eval = utilitySpace.getEvaluator(pie.getNumber());
				            Integer evaluation = ((EvaluatorDiscrete)eval).getValue((ValueDiscrete)value);

							values.put(i, value);
				            
				            if ( evaluation != 0 && flag ) // reaching rvB
							{
								rvB =  (double) i/n; // rvB normalized
								utilitySpace.setReservationValue(rvB);
								flag = false;
							}
				        }
						break;			
					}//case
				default: throw new Exception("Type " + pie.getType() + " not supported by TAgent.");
				} //switch

				print_("   rvB = " + rvB );
				
				ArrayList<Double> Bids = new ArrayList<Double>(totalSessions);

				for ( i = 1 ; i <= totalSessions ; i++ )
					Bids.add(bid(i));
				
				print__("############     bids : \n");
				for ( i = 0 ; i < totalSessions ; i++ )
					print_( " \t B[" + i + "] = " + Bids.get(i));
					
				print__("\n");

			// }}
			}
			catch (Exception e) 
			{   
				e.printStackTrace(); 
			}	
		}
/********************************************************************************************************************************/
		public static String getVersion() 
		{
			return "2.0 (Genius 4.2)"; 
		}
		
		@Override
		public String getName()
		{
			return "OptimalBidder";
		}
/********************************************************************************************************************************/
		public void ReceiveMessage(Action opponentAction) 
		{
			actionOfPartner = opponentAction;
		}
		
/*********************************************************************************************************************************/
		public Action chooseAction()
		{
			Action action = null;
			try 
			{ 
				print_(" Session : " + getSessionNumber() + " / " + getSessionsTotal());
				
				if ( actionOfPartner == null )
				{
					action = chooseOptimalBidAction();
				}
				if ( actionOfPartner instanceof Offer )
				{
					System.out.println(" Him");

					action = chooseOptimalBidAction();
				}
			} 
			catch (Exception e) 
			{ 
				print_("Exception in ChooseAction:"+e.getMessage());
			}
			return action;
		}

/******************************************************************************************************************
 * Wrapper for getOptimalBid, for convenience.
 * @return new Bid()
 */
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
			if (nextBid == null) 
				return (new Accept(getAgentID()));          
			
			return (new Offer(getAgentID(), nextBid));
		}

/*******************************************************************************************************************/
		private Bid getOptimalBid() throws Exception
		{

			print_(" Session : " + getSessionNumber() + " / " + getSessionsTotal() );
	        
// {{
			TreeMap<Integer, Value> entries = new TreeMap<Integer, Value>(values);
		    HashMap<Integer, Value> vals = null;

		    int rleft =  getSessionsTotal() - getSessionNumber(); 
			print_("  rleft = " + rleft );

		    
		    double min = 1.0;
			Value v_opt = null;
			for (Integer key : entries.keySet())
			{ 
			   Value v = values.get(key);
	            
	           vals = new HashMap<Integer, Value>();
			   vals.put(pie.getNumber(), v);

			   	if (false)
		        print_("key = " + key + 
		        		"   " + bid(rleft) +
		        		"   " + ((double) key/n) + 
		        		"   " +  getSessionNumber() + "/" + getSessionsTotal() + 
		        		"\t utility( Value = " + v + " ) = " + getUtility(new Bid(utilitySpace.getDomain(), vals)) );
			
		        if ( Math.abs( bid(rleft) - (double) key/n ) < min )
		        {
		        		min = Math.abs( bid(rleft) - (double) key/n );
		        		v_opt = values.get(key);
		        }
			}
			
		    HashMap<Integer, Value> opt_vals = new  HashMap<Integer, Value>();

		    opt_vals.put(pie.getNumber(), v_opt);
			
		    Bid opt_bid = new Bid(utilitySpace.getDomain(), opt_vals);
		    
	        print_(" Bidding opt_bid = " + opt_bid); 

			return opt_bid;

		}

		double sq(double x) { return x*x; }
		void print_(String s) {System.out.println("############  " + s);}
		void print__(String s) {System.out.print(s);}
		void exit_() {System.out.println("\nexit.\n"); System.exit(0);}
		
	}

// End 