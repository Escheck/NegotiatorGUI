/** 
 * 	OptimalBidder: using the optimal stopping rule (cutoffs) for bidding. 
 * 	B_{j+1} = 1/2 + 1/2 B_j^2
 * 
 * @author rafik		
 ************************************************************************************************************************************/

package agents;

import java.util.HashMap;

import negotiator.issue.ISSUETYPE;
import negotiator.issue.IssueDiscrete;
import negotiator.issue.Value;
import negotiator.issue.ValueDiscrete;
import negotiator.utility.Evaluator;
import negotiator.utility.EvaluatorDiscrete;

public class OptimalBidderSimple extends OptimalBidder 
{	
		private static double rvB; 

 public void init()
 {
	super.init();
	partitions = 1000;
 }
		
/**
 *  computation of the bid for round j as in prop 4.3
 *  @param round j
 *  @return bid value
 **/
 @Override
 public double bid(int j)
 {
	 if ( j == 1 )
		 return 0.5 + 0.5 * rvB ;
	 else
		 return 0.5 + 0.5 *  Math.pow(bid(j-1), 2) ;
 } 
 
 /**
  *  reservation value: if the reservation value is already set (<> -1.0) simply return it, 
  *  otherwise get it from the utility space 
  *  @param double
  *  @return double
  *  @throws Exception 
  **/
 @Override
public double GetReservationValue(double arg) throws Exception
	{
		boolean flag = true;
		double rv = 0.0;
		
		if (arg == -1.0 ) // first time
		{
			if ( pie.getType().equals( ISSUETYPE.DISCRETE) ) //  get/set rvB...
			{
					IssueDiscrete discrete_pie = (IssueDiscrete) pie;
					int nvalues = discrete_pie.getNumberOfValues();
					print_("   nvalues = " + nvalues);	
					values = new HashMap<Integer, Value>(nvalues);

					for (int i = 0 ; i < nvalues ; i++ )
					{
						ValueDiscrete value = discrete_pie.getValue(i);
					    Evaluator evaluator = utilitySpace.getEvaluator(pie.getNumber());
			            Integer evaluation  = ((EvaluatorDiscrete) evaluator).getValue((ValueDiscrete)value);
			            values.put(i, value);
			            
			            if ( evaluation != 0 && flag ) // reaching rvB
						{
							rv = (double) i/partitions; // rvB normalized
							utilitySpace.setReservationValue(rv);
							flag = false;
							print_("   rvB = " + rv );
						}
					}
					return rv;
			}
			else 
			{
				throw new Exception("Type " + pie.getType() + " not supported by " + getName() );
			}
		}
		else
		{
			return arg;
		}	
	}
 
 
} // end 