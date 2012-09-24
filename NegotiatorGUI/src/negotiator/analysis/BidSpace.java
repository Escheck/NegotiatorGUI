package negotiator.analysis;

import java.util.ArrayList;
import java.util.List;
import negotiator.Bid;
import negotiator.BidIterator;
import negotiator.Domain;
import negotiator.Global;
import negotiator.utility.UtilitySpace;

/**
 * BidSpace is a class that can store and do analysis of a space of bids.
 * @author Dmytro Tykhonov, Tim Baarslag, Wouter Pasman
 */
public class BidSpace {
	private UtilitySpace [] utilspaces;
	/** equal for all utility spaces */
	private Domain domain;
	public ArrayList<BidPoint> bidPoints; // size = size(domain)
	
	/** All cache */
	List<BidPoint> paretoFrontier=null; // null if not yet computed
	BidPoint kalaiSmorodinsky=null; // null if not yet computed
	BidPoint nash=null; // null if not yet computed
	
	public BidSpace(UtilitySpace[] spaces) throws Exception {
		initializeUtilitySpaces(spaces);
		if (Global.LOW_MEMORY_MODE) {
			buildSpace(true);
		} else {
			buildSpace(false);
		}
	}
	
	private void initializeUtilitySpaces(UtilitySpace[] spaces) throws Exception {
		utilspaces = spaces.clone();
		for (UtilitySpace utilitySpace : spaces) 
		{
			if (utilitySpace==null)
				throw new NullPointerException("util space is null: " + spaces);
		}
		domain = utilspaces[0].getDomain();
		for (UtilitySpace space : spaces) {
			space.checkReadyForNegotiation(domain);
		}
	}

	public BidSpace(UtilitySpace spaceA, UtilitySpace spaceB) throws Exception {
		this(new UtilitySpace [] {spaceA, spaceB});
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
		UtilitySpace[] spaces = { spaceA, spaceB };
		initializeUtilitySpaces(spaces);
		buildSpace(excludeBids);
	}
	
	/**
	 * Create the space with all bid points from all the {@link UtilitySpace}s.
	 * @throws exception if utility can not be computed for some point.
	 * This should not happen as it seems we checked beforehand that all is set OK.
	 */
	private void buildSpace(boolean excludeBids) throws Exception
	{
		bidPoints=new ArrayList<BidPoint>();
		BidIterator lBidIter = new BidIterator(domain);
		
		// if low memory mode, do not store the actual. At the time of writing this
		// has no side-effects
		while(lBidIter.hasNext()) {
			Bid bid = lBidIter.next();
			Double[] utils = new Double[utilspaces.length];
			for(int i =0; i<utilspaces.length;i++) {
				utils[i] = utilspaces[i].getUtility(bid);
			}
			if (excludeBids) {
				bidPoints.add(new BidPoint(null, utils));
			} else {
				bidPoints.add(new BidPoint(bid, utils));
			}
		}
	}
	
	/**
	 * @return The Pareto frontier. The order is  ascending utilityA.
	 * @throws Exception
	 */
	public List<BidPoint> getParetoFrontier() throws Exception
	{
		boolean isBidSpaceAvailable = !bidPoints.isEmpty();
		if (paretoFrontier==null)
		{		
			long t = System.nanoTime();
			if (isBidSpaceAvailable)
			{
				paretoFrontier = computeParetoFrontier(bidPoints).getFrontier();
				return paretoFrontier;
			}

			ArrayList<BidPoint> subPareto = new ArrayList<BidPoint >();
			BidIterator lBidIter = new BidIterator(domain);
			ArrayList<BidPoint> tmpBidPoints = new ArrayList<BidPoint>();
			boolean isSplitted = false;
			int count=0;
			while(lBidIter.hasNext()) {
				Bid bid = lBidIter.next();
				Double[] utils = new Double[utilspaces.length];
				for(int i=0; i<utilspaces.length;i++)
					utils[i] = utilspaces[i].getUtility(bid);
				tmpBidPoints.add(new BidPoint(bid,utils));
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
		return frontier;
	}
	
	public List<Bid> getParetoFrontierBids() throws Exception
	{
		ArrayList<Bid> bids=new ArrayList<Bid> ();
		List<BidPoint> points = getParetoFrontier();
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
		double asymmetry=2; // every point in space will have lower asymmetry than this.
		for (BidPoint p:paretoFrontier)
		{
			double asymofp = 0;
			for(int i=0;i<utilspaces.length;i++) {
				for(int j=i+1;j<utilspaces.length;j++) {
					asymofp += Math.abs(p.getUtility(i)-p.getUtility(j));
				}
			}
			
			if (asymofp<asymmetry) { kalaiSmorodinsky=p; asymmetry=asymofp; }
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
		if (nash != null)
			return nash;
		if (getParetoFrontier().size() < 1)
			throw new AnalysisException(
					"Nash product: Pareto frontier is unavailable.");
		double maxp = -1;
		double[] agentResValue = new double[utilspaces.length];
		for (int i = 0; i < utilspaces.length; i++)
			if (utilspaces[i].getReservationValue() != null)
				agentResValue[i] = utilspaces[i].getReservationValue();
			else
				agentResValue[i] = .0;
		for (BidPoint p : paretoFrontier) {
			double utilofp = 1;
			for (int i = 0; i < utilspaces.length; i++)
				utilofp = utilofp * (p.getUtility(i) - agentResValue[i]);

			if (utilofp > maxp) {
				nash = p;
				maxp = utilofp;
			}
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
	public double ourUtilityOnPareto(double opponentUtility) throws Exception
	{
		if (opponentUtility<0. || opponentUtility>1.)
			throw new Exception("opponentUtil "+opponentUtility+" is out of [0,1].");
		List<BidPoint> pareto=getParetoFrontier();
		// our utility is along A axis, opp util along B axis.

		//add endpoints to pareto curve such that utilB spans [0,1] entirely
		if (pareto.get(0).getUtility(1)<1) pareto.add(0,new BidPoint(null,new Double[] {0.,1.}));
		if (pareto.get(pareto.size()-1).getUtility(1)>0) pareto.add(new BidPoint(null,new Double[] {1.,0.}));
		if (pareto.size()<2) throw new Exception("Pareto has only 1 point?!"+pareto);
		// pareto is monotonically descending in utilB direction.
		int i=0;
		while (! (pareto.get(i).getUtility(1)>=opponentUtility && opponentUtility>pareto.get(i+1).getUtility(1))) 
			i++;

		double oppUtil1=pareto.get(i).getUtility(1); // this is the high value
		double oppUtil2=pareto.get(i+1).getUtility(1); // the low value
		double f=(opponentUtility-oppUtil1)/(oppUtil2-oppUtil1); // f in [0,1] is relative distance from point i.
		// close to point i means f~0. close to i+1 means f~1
		double lininterpol=(1-f)*pareto.get(i).getUtility(0)+f*pareto.get(i+1).getUtility(0);
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
	public BidPoint getNearestBidPoint(double utilA,double utilB,double weightA,double weightB,
			ArrayList<Bid> excludeList)
	{
		System.out.println("determining nearest bid to "+utilA+","+utilB);
		System.out.println("excludes="+excludeList);
		double mindist=9.; // paretospace distances are always smaller than 2
		BidPoint bestPoint=null;
		double r;
		for (BidPoint p:bidPoints)
		{
			boolean contains=false;
			for (Bid b:excludeList) { if (b.equals(p.bid)) { contains=true; break; } }
			if (contains) continue;
			r=weightA*sq(p.getUtility(0)-utilA)+weightB*sq(p.getUtility(1)-utilB);
			if (r<mindist) { mindist=r; bestPoint=p; }
		}
		System.out.println("point found="+bestPoint.bid);
		if (excludeList.size()>1) System.out.println("bid equals exclude(1):"+bestPoint.bid.equals(excludeList.get(1)));
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
			double paretoDistance = bid.getDistance(paretoBid);
			if (paretoDistance < distance) {
				distance = paretoDistance;
			}
		}
		return distance;
	}
	
	public double sq(double x) { return x*x; }
}
