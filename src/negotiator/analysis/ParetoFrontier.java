package negotiator.analysis;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


public class ParetoFrontier 
{
	private ArrayList<BidPoint> frontier;
	
	public ParetoFrontier() 
	{
		frontier = new ArrayList<BidPoint>();
	}
	
	/**
	 * @param bidpoint bid to be added to the Pareto frontier.
	 */
	public void mergeIntoFrontier(BidPoint bidpoint)
	{
		for (BidPoint f : frontier)
		{
			if (bidpoint.isStrictlyDominatedBy(f))		// we will not add this point
				return;
			if (f.isStrictlyDominatedBy(bidpoint))		// we will merge this point into the frontier
			{
				merge(bidpoint);
				return;
			}
		}
		
		// b is not above or below the frontier, so it must be part of the frontier
		frontier.add(bidpoint);
	}
	
	private void merge(BidPoint b)
	{
		ArrayList<BidPoint> toBeRemoved = new ArrayList<BidPoint>();
		for (BidPoint f : frontier)
		{
			if (f.isStrictlyDominatedBy(b))		// delete dominated frontier points
				toBeRemoved.add(f);
		}
		frontier.removeAll(toBeRemoved);
		frontier.add(b);
	}
	
	/**
	 * @author TB
	 * @param points
	 * @param frontier
	 * @return All points lying strictly above the frontier
	 */
	public List<BidPoint> filterPointsAboveFrontier(List<BidPoint> points)
	{
		List<BidPoint> filtered = new ArrayList<BidPoint>();
		for (BidPoint b : points)
			if (this.isBelowFrontier(b))
				continue;
			else
				filtered.add(b);
		return filtered;
	}

	public boolean isBelowFrontier(BidPoint b)
	{
		for (BidPoint f : this.getFrontier())
			if (b.isStrictlyDominatedBy(f))
				return true;
		return false;
	}
	
	
	/**
	 * The order is ascending utilityA.
	 * @author TB
	 *  
	 */
	public void sort()
	{
		Collections.sort(frontier, new Comparator<BidPoint>() {
			
			public int compare(BidPoint x, BidPoint y) {
				if (x.getUtilityA() < y.getUtilityA())
					return -1;
				else if (x.getUtilityA() > y.getUtilityA())
					return 1;
				else
					return 0;
			}
		});
	}

	public int size() 
	{
		return frontier.size();
	}
	
	public ArrayList<BidPoint> getFrontier() 
	{
		return frontier;
	}
}
