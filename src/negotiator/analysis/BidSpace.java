package negotiator.analysis;


import java.util.ArrayList;
import java.util.List;

import negotiator.Bid;
import negotiator.BidIterator;
import negotiator.Domain;
import negotiator.Global;
import negotiator.utility.UtilitySpace;

/**
 * @author W.Pasman 15nov07
 * BidSpace is a class that can store and do analysis of a space of bids.
 * There seems lot of overlap with the Analysis class.
 * But to be safe I did not try to modify the Analysis class and introduced a new class.
 */
public class BidSpace {
	UtilitySpace utilspaceA;
	UtilitySpace utilspaceB;
	/** equal to utilspaceA.domain = utilspaceB.domain */
	Domain domain;
	public ArrayList<BidPoint> bidPoints;
	
	/** All cache */
	ArrayList<BidPoint> paretoFrontier=null; // not yet set.
	BidPoint kalaiSmorodinsky=null; // null if not set.
	BidPoint nash=null; // null if not set.
	
	public BidSpace(UtilitySpace spaceA, UtilitySpace spaceB) throws Exception
	{
		utilspaceA=spaceA;
		utilspaceB=spaceB;
		if (utilspaceA==null || utilspaceB==null)
			throw new NullPointerException("util space is null");
		domain=utilspaceA.getDomain();
		utilspaceA.checkReadyForNegotiation("spaceA", domain);
		utilspaceB.checkReadyForNegotiation("spaceA", domain);
		if (Global.LOW_MEMORY_MODE) {
			BuildSpace(true);
		} else {
			BuildSpace(false);
		}
	}
	
	
	/**
	 * special version that does NOT check the *second* utility space for
	 * compatibility with the domain. Use on your own risk.
	 * The first space must contain the domain.
	 * @param spaceA
	 * @param spaceB
	 * @param only store the bid points and not the bids to save memory
	 * @throws Exception
	 */
	public BidSpace(UtilitySpace spaceA, UtilitySpace spaceB, boolean excludeBids) throws Exception
	{
		utilspaceA=spaceA;
		utilspaceB=spaceB;
		if (utilspaceA==null || utilspaceB==null)
			throw new NullPointerException("util space is null");
		domain=utilspaceA.getDomain();
		utilspaceA.checkReadyForNegotiation("spaceA", domain);
		BuildSpace(excludeBids);
	}
	/*
	 * Create the space with all bid points from the two util spaces.
	 * @throws exception if utility can not be computed for some poitn.
	 * This should not happen as it seems we checked beforehand that all is set OK.
	 */
	void BuildSpace(boolean excludeBids) throws Exception
	{
		// initially allocating the right size improves computational efficiency and memory cost
		bidPoints=new ArrayList<BidPoint>((int)domain.getNumberOfPossibleBids());
		BidIterator lBidIter = new BidIterator(domain);
		
		// if low memory mode, do not store the actual. At the time of writing this
		// has no side-effects
		if (excludeBids) {
			while(lBidIter.hasNext()) {
				Bid bid = lBidIter.next();
				bidPoints.add(new BidPoint(null, utilspaceA.getUtility(bid), utilspaceB.getUtility(bid)));
			}
		} else {
			while(lBidIter.hasNext()) {
				Bid bid = lBidIter.next();
				bidPoints.add(new BidPoint(bid, utilspaceA.getUtility(bid), utilspaceB.getUtility(bid)));
			}
		}
	}
	
	/**
	 * @return The Pareto frontier. The order is  ascending utilityA.
	 * @throws Exception
	 */
	public ArrayList<BidPoint> getParetoFrontier() throws Exception
	{
		boolean isBidSpaceAvailable = !bidPoints.isEmpty();
		if (paretoFrontier==null)
		{		
			long t = System.nanoTime();
			if (isBidSpaceAvailable)
			{
//				System.out.println("ParetoFrontier start computation for known bid space of size " + bidPoints.size());
				paretoFrontier = computeParetoFrontier(bidPoints).getFrontier();
//				System.out.println("ParetoFrontier end computation:"+ (System.nanoTime() - t) / 1000000 + "ms.");;
				return paretoFrontier;
			}

//			System.out.println("ParetoFrontier start computation for very large bid space of size "  + domain.getNumberOfPossibleBids());
			ArrayList<BidPoint> subPareto = new ArrayList<BidPoint >();
			BidIterator lBidIter = new BidIterator(domain);
			ArrayList<BidPoint> tmpBidPoints = new ArrayList<BidPoint>();
			boolean isSplitted = false;
			int count=0;
			while(lBidIter.hasNext()) 
			{
				Bid bid = lBidIter.next();
				tmpBidPoints.add(new BidPoint(bid, utilspaceA.getUtility(bid), utilspaceB.getUtility(bid)));
				count++;
				if(count>500000) 
				{
					subPareto.addAll(computeParetoFrontier(tmpBidPoints).getFrontier());
					tmpBidPoints = new ArrayList<BidPoint >();
					count = 0;
					isSplitted = true;
				}
			}
			// Add the remainder to the sub-Pareto frontier
			if(tmpBidPoints.size()>0)subPareto.addAll(computeParetoFrontier(tmpBidPoints).getFrontier());

			if (isSplitted)
				paretoFrontier = computeParetoFrontier(subPareto).getFrontier();		// merge sub-pareto's
			else
				paretoFrontier = subPareto;
			System.out.println("Multi-ParetoFrontier end computation:"+ (System.nanoTime() - t) / 1000000 + "ms.");
		}
		return paretoFrontier;
	}
	
	/**
	 * Private because it should be called only with the bids as built by BuildSpace.
	 * @return the sorted pareto frontier of the bidpoints.
	 * @author Tim Baarslag
	 * @param points the ArrayList<BidPoint> as computed by BuildSpace and stored in bidpoints.
	 * @throws Exception if problem occurs
	 */
	private ParetoFrontier computeParetoFrontier(List<BidPoint> points) throws Exception
	{
		ParetoFrontier frontier = new ParetoFrontier();
		for (BidPoint p: points)
			frontier.mergeIntoFrontier(p);

		frontier.sort();
//		System.out.println("Frontier = " + frontier.size() + ": " + frontier);
		return frontier;
	}
	
	public ArrayList<Bid> getParetoFrontierBids() throws Exception
	{
		ArrayList<Bid> bids=new ArrayList<Bid> ();
		ArrayList<BidPoint> points=getParetoFrontier();
		for (BidPoint p:points) bids.add(p.bid);
		return bids;
	}
	
	
	/**
	 * Calculates Kalai-Smorodinsky optimal outcome. Assumes that Pareto frontier is already built.
	 * Kalai-Smorodinsky is the point on paretofrontier 
	 * that has least difference in utilities for A and B
	 * @author Dmytro Tykhonov, cleanup by W.Pasman
	 * @returns the kalaiSmorodinsky BidPoint.
	 * @throws AnalysisException
	 */
	public BidPoint getKalaiSmorodinsky() throws Exception 
	{	
		if (kalaiSmorodinsky!=null) return kalaiSmorodinsky;
		if(getParetoFrontier().size()<1) 
			throw new AnalysisException("kalaiSmorodinsky product: Pareto frontier is unavailable.");
		double minassymetry=2; // every point in space will have lower assymetry than this.
		for (BidPoint p:paretoFrontier)
		{
			double asymofp = Math.abs(p.utilityA-p.utilityB);
			if (asymofp<minassymetry) { kalaiSmorodinsky=p; minassymetry=asymofp; }
		}
		return kalaiSmorodinsky;
	}
	
	
	
	/**
	 * Calculates the undiscounted Nash optimal outcome. Assumes that Pareto frontier is already built.
	 * Nash is the point on paretofrontier that has max product of utilities for A and B
	 * @author Dmytro Tykhonov, cleanup by W.Pasman
	 * @returns the Nash BidPoint.
	 * @throws AnalysisException
	 */
	public BidPoint getNash() throws Exception 
	{
		if (nash!=null) return nash;
		if(getParetoFrontier().size()<1) 
			throw new AnalysisException("Nash product: Pareto frontier is unavailable.");
		double maxp = -1;
		double agentAresValue=0, agentBresValue=0;
		if(utilspaceA.getReservationValue()!=null) agentAresValue = utilspaceA.getReservationValue();
		if(utilspaceB.getReservationValue()!=null) agentBresValue = utilspaceB.getReservationValue();
		for (BidPoint p:paretoFrontier)
		{
			double utilofp = (p.utilityA -agentAresValue)*(p.utilityB-agentBresValue);
			if (utilofp>maxp) { nash=p; maxp=utilofp; }
		}
		return nash;
	}
	
	/**
	 * Calculate own coordinate 
	 * @param opponentUtility
	 * @return the utility of us on the pareto curve
	 * @throws exception if getPareto fails or other cases, e.g. paretoFrontier contains utilityB=NAN.
	 * Still unclear why utilB evaluates to NAN though...
	 */
	public double OurUtilityOnPareto(double opponentUtility) throws Exception
	{
		if (opponentUtility<0. || opponentUtility>1.)
			throw new Exception("opponentUtil "+opponentUtility+" is out of [0,1].");
		ArrayList<BidPoint> pareto=getParetoFrontier();
		// our utility is along A axis, opp util along B axis.

		//add endpoints to pareto curve such that utilB spans [0,1] entirely
		if (pareto.get(0).utilityB<1) pareto.add(0,new BidPoint(null,0.,1.));
		if (pareto.get(pareto.size()-1).utilityB>0) pareto.add(new BidPoint(null,1.,0.));
		if (pareto.size()<2) throw new Exception("Pareto has only 1 point?!"+pareto);
		// pareto is monotonically descending in utilB direction.
		int i=0;
//		System.out.println("Searching for opponentUtility = " + opponentUtility);
		while (! (pareto.get(i).utilityB>=opponentUtility && opponentUtility>=pareto.get(i+1).utilityB)) 
		{
//			System.out.println(i + ". Trying [" + pareto.get(i).utilityB +  ", " + pareto.get(i+1).utilityB + "]");
			i++;
		}
		
		double oppUtil1=pareto.get(i).utilityB; // this is the high value
		double oppUtil2=pareto.get(i+1).utilityB; // the low value
		double f=(opponentUtility-oppUtil1)/(oppUtil2-oppUtil1); // f in [0,1] is relative distance from point i.
		// close to point i means f~0. close to i+1 means f~1
		double lininterpol=(1-f)*pareto.get(i).utilityA+f*pareto.get(i+1).utilityA;
		return lininterpol;
	}
	
	public String toString()
	{
		return bidPoints.toString();
	}
		
	/**
	 * find the bid with the minimal distance weightA*DeltaUtilA^2+weightB*DeltaUtilB^2
	 * where DeltaUtilA is the difference between given utilA and the actual util of bid
	 * @author W.Pasman
	 * @param utilA the agent-A utility of the point to be found
	 * @param utilB the agent-B utility of the point to be found
	 * @param weightA weight in A direction
	 * @param weightB weight in B direction
	 * @param excludeList Bids to be excluded from the search.
	 * @return best point, or null if none remaining.
	 */
	public BidPoint NearestBidPoint(double utilA,double utilB,double weightA,double weightB,
			ArrayList<Bid> excludeList)
	{
		System.out.println("determining nearest bid to "+utilA+","+utilB);
//		System.out.println("excludes="+excludeList);
		double mindist=9.; // paretospace distances are always smaller than 2
		BidPoint bestPoint=null;
		double r;
		for (BidPoint p:bidPoints)
		{
			boolean contains=false;
			//disabled excluding 16-11-2010
			//for (Bid b:excludeList) { if (b.equals(p.bid)) { contains=true; break; } }
			// WERKT NIET????if (excludeList.indexOf(p.bid)!=-1) continue; 
			//neither ArrayList.contains nor ArrayList.indexOf seem to use .equals
			// although manual claims that indexOf is using equals???
			if (contains) continue;
			r=weightA*sq(p.utilityA-utilA)+weightB*sq(p.utilityB-utilB);
			if (r<mindist) { mindist=r; bestPoint=p; }
		}
		System.out.println("point found: (" + bestPoint.utilityA + ", " + bestPoint.utilityB + ") ="+bestPoint.bid);
		//System.out.println("p.bid is in excludelist:"+excludeList.indexOf(bestPoint.bid));
//		if (excludeList.size()>1) System.out.println("bid equals exclude(1):"+bestPoint.bid.equals(excludeList.get(1)));
		//System.out.println();
		return bestPoint;
	}
	
	public double distanceToNearestParetoBid(BidPoint bid) {
		if (paretoFrontier == null) {
			try {
				paretoFrontier = getParetoFrontier();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		double distance = Double.POSITIVE_INFINITY;
		for (BidPoint paretoBid : paretoFrontier) {
			double paretoDistance = bid.distanceTo(paretoBid);
			if (paretoDistance < distance) {
				distance = paretoDistance;
			}
		}
		return distance;
	}
	
	public double sq(double x) { return x*x; }
}
