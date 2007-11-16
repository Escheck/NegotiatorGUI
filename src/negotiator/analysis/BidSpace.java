package negotiator.analysis;


import negotiator.Bid;
import negotiator.BidIterator;
import negotiator.utility.UtilitySpace;
import negotiator.Domain;

import java.util.ArrayList;
import java.util.Date;

/**
 * 
 * 
 * @author W.Pasman 15nov07
 * BidSpace is a class that can store and do analysis of a space of bids.
 * There seems lot of overlap with the Analysis class.
 * But to be safe I did not try to modify the Analysis class and introduced a new class.
 */
public class BidSpace {
	UtilitySpace utilspaceA;
	UtilitySpace utilspaceB;
	Domain domain; // equals to utilspaceA.domain = utilspaceB.domain
	ArrayList<BidPoint> bidpoints;
	ArrayList<BidPoint> paretoFrontier=null; // not yet set.
	
	public BidSpace(UtilitySpace spaceA, UtilitySpace spaceB) throws Exception
	{
		utilspaceA=spaceA;
		utilspaceB=spaceB;
		if (utilspaceA==null || utilspaceB==null)
			throw new NullPointerException("util space is null");
		domain=utilspaceA.getDomain();
		utilspaceA.checkReadyForNegotiation("spaceA", domain);
		utilspaceB.checkReadyForNegotiation("spaceA", domain);
		BuildSpace();
	}
	
	
	/**
	 * special version that does NOT check the *second* utility space for
	 * compatibility with the domain. Use on your own risk.
	 * The first space must contain the domain.
	 * @param spaceA
	 * @param spaceB
	 * @param anything. If you use this three-para initializer the check will not
	 * be done on 2nd domain. The boolean has no function at all except
	 * being a third parameter that makes a differnet function call.
	 * @throws Exception
	 */
	public BidSpace(UtilitySpace spaceA, UtilitySpace spaceB,boolean anything) throws Exception
	{
		utilspaceA=spaceA;
		utilspaceB=spaceB;
		if (utilspaceA==null || utilspaceB==null)
			throw new NullPointerException("util space is null");
		domain=utilspaceA.getDomain();
		utilspaceA.checkReadyForNegotiation("spaceA", domain);
		BuildSpace();
	}
	
	/*
	 * Create the space with all bid points from the two util spaces.
	 * @throws exception if utility can not be computed for some poitn.
	 * This should not happen as it seems we checked beforehand that all is set OK.
	 */
	void BuildSpace() throws Exception
	{
		bidpoints=new ArrayList<BidPoint>();
		BidIterator lBidIter = new BidIterator(domain);
		while(lBidIter.hasNext()) {
			Bid bid = lBidIter.next();
			bidpoints.add(new BidPoint(bid,utilspaceA.getUtility(bid),utilspaceB.getUtility(bid)));
		}
	}
	
	public ArrayList<BidPoint> getParetoFrontier() throws Exception
	{
		if (paretoFrontier==null)
		{
	        System.out.println("ParetoFrontier start computation:"+(new Date()));
	        paretoFrontier=computeParetoFrontier(bidpoints);
	        System.out.println("ParetoFrontier end computation:"+(new Date()));
		}
		return paretoFrontier;
	}
	
	/** private because it should be called only
	 * with the bids as built by BuildSpace.
	 * @param points the ArrayList<BidPoint> as computed by BuildSpace and stored in bidpoints.
	 * @throws Exception if problem occurs
	 * @return the pareto frontier of the bidpoints.
	 */
	ArrayList<BidPoint> computeParetoFrontier(ArrayList<BidPoint> points) throws Exception
	{
		int n=points.size();
		if (n<=1) return points; // end recursion
		
		// split list in two halves. Unfortunately ArrayList does not have support for this...
		// make new lists that can be modified by us.
		ArrayList<BidPoint> points1=new ArrayList<BidPoint>();
		ArrayList<BidPoint> points2=new ArrayList<BidPoint>();
		for (int i=0; i<n/2; i++) points1.add(points.get(i));
		for (int i=n/2; i<n; i++) points2.add(points.get(i));
		
		ArrayList<BidPoint> pareto1=computeParetoFrontier(points1);
		ArrayList<BidPoint> pareto2=computeParetoFrontier(points2);
		return mergeParetoFrontiers(pareto1,pareto2);
	}
	
	/**
	 * @author W.Pasman
	 * @param pareto1 the first pareto frontier: list of bidpoints with increasing utility for A, decreasing for B
	 * @param pareto2 the second pareto frontier:...
	 * @return new pareto frontier being the merged frontier of the two.
	 */
	public ArrayList<BidPoint> mergeParetoFrontiers(ArrayList<BidPoint> pareto1,ArrayList<BidPoint> pareto2)
	{
		if (pareto1.size()==0) return pareto2;
		if (pareto2.size()==0) return pareto1;

		 // clone because we will remove elements from the list but we want to keep the orig lists.
		 // This looks bit ugly....
		ArrayList<BidPoint> list1=(ArrayList<BidPoint>)(pareto1.clone()); 
		ArrayList<BidPoint> list2=(ArrayList<BidPoint>)(pareto2.clone());
		 // make sure that the first pareto list has the left most point.
		if (list1.get(0).utilityA>list2.get(0).utilityA) 
		{
			ArrayList<BidPoint> list3;
			list3=list1; list1=list2; list2=list3; // swap list1,list2......
		}
		
		// sort the rest
		BidPoint firstpoint=list1.remove(0);
		ArrayList<BidPoint> newpareto=mergeParetoFrontiers(list1,list2);
		 // determine if the first point of list1 can be kept.
		 // the only criterium is the first point of list 2, 
		 // it must be OK with list 1 because that is already a pareto frontier.
		if (firstpoint.utilityB>list2.get(0).utilityB) { 
				 // left point must be higher than next
				newpareto.add(0,firstpoint);
		}
		
		return newpareto;
	}
	
	public ArrayList<Bid> getParetoFrontierBids() throws Exception
	{
		ArrayList<Bid> bids=new ArrayList<Bid> ();
		ArrayList<BidPoint> points=getParetoFrontier();
		for (BidPoint p:points) bids.add(p.bid);
		return bids;
	}
}
